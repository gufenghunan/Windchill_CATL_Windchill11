package com.catl.line.queue;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.catl.line.constant.ConstantLine;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.util.WTException;
/**
 * 创建转图队列
 * @author hdong
 *
 */
public class CreateQueue implements RemoteAccess{
	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		 
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
        rms.setUserName(args[0]);
        rms.setPassword(args[1]);
		rms.invoke("create", CreateQueue.class.getName(), null, null, null);
	}
	public static void create() throws WTException{
		ProcessingQueue oQueue=QueueHelper.manager.getQueue(ConstantLine.queue_dwgtopdf); 
		if(oQueue==null){
			oQueue=QueueHelper.manager.createQueue(ConstantLine.queue_dwgtopdf);  
			QueueHelper.manager.enableQueue(oQueue, true);
			QueueHelper.manager.startQueue(oQueue);
        }
	}
}
