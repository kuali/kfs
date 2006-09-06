<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	htmlFormAction="researchBudgetParameters"
	headerDispatch="saveParameters" feedbackKey="app.krafeedback.link"
	headerTabActive="parameters"
	auditCount="${AuditErrors['parametersAuditErrors'].size}">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		excludeBudgetParameteres="true" />

	<html:hidden property="document.budgetTaskNextSequenceNumber" />
	<html:hidden property="document.budgetPeriodNextSequenceNumber" />
	<html:hidden property="document.personnelNextSequenceNumber" />
	<html:hidden property="document.nonpersonnelNextSequenceNumber" />
	<html:hidden property="document.universityCostShareNextSequenceNumber" />
	<html:hidden property="document.thirdPartyCostShareNextSequenceNumber" />
	
	<kra-b:budgetParameters />

	<kul:documentControls transactionalDocument="false"
		saveButtonOverride="saveParameters" suppressRoutingControls="true"/>

</kul:documentPage>
