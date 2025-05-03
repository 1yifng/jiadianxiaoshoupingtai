
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
    private DictionaryService dictionaryService;//字典
    @Autowired
    private JiadianOrderService jiadianOrderService;//商品订单


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        params.put("jiadianDeleteStart",1);params.put("jiadianDeleteEnd",1);
        CommonUtil.checkMap(params);
        PageUtils page = jiadianService.queryPage(params);

        //字典表数据转换
        List<JiadianView> list =(List<JiadianView>)page.getList();
        for(JiadianView c:list){
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
        JiadianEntity jiadian = jiadianService.selectById(id);
        if(jiadian !=null){
            //entity转view
            JiadianView view = new JiadianView();
            BeanUtils.copyProperties( jiadian , view );//把实体数据重构到view中
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
    public R save(@RequestBody JiadianEntity jiadian){
        Wrapper<JiadianEntity> queryWrapper = new EntityWrapper<JiadianEntity>()
            .eq("jiadian_name", jiadian.getJiadianName())
            .eq("jiadian_types", jiadian.getJiadianTypes())
            .eq("jiadian_kucun_number", jiadian.getJiadianKucunNumber())
            .eq("shangxia_types", jiadian.getShangxiaTypes())
            .eq("jiadian_delete", 1)
            ;

        JiadianEntity jiadianEntity = jiadianService.selectOne(queryWrapper);
        if(jiadianEntity==null){
            jiadian.setJiadianClicknum(1);
            jiadian.setShangxiaTypes(1);
            jiadian.setJiadianDelete(1);
            jiadian.setInsertTime(new Date());
            jiadian.setCreateTime(new Date());
            jiadianService.insert(jiadian);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianEntity jiadian) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        if("".equals(jiadian.getJiadianPhoto()) || "null".equals(jiadian.getJiadianPhoto())){
                jiadian.setJiadianPhoto(null);
        }
            jiadianService.updateById(jiadian);//根据id更新
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        ArrayList<JiadianEntity> list = new ArrayList<>();
        for(Integer id:ids){
            JiadianEntity jiadianEntity = new JiadianEntity();
            jiadianEntity.setId(id);
            jiadianEntity.setJiadianDelete(2);
            list.add(jiadianEntity);
        }
        if(list != null && list.size() >0){
            jiadianService.updateBatchById(list);
        }

        return R.ok();
    }


    /**
    * 个性推荐
    */
    @IgnoreAuth
    @RequestMapping("/gexingtuijian")
    public R gexingtuijian(@RequestParam Map<String, Object> params, HttpServletRequest request){
        CommonUtil.checkMap(params);
        List<JiadianView> returnJiadianViewList = new ArrayList<>();

        //查询订单
        Map<String, Object> params1 = new HashMap<>(params);params1.put("sort","id");params1.put("yonghuId",request.getSession().getAttribute("userId"));
        PageUtils pageUtils = jiadianOrderService.queryPage(params1);
        List<JiadianOrderView> orderViewsList =(List<JiadianOrderView>)pageUtils.getList();
        Map<Integer,Integer> typeMap=new HashMap<>();//购买的类型list
        for(JiadianOrderView orderView:orderViewsList){
            Integer jiadianTypes = orderView.getJiadianTypes();
            if(typeMap.containsKey(jiadianTypes)){
                typeMap.put(jiadianTypes,typeMap.get(jiadianTypes)+1);
            }else{
                typeMap.put(jiadianTypes,1);
            }
        }
        List<Integer> typeList = new ArrayList<>();//排序后的有序的类型 按最多到最少
        typeMap.entrySet().stream().sorted((o1, o2) -> o2.getValue() - o1.getValue()).forEach(e -> typeList.add(e.getKey()));//排序
        Integer limit = Integer.valueOf(String.valueOf(params.get("limit")));
        for(Integer type:typeList){
            Map<String, Object> params2 = new HashMap<>(params);params2.put("jiadianTypes",type);
            PageUtils pageUtils1 = jiadianService.queryPage(params2);
            List<JiadianView> jiadianViewList =(List<JiadianView>)pageUtils1.getList();
            returnJiadianViewList.addAll(jiadianViewList);
            if(returnJiadianViewList.size()>= limit) break;//返回的推荐数量大于要的数量 跳出循环
        }
        //正常查询出来商品,用于补全推荐缺少的数据
        PageUtils page = jiadianService.queryPage(params);
        if(returnJiadianViewList.size()<limit){//返回数量还是小于要求数量
            int toAddNum = limit - returnJiadianViewList.size();//要添加的数量
            List<JiadianView> jiadianViewList =(List<JiadianView>)page.getList();
            for(JiadianView jiadianView:jiadianViewList){
                Boolean addFlag = true;
                for(JiadianView returnJiadianView:returnJiadianViewList){
                    if(returnJiadianView.getId().intValue() ==jiadianView.getId().intValue()) addFlag=false;//返回的数据中已存在此商品
                }
                if(addFlag){
                    toAddNum=toAddNum-1;
                    returnJiadianViewList.add(jiadianView);
                    if(toAddNum==0) break;//够数量了
                }
            }
        }else {
            returnJiadianViewList = returnJiadianViewList.subList(0, limit);
        }

        for(JiadianView c:returnJiadianViewList)
            dictionaryService.dictionaryConvert(c, request);
        page.setList(returnJiadianViewList);
        return R.ok().put("data", page);
    }

    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        CommonUtil.checkMap(params);
        PageUtils page = jiadianService.queryPage(params);

        //字典表数据转换
        List<JiadianView> list =(List<JiadianView>)page.getList();
        for(JiadianView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        JiadianEntity jiadian = jiadianService.selectById(id);
            if(jiadian !=null){

                //点击数量加1
                jiadian.setJiadianClicknum(jiadian.getJiadianClicknum()+1);
                jiadianService.updateById(jiadian);

                //entity转view
                JiadianView view = new JiadianView();
                BeanUtils.copyProperties( jiadian , view );//把实体数据重构到view中

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
    public R add(@RequestBody JiadianEntity jiadian){
        Wrapper<JiadianEntity> queryWrapper = new EntityWrapper<JiadianEntity>()
            .eq("jiadian_name", jiadian.getJiadianName())
            .eq("jiadian_uuid_number", jiadian.getJiadianUuidNumber())
            .eq("jiadian_types", jiadian.getJiadianTypes())
            .eq("jiadian_kucun_number", jiadian.getJiadianKucunNumber())
            .eq("jiadian_clicknum", jiadian.getJiadianClicknum())
            .eq("shangxia_types", jiadian.getShangxiaTypes())
            .eq("jiadian_delete", jiadian.getJiadianDelete())
            ;
        JiadianEntity jiadianEntity = jiadianService.selectOne(queryWrapper);
        if(jiadianEntity==null){
            jiadian.setJiadianClicknum(1);
            jiadian.setJiadianDelete(1);
            jiadian.setInsertTime(new Date());
            jiadian.setCreateTime(new Date());
        jiadianService.insert(jiadian);

            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

}

