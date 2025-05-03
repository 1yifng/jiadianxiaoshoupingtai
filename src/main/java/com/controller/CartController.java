package com.controller;

import com.entity.CartEntity;
import com.entity.view.CartView;
import com.service.CartService;
import com.service.DictionaryService;
import com.utils.CommonUtil;
import com.utils.PageUtils;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 购物车 后端接口
 * 该控制器类处理与购物车相关的HTTP请求，调用对应的服务方法进行业务处理。
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 点击购物车页面时加载购物车列表
     * @param params 查询参数，包含分页信息和其他查询条件
     * @param request HttpServletRequest对象，可用于获取请求相关信息
     * @return 封装了分页数据的响应对象 R
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 检查查询参数是否合法
        CommonUtil.checkMap(params);
        // 调用服务层的分页查询方法
        PageUtils page = cartService.queryPage(params);

        // 获取查询结果列表
        List<CartView> list = (List<CartView>) page.getList();
        // 对列表中的每个 CartView 对象进行字典转换
        for (CartView c : list) {
            dictionaryService.dictionaryConvert(c, request);
        }
        // 返回成功响应，并将分页数据放入响应中
        return R.ok().put("data", page);
    }

    /**
     * 后端详情查询
     * @param id 购物车记录ID，用于唯一标识一条购物车记录
     * @param request HttpServletRequest对象，可用于获取请求相关信息
     * @return 封装了购物车详情的响应对象 R，如果未找到则返回错误信息
     */
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
//        // 调用服务层的获取详情方法
//        CartView view = cartService.getCartInfo(id, request);
//        if (view != null) {
//            // 如果找到记录，返回成功响应并将详情数据放入响应中
//            return R.ok().put("data", view);
//        } else {
//            // 如果未找到记录，返回错误响应
//            return R.error(511, "查不到数据");
//        }
//    }

    /**
     * 后端保存购物车记录
     * @param cart 购物车实体对象，包含购物车的相关信息
     * @return 保存结果的响应对象 R，如果保存失败则返回错误信息
     */
//    @RequestMapping("/save")
//    public R save(@RequestBody CartEntity cart) {
//        if (cartService.saveCart(cart)) {
//            // 如果保存成功，返回成功响应
//            return R.ok();
//        } else {
//            // 如果保存失败，返回错误响应
//            return R.error(511, "商品已添加到购物车");
//        }
//    }

    /**
     * 处理购物车数量变化
     * @param cart 购物车实体对象，包含要修改的购物车信息
     * @return 修改结果的响应对象 R，如果修改失败则返回错误信息
     */
    @RequestMapping("/update")
    public R update(@RequestBody CartEntity cart) {
        if (cartService.updateCart(cart)) {
            // 如果修改成功，返回成功响应
            return R.ok();
        } else {
            // 如果修改失败，返回错误响应
            return R.error(511, "修改失败");
        }
    }

    /**
     * 删除购物车记录
     * @param ids 要删除的购物车记录ID数组，可批量删除
     * @return 删除结果的响应对象 R，如果删除失败则返回错误信息
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        if (cartService.deleteCarts(ids)) {
            // 如果删除成功，返回成功响应
            return R.ok();
        } else {
            // 如果删除失败，返回错误响应
            return R.error(511, "删除失败");
        }
    }

    /**
     * 用户购物车列表分页查询：用于检查商品是否已被添加到购物车
     * @param params 查询参数，yonghuId和jiadianId
     * @param request HttpServletRequest对象，可用于获取请求相关信息
     * @return 封装了分页数据的响应对象 R
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 检查查询参数是否合法
        CommonUtil.checkMap(params);
        // 调用服务层的分页查询方法
        PageUtils page = cartService.queryPage(params);

        // 获取查询结果列表
        List<CartView> list = (List<CartView>) page.getList();
        // 对列表中的每个 CartView 对象进行字典转换
        for (CartView c : list) {
            dictionaryService.dictionaryConvert(c, request);
        }
        // 返回成功响应，并将分页数据放入响应中
        return R.ok().put("data", page);
    }

    /**
     * 前端详情查询
     * @param id 购物车记录ID，用于唯一标识一条购物车记录
     * @param request HttpServletRequest对象，可用于获取请求相关信息
     * @return 封装了购物车详情的响应对象 R，如果未找到则返回错误信息
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        // 调用服务层的获取详情方法
        CartView view = cartService.getCartDetail(id, request);
        if (view != null) {
            // 如果找到记录，返回成功响应并将详情数据放入响应中
            return R.ok().put("data", view);
        } else {
            // 如果未找到记录，返回错误响应
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 加入购物车
     * @param cart 购物车实体对象，包含购物车的相关信息
     * @return 保存结果的响应对象 R，如果保存失败则返回错误信息
     */
    @RequestMapping("/add")
    public R add(@RequestBody CartEntity cart) {
        if (cartService.addCart(cart)) {
            // 如果保存成功，返回成功响应
            return R.ok();
        } else {
            // 如果保存失败，返回错误响应
            return R.error(511, "表中有相同数据");
        }
    }
}