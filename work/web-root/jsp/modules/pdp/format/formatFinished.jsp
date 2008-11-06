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
<c:set var="disbursementNumberRangeAttributes" value="${DataDictionary.DisbursementNumberRange.attributes}" />
<c:set var="formatResultAttributes" value="${DataDictionary.FormatResult.attributes}" />
<c:set var="customerProfileAttributes" value="${DataDictionary.CustomerProfile.attributes}" />
<c:set var="dummyAttributes" value="${DataDictionary.AttributeReferenceDummy.attributes}" />

<kul:page headerTitle="Format Disbursement Finished" transactionalDocument="false" showDocumentInfo="false" htmlFormAction="pdp/formatrice" docTitle="Format Disbursement Finished">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td>Payments selected have been formatted and will be extracted for ACH and Check Printing.<br>
              Your Default Campus Code is <b><c:out value="${KualiForm.campus}"/></b> Process ID: <c:out value="${KualiForm.processId}"/></td>
              <td>&nbsp;</td>
           </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
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
         <c:forEach var="item" items="${KualiForm.results}">
         <tr>
            <td><c:out value="${item.sortGroupName}"/></td>
            <td><c:out value="${item.cust.chartCode}/${item.cust.orgCode}/${item.cust.subUnitCode} ${item.cust.customerDescription}"/></td>
            <td><c:out value="${item.disbursementType.name}"/></td>
            <td align="right"><c:out value="${item.beginDisbursementNbr}"/></td>
            <td align="right"><c:out value="${item.endDisbursementNbr}"/></td>            
            <td align="right"><fmt:formatNumber value="${item.payments}"/></td>
            <td align="right"><fmt:formatNumber value="${item.amount}" type="currency"/></td>
         </tr>
         </c:forEach>
         <tr>
            <td>&nbsp;</td>
            <td><b>Total</b></td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <html:hidden property="totalPaymentCount" />
            <td align="right"><b>${KualiForm.totalPaymentCount}</b></td>
            <td align="right"><b><${KualiForm.currencyFormattedTotalAmount}</b></td>
         </tr>
        </tbody>
      </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>
</kul:page>