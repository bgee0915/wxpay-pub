package com.bgee.wxpay.pay;

import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bgee.wxpay.constants.PayConstants;
import com.bgee.wxpay.util.xmlmap.XmlAndMap;
import com.luckystar.constant.ProductConstant;
import com.luckystar.util.Sha1Util;


/**
 * 微信支付 controller  
 * @author lx
 *
 */

@Controller
@RequestMapping("/pay")
public class PayController {
	
	@Resource
	private PayService payService;
	
	/**
	 * [预支付]     
	 * 访问微信登录授权接口, 并设置微信回调本地的支付接口, 使下一步 [支付] 时能够拿到 code 换取 OpenId 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/prepay")
	public Object prepay(HttpServletRequest request , HttpServletResponse response) {
		
//		微信回调本项目的接口地址
		StringBuffer callbackurl = new StringBuffer();
		callbackurl.append(PayConstants.WX_CALLBACK_LOCAL_URL);
		
//		================================================================================
//		如果需要携带参数    --  解开注释   携带自定义参数即可
//		callbackurl.append("?exampleParam=").append(request.getParameter("exampleParam"))
//					.append("&userId=").append("参数：用户ID")
//					.append("&orderNo=").append("参数：订单号")
//					.append("&describe=").append("参数：商品描述")
//					.append("&money=").append("参数：小钱钱")
//		================================================================================
		
//		微信授权登录地址 
		String oauth2url = payService.getOAuth2Url(callbackurl.toString());
		
		return URLEncoder.encode(oauth2url);
	}
	
	
	/**
	 * [支付]
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/topay")
	public String topay(HttpServletRequest request, HttpServletResponse response) {
		String body = "";
		String attach = "";
		String outTradeNo = "";
		double money = 0.0d;
		
//		调用 授权登录接口 返回的 code , 用来换取  openId
		String code = request.getParameter("code");
		
//		openid
		String openId = payService.getOpenId(code);
		
//		随机数
		String randoms = Math.random()*10 + System.currentTimeMillis() + Math.random()*10 + "";
		
//		参数组装
		SortedMap<String, Object> paramMap = new TreeMap<String, Object>();
		
		paramMap.put("appid", PayConstants.WX_APPID);						// appid
		paramMap.put("mch_id", PayConstants.WX_PARTNER);  					// secret 
		paramMap.put("nonce_str", randoms);  								// 随机数
		paramMap.put("body", body);  										// 商品描述		
		paramMap.put("attach", attach);  									// 附加数据
		paramMap.put("out_trade_no", outTradeNo);  							// 订单号
		paramMap.put("total_fee", money+"");								// 金额	(单位：分)
		paramMap.put("spbill_create_ip", request.getRemoteAddr());  		// IP地址
		paramMap.put("notify_url", PayConstants.WX_NOTIFY_LOCAL_URL);  		// 回调通知地址
		paramMap.put("trade_type", "JSAPI");  								// 交易类型
		paramMap.put("openid", openId);  									// 前面 通过 code 取到的 openid (用户标识)
		paramMap.put("sign", payService.createSign(paramMap));				// 签名
		
		String paramXml = XmlAndMap.toXml(paramMap);
		String prepayid = payService.getPrepayId(paramXml);
		
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String appid2 = ProductConstant.WX_PAY_APPID;
		String timestamp = Sha1Util.getTimeStamp();
		String nonceStr2 = packageParams.get("nonce_str");
		String prepay_id2 = "prepay_id="+prepay_id;
		String packages = prepay_id2;
		finalpackage.put("appId", appid2);  
		finalpackage.put("timeStamp", timestamp);  
		finalpackage.put("nonceStr", nonceStr2);  
		finalpackage.put("package", packages);  
		finalpackage.put("signType", "MD5");
		String finalsign = reqHandler.createSign(finalpackage);
		request.setAttribute("appid", appid2);
		request.setAttribute("timeStamp", timestamp);
		request.setAttribute("nonceStr", nonceStr2);
		request.setAttribute("packageValue", packages);
		request.setAttribute("sign", finalsign);
		
		request.setAttribute("totalFee", total_fee);
		request.setAttribute("userId", userId);
		request.setAttribute("recordString", recordString);
		
		return null;
	}
	
	/**
	 * [通知]
	 * @param request
	 * @param response
	 */
	@RequestMapping("/notifypay")
	public void notifypay(HttpServletRequest request, HttpServletResponse response) {
		
	}
}
