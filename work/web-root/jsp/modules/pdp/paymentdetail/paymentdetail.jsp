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
<%@page import="java.util.List,org.kuali.kfs.pdp.businessobject.PaymentDetail,org.kuali.kfs.pdp.businessobject.PaymentGroup" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
  <head>
    <html:base />
    <title>Payment Details</title>
  </head>
  <body>
    <h1><strong>Payment Details for Payment Detail ID: <c:out value="${KualiForm.paymentDetail.id}"/></strong></h1>
    <br>
    <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
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
    <table width="100%" border=0 cellspacing=0 cellpadding=0>
      <tr>
        <td width="20">&nbsp;</td>
        <td>
          <table width="100%" height=40 border=0 cellpadding=0 cellspacing=0>
            <tr>
               
            <SCRIPT>
	        document.write("<td>Return to:&nbsp;<a href=\"#\" onclick=\"window.close()\">Individual Search Results</a></td>");
            </SCRIPT>
            <NOSCRIPT>
            <td>
            &nbsp;
            </td>
            </NOSCRIPT>
          
          <td>
            &nbsp;
          </td>
        </tr>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
  <tr>
    <td width=20>&nbsp;</td>
    <td>    
      <table width="100%" border=0 cellspacing=0 cellpadding=0>
        <tr>
          <td>
            <table width="100%" border=0 cellpadding=4 cellspacing=0 class="tabtable">
              <tr>
            <c:if test="${KualiForm.btnPressed == 'btnSummaryTab'}" >
              <td class="tab-selected" >
            </c:if>
            <c:if test="${KualiForm.btnPressed != 'btnSummaryTab'}" >
              <td class="tab-notselected" >
            </c:if>
                  <div align="center">
                    <c:if test="${KualiForm.btnPressed != 'btnSummaryTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnSummaryTab" class="HdrScrLnk">
                    </c:if>
                    Summary
                    <c:if test="${KualiForm.btnPressed != 'btnSummaryTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${KualiForm.btnPressed == 'btnBatchTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${KualiForm.btnPressed != 'btnBatchTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align="center">
                    <c:if test="${KualiForm.btnPressed != 'btnBatchTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnBatchTab" class="HdrScrLnk">
                    </c:if>
                    Batch
                    <c:if test="${KualiForm.btnPressed != 'btnBatchTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${KualiForm.btnPressed == 'btnPaymentTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${KualiForm.btnPressed != 'btnPaymentTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align="center">
                    <c:if test="${KualiForm.btnPressed != 'btnPaymentTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnPaymentTab" class="HdrScrLnk">
                    </c:if>
                    Payment
                    <c:if test="${KualiForm.btnPressed != 'btnPaymentTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${KualiForm.btnPressed == 'btnPayeeTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${KualiForm.btnPressed != 'btnPayeeTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align="center">
                    <c:if test="${KualiForm.btnPressed != 'btnPayeeTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnPayeeTab" class="HdrScrLnk">
                    </c:if>
                    Payee
                    <c:if test="${KualiForm.btnPressed != 'btnPayeeTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${KualiForm.btnPressed == 'btnAccountingTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${KualiForm.btnPressed != 'btnAccountingTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${KualiForm.btnPressed != 'btnAccountingTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnAccountingTab" class="HdrScrLnk">
                    </c:if>
                    Accounting
                    <c:if test="${KualiForm.btnPressed != 'btnAccountingTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${KualiForm.btnPressed == 'btnAchInfoTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${KualiForm.btnPressed != 'btnAchInfoTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${KualiForm.btnPressed != 'btnAchInfoTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnAchInfoTab" class="HdrScrLnk">
                    </c:if>
                    ACH Info
                    <c:if test="${KualiForm.btnPressed != 'btnAchInfoTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>                
                <c:if test="${KualiForm.btnPressed == 'btnHistoryTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${KualiForm.btnPressed != 'btnHistoryTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${KualiForm.btnPressed != 'btnHistoryTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnHistoryTab" class="HdrScrLnk">
                    </c:if>
                    History
                    <c:if test="${KualiForm.btnPressed != 'btnHistoryTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${KualiForm.btnPressed == 'btnNotesTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${KualiForm.btnPressed != 'btnNotesTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${KualiForm.btnPressed != 'btnNotesTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetailrice.do?DetailId=${KualiForm.paymentDetail.id}&methodToCall=showPaymentDetail&tab=btnNotesTab" class="HdrScrLnk">
                    </c:if>
                    Notes
                    <c:if test="${KualiForm.btnPressed != 'btnNotesTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
  <tr>
    <td width="20">&nbsp</td>
    <td>
      <table width="100%" border=0 cellpadding=0 cellspacing=0>
          <!-- SUMMARY TAB -->
          <jsp:include page="summaryTab.jsp" flush="true"/>
          <c:if test="${KualiForm.btnPressed == 'btnBatchTab'}" >
            <!-- BATCH TAB -->
            <jsp:include page="batchTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${KualiForm.btnPressed == 'btnPaymentTab'}" >
            <!-- PAYMENT TAB -->
            <jsp:include page="paymentTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${KualiForm.btnPressed == 'btnPayeeTab'}" >
            <!-- PAYEE TAB -->
            <jsp:include page="payeeTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${KualiForm.btnPressed == 'btnAccountingTab'}" >
            <!-- ACCOUNTING TAB -->
            <jsp:include page="accountingTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${KualiForm.btnPressed == 'btnAchInfoTab'}" >
            <!-- ACH INFO TAB -->
            <jsp:include page="achInfoTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${KualiForm.btnPressed == 'btnHistoryTab'}" >
            <!-- HISTORY TAB -->
            <jsp:include page="historyTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${KualiForm.btnPressed == 'btnNotesTab'}" >
            <!-- NOTES TAB -->
            <jsp:include page="notesTab.jsp" flush="true"/>
          </c:if>
</body>
</html:html>
