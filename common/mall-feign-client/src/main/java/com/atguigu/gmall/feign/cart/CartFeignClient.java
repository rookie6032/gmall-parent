package com.atguigu.gmall.feign.cart;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/rpc/inner/cart")
@FeignClient("service-cart")
public interface CartFeignClient {
    @GetMapping("/add/{skuId}")
    Result<AddSuccessVo> addSkuToCart(@PathVariable("skuId") Long skuId,
                                      @RequestParam("num") Integer num);

    /**
     * 删除选中的所有商品
     * @return
     */
    @GetMapping("/delete/checked")
    public Result deleteChecked();


    @GetMapping("/checked/items")
    Result<List<CartInfo>> getCheckedCartItems();

}
