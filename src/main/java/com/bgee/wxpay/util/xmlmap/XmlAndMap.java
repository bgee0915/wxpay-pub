package com.bgee.wxpay.util.xmlmap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.util.StringUtils;

/*

导入
<!-- jdom -->
<dependency>
	<groupId>jdom</groupId>
	<artifactId>jdom</artifactId>
	<version>1.0</version>
</dependency>

*/

/**
 * xml 和 map 相互转换你
 * @author lx
 *
 */
public class XmlAndMap {
	/**
	 * map to  xml
	 * @param params
	 * @return
	 */
	public static String toXml(Map<String, Object> params) {
        StringBuilder xml = new StringBuilder();
        xml.append("<xml>");
        for (Entry<String, Object> entry : params.entrySet()) {
            String key   = entry.getKey();
            Object value = entry.getValue();
            // 略过空值
            if (StringUtils.isEmpty(value)) continue;
            xml.append("<").append(key).append(">")
               .append(entry.getValue())
               .append("</").append(key).append(">");
        }
        xml.append("</xml>");
        return xml.toString();
    }
	
	
	/**
	 * xml to map
	 * @param strxml
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object> toMap(String strxml) throws Exception {
		if(null == strxml || "".equals(strxml)) {
			return null;
		}
		
		Map<String,Object> m = new HashMap<String,Object>();
		InputStream in = new ByteArrayInputStream(strxml.getBytes());
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if(children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}
			m.put(k, v);
		}
		in.close();
		return m;
	}
	
	/**
	 * 获取子结点的xml
	 * @param children
	 * @return String
	 */
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if(!children.isEmpty()) {
			Iterator it = children.iterator();
			while(it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if(!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		
		return sb.toString();
	}
	
}
