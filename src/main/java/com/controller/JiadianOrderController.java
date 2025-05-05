
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
 * 商品订单
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/jiadianOrder")
public class JiadianOrderController {

    @Autowired
    private JiadianOrderService jiadianOrderService;
    @Autowired
    private AddressService addressService;//收货地址
    @Autowired
    private CartService cartService;//购物车
    @Autowired
    private DictionaryService dictionaryService;//字典
    @Autowired
    private JiadianService jiadianService;//商品
    @Autowired
    private JiadianCommentbackService jiadianCommentbackService;//商品评价
    @Autowired
    private YonghuService yonghuService;//用户


    /**
    * 管理员查订单列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = jiadianOrderService.queryPage(params);

        //字典表数据转换
        List<JiadianOrderView> list =(List<JiadianOrderView>)page.getList();
        for(JiadianOrderView c:list){
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
        JiadianOrderEntity jiadianOrder = jiadianOrderService.selectById(id);
        if(jiadianOrder !=null){
            //entity转view
            JiadianOrderView view = new JiadianOrderView();
            BeanUtils.copyProperties( jiadianOrder , view );//把实体数据重构到view中
            //级联表 收货地址
            //级联表
            AddressEntity address = addressService.selectById(jiadianOrder.getAddressId());
            if(address != null){
            BeanUtils.copyProperties( address , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setAddressId(address.getId());
            }
            //级联表 商品
            //级联表
            JiadianEntity jiadian = jiadianService.selectById(jiadianOrder.getJiadianId());
            if(jiadian != null){
            BeanUtils.copyProperties( jiadian , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "yonghuId"});//把级联的数据添加到view中,并排除id和创建时间字段,当前表的级联注册表
            view.setJiadianId(jiadian.getId());
            }
            //级联表 用户
            //级联表
            YonghuEntity yonghu = yonghuService.selectById(jiadianOrder.getYonghuId());
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
    public R save(@RequestBody JiadianOrderEntity jiadianOrder, HttpServletRequest request){

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            jiadianOrder.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        jiadianOrder.setCreateTime(new Date());
        jiadianOrder.setInsertTime(new Date());
        jiadianOrderService.insert(jiadianOrder);

        return R.ok();
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody JiadianOrderEntity jiadianOrder) {
            jiadianOrderService.updateById(jiadianOrder);//根据id更新
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        jiadianOrderService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
    * 订单数据列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){

        CommonUtil.checkMap(params);
        PageUtils page = jiadianOrderService.queryPage(params);

        //字典表数据转换
        List<JiadianOrderView> list =(List<JiadianOrderView>)page.getList();
        for(JiadianOrderView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        JiadianOrderEntity jiadianOrder = jiadianOrderService.selectById(id);
            if(jiadianOrder !=null){
                //entity转view
                JiadianOrderView view = new JiadianOrderView();
                BeanUtils.copyProperties( jiadianOrder , view );//把实体数据重构到view中

                //级联表
                    AddressEntity address = addressService.selectById(jiadianOrder.getAddressId());
                if(address != null){
                    BeanUtils.copyProperties( address , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setAddressId(address.getId());
                }
                //级联表
                    JiadianEntity jiadian = jiadianService.selectById(jiadianOrder.getJiadianId());
                if(jiadian != null){
                    BeanUtils.copyProperties( jiadian , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setJiadianId(jiadian.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(jiadianOrder.getYonghuId());
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
    public R add(@RequestBody JiadianOrderEntity jiadianOrder, HttpServletRequest request){
            JiadianEntity jiadianEntity = jiadianService.selectById(jiadianOrder.getJiadianId());
            if(jiadianEntity == null){
                return R.error(511,"查不到该商品");
            }

            else if(jiadianEntity.getJiadianNewMoney() == null){
                return R.error(511,"现价不能为空");
            }
            else if((jiadianEntity.getJiadianKucunNumber() -jiadianOrder.getBuyNumber())<0){
                return R.error(511,"购买数量不能大于库存数量");
            }

            Integer userId = (Integer) request.getSession().getAttribute("userId");
            YonghuEntity yonghuEntity = yonghuService.selectById(userId);
            if(yonghuEntity == null)
                return R.error(511,"用户不能为空");
            if(yonghuEntity.getNewMoney() == null)
                return R.error(511,"用户金额不能为空");
            double balance = yonghuEntity.getNewMoney() - jiadianEntity.getJiadianNewMoney()*jiadianOrder.getBuyNumber();//余额
            if(balance<0)
                return R.error(511,"余额不够支付");
            jiadianOrder.setJiadianOrderTypes(101); //设置订单状态为已支付
            jiadianOrder.setJiadianOrderTruePrice(jiadianEntity.getJiadianNewMoney()*jiadianOrder.getBuyNumber()); //设置实付价格
            jiadianOrder.setYonghuId(userId); //设置订单支付人id
            jiadianOrder.setJiadianOrderUuidNumber(String.valueOf(new Date().getTime()));
            jiadianOrder.setJiadianOrderPaymentTypes(1);
            jiadianOrder.setInsertTime(new Date());
            jiadianOrder.setCreateTime(new Date());
                jiadianEntity.setJiadianKucunNumber( jiadianEntity.getJiadianKucunNumber() -jiadianOrder.getBuyNumber());
                jiadianService.updateById(jiadianEntity);
                jiadianOrderService.insert(jiadianOrder);//新增订单
            //更新第一注册表
            yonghuEntity.setNewMoney(balance);//设置金额
            yonghuService.updateById(yonghuEntity);


            return R.ok();
    }
    /**
     * 添加订单
     */
    @RequestMapping("/order")
    public R add(@RequestParam Map<String, Object> params, HttpServletRequest request){
        String jiadianOrderUuidNumber = String.valueOf(new Date().getTime());

        //获取当前登录用户的id
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        Integer addressId = Integer.valueOf(String.valueOf(params.get("addressId")));
        Integer jiadianOrderPaymentTypes = Integer.valueOf(String.valueOf(params.get("jiadianOrderPaymentTypes")));//支付类型

        String data = String.valueOf(params.get("jiadians"));
        JSONArray jsonArray = JSON.parseArray(data);
        List<Map> jiadians = JSON.parseObject(jsonArray.toString(), List.class);

        //获取当前登录用户的个人信息
        YonghuEntity yonghuEntity = yonghuService.selectById(userId);

        //当前订单表
        List<JiadianOrderEntity> jiadianOrderList = new ArrayList<>();
        //商品表
        List<JiadianEntity> jiadianList = new ArrayList<>();
        //购物车ids
        List<Integer> cartIds = new ArrayList<>();

        BigDecimal zhekou = new BigDecimal(1.0);

        //循环取出需要的数据
        for (Map<String, Object> map : jiadians) {
           //取值
            Integer jiadianId = Integer.valueOf(String.valueOf(map.get("jiadianId")));//商品id
            Integer buyNumber = Integer.valueOf(String.valueOf(map.get("buyNumber")));//购买数量
            JiadianEntity jiadianEntity = jiadianService.selectById(jiadianId);//购买的商品
            String id = String.valueOf(map.get("id"));
            if(StringUtil.isNotEmpty(id))
                cartIds.add(Integer.valueOf(id));

            //判断商品的库存是否足够
            if(jiadianEntity.getJiadianKucunNumber() < buyNumber){
                //商品库存不足直接返回
                return R.error(jiadianEntity.getJiadianName()+"的库存不足");
            }else{
                //商品库存充足就减库存
                jiadianEntity.setJiadianKucunNumber(jiadianEntity.getJiadianKucunNumber() - buyNumber);
            }

            //订单信息表增加数据
            JiadianOrderEntity jiadianOrderEntity = new JiadianOrderEntity<>();

            //赋值订单信息
            jiadianOrderEntity.setJiadianOrderUuidNumber(jiadianOrderUuidNumber);//订单号
            jiadianOrderEntity.setAddressId(addressId);//收货地址
            jiadianOrderEntity.setJiadianId(jiadianId);//商品
            jiadianOrderEntity.setYonghuId(userId);//用户
            jiadianOrderEntity.setBuyNumber(buyNumber);//购买数量
            jiadianOrderEntity.setJiadianOrderTypes(101);//订单类型
            jiadianOrderEntity.setJiadianOrderPaymentTypes(jiadianOrderPaymentTypes);//支付类型
            jiadianOrderEntity.setInsertTime(new Date());//订单创建时间
            jiadianOrderEntity.setCreateTime(new Date());//创建时间

            //判断是什么支付方式 1代表余额 2代表积分
            if(jiadianOrderPaymentTypes == 1){//余额支付
                //计算金额
                Double money = new BigDecimal(jiadianEntity.getJiadianNewMoney()).multiply(new BigDecimal(buyNumber)).multiply(zhekou).doubleValue();

                if(yonghuEntity.getNewMoney() - money <0 ){
                    return R.error("余额不足,请充值！！！");
                }else{
                    //计算所获得积分
                    Double buyJifen =0.0;
                yonghuEntity.setNewMoney(yonghuEntity.getNewMoney() - money); //设置金额


                    jiadianOrderEntity.setJiadianOrderTruePrice(money);

                }
            }
            jiadianOrderList.add(jiadianOrderEntity);
            jiadianList.add(jiadianEntity);

        }
        jiadianOrderService.insertBatch(jiadianOrderList);
        jiadianService.updateBatchById(jiadianList);
        yonghuService.updateById(yonghuEntity);
        if(cartIds != null && cartIds.size()>0)
            cartService.deleteBatchIds(cartIds);

        return R.ok();
    }



