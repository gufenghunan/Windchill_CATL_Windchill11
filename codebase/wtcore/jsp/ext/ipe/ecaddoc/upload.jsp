	<%@ page import="java.util.StringTokenizer" %>  
	<%@ page import="java.util.LinkedHashMap" %>  
	<%@ page import="java.io.InputStream" %>  
	<%@ page import="java.io.OutputStream" %>  
	<%@ page import="java.io.FileOutputStream" %>  
	<%@ page import="java.io.File" %> 
	<%@ page import="java.io.ByteArrayOutputStream" %>
	<%@ page import="java.io.PrintWriter" %>
	<%@ page import="java.net.URLDecoder" %>
	

    <%  
	
	String filename = request.getHeader("X-File-Name");
	String tempfolderpath = request.getHeader("folder");
	LinkedHashMap<String, String> paramLinkedMap = new LinkedHashMap<String, String>();
	System.out.println(tempfolderpath);
	new File(tempfolderpath).mkdirs();
try{
	String data=null;
	InputStream is = request.getInputStream();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	OutputStream outputStream =  new FileOutputStream(new File(tempfolderpath+"\\"+filename));
	int read = 0;
			byte[] bytes = new byte[1024];
	 
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
	outputStream.close();
}catch(Exception e){
e.printStackTrace();
} 
		      
    %>  