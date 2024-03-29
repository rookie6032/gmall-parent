package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessRespVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class LoginController {

    UserInfoService userInfoService;

    public LoginController(UserInfoService userInfoService){
        this.userInfoService = userInfoService;
    }

    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo,
                        HttpServletRequest request){
        String ipAddress = IpUtil.getIpAddress(request);
        LoginSuccessRespVo vo = userInfoService.login(userInfo,ipAddress);
        return Result.ok(vo);
    }

    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token){
        userInfoService.logout(token);
        return Result.ok();
    }
}
