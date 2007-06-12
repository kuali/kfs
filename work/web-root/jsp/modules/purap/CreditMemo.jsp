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
    documentTypeName="KualiCreditMemoDocument"
    htmlFormAction="purapCreditMemo" renderMultipart="true"
    showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>
 
    <c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />
    

    <kul:hiddenDocumentFields excludePostingYear="true" />
	
	<!-- purap:hiddenPurapFields / -->
	<!--  I could not use hiddenPurapFields, because I was not sure if CM needs to have a requisitionSourceCode --> 
	<html:hidden property="document.purapDocumentIdentifier" />
	<html:hidden property="document.statusCode" />
	<html:hidden property="document.vendorHeaderGeneratedIdentifier" />
	<html:hidden property="document.vendorDetailAssignedIdentifier" />
   
    <!-- html:hidden property="document.purchaseOrderEncumbranceFiscalYear" / --> 
    <!--  html:hidden property="document.creditMemoCostSourceCode" / -->
    <html:hidden property="document.accountsPayableProcessorIdentifier" />
    <!-- html:hidden property="document.creditMemoInitiated" /-->
    
    <!-- TODO move this to where? -->
    <!-- html:hidden property="document.requisitionIdentifier" / -->
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
	    <kul:documentOverview editingMode="${KualiForm.editingMode}"
	        includePostingYear="true"
	        postingYearAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}" />
	</c:if>
    
    <c:if test="${KualiForm.editingMode['displayInitTab']}" > 
    	<purap:creditMemoInit documentAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}"
	 		 displayCreditMemoInitFields="true" /> 
	</c:if>
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
		<purap:vendor
	        documentAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}" 
	        displayPurchaseOrderFields="false" displayCreditMemoFields="true"/>
		<!--  c:out value="${KualiForm.creditMemoInitiated}" / -->
		
	
		<purap:creditMemoInfo documentAttributes="${DataDictionary.KualiCreditMemoDocument.attributes}"
	 		 displayCreditMemoInfoFields="true" />        
	
	   
	    < kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}" /> 
	    
	
	    < kul:adHocRecipients />
	
	    <kul:routeLog />
	
	
    <purap:relatedDocuments
           documentAttributes="${DataDictionary.RelatedDocuments.attributes}"
           />
	</c:if>
    <kul:panelFooter />
    <c:if test="${KualiForm.editingMode['displayInitTab']}" >
	    <div align="right"><br>
	          ** You must enter one and only one of these fields: PREQ #, Purchase Order #, or Vendor #.</div>
	    </div><br>
	</c:if>       
    <c:if test="${KualiForm.editingMode['displayInitTab']}">
 		<c:set var="extraButtons" value="${KualiForm.extraButtons}" />
 	</c:if>
  	<kul:documentControls 
        transactionalDocument="true"  
        extraButtons="${extraButtons}"  
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}"
       	
    />
   
</kul:documentPage>
