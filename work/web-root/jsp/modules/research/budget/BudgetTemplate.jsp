<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	htmlFormAction="researchBudgetTemplate" headerDispatch="template"
	headerTabActive="template" feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true" />

	<kra-b:budgetTemplate />

	<div id="globalbuttons" class="globalbuttons"><html:image
		src="images/buttonsmall_template.gif" styleClass="globalbuttons"
		property="methodToCall.doTemplate" alt="Copy current document" /></div>

</kul:documentPage>
