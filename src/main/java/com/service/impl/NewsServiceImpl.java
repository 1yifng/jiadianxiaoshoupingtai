package com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.NewsDao;
import com.entity.NewsEntity;
import com.service.NewsService;
import com.entity.view.NewsView;
import com.service.DictionaryService;
import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import javax.servlet.http.HttpServletRequest;

/**
 * 公告信息 服务实现类
 */
@Service("newsService")
@Transactional
public class NewsServiceImpl extends ServiceImpl<NewsDao, NewsEntity> implements NewsService {

    @Autowired
    private DictionaryService dictionaryService; // 字典服务

    @Override
    public PageUtils queryPage(Map<String, Object> params, HttpServletRequest request) {
        // 创建分页对象
        Page<NewsView> page = new Query<NewsView>(params).getPage();
        // 查询分页数据
        page.setRecords(baseMapper.selectListView(page, params));
        // 获取查询结果列表
        List<NewsView> list = page.getRecords();
        // 进行字典表数据转换
        for (NewsView c : list) {
            dictionaryService.dictionaryConvert(c, request);
        }
        return new PageUtils(page);
    }

    @Override
    public NewsView getNewsInfo(Long id, HttpServletRequest request) {
        // 通过ID从服务层获取新闻实体
        NewsEntity news = selectById(id);
        if (news != null) {
            // 创建视图对象
            NewsView view = new NewsView();
            // 复制实体属性到视图对象
            BeanUtils.copyProperties(news, view);
            // 进行字典字段转换
            dictionaryService.dictionaryConvert(view, request);
            return view;
        }
        return null;
    }

    @Override
    public boolean saveNews(NewsEntity news) {
        // 创建查询条件
        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
                .eq("news_name", news.getNewsName())
                .eq("news_types", news.getNewsTypes());
        // 查询是否存在相同数据
        NewsEntity newsEntity = selectOne(queryWrapper);
        if (newsEntity == null) {
            // 设置插入时间和创建时间
            news.setInsertTime(new Date());
            news.setCreateTime(new Date());
            // 插入新闻信息
            return insert(news);
        }
        return false;
    }

    @Override
    public boolean updateNews(NewsEntity news) {
        if ("".equals(news.getNewsPhoto()) || "null".equals(news.getNewsPhoto())) {
            news.setNewsPhoto(null);
        }
        // 根据ID更新新闻信息
        return updateById(news);
    }

    @Override
    public boolean deleteNews(Integer[] ids) {
        // 批量删除新闻信息
        return deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public PageUtils getFrontendList(Map<String, Object> params, HttpServletRequest request) {
        // 创建分页对象
        Page<NewsView> page = new Query<NewsView>(params).getPage();
        // 查询分页数据
        page.setRecords(baseMapper.selectListView(page, params));
        // 获取查询结果列表
        List<NewsView> list = page.getRecords();
        // 进行字典表数据转换
        for (NewsView c : list) {
            dictionaryService.dictionaryConvert(c, request);
        }
        return new PageUtils(page);
    }

    @Override
    public NewsView getFrontendDetail(Long id, HttpServletRequest request) {
        // 根据主键ID查询公告实体
        NewsEntity news = selectById(id);
        if (news != null) {
            // 创建视图对象
            NewsView view = new NewsView();
            // 复制实体属性到视图对象
            BeanUtils.copyProperties(news, view);
            // 进行字典表字段转换
            dictionaryService.dictionaryConvert(view, request);
            return view;
        }
        return null;
    }
}