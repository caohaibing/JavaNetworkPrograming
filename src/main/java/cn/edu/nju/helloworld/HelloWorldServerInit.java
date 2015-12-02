package cn.edu.nju.helloworld;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**     
 * 类名称：HelloWorldServerInit    
 * 类描述：    服务器初始化
 * @author：Administrator    
 * @version：2015年12月2日 下午2:10:48    
 *     
 */
public class HelloWorldServerInit extends ChannelInitializer<SocketChannel> {
	private final SslContext sslCtx;

	public HelloWorldServerInit(SslContext ctx) {
		this.sslCtx = ctx;
	}

	/**
	 *初始化通道
	 */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline(); //获取通道管道
		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		pipeline.addLast( //责任链模式的使用，类似于Struts的拦截器
				new HttpServerCodec(), new HelloWorldServerHandler() //将处理器添加到初始化类中去。。。
		);
	}

}
