package com.catl.common.toolbox.data;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.util.GenericUtil;
import com.catl.part.PartConstant;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class refreshWTPartMaturity implements RemoteAccess{

    public static void main(String[] args) throws RemoteException, InvocationTargetException {
		
		if (args == null || args.length < 2){
        	
        	System.out.printf("请输入正确的用户名、密码！");
        } else {
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(args[0]);
			ms.setPassword(args[1]);
			try {
				SessionHelper.manager.setAuthenticatedPrincipal(args[0]);
				RemoteMethodServer.getDefault()
				.invoke("doLoad",
						refreshWTPartMaturity.class.getName(), null, null,
						null);
			} catch (WTException e) {
				e.printStackTrace();
			}
        }
	}

    public static void doLoad(){
        
    	Transaction ts=null;
        try {
        	ts = new Transaction();
            ts.start();
            
            Vector<WTPartMaster> wtparts=getWTPartMaster();
            
            for(WTPartMaster partMaster : wtparts){

            	String oldMaturity = (String)GenericUtil.getObjectAttributeValue(partMaster, PartConstant.IBA_CATL_Maturity);
            	
            	// 如果旧物料已经有成熟度，则不更新
            	if (oldMaturity == null || "".equals(oldMaturity.trim())){
            		PersistableAdapter genericObj = new PersistableAdapter(partMaster, null, null, new UpdateOperationIdentifier());
        			genericObj.load(PartConstant.IBA_CATL_Maturity);
        			genericObj.set(PartConstant.IBA_CATL_Maturity, "1");
        			Persistable updatedObject = genericObj.apply();
        			partMaster = (WTPartMaster) PersistenceHelper.manager.save(updatedObject);
            	}
            }
            System.out.println("refreshWTPartMaturity 操作成功！");
            ts.commit();
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("报错信息："+e.getMessage());
            if(ts != null)
                ts.rollback();
        }

    }

    static Vector<WTPartMaster> getWTPartMaster() throws WTException {
    	
		Vector<WTPartMaster> wtparts=new Vector<WTPartMaster>();
		QuerySpec qs= new QuerySpec(WTPartMaster.class);
//		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] {0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){			
			WTPartMaster wtpartmaster = (WTPartMaster) qr.nextElement();
			wtparts.add(wtpartmaster);
		}
		return wtparts;
	}

}
