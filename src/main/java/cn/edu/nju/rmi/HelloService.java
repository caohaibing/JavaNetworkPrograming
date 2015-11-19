package cn.edu.nju.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface HelloService extends Remote {
	public String echo(String msg) throws RemoteException;  //必须声明抛出这种异常
	public Date getTime() throws RemoteException;
}
