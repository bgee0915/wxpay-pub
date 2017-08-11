===========================================================================================

有问题请私信   
	bgee0915@outlook.com
	
github:  
	https://github.com/bgee0915

===========================================================================================

0：  注意事项

	# 本项目不保证能够顺利启动运行, 只负责提供核心部分代码以供参考, 核心代码是没问题的        ゞ(o｀Д´o)来打架啊！
	# 支付方式  -- 微信公众号支付 
	# 使用的是 ssm 框架
	# 未嵌入业务代码
	# PayConstants 中有未设置的常量
	# 所以请阅读   [微信支付.docx]
	# 支付动作发生后请务必结合业务考虑   [交易时金额的数值前后一致性] && [并发性] && ....
	# 其它的想到后再补充 

===========================================================================================

1：用到的工具类 需要在pom中导入的包 (本项目已经导入了)

	<!-- json -->
	<dependency>
		<groupId>luckystar-oth</groupId>
		<artifactId>json-lib</artifactId>
		<version>2.4</version>
		<classifier>jdk15</classifier>
	</dependency>
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>fastjson</artifactId>
		<version>1.2.8</version>
	</dependency>
	
	<!-- httpclient -->
	<dependency>
		<groupId>org.apache.httpcomponents</groupId>
		<artifactId>httpclient</artifactId>
		<version>4.1.1</version>
	</dependency>
	
	<!-- jdom -->
	<dependency>
		<groupId>jdom</groupId>
		<artifactId>jdom</artifactId>
		<version>1.0</version>
	</dependency>

===========================================================================================

2：流程

	wxpay/webapp/WEB-INF/pages/pay/prepay.jsp       <巴疼>付钱</巴疼>
		
	↓     
		
	PayController.java	---  prepay()
		
	↓
					
	微信登录授权  		
							
	↓	[返回 code]
							
	PayController.java  ---  topay()
		
	↓	访问统一下单接口   ->  获取sign  ->  获取 prepayid  ->  获取支付 sign
							
	wxpay/webapp/WEB-INF/pages/pay/pay.jsp  
	
	↓
	
	调用微信的  jsapi
	
	↓
	
	PayController.java  ---  notifypay()
	
	↓
	
	返回结果给微信

===========================================================================================
