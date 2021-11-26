
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
 <div class="centeredImage"  id="loading"><h1>Actualizando estructura electrica...</h1> </div>
 

    <%  
	String message="";
	String oid = request.getParameter("oid");
	String modified = request.getParameter("modified");
		
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
		opener.location.reload(); // or opener.location.href = opener.location.href;
		window.close(); // or self.close();
	}

    window.onload = doUpload();  // invoke pageLoad after all content is loaded
	
	
	function doUpload() {
	
	var oid='<%=oid%>';
	var modified='<%=modified%>';

		if (window.XMLHttpRequest){
			var xhr = new XMLHttpRequest();
			xhr.open("GET", "wtcore/jsp/ext/ipe/ecaddoc/update_ebom.jsp",true);
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
			xhr.setRequestHeader("modified", modified);
			xhr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT"); 
			
			//Initiate upload
			xhr.send(null);
		}
	}

function removeElement(ele) {
    ele.parentNode.removeChild(ele);
}
	
	
</script>	


<%@include file="/netmarkets/jsp/util/end.jspf"%>
