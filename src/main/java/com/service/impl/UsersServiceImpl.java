package com.service.impl;


import com.service.UsersService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.UsersDao;
import com.entity.UsersEntity;
import com.utils.R;

// 引入 TokenService 相关
import com.service.TokenService;
import javax.servlet.http.HttpServletRequest;

@Service("userService")
public class UsersServiceImpl extends ServiceImpl<UsersDao, UsersEntity> implements UsersService {

	@Override
	public R login(String username, String password, TokenService tokenService) {
		// 根据用户名查询用户
		UsersEntity user = this.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
		// 用户不存在或密码不匹配
		if (user == null || !user.getPassword().equals(password)) {
			// 返回错误信息，避免信息泄露统一提示
			return R.error("账号或密码不正确");
		}
		// 生成 JWT 令牌（包含用户 ID、用户名、角色类型）
		String token = tokenService.generateToken(user.getId(), username, "users", user.getRole());
		// 构建返回数据
		R r = R.ok();
		// 放入身份令牌
		r.put("token", token);
		// 放入用户角色
		r.put("role", user.getRole());
		// 放入用户 ID
		r.put("userId", user.getId());
		return r;
	}

	@Override
	public R logout(HttpServletRequest request) {
		// 使当前会话失效
		request.getSession().invalidate();
		// 返回退出成功信息
		return R.ok("退出成功");
	}

	@Override
	public R getCurrUser(HttpServletRequest request) {
		// 从会话中获取用户 ID
		Integer id = (Integer) request.getSession().getAttribute("userId");
		// 根据用户 ID 查询用户信息
		UsersEntity user = this.selectById(id);
		// 返回包含用户信息的响应实体
		return R.ok().put("data", user);
	}
}