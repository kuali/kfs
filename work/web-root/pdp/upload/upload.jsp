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
<html:form action="/pdp/manualuploadfile.do" enctype="multipart/form-data">
<table border="0" width="100%">
<tr>
  <td width="10%">&nbsp;</td>
  <td width="80%"><html:errors/><br /></td>
  <td width="10%">&nbsp;</td>
</tr>
<tr>
  <td width="10%">&nbsp;</td>
  <td width="80%">
  <%  
      if ( request.getHeader("user-agent").indexOf("Safari") > 0 ) {
   %>
		To manually upload a payment file to the Pre-Disbursement Processor (PDP):
						<p> 
							<ol>
								<li>Click the <b>Choose File</b> button and begin searching for the file you want to upload. </li>
						  	<li>Once you’ve located the payment file, select the file by double clicking on the file name or by highlighting the file and clicking on the <b>Open</b> button. </li>
						  	<li>With the path and filename now selected click the <b>Submit</b> button to upload your payment file. </li>
						  	<li>After the file is submitted your browser will display statistics and messages about the upload process.  In addition, an email will be sent to the primary contact for files submitted from your area as confirmation that the correct file was processed. </li>
							</ol>
						</p>
  <%
      } else {
   %>
		To manually upload a payment file to the Pre-Disbursement Processor (PDP):
						<p> 
							<ol>
								<li>Click the <b>Browse</b> button and begin searching for the file you want to upload. </li>
						   	<li>Once you’ve located the payment file, select the file by double clicking on the file name or by highlighting the file and clicking on the <b>Open</b> button. </li>
						   	<li>With the path and filename now selected click the <b>Submit</b> button to upload your payment file. </li>
						  	<li>After the file is submitted your browser will display statistics and messages about the upload process.  In addition, an email will be sent to the primary contact for files submitted from your area as confirmation that the correct file was processed. </li>
						  </ol>
						</p>
  <%
      }
   %>
  
  <br />
  </td>
  <td width="10%">&nbsp;</td>
</tr>
<tr>
  <td>&nbsp;</td>
  <td><br /><br />
      <html:file property="file"/>
      <br /><br /></td>
  <td>&nbsp;</td>
</tr>
<tr>
  <td>&nbsp;</td>
  <td><html:submit /></td>
  <td>&nbsp;</td>
</tr>
</table>
</html:form>
<br>
<c:import url="/pdp/backdoor.jsp"/>
</body>
</html:html>
