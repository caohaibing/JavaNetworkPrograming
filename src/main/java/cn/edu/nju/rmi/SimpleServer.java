package cn.edu.nju.rmi;

import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleServer {
	public static void main(String[] args) throws RemoteException, NamingException {
		try {
			HelloService service1 = new HelloServiceImpl("service1");
			HelloService service2 = new HelloServiceImpl("service2");

			Context namingContext = new InitialContext();
			namingContext.rebind("rmi://localhost/service1", service1); // 对象被注册到名称上面了
			namingContext.rebind("rmi://localhost/service2", service2);

			System.out.println("服务器注册了两个HelloService对象");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