    /**
     * 用户退款
     */
    @RequestMapping("/refund")
    public R refund(Integer id, HttpServletRequest request) {
        try {
            // 1. 查询订单信息
            JiadianOrderEntity jiadianOrder = jiadianOrderService.selectById(id);
            if (jiadianOrder == null) {
                return R.error(511, "查不到该订单");
            }
            // 获取订单关键信息
            Integer buyNumber = jiadianOrder.getBuyNumber();        // 购买数量
            Integer jiadianOrderPaymentTypes = jiadianOrder.getJiadianOrderPaymentTypes(); // 原支付方式
            Integer jiadianId = jiadianOrder.getJiadianId();        // 商品ID
            // 2. 验证商品信息
            if (jiadianId == null) {
                return R.error(511, "订单中商品ID不能为空");
            }
            JiadianEntity jiadianEntity = jiadianService.selectById(jiadianId);
            if (jiadianEntity == null) {
                return R.error(511, "关联商品不存在");
            }
            if (jiadianEntity.getJiadianNewMoney() == null) {
                return R.error(511, "商品价格异常");
            }
            // 3. 验证用户信息
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            YonghuEntity yonghuEntity = yonghuService.selectById(userId);
            if (yonghuEntity == null) {
                return R.error(511, "用户不存在");
            }
            if (yonghuEntity.getNewMoney() == null) {
                return R.error(511, "用户账户余额异常");
            }
            Double zhekou = 1.0; // 折扣系数（当前为1表示无折扣，可扩展促销场景）
            // 4. 处理不同支付方式的退款逻辑
            if (jiadianOrderPaymentTypes == 1) { // 1-余额支付方式
                // 计算应退金额（商品单价 * 数量 * 折扣）
                Double money = jiadianEntity.getJiadianNewMoney() * buyNumber * zhekou;
                // 退还金额到用户余额
                yonghuEntity.setNewMoney(yonghuEntity.getNewMoney() + money);
            }
            // 5. 恢复商品库存
            jiadianEntity.setJiadianKucunNumber(jiadianEntity.getJiadianKucunNumber() + buyNumber);
            // 6. 更新订单状态为已退款（102）
            jiadianOrder.setJiadianOrderTypes(102);
            jiadianOrderService.updateById(jiadianOrder);
            // 7. 更新用户账户信息
            yonghuService.updateById(yonghuEntity);
            // 8. 更新商品库存信息
            jiadianService.updateById(jiadianEntity);
            return R.ok().put("msg", "退款成功");
        } catch (Exception e) {
            return R.error("退款处理失败，请联系管理员");
        }
    }

