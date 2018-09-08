package entity;

import java.util.UUID;

public class Task {

    public String getTaskId() {
	return taskId;
    }

    public void setTaskId(String taskId) {
	this.taskId = taskId;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    String taskId;
    String url;
    String createTime;
    
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Task(String url){
	taskId = UUID.randomUUID().toString();
	this.url = url;
    }

}
