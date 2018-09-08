package manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import entity.Task;

public class TaskManager extends java.util.TimerTask {
    
     public LinkedList<String> urlToCrawList = new LinkedList<>();
     public HashSet<String> urlCrawedHashSet = new HashSet<>();
     public List<Task> taskQueueList = new LinkedList<>();
     
     public Task getTaskById(String id){
	 for(Task task: taskQueueList ){
	     if(task.getTaskId().equals(id)){
		 return task;
	     }
	 }
	 return null;
     }

    @Override
    public void run() {
	// TODO Auto-generated method stub
	Date now = new Date();
	for(Task task : taskQueueList){
	    
	    String createTime = task.getCreateTime();
	    SimpleDateFormat df = new  SimpleDateFormat("yyyyMMddhhmmss");
	    Date cDate = null;
	    try {
		cDate = df.parse(createTime);
	    } catch (ParseException e) {
		e.printStackTrace();
	    }
	    long dif = (now.getTime() - cDate.getTime())/1000 ;
	    if(dif>7){  //运行时间超过7s，判定任务无效，重新加入等待执行队列
		urlToCrawList.add(task.getUrl());
		taskQueueList.remove(task);
		System.err.println("因任务超时，url"+task.getUrl()+"被重新加回待爬取队列，任务"+task.getTaskId()+"被从监听队列移除");
	    }
	    
	}
    }

}
