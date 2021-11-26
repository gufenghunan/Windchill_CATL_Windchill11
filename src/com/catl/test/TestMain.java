package com.catl.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTList;
import wt.fc.collections.WTSet;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.maturity.PromotionTarget;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTMessage;
import wt.vc.Versioned;
import wt.vc.baseline.BaselineHelper;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.WfException;
import wt.workflow.work.WorkflowHelper;

import com.catl.bom.cad.CatlFinderCreator;
import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.workflow.ECWorkflowUtil;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.doc.workflow.DocClassificationModel;
import com.catl.doc.workflow.DocWfUtil;
import com.catl.integration.ReleaseUtil;
import com.catl.report.MultiValuedAttribute;
import com.google.thirdparty.publicsuffix.PublicSuffixPatterns;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.SearchOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
public class TestMain implements RemoteAccess{
    private static final String CLASSNAME = TestMain.class.getName();
	private static Logger log =Logger .getLogger(TestMain.class.getName());
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);
	
	    
		String method = "checkOldPartNumber";
        WTPart part = (WTPart)GenericUtil.getInstance("VR:wt.part.WTPart:"+args[0]);
        System.out.println(".........part="+part.getNumber());
        Class[] types = {WTPart.class};
        Object[] values={part};
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
	}
	
	public static void checkOldPartNumber(WTPart checkPart) throws WTException{
		if(checkPart != null){
			Object objValue = null;
			try {
				PersistableAdapter genericObj = new PersistableAdapter(checkPart, null, null, new SearchOperationIdentifier());
				genericObj.load("oldPartNumber");
				objValue = genericObj.get("oldPartNumber");
			} catch (WTException e) {
				e.printStackTrace();
			}
			Set<WTPart> set = PartUtil.getLastedPartByStringIBAValue(checkPart.getNumber(), "oldPartNumber", (String)objValue);
			for(WTPart part : set){
				System.out.println("...................."+part.getNumber());
			}
		}
	}
	
	
	
	
	  public static void remoteInvoke(){
	        String method = "doRemoteTest";
	        String refStr = "Any String";
	        Class[] types = {String.class};
	        Object[] values={refStr};
	        try {
	         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	    }
	  
	public static void doRemoteTest(String anyString){
	          try{
	                
	                String refStr = "VR:wt.change2.WTChangeRequest2:72543";
                    MultiValuedAttribute.getAttributes(refStr);
                    
                    
                    
                } catch (WTException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
	}
	
	
	
	
	
	public static void getTaskNumByUsername(String username){
	    System.out.println("username:"+username);
	    WTUser user = null;
        try {
                 user = OrganizationServicesHelper.manager.getAuthenticatedUser(username);
                 if(user==null){
                     System.out.println("WARN: User " + username +  " is not existed in the System ");         
                 }else{
                     System.out.println("email:"+user.getEMail());
                 }
                 QueryResult result = WorkflowHelper.service.getUncompletedWorkItems(user);
                 System.out.println("username:"+username+",taskNum="+result.size());
                 while(result.hasMoreElements()){
                     System.out.println("username:"+username+",taskNum="+result.nextElement().toString());
                 }
        } catch (WTException e) {
                 e.printStackTrace();
        }
	}

	 public static void remoteDocType() throws WTException {
	     WTObject obj = (WTObject)GenericUtil.getInstance("VR:wt.part.WTPart:862279");
	        System.out.println(((WTPart)obj).getNumber());
	        QueryResult docresult =PartDocServiceCommand.getAssociatedDescribeDocuments((WTPart)obj);
	        while (docresult.hasMoreElements()) {//取第一个 PCBA、AUTO、GERBER文档
	            WTDocument doc = (WTDocument) docresult.nextElement();
	            System.out.println(doc);
	            TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
	            System.out.println(ti);
	           String  type = ti.getTypename();
	           System.out.println("doc type:"+type);
	           boolean isNeedPart = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_autocadDrawing)
	                   || type.contains(TypeName.doc_type_gerberDoc);
	           if(isNeedPart){
	               System.out.println("select doc number:"+doc.getNumber()+",doc version:"+doc.getVersionIdentifier().getValue());
	               break;
	           }
	        }
        
    }

    public static void deleterole()
	 {
		 String partStr = "VR:wt.doc.WTDocument:470704";
		  WTDocument doc = (WTDocument) GenericUtil.getInstance(partStr);
		  Role role = Role.toRole(RoleName.COUNTERSIGN_PEOPLE);
		  try {
			Team team2 = (Team) TeamHelper.service.getTeam(doc);
			team2.deleteRole(role);
			
		} catch (TeamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	public static void testgetObjectbyIBA()
	{
		String partStr = "OR:wt.part.WTPart:463494";
		WTPart part = (WTPart) GenericUtil.getInstance(partStr);
		try {
			ArrayList list =getWTObjectByIBA("wt.part.WTPart", "cls", "500201");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList getWTObjectByIBA(String objClassTypeName, String define, String value) throws Exception {

        Class objclass = Class.forName(objClassTypeName);

        ArrayList<String> returnArrayList = new ArrayList<String>();

        if (value != null && value.length() > 0 && define != null && define.length() > 0) {

            QuerySpec queryspec = new QuerySpec();// 構造查詢規格

            // i,j用于構造SearchCondition

            int td = queryspec.appendClassList(StringDefinition.class, false);// false表示返回此對象

            int tv = queryspec.appendClassList(StringValue.class, false);// false表示返回此對象

            int tp = queryspec.appendClassList(objclass, true);// true表示不返回此對象

            queryspec.appendOpenParen();// 表示'('符號

            queryspec.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", objclass, "thePersistInfo.theObjectIdentifier.id"), new int[] { tv, tp });

            queryspec.appendAnd();

            queryspec.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", StringDefinition.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { tv, td });

            queryspec.appendAnd();

            queryspec.appendWhere(new SearchCondition(StringValue.class, "value2", SearchCondition.EQUAL, value), new int[] { tv });

            queryspec.appendAnd();

            queryspec.appendWhere(new SearchCondition(StringDefinition.class, "name", SearchCondition.LIKE, define), new int[] { td });

            queryspec.appendCloseParen();// 表示')'符號

            System.out.println("queryspec==>" + queryspec);

            QueryResult queryresult = PersistenceHelper.manager.find((StatementSpec) queryspec);

            System.out.println("queryresult==>" + queryresult.size());
            for (; queryresult.hasMoreElements();) {

                Persistable apersistable[] = (Persistable[]) queryresult.nextElement();

                if (apersistable[0] != null) {

                    if(apersistable[0] instanceof WTPart){

                    	WTPart part = (WTPart) apersistable[0];
                    	if(!returnArrayList.contains(part.getNumber()))
                    	{
                    	returnArrayList.add(part.getNumber());
                    	}
                      System.out.println("part number===="+part.getNumber());
                  
                    }               
                }
            }
        }
        System.out.println("returnArrayList==>" + returnArrayList);

        return returnArrayList;

    }

	
    public static void testgetECNbuobject()
    {
    	Boolean isworkECA=false;
		String partStr = "OR:wt.part.WTPart:463494";
		WTPart part = (WTPart) GenericUtil.getInstance(partStr);
    	try {
    		WTCollection collection= RelatedChangesQueryCommands.getRelatedResultingChangeNotices(part);
    		Iterator iterator= collection.iterator();
    		while(iterator.hasNext()){
    			ObjectReference objReference=(ObjectReference)iterator.next();
				WTChangeOrder2 eco = (WTChangeOrder2)objReference.getObject() ;
				System.out.println("eco number=="+eco.getNumber());
				QueryResult ecnqr = ChangeHelper2.service.getChangeActivities(eco);
				while (ecnqr.hasMoreElements()) {
					WTChangeActivity2 eca = (WTChangeActivity2) ecnqr.nextElement();
					System.out.println(eca.getNumber());
					 if(!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED)&&
								!eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED) )
							 {
								 isworkECA=true;
							 }
				}
				
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public static void testdeletehtmlfile() throws Exception
	{
	    String refStr = "VR:wt.change2.WTChangeOrder2:450473";
        WTObject obj = (WTObject)GenericUtil.getInstance(refStr);
	    WTChangeOrder2  eco= (WTChangeOrder2)obj;
	    //DocUtil.deleteAttachmentHtml(eco);
	    System.out.println("need date==="+eco.getNeedDate());
	    
	    fixTime(eco.getNeedDate());
	    
	}
    private static Timestamp fixTime(Timestamp timestamp) {
        timestamp = new Timestamp(timestamp.getTime() - (timestamp.getTimezoneOffset()) * 60 * 1000);
        System.out.println(timestamp);
        return timestamp;
    }
	public static void testgetpreversion() throws WTException
	{
		String partStr = "VR:wt.part.WTPart:347553";
		WTPart part = (WTPart) GenericUtil.getInstance(partStr);
		RevisionControlled verson= (RevisionControlled) PartUtil.getPreviousVersion(part);
		System.out.println("version==="+verson.getVersionIdentifier().getValue()+
				"."+verson.getIterationIdentifier().getValue());
		
		
	}
    public static void testsetECAstate() throws ChangeException2, WTException
    {
	    String refStr = "VR:wt.change2.WTChangeOrder2:386477";
        WTObject obj = (WTObject)GenericUtil.getInstance(refStr);
	    WTChangeOrder2  eco= (WTChangeOrder2)obj;
	    
	   QueryResult qr = ChangeHelper2.service.getChangeActivities(eco);
	   System.out.println("qr.size"+qr.size());
	   WTList ecaList = new WTArrayList(20);
       
	   while(qr.hasMoreElements()){
           WTChangeActivity2 eca =  (WTChangeActivity2)qr.nextElement();
           System.out.println("eca number=="+eca.getNumber());
           ecaList.add(eca);   
           
       }
       
       LifeCycleHelper.service.setLifeCycleState(ecaList, State.toState(ChangeState.IMPLEMENTATION), false);
	   
      
    }

  
    
	public static void remoteInvoke(String refStr,String method,String className){
	    System.out.println("Enter into remote invoke");
        Class[] types = { String.class };
        Object[] values = { refStr };
        try {
         RemoteMethodServer.getDefault().invoke(method, className, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
	}
	
	
	
	private static void getPropertyTest() {
		Properties props = null;
		try {
			props = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String rdmaddress = props.getProperty("rdm.address");
		System.out.println("RDM add ress " + rdmaddress );
	}
	
	public static void testLifeCycleState(){
		String partStr = "VR:wt.part.WTPart:172197";
		WTPart part = (WTPart) GenericUtil.getInstance(partStr);
		System.out.println("part number = " + part.getNumber());
		try {
			LifeCycleHelper.service.setLifeCycleState(part, State.toState(PartState.RELEASED), false);
			part = (WTPart) PersistenceHelper.manager.refresh(part);
		} catch (WTInvalidParameterException | WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("state = " + part.getLifeCycleState().toString());
		
		
	}
	public static void TestDocCls() throws WTException
	{
		Vector clsVector = DocWfUtil.getExcelData("DocumentClassification.xls");
		DocClassificationModel model=(DocClassificationModel) clsVector.get(0);

	}
	public static void TestClassify()
	{
		String nodename="320320";
		ArrayList<String> parentNameString=new ArrayList<String>();
		
		try {
			LWCStructEnumAttTemplate nodeAttTemplate=ClassificationUtil.getLWCStructEnumAttTemplateByName(nodename);
			System.out.println("nodeAttTemplate name=="+nodeAttTemplate.getName());
			ClassificationUtil.getLastNodeName(nodeAttTemplate,parentNameString);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("parent node name==="+parentNameString.get(0).toString());
	}
	
	public static void TestgetChangeableActivity() throws ChangeException2, WTException
	{
		String partStr = "VR:wt.part.WTPart:300149";
		WTPart part = (WTPart) GenericUtil.getInstance(partStr);
		QueryResult queryResult=ChangeHelper2.service.getAffectingChangeActivities(part);
		System.out.println("Queryresult=="+queryResult.size());
	}
	
	public static void getRoleFromPromotionNotice(){
		
		String pnOid = "OR:wt.maturity.PromotionNotice:176902";
		PromotionNotice pn = (PromotionNotice)GenericUtil.getInstance(pnOid);
		System.out.println("PN =" + pn.getName());
		System.out.println(WorkflowUtil.getUserOnRole((LifeCycleManaged)pn, RoleName.PMC));
		
		
		
	}
	public static void CreatePromotionTarget()
	{
		String pnOid = "OR:wt.maturity.PromotionNotice:298138";
		PromotionNotice pn = (PromotionNotice)GenericUtil.getInstance(pnOid);
		System.out.println("PN =" + pn.getName());
		String partStr = "VR:wt.part.WTPart:300123";
		WTPart part = (WTPart) GenericUtil.getInstance(partStr);
		System.out.println("part number ="+part.getNumber());
		try {
			PromotionTarget promotionTarget=PromotionTarget.newPromotionTarget(pn, part);
			System.out.println("promotionTarget =="+promotionTarget.getRoleAObject());
			System.out.println("promotionTarget =="+promotionTarget.getRoleBObject());
			PersistenceServerHelper.manager.insert(promotionTarget);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			System.out.println("create promotion tartget failed!");
			e.printStackTrace();
		}
		QueryResult result=null;
		try {
			result = MaturityHelper.service.getPromotionTargets(pn);
		} catch (MaturityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("promotion targets result.size()=="+result.size());
		while (result.hasMoreElements()) {
			Object object = (Object) result.nextElement();
			String number=BomWfUtil.getObjectnumber((Persistable) object);
			System.out.println("number==="+number);
			
		}
	}
	public static void refreshPromotionTargets()
			throws WTException {
		String pnOid = "OR:wt.maturity.PromotionNotice:298138";
		PromotionNotice pn = (PromotionNotice)GenericUtil.getInstance(pnOid);
		System.out.println("PN =" + pn.getName());
		
		try {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			System.out.println("qr.size==="+qr.size());
			WTSet old_set = new WTHashSet();
			WTSet new_set = new WTHashSet();
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				old_set.add(obj);
					if (obj instanceof WTPart) {
						WTPart oldpart=(WTPart)obj;
                        WTPart newPart =PartUtil.getLastestWTPartByNumber(oldpart.getNumber());
						new_set.add(newPart);
					}if(obj instanceof WTDocument)
					{
						WTDocument olddoc=(WTDocument)obj;
						WTDocument newdoc =DocUtil.getLatestWTDocument(olddoc.getNumber());
						new_set.add(newdoc);
					}
					if(obj instanceof EPMDocument)
					{
						EPMDocument epmdoc=(EPMDocument)obj;
						EPMDocument newepmdoc =DocUtil.getLastestEPMDocumentByNumber(epmdoc.getNumber());
						new_set.add(newepmdoc);
					}
					else {
						new_set.add(obj);
					}
				
			}
			wt.maturity.MaturityBaseline bl = pn.getConfiguration();
			System.out.println("start to delete----->");
			MaturityHelper.service.deletePromotionTargets(pn, old_set);
			BaselineHelper.service.removeFromBaseline(old_set, bl);
			old_set = null;
			
			System.out.println("start to add new ----->");
			MaturityHelper.service.savePromotionTargets(pn, new_set);
			BaselineHelper.service.addToBaseline(new_set, bl);
			System.out.println("end to add----->");
			new_set = null;

		} catch (Exception _wte) {
          log.debug(pn.getNumber()+"refresh pn EXception!!");
		}
	}
    public static void SystemoutMessage()
    {
    	System.out.println("-----start--------------->>>>>");
    	log.debug("-----------------------------start--------------->>>>>");
    }

}
