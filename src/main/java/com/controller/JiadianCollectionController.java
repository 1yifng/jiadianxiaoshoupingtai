package com.controller;

import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import com.utils.*;
import com.service.DictionaryService;
import com.annotation.IgnoreAuth;
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

/**
 * 商品收藏
 * 后端接口
 * @author
 * @email
 */
@RestController
@Controller
@RequestMapping("/jiadianCollection")
public class JiadianCollectionController {
    @Autowired
    private JiadianCollectionService jiadianCollectionService;
    @Autowired
    private DictionaryService dictionaryService; // 字典
    @Autowired
    private JiadianService jiadianService; // 商品
    @Autowired
    private YonghuService yonghuService; // 用户

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        CommonUtil.checkMap(params);
        PageUtils page = jiadianCollectionService.queryPage(params);

        // 字典表数据转换
        List<JiadianCollectionView> list = (List<JiadianCollectionView>) page.getList();
        for (JiadianCollectionView c : list) {
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
        JiadianCollectionEntity jiadianCollection = jiadianCollectionService.selectById(id);
        if (jiadianCollection != null) {
            // entity转view
            JiadianCollectionView view = new JiadianCollectionView();
            BeanUtils.copyProperties(jiadianCollection, view); // 把实体数据重构到view中
            // 级联表 商品
            // 级联表
            JiadianEntity jiadian = jiadianService.selectById(jiadianCollection.getJiadianId());
            if (jiadian != null) {
                BeanUtils.copyProperties(jiadian, view, new String[]{"id", "createTime", "insertTime", "updateTime", "yonghuId"}); // 把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
                view.setJiadianId(jiadian.getId());
            }
            // 级联表 用户
            // 级联表
            YonghuEntity yonghu = yonghuService.selectById(jiadianCollection.getYonghuId());
            if (yonghu != null) {
                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createTime", "insertTime", "updateTime", "yonghuId"}); // 把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
                view.setYonghuId(yonghu.getId());
            }
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
    public R save(@RequestBody JiadianCollectionEntity jiadianCollection, HttpServletRequest request) {
        return jiadianCollectionService.saveJiadianCollection(jiadianCollection, request);
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianCollectionEntity jiadianCollection) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        return jiadianCollectionService.updateJiadianCollection(jiadianCollection);
    }

    /**
     * 管理员删除商品收藏
     */
    @RequestMapping("/delete1")
    public R delete(@RequestBody Integer[] ids) {
        return jiadianCollectionService.deleteJiadianCollections(ids);
    }

    /**
     * 用户取消收藏
     * 使用 GET 请求，通过查询参数接收 jiadianId 和 yonghuId
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public R delete(@RequestParam("jiadianId") Integer jiadianId, @RequestParam("yonghuId") Integer yonghuId) {
        return jiadianCollectionService.deleteJiadianCollectionByUser(jiadianId, yonghuId);
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        CommonUtil.checkMap(params);
        PageUtils page = jiadianCollectionService.queryPage(params);

        // 字典表数据转换
        List<JiadianCollectionView> list = (List<JiadianCollectionView>) page.getList();
        for (JiadianCollectionView c : list)
            dictionaryService.dictionaryConvert(c, request); // 修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        JiadianCollectionEntity jiadianCollection = jiadianCollectionService.selectById(id);
        if (jiadianCollection != null) {
            // entity转view
            JiadianCollectionView view = new JiadianCollectionView();
            BeanUtils.copyProperties(jiadianCollection, view); // 把实体数据重构到view中

            // 级联表
            JiadianEntity jiadian = jiadianService.selectById(jiadianCollection.getJiadianId());
            if (jiadian != null) {
                BeanUtils.copyProperties(jiadian, view, new String[]{"id", "createDate"}); // 把级联的数据添加到view中,并排除id和创建时间字段
                view.setJiadianId(jiadian.getId());
            }
            // 级联表
            YonghuEntity yonghu = yonghuService.selectById(jiadianCollection.getYonghuId());
            if (yonghu != null) {
                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createDate"}); // 把级联的数据添加到view中,并排除id和创建时间字段
                view.setYonghuId(yonghu.getId());
            }
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 添加收藏
     */
    @RequestMapping("/add")
    public R add(@RequestBody JiadianCollectionEntity jiadianCollection) {
        return jiadianCollectionService.addJiadianCollection(jiadianCollection);
    }
}