package cn.edu.nju.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

/**     
 * 类名称：HelloWorldServer    
 * 类描述：    主类
 * @author：Administrator    
 * @version：2015年12月2日 下午1:58:20    
 *     
 */
public class HelloWorldServer {

	private static boolean SSL = System.getProperty("ssl") != null; //ssl，是否有安全通道
	static final int PORT = Integer.parseInt(System.getProperty("port",
			SSL ? "8443" : "8080")); //设置端口

	/**    
	 * 方法作用：  main函数
	 * @return      
	 * @throws CertificateException 
	 * @throws SSLException 
	 * @throws InterruptedException 
	*/
	public static void main(String[] args) throws CertificateException,
			SSLException, InterruptedException {
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(),
					ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}
		//两个事件组，一个负责连接，分配等，一个处理请求
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workGroup = new NioEventLoopGroup();
		//配置服务器
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new HelloWorldServerInit(sslCtx)); //添加服务器初始化类

			Channel ch = b.bind(PORT).sync().channel();
			System.err.println("打开浏览器，定位到以下地址：  " + (SSL ? "https" : "http")
					+ "://127.0.0.1:" + PORT + '/');
			ch.closeFuture().sync();

		} finally {
			//关闭
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}

	}

}
