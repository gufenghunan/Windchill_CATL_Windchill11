package com.catl.integration.rdm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.fc.ObjectIdentifier;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.OrderByExpression;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.catl.common.util.PartUtil;
import com.catl.integration.rdm.bean.RDMTaskBean;
import com.catl.integration.rdm.bean.RDMWorkflowBean;
import com.catl.integration.webservice.rdm.DeliverableDocumentLinkInfo;
import com.infoengine.object.factory.Att;
import com.infoengine.object.factory.Element;
import com.infoengine.object.factory.Group;

public class RdmIntegrationHelper implements RemoteAccess {
    static String rdmhost = "";

    static String rdmUser = "";

    static String rdmPassword = "";

    /**
     * RDM db properties
     */
    static String RDM_HOST = "";

    static String RDM_SERVICE_NAME = "";

    static String RDM_DB_USER = "";

    static String RDM_DB_PASSWORD = "";

    static String RDM_DB_URL = "";

    private static final String CLASSNAME = RdmIntegrationHelper.class.getName();

    private static final Logger logger = LogR.getLogger(CLASSNAME);
    static {
        Properties props = null;
        try {
            props = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (props == null) {
            logger.debug("properties file is null");
        }
        rdmhost = props.getProperty("rdm.address");
        rdmUser = props.getProperty("rdm.integration.user");
        rdmPassword = props.getProperty("rdm.integration.password");

        RDM_HOST = props.getProperty("rdm.db.host");
        RDM_SERVICE_NAME = props.getProperty("rdm.db.servicename");
        RDM_DB_USER = props.getProperty("rdm.db.user");
        RDM_DB_PASSWORD = props.getProperty("rdm.db.password");

        RDM_DB_URL = props.getProperty("rdm.db.dburl");
        logger.debug(">>>>>rdmaddress:" + rdmhost);
    }

    public static String getRDMHost() {
        return rdmhost;
    }

    public static String getRDMUser() {
        return rdmUser;
    }

    public static String getRDMPassword() {
        return rdmPassword;
    }

    public static PDMLinkProduct getPDMLinkProduct(String prdName) throws WTException {
        PDMLinkProduct product = null;
        QuerySpec qus = new QuerySpec(PDMLinkProduct.class);
        SearchCondition sec = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, prdName, false);
        qus.appendSearchCondition(sec);
        ClassAttribute clsAttr = new ClassAttribute(PDMLinkProduct.class, PDMLinkProduct.MODIFY_TIMESTAMP);
        OrderBy order = new OrderBy((OrderByExpression) clsAttr, true);
        qus.appendOrderBy(order);
        QueryResult qur = PersistenceHelper.manager.find(qus);
        if (qur.hasMoreElements()) {
            product = (PDMLinkProduct) qur.nextElement();
        }
        return product;
    }

    public static WTDocument getDocByBranchId(Long ufid) throws WTException {
        WTDocument doc = null;
        QuerySpec qus = new QuerySpec(WTDocument.class);
        SearchCondition sec = new SearchCondition(WTDocument.class, "iterationInfo.branchId", SearchCondition.EQUAL, ufid);
        qus.appendSearchCondition(sec);
        QueryResult qur = PersistenceHelper.manager.find(qus);
        LatestConfigSpec cfg = new LatestConfigSpec();
        QueryResult qr = cfg.process(qur);
        if (qr.hasMoreElements()) {
            doc = (WTDocument) qr.nextElement();
        }
        return doc;
    }

