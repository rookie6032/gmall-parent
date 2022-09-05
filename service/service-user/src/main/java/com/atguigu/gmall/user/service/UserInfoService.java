package com.atguigu.gmall.user.service;


import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessRespVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author gxf
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-07-05 19:48:08
*/
public interface UserInfoService extends IService<UserInfo> {

    LoginSuccessRespVo login(UserInfo userInfo, String ipAddress);

    void logout(String token);
}
