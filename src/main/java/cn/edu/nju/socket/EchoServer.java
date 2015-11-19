package cn.edu.nju.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class EchoServer {
	private int port = 8000;
	private ServerSocket serverSocket;
	private ThreadPoolExecutor threadPool;   //用线程池来管理提交的任务
	private int POOL_SIZE = 4;

	public EchoServer() throws IOException {
		this.serverSocket = new ServerSocket(port);
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);// 创建一个线程池
		System.out.println("服务器启动！！");
	}

	public void service() throws IOException {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				threadPool.execute(new ServerHandler(socket));// 任务添加到线程池里去

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				if(!threadPool.isShutdown()){
					threadPool.shutdown();
				}
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
