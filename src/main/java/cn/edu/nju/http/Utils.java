package cn.edu.nju.http;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 工具类
 * @author tanghuafa
 *
 */
public class Utils {
	
	private static Charset charset=Charset.forName("UTF-8");
	/**
	 * 解码
	 * @param buf
	 * @return
	 */
	public static String decode(ByteBuffer buf){
		CharBuffer charBuffer=charset.decode(buf);
		return charBuffer.toString();
	}
	
	/**
	 * 编码
	 * @param str
	 * @return
	 */
	public static ByteBuffer encode(String str){
		return charset.encode(str);
	}
}
