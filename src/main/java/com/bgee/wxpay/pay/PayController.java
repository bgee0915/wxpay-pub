package com.bgee.wxpay.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bgee.wxpay.constants.PayConstants;
import com.bgee.wxpay.util.xmlmap.XmlAndMap;

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
//					.append("&money=").append("参数：小钱钱")
//					.append("&userId=").append("参数：用户ID")
//					.append("&orderNo=").append("参数：订单号")
//					.append("&describe=").append("参数：商品描述")
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
		
		String paramXml = XmlAndMap.toXml(paramMap);						// paramMap -> paramXml
		String prepayid = payService.getPrepayId(paramXml);					// paramXml -> 预支付交易会话标识 prepare_id
		
		SortedMap<String, Object> finalMap = new TreeMap<String, Object>();
		finalMap.put("appId", PayConstants.WX_APPID);  
		finalMap.put("timeStamp", System.currentTimeMillis() / 1000);  
		finalMap.put("nonceStr", paramMap.get("nonce_str"));  
		finalMap.put("package", "prepay_id=" + prepayid);  
		finalMap.put("signType", "MD5");
		String finalsign = payService.createSign(finalMap);					// 支付签名
		
		
		request.setAttribute("appid", PayConstants.WX_APPID);
		request.setAttribute("timeStamp", finalMap.get("timeStamp"));
		request.setAttribute("nonceStr", finalMap.get("nonceStr"));
		request.setAttribute("package", finalMap.get("package"));
		request.setAttribute("paySign", finalsign);
		request.setAttribute("totalFee", money);
		
		return "pay/pay";
	}
	
	/**
	 * [支付结果通知]
	 * @param request
	 * @param response
	 */
	@RequestMapping("/notifypay")
	public void notifypay(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder result = new StringBuilder();
		try {
			String line = null;
			BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
			while((line = br.readLine())!=null){
				result.append(line);
			}
			
//			返回结果
			Map<String,Object> map = XmlAndMap.toMap(result.toString());
			
			Object result_code = map.get("result_code");
			Object return_code = map.get("return_code");
			Object attachObj = map.get("attach");			//附加数据
			Object totalFeeObj = map.get("total_fee");		//消费总额
			Object outTradeNo = map.get("out_trade_no");	//商户订单号
			Object transId = map.get("transaction_id");		//微信支付订单号
			
//			微信处理 返回状态码(return_code) 和 业务处理结果 (result_code)
			if("SUCCESS".equals(result_code.toString()) && "SUCCESS".equals(return_code.toString())) {

				
//				支付成功后要返回xml形式success给微信,否则会反反复复的通知8次  30min/次
				response.getWriter().write(payService.returnResult(true));
			}
			
//			返回失败信息给微信
			response.getWriter().write(payService.returnResult(false));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
