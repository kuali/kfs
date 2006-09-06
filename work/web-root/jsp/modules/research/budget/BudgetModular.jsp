<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	htmlFormAction="researchBudgetModular" headerDispatch="save"
	headerTabActive="modular" showTabButtons="true"
	feedbackKey="app.krafeedback.link"
	auditCount="${AuditErrors['modularSoftAuditErrors'].size}">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="false" />

	<kra-b:budgetModular />
		
	<div align="center"><kul:documentControls transactionalDocument="false" saveButtonOverride="saveAndRegenerate" suppressRoutingControls="true"/>
	</div>

</kul:documentPage>
