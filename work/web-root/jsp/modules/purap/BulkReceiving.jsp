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
    documentTypeName="BulkReceivingDocument"
    htmlFormAction="purapBulkReceiving" renderMultipart="true"
    showTabButtons="true">

    <kfs:hiddenDocumentFields isFinancialDocument="false" />
    		     		
    <c:choose>
    <c:when test="${!empty KualiForm.editingMode['fullEntry']}">
    	<c:set var="fullEntryMode" value="true" scope="request" />
    </c:when>
    <c:otherwise>
    	<c:set var="fullEntryMode" value="false" scope="request" />
    </c:otherwise>
    </c:choose>
    
	<c:if test="${KualiForm.editingMode['displayInitTab']}" > 
    	<purap:bulkReceivingInit documentAttributes="${DataDictionary.BulkReceivingDocument.attributes}"/>
	</c:if>

    <c:choose>
    <c:when test="${not empty KualiForm.document.purchaseOrderIdentifier}" >    
    	<c:set var="isPOAvailable" value="true" scope="request" />
    </c:when>
    <c:otherwise>
    	<c:set var="isPOAvailable" value="false" scope="request" />
    </c:otherwise>
    </c:choose>
    
   <c:if test="${not KualiForm.editingMode['displayInitTab']}" >
	    <kfs:documentOverview editingMode="${KualiForm.editingMode}" />
	
		 <purap:bulkReceivingVendor
		    documentAttributes="${DataDictionary.BulkReceivingDocument.attributes}"/> 
	
	    <purap:bulkReceivingDelivery
			documentAttributes="${DataDictionary.BulkReceivingDocument.attributes}"
			deliveryReadOnly="true" /> 
		          	
	    <purap:relatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	            
		<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/>
	
		<kul:adHocRecipients />
		
	    <kul:routeLog />
	    
	</c:if>
	    		
    <kul:panelFooter />
	
    <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  
  	
    <kfs:documentControls 
        transactionalDocument="true" 
        extraButtons="${extraButtons}"
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}" />
      
</kul:documentPage>