    /**
    * 用户评价
    */
    @RequestMapping("/commentback")
    public R commentback(Integer id, String commentbackText, HttpServletRequest request){

            JiadianOrderEntity jiadianOrder = jiadianOrderService.selectById(id);
        if(jiadianOrder == null)
            return R.error(511,"查不到该订单");
        Integer jiadianId = jiadianOrder.getJiadianId();
        if(jiadianId == null)
            return R.error(511,"查不到该商品");

        JiadianCommentbackEntity jiadianCommentbackEntity = new JiadianCommentbackEntity();
            jiadianCommentbackEntity.setId(id);
            jiadianCommentbackEntity.setJiadianId(jiadianId);
            jiadianCommentbackEntity.setYonghuId((Integer) request.getSession().getAttribute("userId"));
            jiadianCommentbackEntity.setJiadianCommentbackText(commentbackText);
            jiadianCommentbackEntity.setInsertTime(new Date());
            jiadianCommentbackEntity.setReplyText(null);
            jiadianCommentbackEntity.setUpdateTime(null);
            jiadianCommentbackEntity.setCreateTime(new Date());
            jiadianCommentbackService.insert(jiadianCommentbackEntity);

            jiadianOrder.setJiadianOrderTypes(105);//设置订单状态为已评价
            jiadianOrderService.updateById(jiadianOrder);//根据id更新
            return R.ok();
    }

    /**
     * 管理员发货
     */
    @RequestMapping("/deliver")
    public R deliver(Integer id ,String jiadianOrderCourierNumber, String jiadianOrderCourierName ){
        JiadianOrderEntity  jiadianOrderEntity = jiadianOrderService.selectById(id);
        jiadianOrderEntity.setJiadianOrderTypes(103);//设置订单状态为已发货
        jiadianOrderEntity.setJiadianOrderCourierNumber(jiadianOrderCourierNumber);
        jiadianOrderEntity.setJiadianOrderCourierName(jiadianOrderCourierName);
        jiadianOrderService.updateById( jiadianOrderEntity);

        return R.ok();
    }


    /**
     * 用户收货
     */
    @RequestMapping("/receiving")
    public R receiving(Integer id ){
        JiadianOrderEntity  jiadianOrderEntity = jiadianOrderService.selectById(id);
        jiadianOrderEntity.setJiadianOrderTypes(104);//设置订单状态为收货
        jiadianOrderService.updateById( jiadianOrderEntity);//
        return R.ok();
    }

}

