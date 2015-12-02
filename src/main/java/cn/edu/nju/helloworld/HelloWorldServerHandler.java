package cn.edu.nju.helloworld;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**     
 * 类名称：HelloWorldServerHandler    
 * 类描述：    控制器，会自动被netty调用
 * @author：Administrator    
 * @version：2015年12月2日 下午2:18:41    
 *     
 */
public class HelloWorldServerHandler extends ChannelInboundHandlerAdapter {
	private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W',
			'o', 'r', 'l', 'd' };   //显示在浏览器端的文字
	
	/**
	 *通道读取结束后要刷新缓冲区
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	/**
	 *读取通道
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;

			if (HttpHeaders.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}
			boolean keepAlive = HttpHeaders.isKeepAlive(req);
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
					OK, Unpooled.wrappedBuffer(CONTENT));
			response.headers().set(CONTENT_TYPE, "text/plain");
			response.headers().set(CONTENT_LENGTH,
					response.content().readableBytes());

			if (!keepAlive) {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);   //写文本
			} else {
				response.headers().set(CONNECTION, Values.KEEP_ALIVE);
				ctx.write(response);    //将文本写到response里去
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
