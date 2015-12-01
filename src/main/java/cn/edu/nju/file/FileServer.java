package cn.edu.nju.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

/**     
 * 类名称：FileServer    
 * 类描述：    文件服务器
 * @author：Administrator    
 * @version：2015年12月1日 下午2:43:23    
 *     
 */
public class FileServer {
	private static final boolean SSL = System.getProperty("ssl") != null;    //系统有无SSL
	private static final int PORT = Integer.parseInt(System.getProperty("port",
			SSL ? "8992" : "8023"));

	public static void main(String[] args) throws CertificateException,
			SSLException {
		final SslContext sslContext;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslContext = SslContextBuilder.forServer(ssc.certificate(),
					ssc.privateKey()).build();
		} else {
			sslContext = null;
		}
		//事件组
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			//配置服务器
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler(LogLevel.INFO))   //配置日志，日志级别为info
					.childHandler(new ChannelInitializer<SocketChannel>() {   //通道初始化

						@Override
						protected void initChannel(SocketChannel arg0)
								throws Exception {
							ChannelPipeline p = arg0.pipeline();     //获取通道
							if (sslContext != null) { 
								p.addLast(sslContext.newHandler(arg0.alloc()));   //创建安全通道

							}
							p.addLast(new FileServerHandler(),    //配置通道
									new StringEncoder(CharsetUtil.UTF_8),
									new LineBasedFrameDecoder(8192),
									new StringDecoder(CharsetUtil.UTF_8),
									new ChunkedWriteHandler());
						}
					});
			ChannelFuture future = b.bind(PORT).sync();    //同步
			future.channel().closeFuture().sync();   //同步关闭

		} catch (Exception e) {
		} finally {
			bossGroup.shutdownGracefully();  //关闭
			workGroup.shutdownGracefully();
		}

	}
}
