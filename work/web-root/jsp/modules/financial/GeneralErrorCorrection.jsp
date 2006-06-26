<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiGeneralErrorCorrectionDocument"
	htmlFormAction="financialGeneralErrorCorrection" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		extraSourceRowFields="referenceOriginCode,referenceNumber,financialDocumentLineDescription"
		extraTargetRowFields="referenceOriginCode,referenceNumber,financialDocumentLineDescription" />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
