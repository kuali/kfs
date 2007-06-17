<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head><title>PDP Error</title></head>
  <body>
  	<c:if test="${not empty title}">
  		<h1><strong><c:out value="${title}"/></strong></h1>
  	</c:if>
  	<c:if test="${empty title}">
  		<h1><strong>Error</strong></h1>
  	</c:if>
  	<jsp:include page="TestEnvironmentWarning.jsp" flush="true"/>
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
		  <tr> 
		    <td width="20" height="20">&nbsp;</td>
		    <td >&nbsp;</td>
		    <td width="20" height="20">&nbsp;</td>
		  </tr>
		  <tr>
		    <td width="20" height="20">&nbsp;</td>
		    <td>
				  <logic:messagesPresent>
					    <br>
					    <table width="50%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t" align="center">
					      <tr>
					        <th align=left valign=top class="datacell">
					          <center><font color="#800000" size="2"><br><b><html:errors/></b></font><br><br></center>
					        </th>
					      </tr>
					    </table>
				  </logic:messagesPresent>
				  <logic:messagesNotPresent>
					    <br>
					    <table width="50%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t" align="center">
					      <tr>
					        <th align=left valign=top class="datacell">
					          <center><font color="#800000" size="2"><br><b>An unexpected error has been encountered, please contact <a href="mailto:pdphelp@indiana.edu">pdphelp@indiana.edu</a> for assistance.</b></font><br><br></center>
					        </th>
					      </tr>
					    </table>
				  </logic:messagesNotPresent>
		    </td>
		    <td width="20" height="20">&nbsp;</td>
		  </tr>
		</table>
	</body>
</html:html>
