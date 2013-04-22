<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true" documentTypeName="ProjectCodeImportDocument" htmlFormAction="coaProjectCodeImport" renderMultipart="true" showTabButtons="true">

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

	<coa:projectCodeImpDetails />
	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
