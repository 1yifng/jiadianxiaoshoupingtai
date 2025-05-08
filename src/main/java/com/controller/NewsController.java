package com.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import com.utils.*;
import com.annotation.IgnoreAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 公告信息
 * 后端接口
 * @author
 * @email
 */
@RestController
@Controller
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role)) {
            params.put("yonghuId", request.getSession().getAttribute("userId"));
        }
        CommonUtil.checkMap(params);
        // 调用服务层的分页查询方法
        PageUtils page = newsService.queryPage(params, request);
        return R.ok().put("data", page);
    }

    /**
     * 后端详情 - 根据ID获取新闻详情信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        // 调用服务层的获取新闻详情方法
        NewsView view = newsService.getNewsInfo(id, request);
        if (view != null) {
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody NewsEntity news) {
        // 调用服务层的保存新闻方法
        boolean result = newsService.saveNews(news);
        if (result) {
            return R.ok();
        } else {
            return R.error(511, "表中有相同数据");
        }
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody NewsEntity news) {
        // 调用服务层的修改新闻方法
        boolean result = newsService.updateNews(news);
        if (result) {
            return R.ok();
        } else {
            return R.error(511, "修改失败");
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        // 调用服务层的删除新闻方法
        boolean result = newsService.deleteNews(ids);
        if (result) {
            return R.ok();
        } else {
            return R.error(511, "删除失败");
        }
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        CommonUtil.checkMap(params);
        // 调用服务层的前端列表查询方法
        PageUtils page = newsService.getFrontendList(params, request);
        return R.ok().put("data", page);
    }

    /**
     * 前端公告详情查询（无需登录）
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        // 调用服务层的前端公告详情查询方法
        NewsView view = newsService.getFrontendDetail(id, request);
        if (view != null) {
            return R.ok().put("data", view);
        } else {
            return R.error(511, "未查询到ID为[" + id + "]的公告信息");
        }
    }
}