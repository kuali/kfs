<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Audit Mode"
	htmlFormAction="researchBudgetAuditMode" headerDispatch="auditmode"
	headerTabActive="auditmode"
	showTabButtons="${KualiForm.auditActivated}"
	feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="false" />

	<kra-b:budgetAuditMode />

</kul:documentPage>
