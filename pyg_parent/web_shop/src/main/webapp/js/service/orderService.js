//服务层
app.service('orderService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../order/findAll.do');
	}
    //发货
    this.sendGood=function(orderId){
        return $http.get("../order/sendGood.do?orderId="+orderId);
    }
});
