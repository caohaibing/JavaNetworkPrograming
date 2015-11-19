package cn.edu.nju.http;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.netty.handler.codec.http.HttpRequest;

public class ServerHandler implements Runnable {
	private HttpRequest httpRequest;
	private SocketChannel socketChannel;
	private String line = System.getProperty("line.separator");

	public ServerHandler(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void run() {
		Socket socket = null;
		try {
			socket = socketChannel.socket();
			System.out.println("接收到客户端的请求！！！" + socket.getInetAddress() + ":" + socket.getPort());
			ByteBuffer buffer = ByteBuffer.allocate(1024); // 创建一个缓冲区
			socketChannel.read(buffer);
			buffer.flip();
			System.out.println("打印HTTP请求"+httpRequest);
			

			// 生成HTTP响应结果
			StringBuilder sb = new StringBuilder("HTTP1.1/200 OK!" + line);
			sb.append("Content-Type:text/css" + line);

			socketChannel.write(Utils.encode(sb.toString()));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
