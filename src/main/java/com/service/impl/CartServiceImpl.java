package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.CartDao;
import com.entity.CartEntity;
import com.entity.view.CartView;
import com.service.CartService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Map;

/**
 * 购物车 服务实现类
 */
@Service("cartService")
@Transactional
public class CartServiceImpl extends ServiceImpl<CartDao, CartEntity> implements CartService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<CartView> page = new Query<CartView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }


    @Override
    public boolean updateCart(CartEntity cart) {
        cart.setUpdateTime(new Date());
        return this.updateById(cart);
    }

    @Override
    public boolean deleteCarts(Integer[] ids) {
        return this.deleteBatchIds(java.util.Arrays.asList(ids));
    }


    @Override
    public boolean addCart(CartEntity cart) {
        Wrapper<CartEntity> queryWrapper = new EntityWrapper<CartEntity>()
                .eq("yonghu_id", cart.getYonghuId())
                .eq("jiadian_id", cart.getJiadianId())
                .eq("buy_number", cart.getBuyNumber());

        CartEntity cartEntity = this.selectOne(queryWrapper);
        if (cartEntity == null) {
            cart.setCreateTime(new Date());
            cart.setInsertTime(new Date());
            return this.insert(cart);
        }
        return false;
    }
}