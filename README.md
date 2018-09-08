# distributed_spider_demo
一个分布式爬虫demo

# 项目介绍<br/>
这个爬虫是一个可拓展的分布式爬虫，采用主从的通信模式，在主机端维护url队列，当从机与主机打招呼后，主机会分发url给从机，从机得到url后进行解析，再返回解析结果给主机持久化，然后主机再分配一个url给从机循环该过程，整个过程就是这样（ps:这个分布式爬虫是以前自己和同学参加学校比赛一起合作的小作品，后来自己在学习lucene,redis等工具的时候也在思考着怎么对这个爬虫进行改进，比赛时的那个版本是可以用的，后来自己改着改着就成现在比较杂的样子了，要运行起来可能要花点功夫，不过代码逻辑基本正确，仅作一个参考吧）<br/>

# 项目结构:（-代表一级目录，--------代表二级目录，以此类推）：<br/>
-crawserver<br/>
--------entity<br/>
---------------Mail<br/>
---------------MailSate<br/>
---------------Task<br/>
--------manager<br/>
---------------taskManager<br/>
--------persistence<br/>
---------------Persistence<br/>
--------server<br/>
---------------Server<br/>
---------------SevUI<br/>
--------test<br/>
---------------TestUtil<br/>

-crawclient<br/>
--------entity<br/>
---------------Mail<br/>
---------------MailSate<br/>
--------client<br/>
---------------Client<br/>
---------------ClientUI<br/>
--------analyser<br/>
---------------Parser<br/>

# 部分关键类功能解释：<br/>

server client共用类:<br/>
Mail类：作为主机和从机之间的通信类(部分getter,setter函数省略掉，太占篇幅)<br/>
MailState类：消息类型<br/>

server端:<br/>
Task类：任务对象<br/>
TaskManager类：维护url的去重和任务队列的监控<br/>
server类 :<br/>

client端:<br/>
parser类：解析url和网页源码（提取规则根据具体抓取需求自行实现）<br/>
client类：（要指出的是限制爬取深度这个功能暂时是为实现的，不过理论上来说按照前文说的给url附加个深度属性，服务器进行判断达到限定深度即可停止爬取是可以实现的）<br/>

# 项目特点：<br/>
待补充，这个比较杂，因为当时自己纯粹是出于一种造轮子的心态，自己是想到啥就往上面加啥，以后有时间我会慢慢整理好列出来，大致可以参考#写在最后的部分

# 写在最后的：<br/>
在本科找实习的面试中，面试官问道从机每爬取一次就返回给主机，这样不会造成主机压力比较大？于是当时也考虑了不同的方案，一种方案是从机接收到url后就一直在本地循环解析，url加入队列，再持久化的过程，这样就只需要和主机打个招呼拿个url就完了，但是这样会造成一个问题，就是url的过滤，因为目前方案中url过滤在主机端进行，主机会过滤掉已爬取的url和重复的url，采用这种方案会导致从机之间不知道对方爬了哪些网页，如果在从机之间增加通信则会大大增加这个爬虫的复杂度，也考虑过为每个从机分配一个hash值，根据hash值判断要不要爬取url，这样从机爬取的url就不会重复，这个方案有一定的可行性，也是可以改进的一种方案。考虑到实际情况，当前爬虫的瓶颈主要是网络请求这一块，所以还是采取当前的通信模式了<br/>
第二个问题就是连接的问题，当前主机和从机采取的是短连接的方式，就是从机每请求一次url都会建立一个新连接请求，从机爬取完后传回主机就断开连接，下次继续发起新的请求获取url。考虑过长连接，这样就不必接连不断地发起请求，但是长连接和短连接哪个性能好一点我还没试，希望以后能回过头来试试。<br/>
第三个问题是这个爬虫作品目前没有考虑丢包情况下从机如何处理，从代码可以看出是从机发送一个请求过去会默认一定可以接受到主机的回复，如果发给主机的包丢失或者主机发回的包丢失那么从机就会处于阻塞状态，目前想法是通过线程休眠和轮询实现超时重新发起请求，重传3次失败则抛出异常<br/>
最后为了提高系统的容错性，我们在主机端维护了一个任务队列，当分配给从机一个任务后，如果从机10s内没有返回任务结果（可能是从机抛错等各种异常），则主机会将该任务从任务队列中移除并将url重新加入待爬取队列分配给其他从机
