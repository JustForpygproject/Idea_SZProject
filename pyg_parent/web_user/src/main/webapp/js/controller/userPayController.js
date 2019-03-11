app.controller('userPayController' ,function($scope ,$location,userPayService){

    // 获取url传递参数
    var orderIdStr = window.location.search.substring(1);

	orderId = orderIdStr.split('=')
	/*alert(orderIdStr)*/
	$scope.createNative=function(){
        userPayService.createNative(orderId[1]).success(
			function(response){
				
				//显示订单号和金额
				$scope.money = (response.total_fee/100).toFixed(2);
				$scope.out_trade_no=response.out_trade_no;
				
				//生成二维码
				 var qr=new QRious({
					    element:document.getElementById('qrious'),
						size:250,
						value:response.code_url,
						level:'H'
			     });
				 
				 queryPayStatus();//调用查询
				
			}	
		);	
	}
	
	//调用查询
	queryPayStatus=function(){
        userPayService.queryPayStatus($scope.out_trade_no).success(
			function(response){
				if(response.success){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if(response.message=='二维码超时'){
						$scope.createNative();//重新生成二维码
					}else{
						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
	
});