<%--
 Copyright 2005-2006 The Kuali Foundation.
 
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
<%@ taglib tagdir="/WEB-INF/tags/dv" prefix="dv"%>

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialDisbursementVoucher"
	documentTypeName="KualiDisbursementVoucherDocument"
	renderMultipart="true" showTabButtons="true">

	<dv:dvPrintCoverSheet />

	<dv:dvMessages />

	<c:if test="${!empty KualiForm.editingMode['fullEntry']}">
		<c:set var="fullEntryMode" value="true" scope="request" />
	</c:if>
	<c:if test="${!empty KualiForm.editingMode['frnEntry']}">
		<c:set var="frnEntryMode" value="true" scope="request" />
	</c:if>
	<c:if test="${!empty KualiForm.editingMode['travelEntry']}">
		<c:set var="travelEntryMode" value="true" scope="request" />
	</c:if>
	<c:if test="${!empty KualiForm.editingMode['wireEntry']}">
		<c:set var="wireEntryMode" value="true" scope="request" />
	</c:if>
	<c:if test="${!empty KualiForm.editingMode['taxEntry']}">
		<c:set var="taxEntryMode" value="true" scope="request" />
	</c:if>
	<c:if test="${!empty KualiForm.editingMode['adminEntry']}">
		<c:set var="adminEntryMode" value="true" scope="request" />
	</c:if>

	<kul:hiddenDocumentFields />

	<html:hidden property="document.dvPayeeDetail.documentNumber" />
	<html:hidden property="document.dvPayeeDetail.versionNumber" />
	<html:hidden
		property="document.dvNonEmployeeTravel.documentNumber" />
	<html:hidden property="document.dvNonEmployeeTravel.versionNumber" />
	<html:hidden
		property="document.dvPreConferenceDetail.documentNumber" />
	<html:hidden property="document.dvPreConferenceDetail.versionNumber" />
	<html:hidden property="document.dvWireTransfer.documentNumber" />
	<html:hidden property="document.dvWireTransfer.versionNumber" />
	<html:hidden
		property="document.dvNonResidentAlienTax.documentNumber" />
	<html:hidden property="document.dvNonResidentAlienTax.versionNumber" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<dv:dvPayee />

	<dv:dvPayment />

	<fin:accountingLines sourceAccountingLinesOnly="true"
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		editableFields="${KualiForm.accountingLineEditableFields}"
		extraSourceRowFields="financialDocumentLineDescription" />

	<dv:dvContact />

    <dv:dvSpecialHandling />

	<dv:dvNRATax />

	<dv:dvWireTransfer />

	<dv:dvForeignDraft />

	<dv:dvNonEmployeeTravel />

	<dv:dvPrePaidTravel />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
