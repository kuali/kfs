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
<%@ include file="/jsp/core/tldHeader.jsp"%>
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRequisitionDocument"
	htmlFormAction="purapRequisition" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<kul:hiddenDocumentFields excludePostingYear="true" />

    <purap:hiddenPurapFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true"
        postingYearAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" >

    	<purap:purapDocumentDetail
	    	documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}"
	    	detailSectionLabel="Requisition Detail" />
    </kul:documentOverview>
	
    <purap:vendor
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}"
        displayRequisitionFields="true" />

    <purap:puritems itemAttributes="${DataDictionary.RequisitionItem.attributes}"
    	accountingLineAttributes="${DataDictionary.RequisitionAccountingLine.attributes}"/>

    <purap:paymentinfo
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

    <purap:delivery
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" 
        displayRequisitionFields="true"
        />

    <purap:additional
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}"
        displayRequisitionFields="true" />

    <!-- purap:viewRelatedDocuments
            documentAttributes="${DataDictionary.SourceDocumentReference.attributes}"
            / -->
    
	<purap:statushistory 
		documentAttributes="${DataDictionary.RequisitionStatusHistory.attributes}" />

	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}" /> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
