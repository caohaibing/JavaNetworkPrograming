package cn.edu.nju.sync.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class ServerHandler implements Runnable {

	private SocketChannel socket;

	public ServerHandler(SocketChannel socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			System.out.println("新连接到达，IP：" + socket.getRemoteAddress() + "端口为:" + socket.getLocalAddress());
			Socket processSock = socket.socket();
			BufferedReader br = getBufferedReader(processSock);
			PrintWriter printWriter = getBufferedWriter(processSock);
			String str = null;
			while ((str = br.readLine()) != null) {
				System.out.println(str);
				printWriter.println(str);
				if (str.equals("bye")) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private BufferedReader getBufferedReader(Socket socket) throws IOException {
		InputStream socketR = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketR));
	}

	/**
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private PrintWriter getBufferedWriter(Socket socket) throws IOException {
		OutputStream socketW = socket.getOutputStream();
		return new PrintWriter(socketW, true);
	}

}
