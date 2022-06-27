package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.CategoryViewService;
import com.atguigu.gmall.product.mapper.CategoryViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author gxf
* @description 针对表【category_view】的数据库操作Service实现
* @createDate 2022-06-26 20:34:57
*/
@Service
public class CategoryViewServiceImpl extends ServiceImpl<CategoryViewMapper, CategoryViewDo>
    implements CategoryViewService{

    @Autowired
    CategoryViewMapper viewMapper;
    @Override
    public CategoryViewDo getViewByC3Id(Long c3id) {
        QueryWrapper<CategoryViewDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("c3id",c3id);
        CategoryViewDo viewDo = viewMapper.selectOne(queryWrapper);
        return viewDo;
    }
}




