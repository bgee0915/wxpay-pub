<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
	<body>
		<button onclick="prepay();">支付</button>
	</body>
	
	<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
	<script type="text/javascript">
	
		// * 不建议从前端传递 money 到后台. money 最好是直接从 后台数据库取出
		// * 微信公众号支付方式  只有 微信浏览器才能唤起支付
		function prepay(){
			$.ajax({
				type	:	"POST",
				url		:	"/pay/prepay",
				async	:	false,
				data	:	{},
				success	:	function(result){
					console.log(result);
					if(result){
						e.preventDefault();
						window.location.href=result;
					}
					return false;
				}
			});
		}
	</script>
</html>