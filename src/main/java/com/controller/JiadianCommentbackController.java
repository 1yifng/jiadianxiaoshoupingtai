
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
 * 商品评价
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/jiadianCommentback")
public class JiadianCommentbackController {
    @Autowired
    private JiadianCommentbackService jiadianCommentbackService;
    @Autowired
    private DictionaryService dictionaryService;//字典
    @Autowired
    private JiadianService jiadianService;//商品
    @Autowired
    private YonghuService yonghuService;//用户


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = jiadianCommentbackService.queryPage(params);

        //字典表数据转换
        List<JiadianCommentbackView> list =(List<JiadianCommentbackView>)page.getList();
        for(JiadianCommentbackView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        JiadianCommentbackEntity jiadianCommentback = jiadianCommentbackService.selectById(id);
        if(jiadianCommentback !=null){
            //entity转view
            JiadianCommentbackView view = new JiadianCommentbackView();
            BeanUtils.copyProperties( jiadianCommentback , view );//把实体数据重构到view中
            //级联表 商品
            //级联表
            JiadianEntity jiadian = jiadianService.selectById(jiadianCommentback.getJiadianId());
            if(jiadian != null){
            BeanUtils.copyProperties( jiadian , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setJiadianId(jiadian.getId());
            }
            //级联表 用户
            //级联表
            YonghuEntity yonghu = yonghuService.selectById(jiadianCommentback.getYonghuId());
            if(yonghu != null){
            BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setYonghuId(yonghu.getId());
            }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody JiadianCommentbackEntity jiadianCommentback, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            jiadianCommentback.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        jiadianCommentback.setCreateTime(new Date());
        jiadianCommentback.setInsertTime(new Date());
        jiadianCommentbackService.insert(jiadianCommentback);

        return R.ok();
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianCommentbackEntity jiadianCommentback)  {
        jiadianCommentback.setUpdateTime(new Date());
        jiadianCommentbackService.updateById(jiadianCommentback);//根据id更新
        return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        jiadianCommentbackService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){

        CommonUtil.checkMap(params);
        PageUtils page = jiadianCommentbackService.queryPage(params);

        //字典表数据转换
        List<JiadianCommentbackView> list =(List<JiadianCommentbackView>)page.getList();
        for(JiadianCommentbackView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        JiadianCommentbackEntity jiadianCommentback = jiadianCommentbackService.selectById(id);
            if(jiadianCommentback !=null){

                //entity转view
                JiadianCommentbackView view = new JiadianCommentbackView();
                BeanUtils.copyProperties( jiadianCommentback , view );//把实体数据重构到view中

                //级联表
                    JiadianEntity jiadian = jiadianService.selectById(jiadianCommentback.getJiadianId());
                if(jiadian != null){
                    BeanUtils.copyProperties( jiadian , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setJiadianId(jiadian.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(jiadianCommentback.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody JiadianCommentbackEntity jiadianCommentback){
        jiadianCommentback.setCreateTime(new Date());
        jiadianCommentback.setInsertTime(new Date());
        jiadianCommentbackService.insert(jiadianCommentback);

            return R.ok();
        }

}

