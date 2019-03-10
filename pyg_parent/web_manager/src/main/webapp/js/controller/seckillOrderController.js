app.controller("seckillOrderController",function($scope,$controller,$http,seckillOrderService){

    $scope.searchEntity={};

    $controller('baseController',{$scope:$scope});
    // 假设定义一个查询的实体：searchEntity
    $scope.search = function(page,rows){
        // 向后台发送请求获取数据:
        seckillOrderService.search(page,rows,$scope.searchEntity).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }

    $scope.seckillUpdateStatus = function(status){
        seckillService.seckillUpdateStatus($scope.selectIds,status).success(function(response){
            if(response.success){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }
});