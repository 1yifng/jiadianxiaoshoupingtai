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
 * 收货地址 服务实现类
 */
@Service("addressService")
@Transactional
public class AddressServiceImpl extends ServiceImpl<AddressDao, AddressEntity> implements AddressService {

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private YonghuService yonghuService;

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        Page<AddressView> page = new Query<AddressView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }

    @Override
    public R saveAddress(AddressEntity address, HttpServletRequest request) {
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("用户".equals(role))
            address.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        Wrapper<AddressEntity> queryWrapper = new EntityWrapper<AddressEntity>()
                .eq("yonghu_id", address.getYonghuId())
                .eq("address_name", address.getAddressName())
                .eq("address_phone", address.getAddressPhone())
                .eq("address_dizhi", address.getAddressDizhi())
                .eq("isdefault_types", address.getIsdefaultTypes());

        AddressEntity addressEntity = this.selectOne(queryWrapper);
        if(addressEntity == null){
            address.setInsertTime(new Date());
            address.setCreateTime(new Date());

            Integer isdefaultTypes = address.getIsdefaultTypes();
            if(isdefaultTypes == 2 ){
                List<AddressEntity> addressEntitys = this.selectList(new EntityWrapper<AddressEntity>().eq("isdefault_types",2));
                if(addressEntitys != null && addressEntitys.size()>0){
                    for(AddressEntity a:addressEntitys)
                        a.setIsdefaultTypes(1);
                    this.updateBatchById(addressEntitys);
                }
            }
            this.insert(address);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    @Override
    public R updateAddress(AddressEntity address) {
        address.setUpdateTime(new Date());

        Integer isdefaultTypes = address.getIsdefaultTypes();
        if(isdefaultTypes == 2 ){
            List<AddressEntity> addressEntitys = this.selectList(new EntityWrapper<AddressEntity>().eq("isdefault_types",2));
            if(addressEntitys != null && addressEntitys.size()>0){
                for(AddressEntity a:addressEntitys)
                    a.setIsdefaultTypes(1);
                this.updateBatchById(addressEntitys);
            }
        }
        this.updateById(address);
        return R.ok();
    }

    @Override
    public R deleteAddresses(Integer[] ids) {
        this.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    @Override
    public R getAddressList(Map<String, Object> params, HttpServletRequest request) {
        PageUtils page = this.queryPage(params);

        List<AddressView> list = (List<AddressView>)page.getList();
        for(AddressView c:list)
            dictionaryService.dictionaryConvert(c, request);

        return R.ok().put("data", page);
    }

    @Override
    public R getAddressDetail(Long id, HttpServletRequest request) {
        AddressEntity address = this.selectById(id);
        if(address != null){
            AddressView view = new AddressView();
            BeanUtils.copyProperties(address, view);

            YonghuEntity yonghu = yonghuService.selectById(address.getYonghuId());
            if(yonghu != null){
                BeanUtils.copyProperties(yonghu, view, new String[]{ "id", "createDate"});
                view.setYonghuId(yonghu.getId());
            }
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }
    }
}