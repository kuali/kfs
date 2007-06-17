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
<title>Format Summary</title>
<style type="text/css">
.border {
    border-top-width: 1px;
    border-top-style: solid;
    border-top-color: #999999;
    border-right-style: solid;
    border-right-color: #999999;
    border-right-width: 1px;
    border-bottom-style: solid;
    border-bottom-color: #999999;
    border-bottom-width: 1px;
    border-left-style: solid;
    border-left-color: #999999;
    border-left-width: 1px;
}
</style>
</head>
  
  <body>
    <html:form action="formatsummary">
  <h1><strong>Format Summary Review</strong></h1><br>
  <jsp:include page="${request.contextPath}/TestEnvironmentWarning.jsp" flush="true"/>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
<c:if test="${total.payments == 0}">
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td>There is no summary data associated with Process ID <b><c:out value="${procId}"/></b>.  Please Try your search again.<br></td>
              <td>&nbsp;</td>
           </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
</c:if>
<c:if test="${total.payments != 0}">
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td><br>This is the summary information for Process ID: <b><c:out value="${procId}"/><br><br></td>
              <td>&nbsp;</td>
           </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
    <tr>
      <td width="20">&nbsp;</td>
      <td class="border">
	      <table border="0" cellpadding="4" cellspacing="0" width="100%">
	        <tbody>
		        <tr>
		          <th>Sort Group</th>
		          <th>Customer</th>
		          <th>Disbursement Type</th>
		          <th>Begin Disbursement Number</th>
		          <th>End Disbursement Number</th>
		          <th>Payment Details</th>
		          <th>Amount</th>
		      	</tr>
	         <c:forEach var="item" items="${formatResultList}">
	         <tr>
	            <td><c:out value="${item.sortGroupName}"/>&nbsp;</td>
	            <td><c:out value="${item.cust.chartCode}/${item.cust.orgCode}/${item.cust.subUnitCode} ${item.cust.customerDescription}"/></td>
	            <td><c:out value="${item.disbursementType.description}"/></td>
	            <td align="right"><c:out value="${item.beginDisbursementNbr}"/></td>
	            <td align="right"><c:out value="${item.endDisbursementNbr}"/></td>            
	            <td align="right"><b><fmt:formatNumber value="${item.payments}"/></b></td>
	            <td align="right"><fmt:formatNumber value="${item.amount}" type="currency"/></td>
	         </tr>
	         </c:forEach>
	         <tr>
	            <td>&nbsp;</td>
	            <td><b>Total</b></td>
	            <td>&nbsp;</td>
	            <td>&nbsp;</td>
	            <td>&nbsp;</td>
	            <td align="right"><b><fmt:formatNumber value="${total.payments}"/></b></td>
	            <td align="right"><b><fmt:formatNumber value="${total.amount}" type="currency"/></b></td>
	         </tr>
	        </tbody>
	      </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
    <tr>
      <td width="20">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="20">&nbsp;</td>
    </tr>
</c:if>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td><a href="<%= request.getContextPath().toString() %>/formatsummary.do">Select A Different Format Process</a></td>
              <td>&nbsp;</td>
           </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>

<p>&nbsp;</p>
<c:import url="/backdoor.jsp"/>
    </html:form>
  </body>
</html:html>
