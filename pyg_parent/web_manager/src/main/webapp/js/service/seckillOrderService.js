app.service("seckillOrderService",function($http){

    this.search = function(page,rows,searchEntity){
        return $http.post("../seckill/searchOrderList.do?page="+page+"&rows="+rows,searchEntity);
    }

    this.seckillUpdateStatus = function(ids,status){
        return $http.get('../seckill/updateStatus.do?ids='+ids+"&status="+status);
    }
});