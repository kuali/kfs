<%@ page language="java"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<head>
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
<title>Upload Payment File</title>
</head>
<h1><strong>Upload Payment File</strong></h1>
<jsp:include page="${request.contextPath}/TestEnvironmentWarning.jsp" flush="true"/>
<p><strong>Upload Not Successful</strong></p>
<br>
<p>The following errors were found that prevent this file from being loaded:</p>
<table>
	<tr>
		<td>&nbsp;</td>
		<td>
			<table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
  			<tr>
    			<th>Error</th>
  			</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<table width="100%" border=0 cellpadding=0 cellspacing=0>
			  <c:forEach var="item" items="${errors}">
			  <tr> 
			    <td>&nbsp;<c:out value="${item}"/></td>
			  </tr>
			  </c:forEach>
			</table>
		</td>
	</tr>
</table>
<p>Please correct these problems and try to upload again</p>
<p><html:link action="/manualupload.do">Upload File</html:link></p>
<br>
<c:import url="/backdoor.jsp"/>
</body>
</html:html>
