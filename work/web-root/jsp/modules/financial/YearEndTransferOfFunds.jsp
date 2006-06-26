<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiYearEndTransferOfFundsDocument"
	htmlFormAction="financialYearEndTransferOfFunds" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields />
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" />
	<kul:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
