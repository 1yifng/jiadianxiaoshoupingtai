package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.YonghuDao;
import com.entity.YonghuEntity;
import com.entity.view.YonghuView;
import com.service.DictionaryService;
import com.service.TokenService;
import com.service.YonghuService;
import com.utils.CommonUtil;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service("yonghuService")
@Transactional
public class YonghuServiceImpl extends ServiceImpl<YonghuDao, YonghuEntity> implements YonghuService {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService; // 字典

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<YonghuView> page = new Query<YonghuView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }

    @Override
    public com.utils.R page(Map<String, Object> params, HttpServletRequest request) {
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role))
            params.put("yonghuId", request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = queryPage(params);

        // 字典表数据转换
        List<YonghuView> list = (List<YonghuView>) page.getList();
        for (YonghuView c : list) {
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    @Override
    public com.utils.R info(Long id, HttpServletRequest request) {
        YonghuEntity yonghu = selectById(id);
        if (yonghu != null) {
            // entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties(yonghu, view); // 把实体数据重构到view中
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    @Override
    public com.utils.R save(YonghuEntity yonghu, HttpServletRequest request) {
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
                .eq("username", yonghu.getUsername())
                .or()
                .eq("yonghu_phone", yonghu.getYonghuPhone());

        YonghuEntity yonghuEntity = selectOne(queryWrapper);
        if (yonghuEntity == null) {
            yonghu.setCreateTime(new Date());
            yonghu.setPassword("123456");
            insert(yonghu);
            return R.ok();
        } else {
            return R.error(511, "账户或者联系方式已经被使用");
        }
    }

    @Override
    public com.utils.R update(YonghuEntity yonghu) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        if ("".equals(yonghu.getYonghuPhoto()) || "null".equals(yonghu.getYonghuPhoto())) {
            yonghu.setYonghuPhoto(null);
        }
        updateById(yonghu); // 根据id更新
        return R.ok();
    }

    @Override
    public com.utils.R delete(Integer[] ids) {
        deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    @Override
    public com.utils.R login(String username, String password) {

// 1. 先检查用户是否存在
        YonghuEntity yonghu = selectOne(new EntityWrapper<YonghuEntity>().eq("username", username));
        if (yonghu == null) {
            return R.error("账号不存在");
        }

// 2. 再验证密码（注意密码是否加密）
        if (!yonghu.getPassword().equals(password)) { // 确保前端传输的密码与数据库存储格式一致
            return R.error("密码错误");
        }

// 3. 最后生成Token
        String token = tokenService.generateToken(yonghu.getId(), username, "yonghu", "用户");


        // 构建返回数据
        R r = R.ok();
        r.put("token", token);       // 身份令牌
        r.put("role", "用户");        // 用户角色
        r.put("username", yonghu.getYonghuName()); // 用户昵称
        r.put("tableName", "yonghu"); // 对应数据库表名
        r.put("userId", yonghu.getId()); // 用户ID
        return r;
    }

    @Override
    public com.utils.R register(YonghuEntity yonghu) {
        // 构建唯一性校验条件（用户名/手机号任一重复则注册失败）
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
                .eq("username", yonghu.getUsername())    // 用户名唯一
                .or()
                .eq("yonghu_phone", yonghu.getYonghuPhone());   // 手机号唯一

        // 执行唯一性检查
        YonghuEntity existUser = selectOne(queryWrapper);
        if (existUser != null)
            return R.error("账户/联系方式已被使用");

        // 初始化新用户数据
        yonghu.setNewMoney(0.0);      // 设置初始账户余额
        yonghu.setCreateTime(new Date()); // 记录注册时间

        // 插入数据库
        insert(yonghu);

        return R.ok();
    }

    @Override
    public com.utils.R resetPassword(Integer id) {
        YonghuEntity yonghu = selectById(id);
        yonghu.setPassword("123456");
        updateById(yonghu);
        return R.ok();
    }

    @Override
    public com.utils.R updatePassword(String oldPassword, String newPassword, HttpServletRequest request) {
        YonghuEntity yonghu = selectById((Integer) request.getSession().getAttribute("userId"));
        if (newPassword == null) {
            return R.error("新密码不能为空");
        }
        if (!oldPassword.equals(yonghu.getPassword())) {
            return R.error("原密码输入错误");
        }
        if (newPassword.equals(yonghu.getPassword())) {
            return R.error("新密码不能和原密码一致");
        }
        yonghu.setPassword(newPassword);
        updateById(yonghu);
        return R.ok();
    }

    @Override
    public com.utils.R resetPass(String username) {
        YonghuEntity yonghu = selectOne(new EntityWrapper<YonghuEntity>().eq("username", username));
        if (yonghu != null) {
            yonghu.setPassword("123456");
            updateById(yonghu);
            return R.ok();
        } else {
            return R.error("账号不存在");
        }
    }

    @Override
    public com.utils.R getCurrYonghu(HttpServletRequest request) {
        Integer id = (Integer) request.getSession().getAttribute("userId");
        YonghuEntity yonghu = selectById(id);
        if (yonghu != null) {
            // entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties(yonghu, view); // 把实体数据重构到view中

            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    @Override
    public com.utils.R list(Map<String, Object> params, HttpServletRequest request) {
        CommonUtil.checkMap(params);
        PageUtils page = queryPage(params);

        // 字典表数据转换
        List<YonghuView> list = (List<YonghuView>) page.getList();
        for (YonghuView c : list)
            dictionaryService.dictionaryConvert(c, request); // 修改对应字典表字段

        return R.ok().put("data", page);
    }

    @Override
    public com.utils.R detail(Long id, HttpServletRequest request) {
        YonghuEntity yonghu = selectById(id);
        if (yonghu != null) {
            // entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties(yonghu, view); // 把实体数据重构到view中

            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    @Override
    public com.utils.R add(YonghuEntity yonghu) {
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
                .eq("username", yonghu.getUsername())
                .or()
                .eq("yonghu_phone", yonghu.getYonghuPhone());
        YonghuEntity yonghuEntity = selectOne(queryWrapper);
        if (yonghuEntity == null) {
            yonghu.setCreateTime(new Date());
            yonghu.setPassword("123456");
            insert(yonghu);
            return R.ok();
        } else {
            return R.error(511, "账户或者联系方式已经被使用");
        }
    }
}