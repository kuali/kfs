<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRequisitionDocument"
	htmlFormAction="financialRequisition" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields excludePostingYear="true" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true" />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
