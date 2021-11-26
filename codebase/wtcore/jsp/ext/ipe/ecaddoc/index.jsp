   <!-- <form action="upload.jsp" method="post" enctype="multipart/form-data">  
    Select File:<input type="file" name="fname"/><br/>  
    <input type="image" src="MainUpload.png"/>  
    </form>  -->
<!DOCTYPE html>
<html lang="es" dir="ltr" itemscope itemtype="http://schema.org/Article">
	<input type="file" id="file" name="file" />
<input type="button" value="upload" onclick="doUpload()" />
	<script>
   var client = new XMLHttpRequest();
  
   function upload() 
   {
      var file = document.getElementById("file");
     
      /* Create a FormData instance */
      var formData = new FormData();
      /* Add the file */ 
      formData.append("file", file.files[0],file.files[0].name);
      //client.open("post", "/upload", true);
	  client.open("POST", "upload.jsp", true);
      client.setRequestHeader("Content-Type", "multipart/form-data");
      client.send(formData);  /* Send to server */
   }
     
   /* Check the response status */  
   client.onreadystatechange = function() 
   {
      if (client.readyState == 4 && client.status == 200) 
      {
         alert(client.statusText);
      }
   }
   
   client.onload = function () {
  if (client.status === 200) {
    // File(s) uploaded.
  } else {
    alert('An error occurred!');
  }
};
      
</script>
<script>

function doUpload() {

    var xhr = new XMLHttpRequest();
	var folder='D:\ptc\Windchill_10.2\Windchill\temp\test';
    var file = document.getElementById("file").files[0];
	alert(file.name);
		xhr.open("POST", "upload.jsp",false);
		xhr.onload = function() {
			if (xhr.status === 200) {
				//alert('User\'s name is ' + xhr.responseText);
			}
			else {
				alert('Request failed.  Returned status of ' + xhr.status);
			}
		};
		//Set a few headers so we know the file name in the server
		xhr.setRequestHeader("Cache-Control", "no-cache");
		xhr.setRequestHeader("X-Requested-With", "XmlHttpRequest");
		xhr.setRequestHeader("X-File-Name", file.name);
		xhr.setRequestHeader("folder", folder);
		xhr.setRequestHeader("If-Modified-Since", "Thu, 1 Jan 1970 00:00:00 GMT"); 
		//Initiate upload
		xhr.send(file);
		alert("SENT");
   // stopProp(e);
}
</script>
