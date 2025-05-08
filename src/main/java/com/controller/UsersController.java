package com.controller;


import javax.servlet.http.HttpServletRequest;

import com.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.service.TokenService;
import com.utils.R;

/**
 * 登录相关
 */
@RequestMapping("users")
@RestController
public class UsersController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private TokenService tokenService;

	/**
	 * 管理员登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 包含认证令牌和用户信息的响应实体
	 */
	@PostMapping(value = "/login")
	public R login(String username, String password) {
		// 调用服务层的登录方法
		return usersService.login(username, password, tokenService);
	}

	/**
	 * 退出
	 */
	@GetMapping(value = "logout")
	public R logout(HttpServletRequest request) {
		// 调用服务层的退出方法
		return usersService.logout(request);
	}

	/**
	 * 获取用户的 session 用户信息
	 */
	@RequestMapping("/session")
	public R getCurrUser(HttpServletRequest request) {
		// 调用服务层的获取当前用户信息方法
		return usersService.getCurrUser(request);
	}
}