通过阅读线程池，阻塞队列源码，了解过tomcat基本架构和请求处理方式后尝试编写的实例。<br/>
    1.简单模拟阻塞队列的实现，使用了生产者消费者模式，底层采用LinkedList来模拟LinkedBlockingQueue的实现。<br/>
    2.简单模拟线程池的实现，编写的线程池采用线程安全懒汉单例模式，也模拟了当前线程数与核心线程数比较大小后的不同策略，当线程数超过最大允许线程数时的拒绝策略。<br/>
    3.简单模拟tomcat基本架构和请求处理流程，解析WEB.XML文件后采用HashMap作为ServletMapping容器，以URL为key，class为value，简单封装了HttpRequest和HttpResponse解析和处理请求，每一个请求封装为Handler对象，由线程池分配一个线程处理，在处理线程中，请求的URL去匹配HashMap中的value，找到具体class后通过反射动态实例化具体的servlet并将HttpRequest和HttpResponse作为参数传入。
