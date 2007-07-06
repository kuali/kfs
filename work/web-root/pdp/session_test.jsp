<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
<head><title>Session Test Page</title></head>
<html:form action="sessiontest">
  <body>
  	<h1><strong>Session Test</strong></h1>
  	<jsp:include page="TestEnvironmentWarning.jsp" flush="true"/>
  	<h3><a href="<%= request.getContextPath().toString() %>/pdp/sessiontest.do?btnSubmit=param&searchStatus=HELD">Search For HELD payments</a></h3>
  	<br><br>
  	<input type="image" name="btnSubmit" src="<%= request.getContextPath() + "/pdp/images/button_search.gif" %>" alt="Submit" align="absmiddle">
  	<br><br>
    <p>Log Message:</p>
    <br><br><br>
    <c:out value="${logMessages}" escapeXml="false"/>
  </body>
</html:form>
</html:html>
