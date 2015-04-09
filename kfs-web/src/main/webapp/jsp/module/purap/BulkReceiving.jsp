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
    documentTypeName="BulkReceivingDocument"
    htmlFormAction="purapBulkReceiving" renderMultipart="true"
    showTabButtons="true">

    <sys:hiddenDocumentFields isFinancialDocument="false" />
    		     		
    <c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
    
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
	    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
		 <purap:bulkReceivingVendor
		    documentAttributes="${DataDictionary.BulkReceivingDocument.attributes}"/> 
	
	    <purap:bulkReceivingDelivery
			documentAttributes="${DataDictionary.BulkReceivingDocument.attributes}"
			deliveryReadOnly="true" /> 
		          	
	    <purap:relatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	            
		<kul:notes />
	
		<kul:adHocRecipients />
		
	    <kul:routeLog />
	    
	    <kul:superUserActions />
	</c:if>
	    		
    <kul:panelFooter />
	
    <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  
  	
    <sys:documentControls 
        transactionalDocument="true" 
        extraButtons="${extraButtons}"
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}" />
      
</kul:documentPage>
