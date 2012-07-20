<%--
 Copyright 2006-2008 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>

<%@ page import="org.kuali.kfs.sys.context.SpringContext" %>
<%@ page import="org.kuali.kfs.coa.service.AccountService" %>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<script type='text/javascript'>
function toggle(id) {
  var v=document.getElementById(id); 
  if('none' != v.style.display) {
    v.style.display='none';
  } else {
    v.style.display='';
  }
}
</script>
<c:if test="${!accountingLineScriptsLoaded}">
       <script type='text/javascript' src="dwr/interface/ChartService.js"></script>
       <script type='text/javascript' src="dwr/interface/AccountService.js"></script>
       <script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
       <script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
       <script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
       <script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
       <script type='text/javascript' src="dwr/interface/ProjectCodeService.js"></script>
       <script type='text/javascript' src="dwr/interface/OriginationCodeService.js"></script>
       <script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
       <c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>

<c:set var="accountsCanCrossCharts" value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="paymentApplicationDocumentAttributes" value="${DataDictionary['PaymentApplicationDocument'].attributes}" />
<c:set var="invoiceAttributes" value="${DataDictionary['CustomerInvoiceDocument'].attributes}" />
<c:set var="invoicePaidAppliedAttributes"
	value="${DataDictionary['InvoicePaidApplied'].attributes}" />
<c:set var="customerAttributes" value="${DataDictionary['Customer'].attributes}" />
<c:set var="customerInvoiceDetailAttributes"
	value="${DataDictionary['CustomerInvoiceDetail'].attributes}" />
<c:set var="hasRelatedCashControlDocument" value="${null != KualiForm.cashControlDocument}" />
<c:set var="isCustomerSelected"
	value="${!empty KualiForm.document.accountsReceivableDocumentHeader.customerNumber}" />
<c:set var="invoiceApplications" value="${KualiForm.invoiceApplications}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="PaymentApplicationDocument"
	htmlFormAction="arPaymentApplicationDocument" renderMultipart="true"
	showTabButtons="true">

	<sys:hiddenDocumentFields isFinancialDocument="false" />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

    <ar:paymentApplicationControlInformation isCustomerSelected="${isCustomerSelected}"
        hasRelatedCashControlDocument="${hasRelatedCashControlDocument}"
        customerAttributes="${customerAttributes}"
        customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}"
        invoiceAttributes="${invoiceAttributes}" readOnly="${readOnly}" />

	<ar:paymentApplicationSummaryOfAppliedFunds isCustomerSelected="${isCustomerSelected}"
	   hasRelatedCashControlDocument="${hasRelatedCashControlDocument}" readOnly="${readOnly}" />

	<ar:paymentApplicationQuickApplyToInvoice isCustomerSelected="${isCustomerSelected}"
	   hasRelatedCashControlDocument="${hasRelatedCashControlDocument}"
	   readOnly="${readOnly}" 
	   customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}" 
	   invoiceAttributes="${invoiceAttributes}" />
	
	<ar:paymentApplicationApplyToInvoiceDetail customerAttributes="${customerAttributes}"
		customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}"
		invoiceAttributes="${invoiceAttributes}" readOnly="${readOnly}" />

    <ar:paymentApplicationNonAr customerAttributes="${customerAttributes}"
        isCustomerSelected="${isCustomerSelected}"
        hasRelatedCashControlDocument="${hasRelatedCashControlDocument}"
        readOnly="${readOnly}"
        accountsCanCrossCharts="${accountsCanCrossCharts}"/>
    <ar:paymentApplicationUnappliedTab
		isCustomerSelected="${isCustomerSelected}" readOnly="${readOnly}" 
		hasRelatedCashControlDocument="${hasRelatedCashControlDocument}" />
        
	<gl:generalLedgerPendingEntries />
		            
	<kul:notes />
		
	<kul:adHocRecipients />
	
	<kul:routeLog />
	
	<kul:superUserActions />
	
	<kul:panelFooter />
	
	<sys:documentControls transactionalDocument="true" />
</kul:documentPage>
