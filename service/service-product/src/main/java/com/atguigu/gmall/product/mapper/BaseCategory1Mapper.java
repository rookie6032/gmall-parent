package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.vo.CategoryVo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author gxf
* @description 针对表【base_category1(一级分类表)】的数据库操作Mapper
* @createDate 2022-06-27 00:26:34
* @Entity com.atguigu.gmall.product.domain.BaseCategory1
*/
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {
    List<CategoryVo> getCategorys();

}




