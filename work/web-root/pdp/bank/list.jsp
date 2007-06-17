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
<app:getBank/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
<head><title>Bank List</title></head>
<body>

<h1><strong>Bank Maintenance</strong></h1>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="20">
      &nbsp;
    </td>
    <td>
      <br>
      <font color="#800000">
          <html:errors/>
        <br>
      </font>
    </td>
  </tr>
</table>

<table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
    <td width=20>&nbsp;</td>
    <td>
      <table width="100%" height=40 border=0 cellpadding=0 cellspacing=0>
        <tr>
          <!-- Fix this back link -->
          <td>
            <strong>Bank List:</strong>
          </td>
          <td>
            <div align=right>
          	<a href="bank.do?bankId=0">Create a New Bank</a>
            </div>
          </td>
        </tr>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
</table>
<br>    
<table width="95%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t" align="center" >
  <tbody>
    <tr>
      <th><div align="center">ID</div></th>
      <th><div align="center">Name</div></th>
      <th><div align="center">Description</div></th>
      <th><div align="center">Disbursement Type</div></th>
      <th><div align="center">Routing Number</div></th>
      <th><div align="center">Account Number</div></th>
      <th><div align="center">Active Indicator</div></th>
      <th><div align="center">Last Update</div></th>
    </tr>
    <logic:iterate id="b" name="BankList" indexId="i">
    <tr valign="middle" align="left">
      <td nowrap="nowrap" class="datacell">
        <a href="bank.do?bankId=<c:out value="${b.id}"/>"><c:out value="${b.id}"/></a>&nbsp;
      </td>
      <td nowrap="nowrap" class="datacell"><c:out value="${b.name}"/>&nbsp;</td>
      <td nowrap="nowrap" class="datacell"><c:out value="${b.description}"/>&nbsp;</td>
      <td nowrap="nowrap" class="datacell"><c:out value="${b.disbursementType.description}"/>&nbsp;</td>
      <td nowrap="nowrap" class="datacell"><c:out value="${b.routingNumber}"/>&nbsp;</td>
      <td nowrap="nowrap" class="datacell"><c:out value="${b.accountNumber}"/>&nbsp;</td>
      <td nowrap="nowrap" class="datacell">
        <logic:equal name="b" property="active" value="true">Active</logic:equal>
        <logic:notEqual name="b" property="active" value="true">Not Active</logic:notEqual>   
      </td>
      <td nowrap="nowrap" class="datacell">
        <fmt:formatDate value="${b.lastUpdate}"/> by <c:out value="${b.lastUpdateUser.networkId}"/>
      </td>
    </tr>
    </logic:iterate>
  </tbody>
</table>
<br>
<c:import url="/backdoor.jsp"/>
</body>
</html:html>
