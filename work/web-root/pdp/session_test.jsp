
<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css"/>
<head><title>Session Test Page</title></head>
<html:form action="sessiontest">
  <body>
  	<h1><strong>Session Test</strong></h1>
  	<jsp:include page="TestEnvironmentWarning.jsp" flush="true"/>
  	<h3><a href="<%= request.getContextPath().toString() %>/sessiontest.do?btnSubmit=param&searchStatus=HELD">Search For HELD payments</a></h3>
  	<br><br>
  	<input type="image" name="btnSubmit" src="<%= request.getContextPath() + "/images/button_search.gif" %>" alt="Submit" align="absmiddle">
  	<br><br>
    <p>Log Message:</p>
    <br><br><br>
    <c:out value="${logMessages}" escapeXml="false"/>
  </body>
</html:form>
</html:html>
