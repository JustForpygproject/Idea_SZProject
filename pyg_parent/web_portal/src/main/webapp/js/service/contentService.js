app.service("contentService",function($http){
	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}
    this.findItemCatList = function(){
		return $http.get("itemcat/findItemCatList.do?");
	}
});