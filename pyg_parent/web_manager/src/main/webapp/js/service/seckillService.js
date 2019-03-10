app.service("seckillService",function($http){
    this.search = function(page,rows,searchEntity){
        return $http.post("../seckill/search.do?page="+page+"&rows="+rows,searchEntity);
    }

    this.seckillUpdateStatus = function(ids,status){
        return $http.get('../seckill/updateStatus.do?ids='+ids+"&status="+status);
    }
});