
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 用户
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/yonghu")
public class YonghuController {
    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;//字典



    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = yonghuService.queryPage(params);

        //字典表数据转换
        List<YonghuView> list =(List<YonghuView>)page.getList();
        for(YonghuView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        YonghuEntity yonghu = yonghuService.selectById(id);
        if(yonghu !=null){
            //entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties( yonghu , view );//把实体数据重构到view中
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
            .eq("username", yonghu.getUsername())
            .or()
            .eq("yonghu_phone", yonghu.getYonghuPhone())
//            .or()
//            .eq("yonghu_id_number", yonghu.getYonghuIdNumber())
            ;

        YonghuEntity yonghuEntity = yonghuService.selectOne(queryWrapper);
        if(yonghuEntity==null){
            yonghu.setCreateTime(new Date());
            yonghu.setPassword("123456");
            yonghuService.insert(yonghu);
            return R.ok();
        }else {
            return R.error(511,"账户或者联系方式已经被使用");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody YonghuEntity yonghu) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        if("".equals(yonghu.getYonghuPhoto()) || "null".equals(yonghu.getYonghuPhoto())){
                yonghu.setYonghuPhoto(null);
        }

            yonghuService.updateById(yonghu);//根据id更新
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        yonghuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 用户登录接口
     * @param username 用户名
     * @param password 密码（需前端加密后传输）
     * @return 包含Token和用户信息的响应对象
     */
    @IgnoreAuth // 允许未登录访问
    @RequestMapping(value = "/login")
    public R login(String username, String password) {
        // 根据用户名查询用户实体
        YonghuEntity yonghu = yonghuService.selectOne(
                new EntityWrapper<YonghuEntity>().eq("username", username)
        );

        // 验证用户是否存在且密码匹配
        if(yonghu==null || !yonghu.getPassword().equals(password))
            return R.error("账号或密码不正确");

        // 生成JWT令牌（包含用户ID、用户名、角色类型）
        String token = tokenService.generateToken(yonghu.getId(), username, "yonghu", "用户");

        // 构建返回数据
        R r = R.ok();
        r.put("token", token);       // 身份令牌
        r.put("role","用户");        // 用户角色
        r.put("username",yonghu.getYonghuName()); // 用户昵称
        r.put("tableName","yonghu"); // 对应数据库表名
        r.put("userId",yonghu.getId()); // 用户ID
        return r;
    }

    /**
     * 用户注册接口
     * @param yonghu 用户实体（包含用户名、手机号、身份证号等字段）
     * @return 注册结果响应
     */
    @IgnoreAuth // 允许未登录访问
    @PostMapping(value = "/register")
    public R register(@RequestBody YonghuEntity yonghu) {
        // 构建唯一性校验条件（用户名/手机号任一重复则注册失败）
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
                .eq("username", yonghu.getUsername())    // 用户名唯一
                .or()
                .eq("yonghu_phone", yonghu.getYonghuPhone()) ;   // 手机号唯一
//                .or()
//                .eq("yonghu_id_number", yonghu.getYonghuIdNumber()) // 身份证唯一

        // 执行唯一性检查
        YonghuEntity existUser = yonghuService.selectOne(queryWrapper);
        if(existUser != null)
            return R.error("账户/联系方式已被使用");

        // 初始化新用户数据
        yonghu.setNewMoney(0.0);      // 设置初始账户余额
        yonghu.setCreateTime(new Date()); // 记录注册时间

        // 插入数据库
        yonghuService.insert(yonghu);

        return R.ok();
    }

    /**
     * 重置密码
     */
    @GetMapping(value = "/resetPassword")
    public R resetPassword(Integer  id) {
        YonghuEntity yonghu = yonghuService.selectById(id);
        yonghu.setPassword("123456");
        yonghuService.updateById(yonghu);
        return R.ok();
    }

	/**
	 * 修改密码
	 */
	@GetMapping(value = "/updatePassword")
	public R updatePassword(String  oldPassword, String  newPassword, HttpServletRequest request) {
        YonghuEntity yonghu = yonghuService.selectById((Integer)request.getSession().getAttribute("userId"));
		if(newPassword == null){
			return R.error("新密码不能为空") ;
		}
		if(!oldPassword.equals(yonghu.getPassword())){
			return R.error("原密码输入错误");
		}
		if(newPassword.equals(yonghu.getPassword())){
			return R.error("新密码不能和原密码一致") ;
		}
        yonghu.setPassword(newPassword);
		yonghuService.updateById(yonghu);
		return R.ok();
	}



    /**
     * 忘记密码
     */
    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username) {
        YonghuEntity yonghu = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("username", username));
        if(yonghu!=null){
            yonghu.setPassword("123456");
            yonghuService.updateById(yonghu);
            return R.ok();
        }else{
           return R.error("账号不存在");
        }
    }


    /**
    * 获取用户的session用户信息
    */
    @RequestMapping("/session")
    public R getCurrYonghu(HttpServletRequest request){
        Integer id = (Integer)request.getSession().getAttribute("userId");
        YonghuEntity yonghu = yonghuService.selectById(id);
        if(yonghu !=null){
            //entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties( yonghu , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }
    }


    /**
    * 退出
    */
    @GetMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }



    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){

        CommonUtil.checkMap(params);
        PageUtils page = yonghuService.queryPage(params);

        //字典表数据转换
        List<YonghuView> list =(List<YonghuView>)page.getList();
        for(YonghuView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        YonghuEntity yonghu = yonghuService.selectById(id);
            if(yonghu !=null){


                //entity转view
                YonghuView view = new YonghuView();
                BeanUtils.copyProperties( yonghu , view );//把实体数据重构到view中

                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody YonghuEntity yonghu){
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
            .eq("username", yonghu.getUsername())
            .or()
            .eq("yonghu_phone", yonghu.getYonghuPhone())
//            .or()
//            .eq("yonghu_id_number", yonghu.getYonghuIdNumber())
            ;
        YonghuEntity yonghuEntity = yonghuService.selectOne(queryWrapper);
        if(yonghuEntity==null){
            yonghu.setCreateTime(new Date());
            yonghu.setPassword("123456");
        yonghuService.insert(yonghu);

            return R.ok();
        }else {
            return R.error(511,"账户或者联系方式已经被使用");
        }
    }

}

