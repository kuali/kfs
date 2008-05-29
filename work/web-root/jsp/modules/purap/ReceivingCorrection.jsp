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
    documentTypeName="ReceivingCorrectionDocument"
    htmlFormAction="purapReceivingCorrection" renderMultipart="true"
    showTabButtons="true">

    <kul:hiddenDocumentFields isTransactionalDocument="false" />
        
	<html:hidden property="document.purchaseOrderIdentifier" />
	<html:hidden property="document.accountsPayablePurchasingDocumentLinkIdentifier" />
	<html:hidden property="document.receivingLineDocumentNumber" />

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
    	<c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>
    		    
    <kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<purap:receivingVendor
	    documentAttributes="${DataDictionary.ReceivingCorrectionDocument.attributes}" />

	<purap:receivingCorrectionItems
		itemAttributes="${DataDictionary.ReceivingCorrectionItem.attributes}" />
	
    <purap:delivery
		documentAttributes="${DataDictionary.ReceivingCorrectionDocument.attributes}" 
		deliveryReadOnly="true" />
          	
    <purap:relatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />

	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/>

    <kul:routeLog />
    		
    <kul:panelFooter />
	
  	<kul:documentControls transactionalDocument="true"  />
    
   
</kul:documentPage>
