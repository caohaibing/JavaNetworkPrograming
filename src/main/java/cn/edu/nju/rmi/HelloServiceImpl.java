package cn.edu.nju.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8401327978540781750L;

	private String name;

	protected HelloServiceImpl(String name) throws RemoteException {  //必须抛出remoteexception异常
		this.name = name;
	}

	@Override
	public Date getTime() throws RemoteException {
		System.out.println(name + "调用getTime方法");

		return new Date();
	}

	@Override
	public String echo(String msg)  throws RemoteException{
		System.out.println(name + "调用echo方法");
		return "echo" + msg + "from" + name;
	}
	


}
