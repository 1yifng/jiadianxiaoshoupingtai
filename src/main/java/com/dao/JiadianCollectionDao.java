package com.dao;

import com.entity.JiadianCollectionEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.JiadianCollectionView;

/**
 * 商品收藏 Dao 接口
 */
public interface JiadianCollectionDao extends BaseMapper<JiadianCollectionEntity> {

   List<JiadianCollectionView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
