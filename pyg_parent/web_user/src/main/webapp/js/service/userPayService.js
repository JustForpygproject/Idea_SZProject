app.service('userPayService',function($http){
	//本地支付

	this.createNative=function(orderIdStr){
		return $http.get('userPay/createNative.do?orderIdStr='+orderIdStr);
	}
	
	//查询支付状态
	this.queryPayStatus=function(out_trade_no){
		return $http.get('userPay/queryPayStatus.do?out_trade_no='+out_trade_no);
	}
});