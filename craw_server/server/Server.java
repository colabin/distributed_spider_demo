package server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import entity.Mail;
import entity.MailState;
import entity.Task;

import persistence.Persisitence;
import manager.TaskManager;

public class server3 {
    
    Lock lock = new Lock();
    Persisitence persistence = new Persisitence();
    
    boolean isListen = true; // 表示服务器是否继续接收链接
    int downMes = 0; // 提取到的网页数量
    int clientNum = 0 ; //连接的客户端数量
 
    int numToCraw = 10; // 限制爬取数量
    int depthToCraw = 3; // 限制爬取层数
    
    ServerSocket clientListener;
    int port; // 监听端口
    
    TaskManager taskManager = new TaskManager();
    ExecutorService executor = Executors.newFixedThreadPool(50); // 创建固定容量大小的缓冲池

    public server3(int por, String url) throws Exception {
	this.port = por;
	taskManager.urlToCrawList.add(url);
    }

    public void setNumToCraw(int numToCraw) {
        this.numToCraw = numToCraw;
    }

    public void setDepthToCraw(int depthToCraw) {
        this.depthToCraw = depthToCraw;
    }

    public void start() throws IOException {

	clientListener = new ServerSocket(port);
	while (true) {
	    System.out.println("主线程等待客户端连接");
	    Socket socket = clientListener.accept();
	    // 开启一个线程
	    executor.execute(new CreateServerThread(socket));
	    
	    // 不断轮训判断是否满足爬取条件
//	    if (downMes >= numToCraw ||depthToCraw >=5 ) {
//		isListen = false; // 此时服务器主动与客户端挥手
//	    }
//	    if (clientNum<= 0 ) {
//		return;
//	    }
	}
    }
    class Lock {
    } // 用于同步对任务队列的访问

    class CreateServerThread implements Runnable {
	private Socket client;
	ObjectInputStream is = null;
	ObjectOutputStream os = null;
	Mail send ; 
	Mail get ; 

	public CreateServerThread(Socket s) throws IOException {
	    client = s;
	}

	@Override
	public void run ()  {
	    System.out.println("进入服务器端子线程并和"+client.getInetAddress()+"开始通信");
	    try {
		is = new ObjectInputStream(new BufferedInputStream(
			client.getInputStream()));
		os = new ObjectOutputStream(client.getOutputStream());
		String contentExtracted = null ;
		Mail get = (Mail) is.readObject();
		
		if (!isListen) { 
			os.writeObject(new Mail(MailState.Bye)); // 客户端可能还会传链接过来客户端进入END状态开始上传
			clientNum -- ;
			is.close();
			os.close();
			return ;
		}
		
		    switch (get.getType()) {
		    case Greeting:
			    clientNum++ ; //客户端连接数量+1
			    if (taskManager.urlToCrawList.size() != 0) {
				  synchronized (lock) {  sendURL();}
			    }
			    else{
				Thread.sleep(10000);
				  if (taskManager.urlToCrawList.size() != 0){
				      synchronized (lock) {  sendURL();}
				  }
				  else{
				      os.writeObject(new Mail(MailState.Bye)); // 客户端可能还会传链接过来客户端进入END状态开始上传
				      os.close();
				  }
			    }
//			Timer timer = new Timer(); // 定时扫描任务队列，清楚超时的任务
//			timer.schedule(taskManager, 1000, 5000); // 1s后执行，每5s扫描一次
			
			break;
		    case Passage:
			List<String> extractedContent2 = get.getExtractedContent();
			System.err.println("服务器获得的网页内容————————————————————"+extractedContent2.toString());
			//持久化
			downMes++;
			
			String[] links = get.getUrlList().split(" ");
			for (String a : links) {
			    System.err.println("传来的链接数"+links.length);
			 //   if (!taskManager.urlCrawedHashSet.contains(a)&& !taskManager.urlToCrawList.contains(a)) // 保护客户端以前没有爬取过和待爬取队列不重复
				taskManager.urlToCrawList.offerLast(a); // 将客户端发过来的链接全部压入队列
			    System.err.println("传来url压入队列，目前待爬取url队列数量"+taskManager.urlToCrawList.size());
			}
			String taskid = get.getTaskId();
			Task t = taskManager.getTaskById(taskid);
			taskManager.taskQueueList.remove(t); // 从执行队列里移除
			System.err.println("url压入队列完毕，任务Id:"+t.getTaskId()+"url:"+t.getUrl()+"完成并从任务队列移除");
			taskManager.urlCrawedHashSet.add(t.getUrl()); // 将url添加到已爬取队列
			System.err.println("url:"+t.getUrl()+"加入到已爬取url队列");
			
			//从队列里面取url,对队列的访问要加锁
			    if (taskManager.urlToCrawList.size() != 0) {
				synchronized (lock) {  sendURL();}
			    }
			    else{
				Thread.sleep(10000);
				  if (taskManager.urlToCrawList.size() != 0){
				      synchronized (lock) {  sendURL();}
				  }
				  else{
				      os.writeObject(new Mail(MailState.Bye)); // 客户端可能还会传链接过来客户端进入END状态开始上传
				      os.close();
				  }
			    }
			default:
			    return ;
		    } // 结束switch
	    } catch (Exception e) {
               e.printStackTrace();
	    }
	}

	private void sendURL() throws IOException {
	    String url = taskManager.urlToCrawList.poll();
	    Task task = new Task(url);
	    String createTime = new SimpleDateFormat("yyyyMMddhhMMss").format(new Date()); // 生成加入队列时间
	    task.setCreateTime(createTime);
	    taskManager.taskQueueList.add(task); // 加入任务队列
	    Mail mail = new Mail(MailState.Linking);
	    mail.setUrlList(url);
	    mail.setTaskId(task.getTaskId());
	    System.err.println("任务Id:"+task.getTaskId()+"url:"+task.getUrl()+"被加入到任务监听队列");
	    System.err.println("任务Id:"+task.getTaskId()+"url:"+task.getUrl()+"被发送到client");
	    os.writeObject(mail);
	    os.flush();
	}
    }
    public static void main(String[] args) throws Exception{
	    server3 s = new server3(9000, "http://www.qq.com");
	    s.setDepthToCraw(1);
	    s.setNumToCraw(1);
	    s.start();
	}
}
