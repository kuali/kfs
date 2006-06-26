<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiNonCheckDisbursementDocument"
	htmlFormAction="financialNonCheckDisbursement" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<fin:accountingLines sourceAccountingLinesOnly="true"
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		extraSourceRowFields="referenceNumber,financialDocumentLineDescription" />
	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
