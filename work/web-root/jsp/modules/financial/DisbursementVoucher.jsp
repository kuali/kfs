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

	<html:hidden property="document.dvPayeeDetail.financialDocumentNumber" />
	<html:hidden property="document.dvPayeeDetail.versionNumber" />
	<html:hidden
		property="document.dvNonEmployeeTravel.financialDocumentNumber" />
	<html:hidden property="document.dvNonEmployeeTravel.versionNumber" />
	<html:hidden
		property="document.dvPreConferenceDetail.financialDocumentNumber" />
	<html:hidden property="document.dvPreConferenceDetail.versionNumber" />
	<html:hidden property="document.dvWireTransfer.financialDocumentNumber" />
	<html:hidden property="document.dvWireTransfer.versionNumber" />
	<html:hidden
		property="document.dvNonResidentAlienTax.financialDocumentNumber" />
	<html:hidden property="document.dvNonResidentAlienTax.versionNumber" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<dv:dvPayee />

	<dv:dvSpecialHandling />

	<dv:dvPayment />

	<fin:accountingLines sourceAccountingLinesOnly="true"
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		editableFields="${KualiForm.accountingLineEditableFields}"
		extraSourceRowFields="financialDocumentLineDescription" />

	<dv:dvContact />

	<dv:dvNRATax />

	<dv:dvWireTransfer />

	<dv:dvForeignDraft />

	<dv:dvNonEmployeeTravel />

	<dv:dvPrePaidTravel />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
