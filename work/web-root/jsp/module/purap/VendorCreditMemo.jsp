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

<kul:documentPage showDocumentInfo="true" documentTypeName="VendorCreditMemoDocument" htmlFormAction="purapVendorCreditMemo" renderMultipart="true" showTabButtons="true">

    <c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
 
    <c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />
                     
    <c:if test="${displayInitTab}" > 
    	<purap:creditMemoInit documentAttributes="${DataDictionary.VendorCreditMemoDocument.attributes}" /> 
    	
    	<kul:panelFooter />
    
	    <div align="right"><br><bean:message key="message.creditMemo.initMessage" /></div><br>
	</c:if>
	
	<c:if test="${not displayInitTab}" >
		<!--  Display hold message if payment is on hold -->
	    <c:if test="${KualiForm.document.holdIndicator}">	
		  <h4>This Credit Memo has been Held by <c:out value="${KualiForm.document.lastActionPerformedByPersonName}"/></h4>		
	    </c:if>
	    
		<sys:documentOverview editingMode="${KualiForm.editingMode}" includePostingYear="true" fiscalYearReadOnly="true" postingYearAttributes="${DataDictionary.VendorCreditMemoDocument.attributes}" />
	        
		<purap:vendor documentAttributes="${DataDictionary.VendorCreditMemoDocument.attributes}" displayPurchaseOrderFields="false" displayCreditMemoFields="true"/>
	
		<purap:creditMemoInfo documentAttributes="${DataDictionary.VendorCreditMemoDocument.attributes}" />        

	  	<purap:paymentRequestProcessItems 
			documentAttributes="${DataDictionary.VendorCreditMemoDocument.attributes}"
			itemAttributes="${DataDictionary.CreditMemoItem.attributes}"
			accountingLineAttributes="${DataDictionary.CreditMemoAccount.attributes}"
			isCreditMemo="true" />
	  
	    <purap:summaryaccounts
            itemAttributes="${DataDictionary.CreditMemoItem.attributes}"
    	    documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" />  
    	    	
		<purap:customRelatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}"/>
           	
	    <purap:customPaymentHistory documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	
	    <gl:generalLedgerPendingEntries />
	    
		<kul:notes attachmentTypesValuesFinderClass="${documentEntry.attachmentTypesValuesFinderClass}" />
	    
	    <kul:adHocRecipients />
	    
	    <kul:routeLog />
	
		<kul:superUserActions />
	
        <kul:panelFooter />
	</c:if>
	
	<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
	
  	<sys:documentControls transactionalDocument="true" extraButtons="${extraButtons}" suppressRoutingControls="${displayInitTab}" />
   
</kul:documentPage>
