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

<kul:documentPage showDocumentInfo="true"
    documentTypeName="PaymentRequestDocument"
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
	<html:hidden property="document.vendorHeaderGeneratedIdentifier" />
	<html:hidden property="document.vendorDetailAssignedIdentifier" />
	<html:hidden property="document.accountsPayablePurchasingDocumentLinkIdentifier" />
	<html:hidden property="document.paymentRequestedCancelIndicator" />
	<html:hidden property="document.holdIndicator" />
	<html:hidden property="document.continuationAccountIndicator" />
	<html:hidden property="document.lastActionPerformedByUniversalUserId" />
   
    <!-- html:hidden property="document.purchaseOrderEncumbranceFiscalYear" / --> 
    <html:hidden property="document.paymentRequestCostSourceCode" />
    <html:hidden property="document.accountsPayableProcessorIdentifier" />
    <c:if test="${not KualiForm.editingMode['displayInitTab'] }">
	    <html:hidden property="document.vendorInvoiceAmount" />
	</c:if>
    <!-- html:hidden property="document.paymentRequestInitiated" /-->
	<html:hidden property="document.processingCampusCode" />
	<html:hidden property="calculated" />
	<html:hidden property="document.unmatchedOverride" />
	    
    <!-- TODO move this to where? -->
    <!-- html:hidden property="document.requisitionIdentifier" / -->

	<!--  Display hold message if payment is on hold -->
	<c:if test="${KualiForm.paymentRequestDocument.holdIndicator}">	
		<h3>This Payment Request has been Held by <c:out value="${KualiForm.paymentRequestDocument.lastActionPerformedByPersonName}"/></h3>		
	</c:if>
	
	<c:if test="${KualiForm.paymentRequestDocument.paymentRequestedCancelIndicator}">	
		<h3>This Payment Request has been Requested for Cancel by <c:out value="${KualiForm.paymentRequestDocument.lastActionPerformedByPersonName}"/></h3>		
	</c:if>
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
	    <kul:documentOverview editingMode="${KualiForm.editingMode}"
	        includePostingYear="true"
	        fiscalYearReadOnly="true"
	        postingYearAttributes="${DataDictionary.PaymentRequestDocument.attributes}" />
	</c:if>
    
    <c:if test="${KualiForm.editingMode['displayInitTab']}" > 
    	<purap:paymentRequestInit documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	 		 displayPaymentRequestInitFields="true" />
	</c:if>
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
		< purap:vendor
	        documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}" 
	        displayPurchaseOrderFields="false" displayPaymentRequestFields="true"/>
		<!--  c:out value="${KualiForm.paymentRequestInitiated}" / -->		
	
		<purap:paymentRequestInvoiceInfo documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	 		 displayPaymentRequestInvoiceInfoFields="true" />        

		<purap:paymentRequestProcessItems 
			documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
			itemAttributes="${DataDictionary.PaymentRequestItem.attributes}"
			accountingLineAttributes="${DataDictionary.PaymentRequestAccount.attributes}" />
		   
	    <purap:summaryaccounts
            itemAttributes="${DataDictionary.PaymentRequestItem.attributes}"
    	    documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" />  
	
		<purap:relatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}"/>
           	
	    <purap:paymentHistory documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    	
        <gl:generalLedgerPendingEntries />

	    <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/>
	
	    <kul:routeLog />
    	
	</c:if>
	
    <kul:panelFooter />
	<c:set var="extraButtons" value="${KualiForm.extraButtons}" />
  	<kul:documentControls 
        transactionalDocument="true"  
        extraButtons="${extraButtons}"  
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}"
       	
    />
   
</kul:documentPage>
