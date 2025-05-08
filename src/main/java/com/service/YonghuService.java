package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.YonghuEntity;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import java.util.List;

/**
 * 用户 服务类
 */
public interface YonghuService extends IService<YonghuEntity> {

    /**
     * @param params 查询参数
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 后端列表查询
     * @param params 查询参数
     * @param request HttpServletRequest对象
     * @return 包含分页数据的R对象
     */
    com.utils.R page(Map<String, Object> params, HttpServletRequest request);

    /**
     * 后端详情查询
     * @param id 用户ID
     * @param request HttpServletRequest对象
     * @return 包含用户详情的R对象
     */
    com.utils.R info(Long id, HttpServletRequest request);

    /**
     * 后端保存用户信息
     * @param yonghu 用户实体
     * @param request HttpServletRequest对象
     * @return 保存结果的R对象
     */
    com.utils.R save(YonghuEntity yonghu, HttpServletRequest request);

    /**
     * 后端修改用户信息
     * @param yonghu 用户实体
     * @return 修改结果的R对象
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    com.utils.R update(YonghuEntity yonghu) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException;

    /**
     * 删除用户信息
     * @param ids 用户ID数组
     * @return 删除结果的R对象
     */
    com.utils.R delete(Integer[] ids);

    /**
     * 用户登录接口
     * @param username 用户名
     * @param password 密码（需前端加密后传输）
     * @return 包含Token和用户信息的响应对象
     */
    com.utils.R login(String username, String password);

    /**
     * 用户注册接口
     * @param yonghu 用户实体（包含用户名、手机号、身份证号等字段）
     * @return 注册结果响应
     */
    com.utils.R register(YonghuEntity yonghu);

    /**
     * 重置密码
     * @param id 用户ID
     * @return 重置结果的R对象
     */
    com.utils.R resetPassword(Integer id);

    /**
     * 修改密码
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @param request HttpServletRequest对象
     * @return 修改结果的R对象
     */
    com.utils.R updatePassword(String oldPassword, String newPassword, HttpServletRequest request);

    /**
     * 忘记密码
     * @param username 用户名
     * @return 重置结果的R对象
     */
    com.utils.R resetPass(String username);

    /**
     * 获取用户的session用户信息
     * @param request HttpServletRequest对象
     * @return 包含用户信息的R对象
     */
    com.utils.R getCurrYonghu(HttpServletRequest request);

    /**
     * 前端列表查询
     * @param params 查询参数
     * @param request HttpServletRequest对象
     * @return 包含分页数据的R对象
     */
    com.utils.R list(Map<String, Object> params, HttpServletRequest request);

    /**
     * 前端详情查询
     * @param id 用户ID
     * @param request HttpServletRequest对象
     * @return 包含用户详情的R对象
     */
    com.utils.R detail(Long id, HttpServletRequest request);

    /**
     * 前端保存用户信息
     * @param yonghu 用户实体
     * @return 保存结果的R对象
     */
    com.utils.R add(YonghuEntity yonghu);
}