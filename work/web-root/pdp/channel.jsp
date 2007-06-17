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
<html:html locale="true">
<%
  String http = request.isSecure() ? "https://" : "http://";
  String url =  http + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
 %>
<head>
<title>PDP Channel</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="https://docs.onestart.iu.edu/dav/MY/channels/style-channel.css" rel="stylesheet" type="text/css">
<script language='javascript' src='https://docs.onestart.iu.edu/dav/MY/shared/OneStartGlobal.js'></script>
</head>

<body bgcolor="#ffffff" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">
<table width="100%" border="0" cellspacing="0" cellpadding="1">
  <tr>
    <th colspan="3">Pre-Disbursement Processor (PDP)</th>
  </tr>

  <tr>
    <td colspan="3"><img src="https://docs.onestart.iu.edu/dav/MY/channels/images-channelglobal/pixel_clear.gif" width="5" height="5"></td>
  </tr>
  <tr>
    <td width="200" valign="top">
    <img src="https://uisapp2.iu.edu/pdp-prd/images/logo_small.gif" border="0" alt="PDP" hspace="1">
    <li><span>Hours: Mon-Sat, 6am-9pm</span></li>
  </td>
<c:if test="${not SecurityRecord.anyRole}">
  <td valign="middle">
    You do not have rights to the PDP application.
  </td>
</c:if>
<c:if test="${SecurityRecord.limitedViewRole or SecurityRecord.viewIdRole or SecurityRecord.viewBankRole or SecurityRecord.viewAllRole or SecurityRecord.submitRole or SecurityRecord.processRole}">
  <td valign="top">
    <ul>
    <b>PDP Application Components</b>
      <c:if test="${SecurityRecord.limitedViewRole or SecurityRecord.viewIdRole or SecurityRecord.viewBankRole or SecurityRecord.viewAllRole}">
          <li><portal:portalLink displayTitle="true" title="Search for Payment" url="pdp/paymentsearch.do" prefix="../" /></li>
          <li><portal:portalLink displayTitle="true" title="Search for Batch" url="pdp/batchsearch.do" prefix="../" /></li>
      </c:if>
      <c:if test="${SecurityRecord.submitRole}">
          <li><portal:portalLink displayTitle="true" title="Manually Upload Payment File" url="pdp/manualupload.do" prefix="../" /></li>
      </c:if>
      <c:if test="${SecurityRecord.processRole}">
          <li><portal:portalLink displayTitle="true" title="Format Checks/ACH" url="pdp/formatselection.do" prefix="../" /></li>
          <li><portal:portalLink displayTitle="true" title="Forumat Summary Review" url="pdp/formatsummary.do" prefix="../" /></li>
      </c:if>
    </ul>
  </td>
</c:if>
 <c:if test="${SecurityRecord.rangesRole or SecurityRecord.sysAdminRole}">
  <td valign="top">
    <ul>
    <b>Support</b>
      <c:if test="${SecurityRecord.rangesRole}">
          <li><portal:portalLink displayTitle="true" title="Disbursement Ragne Maintenance" url="pdp/disbursementmaint.do" prefix="../" /></li>
      </c:if>
      <c:if test="${SecurityRecord.sysAdminRole}">
          <li><portal:portalLink displayTitle="true" title="Bank Maintenance" url="pdp/bank.do" prefix="../" /></li>
          <li><portal:portalLink displayTitle="true" title="Customer Profile Maintenance" url="pdp/customerprofile.do" prefix="../" /></li>
      </c:if>
    </ul>
    <!--<ul>
    <b>Training</b>
        <li>
    </ul>
    <ul>
    <b>Other</b>
    <li><a href="javascript:focusInOnUrl('yourlink.html')">Frequently Asked Questions</a> </li>
    <li><a href="javascript:focusInOnUrl('yourlink.html')">Current Announcements</a></li>
    </ul> -->
    </td>
</c:if>
  </tr>
</table>
<br>
<c:import url="/pdp/backdoor.jsp"/>
</body>
</html:html>
