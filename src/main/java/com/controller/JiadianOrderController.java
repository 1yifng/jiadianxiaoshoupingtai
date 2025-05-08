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
 * 商品订单
 * 后端接口
 * @author
 * @email
 */
@RestController
@Controller
@RequestMapping("/jiadianOrder")
public class JiadianOrderController {

    @Autowired
    private JiadianOrderService jiadianOrderService;

    /**
     * 管理员查订单列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        return jiadianOrderService.adminQueryOrderList(params, request);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        return jiadianOrderService.backendOrderDetail(id, request);
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody JiadianOrderEntity jiadianOrder, HttpServletRequest request) {
        return jiadianOrderService.backendSaveOrder(jiadianOrder, request);
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianOrderEntity jiadianOrder) {
        return jiadianOrderService.backendUpdateOrder(jiadianOrder);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        return jiadianOrderService.deleteOrders(ids);
    }

    /**
     * 订单数据列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        return jiadianOrderService.orderList(params, request);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        return jiadianOrderService.frontendOrderDetail(id, request);
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody JiadianOrderEntity jiadianOrder, HttpServletRequest request) {
        return jiadianOrderService.frontendSaveOrder(jiadianOrder, request);
    }

    /**
     * 添加订单
     */
    @RequestMapping("/order")
    public R add(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        return jiadianOrderService.addOrder(params, request);
    }

    /**
     * 用户退款
     */
    @RequestMapping("/refund")
    public R refund(Integer id, HttpServletRequest request) {
        return jiadianOrderService.userRefund(id, request);
    }

    /**
     * 用户评价
     */
    @RequestMapping("/commentback")
    public R commentback(Integer id, String commentbackText, HttpServletRequest request) {
        return jiadianOrderService.userCommentback(id, commentbackText, request);
    }

    /**
     * 管理员发货
     */
    @RequestMapping("/deliver")
    public R deliver(Integer id, String jiadianOrderCourierNumber, String jiadianOrderCourierName) {
        return jiadianOrderService.adminDeliver(id, jiadianOrderCourierNumber, jiadianOrderCourierName);
    }

    /**
     * 用户收货
     */
    @RequestMapping("/receiving")
    public R receiving(Integer id) {
        return jiadianOrderService.userReceiving(id);
    }
}