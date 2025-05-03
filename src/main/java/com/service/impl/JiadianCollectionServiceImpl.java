package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.springframework.stereotype.Service;
import java.util.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.JiadianCollectionDao;
import com.entity.JiadianCollectionEntity;
import com.service.JiadianCollectionService;
import com.entity.view.JiadianCollectionView;

/**
 * 商品收藏 服务实现类
 */
@Service("jiadianCollectionService")
@Transactional
public class JiadianCollectionServiceImpl extends ServiceImpl<JiadianCollectionDao, JiadianCollectionEntity> implements JiadianCollectionService {

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        Page<JiadianCollectionView> page =new Query<JiadianCollectionView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }

    public void deleteByJiadianIdAndYonghuId(Integer jiadianId, Integer yonghuId) {
        Wrapper<JiadianCollectionEntity> queryWrapper = new EntityWrapper<>();
        queryWrapper.eq("jiadian_id", jiadianId).eq("yonghu_id", yonghuId);
        this.delete(queryWrapper);
    }


}
