<%@ include file="/jsp/core/tldHeader.jsp"%>
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRequisitionDocument"
	htmlFormAction="purapRequisition" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields excludePostingYear="true" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true" />

    <purap:vendor
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}"
        displayRequisitionFields="true" />

    <purap:items
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

    <purap:paymentinfo
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

    <purap:delivery
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

    <purap:additional
        editingMode="${KualiForm.editingMode}"
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
