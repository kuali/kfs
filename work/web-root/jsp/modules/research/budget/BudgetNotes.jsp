<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	htmlFormAction="researchBudgetNotes" headerDispatch="notes"
	headerTabActive="notes" feedbackKey="app.krafeedback.link" renderMultipart="true" >

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true" />

	<kra-b:budgetNotes />

</kul:documentPage>
