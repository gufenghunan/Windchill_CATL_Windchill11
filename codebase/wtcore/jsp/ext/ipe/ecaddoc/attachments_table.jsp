<%@ page import="com.ptc.windchill.enterprise.attachments.attachmentsResource" %>
<%@ page import="wt.preference.PreferenceHelper" %>
<%@ page import="wt.preference.PreferenceClient" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="wc" uri="http://www.ptc.com/windchill/taglib/core" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>


<% request.setAttribute(com.ptc.netmarkets.util.misc.NmAction.SHOW_CONTEXT_INFO, "true");

String tempbase = wt.util.WTProperties.getLocalProperties().getProperty("wt.temp");
	wt.org.WTPrincipal currentUser = wt.session.SessionHelper.getPrincipal();

	String tempfolderpath = tempbase+"\\"+currentUser.getName()+ ((new java.util.Date()).getTime());
	tempfolderpath=tempfolderpath.replace("\\","/");
%>
<script>

 document.createElement('header');
 </script>

  <input type="hidden" name="tempfolderpath" value="<%=tempfolderpath%>" />
<b>* Content</b>
<br/>

<div id="subirCarpetaDiv">
<table border=1 width=50% cellspacing="0" class="x-btn  x-btn-icon" id="netmarkets.network.treeP421707191327125_shortcutbar">
	<tr>
		<th>
			<b>Load Project&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>
		</th>
		<th>
			<input type="file" id="folder" name="folder" onchange="subirCarpeta()" webkitdirectory directory/>
		</th>
	</tr>
</table>
<br/>
<br/>
</div>


<table border=1 width=50% cellspacing="0" class="x-btn  x-btn-icon" id="netmarkets.network.treeP421707191327124_shortcutbar">
<thead>
  <tr>
	<th width=20%></th>
    <th width=80%>File Name</th>   
  </tr>
 </thead>
<tbody>
<tr>
<td><b>* Schematic File</b></td>
<!--<td align=center><input width=100% type="file" name="pdffile"> </td>-->
<td>
	<input type="file" id="content" name="content" onchange="doUpload('content','progressContent')" />
	</td>
	</tr>
<tr>
<td/>
	<td>

	<progress id="progressContent" width="100%" max="100" value="0"/>
	<!--<input type="button" value="upload" onclick="doUpload('pdffile')" />-->
	
</td>
</tr>
<tr>
<tr>
<td><b>* Visualization File</b></td>
<!--<td align=center><input width=100% type="file" name="pdffile"> </td>-->
<td>
	<input type="file" id="pdffile" name="pdffile" accept="application/pdf" onchange="doUpload('pdffile','progresspdfBar')"/>
	</td>
	</tr>
<tr>
<td/>
	<td>

	<progress id="progresspdfBar" width="100%" max="100" value="0"/>
	<!--<input type="button" value="upload" onclick="doUpload('pdffile')" />-->
	
</td>
</tr>
<tr>
<td><b>* Cable List</b></td>

<td>
	<input type="file" id="xmlfileCables" name="xmlfileCables" accept="text/xml" onchange="doUpload('xmlfileCables','progressxmlBarCables')"/>
	</td>
	</tr>
<tr>
<td/>

	<td>

		<progress id="progressxmlBarCables" max="100" value="0"/>

	</td>
</tr>

<tr>
<td><b>* Electric Component List</b></td>

<td>
	<input type="file" id="xmlfileComponentes" name="xmlfileComponentes" accept="text/xml" onchange="doUpload('xmlfileComponentes','progressxmlBarComponentes')"/>
	</td>
	</tr>
<tr>
<td/>

	<td>

		<progress id="progressxmlBarComponentes" max="100" value="0"/>

	</td>
</tr>

</tbody></table>

<script>
function comprobarNavegador(){
	var ua = window.navigator.userAgent;
	var msie = ua.indexOf("MSIE ");
	if (msie > 0){
		//si es explorer oculto el de subir porque no funciona
		var folder=document.getElementById('subirCarpetaDiv');
		folder.style.display = 'none'; 
	}	
}
 

var carpetas = new Array('Archive', 'PDF','Reports');

