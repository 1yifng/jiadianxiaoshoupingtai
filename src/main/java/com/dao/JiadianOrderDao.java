package com.dao;

import com.entity.JiadianOrderEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.JiadianOrderView;

/**
 * 商品订单 Dao 接口
 */
public interface JiadianOrderDao extends BaseMapper<JiadianOrderEntity> {

   List<JiadianOrderView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

   /**
    * 根据 jiadian_id 和 yonghu_id 查询订单记录
    * @param jiadianId 商品 ID
    * @param yonghuId 用户 ID
    * @return 订单记录
    */
// 正确写法（返回列表）
   List<JiadianOrderEntity> selectByJiadianIdAndYonghuId(
           @Param("jiadianId") Integer jiadianId,
           @Param("yonghuId") Integer yonghuId
   );

}
