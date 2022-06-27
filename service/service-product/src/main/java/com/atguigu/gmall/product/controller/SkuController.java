package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/product")
@RestController
public class SkuController {

    @Autowired
    private SkuInfoService skuInfoService;

    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @GetMapping("/list/{page}/{limit}")
    public  Result list(@PathVariable("page")Long page,
                        @PathVariable("limit")Long limit){
        Page<SkuInfo> page1 = new Page<>(page,limit);
        Page<SkuInfo> result = skuInfoService.page(page1);
        return Result.ok(result);
    }

    /**
     * 上架
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuInfoService.upSku(skuId);
        return Result.ok();
    }

    /**
     * 下架
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.downSku(skuId);
        return Result.ok();
    }
}
