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
<app:getBank active="Y" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head>
    <html:base />
    <title>Disbursement Range Maintenance</title>
  </head>
  <body>
<h1><strong>Disbursement Range Maintenance</strong></h1>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
<html:form action="/pdp/disbursementmaintsave">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="20">&nbsp;</td>
    <td>
      <br><font color="#800000"><html:errors/></font><br>
    </td>
  </tr>
</table>
<br>
<html:hidden property="id" />
<html:hidden property="version" />
<table width="75%" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" align="center">
  <tbody>
  <tr>
    <th align="center" valign="top" nowrap="true" colspan="2">
    	<strong>
      <logic:empty name="PdpDisbursementNumberMaintenanceForm" property="id"><div align="center">New Disbursement Range</div></logic:empty>
      <logic:notEmpty name="PdpDisbursementNumberMaintenanceForm" property="id">
        <logic:equal name="PdpDisbursementNumberMaintenanceForm" property="id" value="0">
        	<div align="center">New Disbursement Range</div>
        </logic:equal>
        <logic:notEqual name="PdpDisbursementNumberMaintenanceForm" property="id" value="0">
        	<div align="center">Disbursement Range ID: <c:out value="${DisbursementNumberMaintenanceForm.id}"/></div>
        </logic:notEqual>
      </logic:notEmpty>
      </strong>
    </th>
  <tr>
    <th align="right" valign="top" nowrap="true" width="33%"><font color="red">*</font>Campus Processing Location:</th>
    <td align="left" class="datacell">
      <html:text property="physCampusProcCode" tabindex="1" maxlength="2"/>&nbsp;
    </td>
  </tr>
	<tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Bank:</th>
    <td align="left" class="datacell">
    <logic:iterate id="b" name="BankList" indexId="i">
    	<html:radio property="bankId" value="${b.id}" tabindex="2" />
    		<c:out value="${b.name}"/> - <c:out value="${b.disbursementType.description}"/><br>
    </logic:iterate>&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Beginning Disbursement Number:</th>
    <td align="left" class="datacell">
      <html:text property="beginDisbursementNbr" tabindex="3" maxlength="9"/>&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Ending Disbursement Number:</th>
    <td align="left" class="datacell">
      <html:text property="endDisbursementNbr" tabindex="4" maxlength="9"/>&nbsp;
    </td>
  </tr>
  <tr>
	  <th align="right" valign="top" nowrap="true"><font color="red">*</font>Last Assigned Disbursement Number:</th>
	  <td align="left" class="datacell">
	    <html:text property="lastAssignedDisbNbr" tabindex="5" maxlength="9"/>&nbsp;
	  </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Range Effective Date:<br>(ex: 01/08/2004)</th>
    <td align="left" class="datacell">
      <html:text property="disbNbrEffectiveDt" tabindex="6" maxlength="10"/>&nbsp;
    </td>
  </tr>
  <tr>
    <th align="right" valign="top" nowrap="true"><font color="red">*</font>Range Expiration Date:<br>(ex: 01/08/2004)</th>
    <td align="left" class="datacell">
      <html:text property="disbNbrExpirationDt" tabindex="7" maxlength="10"/>&nbsp;
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
       <td align="right" nowrap="nowrap"><font style="align" color="red">*</font> Required Field</td> 
       <td>&nbsp;</td>
    </tr>
  </tbody>
</table>
</html:form>
<c:import url="/backdoor.jsp"/>
</body>
</html:html>
