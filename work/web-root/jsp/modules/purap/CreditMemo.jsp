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

<kul:documentPage showDocumentInfo="true" documentTypeName="CreditMemoDocument" htmlFormAction="purapCreditMemo" renderMultipart="true" showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>
 
    <c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />
        
    <kul:hiddenDocumentFields excludePostingYear="true" />
	
	<purap:hiddenPurapFields includeNonAPFields="false" />
    <html:hidden property="document.accountsPayableProcessorIdentifier" />
    <html:hidden property="document.processingCampusCode" />
    <html:hidden property="calculated" />
	<html:hidden property="document.unmatchedOverride" />
	<html:hidden property="document.continuationAccountIndicator" />
	    
    <c:if test="${displayInitTab}" > 
    	<purap:creditMemoInit documentAttributes="${DataDictionary.CreditMemoDocument.attributes}" /> 
    	
    	<kul:panelFooter />
    
	    <div align="right"><br><bean:message key="message.creditMemo.initMessage" /></div><br>
	</c:if>
	
	<c:if test="${not displayInitTab}" >
		<!--  Display hold message if payment is on hold -->
	    <c:if test="${KualiForm.document.holdIndicator}">	
		  <h3>This Credit Memo has been Held by <c:out value="${KualiForm.document.lastActionPerformedByPersonName}"/></h3>		
	    </c:if>
	    
		<kul:documentOverview editingMode="${KualiForm.editingMode}" includePostingYear="true" fiscalYearReadOnly="true" postingYearAttributes="${DataDictionary.CreditMemoDocument.attributes}" />
	        
		<purap:vendor documentAttributes="${DataDictionary.CreditMemoDocument.attributes}" displayPurchaseOrderFields="false" displayCreditMemoFields="true"/>
	
		<purap:creditMemoInfo documentAttributes="${DataDictionary.CreditMemoDocument.attributes}" />        

	  	<purap:paymentRequestProcessItems 
			documentAttributes="${DataDictionary.CreditMemoDocument.attributes}"
			itemAttributes="${DataDictionary.CreditMemoItem.attributes}"
			accountingLineAttributes="${DataDictionary.CreditMemoAccount.attributes}" isCreditMemo="true" />
	  
	    <purap:summaryaccounts
            itemAttributes="${DataDictionary.CreditMemoItem.attributes}"
    	    documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" />  
    	    	
		<purap:relatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}"/>
           	
	    <purap:paymentHistory documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	
	    <gl:generalLedgerPendingEntries />

	    <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}" allowsNoteFYI="true"/> 
	
	    <kul:routeLog />
	
        <kul:panelFooter />
	</c:if>
	
	<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
	
  	<kul:documentControls transactionalDocument="true" extraButtons="${extraButtons}" suppressRoutingControls="${displayInitTab}" />
   
</kul:documentPage>
