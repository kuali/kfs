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
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
  <head>
    <html:base />
    <title>Batch Update</title>
  </head>
  <body>
    <h1><strong>Payment Update:</strong></h1><br>
    <jsp:include page="${request.getContextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
    <html:form action="/pdp/batchupdate">
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
                          <th width="25%" align=right nowrap >Batch ID:</th>
                          <td width="25%" align=left class="datacell"><c:out value="${BatchDetail.id}" />&nbsp;</td>
                          <th width="25%" align=right nowrap >Customer File Creation Date:</th>
                          <td width="25%" align=left class="datacell">
                            <fmt:formatDate value="${BatchDetail.customerFileCreateTimestamp}" pattern="MM/dd/yyyy'  at  'hh:mm a" />&nbsp;
                          </td>
                        </tr>
                        <tr>
                          <th align=right nowrap >Total Payment Count:</th>
                          <td align=left class="datacell"><c:out value="${BatchDetail.paymentCount}" />&nbsp;</td>
                          <th align=right nowrap >PDP File Processed Date:</th>
                          <td colspan=1 align=left class="datacell">
                            <fmt:formatDate value="${BatchDetail.fileProcessTimestamp}" pattern="MM/dd/yyyy'  at  'hh:mm a" />&nbsp;
                          </td>
                        </tr>
                        <tr>
                          <th align=right nowrap >Total Payment Amount:</th>
                          <td align=left class="datacell">
                            <fmt:formatNumber value="${BatchDetail.paymentTotalAmount}" type="currency" />&nbsp;
                          </td>
                          <th align=right nowrap >Submitter User ID:</th>
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
                                <input type="image" name="btnUpdateSave" src="<%= request.getContextPath().toString() %>/pdp/images/button_save.gif" alt="Save" align="absmiddle">
                                <input type="image" name="btnUpdateClear" src="<%= request.getContextPath().toString() %>/pdp/images/button_clearfields.gif" alt="Clear" align="absmiddle">
                                <input type="image" name="btnUpdateCancel" src="<%= request.getContextPath().toString() %>/pdp/images/button_cancel.gif" alt="Cancel" align="absmiddle">
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
  <c:import url="/pdp/backdoor.jsp"/>
</body>
</html:html>
