package com.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.UsersEntity;
import com.utils.PageUtils;

// 引入 TokenService 相关
import com.service.TokenService;
import javax.servlet.http.HttpServletRequest;

public interface UsersService extends IService<UsersEntity> {
//	// 查询分页数据，传入查询参数
//	PageUtils queryPage(Map<String, Object> params);
//	// 根据条件查询用户列表
//	List<UsersEntity> selectListView(Wrapper<UsersEntity> wrapper);
//	// 查询分页数据，传入查询参数和查询条件
//	PageUtils queryPage(Map<String, Object> params, Wrapper<UsersEntity> wrapper);
	// 管理员登录方法，传入用户名和密码
	com.utils.R login(String username, String password, TokenService tokenService);
	// 管理员退出方法，传入 HttpServletRequest 对象
	com.utils.R logout(HttpServletRequest request);
	// 获取当前用户信息，传入 HttpServletRequest 对象
	com.utils.R getCurrUser(HttpServletRequest request);
}