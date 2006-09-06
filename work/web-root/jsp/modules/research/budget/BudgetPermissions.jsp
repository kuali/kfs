<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Permissions"
	htmlFormAction="researchBudgetPermissions" headerDispatch="save"
	headerTabActive="permissions" feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="false" />

	<%--  %><kra-b:budgetPermissions /> --%>
	
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<kra:kraAdHocRecipients editingMode="${KualiForm.editingMode}"/>
	
	<kul:routeLog />
	
	<kul:panelFooter />

	<div align="center"><kul:documentControls transactionalDocument="false" suppressRoutingControls="true" />
	</div>

</kul:documentPage>
