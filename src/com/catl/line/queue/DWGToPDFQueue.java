package com.catl.line.queue;

import java.sql.Timestamp;

import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;

import wt.org 

.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueEntry;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class DWGToPDFQueue{
	/**
	 * 将文档添加到队列
	 * @param self 文档oid
	 * @throws WTException
	 */
	public static void executeWfExpression(String self) throws WTException{
		SessionServerHelper.manager.setAccessEnforced(false);
		try{
		ProcessingQueue oQueue=QueueHelper.manager.getQueue(ConstantLine.queue_dwgtopdf); 
		if(oQueue==null){
			throw new LineException("找不到队列"+ConstantLine.queue_dwgtopdf);
		}
		WTPrincipal user=SessionHelper.manager.getAdministrator();
		QueueEntry entry=oQueue.addEntry(user, "execute", DWGToPDF.class.getName(), new Class[] { String.class }, new Object[] { self });
		entry.setStartExec(new Timestamp(System.currentTimeMillis()+3000));//延迟三秒执行
		entry.setFailureCount(3l);
		}catch(WTException e){
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}
	}
}
