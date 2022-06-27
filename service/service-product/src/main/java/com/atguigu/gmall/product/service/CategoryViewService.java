package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.dto.CategoryViewDo;

import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author gxf
* @description 针对表【category_view】的数据库操作Service
* @createDate 2022-06-26 20:34:57
*/
public interface CategoryViewService extends IService<CategoryViewDo> {

    CategoryViewDo getViewByC3Id(Long c3id);
}
