package com.controller;

import com.service.JiadianCommentbackService;
import com.utils.PageUtils;
import com.utils.R;
import com.entity.view.JiadianCommentbackView;
import com.entity.JiadianCommentbackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * 商品评价
 * 后端接口
 * @author
 * @email
 */
@RestController
@RequestMapping("/jiadianCommentback")
public class JiadianCommentbackController {

    @Autowired
    private JiadianCommentbackService jiadianCommentbackService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        PageUtils page = jiadianCommentbackService.handleBackendPage(params, request);
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        JiadianCommentbackView view = jiadianCommentbackService.handleBackendInfo(id, request);
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
    public R save(@RequestBody JiadianCommentbackEntity jiadianCommentback, HttpServletRequest request) {
        jiadianCommentbackService.handleBackendSave(jiadianCommentback, request);
        return R.ok();
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianCommentbackEntity jiadianCommentback) {
        jiadianCommentbackService.handleBackendUpdate(jiadianCommentback);
        return R.ok();
    }

    /**
     * 删除 直接删除评价
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Integer[] ids) {
//        jiadianCommentbackService.deleteBatchIds(java.util.Arrays.asList(ids));
//        return R.ok();
//    }

    /**
     * 删除 删除后将订单状态变更成已收货状态 达到可再次评价的效果
     */
    @RequestMapping("/delete")
    public R delete(@RequestParam("commentId") Long commentId, @RequestParam("orderId") Long orderId) {
        jiadianCommentbackService.deleteCommentAndUpdateOrderType(commentId, orderId);
        return R.ok();
    }

    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        PageUtils page = jiadianCommentbackService.handleFrontendList(params, request);
        return R.ok().put("data", page);
    }

    /**
     * 前端详情
     */
//    @RequestMapping("/detail/{id}")
//    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
//        JiadianCommentbackView view = jiadianCommentbackService.handleFrontendDetail(id, request);
//        if (view != null) {
//            return R.ok().put("data", view);
//        } else {
//            return R.error(511, "查不到数据");
//        }
//    }

    /**
     * 前端保存
     */
//    @RequestMapping("/add")
//    public R add(@RequestBody JiadianCommentbackEntity jiadianCommentback) {
//        jiadianCommentbackService.handleFrontendSave(jiadianCommentback);
//        return R.ok();
//    }
}