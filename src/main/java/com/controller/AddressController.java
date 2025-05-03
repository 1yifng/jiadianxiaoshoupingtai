package com.controller;

import com.entity.AddressEntity;
import com.service.AddressService;
import com.service.DictionaryService;
import com.service.YonghuService;
import com.utils.CommonUtil;
import com.utils.PageUtils;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 收货地址
 * 后端接口
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 保存地址信息
     */
    @RequestMapping("/save")
    public R save(@RequestBody AddressEntity address, HttpServletRequest request) {
        return addressService.saveAddress(address, request);
    }

    /**
     * 修改地址信息
     */
    @RequestMapping("/update")
    public R update(@RequestBody AddressEntity address) {
        return addressService.updateAddress(address);
    }

    /**
     * 删除地址
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        return addressService.deleteAddresses(ids);
    }

    /**
     * 获取地址列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        CommonUtil.checkMap(params);
        return addressService.getAddressList(params, request);
    }

    /**
     * 获取地址详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        return addressService.getAddressDetail(id, request);
    }
}