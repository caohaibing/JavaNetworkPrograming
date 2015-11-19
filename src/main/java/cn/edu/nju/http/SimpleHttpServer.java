package cn.edu.nju.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SimpleHttpServer {

	private int port = 8000;
	private ServerSocketChannel serverSocketChannel;
	private ThreadPoolExecutor threadPool;
	private int POOL_SIZE = 4;

	private SimpleHttpServer() throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);
		serverSocketChannel.bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);//设置为异步
		threadPool = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		System.out.println("服务器已启动！");
	}

	public void service() {
		SocketChannel socketChannel = null;
		try {
			socketChannel = serverSocketChannel.accept();
			threadPool.execute(new ServerHandler(socketChannel));
			

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {

	}

}
