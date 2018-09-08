package persistence;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import redis.clients.jedis.Jedis;


public class Persisitence {

    Analyzer analyzer;
    File indexFile;
    IndexWriterConfig indexWriterConfig;
    Directory directory;
    IndexWriter indexWriter;
    
    Jedis jedis;
	/*保存网页字节数组到本地文件 filePath 为要保存的文件的相对地址*/
	public void saveToLocal(String content,String filePath) throws IOException{
		FileWriter writer = new FileWriter(filePath,true);
		writer.write(content+"\n");
		writer.flush();
		writer.close();
	}
	
	public void prepareLucene() throws IOException{
	    analyzer = new IKAnalyzer(true);  
	    indexFile = new File("C:\\Users\\coladong\\Desktop\\indexDir\\");
	    indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer); 
	    directory = new SimpleFSDirectory(indexFile);
	    indexWriter = new IndexWriter(directory, indexWriterConfig);
	}
	
	public void saveAsLucene(String title) throws IOException{
	             this.prepareLucene();
	             Document doc = new Document();  
	             doc.add(new Field("title", title, Field.Store.YES,Field.Index.ANALYZED));  
	    	     //indexWriter.deleteAll(); 
	    	    System.out.println("对内容建立索引");
	            indexWriter.addDocument(doc);  
	            indexWriter.commit(); //相当于flush();不然就不会同步到磁盘
	}
	
	public void prepareJedis(){
	   jedis  = new Jedis("localhost");
	}
	
	public void savaAsRedis(String title){
	       this.prepareJedis();
 	       System.out.println("Connection to server sucessfully");
 	       jedis.lpush("titleList", title);
	}

	}
