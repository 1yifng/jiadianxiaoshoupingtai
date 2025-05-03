
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
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private static final String TABLE_NAME = "news";

    @Autowired
    private NewsService newsService;


    @Autowired
    private TokenService tokenService;

    @Autowired
    private AddressService addressService;//收货地址
    @Autowired
    private CartService cartService;//购物车
    @Autowired
    private DictionaryService dictionaryService;//字典
    @Autowired
    private ForumService forumService;//论坛
    @Autowired
    private JiadianService jiadianService;//商品
    @Autowired
    private JiadianCollectionService jiadianCollectionService;//商品收藏
    @Autowired
    private JiadianCommentbackService jiadianCommentbackService;//商品评价
    @Autowired
    private JiadianOrderService jiadianOrderService;//商品订单
    @Autowired
    private LiuyanService liuyanService;//留言反馈
    @Autowired
    private YonghuService yonghuService;//用户
    @Autowired
    private UsersService usersService;//管理员


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("用户".equals(role))
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
        // 记录调试日志：显示当前执行方法和入参信息
        logger.debug("info方法:,,Controller:{},,id:{}", this.getClass().getName(), id);
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
    public R save(@RequestBody NewsEntity news, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,news:{}",this.getClass().getName(),news.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
            .eq("news_name", news.getNewsName())
            .eq("news_types", news.getNewsTypes())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
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
    public R update(@RequestBody NewsEntity news, HttpServletRequest request) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        logger.debug("update方法:,,Controller:{},,news:{}",this.getClass().getName(),news.toString());
        NewsEntity oldNewsEntity = newsService.selectById(news.getId());//查询原先数据

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
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
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
//        List<NewsEntity> oldNewsList =newsService.selectBatchIds(Arrays.asList(ids));//要删除的数据
        newsService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<NewsEntity> newsList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            NewsEntity newsEntity = new NewsEntity();
//                            newsEntity.setNewsName(data.get(0));                    //公告标题 要改的
//                            newsEntity.setNewsTypes(Integer.valueOf(data.get(0)));   //公告类型 要改的
//                            newsEntity.setNewsPhoto("");//详情和图片
//                            newsEntity.setInsertTime(date);//时间
//                            newsEntity.setNewsContent("");//详情和图片
//                            newsEntity.setCreateTime(date);//时间
                            newsList.add(newsEntity);


                            //把要查询是否重复的字段放入map中
                        }

                        //查询是否重复
                        newsService.insertBatch(newsList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }




    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

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
        // 记录调试日志（包含类名和方法参数）
        logger.debug("detail方法:,,Controller:{},,id:{}", this.getClass().getName(), id);

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
    public R add(@RequestBody NewsEntity news, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,news:{}",this.getClass().getName(),news.toString());
        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
            .eq("news_name", news.getNewsName())
            .eq("news_types", news.getNewsTypes())
//            .notIn("news_types", new Integer[]{102})
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
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

