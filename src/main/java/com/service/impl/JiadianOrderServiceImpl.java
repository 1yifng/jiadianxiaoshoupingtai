package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.JiadianOrderDao;
import com.entity.JiadianOrderEntity;
import com.service.JiadianOrderService;
import com.entity.view.JiadianOrderView;

/**
 * 商品订单 服务实现类
 */
@Service("jiadianOrderService")
@Transactional
public class JiadianOrderServiceImpl extends ServiceImpl<JiadianOrderDao, JiadianOrderEntity> implements JiadianOrderService {

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        Page<JiadianOrderView> page =new Query<JiadianOrderView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }


}
