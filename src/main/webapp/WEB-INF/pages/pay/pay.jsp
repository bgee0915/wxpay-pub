<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>


<!-- 唤起支付窗口 -->
<script>
	function onBridgeReady() {
		WeixinJSBridge.invoke('getBrandWCPayRequest', {
			"appId" : "${appid}", //公众号名称，由商户传入     
			"timeStamp" : "${timeStamp}", //时间戳，自1970年以来的秒数     
			"nonceStr" : "${nonceStr}", //随机串     
			"package" : "${package}",
			"signType" : "MD5", //微信签名方式:     
			"paySign" : "${paySign}" //微信签名 
		}, function(res) {
			if (res.err_msg.indexOf("ok")!=-1) {
				alert('支付成功');
				window.location.replace("/pay/paySuccess");
			}else{
				alert('支付失败');
				if(res.err_msg == "get_brand_wcpay_request:cancel"){  
					window.location.href="/pay/payError";
	            }else{  
					window.location.href="/pay/payError";
	            }  
			}
		});
	}
	
	function pay() {
		if (typeof WeixinJSBridge == "undefined") {
			if (document.addEventListener) {
				document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
			} else if (document.attachEvent) {
				document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
				document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
			}
		} else {
			onBridgeReady();
		}
	}
	
	//支付
	pay();
</script>


</body>
</html>