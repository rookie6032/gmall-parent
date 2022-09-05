package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.ItemFeignClient;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;
    @Autowired
    SkuFeignClient skuFeignClient;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId")Long skuId, Model model){

        Result<SkuDetailVo> skuDetail = itemFeignClient.getSkuDetail(skuId);
        SkuDetailVo data = skuDetail.getData();
        if(data != null){
            //分类
            model.addAttribute("categoryView",data.getCategoryView());

            //sku信息
            model.addAttribute("skuInfo",data.getSkuInfo());

            //sku价格，现场再查一下
            Result<BigDecimal> price = skuFeignClient.getSkuPrice(skuId);
            model.addAttribute("price",price.getData());

            //spu定义的所有销售属性名和值
            model.addAttribute("spuSaleAttrList",data.getSpuSaleAttrList());

            //valuesSkuJson： {“118|120”：49， “119|120”：50}
            //查出当前sku对应的spu到底有多少sku，
            // 并每个sku的销售属性值组合，按照 值组合为key，skuId为value，存到一个map中。再把这个map转为json给前端
            model.addAttribute("valuesSkuJson",data.getValuesSkuJson());
        }else {
            return "item/error";
        }

        return "item/index";
    }
}
