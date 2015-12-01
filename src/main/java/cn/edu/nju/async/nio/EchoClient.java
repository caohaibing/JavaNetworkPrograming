package cn.edu.nju.async.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import cn.edu.nju.http.Utils;

public class EchoClient {
	private SocketChannel socketChannel;
	private ByteBuffer sendBuf = ByteBuffer.allocate(1024);
	private ByteBuffer receiveBuf = ByteBuffer.allocate(1024);
	private Selector selector;
	private int SERVER_PORT = 8000;

	public EchoClient() throws IOException {
		socketChannel = SocketChannel.open();
		InetAddress LOCAL_HOST = InetAddress.getLocalHost();
		InetSocketAddress inetSocketAddress = new InetSocketAddress(LOCAL_HOST, SERVER_PORT);
		socketChannel.connect(inetSocketAddress);
		socketChannel.configureBlocking(false);
		selector = Selector.open();
		System.out.println("与服务器连接成功");

	}

	public void reveiveFromUser() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String readStr = null;
			while ((readStr = br.readLine()) != null) {
				synchronized (sendBuf) {
					sendBuf.put(Utils.encode(readStr + "\r\n"));
				}
				if (readStr.equals("bye")) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送数据
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void send(SelectionKey key) throws IOException {
		@SuppressWarnings("unused")
		SocketChannel sc = (SocketChannel) key.channel();
		synchronized (sendBuf) {
			sendBuf.flip();
			socketChannel.write(sendBuf);
			sendBuf.compact();
		}
	}

	/**
	 * 接收数据
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void receive(SelectionKey key) throws IOException {
		SocketChannel sc = (SocketChannel) key.channel();
		sc.read(receiveBuf);
		receiveBuf.flip();
		String data = Utils.decode(receiveBuf);

		if (data.indexOf("\n") == -1)
			return;

		String outputData = data.substring(0, data.indexOf("\n") + 1);
		System.out.println(outputData);

		if (outputData.equals("bye\r\n")) {
			key.cancel();
			sc.close();
			System.out.println("关闭与服务器的连接");
			selector.close();
			System.exit(0);
		}
		ByteBuffer temp = Utils.encode(outputData);
		receiveBuf.position(temp.limit());
		receiveBuf.compact();
	}

	/**
	 * 与服务器对话
	 * 
	 * @throws IOException
	 */
	public void talk() throws IOException {

		socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);// 注册读写事件
		while (selector.select() > 0) {
			Set<SelectionKey> keys = selector.selectedKeys();// 返回一个set集合
			Iterator<SelectionKey> it = keys.iterator();
			while (it.hasNext()) {
				SelectionKey key = it.next(); // 取出其中一个key，然后获得操作
				it.remove(); // 删除掉已经处理的
				if (key.isAcceptable()) {

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
	 * main 方法，要开设一个线程，不断的去读取用户输入的东西，然后调用发送或者接受方法
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try {
			final EchoClient echoClient = new EchoClient();
			new Thread() {
				@Override
				public void run() {
					echoClient.reveiveFromUser();
				}
			}.start();

			echoClient.talk();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

}
