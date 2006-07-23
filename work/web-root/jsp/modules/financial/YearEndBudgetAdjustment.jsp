<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiYearEndBudgetAdjustmentDocument"
	htmlFormAction="financialYearEndBudgetAdjustment" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields/>

	<kul:documentOverview editingMode="${KualiForm.editingMode}"/>

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
