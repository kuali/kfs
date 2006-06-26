<%@ include file="/jsp/core/tldHeader.jsp"%>


<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialYearEndDistributionOfIncomeAndExpense"
	documentTypeName="KualiYearEndDistributionOfIncomeAndExpenseDocument"
	renderMultipart="true" showTabButtons="true">

	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
