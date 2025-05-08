package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.JiadianEntity;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.utils.R;
import org.springframework.lang.Nullable;
import java.util.List;

/**
 * 商品 服务类
 */
public interface JiadianService extends IService<JiadianEntity> {

    /**
     * @param params 查询参数
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 后端保存商品
     * @param jiadian 商品实体
     * @return R对象
     */
    R saveJiadian(JiadianEntity jiadian);

    /**
     * 后端修改商品
     * @param jiadian 商品实体
     * @return R对象
     */
    R updateJiadian(JiadianEntity jiadian) ;

    /**
     * 删除商品
     * @param ids 商品ID数组
     * @return R对象
     */
    R deleteJiadian(Integer[] ids);

    /**
     * 个性推荐商品
     * @param params 查询参数
     * @param request HttpServletRequest对象
     * @return R对象
     */
    R gexingtuijianJiadian(Map<String, Object> params, HttpServletRequest request);

    /**
     * 前端保存商品
     * @param jiadian 商品实体
     * @return R对象
     */
    R addJiadian(JiadianEntity jiadian);
}