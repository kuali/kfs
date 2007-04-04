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
<%@ include file="/jsp/core/tldHeader.jsp"%>
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="KualiPaymentRequestDocument"
    htmlFormAction="purapPaymentRequest" renderMultipart="true"
    showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>
 
    <c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />
    

    <kul:hiddenDocumentFields excludePostingYear="true" />
	
	<!-- purap:hiddenPurapFields / -->
	<!--  I could not use hiddenPurapFields, because I was not sure if PREQ needs to have a requisitionSourceCode --> 
	<html:hidden property="document.purapDocumentIdentifier" />
	<html:hidden property="document.statusCode" />
	<!--  html:hidden property="document.vendorHeaderGeneratedIdentifier" / -->
	<!--  html:hidden property="document.vendorDetailAssignedIdentifier" / -->
   
    <html:hidden property="document.purchaseOrderEncumbranceFiscalYear" /> 
    <html:hidden property="document.paymentRequestCostSourceCode" />
    <html:hidden property="document.accountsPayableProcessorIdentifier" />
    <!-- html:hidden property="document.paymentRequestInitiated" /-->
    
    <!-- TODO move this to where? -->
    <!-- html:hidden property="document.requisitionIdentifier" / -->

    <kul:documentOverview editingMode="${KualiForm.editingMode}"
        includePostingYear="true"
        postingYearAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}" />

         
 	<purap:paymentRequestInit documentAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}"
 		 displayPaymentRequestInitFields="true" />
	
	<!--  purap:vendor
        documentAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}" 
        displayPurchaseOrderFields="false" displayPaymentRequestFields="true"/ -->
	<!-- c:out value="${KualiForm.paymentRequestInitiated}" /-->
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
		<purap:paymentRequestInvoiceInfo documentAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}"
	 		 displayPaymentRequestInvoiceInfoFields="true" />        
	</c:if>
    <!-- kul:notes / -->

    <!-- kul:adHocRecipients / -->

    <kul:routeLog />

    <kul:panelFooter />
    <c:if test="${KualiForm.editingMode['displayInitTab']}">
 		<c:set var="extraButtons" value="${KualiForm.extraButtons}" />
 	</c:if>
  	<kul:documentControls 
        transactionalDocument="true"  
        extraButtons="${extraButtons}"  
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}"
       	
    />
    <!-- suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}" -->
</kul:documentPage>
