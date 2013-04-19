<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true" documentTypeName="SubObjectCodeImportDocument" htmlFormAction="coaSubObjectCodeImport" renderMultipart="true" showTabButtons="true">

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

	<coa:subObjectCodeImpDetails />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
