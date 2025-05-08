package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.JiadianOrderEntity;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import java.util.List;

/**
 * 商品订单 服务类
 */
public interface JiadianOrderService extends IService<JiadianOrderEntity> {

    /**
     * @param params 查询参数
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 管理员查订单列表
     * @param params 查询参数
     * @param request HttpServletRequest对象
     * @return R对象，包含查询结果
     */
    com.utils.R adminQueryOrderList(Map<String, Object> params, HttpServletRequest request);

    /**
     * 后端详情
     * @param id 订单ID
     * @param request HttpServletRequest对象
     * @return R对象，包含订单详情
     */
    com.utils.R backendOrderDetail(Long id, HttpServletRequest request);

    /**
     * 后端保存订单
     * @param jiadianOrder 订单实体
     * @param request HttpServletRequest对象
     * @return R对象，保存结果
     */
    com.utils.R backendSaveOrder(JiadianOrderEntity jiadianOrder, HttpServletRequest request);

    /**
     * 后端修改订单
     * @param jiadianOrder 订单实体
     * @return R对象，修改结果
     */
    com.utils.R backendUpdateOrder(JiadianOrderEntity jiadianOrder);

    /**
     * 删除订单
     * @param ids 订单ID数组
     * @return R对象，删除结果
     */
    com.utils.R deleteOrders(Integer[] ids);

    /**
     * 订单数据列表
     * @param params 查询参数
     * @param request HttpServletRequest对象
     * @return R对象，包含订单列表
     */
    com.utils.R orderList(Map<String, Object> params, HttpServletRequest request);

    /**
     * 前端详情
     * @param id 订单ID
     * @param request HttpServletRequest对象
     * @return R对象，包含订单详情
     */
    com.utils.R frontendOrderDetail(Long id, HttpServletRequest request);

    /**
     * 前端保存订单
     * @param jiadianOrder 订单实体
     * @param request HttpServletRequest对象
     * @return R对象，保存结果
     */
    com.utils.R frontendSaveOrder(JiadianOrderEntity jiadianOrder, HttpServletRequest request);

    /**
     * 添加订单
     * @param params 查询参数
     * @param request HttpServletRequest对象
     * @return R对象，添加结果
     */
    com.utils.R addOrder(Map<String, Object> params, HttpServletRequest request);

    /**
     * 用户退款
     * @param id 订单ID
     * @param request HttpServletRequest对象
     * @return R对象，退款结果
     */
    com.utils.R userRefund(Integer id, HttpServletRequest request);

    /**
     * 用户评价
     * @param id 订单ID
     * @param commentbackText 评价内容
     * @param request HttpServletRequest对象
     * @return R对象，评价结果
     */
    com.utils.R userCommentback(Integer id, String commentbackText, HttpServletRequest request);

    /**
     * 管理员发货
     * @param id 订单ID
     * @param jiadianOrderCourierNumber 快递单号
     * @param jiadianOrderCourierName 快递公司名称
     * @return R对象，发货结果
     */
    com.utils.R adminDeliver(Integer id, String jiadianOrderCourierNumber, String jiadianOrderCourierName);

    /**
     * 用户收货
     * @param id 订单ID
     * @return R对象，收货结果
     */
    com.utils.R userReceiving(Integer id);
}