function subirCarpeta(){
	var folder = document.getElementById("folder");
	var fileCount = folder.files.length;
	var contadorReports=0;	
	var contadorValidar=0;
	for (var i = 0; i < fileCount; i++) {
		var rutaFichero=folder.files[i].webkitRelativePath;
		//console.log(rutaFichero);
		rutaFichero=rutaFichero.substring(rutaFichero.indexOf("/")+1);
		
		var carpeta=rutaFichero.substring(0,rutaFichero.indexOf("/"));
		//si es una de las carpetas
		if(carpetas.indexOf(carpeta) > -1){
			if(carpeta=='Archive'){
				var content = document.getElementById("content");
				content.type="text";
				content.value=folder.files[i].name;
				subir('content','progressContent',folder.files[i]);
				contadorValidar++;
			}
			else if(carpeta=='PDF'){
				var content = document.getElementById("pdffile");
				content.type="text";
				content.value=folder.files[i].name;
				subir('pdffile','progresspdfBar',folder.files[i]);
				contadorValidar++;
			}
			else if(carpeta=='Reports'){
				if(contadorReports==0){
					var content = document.getElementById("xmlfileCables");
					content.type="text";
					content.value=folder.files[i].name;
					subir('xmlfileCables','progressxmlBarCables',folder.files[i]);
					contadorValidar++;
				}
				else{
					var content = document.getElementById("xmlfileComponentes");
					content.type="text";
					content.value=folder.files[i].name;
					subir('xmlfileComponentes','progressxmlBarComponentes',folder.files[i]);
					contadorValidar++;
				}
				contadorReports++;
			}
		}
		else{
			console.log(carpeta+ ' no esta incluido');
		}
	}
	if(contadorValidar<4)
		alert('Falta algún fichero');
}
function subir(id,progressbar,file){
	var folder='<%=tempfolderpath%>';
	if (window.XMLHttpRequest){
		var progressBar = document.getElementById(progressbar);
		var xhr = new XMLHttpRequest();
		xhr.open("POST", "wtcore/jsp/ext/ipe/ecaddoc/upload.jsp",false);
		xhr.onload = function() {
			if (xhr.status === 200) {
				//alert('User\'s name is ' + xhr.responseText);
			}
			else {
				alert('Request failed.  Returned status of ' + xhr.status);
			}
		};
		var filename = extractFilename(file.name);
		console.log('filename '+filename);
		//Set a few headers so we know the file name in the server
		xhr.setRequestHeader("Cache-Control", "no-cache");
		xhr.setRequestHeader("X-Requested-With", "XmlHttpRequest");
		xhr.setRequestHeader("X-File-Name", filename);
		xhr.setRequestHeader("folder", folder);
		xhr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT"); 
		
		 xhr.upload.addEventListener("progress", uploadProgress, false);
		
		xhr.onload = function() {
        if (xhr.status == 200) {
          	progressBar.value = 100;

        } else {
            alert("Error! Upload failed");
        }
    };
		//Initiate upload
		    progressBar.value = 0;
		xhr.send(file);
	}else if (window.XDomainRequest) {
		var xdr = new XDomainRequest();
		xdr.onload=function() {
			alert( xdr.responseText);
		}
		/*xdr.setRequestHeader("Cache-Control", "no-cache");
		xdr.setRequestHeader("X-File-Name", file.name);
		xdr.setRequestHeader("folder", folder);
		xdr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT"); */
		xdr.open("POST", "wtcore/jsp/ext/ipe/ecaddoc/upload.jsp");
		xdr.send(file);
	}
	 else
        alert("Cross Domain not supported");
}

function doUpload(id,progressbar) {	
console.log('doUpload '+id+' '+progressbar);
	var folder='<%=tempfolderpath%>';
	//var file = document.getElementById(id).files[0];
	var x = document.getElementById(id);
	var txt="";
	if ('files' in x) {
        if (x.files.length == 0) {
            alert("Select one or more files.");
        } else {
            for (var i = 0; i < x.files.length; i++) {
                txt += "<br><strong>" + (i+1) + ". file</strong><br>";
                var file = x.files[i];
                if ('name' in file) {
                    txt += "name: " + file.name + "<br>";
                }
                if ('size' in file) {
                    txt += "size: " + file.size + " bytes <br>";
                }
            }
        }
    } else{
	            alert("Navegador no compatible. Utilizar Firefox, Chrome o Internet Explorer 10 o superior");

	}
	//var fich= document.getElementById(id).value;
	//var file = getFile(fich);
	if (window.XMLHttpRequest){
		var progressBar = document.getElementById(progressbar);
		var xhr = new XMLHttpRequest();
		xhr.open("POST", "wtcore/jsp/ext/ipe/ecaddoc/upload.jsp",false);
		xhr.onload = function() {
			if (xhr.status === 200) {
				//alert('User\'s name is ' + xhr.responseText);
			}
			else {
				alert('Request failed.  Returned status of ' + xhr.status);
			}
		};
		var filename = extractFilename(file.name);
		//Set a few headers so we know the file name in the server
		xhr.setRequestHeader("Cache-Control", "no-cache");
		xhr.setRequestHeader("X-Requested-With", "XmlHttpRequest");
		xhr.setRequestHeader("X-File-Name", filename);
		xhr.setRequestHeader("folder", folder);
		xhr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT"); 
		
		 xhr.upload.addEventListener("progress", uploadProgress, false);
		
		xhr.onload = function() {
        if (xhr.status == 200) {
          	progressBar.value = 100;

        } else {
            alert("Error! Upload failed");
        }
    };
		//Initiate upload
		    progressBar.value = 0;
		xhr.send(file);
	}else if (window.XDomainRequest) {
		var xdr = new XDomainRequest();
		xdr.onload=function() {
			alert( xdr.responseText);
		}
		/*xdr.setRequestHeader("Cache-Control", "no-cache");
		xdr.setRequestHeader("X-File-Name", file.name);
		xdr.setRequestHeader("folder", folder);
		xdr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT"); */
		xdr.open("POST", "wtcore/jsp/ext/ipe/ecaddoc/upload.jsp");
		xdr.send(file);
	}
	 else
        alert("Cross Domain not supported");
   // stopProp(e);
}


function extractFilename(s){ 
  // returns string containing everything from the end of the string 
  //   that is not a back/forward slash or an empty string on error
  //   so one can check if return_value===''
  return (typeof s==='string' && (s=s.match(/[^\\\/]+$/)) && s[0]) || '';
} 

// progress on transfers from the server to the client (downloads)
function updateProgress(evt) {
  if (evt.lengthComputable) {
    var percentComplete = evt.loaded / evt.total;
  } else {
    // Unable to compute progress information since the total size is unknown
  }
}

function transferComplete(evt) {
  alert("The transfer is complete.");
}

function transferFailed(evt) {
  alert("An error occurred while transferring the file.");
}

function transferCanceled(evt) {
  alert("The transfer has been canceled by the user.");
}

function uploadProgress(evt) {
        if (evt.lengthComputable) {
          var percentComplete = Math.round(evt.loaded * 100 / evt.total);
          document.getElementById('progressxmlBar').value = percentComplete;
        }
        else {
          document.getElementById('progressNumber').innerHTML = 'unable to compute';
        }
      }
comprobarNavegador();
</script>



