package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.CartEntity;
import com.entity.view.CartView;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * 购物车 服务类
 */
public interface CartService extends IService<CartEntity> {

    /**
     * @param params 查询参数
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 后端详情
     * @param id 购物车记录ID
     * @param request HttpServletRequest对象
     * @return 包含购物车详情的 CartView 对象
     */
//    CartView getCartInfo(Long id, HttpServletRequest request);

    /**
     * 后端保存
     * @param cart 购物车实体对象
     * @return 保存结果，成功返回 true，失败返回 false
     */
//    boolean saveCart(CartEntity cart);

    /**
     * 后端修改
     * @param cart 购物车实体对象
     * @return 修改结果，成功返回 true，失败返回 false
     */
    boolean updateCart(CartEntity cart);

    /**
     * 删除购物车记录
     * @param ids 要删除的购物车记录ID数组
     * @return 删除结果，成功返回 true，失败返回 false
     */
    boolean deleteCarts(Integer[] ids);

    /**
     * 前端详情
     * @param id 购物车记录ID
     * @param request HttpServletRequest对象
     * @return 包含购物车详情的 CartView 对象
     */
    CartView getCartDetail(Long id, HttpServletRequest request);

    /**
     * 前端保存
     * @param cart 购物车实体对象
     * @return 保存结果，成功返回 true，失败返回 false
     */
    boolean addCart(CartEntity cart);
}