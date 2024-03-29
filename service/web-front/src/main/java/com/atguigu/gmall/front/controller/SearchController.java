package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 处理检索请求的
 */
@Controller
public class SearchController {


    @Autowired
    SearchFeignClient searchFeignClient;

    /**
     * 浏览器<-->网关<-->前端<-->(拿到一些检索条件) <--> 检索服务 -- 去es检索
     * |          |
     * --得到结果---|
     * <p>
     * 跳到商品列表页
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        String uri = request.getRequestURI();
        //+后面的参数
        String queryString = request.getQueryString();

        //TODO 远程调用检索服务去检索
        Result<Map<String, Object>> search = searchFeignClient.search(param);

        Map<String, Object> data = search.getData();

        //远程得到的map所有数据全部交给页面
        model.addAllAttributes(data);


//        model.addAttribute("searchParam",data.getSearchParam());
//        model.addAttribute("trademarkParam",data.getTrademarkParam());
//        model.addAttribute("propsParamList",data.getPropsParamList());
//        model.addAttribute("trademarkList",data); //品牌的二次检索区展示的内容
//        model.addAttribute("attrsList",null);
//        model.addAttribute("orderMap",null);
//        model.addAttribute("goodsList",null);
//        model.addAttribute("pageNo",null);
//        model.addAttribute("totalPages",null);
//        model.addAttribute("urlParam",null);


        return "list/index";
    }
}