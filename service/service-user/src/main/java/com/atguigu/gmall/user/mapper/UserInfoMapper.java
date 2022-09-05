package com.atguigu.gmall.user.mapper;


import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author gxf
* @description 针对表【user_info(用户表)】的数据库操作Mapper
* @createDate 2022-07-05 19:48:08
* @Entity com.atguigu.gmall.user.domain.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    UserInfo getUserByLoginNameAndPasswd(@Param("loginName")String loginName, @Param("passwd")String passwd);
}




