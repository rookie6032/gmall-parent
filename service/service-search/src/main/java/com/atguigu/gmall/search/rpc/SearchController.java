package com.atguigu.gmall.search.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequestMapping("/rpc/inner/search")
@RestController
public class SearchController {

    @Autowired
    GoodsSearchService goodsSearchService;

    @PostMapping("/goods")
    public Result<SearchResponseVo> search(@RequestBody SearchParam param, HttpServletRequest request){
        //检索
        SearchResponseVo vo = goodsSearchService.search(param);
        return Result.ok(vo);
    }
    @PostMapping("/up")
    public Result upGoods(@RequestBody Goods goods){
        goodsSearchService.upGoods(goods);
        return Result.ok();
    }

    @GetMapping("/down/{skuId}")
    public  Result downGoods(@PathVariable("skuId") Long skuId){
        goodsSearchService.downGoods(skuId);
        return  Result.ok();
    }

    /**
     * 增加商品热度分
     * @return
     */
    @GetMapping("/incr/hotscore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId,
                               @RequestParam("score") Long score){

        goodsSearchService.incrHotScore(skuId,score);
        return Result.ok();
    }
}
