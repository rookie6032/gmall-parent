package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author gxf
* @description 针对表【base_category1(一级分类表)】的数据库操作Service
* @createDate 2022-06-27 00:26:34
*/
public interface BaseCategory1Service extends IService<BaseCategory1> {

    List<BaseCategory2> getCategory2(Long category1Id);
}
