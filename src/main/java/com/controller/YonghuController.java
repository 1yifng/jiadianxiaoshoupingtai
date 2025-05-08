package com.controller;

import com.annotation.IgnoreAuth;
import com.entity.YonghuEntity;
import com.service.YonghuService;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        return yonghuService.page(params, request);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        return yonghuService.info(id, request);
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuEntity yonghu, HttpServletRequest request) {
        return yonghuService.save(yonghu, request);
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody YonghuEntity yonghu) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        return yonghuService.update(yonghu);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        return yonghuService.delete(ids);
    }

    /**
     * 用户登录接口
     * @param username 用户名
     * @param password 密码（需前端加密后传输）
     * @return 包含Token和用户信息的响应对象
     */
    @RequestMapping(value = "/login")
    public R login(String username, String password) {
        return yonghuService.login(username, password);
    }

    /**
     * 用户注册接口
     * @param yonghu 用户实体（包含用户名、手机号、身份证号等字段）
     * @return 注册结果响应
     */
    @PostMapping(value = "/register")
    public R register(@RequestBody YonghuEntity yonghu) {
        return yonghuService.register(yonghu);
    }

    /**
     * 重置密码
     */
    @GetMapping(value = "/resetPassword")
    public R resetPassword(Integer id) {
        return yonghuService.resetPassword(id);
    }

    /**
     * 修改密码
     */
    @GetMapping(value = "/updatePassword")
    public R updatePassword(String oldPassword, String newPassword, HttpServletRequest request) {
        return yonghuService.updatePassword(oldPassword, newPassword, request);
    }

    /**
     * 忘记密码
     */
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username) {
        return yonghuService.resetPass(username);
    }

    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrYonghu(HttpServletRequest request) {
        return yonghuService.getCurrYonghu(request);
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
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        return yonghuService.list(params, request);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        return yonghuService.detail(id, request);
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody YonghuEntity yonghu) {
        return yonghuService.add(yonghu);
    }
}