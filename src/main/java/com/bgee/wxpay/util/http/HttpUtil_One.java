package com.bgee.wxpay.util.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


import net.sf.json.JSONObject;

public class HttpUtil_One {

	/**
	 * 发送https请求，返回json格式
	 * 
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
		} catch (Exception e) {
		}
		return jsonObject;
	}
	
	/**
	 * 请求服务器，获取服务器上的图片
	 * @param requestUrl	请求的url地址
	 * @param requestMethod	请求的方式 POST/GET
	 * @param outputStr		请求参数
	 * @return	图片的字节流
	 */
	public static byte[] httpsRequestFile(String requestUrl, String requestMethod, String outputStr) {
		byte[]  fileBytes = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			fileBytes =  readInputStream(inputStream);
			// 释放资源
			inputStream = null;
			conn.disconnect();
		} catch (ConnectException ce) {
		} catch (Exception e) {
		}
		return fileBytes;
	}
	
	public static byte[] readInputStream(InputStream inStream) throws Exception{    
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        while( (len=inStream.read(buffer)) != -1 ){    
            outStream.write(buffer, 0, len);    
        }
        inStream.close();    
        return outStream.toByteArray();    
    }

	
	/**
	 * URL编码（utf-8）
	 * 
	 * @param source
	 * @return
	 */
	public static String urlEncodeUTF8(String source) {
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据内容类型判断文件扩展名
	 * 
	 * @param contentType 内容类型
	 * @return
	 */
	public static String getFileExt(String contentType) {
		String fileExt = "";
		if ("image/jpeg".equals(contentType))
			fileExt = ".jpg";
		else if ("audio/mpeg".equals(contentType))
			fileExt = ".mp3";
		else if ("audio/amr".equals(contentType))
			fileExt = ".amr";
		else if ("video/mp4".equals(contentType))
			fileExt = ".mp4";
		else if ("video/mpeg4".equals(contentType))
			fileExt = ".mp4";
		return fileExt;
	}
	
	
	/**
	 * 原始方法发送 post 请求 带参数
	 * @param url 	请求地址
	 * @param map	key 键   value 值       key=value&key=value&key=value 
	 * @return 返回的结果
	 * @throws Exception
	 */
	public static String httpPostRequestOriginal(String url,Map<String,Object> map) {
		
		String line = "";
		String result = "";
		
		URL postUrl;
		try {
			postUrl = new URL(url);
		 
	        // 打开连接
	        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
	      
	        // 设置是否向connection输出，因为这个是post请求，参数要放在
	        // http正文内，因此需要设为true
	        connection.setDoOutput(true);
	        // Read from the connection. Default is true.
	        connection.setDoInput(true);
	        // 默认是 GET方式
			connection.setRequestMethod("POST");
			 
	        // Post 请求不能使用缓存
	        connection.setUseCaches(false);
	        connection.setInstanceFollowRedirects(true);
	        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
	        // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
	        // 进行编码
	        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
	        // 要注意的是connection.getOutputStream会隐含的进行connect。
				connection.connect();
	        DataOutputStream out;
				out = new DataOutputStream(connection.getOutputStream());
	        // The URL-encoded contend
	        // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
	        String content = "";
	        if(map.isEmpty()){
	        	System.out.println("map is null: " + map);
	        	return null;
	        }
	        int i = 0;
	        for(Map.Entry<String, Object> m : map.entrySet()){
	        	i++;
	        	if(i != 1){
	        		content += "&";
	        	}
	        	content += m.getKey();
	        	content += "=";
				content += URLEncoder.encode(m.getValue().toString(),"UTF-8");
	        }
//	        		"api_key=" + URLEncoder.encode(api_key, "UTF-8");
//	        content +="&response="+URLEncoder.encode(response, "UTF-8");
	        System.out.println(content);
	        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
	        out.writeBytes(content);
	
	        out.flush();
	        out.close(); 
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        
	        
	        while ((line = reader.readLine()) != null){
	            System.out.println("line = " + line);
	            result = line;
	        }
	      
	        reader.close();
	        connection.disconnect();
        }catch(IOException e){
        	e.printStackTrace();
        }
		return result;
	}
	
	public static void main(String[] args){
		 Map<String, Object> map = new LinkedHashMap<String, Object>();  
	        map.put("username","zhaokuo");  
	        map.put("password", "123456");  
	        map.put("email", "zhaokuo719@163.com");  
	        map.put("sex", "男");  
	          
	        //第一种 用for循环的方式  
	        for (Map.Entry<String, Object> m :map.entrySet())  {  
	            System.out.println(m.getKey()+"\t"+m.getValue());  
	        }  
	          
	}
}
