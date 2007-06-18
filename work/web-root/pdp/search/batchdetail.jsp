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
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css"/>
<head>
  <html:base />
  <title>Batch Detail</title>
  <c:if test="${param.results != null}">
    <script>
      location.hash = "results";
    </script>
  </c:if>
</head>
<body>
  <!--  BATCH DETAIL -->
  <h1><strong>Payments for This Batch</strong></h1>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
  <br>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <br>
        <font color="#800000">
          <html:errors/>&nbsp;
        </font>&nbsp;
        <br>
      </td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="4">
          <tr>
            <td><strong>Batch Details:</strong></td>
            <td></td>
          </tr>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
    <tr>
      <td width="20">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="20">&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
          <tbody>
            <tr>
              <th align=right nowrap >Batch ID:</th>
              <td width="50%" align=left class="datacell"><c:out value="${BatchDetail.id}" />&nbsp;</td>
              <th align=right nowrap >Customer File Creation Date:</th>
              <td colspan=1 align=left class="datacell">
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
            <tr valign=middle align=left>
              <th colspan="4" nowrap=nowrap>
              <div align="center">
                <html:form action="/pdp/batchupdate">
                  <html:hidden property="changeId" value="BatchDetail.id" />
                    <c:if test="${SecurityRecord.cancelRole}">
                      <input type="image" name="btnBatchCancel" src="<%= request.getContextPath().toString() %>/pdp/images/button_cancelbatch.gif" alt="Cancel Batch" align="absmiddle"></a>
                    </c:if>
                    <c:if test="${SecurityRecord.holdRole}">
                    <c:if test="${empty allAreHeld}">
                      <input type="image" name="btnBatchHold" src="<%= request.getContextPath().toString() %>/pdp/images/button_holdbatch.gif" alt="Hold Batch" align="absmiddle"></a>
                    </c:if>
                    <c:if test="${not empty allAreHeld}">
                      <input type="image" name="btnBatchRemoveHold" src="<%= request.getContextPath().toString() %>/pdp/images/button_rembatchhold.gif" alt="Remove Batch Hold" align="absmiddle"></a>
                    </c:if>
                  </c:if>
                </html:form>
              </div>
            </tr>
          </tbody>
        </table>
        <br>
        <table>
          <tr>
            <td colspan="4">Back to:</td>
            <td><a href="<%= request.getContextPath().toString() %>/batchsearch.do?btnBack_batch=param">Batch Search Results</a></td>
            <td colspan="3"></td>
          </tr>
        </table>
      </td>
      <td>&nbsp;</td>
    </tr>
  </table>
