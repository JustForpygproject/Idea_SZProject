//购物车控制层
app.controller('cartController',function($scope,cartService){

    //显示当前登陆者用户名
    $scope.showName = function () {
        cartService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

    //修改地址别名
    $scope.update = function (param) {
        alert(param);
        return $scope.entity.alias =param;

    }

	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				$scope.totalValue= cartService.sum($scope.cartList);
			}
		);
	}
	
	//数量加减
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				if(response.success){//如果成功
					$scope.findCartList();//刷新列表
				}else{
					alert(response.message);
				}				
			}		
		);		
	}
	

	
	//获取当前用户的地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				for(var i=0;i<$scope.addressList.length;i++){
					if($scope.addressList[i].isDefault=='1'){
						$scope.address=$scope.addressList[i];
						break;
					}					
				}
				
			}
		);		
	}





    // 保存地址的方法:
    $scope.save = function(){
        // 区分是保存还是修改
        var object;
        if($scope.entity.id != null){
            // 更新
            object = cartService.update($scope.entity);
        }else{
            // 保存
            object = cartService.save($scope.entity);
        }
        object.success(function(response){
            // {success:true,message:xxx}
            // 判断保存是否成功:
            if(response.success==true){
                // 保存成功
                alert(response.message);
                location.href="home-setting-address.html";
                // $scope.reloadList();
            }else{
                // 保存失败
                alert(response.message);
            }
        });
    }


    // 查询一个地址
    $scope.findById = function(id){
        cartService.findById(id).success(function(response){
            // {id:xx,name:yy,firstChar:zz}
            $scope.entity = response;
        });
    }

    // 删除地址:
    $scope.dele = function(id){
        cartService.dele(id).success(function(response){
            // 判断保存是否成功:
            if(response.success==true){
                // 删除成功
                alert(response.message);
                // $scope.reloadList();//刷新列表
                location.href="home-setting-address.html";
            }else{
                // 保存失败
                alert(response.message);
            }
        });
    }


    // 查询一级分类列表:
    $scope.selectItemCat1List = function(){
        cartService.findByParentId(0).success(function(response){
            $scope.provinceList = response;
        });
    }


    // 查询二级分类列表:
    $scope.$watch("entity.provinceId",function(newValue,oldValue){
        cartService.findByParentId2(newValue).success(function(response){
            $scope.cityList = response;
        });
    });

    // 查询三级分类列表:
    $scope.$watch("entity.cityId",function(newValue,oldValue){
        cartService.findByParentId3(newValue).success(function(response){
            $scope.areaList = response;
        });
    });

    // 设置默认地址
    $scope.updateStatus = function(id){
        cartService.updateStatus(id).success(function(response){
            if(response.success){
                alert(response.message);
                $scope.reloadList();//刷新列表
            }else{
                alert(response.message);
            }
        });
    }

	
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;		
	}
	
	//判断某地址对象是不是当前选择的地址
	$scope.isSeletedAddress=function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}		
	}
	
	$scope.order={paymentType:'1'};//订单对象
	
	//选择支付类型
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	
	//保存订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		
		cartService.submitOrder( $scope.order ).success(
			function(response){
				//alert(response.message);
				if(response.success){
					//页面跳转
					if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
						location.href="pay.html";
					}else{//如果货到付款，跳转到提示页面
						location.href="paysuccess.html";
					}
					
				}else{
					alert(response.message);	//也可以跳转到提示页面				
				}
				
			}				
		);		
	}
	
});