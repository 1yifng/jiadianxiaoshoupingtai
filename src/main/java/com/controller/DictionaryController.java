
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
 * 字典
 * 后端接口
*/
@RestController
@Controller
@RequestMapping("/dictionary")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    @IgnoreAuth
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        CommonUtil.checkMap(params);
        PageUtils page = dictionaryService.queryPage(params);

        //字典表数据转换
        List<DictionaryView> list =(List<DictionaryView>)page.getList();
        for(DictionaryView c:list){
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
        DictionaryEntity dictionary = dictionaryService.selectById(id);
        if(dictionary !=null){
            //entity转view
            DictionaryView view = new DictionaryView();
            BeanUtils.copyProperties( dictionary , view );//把实体数据重构到view中
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
    public R save(@RequestBody DictionaryEntity dictionary, HttpServletRequest request){
        Wrapper<DictionaryEntity> queryWrapper = new EntityWrapper<DictionaryEntity>()
            .eq("dic_code", dictionary.getDicCode())
            .eq("index_name", dictionary.getIndexName())
            ;
        if(dictionary.getDicCode().contains("_erji_types")){
            queryWrapper.eq("super_id",dictionary.getSuperId());
        }

        DictionaryEntity dictionaryEntity = dictionaryService.selectOne(queryWrapper);
        if(dictionaryEntity==null){
            dictionary.setCreateTime(new Date());
            dictionaryService.insert(dictionary);
            //字典表新增数据,把数据再重新查出,放入监听器中
            List<DictionaryEntity> dictionaryEntities = dictionaryService.selectList(new EntityWrapper<DictionaryEntity>());
            ServletContext servletContext = request.getServletContext();
            Map<String, Map<Integer,String>> map = new HashMap<>();
            for(DictionaryEntity d :dictionaryEntities){
                Map<Integer, String> m = map.get(d.getDicCode());
                if(m ==null || m.isEmpty()){
                    m = new HashMap<>();
                }
                m.put(d.getCodeIndex(),d.getIndexName());
                map.put(d.getDicCode(),m);
            }
            servletContext.setAttribute("dictionaryMap",map);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody DictionaryEntity dictionary, HttpServletRequest request)  {
            dictionaryService.updateById(dictionary);//根据id更新
            //如果字典表修改数据的话,把数据再重新查出,放入监听器中
            List<DictionaryEntity> dictionaryEntities = dictionaryService.selectList(new EntityWrapper<DictionaryEntity>());
            ServletContext servletContext = request.getServletContext();
            Map<String, Map<Integer,String>> map = new HashMap<>();
            for(DictionaryEntity d :dictionaryEntities){
                Map<Integer, String> m = map.get(d.getDicCode());
                if(m ==null || m.isEmpty()){
                    m = new HashMap<>();
                }
                m.put(d.getCodeIndex(),d.getIndexName());
                map.put(d.getDicCode(),m);
            }
            servletContext.setAttribute("dictionaryMap",map);
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        dictionaryService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 最大值
     */
    @RequestMapping("/maxCodeIndex")
    public R maxCodeIndex(@RequestBody DictionaryEntity dictionary){
        List<String> descs = new ArrayList<>();
        descs.add("code_index");
        Wrapper<DictionaryEntity> queryWrapper = new EntityWrapper<DictionaryEntity>()
                .eq("dic_code", dictionary.getDicCode())
                .orderDesc(descs);

        List<DictionaryEntity> dictionaryEntityList = dictionaryService.selectList(queryWrapper);
        if(dictionaryEntityList.size()>0 ){
            return R.ok().put("maxCodeIndex",dictionaryEntityList.get(0).getCodeIndex()+1);
        }else{
            return R.ok().put("maxCodeIndex",1);
        }
    }
}

