 //控制层 
app.controller('orderController' ,function($scope,$controller,$location,orderService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		orderService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    


	// 显示状态
	$scope.status = ["未审核","审核通过","审核未通过","关闭"];
	
	$scope.itemCatList = [];
	// 显示分类:
	$scope.findItemCatList = function(){
		itemCatService.findAll().success(function(response){
			//遍历分类表所有数据
			for(var i=0;i<response.length;i++){
				//response[i] 获取分类数据的某一条
				//response[i].id获取分类数据某一条的id属性值
				//$scope.itemCatList[4] = 网络原创
				//$scope.itemCatList[1] = 图书、音像、电子书刊
				//itemCatList[分类id] = 分类名称
				//{{itemCatList[entity.category1Id]}} 相当于itemCatList[分类id] 通过分类索引号直接取分类的名称
				$scope.itemCatList[response[i].id] = response[i].name;
			}
		});
	}

//发货的方法
    $scope.sendGood=function(orderId){
        alert(orderId);
        orderService.sendGood(orderId).success(
            function(response){
                if(response.success){
                    location="http://localhost:8082/admin/order.html";
                }else{
                    alert(response.message);
                }

            }
        );
    }

});	
