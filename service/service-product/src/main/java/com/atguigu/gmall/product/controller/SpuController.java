package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuPosterService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.impl.SpuInfoServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class SpuController {
    @Autowired
    private SpuInfoServiceImpl spuInfoService;

    @Autowired
    private SpuImageService spuImageService;

    @Autowired
    private SpuSaleAttrService spuSaleAttrService;

    @GetMapping("/{page}/{limit}")
    public Result getSpuByCategoryId(@PathVariable("page") Long page,
                                     @PathVariable("limit") Long limit,
                                     @RequestParam ("category3Id") Long category3Id){
        Page<SpuInfo> page1 = new Page<>(page,limit);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_Id",category3Id);
        Page<SpuInfo> result = spuInfoService.page(page1, wrapper);
        return Result.ok(result);
    }

    @PostMapping("/saveSpuInfo")
    public Result saveSpu(@RequestBody SpuInfo spuInfo){
        //spuinfo的大保存
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId") Long spuId){
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> list = spuImageService.list(wrapper);
        return Result.ok(list);
    }
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId")Long spuId){

        //获取spu的所有销售属性名和值
        List<SpuSaleAttr> saleAttrs = spuSaleAttrService.getSpuSaleAttrList(spuId);

        return Result.ok(saleAttrs);
    }
}
