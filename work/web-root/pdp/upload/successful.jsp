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
<head>
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
<title>Upload Payment File</title>
</head>
<h1><strong>Upload Payment File</strong></h1>
<jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
<p><strong>Upload Successful</strong></p>
<br>
<logic:present name="status">
<p>
Chart: <strong><c:out value="${status.header.chart}"/></strong><br>
Organization: <strong><c:out value="${status.header.org}"/></strong><br>
Sub Unit: <strong><c:out value="${status.header.subUnit}"/></strong><br>
Creation Date: <strong><fmt:formatDate dateStyle="long" value="${status.header.creationDate}"/></strong><br>
Batch ID: <strong><c:out value="${status.batchId}"/></strong><br>
Payment Count: <strong><c:out value="${status.detailCount}"/></strong><br>
Payment Total Amount: <strong><fmt:formatNumber currencyCode="USD" value="${status.detailTotal}"/></strong><br>
</p>
</logic:present>
<logic:present name="errors">
<p><strong>The following issues were found when processing this file:</strong></p>
<table>
	<tr>
		<td>&nbsp;</td>
		<td>
			<table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
  			<tr>
    			<th>Issues</th>
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
</logic:present>
<p><html:link action="/pdp/manualupload.do">Upload File</html:link></p>
<br>
<c:import url="/pdp/backdoor.jsp"/>
</body>
</html:html>
