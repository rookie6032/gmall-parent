package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author gxf
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service
* @createDate 2022-07-11 00:05:23
*/
public interface OrderInfoService extends IService<OrderInfo> {

    void saveDetail(OrderInfo orderInfo, OrderSubmitVo order);

    void updateOrderStatus(Long orderId,
                           Long userId,
                           String orderStatus,
                           String processStatus,
                           String expectStatus);

    OrderInfo getOrderInfoByIdAndUserId(Long id);
}
