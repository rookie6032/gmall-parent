package com.atguigu.gmall.pay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/payment")
public class AlipaySuccessController {

    @GetMapping("/alipay/success")
    public String alipaySuccess(){

        return "redirect:http://payment.gmall.com/payment/success.html";
    }

}
