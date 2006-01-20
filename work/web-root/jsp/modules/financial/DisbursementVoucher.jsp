<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialDisbursementVoucher" documentTypeName="KualiDisbursementVoucherDocument"  renderMultipart="true" showTabButtons="true">

        <kul:dvMessages/>
		
		<html:hidden property="document.nextSourceLineNumber"/>
        
        <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
          <c:set var="fullEntryMode" value="true" scope="request"/>
        </c:if>
        <c:if test="${!empty KualiForm.editingMode['frnEntry']}">
          <c:set var="frnEntryMode" value="true" scope="request"/>
        </c:if>
        <c:if test="${!empty KualiForm.editingMode['travelEntry']}">
          <c:set var="travelEntryMode" value="true" scope="request"/>
        </c:if>
        <c:if test="${!empty KualiForm.editingMode['wireEntry']}">
          <c:set var="wireEntryMode" value="true" scope="request"/>
        </c:if>
        <c:if test="${!empty KualiForm.editingMode['taxEntry']}">
          <c:set var="taxEntryMode" value="true" scope="request"/>
        </c:if>
        <c:if test="${!empty KualiForm.editingMode['adminEntry']}">
          <c:set var="adminEntryMode" value="true" scope="request"/>
        </c:if>
        
		<kul:hiddenDocumentFields />

        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
        
        <kul:dvPayee/>
        
        <kul:dvSpecialHandling/>
        
        <kul:dvPayment/>

        <fin:accountingLines sourceAccountingLinesOnly="true" editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}" editableFields="${KualiForm.accountingLineEditableFields}"/>
	    
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
