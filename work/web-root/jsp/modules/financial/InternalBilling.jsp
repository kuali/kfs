<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialInternalBilling"
	documentTypeName="KualiInternalBillingDocument" renderMultipart="true"
	showTabButtons="true">

	<html:hidden property="document.nextItemLineNumber" />
	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" />
	<kul:items editingMode="${KualiForm.editingMode}" />
	<kul:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients editingMode="${KualiForm.editingMode}" />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
