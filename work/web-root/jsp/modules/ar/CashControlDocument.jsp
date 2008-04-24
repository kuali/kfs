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

<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />
<c:set var="editDetails"
	value="${!empty KualiForm.editingMode['editDetails']}" />
<c:set var="showGenerateButton"
	value="${!empty KualiForm.editingMode['showGenerateButton']}" />
<c:set var="editPaymentMedium"
	value="${!empty KualiForm.editingMode['editPaymentMedium']}" />
<c:set var="editRefDocNbr"
	value="${!empty KualiForm.editingMode['editRefDocNbr']}" />
<c:set var="editPaymentAppDoc"
	value="${!empty KualiForm.editingMode['editPaymentAppDoc']}" />
	
<kul:documentPage showDocumentInfo="true"
	documentTypeName="CashControlDocument"
	htmlFormAction="arCashControlDocument" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<kul:hiddenDocumentFields isTransactionalDocument="false" />
	
    <ar:cashControl
        documentAttributes="${DataDictionary.CashControlDocument.attributes}"
        readOnly="${readOnly}"
        showGenerateButton = "${showGenerateButton}"
        editPaymentMedium= "${editPaymentMedium}"
        editRefDocNbr= "${editRefDocNbr}" />
        
    <ar:cashControlDetails
        documentAttributes="${DataDictionary.CashControlDocument.attributes}"
        cashControlDetailAttributes="${DataDictionary.CashControlDetail.attributes}"
        readOnly="${readOnly}"
        editDetails = "${editDetails}"
        editPaymentAppDoc = "${editPaymentAppDoc}"/>  
        
    <gl:generalLedgerPendingEntries />
                
	<kul:notes /> 
	
	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
