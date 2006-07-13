<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiYearEndBudgetAdjustmentDocument"
	htmlFormAction="financialYearEndBudgetAdjustment" renderMultipart="true"
	showTabButtons="true">

	<SCRIPT type="text/javascript">
<!--
    function submitForm() {
        document.forms[0].submit();
    }
//-->
</SCRIPT>

	<kul:hiddenDocumentFields excludePostingYear="true" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true" />

	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		currentBaseAmount="true" displayMonthlyAmounts="true" 
		extraHiddenFields=",budgetAdjustmentPeriodCode,fringeBenefitIndicator" />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
