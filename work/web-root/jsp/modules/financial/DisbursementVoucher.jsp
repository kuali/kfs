<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialDisbursementVoucher" documentTypeName="KualiDisbursementVoucherDocument"  renderMultipart="true" showTabButtons="true">

        <kul:dvMessages/>
		
		<html:hidden property="document.nextSourceLineNumber"/>
		<html:hidden property="document.nextTargetLineNumber"/>
		<html:hidden property="document.dvNonEmployeeExpense.financialDocumentNumber" value="document.financialDocumentNumber"/>
		<html:hidden property="document.dvNonEmployeeTravel.financialDocumentNumber" value="document.financialDocumentNumber"/>
	    <html:hidden property="document.dvNonResidentAlienTax.financialDocumentNumber" value="document.financialDocumentNumber"/>
		<html:hidden property="document.dvPayeeDetail.financialDocumentNumber" value="document.financialDocumentNumber"/>
		<html:hidden property="document.dvPreConferenceDetail.financialDocumentNumber" value="document.financialDocumentNumber"/>
		<html:hidden property="document.dvPreConferenceRegistrant.financialDocumentNumber" value="document.financialDocumentNumber"/>
		<html:hidden property="document.dvWireTransfer.financialDocumentNumber" value="document.financialDocumentNumber"/>
		
		<kul:hiddenDocumentFields />

        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
        
        <kul:dvPayee/>
        
        <kul:dvSpecialHandling/>
        
        <kul:dvPayment/>

        <fin:accountingLines sourceAccountingLinesOnly="true" editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}"/>
	    
	    <kul:dvContact/>
	    
	    <kul:dvNRATax/>
	    
	    <kul:dvWireTransfer/>
	    
	    <kul:dvForeignDraft/>
		
		<kul:generalLedgerPendingEntries/>

		<kul:notes editingMode="${KualiForm.editingMode}"/>
			
		<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>		
			
		<kul:routeLog/>
		
		<kul:panelFooter/>

		<kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
