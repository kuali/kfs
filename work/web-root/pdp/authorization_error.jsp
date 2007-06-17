
<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head><title>Authorization Error</title></head>
  <body>
  	<h1><strong>Authorization Error</strong></h1>
  	<jsp:include page="TestEnvironmentWarning.jsp" flush="true"/>
  	<p><strong>We were unable to authorize your username</strong></p>
  </body>
</html:html>
