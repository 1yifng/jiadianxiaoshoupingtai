package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.AddressDao;
import com.entity.AddressEntity;
import com.entity.YonghuEntity;
import com.entity.view.AddressView;
import com.service.AddressService;
import com.service.DictionaryService;
import com.service.YonghuService;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 收货地址服务实现类
 * 继承MyBatis-Plus的ServiceImpl，实现AddressService接口
 */
@Service("addressService") // 声明为Spring服务组件
@Transactional // 开启事务管理
public class AddressServiceImpl extends ServiceImpl<AddressDao, AddressEntity> implements AddressService {

    @Autowired
    private DictionaryService dictionaryService; // 字典服务（用于字段转换）
    @Autowired
    private YonghuService yonghuService; // 用户服务

    /**
     * 分页查询收货地址
     * @param params 查询参数（包含分页和过滤条件）
     * @return 分页结果
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 使用Query工具类构建分页参数
        Page<AddressView> page = new Query<AddressView>(params).getPage();
        // 调用DAO层查询数据，并设置到分页对象中
        page.setRecords(baseMapper.selectListView(page, params));
        // 返回分页工具类结果
        return new PageUtils(page);
    }

    /**
     * 新增收货地址
     * @param address 地址实体
     * @param request HTTP请求对象（用于获取用户信息）
     * @return 操作结果
     */
    @Override
    public R saveAddress(AddressEntity address, HttpServletRequest request) {
        // 从会话中获取用户角色
        String role = String.valueOf(request.getSession().getAttribute("role"));
        // 如果是用户角色，设置地址关联的用户ID
        if ("用户".equals(role))
            address.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        // 构建查询条件：检查是否已存在相同地址
        Wrapper<AddressEntity> queryWrapper = new EntityWrapper<AddressEntity>()
                .eq("yonghu_id", address.getYonghuId()) // 用户ID匹配
                .eq("address_name", address.getAddressName()) // 收货人姓名匹配
                .eq("address_phone", address.getAddressPhone()) // 电话匹配
                .eq("address_dizhi", address.getAddressDizhi()) // 地址匹配
                .eq("isdefault_types", address.getIsdefaultTypes()); // 是否默认匹配

        // 查询是否已存在相同地址
        AddressEntity addressEntity = this.selectOne(queryWrapper);
        if (addressEntity == null) {
            // 设置插入时间和创建时间
            address.setInsertTime(new Date());
            address.setCreateTime(new Date());

            // 如果新增的地址是默认地址（isdefault_types=2）
            Integer isdefaultTypes = address.getIsdefaultTypes();
            if (isdefaultTypes == 2) {
                // 查询所有已存在的默认地址
                List<AddressEntity> addressEntitys = this.selectList(
                        new EntityWrapper<AddressEntity>().eq("isdefault_types", 2));
                // 将其他默认地址改为非默认（isdefault_types=1）
                if (addressEntitys != null && addressEntitys.size() > 0) {
                    for (AddressEntity a : addressEntitys)
                        a.setIsdefaultTypes(1);
                    this.updateBatchById(addressEntitys); // 批量更新
                }
            }
            this.insert(address); // 插入新地址
            return R.ok(); // 返回成功
        } else {
            return R.error(511, "表中有相同数据"); // 返回重复错误
        }
    }

    /**
     * 更新收货地址
     * @param address 地址实体
     * @return 操作结果
     */
    @Override
    public R updateAddress(AddressEntity address) {
        // 设置更新时间
        address.setUpdateTime(new Date());

        // 如果更新为默认地址（isdefault_types=2）
        Integer isdefaultTypes = address.getIsdefaultTypes();
        if (isdefaultTypes == 2) {
            // 查询所有已存在的默认地址
            List<AddressEntity> addressEntitys = this.selectList(
                    new EntityWrapper<AddressEntity>().eq("isdefault_types", 2));
            // 将其他默认地址改为非默认
            if (addressEntitys != null && addressEntitys.size() > 0) {
                for (AddressEntity a : addressEntitys)
                    a.setIsdefaultTypes(1);
                this.updateBatchById(addressEntitys); // 批量更新
            }
        }
        this.updateById(address); // 更新当前地址
        return R.ok(); // 返回成功
    }

    /**
     * 批量删除收货地址
     * @param ids 地址ID数组
     * @return 操作结果
     */
    @Override
    public R deleteAddresses(Integer[] ids) {
        this.deleteBatchIds(Arrays.asList(ids)); // 批量删除
        return R.ok(); // 返回成功
    }

    /**
     * 获取地址列表（带分页和字典转换）
     * @param params 查询参数
     * @param request HTTP请求对象（用于字典转换）
     * @return 分页结果
     */
    @Override
    public R getAddressList(Map<String, Object> params, HttpServletRequest request) {
        // 调用分页查询方法
        PageUtils page = this.queryPage(params);
        // 遍历结果，转换字典字段（如 isdefault_types 转文字）
        List<AddressView> list = (List<AddressView>) page.getList();
        for (AddressView c : list)
            dictionaryService.dictionaryConvert(c, request);
        // 返回分页数据
        return R.ok().put("data", page);
    }

    /**
     * 获取地址详情（关联用户信息）
     * @param id 地址ID
     * @param request HTTP请求对象（用于字典转换）
     * @return 地址详情
     */
    @Override
    public R getAddressDetail(Long id, HttpServletRequest request) {
        // 根据ID查询地址
        AddressEntity address = this.selectById(id);
        if (address != null) {
            // 转换为视图对象
            AddressView view = new AddressView();
            BeanUtils.copyProperties(address, view);

            // 关联查询用户信息
            YonghuEntity yonghu = yonghuService.selectById(address.getYonghuId());
            if (yonghu != null) {
                // 复制用户属性到视图（排除id和createDate字段）
                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createDate"});
                view.setYonghuId(yonghu.getId());
            }
            // 转换字典字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view); // 返回详情
        } else {
            return R.error(511, "查不到数据"); // 返回错误
        }
    }
}