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
					          <center><font color="#800000" size="2"><br><b>An unexpected error has been encountered.</b></font><br><br></center>
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
