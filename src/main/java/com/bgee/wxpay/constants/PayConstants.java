package com.bgee.wxpay.constants;

/**
 * 支付常量
 * @author lx
 *
 */
public class PayConstants {
	
	/** 微信公众号 appid  */
	public static final String WX_APPID = "";
	
	/** 微信公众号  secret  */
	public static final String WX_SECRET = "";
	
	/** 微信商户号 partner    */
	public static final String WX_PARTNER = "";
	
	/** 微信商户号的apiKey  partnerkey */
	public static final String WX_PARTNERKEY = "";
	
	/** 微信回调本地的支付接口 */
	public static final String WX_CALLBACK_LOCAL_URL = "http://www.本项目绑定的域名.com/pay/topay";
	
	/** 微信支付后通知本地接口 */
	public static final String WX_NOTIFY_LOCAL_URL = "http://www.本项目绑定的域名.com/pay/notifypay";
	
	/** 微信登录授权接口 */
	public static final String WX_OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
	
	/** 微信获取OPENID 接口  */
	public static final String WX_OPENID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
	
	/** 微信统一支付接口  */
	public static final String WX_PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	
	
	
}
