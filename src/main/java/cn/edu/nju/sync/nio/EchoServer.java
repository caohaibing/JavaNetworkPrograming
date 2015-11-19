package cn.edu.nju.sync.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class EchoServer {
	private int port = 8000;
	private ServerSocketChannel serverSocketChannel;
	private ThreadPoolExecutor threadPool;

	private int POOL_SIZE = 4;

	public EchoServer() throws IOException {
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);
		serverSocketChannel = ServerSocketChannel.open();  //用serversocketchannet来创建一个对象
		serverSocketChannel.socket().setReuseAddress(true);  //设置为可连接模式
		serverSocketChannel.bind(new InetSocketAddress(port)); //绑定端口和IP地址
		System.out.println("服务器启动！！！");

	}

	public void service() {
		while (true) {
			SocketChannel socket = null;
			try {
				socket = serverSocketChannel.accept();  //通过channel来获取与之关联的socket对象
				threadPool.execute(new ServerHandler(socket));  //放入线程池里，让线程池管理

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			new EchoServer().service();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
