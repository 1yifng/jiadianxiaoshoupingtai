package com.controller;

import com.annotation.IgnoreAuth;
import com.entity.JiadianEntity;
import com.entity.view.JiadianView;
import com.service.DictionaryService;
import com.service.JiadianOrderService;
import com.service.JiadianService;
import com.utils.CommonUtil;
import com.utils.PageUtils;
import com.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 商品
 * 后端接口
 * @author
 * @email
 */
@RestController
@Controller
@RequestMapping("/jiadian")
public class JiadianController {

    @Autowired
    private JiadianService jiadianService;
    @Autowired
    private DictionaryService dictionaryService; // 字典
    @Autowired
    private JiadianOrderService jiadianOrderService; // 商品订单

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role))
            params.put("yonghuId", request.getSession().getAttribute("userId"));
        params.put("jiadianDeleteStart", 1);
        params.put("jiadianDeleteEnd", 1);
        CommonUtil.checkMap(params);
        PageUtils page = jiadianService.queryPage(params);

        // 字典表数据转换
        List<JiadianView> list = (List<JiadianView>) page.getList();
        for (JiadianView c : list) {
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        JiadianEntity jiadian = jiadianService.selectById(id);
        if (jiadian != null) {
            // entity转view
            JiadianView view = new JiadianView();
            BeanUtils.copyProperties(jiadian, view); // 把实体数据重构到view中
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody JiadianEntity jiadian) {
        return jiadianService.saveJiadian(jiadian);
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianEntity jiadian) {
        return jiadianService.updateJiadian(jiadian);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        return jiadianService.deleteJiadian(ids);
    }

    /**
     * 个性推荐
     */
    @IgnoreAuth
    @RequestMapping("/gexingtuijian")
    public R gexingtuijian(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 添加默认排序
        if (!params.containsKey("sort")) {
            params.put("sort", "id");
        }
        if (!params.containsKey("order")) {
            params.put("order", "desc");
        }
        return jiadianService.gexingtuijianJiadian(params, request);
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        CommonUtil.checkMap(params);
        PageUtils page = jiadianService.queryPage(params);

        // 字典表数据转换
        List<JiadianView> list = (List<JiadianView>) page.getList();
        for (JiadianView c : list)
            dictionaryService.dictionaryConvert(c, request); // 修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        JiadianEntity jiadian = jiadianService.selectById(id);
        if (jiadian != null) {

            // 点击数量加1
            jiadian.setJiadianClicknum(jiadian.getJiadianClicknum() + 1);
            jiadianService.updateById(jiadian);

            // entity转view
            JiadianView view = new JiadianView();
            BeanUtils.copyProperties(jiadian, view); // 把实体数据重构到view中

            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody JiadianEntity jiadian) {
        return jiadianService.addJiadian(jiadian);
    }
}