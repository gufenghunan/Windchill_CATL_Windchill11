package com.catl.common.toolbox.export;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.common.toolbox.data.UpdatePartName;
import com.catl.loadData.util.ExcelWriter;

public class PartAndIBA implements RemoteAccess{

    private static final String CLASSNAME = PartAndIBA.class.getName();
    private static Logger log =Logger .getLogger(PartAndIBA.class.getName());
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "exportPart";
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, null, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void exportPart() throws Exception{
        System.out.println("start PartAndIBA.......................");
        MethodContext context = MethodContext.getContext();
        WTConnection wtConn = (WTConnection)context.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<String[]> list = new ArrayList<String[]>();
        String[] str = null;
        try{
            String sql = "select pm.wtpartnumber,sv_oldpn.value2 oldPartNUmber,pm.name,sv_gg.value2 specification from wtpartmaster pm left join wtpart p on p.ida3masterreference=pm.ida2a2 and p.LATESTITERATIONINFO=1"  
                    +" left join (select sv.IDA3A4,sv.value2 from StringValue sv left join stringdefinition sd on sv.ida3a6=sd.ida2a2 where sd.name='specification') sv_gg on sv_gg.ida3a4=p.ida2a2 "
                    +" left join (select sv.IDA3A4,sv.value2 from StringValue sv left join stringdefinition sd on sv.ida3a6=sd.ida2a2 where sd.name='oldPartNumber') sv_oldpn on sv_oldpn.ida3a4=p.ida2a2 "
                    +" order by pm.wtpartnumber";
            statement = wtConn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                str  =new String[4];
                str[0]  = resultSet.getString("wtpartnumber");
                str[1] = resultSet.getString("oldPartNUmber");
                str[2] = resultSet.getString("name");
                str[3]  = resultSet.getString("specification");
                list.add(str);
            }
            if(list.size()>0){
                ExcelWriter ew = new ExcelWriter();
                Date date = new Date();
                boolean flat = ew.exportExcelList("d://data/物料信息"+date.getTime()+".xlsx", "物料信息", new String[]{"编号","旧物料号","名称","规格"},list);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            if(resultSet != null) resultSet.close();
            if(statement != null) statement.close();
            if(wtConn != null && wtConn.isActive()) wtConn.release();
        }
        System.out.println("end PartAndIBA.......................");
    }
}
