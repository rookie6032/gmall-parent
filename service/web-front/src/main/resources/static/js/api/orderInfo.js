var orderInfo = {

    api_name: '/api/com.atguigu.gmall.order',

    getOrderDetail(orderDetailId) {
        return request({
            url: this.api_name + `/auth/getOrderDetail/${orderDetailId}`,
            method: 'get'
        })
    }
}
