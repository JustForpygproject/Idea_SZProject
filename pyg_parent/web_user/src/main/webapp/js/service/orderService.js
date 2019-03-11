//服务层
app.service('orderService',function($http) {
    //购物车列表
    this.findOrderList = function () {
        return $http.get('order/getOrderList.do');
    }
});