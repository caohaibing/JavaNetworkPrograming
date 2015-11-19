package cn.edu.nju.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author tanghuafa
 *
 */
public class EchoServer {
	private final int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public void start() {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(eventLoopGroup).channel(NioServerSocketChannel.class).localAddress(port)
					.childHandler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast(new EchoServerHandler());
						}
					});

		} catch (Exception e) {
		}

	}

	public static void main(String[] args) {
		
	}

}
