package com.catl.ecad.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import wt.doc.DocumentMaster;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainer;
import wt.mail.EMailMessage;
import wt.method.RemoteAccess;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

public class MailUtil implements RemoteAccess {

    private static final String CLASSNAME = MailUtil.class.getName();

    private static Logger log = Logger.getLogger(CLASSNAME);
    
    private static String DHF_SUBJECT ="文档生效通知";
    
    private static String DHF_TITLE = "以下文档已生效";
    
    private static String MAIL_FROM= "";

    private static String EBOM_SUBJECT="已审核完成";
    
    static {
        try {
            wt.util.WTProperties properties = wt.util.WTProperties.getLocalProperties();
            MAIL_FROM = properties.getProperty("wt.mail.from");
        } catch (Throwable t) {
            System.err.println("Error initializing " + MailUtil.class.getName());
            t.printStackTrace(System.err);
            throw new ExceptionInInitializerError(t);
        }
    }
    

    /**
     * 原理图检入时发送邮件通知
     * @param schepm
     * @param s
     * @throws WTException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JSONException 
     */
    public static void sendMail_SCH(EPMDocument schepm, List<WTUser> s, JSONObject variables) throws WTException, FileNotFoundException, IOException, JSONException{
    	if(schepm != null){
    		List<EPMDocument> list = ECADutil.getPCBsBySCH(schepm);
    		log.debug("************** STATR SEND MAIL NOTICE=" +schepm.getNumber());
    		sendMailTo(schepm, list, s, variables, "原理图"+schepm.getNumber()+"更新通知");
    		log.debug("************** END SEND MAIL NOTICE ************** ");
    	}
    	
    }
    
    /**
	 * 根据原理图获取关联的PCB图
	 * @param epm
	 * @return
	 * @throws WTException
	 */
	public static List<EPMDocument> getPCBsBySCH(EPMDocument epm) throws WTException {
		List<EPMDocument> pcbs = new ArrayList<EPMDocument>();
		if(epm == null){
			return pcbs;
		}
		QueryResult qr = EPMStructureHelper.service.navigateReferencedBy((DocumentMaster) epm.getMaster(), null, true);
		qr = new LatestConfigSpec().process(qr);
		while (qr.hasMoreElements()) {
			Object refDoc = qr.nextElement();
			if(refDoc instanceof EPMDocument){
				EPMDocument epmDocument = (EPMDocument)refDoc;
				if(ECADutil.isPCBEPM(epmDocument)){
					pcbs.add(epmDocument);
				}
			}				
		}		
		return pcbs;
	}



    /**
     * 邮件通知:原理图检入时，如果勾选通知ECAD工程师绘制PCB图，
     * 则发送邮件通知ECAD工程师组进行PCB图绘制
     * 
     * @param schepm
     * @param pList
     * @param s
     * @param subject
     * @param title
     * @throws WTException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JSONException 
     */

