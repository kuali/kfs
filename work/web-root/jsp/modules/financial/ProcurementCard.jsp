<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:documentPage showDocumentInfo="true" documentTypeName="KualiProcurementCardDocument" htmlFormAction="financialProcurementCard"
                  renderMultipart="true" showTabButtons="true">

		<html:hidden property="document.nextSourceLineNumber"/>
		<html:hidden property="document.nextTargetLineNumber"/>
		
		<kul:hiddenDocumentFields />
		
        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
        
        <fin:procurementCardCycleCardDetails/>
     
        <fin:procurementCardAccountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}"/>
	    
		<kul:generalLedgerPendingEntries/>
		
		<kul:notes/>
		
		<kul:adHocRecipients/>
		
		<kul:routeLog/>
		
		<kul:panelFooter/>
		
		<kul:documentControls transactionalDocument="true" />

</kul:documentPage>