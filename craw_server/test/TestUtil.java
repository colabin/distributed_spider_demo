package test;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import redis.clients.jedis.Jedis;

public class TestUtil {
    
    
    @Test
    public  void testReids() {
	 
	       //利用redis缓存到内存
		       Jedis jedis = new Jedis("localhost");
		       System.out.println("Connection to server sucessfully");
		       //存储数据到列表中
		      // 获取存储的数据并输出
		      List<String> list = jedis.lrange("titleList", 0 ,100);
		      for(int i=0; i<list.size(); i++) {
		        System.out.println("Stored string in redis:: "+list.get(i));
		      }
	       
	 }
    
  
    
    @Test
    public void testLucene() throws Exception {
	
	System.out.print("enter enter  word");
	Scanner input = new Scanner(System.in) ;
	String keyword = input.next();
	
	Analyzer analyzer = new IKAnalyzer(true);
        File indexFile = new File("C:\\Users\\coladong\\Desktop\\indexDir\\");
        Directory directory = null;	
        
        directory = new SimpleFSDirectory(indexFile);
	IndexSearcher searcher = new IndexSearcher(directory);
	QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36,
		new String[] { "title", "content" }, analyzer);
	Query query = queryParser.parse(keyword);

	TopDocs rs = searcher.search(query, null, 10);
	for (int i = 0; i < rs.scoreDocs.length; i++) {
	    // rs.scoreDocs[i].doc 是获取索引中的标志位id, 从0开始记录
	    Document firstHit = searcher.doc(rs.scoreDocs[i].doc);
	    System.out.println("title:" + firstHit.getField("title").stringValue());
	}
	   directory.close(); 
    }

}
