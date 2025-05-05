
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 公告信息
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    private DictionaryService dictionaryService;//字典

    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = newsService.queryPage(params);

        //字典表数据转换
        List<NewsView> list =(List<NewsView>)page.getList();
        for(NewsView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
     * 后端详情 - 根据ID获取新闻详情信息
     * @param id 新闻记录的主键ID，通过URL路径传递
     * @param request HTTP请求对象，用于获取上下文信息（如字典翻译需要locale等）
     * @return 包含新闻详情的统一响应对象 或 错误信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        // 通过ID从服务层获取新闻实体
        NewsEntity news = newsService.selectById(id);
        if(news != null){
            // 创建视图对象并复制实体属性（将持久层实体转换为展示层对象）
            NewsView view = new NewsView();
            BeanUtils.copyProperties(news, view);  // 使用Spring工具类进行属性拷贝
            // 进行字典字段转换（例如：将状态码0/1转换为"正常"/"禁用"等可读文本）
            dictionaryService.dictionaryConvert(view, request);
            // 返回成功响应，携带处理后的视图对象
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody NewsEntity news){

        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
            .eq("news_name", news.getNewsName())
            .eq("news_types", news.getNewsTypes())
            ;

        NewsEntity newsEntity = newsService.selectOne(queryWrapper);
        if(newsEntity==null){
            news.setInsertTime(new Date());
            news.setCreateTime(new Date());
            newsService.insert(news);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody NewsEntity news) {

        if("".equals(news.getNewsPhoto()) || "null".equals(news.getNewsPhoto())){
                news.setNewsPhoto(null);
        }

            newsService.updateById(news);//根据id更新
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        newsService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){

        CommonUtil.checkMap(params);
        PageUtils page = newsService.queryPage(params);

        //字典表数据转换
        List<NewsView> list =(List<NewsView>)page.getList();
        for(NewsView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }


    /**
     * 前端公告详情查询（无需登录）
     * @param id 公告唯一标识符（路径参数）
     * @param request HTTP请求对象（可用于获取上下文信息）
     * @return 包含公告详细数据的响应对象
     *
     * 流程说明：
     * 1. 根据ID查询公告实体数据
     * 2. 转换实体为视图对象（可能过滤敏感字段）
     * 3. 处理字典字段转换（如状态码转文字描述）
     */
    @IgnoreAuth // 开放访问，无需身份验证
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {

        // 1. 根据主键ID查询公告实体
        NewsEntity news = newsService.selectById(id);

        if (news != null) {
            // 2. 创建视图对象并复制属性（实体->视图对象转换）
            NewsView view = new NewsView();
            BeanUtils.copyProperties(news, view); // 使用Spring工具类进行属性拷贝

            // 3. 字典表字段转换（示例：将news_type数值转换为文字说明）
            // 如：1->"系统公告", 2->"活动通知"...
            dictionaryService.dictionaryConvert(view, request);

            // 返回格式化后的公告数据
            return R.ok().put("data", view);
        } else {
            // 处理数据不存在的情况
            return R.error(511, "未查询到ID为["+id+"]的公告信息");
        }
    }

    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody NewsEntity news){
        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
            .eq("news_name", news.getNewsName())
            .eq("news_types", news.getNewsTypes())
            ;
        NewsEntity newsEntity = newsService.selectOne(queryWrapper);
        if(newsEntity==null){
            news.setInsertTime(new Date());
            news.setCreateTime(new Date());
        newsService.insert(news);

            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

}

