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
    documentTypeName="ReceivingLineDocument"
    htmlFormAction="purapReceivingLine" renderMultipart="true"
    showTabButtons="true">
    
    <kul:hiddenDocumentFields isTransactionalDocument="false" />
    		     		
    <c:choose>
    <c:when test="${!empty KualiForm.editingMode['fullEntry']}">
    	<c:set var="fullEntryMode" value="true" scope="request" />
    </c:when>
    <c:otherwise>
    	<c:set var="fullEntryMode" value="false" scope="request" />
    </c:otherwise>
    </c:choose>
    
	<c:if test="${KualiForm.editingMode['displayInitTab']}" > 
		<html:hidden property="fromPurchaseOrder" />
    	<purap:receivingLineInit documentAttributes="${DataDictionary.ReceivingLineDocument.attributes}"/>
	</c:if>
    
    <c:if test="${not KualiForm.editingMode['displayInitTab']}" >
	<html:hidden property="document.purchaseOrderIdentifier" />
	<html:hidden property="document.accountsPayablePurchasingDocumentLinkIdentifier" />
	<html:hidden property="document.vendorHeaderGeneratedIdentifier" />
	<html:hidden property="document.vendorDetailAssignedIdentifier" />
	<html:hidden property="document.alternateVendorHeaderGeneratedIdentifier" />
	<html:hidden property="document.alternateVendorDetailAssignedIdentifier" />
    
	    <kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
		<purap:receivingVendor
		    documentAttributes="${DataDictionary.ReceivingLineDocument.attributes}" />
	
		<purap:receivingLineItems
			itemAttributes="${DataDictionary.ReceivingLineItem.attributes}" />
		
	    <purap:delivery
			documentAttributes="${DataDictionary.ReceivingLineDocument.attributes}"
			deliveryReadOnly="true" />
		          	
	    <purap:relatedDocuments
	            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />

		<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/>
	
	    <kul:routeLog />
	</c:if>
	    		
    <kul:panelFooter />
	
    <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  	
  	
    <kul:documentControls 
        transactionalDocument="true" 
        extraButtons="${extraButtons}"
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}" />
      
</kul:documentPage>
