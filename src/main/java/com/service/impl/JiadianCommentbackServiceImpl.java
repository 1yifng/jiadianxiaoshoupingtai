package com.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JiadianCommentbackDao;
import com.dao.JiadianDao;
import com.dao.JiadianOrderDao;
import com.dao.YonghuDao;
import com.entity.JiadianCommentbackEntity;
import com.entity.JiadianEntity;
import com.entity.JiadianOrderEntity;
import com.entity.YonghuEntity;
import com.entity.view.JiadianCommentbackView;
import com.service.*;
import com.utils.PageUtils;
import com.utils.Query;
import jdk.internal.instrumentation.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品评价 服务实现类
 */
@Service("jiadianCommentbackService")
@Transactional
public class JiadianCommentbackServiceImpl extends ServiceImpl<JiadianCommentbackDao, JiadianCommentbackEntity> implements JiadianCommentbackService {

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private JiadianService jiadianService;
    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private JiadianOrderService jiadianOrderService;
    @Autowired
    private JiadianOrderDao jiadianOrderDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JiadianCommentbackView> page = new Query<JiadianCommentbackView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }

    @Override
    public PageUtils handleBackendPage(Map<String, Object> params, HttpServletRequest request) {
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role)) {
            params.put("yonghuId", request.getSession().getAttribute("userId"));
        }
        PageUtils page = queryPage(params);

        // 字典表数据转换
        List<JiadianCommentbackView> list = (List<JiadianCommentbackView>) page.getList();
        for (JiadianCommentbackView c : list) {
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return page;
    }

    @Override
    public JiadianCommentbackView handleBackendInfo(Long id, HttpServletRequest request) {
        JiadianCommentbackEntity jiadianCommentback = selectById(id);
        if (jiadianCommentback != null) {
            // entity转view
            JiadianCommentbackView view = new JiadianCommentbackView();
            BeanUtils.copyProperties(jiadianCommentback, view); // 把实体数据重构到view中
            // 级联表 商品
            JiadianEntity jiadian = jiadianService.selectById(jiadianCommentback.getJiadianId());
            if (jiadian != null) {
                BeanUtils.copyProperties(jiadian, view, new String[]{"id", "createTime", "insertTime", "updateTime", "yonghuId"}); // 把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
                view.setJiadianId(jiadian.getId());
            }
            // 级联表 用户
            YonghuEntity yonghu = yonghuService.selectById(jiadianCommentback.getYonghuId());
            if (yonghu != null) {
                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createTime", "insertTime", "updateTime", "yonghuId"}); // 把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
                view.setYonghuId(yonghu.getId());
            }
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return view;
        }
        return null;
    }

    @Override
    public void handleBackendSave(JiadianCommentbackEntity jiadianCommentback, HttpServletRequest request) {
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role)) {
            jiadianCommentback.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        }
        jiadianCommentback.setCreateTime(new Date());
        jiadianCommentback.setInsertTime(new Date());
        insert(jiadianCommentback);
    }

    @Override
    public void handleBackendUpdate(JiadianCommentbackEntity jiadianCommentback) {
        jiadianCommentback.setUpdateTime(new Date());
        updateById(jiadianCommentback); // 根据id更新
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAndUpdateOrderStatus(Integer[] ids) {
        // 先查询所有评论信息（在删除前获取数据）
        List<JiadianCommentbackEntity> commentbacks = Arrays.stream(ids)
                .map(this::selectById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 执行删除操作
        deleteBatchIds(Arrays.asList(ids));

        // 更新订单状态
        commentbacks.forEach(commentback -> {
            // 获取订单列表（即使LIMIT 1也返回列表）
            List<JiadianOrderEntity> orders = jiadianOrderDao.selectByJiadianIdAndYonghuId(
                    commentback.getJiadianId(),
                    commentback.getYonghuId()
            );

            if (!orders.isEmpty()) {
                JiadianOrderEntity order = orders.get(0);  // 取第一条记录
                if (order.getJiadianOrderTypes() == 105) {  // 双重校验
                    order.setJiadianOrderTypes(104);
                    jiadianOrderService.updateById(order);
                }
            }
        });
    }



    @Override
    public PageUtils handleFrontendList(Map<String, Object> params, HttpServletRequest request) {
        PageUtils page = queryPage(params);

        // 字典表数据转换
        List<JiadianCommentbackView> list = (List<JiadianCommentbackView>) page.getList();
        for (JiadianCommentbackView c : list) {
            dictionaryService.dictionaryConvert(c, request); // 修改对应字典表字段
        }
        return page;
    }

}