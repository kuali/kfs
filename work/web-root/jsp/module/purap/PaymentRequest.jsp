<%--
 Copyright 2007 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="PaymentRequestDocument"
    htmlFormAction="purapPaymentRequest" renderMultipart="true"
    showTabButtons="true">

    <c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
    <c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />    
    <c:set var="taxInfoViewable" value="${KualiForm.editingMode['taxInfoViewable']}" scope="request" />
    <c:set var="taxAreaEditable" value="${KualiForm.editingMode['taxAreaEditable']}" scope="request" />

	<!--  Display hold message if payment is on hold -->
	<c:if test="${KualiForm.paymentRequestDocument.holdIndicator}">	
		<h4>This Payment Request has been Held by <c:out value="${KualiForm.paymentRequestDocument.lastActionPerformedByPersonName}"/></h4>		
	</c:if>
	
	<c:if test="${KualiForm.paymentRequestDocument.paymentRequestedCancelIndicator}">	
		<h4>This Payment Request has been Requested for Cancel by <c:out value="${KualiForm.paymentRequestDocument.lastActionPerformedByPersonName}"/></h4>		
	</c:if>
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
	    <sys:documentOverview editingMode="${KualiForm.editingMode}"
	        includePostingYear="true"
	        fiscalYearReadOnly="true"
	        postingYearAttributes="${DataDictionary.PaymentRequestDocument.attributes}" >
	        
	    	<purap:purapDocumentDetail
	    		documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	    		detailSectionLabel="Payment Request Detail"
	    		editableAccountDistributionMethod="${KualiForm.readOnlyAccountDistributionMethod}"
	    		paymentRequest="true" />
	    </sys:documentOverview>
	</c:if>
    
    <c:if test="${KualiForm.editingMode['displayInitTab']}" > 
    	<purap:paymentRequestInit 
    		documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	 		displayPaymentRequestInitFields="true" />
	</c:if>
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
		<purap:vendor
	        documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}" 
	        displayPurchaseOrderFields="false" displayPaymentRequestFields="true"/>
	
		<purap:paymentRequestInvoiceInfo 
			documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	 		displayPaymentRequestInvoiceInfoFields="true" />        

	  	<c:if test="${taxInfoViewable || taxAreaEditable}">
		<purap:paymentRequestTaxInfo 
			documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}" />  
	  	</c:if>      

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

		<kul:notes attachmentTypesValuesFinderClass="${DataDictionary.PaymentRequestDocument.attachmentTypesValuesFinderClass}" />
        
	    <kul:adHocRecipients />
	    
	    <kul:routeLog />
    	
	</c:if>
	
    <kul:panelFooter />
	<c:set var="extraButtons" value="${KualiForm.extraButtons}" />
  	<sys:documentControls 
        transactionalDocument="true"  
        extraButtons="${extraButtons}"  
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}"
       	
    />
   
</kul:documentPage>
