<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetAdjustmentDocument"
	htmlFormAction="financialBudgetAdjustment" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields excludePostingYear="true" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true" />

	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		currentBaseAmount="true" displayMonthlyAmounts="true" 
		extraHiddenFields=",budgetAdjustmentPeriodCode,fringeBenefitIndicator"
		accountingLineAttributes="${DataDictionary['BudgetAdjustmentSourceAccountingLine'].attributes}" />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
