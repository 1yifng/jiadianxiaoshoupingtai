
package com.controller;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import com.utils.*;
import com.service.DictionaryService;
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

/**
 * 商品收藏
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/jiadianCollection")
public class JiadianCollectionController {
    @Autowired
    private JiadianCollectionService jiadianCollectionService;
    @Autowired
    private DictionaryService dictionaryService;//字典
    @Autowired
    private JiadianService jiadianService;//商品
    @Autowired
    private YonghuService yonghuService;//用户


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        CommonUtil.checkMap(params);
        PageUtils page = jiadianCollectionService.queryPage(params);

        //字典表数据转换
        List<JiadianCollectionView> list =(List<JiadianCollectionView>)page.getList();
        for(JiadianCollectionView c:list){
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
        JiadianCollectionEntity jiadianCollection = jiadianCollectionService.selectById(id);
        if(jiadianCollection !=null){
            //entity转view
            JiadianCollectionView view = new JiadianCollectionView();
            BeanUtils.copyProperties( jiadianCollection , view );//把实体数据重构到view中
            //级联表 商品
            //级联表
            JiadianEntity jiadian = jiadianService.selectById(jiadianCollection.getJiadianId());
            if(jiadian != null){
            BeanUtils.copyProperties( jiadian , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setJiadianId(jiadian.getId());
            }
            //级联表 用户
            //级联表
            YonghuEntity yonghu = yonghuService.selectById(jiadianCollection.getYonghuId());
            if(yonghu != null){
            BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setYonghuId(yonghu.getId());
            }
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
    public R save(@RequestBody JiadianCollectionEntity jiadianCollection, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");
        else if("用户".equals(role))
            jiadianCollection.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        Wrapper<JiadianCollectionEntity> queryWrapper = new EntityWrapper<JiadianCollectionEntity>()
            .eq("jiadian_id", jiadianCollection.getJiadianId())
            .eq("yonghu_id", jiadianCollection.getYonghuId())
            .eq("jiadian_collection_types", jiadianCollection.getJiadianCollectionTypes())
            ;

        JiadianCollectionEntity jiadianCollectionEntity = jiadianCollectionService.selectOne(queryWrapper);
        if(jiadianCollectionEntity==null){
            jiadianCollection.setInsertTime(new Date());
            jiadianCollection.setCreateTime(new Date());
            jiadianCollectionService.insert(jiadianCollection);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianCollectionEntity jiadianCollection) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
            jiadianCollectionService.updateById(jiadianCollection);//根据id更新
            return R.ok();
    }



    /**
    * 管理员删除商品收藏
    */
    @RequestMapping("/delete1")
    public R delete(@RequestBody Integer[] ids){
        jiadianCollectionService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 用户取消收藏
     * 使用 GET 请求，通过查询参数接收 jiadianId 和 yonghuId
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public R delete(@RequestParam("jiadianId") Integer jiadianId, @RequestParam("yonghuId") Integer yonghuId) {
        // 使用 MyBatis-Plus 的 EntityWrapper 构建删除条件
        EntityWrapper<JiadianCollectionEntity> queryWrapper = new EntityWrapper<>();
        queryWrapper.eq("jiadian_id", jiadianId).eq("yonghu_id", yonghuId);

        // 调用 MyBatis-Plus 的删除方法
        boolean result = jiadianCollectionService.delete(queryWrapper);

        if (result ) {
            return R.ok().put("message", "删除成功");
        } else {
            return R.error(511, "删除失败，未找到相关记录");
        }
    }



    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){

        CommonUtil.checkMap(params);
        PageUtils page = jiadianCollectionService.queryPage(params);

        //字典表数据转换
        List<JiadianCollectionView> list =(List<JiadianCollectionView>)page.getList();
        for(JiadianCollectionView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        JiadianCollectionEntity jiadianCollection = jiadianCollectionService.selectById(id);
            if(jiadianCollection !=null){
                //entity转view
                JiadianCollectionView view = new JiadianCollectionView();
                BeanUtils.copyProperties( jiadianCollection , view );//把实体数据重构到view中

                //级联表
                    JiadianEntity jiadian = jiadianService.selectById(jiadianCollection.getJiadianId());
                if(jiadian != null){
                    BeanUtils.copyProperties( jiadian , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setJiadianId(jiadian.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(jiadianCollection.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 添加收藏
    */
    @RequestMapping("/add")
    public R add(@RequestBody JiadianCollectionEntity jiadianCollection){
        Wrapper<JiadianCollectionEntity> queryWrapper = new EntityWrapper<JiadianCollectionEntity>()
            .eq("jiadian_id", jiadianCollection.getJiadianId())
            .eq("yonghu_id", jiadianCollection.getYonghuId())
            .eq("jiadian_collection_types", jiadianCollection.getJiadianCollectionTypes())
            ;
        JiadianCollectionEntity jiadianCollectionEntity = jiadianCollectionService.selectOne(queryWrapper);
        if(jiadianCollectionEntity==null){
            jiadianCollection.setInsertTime(new Date());
            jiadianCollection.setCreateTime(new Date());
        jiadianCollectionService.insert(jiadianCollection);

            return R.ok();
        }else {
            return R.error(511,"您已经收藏过了");
        }
    }



}

