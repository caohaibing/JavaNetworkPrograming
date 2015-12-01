package cn.edu.nju.async.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import cn.edu.nju.http.Utils;

/**
 * 非阻塞模式的 服务器 程序
 * 
 * @author tanghuafa
 *
 */
public class EchoServer {
	private ServerSocketChannel serverSocketChannel;
	private int port = 8000;
	@SuppressWarnings("unused")
	private ThreadPoolExecutor threadPool;
	private int POOL_SIZE = 4;
	private Selector selector;

	public EchoServer() throws IOException {
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);

		serverSocketChannel.configureBlocking(false);// 设置为异步
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors() * POOL_SIZE);
		serverSocketChannel.socket().bind(new InetSocketAddress(port));//这边是socket，不是channel
		System.out.println("服务器已启动！");
	}

	public void service() throws IOException {
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		while (selector.select() > 0) {

			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();// 创建迭代器

			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();// 取出服务的要删除该项

				if (key.isAcceptable()) {
					ServerSocketChannel ssc = (ServerSocketChannel) key
							.channel();
					SocketChannel socketChannel = ssc.accept();
					System.out.println("接收到客户的连接："
							+ socketChannel.getRemoteAddress() + ":"
							+ socketChannel.socket().getPort());
					socketChannel.configureBlocking(false);
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					socketChannel.register(selector, SelectionKey.OP_WRITE
							| SelectionKey.OP_READ, buffer);

				}
				if (key.isReadable()) {
					receive(key);
				}

				if (key.isWritable()) {
					send(key);
				}

			}
		}

	}

	/**
	 * 接收数据
	 * 
	 * @param socketChannel
	 * @throws IOException
	 */
	public void receive(SelectionKey selectionKey) throws IOException {
		ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ByteBuffer readBuf = ByteBuffer.allocate(1024);

		socketChannel.read(buffer);
		buffer.flip();
		buffer.limit(buffer.capacity());
		buffer.put(readBuf);
	}

	/**
	 * 发送数据
	 * 
	 * @param selectionKey
	 * @throws IOException
	 */
	public void send(SelectionKey selectionKey) throws IOException {
		ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

		@SuppressWarnings("unused")
		ByteBuffer writeBuf = ByteBuffer.allocate(1024);

		buffer.flip();
		String data = Utils.decode(buffer);
		if (data.indexOf("\r\n") == -1) {
			return;
		}
		String outputStr = data.substring(0, data.indexOf("\n") + 1);
		System.out.println(outputStr);
		ByteBuffer sendBuf = Utils.encode("send:" + outputStr);
		while (sendBuf.hasRemaining()) {
			socketChannel.write(sendBuf);
		}
		ByteBuffer tempBuf = Utils.encode(outputStr);
		buffer.limit(tempBuf.limit());
		buffer.compact(); // 删除已经处理的字符

		if (outputStr.equals("bye\r\n")) {
			selectionKey.cancel();
			socketChannel.close();
			System.out.println("关闭与客户的连接！！！");
		}

	}

	/**
	 * main函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new EchoServer().service();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
