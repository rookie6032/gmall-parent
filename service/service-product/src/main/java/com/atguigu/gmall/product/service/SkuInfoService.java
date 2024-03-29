package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @author lfy
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-06-21 09:01:27
*/
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * 保存skuInfo
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 上架
     * @param skuId
     */
    void upSku(Long skuId);

    /**
     * 下架
     * @param skuId
     */
    void downSku(Long skuId);

    List<Long> getSkuIds();

    BigDecimal getSkuPrice(Long skuId);

    public void updateSkuInfo(SkuInfo skuInfo);

    Goods getGoodsInfoBySkuId(Long skuId);

    CartInfo getCartInfoBySkuId(Long skuId);

    BigDecimal get1010SkuPrice(Long skuId);
}
