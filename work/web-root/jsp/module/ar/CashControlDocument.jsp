<%--
 Copyright 2006-2008 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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

	<c:if
		test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
		<c:set var="fullEntryMode" value="true" scope="request" />
	</c:if>

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

	<sys:hiddenDocumentFields isFinancialDocument="false" />

	<ar:cashControl
		documentAttributes="${DataDictionary.CashControlDocument.attributes}"
		readOnly="${readOnly}" showGenerateButton="${showGenerateButton}"
		editPaymentMedium="${editPaymentMedium}"
		editBankCode="${editBankCode}" showBankCode="${showBankCode}"
		editRefDocNbr="${editRefDocNbr}" />

	<ar:cashControlDetails
		documentAttributes="${DataDictionary.CashControlDocument.attributes}"
		cashControlDetailAttributes="${DataDictionary.CashControlDetail.attributes}"
		readOnly="${readOnly}" editDetails="${editDetails}"
		editPaymentAppDoc="${editPaymentAppDoc}" />

	<gl:generalLedgerPendingEntries />

	<kul:notes />

	<kul:routeLog />
<kul:superUserActions />
	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true"
		extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
