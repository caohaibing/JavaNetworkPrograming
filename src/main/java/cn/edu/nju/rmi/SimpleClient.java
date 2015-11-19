package cn.edu.nju.rmi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class SimpleClient {

	/**
	 * 获取远程对象
	 * cn.edu.nju.rmi.SimpleClient
	 * @param nameContext
	 * @throws NamingException
	 */
	public static void showRemoteObjects(Context nameContext) throws NamingException {
		NamingEnumeration<NameClassPair> e = nameContext.list("rmi");
		while (e.hasMore()) {
			System.out.println(e.next().getName());
		}

	}

	public static void main(String[] args) {
		String url = "rmi:localhost";
		try {

			Context nameContext = new InitialContext();
			HelloService helloService1 = (HelloService) nameContext.lookup(url + "service1");
			HelloService helloService2 = (HelloService) nameContext.lookup(url + "service2");

			Class stubClass = helloService1.getClass();
			System.out.println("helloService1是" + stubClass.getName() + "的类");

			Class[] interfaces = stubClass.getInterfaces();

			for (Class c : interfaces) {
				System.out.println(c.getName());
			}

			System.out.println(helloService1.echo("hello"));
			System.out.println(helloService1.getTime());

			System.out.println(helloService2.echo("service2 hello"));
			System.out.println(helloService2.getTime());

			showRemoteObjects(nameContext);

		} catch (Exception e) {
		}
	}

}
