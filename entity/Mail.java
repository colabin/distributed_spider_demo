package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mail implements Serializable {
    private static final long serialVersionUID = 1L;
    private final MailState Type;
    private String urlList = "";
    private List<String> extractedContent = new ArrayList<String>();
    private String taskId;


    public void setExtractedContent(List<String> extractedContent) {
	this.extractedContent = extractedContent;
    }

    public String getUrlList() {
	return urlList;
    }

    public void setUrlList(String urlList) {
	this.urlList = urlList;
    }

    public List<String> getExtractedContent() {
	return extractedContent;
    }

    public Mail2(MailState type) {
	super();
	Type = type;
    }

    public MailState getType() {
	return Type;
    }

    public String getTaskId() {
	return taskId;
    }

    public void setTaskId(String id) {
	taskId = id;
    }
}
