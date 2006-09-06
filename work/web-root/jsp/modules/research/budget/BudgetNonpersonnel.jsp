<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Non-Personnel Expenses"
	htmlFormAction="researchBudgetNonpersonnel"
	headerTabActive="nonpersonnel" showTabButtons="true"
	headerDispatch="saveNonpersonnel" feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="true" />

	<kra-b:budgetNonpersonnel />

	<div align="center"><kul:documentControls transactionalDocument="false"
		saveButtonOverride="saveNonpersonnel" suppressRoutingControls="true" /></div>

</kul:documentPage>
