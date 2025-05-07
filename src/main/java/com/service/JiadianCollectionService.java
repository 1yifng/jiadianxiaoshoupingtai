package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.JiadianCollectionEntity;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import java.util.List;

/**
 * 商品收藏 服务类
 */
public interface JiadianCollectionService extends IService<JiadianCollectionEntity> {

    /**
     * @param params 查询参数
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 后端保存商品收藏
     * @param jiadianCollection 商品收藏实体
     * @param request HttpServletRequest对象
     * @return 操作结果
     */
    com.utils.R saveJiadianCollection(JiadianCollectionEntity jiadianCollection, HttpServletRequest request);

    /**
     * 后端修改商品收藏
     * @param jiadianCollection 商品收藏实体
     * @return 操作结果
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    com.utils.R updateJiadianCollection(JiadianCollectionEntity jiadianCollection) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException;

    /**
     * 管理员删除商品收藏
     * @param ids 要删除的商品收藏ID数组
     * @return 操作结果
     */
    com.utils.R deleteJiadianCollections(Integer[] ids);

    /**
     * 用户取消收藏
     * @param jiadianId 商品ID
     * @param yonghuId 用户ID
     * @return 操作结果
     */
    com.utils.R deleteJiadianCollectionByUser(Integer jiadianId, Integer yonghuId);

    /**
     * 添加收藏
     * @param jiadianCollection 商品收藏实体
     * @return 操作结果
     */
    com.utils.R addJiadianCollection(JiadianCollectionEntity jiadianCollection);
}