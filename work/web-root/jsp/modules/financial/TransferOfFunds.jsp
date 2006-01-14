<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:documentPage showDocumentInfo="true" documentTypeName="KualiTransferOfFundsDocument" htmlFormAction="financialTransferOfFunds" renderMultipart="true" showTabButtons="true">

		<html:hidden property="document.nextSourceLineNumber"/>
		<html:hidden property="document.nextTargetLineNumber"/>
		<kul:hiddenDocumentFields />
        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
	    <fin:accountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}"/>
		<kul:generalLedgerPendingEntries/>
		<kul:notes/>
		<kul:adHocRecipients/>
		<kul:routeLog/>
		<kul:panelFooter/>
		<kul:documentControls transactionalDocument="true" />

</kul:documentPage>