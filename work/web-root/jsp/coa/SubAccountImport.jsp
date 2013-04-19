<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true" documentTypeName="SubAccountImportDocument" htmlFormAction="coaSubAccountImport" renderMultipart="true" showTabButtons="true">

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

	<coa:subAccountImpDetails />
	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
