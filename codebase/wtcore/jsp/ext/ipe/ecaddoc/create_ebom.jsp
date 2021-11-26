<%@ page import="ext.ipe.wtseeintegration.utils.CMXWTHelper" %>




    <%  
	
	CMXWTHelper helper = new CMXWTHelper();
	String oid = request.getHeader("oid");

	//if(!helper.hasWTPartAssociated(oid)){
	String message = helper.hasECADDocAssociated(oid);
	if(message.trim().length() == 0){
		message = helper.doOperation(oid);
		if (message.trim().length() == 0){
			message="BOM has been created successfully";
		}
		else 
			message="Error, BOM hasn't been created:\n"+message;
	}
	out.println(message);
    %>  
	

