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
import com.utils.R;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

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

    @Override
    public R saveJiadianCollection(JiadianCollectionEntity jiadianCollection, HttpServletRequest request) {
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");
        else if("用户".equals(role))
            jiadianCollection.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        Wrapper<JiadianCollectionEntity> queryWrapper = new EntityWrapper<JiadianCollectionEntity>()
                .eq("jiadian_id", jiadianCollection.getJiadianId())
                .eq("yonghu_id", jiadianCollection.getYonghuId())
                .eq("jiadian_collection_types", jiadianCollection.getJiadianCollectionTypes());

        JiadianCollectionEntity jiadianCollectionEntity = this.selectOne(queryWrapper);
        if(jiadianCollectionEntity==null){
            jiadianCollection.setInsertTime(new Date());
            jiadianCollection.setCreateTime(new Date());
            this.insert(jiadianCollection);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    @Override
    public R updateJiadianCollection(JiadianCollectionEntity jiadianCollection) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.updateById(jiadianCollection);
        return R.ok();
    }

    @Override
    public R deleteJiadianCollections(Integer[] ids) {
        this.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    @Override
    public R deleteJiadianCollectionByUser(Integer jiadianId, Integer yonghuId) {
        Wrapper<JiadianCollectionEntity> queryWrapper = new EntityWrapper<>();
        queryWrapper.eq("jiadian_id", jiadianId).eq("yonghu_id", yonghuId);
        boolean result = this.delete(queryWrapper);
        if (result) {
            return R.ok().put("message", "删除成功");
        } else {
            return R.error(511, "删除失败，未找到相关记录");
        }
    }

    @Override
    public R addJiadianCollection(JiadianCollectionEntity jiadianCollection) {
        Wrapper<JiadianCollectionEntity> queryWrapper = new EntityWrapper<JiadianCollectionEntity>()
                .eq("jiadian_id", jiadianCollection.getJiadianId())
                .eq("yonghu_id", jiadianCollection.getYonghuId())
                .eq("jiadian_collection_types", jiadianCollection.getJiadianCollectionTypes());
        JiadianCollectionEntity jiadianCollectionEntity = this.selectOne(queryWrapper);
        if(jiadianCollectionEntity==null){
            jiadianCollection.setInsertTime(new Date());
            jiadianCollection.setCreateTime(new Date());
            this.insert(jiadianCollection);
            return R.ok();
        } else {
            return R.error(511, "您已经收藏过了");
        }
    }
}