    public static void sendMailTo(EPMDocument schepm, List<EPMDocument> pList, List<WTUser> users, JSONObject variables, String title) throws WTException, FileNotFoundException, IOException, JSONException {
        String mailContent;
        
        String startDate = variables.getString("startDate");
		String endDate = variables.getString("endDate");
		String comment = variables.getString("comment");
        
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(title);
        stringbuffer.append("<html>");
        stringbuffer.append("<head>");
        stringbuffer.append("</head>");
        stringbuffer.append("<body>");
        stringbuffer.append("以下原理图已更新，相关的PCB图如下。\n");
        stringbuffer.append("<table bordercolor='#000000' style='width:100%;' border='1' cellspacing='0' cellpadding='2'>");

        stringbuffer.append(" <tr>");
        stringbuffer.append("<td>");
        stringbuffer.append("<b>编号</b>");
        stringbuffer.append("</td>");
        stringbuffer.append("<td>");
        stringbuffer.append("<b>版本</b>");
        stringbuffer.append("</td>");
        stringbuffer.append("<td>");
        stringbuffer.append("<b>名称</b>");
        stringbuffer.append("</td>");
        stringbuffer.append("<td>");
        stringbuffer.append("<b>修改者</b>");
        stringbuffer.append("</td>");
        stringbuffer.append(" </tr>");


        String schName = schepm.getName();
        String schversion  =VersionControlHelper.getIterationDisplayIdentifier(schepm).toString(); 
        String schsubjectObjURL = getURLByDocument(schepm);
                
        stringbuffer.append(" <tr>");
        stringbuffer.append("<td>原理图:<a href=" + schsubjectObjURL + ">" + schepm.getNumber() +"<br /></td>");
        stringbuffer.append("<td>" +  schversion+"<br /></td>");
        stringbuffer.append("<td>" + schName +"<br /></td>");
        stringbuffer.append("<td>" + schepm.getModifierFullName() +"<br /></td>");
        stringbuffer.append(" </tr>");

        if (pList != null) {
            for (EPMDocument epm : pList) {
            	String docNumber = "";
                String docName = "";
                String version = "";
                // String title = "";
                String subjectObjURL = "";
                docNumber = epm.getNumber();
                docName = epm.getName();
                version = VersionControlHelper.getIterationDisplayIdentifier(epm).toString(); 
                subjectObjURL = getURLByDocument(epm);

                stringbuffer.append(" <tr>");
                stringbuffer.append("<td>PCB图:<a href=" + subjectObjURL + ">" + docNumber +"<br /></td>");
                stringbuffer.append("<td>" +  version+"<br /></td>");
                stringbuffer.append("<td>" + docName +"<br /></td>");
                stringbuffer.append("<td>" + epm.getModifierFullName() +"<br /></td>");
                stringbuffer.append(" </tr>");
            }
        }

        stringbuffer.append(" <tr>");
        stringbuffer.append("<td><b>期望开始日期</b></td>");
        stringbuffer.append("<td colspan='3'>"+startDate+"<br /></td>");
        stringbuffer.append(" </tr>");
        stringbuffer.append(" <tr>");
        stringbuffer.append("<td><b>期望截止日期</b></td>");
        stringbuffer.append("<td colspan='3'>"+endDate+"<br /></td>");
        stringbuffer.append(" </tr>");
        stringbuffer.append(" <tr>");
        stringbuffer.append("<td><b>备注</b></td>");
        stringbuffer.append("<td colspan='3'>"+comment+"<br /></td>");
        stringbuffer.append(" </tr>");
  
        stringbuffer.append("</table>");
        stringbuffer.append("</body>");
        stringbuffer.append("</html>");
        mailContent = stringbuffer.toString();
        
        log.debug("************** \n STATR SEND mailContent:"+ mailContent);
        try {

            EMailMessage localEMailMessage=EMailMessage.newEMailMessage();
            for(WTUser user:users){
            	localEMailMessage.addRecipient(user);
    		}
            //String[] addresses = {"alm-gw01@catlbattery.local"};
     		localEMailMessage.setSubject(title);
    		//localEMailMessage.addEmailAddress(addresses);
    		localEMailMessage.addPart(mailContent, "text/html");
    		localEMailMessage.send(true);
        } catch (WTException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * the method to fetch document oid
     * 
     * @param obj
     * @return
     * @throws PersistenceException
     * @throws WTException
     */

    public static String getURLByDocument(WTObject obj) throws PersistenceException, WTException {
        ReferenceFactory referenceFactory = new ReferenceFactory();
        String url = "";
        if (obj != null) {
            String ufid = getOidByPersistable(obj);
            try {
                WTProperties properties = WTProperties.getLocalProperties();

                URLFactory urlfactory = new URLFactory();
                String baseURL = urlfactory.getBaseURL().toExternalForm();
                url = baseURL + "servlet/TypeBasedIncludeServlet?oid=" + ufid + "&u8=1";
            } catch (WTException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return url;

    }

    /**
     * the method is fetch the container url oid.
     * 
     * @param wtContainer
     * @return
     * @throws PersistenceException
     * @throws WTException
     */
    public static String getURLByWTProduct(WTContainer wtContainer) throws PersistenceException, WTException {
        String url = "";
        ReferenceFactory referenceFactory = new ReferenceFactory();
        if (wtContainer != null) {
            String ufid = "";
            ufid = getOidByPersistable(wtContainer);
            try {
                WTProperties properties = WTProperties.getLocalProperties();
                URLFactory urlfactory = new URLFactory();
                String baseURL = urlfactory.getBaseURL().toExternalForm();
                url = baseURL + "servlet/WindchillAuthGW/wt.enterprise.URLProcessor/URLTemplateAction?action=ObjProps&oid=" + ufid + "&u8=1";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url;

    }

    public static String getOidByPersistable(Persistable persistable) {
        String oid = "";
        try {
            ReferenceFactory rf = new ReferenceFactory();
            WTReference wtrf = rf.getReference(persistable.toString());
            oid = rf.getReferenceString(wtrf);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return oid;
    }
    
    /**
     * DPart邮件通知内容及格式确认：
     * 结构设计（物料+名称+图号+大版本）已更新，请各角色评估是否进行协同变更
     * @author szeng 2017-05-11
     * @param pbo
     * @param self
     * @param s
     * @throws WTException 
     * @throws PersistenceException 
     */
    public static void sendMail_XOAction(WTPart part, List<WTUser> s) throws PersistenceException, WTException {
        String toAddress = "";
       // WTUser wtuser = null;
        String mailContent;
        WTContainer container = part.getContainer();
        String from =MAIL_FROM;
        MailFile mf = new MailFile();

        String subject = part.getNumber()+ "," + part.getName();
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<html>");
        stringbuffer.append("<head>");
        stringbuffer.append("</head>");
        stringbuffer.append("<body>");
        
        stringbuffer.append("<table bordercolor='#000000' style='width:100%;' border='1' cellspacing='0' cellpadding='2'>");
        stringbuffer.append("<tr><td><b>产品型号</b></td><td>"+container.getName()+"</td></tr>");
        stringbuffer.append("<tr><td><b>物料号</b></td><td>"+part.getNumber()+"</td></tr>");
        stringbuffer.append("<tr><td><b>物料名称</b></td><td>"+part.getName()+"</td></tr>");
        stringbuffer.append("<tr><td><b>版本</b></td><td>"+VersionControlHelper.getVersionDisplayIdentifier(part)+"</td></tr>");
        
        stringbuffer.append("</table>");
        stringbuffer.append("<p><b>物料已创建，请评估是否需要进行EIP物料认证流程。</b></p>");
        stringbuffer.append("</body>");
        stringbuffer.append("</html>");
        
        mailContent = stringbuffer.toString();
        //System.out.println(mailContent);
        log.debug("************** \n STATR SEND mailContent:"+ mailContent);
        try {
            WTProperties wtproperties = WTProperties.getLocalProperties();
            String host = wtproperties.getProperty("wt.mail.mailhost");
  
            for (WTUser user : s) {
                //wtuser = (WTUser) it.next();
                toAddress = user.getEMail();
                if (toAddress != null && !toAddress.equals("")) {
                    mf.setTo(toAddress);
                }
            }
            mf.setFrom(from);
            mf.setMailHost(host);
            mf.setSubject(subject);
            mf.setText(mailContent);
            mf.send();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
