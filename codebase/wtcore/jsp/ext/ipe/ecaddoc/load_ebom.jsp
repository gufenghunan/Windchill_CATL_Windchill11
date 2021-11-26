
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=10" />
<style type="text/css">
  .centeredImage
    {
    text-align:center;
    display:block;
    }
</style>
</head>
<%@ page import="ext.ipe.wtseeintegration.utils.CMXWTHelper" %>

<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>

 <p class="centeredImage" >
 <img id="spinner" src="wtcore/jsp/ext/ipe/ecaddoc/images/ico_loader2.gif" alt="Loading..."> </p>
 <div class="centeredImage"  id="loading"><h1>Creating electric BOM...</h1> </div>
 

    <%  
		CMXWTHelper helper = new CMXWTHelper();
		
		String oid = request.getParameter("oid");
		String hasStruct="true";
		//if (helper.hasWTPartAssociated(oid))
			String message = helper.hasECADDocAssociated(oid);
		if(message.trim().length() != 0){%>
		<script>
			alert('<%=message%>');
			opener.location.reload(); // or opener.location.href = opener.location.href;
		window.close(); // or self.close();
			</script>
			<%
			}
			hasStruct = String.valueOf(helper.hasElectricalStructure(oid));
		System.out.println("hasStruct? "+ hasStruct);
    %>  
	

<script type="text/javascript">

    function pageLoad(text)
    {
		removeElement(document.getElementById("spinner"));
				removeElement(document.getElementById("loading"));

        alert(text.trim());
		
		closeAndRefresh();
    }
	
	function closeAndRefresh(){	
	/*PTC.messaging.showInlineMessage([{
    MessageTitle: 'Sample Message Title',
    Messages: ['Sample Message one.','Sample Message Two'],
    MessageType: 'SUCCESS'
}]);*/
		opener.location.reload(); // or opener.location.href = opener.location.href;
		window.close(); // or self.close();
	}

    window.onload = doUpload();  // invoke pageLoad after all content is loaded

	
	
	function doUpload() {
	
	var oid='<%=oid%>';
	var hasStruct ='<%=hasStruct%>';
	if (hasStruct=='false'){
		if (window.XMLHttpRequest){
			var xhr = new XMLHttpRequest();
			xhr.open("GET", "wtcore/jsp/ext/ipe/ecaddoc/create_ebom.jsp",true);
			xhr.onload = function() {
				if (xhr.status === 200) {
					pageLoad(xhr.responseText);
				}
				else {
					alert('Request failed.  Returned status of ' + xhr.status);
				}
			};
			
			//Set a few headers so we know the file name in the server
			xhr.setRequestHeader("Cache-Control", "no-cache");
			xhr.setRequestHeader("X-Requested-With", "XmlHttpRequest");
			xhr.setRequestHeader("oid", oid);
			xhr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT"); 
			
			//Initiate upload
			xhr.send(null);
		}
	}
	else{
		var nav = navigator.appName;

		if(nav == "Microsoft Internet Explorer"){
			location.href="structure_table.jsp?oid="+oid;
		}
		else{
			location.href="wtcore/jsp/ext/ipe/ecaddoc/structure_table.jsp?oid="+oid;
		}
	}
}
function removeElement(ele) {
    ele.parentNode.removeChild(ele);
}

	
	
	
</script>

<%@include file="/netmarkets/jsp/util/end.jspf"%>
