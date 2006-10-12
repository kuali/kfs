<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Permissions"
	htmlFormAction="researchBudgetPermissions" 
	showTabButtons="true"
	headerDispatch="save"
	headerTabActive="permissions" 
	feedbackKey="app.krafeedback.link">
	
	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />
	
	<div align="right">
		<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.PERMISSIONS_HEADER_TAB}" altText="page help"/>
	</div>

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="false" />
	
	<kra:kraAdHocRecipients editingMode="${KualiForm.editingMode}"/>
	
	<kul:routeLog />
	
	<kul:panelFooter />

	<div align="center"><kul:documentControls transactionalDocument="false" suppressRoutingControls="true" />
	</div>

</kul:documentPage>
