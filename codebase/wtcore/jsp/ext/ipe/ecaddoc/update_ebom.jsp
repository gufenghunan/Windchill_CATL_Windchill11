

<%@ page import="ext.ipe.wtseeintegration.utils.CMXWTHelper" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Iterator" %>

    <%  
	String message="";
	try {
		CMXWTHelper helper = new CMXWTHelper();
		String oid = request.getHeader("oid");
		String modified = request.getHeader("modified");
		System.out.println("oid="+oid+"&modified="+modified);
		//wt.doc.WTDocument wtdoc = (wt.doc.WTDocument)new wt.fc.ReferenceFactory().getReference(oid).getObject();
		wt.doc.WTDocument wtdoc = helper.getECADDocAssociated(oid);
        //WTPart zcojf = (WTPart)new wt.fc.ReferenceFactory().getReference(oid).getObject();
		
		java.util.List list = new java.util.ArrayList();
		list.add("XML");
		list.add("xml");
		java.util.Map map = helper.downloadAttachmentsFiltered(wtdoc,list);
		if (map.isEmpty()) {
			System.out.println("El documento no tiene un XML asociado");
			message="El documento no tiene un XML asociado";
		}
		if (message.trim().length() == 0){
			Iterator iterator = map.values().iterator();
			while (iterator.hasNext()) {
				String doc = (String)iterator.next();
				java.io.File file = new java.io.File(doc);
				if (!file.exists()) {
					System.out.println("No se ha podido encontrar el documento " + doc);
					message="No se ha podido encontrar el documento " + doc;
					break;
				}
				java.util.Map<String, java.util.ArrayList<String[]>> mapelements = helper.readXML(doc);
				String projectname = ext.ipe.wtseeintegration.utils.ComexiXMLHandler.readProjectName(doc);
				if (projectname.trim().length() == 0){
					System.out.println("No se ha encontrado el nombre de proyecto en el documento XML");
					message="No se ha encontrado el nombre de proyecto en el documento XML";
					break;
				}
				java.util.Map<String, java.util.Vector> retmap = helper.getRelatedParts(mapelements);
				//helper.updateECADStructure(oid, retmap, modified);
				
				String wrongstate = helper.checkModifiedState(oid, retmap, modified, projectname);
				if (wrongstate.trim().length() != 0){
					message= "Accion no permitida debido a que los siguientes conjuntos estan en estado Liberado o Aprobado: "+ wrongstate;
					break;
				}
				
				helper.updateECADStructure(oid, retmap, modified, projectname);
			}
		}
	} catch (Exception e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
		message=e.getMessage();
	}
	if (message.trim().length() == 0){
			message="Estructura actualizada correctamente";
		}
	else {
		message="Error al generar la estructura:\n"+message;
	}
	  out.println(message);

    %>  

