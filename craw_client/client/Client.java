package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

import analyser.Parser;
import entity.Mail;
import entity.MailState;

public class client3 {
    
    Socket ServerConnection;
    int port;
    String ip;
 
    Parser parser = new Parser(); // 用于获取网页的内容
    
    Mail get ;
    Mail send;
    
    client3( String ip,int port) {
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
	this.port = port;
	this.ip = ip;
	System.setProperty("http.proxyHost", "dev-proxy.oa.com");
	System.setProperty("http.proxyPort", "8080");
    }
    
   public void handing() throws Exception{
       
        ServerConnection = new Socket(ip, port);
        ObjectOutputStream os = new ObjectOutputStream(ServerConnection.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(ServerConnection.getInputStream()));
	
	    send = new Mail(MailState.Greeting);
	    os.writeObject(send);
	    os.flush();
	    
	    get = (Mail)is.readObject();  //获得链接
            String msgType =  get.getType().toString();
            System.err.println("第一次打招呼，服务器传回的消息类型"+msgType);
            
            handleMessage(get);
           
	    is.close();
	    os.close();
	    ServerConnection.close(); 
	    
   }


    public void run() throws Exception {
	handing();
	work();
    }

    private void work() {
	
	
	while (true) {
	    try {
		System.err.println("尝试连接服务器");
		
		ServerConnection = new Socket(ip,port);
		ObjectOutputStream os =  new ObjectOutputStream(ServerConnection.getOutputStream());
		ObjectInputStream  is = new ObjectInputStream(new BufferedInputStream(ServerConnection.getInputStream()));
		
		    System.err.println("连接成功");
		    System.out.println("现在开始将链接和内容上传,待上传链接" + send.getUrlList());
		  
		    os.writeObject(send); // 将爬取到的链接发送到server
		    os.flush();
		    
		    System.out.println("上传完毕" );
		    
		    Mail  get = (Mail)is.readObject();  //服务器接收到一个url链接可能会返回一个待爬取的url，也可能是bye
		           
		    handleMessage(get);   
		    
		    is.close();
		    os.close();
		    ServerConnection.close();
		}
	   catch (Exception e) {
		e.printStackTrace();
		System.out.println("Connection Error:" + e);
	    }
	}
    }
    
    private void handleMessage(Mail get2) throws Exception {
	
	// TODO Auto-generated method stub
	   if(get.getType()==MailState.Bye){  //bye则停止工作
               return ;
           }
           
           String url = get.getUrlList();  //服务端传回url，	先默认url是一个，以后改成多个以逗号连接的url则需要循环调用 parser.getUrlContent(url)
           String  taskId = get.getTaskId();
           System.out.println("服务器传来的待爬取url:"+url+" taskid:"+taskId);
           //提取传来url中的链接保存到待上传队列
           
           String content = parser.getUrlContent(url);
           List<String> linksExtracted  = parser.extracLinks(content,0); 
           String contentExtracted = parser.extractContent(content);
           
           
           send = new Mail(MailState.Passage);
           send.getExtractedContent().add(contentExtracted);
           StringBuffer link_list = new StringBuffer();
	    for (String str : linksExtracted) {
		link_list.append(str).append(",");
	    }
	    send.setUrlList(link_list.toString());
	    send.setTaskId(taskId);  //默认上传玩提取链接后才算任务完成
           System.err.println("链接抓取和内容抓取完成");
 
    }

    @Test
	public static void main(String[] args) throws Exception{
	client3 cli1=new client3("127.0.0.1",9000);
	cli1.run();
	}

}