</table>
<br>
<a name="results"></a>
<logic:notEmpty name="batchIndivSearchResults" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <display-el:table sort="list" name="sessionScope.batchIndivSearchResults" id="item" pagesize="${perPage}" width="100%" cellpadding="4" cellspacing="0" class="bord-r-t">
          <display-el:column sortable="true" title="Customer" headerClass="thfont" class="datacell" width="90px">
            <font size="0px"><c:out value="${item.paymentGroup.batch.customerProfile.chartCode}" />-<c:out value="${item.paymentGroup.batch.customerProfile.orgCode}" />-<c:out value="${item.paymentGroup.batch.customerProfile.subUnitCode}" /></font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="custPaymentDocNbr" title="Source Document Number" headerClass="thfont" class="datacell">
            <font size="0px">
              <c:if test="${(PaymentDetailSearchForm.oldDisbursementNbr != item.paymentGroup.disbursementNbr) && (not empty PaymentDetailSearchForm.oldDisbursementNbr)}">
                <img src="<%= request.getContextPath().toString() %>/pdp/images/bullet-red.gif" alt=">>">
              </c:if>
              <a href="<%= request.getContextPath().toString() %>/paymentdetail.do?DetailId=<c:out value="${item.id}" />"><c:out value="${item.custPaymentDocNbr}" /></a>
            </font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="purchaseOrderNbr" title="PO Number" headerClass="thfont" class="datacell">
            <font size="0px"><c:out value="${item.purchaseOrderNbr}" /></font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="invoiceNbr" title="Invoice Number" headerClass="thfont" class="datacell">
            <font size="0px"><c:out value="${item.invoiceNbr}"/>&nbsp;</font>
          </display-el:column>
          <display-el:column sortable="true" sortProperty="paymentGroup.payeeName" title="Payee Name" headerClass="thfont" class="datacell">
            <font size="0px"><c:out value="${item.paymentGroup.payeeName}" /></font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="paymentGroup.paymentDate" title="Pay Date" headerClass="thfont" class="datacell">
            <font size="0px"><fmt:formatDate value="${item.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" /></font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="paymentGroup.disbursementDate" title="Disb. Date" headerClass="thfont" class="datacell">
            <font size="0px"><fmt:formatDate value="${item.paymentGroup.disbursementDate}" pattern="MM/dd/yyyy" /></font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="paymentGroup.paymentStatus.code" title="Status" headerClass="thfont" class="datacell">
            <font size="0px"><c:out value="${item.paymentGroup.paymentStatus.code}" /></font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="paymentGroup.disbursementType.description" title="Disb. Type" headerClass="thfont" class="datacell">
            <c:if test="${item.paymentGroup.disbursementType!=null}" >
              <font size="0px"><c:out value="${item.paymentGroup.disbursementType.description}" /></font>
            </c:if>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="paymentGroup.disbursementNbr" title="Disb. Number" headerClass="thfont" class="datacell">
            <font size="0px"><c:out value="${item.paymentGroup.disbursementNbr}" /></font>&nbsp;
          </display-el:column>
          <display-el:column sortable="true" sortProperty="netPaymentAmount" title="Net Payment Amount" headerClass="thfont" class="datacell">
            <font size="0px"><fmt:formatNumber value="${item.netPaymentAmount}" type="currency" /></font>&nbsp;
          </display-el:column>
          <display-el:setProperty name="paging.banner.placement">both</display-el:setProperty>
          <display-el:setProperty name="paging.banner.group_size">15</display-el:setProperty>
          <display-el:setProperty name="paging.banner.page.separator">&nbsp;::&nbsp;</display-el:setProperty>
          <display-el:setProperty name="paging.banner.one_item_found">
                 </td>
                 <td width="20">&nbsp;</td>
               </tr>
             </table>
             <br><br>
             <h1><div align="left">Search Results: 1 found.</div></h1>
             <br><br>
             <table width="100%" border="0" cellspacing="0" cellpadding="0">
               <tr>
                 <td width="20">&nbsp;</td>
                 <td>
          </display-el:setProperty>
          <display-el:setProperty name="paging.banner.all_items_found">
                 </td>
                 <td width="20">&nbsp;</td>
               </tr>
             </table>
             <br><br>
             <h1><div align="left">Search Results: {0} found.</div></h1>
             <br><br>
             <table width="100%" border="0" cellspacing="0" cellpadding="0">
               <tr>
                 <td width="20">&nbsp;</td>
                 <td>
          </display-el:setProperty>
          <display-el:setProperty name="paging.banner.some_items_found">
                 </td>
                 <td width="20">&nbsp;</td>
               </tr>
             </table>
             <br><br>
             <h1><div align="left">Search Results: {0} found.  Displaying {2} to {3}.</div>
          </display-el:setProperty>
          <display-el:setProperty name="paging.banner.full">
                 <div align="right">[<a href="{1}">First</a>/ <a href="{2}">Prev</a>] {0} [ <a href="{3}">Next</a>/ <a href="{4}">Last </a>]</div>
              </h1>
              <br><br>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="20">&nbsp;</td>
                  <td>
          </display-el:setProperty>
          <display-el:setProperty name="paging.banner.first">
                 <div align="right">[First/Prev] {0} [ <a href="{3}">Next</a>/ <a href="{4}">Last</a>]</div>
               </h1>
               <br><br>
               <table width="100%" border="0" cellspacing="0" cellpadding="0">
                 <tr>
                   <td width="20">&nbsp;</td>
                   <td>
          </display-el:setProperty>
          <display-el:setProperty name="paging.banner.last">
                  <div align="right">[ <a href="{1}">First</a>/ <a href="{2}">Prev</a>] {0} [Next/Last]</div>
                </h1>
                <br><br>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="20">&nbsp;</td>
                    <td>
          </display-el:setProperty>
          <display-el:setProperty name="paging.banner.onepage" value=""/>
        </display-el:table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </table>
  <br>
</logic:notEmpty>
<p>&nbsp;</p>
<c:import url="/backdoor.jsp"/>
</body>
</html:html>
