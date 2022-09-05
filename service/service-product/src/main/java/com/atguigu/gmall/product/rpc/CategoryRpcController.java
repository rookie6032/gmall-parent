package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/rpc/inner/product")
@RestController
public class CategoryRpcController {
    @Autowired
    CategoryBizService categoryBizService;

    Map<String,List<CategoryVo>> categoryCache = new ConcurrentHashMap<>();

    @GetMapping("/categorys/all")
    public Result<List<CategoryVo>> getCategorys(){

        //查询所有三级分类
        List<CategoryVo> vos =   categoryBizService.getCategorys();
        return Result.ok(vos);
    }
}
