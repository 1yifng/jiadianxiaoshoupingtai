package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.NewsEntity;
import com.entity.view.NewsView;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import java.util.List;

/**
 * 公告信息 服务类
 */
public interface NewsService extends IService<NewsEntity> {

    /**
     * 根据查询参数进行分页查询
     * @param params 查询参数
     * @param request HTTP请求对象，用于字典表数据转换
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params, HttpServletRequest request);

    /**
     * 根据ID获取新闻详情信息
     * @param id 新闻记录的主键ID
     * @param request HTTP请求对象，用于字典表数据转换
     * @return 包含新闻详情的视图对象
     */
    NewsView getNewsInfo(Long id, HttpServletRequest request);

    /**
     * 保存新闻信息
     * @param news 新闻实体对象
     * @return 保存结果，成功返回true，失败返回false
     */
    boolean saveNews(NewsEntity news);

    /**
     * 修改新闻信息
     * @param news 新闻实体对象
     * @return 修改结果，成功返回true，失败返回false
     */
    boolean updateNews(NewsEntity news);

    /**
     * 删除新闻信息
     * @param ids 新闻记录的主键ID数组
     * @return 删除结果，成功返回true，失败返回false
     */
    boolean deleteNews(Integer[] ids);

    /**
     * 前端列表查询
     * @param params 查询参数
     * @param request HTTP请求对象，用于字典表数据转换
     * @return 带分页的查询出来的数据
     */
    PageUtils getFrontendList(Map<String, Object> params, HttpServletRequest request);

    /**
     * 前端公告详情查询
     * @param id 公告唯一标识符
     * @param request HTTP请求对象，用于字典表数据转换
     * @return 包含公告详细数据的视图对象
     */
    NewsView getFrontendDetail(Long id, HttpServletRequest request);
}