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
<app:getReference name="DisbursementType"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head>
    <html:base />
    <title>Bank Maintenance</title>
  </head>
  <body>
<h1><strong>Bank Maintenance</strong></h1>
<br>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
<html:form action="/pdp/banksave">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="20">&nbsp;</td>
    <td>
      <br><font color="#800000"><html:errors/></font><br>
    </td>
  </tr>
</table>
<br><br>
<html:hidden property="id" />
<html:hidden property="version" />
<table width="75%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" align="center">
  <tbody>
  <tr>
    <th align="left" valign="top" nowrap="true" colspan="2">
    	<strong>
      <logic:empty name="PdpBankForm" property="id"><div align="center">New Bank</div></logic:empty>
      <logic:notEmpty name="PdpBankForm" property="id">
        <logic:equal name="PdpBankForm" property="id" value="0"><div align="center">New Bank</div></logic:equal>
        <logic:notEqual name="PdpBankForm" property="id" value="0"><div align="center">Bank ID: <c:out value="${BankForm.id}"/></div></logic:notEqual>
      </logic:notEmpty>
      </strong>
    </th>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Name:</th>
    <td align="left" class="datacell">
      <html:text property="name" tabindex="1" maxlength="25"/>&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Description:</th>
    <td align="left" class="datacell">
      <html:text property="description" tabindex="2" size="25" maxlength="25" />&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Routing Number:</th>
    <td align="left" class="datacell">
      <html:text property="routingNumber" tabindex="3" maxlength="9"/>&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Account Number:</th>
    <td align="left" class="datacell">
      <html:text property="accountNumber" tabindex="4" maxlength="17"/>&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Active?:</th>
    <td align="left" class="datacell">
      <html:select size="1" property="active" value="Y" tabindex="5">
        <html:option value="N">No</html:option>
        <html:option value="Y">Yes</html:option>
      </html:select>&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Disbursement Type:</th>
    <td align="left" class="datacell">
      <html:select size="1" property="disbursementTypeCode" tabindex="6">
        <html:optionsCollection name="DisbursementTypeList" value="code" label="description"/>
      </html:select>&nbsp;
    </td>
  </tr>
</table>
<br>
<table cellpadding="0" width="100%" cellspacing="0" border="0">
  <tbody>
    <tr valign="middle" align="left">
      <td colspan="2" align="center" valign="middle" nowrap="nowrap">
        <input type="image" name="btnSave" src="<%= request.getContextPath().toString() %>/pdp/images/button_save.gif" alt="Save" >&nbsp;
        <input type="image" name="btnCancel" src="<%= request.getContextPath().toString() %>/pdp/images/button_cancel.gif" alt="Cancel" > 
      </td>
    </tr>
    <tr valign="middle" align="left">
       <td align="right" nowrap="nowrap"><font color="red">*</font> Required Field</td>
       <td>&nbsp;</td>
    </tr>
  </tbody>
</table>
</html:form>
<c:import url="/backdoor.jsp"/>
</body>
</html:html>
