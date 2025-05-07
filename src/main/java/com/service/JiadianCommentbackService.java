package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.JiadianCommentbackEntity;
import com.entity.view.JiadianCommentbackView;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品评价 服务类
 */
public interface JiadianCommentbackService extends IService<JiadianCommentbackEntity> {

    /**
     * @param params 查询参数
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 处理后端列表业务逻辑
     * @param params 查询参数
     * @param request HttpServletRequest
     * @return 带分页的查询出来的数据
     */
    PageUtils handleBackendPage(Map<String, Object> params, HttpServletRequest request);

    /**
     * 处理后端详情业务逻辑
     * @param id 评价ID
     * @param request HttpServletRequest
     * @return 评价详情
     */
    JiadianCommentbackView handleBackendInfo(Long id, HttpServletRequest request);

    /**
     * 处理后端保存业务逻辑
     * @param jiadianCommentback 商品评价实体
     * @param request HttpServletRequest
     */
    void handleBackendSave(JiadianCommentbackEntity jiadianCommentback, HttpServletRequest request);

    /**
     * 处理后端修改业务逻辑
     * @param jiadianCommentback 商品评价实体
     */
    void handleBackendUpdate(JiadianCommentbackEntity jiadianCommentback);

    /**
     * 删除评价并更新订单类型
     * @param commentId 评价ID
     * @param orderId 订单ID
     */
    void deleteCommentAndUpdateOrderType(Long commentId, Long orderId);

    /**
     * 处理前端列表业务逻辑
     * @param params 查询参数
     * @param request HttpServletRequest
     * @return 带分页的查询出来的数据
     */
    PageUtils handleFrontendList(Map<String, Object> params, HttpServletRequest request);

    /**
     * 处理前端详情业务逻辑
     * @param id 评价ID
     * @param request HttpServletRequest
     * @return 评价详情
     */
//    JiadianCommentbackView handleFrontendDetail(Long id, HttpServletRequest request);

    /**
     * 处理前端保存业务逻辑
     * @param jiadianCommentback 商品评价实体
     */
//    void handleFrontendSave(JiadianCommentbackEntity jiadianCommentback);
}