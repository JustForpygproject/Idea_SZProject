app.service("uploadService",function($http){
	
	this.uploadFile = function(){
		// 向后台传递数据:
		var formData = new FormData();
		// 向formData中添加数据:
		formData.append("file",file.files[0]);
		
		return $http({
			method:'post',
			url:'../upload/uploadFile.do',
			data:formData,
			headers:{'Content-Type':undefined} ,// 相当于改变上传请求的头信息, 如果不加这个默认是application/text, 如果加上写个相当于是application/json
			transformRequest: angular.identity//使用angularjs序列化上传文件
		});
	}
	
});