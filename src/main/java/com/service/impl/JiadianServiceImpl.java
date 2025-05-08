package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JiadianDao;
import com.entity.JiadianEntity;
import com.entity.view.JiadianOrderView;
import com.entity.view.JiadianView;
import com.service.*;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 商品 服务实现类
 */
@Service("jiadianService")
@Transactional
public class JiadianServiceImpl extends ServiceImpl<JiadianDao, JiadianEntity> implements JiadianService {

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private JiadianOrderService jiadianOrderService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JiadianView> page = new Query<JiadianView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }

    @Override
    public R saveJiadian(JiadianEntity jiadian) {
        Wrapper<JiadianEntity> queryWrapper = new EntityWrapper<JiadianEntity>()
                .eq("jiadian_name", jiadian.getJiadianName())
                .eq("jiadian_types", jiadian.getJiadianTypes())
                .eq("jiadian_kucun_number", jiadian.getJiadianKucunNumber())
                .eq("shangxia_types", jiadian.getShangxiaTypes())
                .eq("jiadian_delete", 1);

        JiadianEntity jiadianEntity = selectOne(queryWrapper);
        if (jiadianEntity == null) {
            jiadian.setJiadianClicknum(1);
            jiadian.setShangxiaTypes(1);
            jiadian.setJiadianDelete(1);
            jiadian.setInsertTime(new Date());
            jiadian.setCreateTime(new Date());
            insert(jiadian);
            return R.ok();
        } else {
            return R.error(511, "表中有相同数据");
        }
    }

    @Override
    public R updateJiadian(JiadianEntity jiadian) {
        if ("".equals(jiadian.getJiadianPhoto()) || "null".equals(jiadian.getJiadianPhoto())) {
            jiadian.setJiadianPhoto(null);
        }
        updateById(jiadian);
        return R.ok();
    }

    @Override
    public R deleteJiadian(Integer[] ids) {
        ArrayList<JiadianEntity> list = new ArrayList<>();
        for (Integer id : ids) {
            JiadianEntity jiadianEntity = new JiadianEntity();
            jiadianEntity.setId(id);
            jiadianEntity.setJiadianDelete(2);
            list.add(jiadianEntity);
        }
        if (list != null && list.size() > 0) {
            updateBatchById(list);
        }
        return R.ok();
    }

    @Override
    public R gexingtuijianJiadian(Map<String, Object> params, HttpServletRequest request) {
        List<JiadianView> returnJiadianViewList = new ArrayList<>();

        // 查询订单时添加排序参数
        Map<String, Object> params1 = new HashMap<>(params);
        params1.put("sort", "id");
        params1.put("order", "desc");
        params1.put("yonghuId", request.getSession().getAttribute("userId"));
        PageUtils pageUtils = jiadianOrderService.queryPage(params1);
        List<JiadianOrderView> orderViewsList = (List<JiadianOrderView>) pageUtils.getList();
        Map<Integer, Integer> typeMap = new HashMap<>(); // 购买的类型list
        for (JiadianOrderView orderView : orderViewsList) {
            Integer jiadianTypes = orderView.getJiadianTypes();
            if (typeMap.containsKey(jiadianTypes)) {
                typeMap.put(jiadianTypes, typeMap.get(jiadianTypes) + 1);
            } else {
                typeMap.put(jiadianTypes, 1);
            }
        }
        List<Integer> typeList = new ArrayList<>(); // 排序后的有序的类型 按最多到最少
        typeMap.entrySet().stream().sorted((o1, o2) -> o2.getValue() - o1.getValue()).forEach(e -> typeList.add(e.getKey())); // 排序
        Integer limit = Integer.valueOf(String.valueOf(params.get("limit")));
        for (Integer type : typeList) {
            Map<String, Object> params2 = new HashMap<>(params);
            params2.put("jiadianTypes", type);
            PageUtils pageUtils1 = queryPage(params2);
            List<JiadianView> jiadianViewList = (List<JiadianView>) pageUtils1.getList();
            returnJiadianViewList.addAll(jiadianViewList);
            if (returnJiadianViewList.size() >= limit) break; // 返回的推荐数量大于要的数量 跳出循环
        }
        // 正常查询出来商品,用于补全推荐缺少的数据
        // 在正常查询补全数据时也确保有排序
        Map<String, Object> queryParams = new HashMap<>(params);
        queryParams.put("sort", "id");
        queryParams.put("order", "desc");
        PageUtils page = queryPage(queryParams);
        if (returnJiadianViewList.size() < limit) { // 返回数量还是小于要求数量
            int toAddNum = limit - returnJiadianViewList.size(); // 要添加的数量
            List<JiadianView> jiadianViewList = (List<JiadianView>) page.getList();
            for (JiadianView jiadianView : jiadianViewList) {
                Boolean addFlag = true;
                for (JiadianView returnJiadianView : returnJiadianViewList) {
                    if (returnJiadianView.getId().intValue() == jiadianView.getId().intValue()) addFlag = false; // 返回的数据中已存在此商品
                }
                if (addFlag) {
                    toAddNum = toAddNum - 1;
                    returnJiadianViewList.add(jiadianView);
                    if (toAddNum == 0) break; // 够数量了
                }
            }
        } else {
            returnJiadianViewList = returnJiadianViewList.subList(0, limit);
        }

        for (JiadianView c : returnJiadianViewList)
            dictionaryService.dictionaryConvert(c, request);
        page.setList(returnJiadianViewList);
        return R.ok().put("data", page);
    }

    @Override
    public R addJiadian(JiadianEntity jiadian) {
        Wrapper<JiadianEntity> queryWrapper = new EntityWrapper<JiadianEntity>()
                .eq("jiadian_name", jiadian.getJiadianName())
                .eq("jiadian_uuid_number", jiadian.getJiadianUuidNumber())
                .eq("jiadian_types", jiadian.getJiadianTypes())
                .eq("jiadian_kucun_number", jiadian.getJiadianKucunNumber())
                .eq("jiadian_clicknum", jiadian.getJiadianClicknum())
                .eq("shangxia_types", jiadian.getShangxiaTypes())
                .eq("jiadian_delete", jiadian.getJiadianDelete());
        JiadianEntity jiadianEntity = selectOne(queryWrapper);
        if (jiadianEntity == null) {
            jiadian.setJiadianClicknum(1);
            jiadian.setJiadianDelete(1);
            jiadian.setInsertTime(new Date());
            jiadian.setCreateTime(new Date());
            insert(jiadian);
            return R.ok();
        } else {
            return R.error(511, "表中有相同数据");
        }
    }
}