package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.AddressEntity;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.utils.R;
import org.springframework.lang.Nullable;
import java.util.List;

/**
 * 收货地址 服务类
 */
public interface AddressService extends IService<AddressEntity> {

    /**
     * @param params 查询参数
     * @return 带分页的查询出来的数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存地址信息
     * @param address 地址实体
     * @param request HttpServletRequest对象
     * @return 操作结果
     */
    R saveAddress(AddressEntity address, HttpServletRequest request);

    /**
     * 修改地址信息
     * @param address 地址实体
     * @return 操作结果
     */
    R updateAddress(AddressEntity address) ;

    /**
     * 删除地址
     * @param ids 地址ID数组
     * @return 操作结果
     */
    R deleteAddresses(Integer[] ids);

    /**
     * 前端获取地址列表
     * @param params 查询参数
     * @param request HttpServletRequest对象
     * @return 操作结果
     */
    R getAddressList(Map<String, Object> params, HttpServletRequest request);

    /**
     * 前端获取地址详情
     * @param id 地址ID
     * @param request HttpServletRequest对象
     * @return 操作结果
     */
    R getAddressDetail(Long id, HttpServletRequest request);
}