package analyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.jdesktop.jdic.browser.WebBrowser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Parser {

    public static String proxyIPList[] = {"ec2-50-16-197-120.compute-1.amazonaws.com"};
    public static int proxyPortList[] = { 8001};
    
    WebBrowser webBrowser = new WebBrowser();
    List<String> nextPageLinks = new ArrayList<String>();

    public String getUrlContent(String url) throws Exception {

	
	URL urlToDownload = new URL(url);
	HttpURLConnection conn = (HttpURLConnection) urlToDownload.openConnection();
	
	conn.setRequestProperty("User-agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
     // conn.setRequestProperty("Cookie",""); //模拟登录功能
	conn.setConnectTimeout(3000);
	conn.setReadTimeout(3000);
	
	
	 for (int i = 1; i <= 3; i++) {
		try {
		    conn.connect();
			int code = conn.getResponseCode();
			// ip被限制,切换ip代理
			if (code == HttpStatus.SC_FORBIDDEN) {
			    for (int j = 0; i < proxyIPList.length; i++) {
				if (testProxyServer(url, proxyIPList[i], proxyPortList[i])) { // 代表有可以用的代理ip
				    return getUrlContent(url);
				}
				if (i == proxyIPList.length) {
				    return null;
				}
			    }
			}
			// 页面重定向
			if (code == HttpStatus.SC_MOVED_PERMANENTLY
				|| code == HttpStatus.SC_MOVED_TEMPORARILY
				|| code == HttpStatus.SC_SEE_OTHER
				|| code == HttpStatus.SC_TEMPORARY_REDIRECT) {
			    // 读取新的URL地址
			    String location = conn.getHeaderField("location");
			    // 再根据location爬取一遍
			    return getUrlContent(location);
			}

			if (code == HttpStatus.SC_OK) { // 如果获取到网页字符集
			    String line = null;
			    StringBuffer bf = new StringBuffer();
			    if (conn.getContentEncoding() != null) {
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream(),
						conn.getContentEncoding()));
				while ((line = reader.readLine()) != null) {
				    bf.append(line);
				}
				return bf.toString();
			    } else {
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "gbk"));
				while ((line = reader.readLine()) != null) {
				    bf.append(line);
				}
				return bf.toString();
			    }
			}
			    //成功则
		} catch (Exception e) {
		    try {
			if(i==3){
			    System.out.println("3次重试均失败");
				break ;
			}
			Thread.sleep(i * 3000);
			e.printStackTrace();
			System.out.println("正在等待重试");
			continue;
		    } catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		}

	    }
       
	 return conn.getResponseMessage();  
	
    }

    public void setProxy(String proxyIP, int proxyPort) {

	// 有些代理在授权用户访问因特网之前，要求用户输入用户名和口令。如果您使用位于防火墙之内的Web浏览器，您就可能碰到过这种情况。以下是执行认证的方法：  
	// URLConnection connection=url.openConnection(); String   password="username:password"; 
	// String   encodedPassword=base64Encode(password); 
	// connection.setRequestProperty("Proxy-Authorization",encodedPassword); 
	// 设置爬取的代理（外网环境下注释掉就可以）
	System.getProperties().put("proxySet", "true");
	System.getProperties().put("proxyHost", proxyIP);
	System.getProperties().put("proxyPort", proxyPort);
    }

    private boolean testProxyServer(String url, String proxyIP, int proxyPort) {
	// TODO Auto-generated method stub
	setProxy(proxyIP, proxyPort);
	try {
	    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	    conn.connect();
	    int statusCode = conn.getResponseCode();
	    if (statusCode == 403) {
		return false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return true;
    }
    /**
     * 获取网页编码
     * 
     * @param url
     * @return
     */
    public String getCharset(String url) throws Exception {
	// log.info("进入读页面的关键词:" + keyword);
	String charset = "";
	URL httpurl = new URL(url);

	HttpURLConnection httpurlcon = (HttpURLConnection) httpurl
		.openConnection();
	// google需要身份
	httpurlcon.setRequestProperty("User-agent", "Mozilla/4.0");
	charset = httpurlcon.getContentType();
	// 如果可以找到
	if (charset.indexOf("charset=") != -1) {
	    charset = charset.substring(charset.indexOf("charset=")
		    + "charset=".length(), charset.length());
	    return charset;
	} else {
	    return null;
	}
    }

    private String dynamicDownLoad(String url) throws Exception {
	
	//webBrowser.setURL(new URL(url));
	// webBrowser .addWebBrowserListener(new WebBrowserListener() {
	// public void documentCompleted(WebBrowserEvent event) { }
	// public void downloadStarted(WebBrowserEvent event) {}
	// public void downloadCompleted(WebBrowserEvent event) { }
	// public void downloadProgress(WebBrowserEvent event) { }
	// public void downloadError(WebBrowserEvent event) { }
	// public void titleChange(WebBrowserEvent event) { }
	// public void statusTextChange(WebBrowserEvent event) { }
	// public void windowClose(WebBrowserEvent arg0) { }
	// });                    //添加监听事件
	
	String jscript = "function getAllHtml() {" + "var a='';"
		+ "a = '<html><head><title>';" + "a += document.title;"
		+ "a += '</title></head>';" + "a += document.body.outerHTML;"
		+ "a += '</html>';" + "return a;" + "}" + "getAllHtml();";
	String result = webBrowser.executeScript(jscript);
	return null;
    }


    public List<String> extracLinks(String content,int number) throws Exception {
	
	Document doc = Jsoup.parse(content);
	if(number==0){     //不需要进行分页爬取
            //TODO 制定规则提取链接
	    return null ;
	}
	else{
	    if(number==0){      //达到提取分页的个数，停止爬取
		    return null;              
		}
	    else{
		    //TODO 制定规则提取下一页链接，深度遍历
		    nextPageLinks.add("下一页link");
		    number--;
		    extracLinks("下一页链接",number);
		}
	    return nextPageLinks ;
	}
	
	
    }

    public String extractContent(String content) throws Exception {

	Document doc = Jsoup.parse(content);
	//TODO 制定规则提取要抓取内容
	return null;
    }
  
}
