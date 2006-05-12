<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:documentPage showDocumentInfo="true" documentTypeName="KualiBudgetAdjustmentDocument" htmlFormAction="financialBudgetAdjustment"
                  renderMultipart="true" showTabButtons="true">

		<html:hidden property="document.nextSourceLineNumber"/>
		<html:hidden property="document.nextTargetLineNumber"/>
		
		<kul:hiddenDocumentFields />
		
        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
        
        <fin:accountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}" displayMonthlyAmounts="true"/>
	    
		<kul:generalLedgerPendingEntries/>
		
		<kul:notes/>
		
		<kul:adHocRecipients/>
		
		<kul:routeLog/>
		
		<kul:panelFooter/>
		
		<kul:documentControls transactionalDocument="true" />

</kul:documentPage>