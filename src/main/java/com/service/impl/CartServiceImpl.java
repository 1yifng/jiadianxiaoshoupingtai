package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.CartDao;
import com.entity.CartEntity;
import com.entity.JiadianEntity;
import com.entity.YonghuEntity;
import com.entity.view.CartView;
import com.service.CartService;
import com.service.DictionaryService;
import com.service.JiadianService;
import com.service.YonghuService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 购物车 服务实现类
 */
@Service("cartService")
@Transactional
public class CartServiceImpl extends ServiceImpl<CartDao, CartEntity> implements CartService {

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private JiadianService jiadianService;
    @Autowired
    private YonghuService yonghuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<CartView> page = new Query<CartView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }

//    @Override
//    public CartView getCartInfo(Long id, HttpServletRequest request) {
//        CartEntity cart = this.selectById(id);
//        if (cart != null) {
//            CartView view = new CartView();
//            BeanUtils.copyProperties(cart, view);
//
//            YonghuEntity yonghu = yonghuService.selectById(cart.getYonghuId());
//            if (yonghu != null) {
//                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createTime", "insertTime", "updateTime", "yonghuId"});
//                view.setYonghuId(yonghu.getId());
//            }
//
//            JiadianEntity jiadian = jiadianService.selectById(cart.getJiadianId());
//            if (jiadian != null) {
//                BeanUtils.copyProperties(jiadian, view, new String[]{"id", "createTime", "insertTime", "updateTime", "yonghuId"});
//                view.setJiadianId(jiadian.getId());
//            }
//
//            dictionaryService.dictionaryConvert(view, request);
//            return view;
//        }
//        return null;
//    }

//    @Override
//    public boolean saveCart(CartEntity cart) {
//        Wrapper<CartEntity> queryWrapper = new EntityWrapper<CartEntity>()
//                .eq("yonghu_id", cart.getYonghuId())
//                .eq("jiadian_id", cart.getJiadianId())
//                .eq("buy_number", cart.getBuyNumber());
//
//        CartEntity cartEntity = this.selectOne(queryWrapper);
//        if (cartEntity == null) {
//            cart.setCreateTime(new Date());
//            cart.setInsertTime(new Date());
//            return this.insert(cart);
//        }
//        return false;
//    }

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
    public CartView getCartDetail(Long id, HttpServletRequest request) {
        CartEntity cart = this.selectById(id);
        if (cart != null) {
            CartView view = new CartView();
            BeanUtils.copyProperties(cart, view);

            YonghuEntity yonghu = yonghuService.selectById(cart.getYonghuId());
            if (yonghu != null) {
                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createDate"});
                view.setYonghuId(yonghu.getId());
            }

            JiadianEntity jiadian = jiadianService.selectById(cart.getJiadianId());
            if (jiadian != null) {
                BeanUtils.copyProperties(jiadian, view, new String[]{"id", "createDate"});
                view.setJiadianId(jiadian.getId());
            }

            dictionaryService.dictionaryConvert(view, request);
            return view;
        }
        return null;
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