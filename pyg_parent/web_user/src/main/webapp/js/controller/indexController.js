//首页控制器
app.controller('indexController', function ($scope, $controller, loginService, orderSearchService) {

    // AngularJS中的继承:伪继承
    $controller('baseController', {$scope: $scope});

    //订单支付状态
    $scope.status = ["未付款", "未付款", "已付款", "等待发货", "已发货", "交易成功", "交易关闭", "待评价"]
    //退款/退货
    $scope.out = ["", "", "退款/退货", "退款/退货", "退款/退货", "退款/退货", "退款/退货", "退款/退货"]
    //支付按钮提示
    $scope.payStatus = ["立即付款", "立即付款", "提醒发货", "提醒发货", "确认收货", "交易成功", "交易成功", "去评价"]

    //显示当前登陆者用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

    //查询订单集合
    $scope.findOrderList = function () {
        orderSearchService.findOrderList().success(
            function (response) {
                $scope.orderList = response;
            }
        )
    }

    //根据订单号取消订单,+取消确认
    $scope.cancelOrder = function (orderIdStr) {
        //利用对话框返回的值 （true 或者 false）
        if (confirm("你确定要取消此订单吗？")) {
            //alert("点击了确定");
            orderSearchService.cancelOrder(orderIdStr).success(
                function (response) {
                    if (response.message=='1'){
                        window.location.reload();
                    } else {
                        alert(response.message)
                    }
                }
            )
        }
        else {
            //alert("点击了取消");
        }
    }

    // 分页查询
    $scope.search = function (page, rows) {
        // 向后台发送请求获取数据:
        orderSearchService.search(page, rows, $scope.status).success(function (response) {
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }

    //根据订单id查询订单,并转到支付页面
    $scope.goToPayPage = function (orderIdStr) {
        // 向后台发送请求获取数据:
        orderSearchService.goToPayPage(orderIdStr).success(
            function (response) {
                //alert(response.message);
                if (response.success) {
                    //页面跳转
                    if (response.message == '1') {//如果是微信支付，跳转到支付页面
                        location.href = "userPay.html?orderIdStr="+orderIdStr;
                    }
                    if (response.message == '2') {//如果货到付款，跳转到提示页面
                        location.href = "paysuccess.html";
                    }
                } else {
                    alert(response.message);	//也可以跳转到提示页面
                    window.location.reload();
                }
            });
    }


    //跳转到静态页面
    $scope.openCartPage = function () {
        window.open("http://localhost:8080/cart.html");
    }


});