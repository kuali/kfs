
<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head><title>Error: Session Timed Out</title></head>
  <body>
  	<h1><strong>Session Timed Out</strong></h1>
  	<jsp:include page="TestEnvironmentWarning.jsp" flush="true"/>
  	<p><strong>Your online session has expired. Please begin again.</p>
  </body>
</html:html>
