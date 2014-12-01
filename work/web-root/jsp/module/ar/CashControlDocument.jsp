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

<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
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
<c:set var="editBankCode"
	value="${!empty KualiForm.editingMode['editBankCode']}" />
<c:set var="showBankCode"
	value="${!empty KualiForm.editingMode['showBankCode']}" />	
	
<kul:documentPage showDocumentInfo="true"
	documentTypeName="CashControlDocument"
	htmlFormAction="arCashControlDocument" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />
	
    <ar:cashControl
        documentAttributes="${DataDictionary.CashControlDocument.attributes}"
        readOnly="${readOnly}"
        showGenerateButton = "${showGenerateButton}"
        editPaymentMedium= "${editPaymentMedium}"
        editBankCode = "${editBankCode}"
        showBankCode = "${showBankCode}"
        editRefDocNbr = "${editRefDocNbr}" />
        
    <ar:cashControlDetails
        documentAttributes="${DataDictionary.CashControlDocument.attributes}"
        cashControlDetailAttributes="${DataDictionary.CashControlDetail.attributes}"
        readOnly="${readOnly}"
        editDetails = "${editDetails}"
        editPaymentAppDoc = "${editPaymentAppDoc}"/>  
        
    <gl:generalLedgerPendingEntries />
                
	<kul:notes /> 
	
	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true" />

</kul:documentPage>
