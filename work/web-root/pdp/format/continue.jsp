<%@ page language="java"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html-el" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html>
<head>
<link rel="stylesheet" type="text/css" href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
<title>Format Disbursement Summary</title>
<script language="JavaScript">
  var formHasAlreadyBeenSubmitted = false;
  function hasFormAlreadyBeenSubmitted() {
    if (formHasAlreadyBeenSubmitted) {
      alert("Action is already in progress. Please do not double click buttons in PDP.");
      return false;
    }
    formHasAlreadyBeenSubmitted = true;
    return true;
  }
</script>
</head>
<body>
  <h1><strong>Format Disbursement Summary</strong></h1><br>
  <jsp:include page="${request.contextPath}/TestEnvironmentWarning.jsp" flush="true"/>
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td><strong>Your Default Campus Code is <b><c:out value="${FormatProcessForm.campusCd}"/> Process ID: <c:out value="${FormatProcessForm.procId}"/></strong></td>
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
      <html:form action="/format" onsubmit="return hasFormAlreadyBeenSubmitted();">
      <html:hidden property="procId"/>
      <html:hidden property="campusCd"/>
      <table border="0" cellpadding="3" cellspacing="0" width="100%" class="bord-r-t">
        <tbody>
          <tr align="left" valign="middle">
            <th nowrap="nowrap">
              <div align="center">
                <input type="image" name="btnContinue" src="<%= request.getContextPath() + "/images/button_continue.gif" %>" alt="Continue" align="center">&nbsp;
                <input type="image" name="btnCancel" src="<%= request.getContextPath() + "/images/button_cancel.gif" %>" alt="Cancel" align="center">
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
<p>&nbsp;</p>
<c:import url="/backdoor.jsp"/>
</body>
</html:html>

