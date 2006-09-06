<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiPreEncumbranceDocument"
	htmlFormAction="financialPreEncumbrance" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<fin:preEncumbranceDetails editingMode="${KualiForm.editingMode}" />
	<fin:accountingLines
		extraTargetRowFields="referenceNumber"
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" />
	<kul:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
