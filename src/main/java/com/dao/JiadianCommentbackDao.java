package com.dao;

import com.entity.JiadianCommentbackEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.JiadianCommentbackView;

/**
 * 商品评价 Dao 接口
 */
public interface JiadianCommentbackDao extends BaseMapper<JiadianCommentbackEntity> {

   List<JiadianCommentbackView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
