
 <%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%> 
 <%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%> 
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> 

 <table> 


	<tr> 
	  <td scope="row" width="100" class="tableColumnHeaderfont" align="right">* folder name:</td> 
	  <td class="tabledatafont" align="left">&nbsp; 
	     <w:textBox name="foldername" id="foldername" maxlength="100" size="20"/> 
	  </td> 
	</tr> 
 </table> 
 <%@include file="/netmarkets/jsp/util/end.jspf"%> 