    public static Persistable getObjectWithBranchId(Class<? extends Persistable> className, String branchId) throws WTException {

        QuerySpec objectsWithIdsSpec = new QuerySpec(className);
        SearchCondition idsCondition = new SearchCondition(className, "iterationInfo.branchId", SearchCondition.EQUAL, Long.valueOf(branchId));
        objectsWithIdsSpec.appendWhere(idsCondition, new int[] { 0 });

        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) objectsWithIdsSpec);
        while (queryResult.hasMoreElements()) {
            return (Persistable) queryResult.nextElement();
        }
        return null;
    }

    public static Persistable getWTPartWithMasterId(String masterId) throws WTException {
        if (masterId != null && masterId.length() > 0) {
            masterId = "wt.part.WTPartMaster:" + masterId;
            WTPartMaster master = (WTPartMaster) PartUtil.getPersistableByOid(masterId);
            if (master != null) {
                WTPart part = (WTPart)PartUtil.getLastestWTPartByNumber(master.getNumber());
                if (part != null) {
                    return part;
                }
            }
        }
        return null;
    }

    public static String replaceXMLChars(String str) {
        if (str == null || str.length() == 0)
            return "";

        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("'", "&apos;");
        str = str.replaceAll("\"", "&quot;");

        return str;
    }

  

    // public static void testJson() {
    // ObjectMapper om = new ObjectMapper();
    // }

    public static Group getProductID(String name, Group output) {
        PDMLinkProduct product;
        try {
            product = getPDMLinkProduct(name);

            if (product == null) {
                Element outputelem = new Element();
                outputelem.addAtt(new Att("result", "error"));
                outputelem.addAtt(new Att("message", "Product doesn't existing"));
                output.addElement(outputelem);
            } else {
                Element outputelem = new Element();
                outputelem.addAtt(new Att("result", "ok"));
                outputelem.addAtt(new Att("data", "[{\"id\":\"" + product.getIdentity() + "\"}]"));
                output.addElement(outputelem);
            }
        } catch (WTException e) {
            Element outputelem = new Element();
            outputelem.addAtt(new Att("result", "error"));
            outputelem.addAtt(new Att("message", e.toString()));
            output.addElement(outputelem);
        }

        return output;
    }

    public boolean updateRDMTasks(WTDocument doc) {
        boolean ret = false;
        try {
            WTDocument firstDoc = (WTDocument) RdmIntegrationHelper.getFirstObject((Master) doc.getMaster());
            if (firstDoc == null) {
                return false;
            }
            try {
                ArrayList<String> idlist = getRDMTasksByDocId(firstDoc.getBranchIdentifier());
                if (idlist != null && idlist.size() > 0) {

                } else {
                    ret = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ret = false;
            }
        } catch (WTException e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    public ArrayList<String> getRDMTasksByDocId(Long branchId) throws Exception {
        ArrayList<String> arr = new ArrayList<String>();
        Connection conn = getConnection();
        if (conn == null) {
            throw new Exception("Cannot access to interface");
        }
        Statement oracle_stmt = null;
        oracle_stmt = conn.createStatement();
        String sql = "Select * from wc_rdm_document where wcobjectid = " + branchId.toString();
        ResultSet rs = oracle_stmt.executeQuery(sql);
        while (rs.next()) {
            String rdmid = (String) rs.getString("rdobjectid");
            if (rdmid != null && rdmid.length() > 0) {
                arr.add(rdmid);
            }
        }
        oracle_stmt.close();
        conn.close();
        oracle_stmt = null;
        conn = null;
        return arr;
    }

    // test
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection oracle_conn = null;

        Class.forName("oracle.jdbc.driver.OracleDriver");
        // oracle_conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.215:1521:wind", "rdm", "rdm");
        // jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=10.16.66.20)(PORT=1601))(CONNECT_DATA=(SERVICE_NAME=mdrdm)))
        // String dbUrl =DSProperties.JDBC_URL + "@" + RDM_HOST+":"+RDM_SERVICE_NAME;
        String dbUrl = RDM_DB_URL;
        logger.debug("message RDM DB:" + dbUrl);
        oracle_conn = DriverManager.getConnection(dbUrl, RDM_DB_USER, RDM_DB_PASSWORD);
        return oracle_conn;
    }

    public static RevisionControlled getFirstObject(Master master) throws WTException {
        RevisionControlled rc = null;
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
        while (queryResult.hasMoreElements()) {
            rc = ((RevisionControlled) queryResult.nextElement());
        }
        return rc;
    }

    /***
     * If type is "MASTER", the vec id should be master oids; If type is "BRANCH", the vec id should be branch oids; If
     * type is "ITERATION", the vec id should be iteration oids;
     * 
     * @param vec
     *            ID List
     * @param output
     *            return group
     * @param type
     *            should be "MASTER", "BRANCH", "ITERATION"
     * @return
     */
    public static Group getProductPropertiesById(Vector<String> vec, Group output, String type) {
        if (!RemoteMethodServer.ServerFlag) {
            try {
                return (Group) RemoteMethodServer.getDefault().invoke("getProductPropertiesById", RdmIntegrationHelper.class.getName(), null, new Class[] { Vector.class, Group.class, String.class },
                        new Object[] { vec, output, type });
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        boolean enforce = false;
        if (vec == null || vec.size() == 0) {
            Element outputelem = new Element();
            outputelem.addAtt(new Att("result", "error"));
            outputelem.addAtt(new Att("message", "Parameter is null"));
            output.addElement(outputelem);
        } else if (!type.equals("BRANCH") && !type.equals("MASTER") && !type.equals("ITERATION")) {
            Element outputelem = new Element();
            outputelem.addAtt(new Att("result", "error"));
            outputelem.addAtt(new Att("message", "Type is not valid"));
            output.addElement(outputelem);
        } else {
            Log.debug(">>>>>>>>>>>>vec size:" + vec.size());
            Element outputelem = new Element();
            String result = "";
            String wrongId = "";
            Vector<String> ret = new Vector<String>();
            JSONObject sinObj = null;
            for (String oid : vec) {
                WTPart part;
                try {
                    enforce = SessionServerHelper.manager.setAccessEnforced(false);
                    Persistable per = null;
                    if (type.equals("ITERATION")) {
                        oid = "wt.part.WTPart:" + oid;
                        Log.debug(">>>>>>>>>>>>oid:" + oid);
                        System.out.println(">>>>>>>>>>>>oid:" + oid);
                        // get part by iteration id
                        per =PartUtil.getPersistableByOid(oid);
                    } else if (type.equals("MASTER")) {
                        // get latest part by master idA2A2
                        per = getWTPartWithMasterId(oid);
                    } else {
                        // get latest part by branch id of that version
                        per = getObjectWithBranchId(WTPart.class, oid);
                    }
                    part = (WTPart) per;
                    String softTypeKey = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
                    if (part == null || softTypeKey.indexOf(MPLDefConstant.PRODUCT) == -1) {
                        wrongId = (wrongId.length() > 0 ? wrongId + "," + oid : oid);
                    } else {
                        //sinObj = getBackProductAttrJSON(part);

                        ret.add(replaceXMLChars(sinObj.toJSONString()));
                        Log.debug(">>>>>>>>sinObj.toJSONString():" + sinObj.toJSONString());
                        System.out.println(">>>>>>>>sinObj.toJSONString():" + sinObj.toJSONString());
                    }

                } catch (NumberFormatException e) {
                    outputelem.addAtt(new Att("result", "error"));
                    outputelem.addAtt(new Att("message", e.toString()));
                    output.addElement(outputelem);
                    e.printStackTrace();
                } catch (WTException e) {
                    outputelem.addAtt(new Att("result", "error"));
                    outputelem.addAtt(new Att("message", e.toString()));
                    output.addElement(outputelem);
                    e.printStackTrace();
                
                } catch (RemoteException e) {
                    outputelem.addAtt(new Att("result", "error"));
                    outputelem.addAtt(new Att("message", e.toString()));
                    output.addElement(outputelem);
                    e.printStackTrace();
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(enforce);
                }
            }
            if (wrongId.length() > 0) {
                outputelem.addAtt(new Att("result", "error"));
                outputelem.addAtt(new Att("message", "Parameter is wrong. [" + wrongId + "]"));
                output.addElement(outputelem);
            } else {
                if (ret.size() > 0) {
                    for (String data : ret) {
                        result = result + (result.length() > 0 ? "," : "") + data;
                    }

                    outputelem.addAtt(new Att("result", "ok"));
                    outputelem.addAtt(new Att("data", "[" + result + "]"));
                    output.addElement(outputelem);
                }
            }
        }
        logger.debug(">>>>>>>>>>>output:" + output.getElementList());
        return output;
    }

    public static String getBlankIfNull(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

   

  


    private static String getRDMToken() throws IOException {
        logger.debug(">>>>>getRDMToken involved!");
        String token = "";

        String urlStr = rdmhost + "/user/login.wbs?username=" + rdmUser + "&password=" + rdmPassword;
        logger.debug("urlStr" + urlStr);
        token = RDMMenuUtil.getHTMLContent(urlStr);
        if (token == null || token.length() == 0)
            return "";

        return token;
    }

    public static List<RDMTaskBean> getRDMToDoTasks() throws IOException, WTException, JSONException {
        logger.debug(">>>>>getRDMToDoTasks involved!");
        List<RDMTaskBean> list = new ArrayList<RDMTaskBean>();

        String token = getRDMToken();
        if (token == null || token.length() == 0) {
            logger.debug("cannot get token");
        }
        logger.debug("getRDMToDoTasks token is:" + token);
        String userName = "";
        WTPrincipal currentUser = (WTUser) wt.session.SessionHelper.manager.getPrincipal();
        userName = currentUser.getName();
        String urlStr = rdmhost + "/task/getReceive.wbs?token=" + token + "&username=" + userName;

        String tasks = RDMMenuUtil.getHTMLContent(urlStr);
        if (tasks == null || tasks.length() == 0) {
            logger.debug("don't have tasks");
            return null;
        }
        JSONObject jsonObj = new JSONObject(tasks);

        if (tasks.startsWith("{'error'")) {
            logger.debug("error in JSON string returned");
            return null;
        }

        String tasksArr = jsonObj.getString("Task");
        if (tasksArr == null || tasksArr.length() == 0) {
            logger.debug("don't have tasks also");
            return null;
        }
        if (tasksArr.startsWith("{")) {
            tasksArr = "[" + tasksArr + "]";
        }
        JSONArray jsonarray = new JSONArray(tasksArr);
        if (jsonarray == null || jsonarray.length() == 0) {
            logger.debug("don't have tasks also");
            return null;
        }
        RDMTaskBean taskBean = null;
        for (int i = 0; i < jsonarray.length(); i++) {
            String id = jsonarray.getJSONObject(i).getString("ID");
            String name = jsonarray.getJSONObject(i).getString("Name");
            String state = jsonarray.getJSONObject(i).getString("Status");
            String planStartDate = jsonarray.getJSONObject(i).getString("PlanStartDate");
            String planEndDate = jsonarray.getJSONObject(i).getString("PlanEndDate");
            String object = jsonarray.getJSONObject(i).getString("Object");// add by chenjianbo for 显示项目名称 列
            // String url = rdmhost + "/mailLinked?workflowType=" + "TSK" + "&objectId=" + id;
            String url = rdmhost + Constant.RDM_WC_LINK + "username=" + userName + "&page=" + "/mailLinked?workflowType=" + "TSK" + "&objectId=" + id;

            taskBean = new RDMTaskBean();
            taskBean.setName(name);
            taskBean.setPlanStartDate(planStartDate);
            taskBean.setPlanEndDate(planEndDate);
            taskBean.setState(state);
            taskBean.setExternalURL(url);
            taskBean.setObject(object);
            list.add(taskBean);
        }

        logger.debug(">>>>>getRDMToDoTasks ended!");
        return list;
    }

    public static List<RDMWorkflowBean> getRDMWorkflows() throws IOException, WTException, JSONException {
        logger.debug(">>>>>getRDMWorkflows involved!");
        List<RDMWorkflowBean> list = new ArrayList<RDMWorkflowBean>();

        String token = getRDMToken();
        if (token == null || token.length() == 0) {
            logger.debug("cannot get token");
        }
        logger.debug("getRDMWorkflows token is:" + token);
        String userName = "";
        WTPrincipal currentUser = (WTUser) wt.session.SessionHelper.manager.getPrincipal();
        userName = currentUser.getName();
        String urlStr = rdmhost + "/workflow/getReceive.wbs?token=" + token + "&username=" + userName;

        String tasks = RDMMenuUtil.getHTMLContent(urlStr);
        if (tasks == null || tasks.length() == 0) {
            logger.debug("don't have workflows");
            return null;
        }
        JSONObject jsonObj = new JSONObject(tasks);

        if (tasks.startsWith("{'error'")) {
            logger.debug("error in JSON string returned");
            return null;
        }

        String tasksArr = jsonObj.getString("WF");
        if (tasksArr == null || tasksArr.length() == 0) {
            logger.debug("don't have workflows also");
            return null;
        }
        if (tasksArr.startsWith("{")) {
            tasksArr = "[" + tasksArr + "]";
        }
        JSONArray jsonarray = new JSONArray(tasksArr);
        if (jsonarray == null || jsonarray.length() == 0) {
            logger.debug("don't have workflows also");
            return null;
        }
        logger.debug(">>>>>tasksArr" + tasksArr);
        RDMWorkflowBean workflowBean = null;
        for (int i = 0; i < jsonarray.length(); i++) {
            String id = jsonarray.getJSONObject(i).getString("ID");
            String name = jsonarray.getJSONObject(i).getString("Name");
            String state = jsonarray.getJSONObject(i).getString("StatusName");
            String typeName = jsonarray.getJSONObject(i).getString("TypeName");
            String project = jsonarray.getJSONObject(i).getString("Project");
            String code = jsonarray.getJSONObject(i).getString("Code");
            String type = jsonarray.getJSONObject(i).getString("Type");
            // String url = rdmhost + "/mailLinked?workflowType=" + type + "&objectId=" + id;
            String url = rdmhost + Constant.RDM_WC_LINK + "username=" + userName + "&page=" + "/mailLinked?workflowType=" + type + "&objectId=" + id;
            workflowBean = new RDMWorkflowBean();
            workflowBean.setName(name);
            workflowBean.setState(state);
            workflowBean.setTypeName(typeName);
            workflowBean.setCode(code);
            workflowBean.setProject(project);
            workflowBean.setExternalURL(url);

            list.add(workflowBean);
        }

        logger.debug(">>>>>getRDMWorkflows ended!");
        return list;
    }






    /**
     * 根据项目号和文档名称查找文档
     * 
     * @param output
     * @return
     */
    public static Group getProjectDocuments(Group output) {
        Element outputelem = new Element();
        String result = "";
        String wrongId = "";
        Vector<String> ret = new Vector<String>();

        System.out.println("getElementCount" + output.getElementCount());
        try {
            for (int i = 0; i < output.getElementCount(); i++) {
                Element element = output.getElementAt(i);

                String number = (String) element.getAtt("number").getValue().toString();
                String name = (String) element.getAtt("name").getValue().toString();
                String obid = (String) element.getAtt("obid").getValue().toString();
                String state = (String) element.getAtt("state.state").getValue().toString();
                if (state != null && (state.equalsIgnoreCase("DESIGNRELEASE") || state.equalsIgnoreCase("MANUFACTURERELEASE") || state.equalsIgnoreCase("RELEASED"))) {
                    State states = State.toState(state);
                    JSONObject sinObj = new JSONObject();
                    sinObj.put("number", number);
                    sinObj.put("name", name);
                    sinObj.put("obid", obid);
                    sinObj.put("status", states.getDisplay(Locale.SIMPLIFIED_CHINESE));
                    ret.add(replaceXMLChars(sinObj.toJSONString()));
                }

            }

        } catch (NumberFormatException e) {
            outputelem.addAtt(new Att("result", "error"));
            outputelem.addAtt(new Att("message", e.toString()));
            output.addElement(outputelem);
            e.printStackTrace();
        } catch (JSONException e) {
            outputelem.addAtt(new Att("result", "error"));
            outputelem.addAtt(new Att("message", e.toString()));
            output.addElement(outputelem);
            e.printStackTrace();
            e.printStackTrace();
        }
        if (wrongId.length() > 0) {
            outputelem.addAtt(new Att("result", "error"));
            outputelem.addAtt(new Att("message", "Parameter is wrong. [" + wrongId + "]"));
            output.addElement(outputelem);
        } else {
            if (ret.size() > 0) {
                for (String data : ret) {
                    result = result + (result.length() > 0 ? "," : "") + data;
                }
                outputelem.addAtt(new Att("result", "ok"));
                outputelem.addAtt(new Att("data", "[" + result + "]"));
                output = new Group("output");
                output.addElement(outputelem);
            } else {
                outputelem.addAtt(new Att("result", "ok"));
                outputelem.addAtt(new Att("data", result));
                output = new Group("output");
                output.addElement(outputelem);
            }
        }

        return output;
    }


    /**
     * 反馈项目文档状态 http://localhost:8090/power/wcDocument/updateDocStatus.wbs updateDocStatusToRDM
     * 
     * @throws WTException
     * @throws IOException
     */
    public static void updateDocStatusToRDM(WTObject primaryBusinessObject) throws WTException, IOException {

        logger.debug(">>>>>updateDocStatus involved!");

        String token = "";
        if (token == null || token.length() == 0) {
            logger.debug("cannot get token");
        }
        logger.debug("updateDocStatus token is:" + token);
        if (primaryBusinessObject instanceof PromotionNotice) {
            PromotionNotice promotionnotice = (wt.maturity.PromotionNotice) primaryBusinessObject;
            QueryResult qr = MaturityHelper.service.getPromotionTargets(promotionnotice);
            while (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTDocument) {
                    WTDocument wtdoc = (WTDocument) obj;
                    callRDMUpdateDocStatus(wtdoc, token);
                }
            }
        } else if (primaryBusinessObject instanceof WTDocument) {
            WTDocument wtdoc = (WTDocument) primaryBusinessObject;
            callRDMUpdateDocStatus(wtdoc, token);
        }
        logger.debug("======== updateDocStatus end ========= ");
    }

    private static void callRDMUpdateDocStatus(WTDocument wtdoc, String token) throws IOException {
        logger.debug("======== callRDMUpdateDocStatus start ========= ");
        String state = wtdoc.getState().toString();
        logger.debug(" state : " + state);
        /**add at 20141210 取消文档设计发放状态同步到RDM --DESIGNRELEASE  state.equalsIgnoreCase("DESIGNRELEASE") **/
        if (state.equalsIgnoreCase("DESIGNRELEASE") || state.equalsIgnoreCase("MANUFACTURERELEASE") || state.equalsIgnoreCase("RELEASED")) {
            State states = State.toState(state);
            String strStates = states.getDisplay(Locale.SIMPLIFIED_CHINESE);
            /**
             * add by chenjianbo at 2014-7-24 RDM需要转字符否则有乱码
             */
            // strStates = java.net.URLDecoder.decode(strStates, "UTF-8");
            String urlStr = rdmhost + "/wcDocument/updateDocStatus.wbs?token=" + token + "&wcObjId=" + "wt.doc.WTDocument:" + String.valueOf(wtdoc.getBranchIdentifier()) + "&isThough=Y"
                    + "&statusName=" + URLEncoder.encode(strStates, "UTF-8");//
            logger.debug("======== callRDMUpdateDocStatus URL: " + urlStr);
            String result = RDMMenuUtil.getHTMLContent(urlStr);
            logger.debug("updateDocStatus result:" + result);
        }
    }


    public static String saveIssueData(String keyValue) throws IOException {
        String urlStr = rdmhost + "/wcIssue/saveIssueData.wbs?IdArr=" + keyValue;
        String result = RDMMenuUtil.getHTMLContent(urlStr);
        return result;
    }

    public static String queryIssue(String bugId) throws IOException {
        String urlStr = rdmhost + "/wcIssue/queryBug.wbs?bugId=" + bugId;
        String result = RDMMenuUtil.getHTMLContent(urlStr);
        return result;
    }

    public static String queryProject(String code) throws IOException {
        String urlStr = rdmhost + "/wcIssue/queryProject.wbs?code=" + code;
        String result = RDMMenuUtil.getHTMLContent(urlStr);
        return result;
    }

    public static String saveIssueReport(String keyValue) throws IOException {
        String urlStr = rdmhost + "/wcDocument/saveWcRelate.wbs?token=&delivParam=" + URLEncoder.encode(keyValue, "UTF-8");
        System.out.println(urlStr);
        String result = RDMMenuUtil.getHTMLContent(urlStr);
        System.out.println(result);
        return result;
    }
    
    /** add by zhengjiahong start**/
    /**
     * RDM调用--是否存在文件夹
     * @param projectCode
     * @param projectName
     * @return
     * @throws WTException
     * @throws UnsupportedEncodingException
     */
    public static boolean existFolder(String projectCode, String projectName) throws WTException, UnsupportedEncodingException {
        QuerySpec qs = new QuerySpec(SubFolder.class);
        qs.appendWhere(new SearchCondition(SubFolder.class,SubFolder.NAME,SearchCondition.EQUAL,projectCode+"("+projectName+")"),new int[]{0});
        
        QueryResult qr = PersistenceHelper.manager.find(qs);
        if(qr.size()>0)
        	return true;
        
        return false;
    }
    /**
     * 查询RDM创建文档需要的   文件夹Oid&容器Oid
     * @param projectCode
     * @param projectName
     * @param docType
     * @return
     * @throws WTException
     * @throws UnsupportedEncodingException
     */
    public static String[] queryFolderOidAndContanierOid(String projectCode, String projectName,String docType) throws WTException, UnsupportedEncodingException {
    	String[] ret= new String[2];
        QuerySpec qs = new QuerySpec(SubFolder.class);
        qs.appendWhere(new SearchCondition(SubFolder.class,SubFolder.NAME,SearchCondition.EQUAL,projectCode+"("+projectName+")"),new int[]{0});
        
        QueryResult qr = PersistenceHelper.manager.find(qs);
        while(qr.hasMoreElements()){
        	SubFolder folder = (SubFolder)qr.nextElement(); 
        	WTContainer container = folder.getContainer();
        	ret[0] = "ContainerOid=OR%3A"+container.toString().replaceAll(":", "%3A");
            QueryResult subFolders = FolderHelper.service.findSubFolders(folder);
            while(subFolders.hasMoreElements()){
            	SubFolder subFolder = (SubFolder)subFolders.nextElement();
            	String subFolderName = subFolder.getName();
            	if(subFolderName.equals("产品资料")){
            		QueryResult subSubFolders = FolderHelper.service.findSubFolders(subFolder);
            		while(subSubFolders.hasMoreElements()){
                    	SubFolder subSubFolder = (SubFolder)subSubFolders.nextElement();
                    	String subSubFolderName = subSubFolder.getName();
                    	if(subSubFolderName.equals(docType)){
                    		ret[1] = "context=comp%24folderbrowser_table%24OR%3A"+subSubFolder.getPersistInfo().getObjectIdentifier().toString().replaceAll(":", "%3A")+"%24&oid=OR%3A"+subSubFolder.getPersistInfo().getObjectIdentifier().toString().replaceAll(":", "%3A");
                    		return ret;
                    	}
            		}
            	}
            }
        }
        return null;
    }
    /**
     * 发送文档ID与RDM交付物ID 关联关系到RDM
     * @param deliverableId
     * @param docId
     * @param docBranchId
     * @param docRealName
     * @return
     * @throws IOException
     */
    public static String receiveCreateStatusToRDM(Map<String,String> map) throws IOException{
    	String deliverableId = map.get("deliverableId");
    	String docNumber = map.get("docNumber");
    	String docBranchId = map.get("docbranchId");
    	String docName = map.get("docName");
    	String urlStr = rdmhost + "/wsWindchillTaskDocument/receiveCreateStatus.wbs?deliverableId=" + URLEncoder.encode(deliverableId, "UTF-8")+"&docId=" + URLEncoder.encode(docNumber, "UTF-8")+"&docBranchId=" + URLEncoder.encode(docBranchId, "UTF-8")+"&docRealName=" + URLEncoder.encode(docName, "UTF-8");
        String result = RDMMenuUtil.getHTMLContent(urlStr);
        return result;
    }
    /**
     * 发送文档状态到RDM
     * @param deliverableId
     * @param docId
     * @param docBranchId
     * @param docRealName
     * @param state
     * @return
     * @throws IOException
     */
    public static String updateDocumentStatusToRDM(Map<String,String> map) throws IOException{
    	String deliverableId = map.get("deliverableId");
    	String docNumber = map.get("docNumber");
    	String docBranchId = map.get("branchId");
    	String state = map.get("state");
    	String urlStr = rdmhost + "/wsWindchillTaskDocument/updateDocumentStatus.wbs?deliverableId=" + URLEncoder.encode(deliverableId, "UTF-8")+"&docId=" + URLEncoder.encode(docNumber, "UTF-8")+"&docBranchId=" + URLEncoder.encode(docBranchId, "UTF-8")+"&docStatus=" + URLEncoder.encode(state, "UTF-8");
        String result = RDMMenuUtil.getHTMLContent(urlStr);
        return result;
    }
    public static String deleteDocumentLinkToRDM(Map<String,String> map) throws IOException{
    	String deliverableId = map.get("deliverableId");
    	String docNumber = map.get("docNumber");
    	String docBranchId = map.get("branchId");
    	String urlStr = rdmhost + "/wsWindchillTaskDocument/deleteDocumentLink.wbs?deliverableId=" + URLEncoder.encode(deliverableId, "UTF-8")+"&docId=" + URLEncoder.encode(docNumber, "UTF-8")+"&docBranchId=" + URLEncoder.encode(docBranchId, "UTF-8");
        String result = RDMMenuUtil.getHTMLContent(urlStr);
        return result;
    }
    /**
     * 查询RDM与文档关联对象
     * @param map
     * @return
     * @throws WTException
     */
    public static ObjectLinkedByRdm queryObjectLinkedByRdm(Map<String,String> map) throws WTException{
    	QuerySpec query = new QuerySpec(ObjectLinkedByRdm.class);
    	
		String docNumber = map.get("docNumber");
    	if(docNumber != null && !docNumber.equals(""))
    		query.appendWhere(new SearchCondition(ObjectLinkedByRdm.class,ObjectLinkedByRdm.OBJECT_NUMBER,SearchCondition.EQUAL,docNumber));
    	
    	String docType = map.get("docType");
    	if(docType != null && !docType.equals("")){
    		if(query.getConditionCount() > 0)
        		query.appendAnd();
    		query.appendWhere(new SearchCondition(ObjectLinkedByRdm.class,ObjectLinkedByRdm.OBJECT_TYPE,SearchCondition.EQUAL,docType));
    	}
    	
    	String branchId = map.get("branchId");
    	if(branchId != null && !branchId.equals("")){
    		if(query.getConditionCount() > 0)
        		query.appendAnd();
    		query.appendWhere(new SearchCondition(ObjectLinkedByRdm.class,ObjectLinkedByRdm.BRANCH_ID,SearchCondition.EQUAL,Long.parseLong(branchId)));
    	}
    		
    	String deliverableId = map.get("deliverableId");
    	if(deliverableId != null && !deliverableId.equals("")){
    		if(query.getConditionCount() > 0)
        		query.appendAnd();
    		query.appendWhere(new SearchCondition(ObjectLinkedByRdm.class,ObjectLinkedByRdm.DELIVERABLE_ID,SearchCondition.EQUAL,deliverableId));
    	}
    	
    	QueryResult result = PersistenceHelper.manager.find(query);
    	if(result.hasMoreElements()){
    		return (ObjectLinkedByRdm)result.nextElement();
    	}
    	return null;
    }
    /** add by zhengjiahong end**/
}
