package cn.edu.nju.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

/**
 * 基于UDP实现的聊天服务器
 * @author tanghuafa
 *
 */
public class EchoServer {
	private int port = 8000;
	private DatagramSocket socket;

	public EchoServer() throws SocketException {

		socket = new DatagramSocket(port);
		System.out.println("服务器启动");
	}

	public void service() {
		while (true) {
			try {
				DatagramPacket dp = new DatagramPacket(new byte[512], 512);

				socket.receive(dp); // 接收客户端的数据
				System.out.println("连接到：" + dp.getAddress() + ":"
						+ dp.getPort());

				String str = new String(dp.getData(), 0, dp.getLength()); // 将数据转换为字符串
				System.out.println("客户端发来的信息为:" + str);
				int tmp = new Random().nextInt() % 100;
				dp.setData(new String("回复客户端一个随机数吧：" + tmp).getBytes());
				socket.send(dp);// 发送出数据

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {
		try {
			new EchoServer().service();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

}
