package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
* @author lfy
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2022-06-21 09:01:27
* @Entity com.atguigu.gmall.product.domain.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {


    void updateSaleStatus(@Param("skuId") Long skuId,@Param("status") int status);

    List<Long> getSkuIds();


    Goods getGoodsInfoBySkuId(@Param("skuId") Long skuId);

    BigDecimal getSkuPrice(@Param("skuId") Long skuId);
}




