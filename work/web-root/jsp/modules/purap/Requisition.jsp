<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
	documentTypeName="RequisitionDocument"
	htmlFormAction="purapRequisition" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<kul:hiddenDocumentFields excludePostingYear="true" />

    <purap:hiddenPurapFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true"
        fiscalYearReadOnly="true"
        postingYearAttributes="${DataDictionary.RequisitionDocument.attributes}" >

    	<purap:purapDocumentDetail
	    	documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
	    	detailSectionLabel="Requisition Detail"
	    	editableFundingSource="true" />
    </kul:documentOverview>
	
    <purap:vendor
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
        displayRequisitionFields="true" />

    <purap:puritems itemAttributes="${DataDictionary.RequisitionItem.attributes}"
    	accountingLineAttributes="${DataDictionary.RequisitionAccount.attributes}" 
    	displayRequisitionFields="true"/>

    <purap:paymentinfo
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}" />

    <purap:delivery
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}" />

    <purap:additional
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
        displayRequisitionFields="true" />
    
    <!-- purap:capitalAsset 
    	documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
        displayRequisitionFields="true" / -->
     
    <!-- TEMPORARILY DISABLING ACCOUNT SUMMARY FUNCTIONALITY -->
    <purap:summaryaccounts
        itemAttributes="${DataDictionary.RequisitionItem.attributes}"
    	documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" />

    <purap:relatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    
    <purap:paymentHistory
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	            
	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
