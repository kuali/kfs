<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
	
		<purap:customRelatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}"/>

	    <purap:customPaymentHistory documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    	
        <gl:generalLedgerPendingEntries />

		<kul:notes attachmentTypesValuesFinderClass="${DataDictionary.PaymentRequestDocument.attachmentTypesValuesFinderClass}" />
        
	    <kul:adHocRecipients />
	    
	    <kul:routeLog />
	    
    	<kul:superUserActions />
	</c:if>
	
    <kul:panelFooter />
	<c:set var="extraButtons" value="${KualiForm.extraButtons}" />
  	<sys:documentControls 
        transactionalDocument="true"  
        extraButtons="${extraButtons}"  
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}"
       	
    />
   
</kul:documentPage>
