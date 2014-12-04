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
    documentTypeName="LineItemReceivingDocument"
    htmlFormAction="purapLineItemReceiving" renderMultipart="true"
    showTabButtons="true">
    		     		
    <c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
    
	<c:if test="${KualiForm.editingMode['displayInitTab']}" > 
    	<purap:receivingLineInit documentAttributes="${DataDictionary.LineItemReceivingDocument.attributes}"/>
	</c:if>
    
    <c:if test="${not KualiForm.editingMode['displayInitTab']}" >
	
	    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
		<purap:receivingVendor documentAttributes="${DataDictionary.LineItemReceivingDocument.attributes}" />
	
		<purap:receivingLineItems itemAttributes="${DataDictionary.LineItemReceivingItem.attributes}" />
		
	    <purap:delivery
			documentAttributes="${DataDictionary.LineItemReceivingDocument.attributes}"
			deliveryReadOnly="true" />
		          	
	    <purap:relatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />

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
