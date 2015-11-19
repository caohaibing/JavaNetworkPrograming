package cn.edu.nju.udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * 基于UDP实现的聊天服务器
 * 
 * @author tanghuafa
 *
 */
public class EchoClient {
	private String remoteHost = "localhost";
	private int port = 8000;
	private DatagramSocket socket;

	public EchoClient() throws SocketException {
		socket = new DatagramSocket();
		System.out.println("客户端启动");
	}

	public void talk() {
		try {
			InetAddress remoteIP = InetAddress.getByName(remoteHost);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));// 用户输入

			String str = null;
			while ((str = br.readLine()) != null) {

				byte[] outputByte = str.getBytes(); // 将输入的字符串转为byte
				DatagramPacket dpout = new DatagramPacket(outputByte, outputByte.length, remoteIP, port);
				socket.send(dpout); // 封装为packet发送出去

				DatagramPacket dpin = new DatagramPacket(new byte[512], 512); // 接收服务器端发送的报文
				socket.receive(dpin); // 接收
				System.out.println("接收到服务器的数据为:" + new String(dpin.getData(), 0, dpin.getLength()));

				if (str.equals("bye\r\n"))
					break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

	public static void main(String[] args) throws SocketException {
		new EchoClient().talk();
	}

}
