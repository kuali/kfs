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

<kul:documentPage showDocumentInfo="true" documentTypeName="KualiCreditMemoDocument" htmlFormAction="purapCreditMemo" renderMultipart="true" showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>
 
    <c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />

    <kul:hiddenDocumentFields excludePostingYear="true" />
	
	<purap:hiddenPurapFields includeNonAPFields="false" />
    <html:hidden property="document.accountsPayableProcessorIdentifier" />
    <html:hidden property="document.processingCampusCode" />
    
    <c:if test="${displayInitTab}" > 
    	<purap:creditMemoInit documentAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}" /> 
    	
    	<kul:panelFooter />
    
	    <div align="right"><br>
	          ** You must enter one and only one of these fields: PREQ #, Purchase Order #, or Vendor #.</div>
	    </div><br>
	  
 		<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
	</c:if>
	
	<c:if test="${not displayInitTab}" >
		<kul:documentOverview editingMode="${KualiForm.editingMode}" includePostingYear="true" postingYearAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}" />
	        
		<purap:vendor documentAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}" displayPurchaseOrderFields="false" displayCreditMemoFields="true"/>
	
		<purap:creditMemoInfo documentAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}" />        
	   
	    <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}" /> 
	
	    <kul:adHocRecipients />
	
	    <kul:routeLog />
	
        <purap:relatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	</c:if>
	
  	<kul:documentControls transactionalDocument="true" extraButtons="${extraButtons}" suppressRoutingControls="${displayInitTab}" />
   
</kul:documentPage>
