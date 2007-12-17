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
<html:html>
<head>
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
<title>Format Disbursement Summary</title>
<script type="text/javascript">
  var formHasAlreadyBeenSubmitted = false;
  var excludeSubmitRestriction = false;
  function hasFormAlreadyBeenSubmitted() {
    if ( document.getElementById( "formComplete" ) ) { 
	  if (formHasAlreadyBeenSubmitted && !excludeSubmitRestriction) {
        alert("Page already being processed by the server.");
        return false;
      } else {
        formHasAlreadyBeenSubmitted = true;
        return true;
      }
      excludeSubmitRestriction = false; 
    } else {
      alert("Page has not finished loading.");
      return false;
    }
  }
</script>
</head>
<body>
  <h1><strong>Format Disbursement Summary</strong></h1><br>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td><strong>Your Default Campus Code is <b><c:out value="${PdpFormatProcessForm.campusCd}"/> Process ID: <c:out value="${PdpFormatProcessForm.procId}"/></strong></td>
              <td width="20">&nbsp;</td>
            </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>
<br>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td>Payments selected for format process:</td>
              <td>&nbsp;</td>
           </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>

<table align="center" border="0" cellpadding="0" cellspacing="0" width="90%">
  <tbody>
  <tr>
    <td width="20">&nbsp;</td>
    <td>&nbsp;</td>
    <td width="20">&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <table border="0" cellpadding="4" cellspacing="0" width="100%" class="bord-r-t">
        <tbody>
          <tr>
            <th width="15%">Sort Group</th>
            <th width="45%">Customer</th>
            <th width="20%">Payment Details</th>
            <th width="20%">Amount</th>
         </tr>
        </tbody>
      </table>
      <table border="0" cellpadding="4" cellspacing="0" width="100%">
        <tbody>
         <c:forEach var="item" items="${results}">
         <tr>
            <td width="15%"><c:out value="${item.sortGroupName}"/></td>
            <td width="45%"><c:out value="${item.cust.chartCode}/${item.cust.orgCode}/${item.cust.subUnitCode} ${item.cust.customerDescription}"/></td>
            <td width="20%" align="right"><fmt:formatNumber value="${item.payments}"/></td>
            <td width="20%" align="right"><fmt:formatNumber value="${item.amount}" type="currency"/></td>
         </tr>
         </c:forEach>
         <tr>
            <td>&nbsp;</td>
            <td align="left"><b>Total</b></td>
            <td align="right"><b><fmt:formatNumber value="${total.payments}"/></b></td>
            <td align="right"><b><fmt:formatNumber value="${total.amount}" type="currency"/></b></td>
         </tr>
        </tbody>
      </table>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <html:form action="/pdp/format" onsubmit="return hasFormAlreadyBeenSubmitted();">
      <html:hidden property="procId"/>
      <html:hidden property="campusCd"/>
      <table border="0" cellpadding="3" cellspacing="0" width="100%" class="bord-r-t">
        <tbody>
          <tr align="left" valign="middle">
            <th nowrap="nowrap">
              <div align="center">
                <input type="image" name="btnContinue" src="<%= request.getContextPath() + "/pdp/images/button_continue.gif" %>" alt="Continue" align="center">&nbsp;
                <input type="image" name="btnCancel" src="<%= request.getContextPath() + "/pdp/images/button_cancel.gif" %>" alt="Cancel" align="center">
              </div>
            </th>
          </tr>
        </tbody>
      </table>
      </html:form>
    </td>
    <td>&nbsp;</td>
  </tr>
  </tbody>
</table>
<div id="formComplete"></div>
<p>&nbsp;</p>
<c:import url="/pdp/backdoor.jsp"/>
</body>
</html:html>

