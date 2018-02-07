package com.test.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * 该类首先会解析web.xml文件，然后根据url的信息，拿到相应的servlet并存储在容器中
 */
public class ServletContainer {

	public static List<ServletMapping> servletMappingList = new ArrayList<ServletMapping>();
	private static String DEFAULT_WEBXML_LOCATION = "MyTomcat\\WebRoot\\WEB-INF\\web.xml";
	
	/*
	 * 解析web.xml并将servlet实体关系放入servletMappingList中
	 */
	private static void scanXMLConfig(){
		 try {
			 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
			 DocumentBuilder db = dbf.newDocumentBuilder();
			 //使用绝对路径寻找并加载web.xml文件
			 Document document = db.parse(getFile());
			 Element root = document.getDocumentElement();  
	         NodeList xmlNodes = root.getChildNodes();  
	         for (int i = 0; i < xmlNodes.getLength(); i++) {  
	        	 Node config = xmlNodes.item(i);  
	        	 if (null != config && config.getNodeType() == Node.ELEMENT_NODE) {
	        		 //将遍历到的servlet分装为ServletMapping对象
	        		 String outerName = config.getNodeName();
	        		 ServletMapping servlet = null;
	        		 if("servlet".equals(outerName)){
	        			 servlet = new ServletMapping();
	        			 NodeList childNodes = config.getChildNodes();  
	        			 for (int j = 0; j < childNodes.getLength(); j++) {  
	                         Node node = childNodes.item(j);  
	                         if (null != node && node.getNodeType() == Node.ELEMENT_NODE) {  
	                             String innerName = node.getNodeName();  
	                             String textContent = node.getTextContent();  
	                             if ("servlet-name".equals(innerName)) {  
	                            	 servlet.setServletName(textContent);
	                             } else if ("servlet-class".equals(innerName)) {  
	                            	 servlet.setClazz(textContent);
	                             }  
	                         } 
	                         servletMappingList.add(servlet);
	                     }
	        		 }else if("servlet-mapping".equals(outerName)){
	        			 NodeList childNodes = config.getChildNodes(); 
	        			 for (int j = 0; j < childNodes.getLength(); j++) {  
	                         Node node = childNodes.item(j);  
	                         if (null != node && node.getNodeType() == Node.ELEMENT_NODE) {  
	                             String innerName = node.getNodeName();  
	                             String textContent = node.getTextContent();  
	                             if ("servlet-name".equals(innerName)) {  
	                            	 for(Iterator<ServletMapping> iterator = servletMappingList.iterator();iterator.hasNext();){
	                            		 servlet = iterator.next();
	                            		 if(!servlet.getServletName().equals(textContent)) continue;
	                            	 }
	                             } 
	                             if ("url-pattern".equals(innerName)) {  
	                            	 servlet.setUrl(textContent);
	                             }  
	                         }  
	                     } 
	        		 }
	        	 }
	         }
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	private static File getFile(){
		ClassLoader classLoader = Thread.currentThread()  
	            .getContextClassLoader();  
	    if (classLoader == null) {  
	        classLoader = ClassLoader.getSystemClassLoader();  
	    }  
	    URL url = classLoader.getResource("");  
	    String ROOT_CLASS_PATH = url.getPath() + "/";  
	    File rootFile = new File(ROOT_CLASS_PATH);  
	    String WEB_INFO_DIRECTORY_PATH = rootFile.getParent() + "/";  
	    File webInfoDir = new File(WEB_INFO_DIRECTORY_PATH);  
	    String SERVLET_CONTEXT_PATH = webInfoDir.getParent() + "/";  
	  
	               //这里 SERVLET_CONTEXT_PATH 就是WebRoot的路径  
	  
	    String path = SERVLET_CONTEXT_PATH + DEFAULT_WEBXML_LOCATION;  
	    path = path.replaceAll("%20", " ");
	    return new File(path);  
	}
	
	static{
		scanXMLConfig();
	}
}
