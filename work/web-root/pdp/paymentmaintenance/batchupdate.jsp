
<%@ page language="java"%>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head>
    <html:base />
    <title>Batch Update</title>
	</head>
  
  <body>
   	<h1><strong>Payment Update:</strong></h1><br>

 <jsp:include page="${request.getContextPath}/TestEnvironmentWarning.jsp" flush="true"/>
<html:form action="batchupdate">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top">
  	<td width="20">&nbsp</td>
    <td>
      <table width="100%" border=0 cellpadding=0 cellspacing=0>
        <tbody>
        	<tr>
            <td height="30" nowrap="true" colspan="3">
					    <br>
					  	<font color="#800000">
								<html:errors/>&nbsp;
							</font>&nbsp;
            </td>
          </tr>
         	<tr>
            <td height="30" nowrap="true" colspan="3">
					    <br>
					    <c:choose>
					    	<c:when test="${PaymentMaintenanceForm.action eq 'batchHold'}">
					    		You are about to Hold the following Batch.
					    	</c:when>
					    	<c:when test="${PaymentMaintenanceForm.action eq 'batchRemoveHold'}">
					    		You are about to Remove a Hold on the following Batch.
					    	</c:when>
					    	<c:when test="${PaymentMaintenanceForm.action eq 'batchCancel'}">
					    		You are about to Cancel the following Batch.  Once a batch has been cancelled none of the payments in it can be re-opened for payment.
					    	</c:when>
					    </c:choose>
					    &nbsp;<br><br>
            </td>
          </tr>
					<tr>
	          <td>&nbsp;</td>
						<td>
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
				            <tr>
					            <th width="25%" align=right nowrap >
					            	Batch ID:
					            </th>
					            <td width="25%" align=left class="datacell">
					              <c:out value="${BatchDetail.id}" />&nbsp;
					            </td>
					            <th width="25%" align=right nowrap >
					            	Customer File Creation Date:
					            </th>
					            <td width="25%" align=left class="datacell">
					            	<fmt:formatDate value="${BatchDetail.customerFileCreateTimestamp}" pattern="MM/dd/yyyy'  at  'hh:mm a" />&nbsp;
							    		</td>
					          </tr>
					          <tr>
					            <th align=right nowrap >
					            	Total Payment Count:
					           	</th>
					            <td align=left class="datacell">
					              <c:out value="${BatchDetail.paymentCount}" />&nbsp;
							        </td>
					            <th align=right nowrap >
					            	PDP File Processed Date:
					            </th>
					            <td colspan=1 align=left class="datacell">
					            	<fmt:formatDate value="${BatchDetail.fileProcessTimestamp}" pattern="MM/dd/yyyy'  at  'hh:mm a" />&nbsp;
							    		</td>
							      </tr>
					          <tr>
					            <th align=right nowrap >
					            	Total Payment Amount:
					            </th>
					            <td align=left class="datacell">
					              <fmt:formatNumber value="${BatchDetail.paymentTotalAmount}" type="currency" />&nbsp;
							        </td>
					            <th align=right nowrap >
					            	Submitter User ID:
					            </th>
					            <td align=left class="datacell">
					              <c:out value="${BatchDetail.submiterUser.networkId}" />&nbsp;
							        </td>
					          </tr>
				        </tbody>
				      </table>
	          </td>
	          <td>&nbsp;</td>
					</tr>
				  <tr>
				    <td>&nbsp;</td>
				      <td>
					    	<br><br>Please enter a note to describe the change:&nbsp;<br>
					    </td>
				    <td>&nbsp;</td>
				  </tr>
					<tr>
	          <td>&nbsp;</td>
				    <td>
				    	<br>
				    	<div align="center">
				      <table cellpadding=3 cellspacing=0 border=0  class="bord-r-t">
				        <tbody>
				        	<tr>
				            <td class="datacell">
				              <html:textarea cols="40" rows="4" property="changeText" />
				              <html:hidden property="changeId" />
				              <html:hidden property="action"/>&nbsp;
										</td>
				          </tr>
				          <tr valign=middle align=left>
				            <th nowrap=nowrap colspan="2">
				              <div align="center">
					              <input type="image" name="btnUpdateSave" src="<%= request.getContextPath().toString() %>/images/button_save.gif" alt="Save" align="absmiddle">
					              <input type="image" name="btnUpdateClear" src="<%= request.getContextPath().toString() %>/images/button_clearfields.gif" alt="Clear" align="absmiddle">
					              <input type="image" name="btnUpdateCancel" src="<%= request.getContextPath().toString() %>/images/button_cancel.gif" alt="Cancel" align="absmiddle">
											</div>
				            </th>
				          </tr>
				        </tbody>
				      </table>
							</div>
	          </td>
	          <td>&nbsp;</td>
	        </tr>
				</tbody>
			</table>
		</td>
		<td width=20>&nbsp;</td>
	</tr>
</table>

</html:form>

<c:import url="/backdoor.jsp"/>
</body>
</html:html>
