package cn.edu.nju.file;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;

import java.io.RandomAccessFile;

/**     
 * 类名称：FileServerHandler    
 * 类描述：    文件服务器控制器
 * @author：Administrator    
 * @version：2015年12月1日 下午3:01:40    
 *     
 */
public class FileServerHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)    //重写读方法   
			throws Exception {
		RandomAccessFile raf = null;   
		long length = -1;
		try {
			raf = new RandomAccessFile(msg, "r");
			length = raf.length();
		} catch (Exception e) {
			ctx.writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": "
					+ e.getMessage() + '\n');
			return;
		} finally {
			if (length < 0 && raf != null) {
				raf.close();
			}
		}
		ctx.write("ok:" + raf.length() + "\n");
		if (ctx.pipeline().get(SslHandler.class) == null) {
			ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));   //写管道

		} else {
			ctx.write(new ChunkedFile(raf));    //通过块文件来写
		}
		ctx.writeAndFlush("\n");   //刷新缓冲区
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush("键入要检索的文件的路径");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();

		if (ctx.channel().isActive()) {
			ctx.writeAndFlush(
					"ERR: " + cause.getClass().getSimpleName() + ": "
							+ cause.getMessage() + '\n').addListener(
					ChannelFutureListener.CLOSE);
		}
	}

}
