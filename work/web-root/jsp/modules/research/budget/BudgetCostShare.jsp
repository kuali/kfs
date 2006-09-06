<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Cost Share"
	htmlFormAction="researchBudgetCostShare" showTabButtons="true"
	headerDispatch="saveBudgetCostShare" headerTabActive="costshare"
	feedbackKey="app.krafeedback.link"
	auditCount="${AuditErrors['costShareAuditErrors'].size}">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="true" />

	<!-- Hidden variables for field level validation. -->
	<logic:iterate id="person" name="KualiForm" property="document.budget.personnel" indexId="personListIndex">
		<html:hidden property="document.budget.personFromList[${personListIndex}].fiscalCampusCode" />
		<html:hidden property="document.budget.personFromList[${personListIndex}].primaryDepartmentCode" />
		<logic:iterate id="userAppointmentTask" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTasks" indexId="userAppointmentTaskIndex">
			<logic:iterate id="userAppointmentTaskPeriod" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriods" indexId="userAppointmentTaskPeriodIndex">
				<html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].budgetPeriodSequenceNumber" />
				<html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].universityCostShareFringeBenefitTotalAmount" />
				<html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].universityCostShareRequestTotalAmount" />
			</logic:iterate>
		</logic:iterate>
	</logic:iterate>
	
	<logic:iterate id="nonpersonnelItem" name="KualiForm" property="document.budget.nonpersonnelItems" indexId="ctr">
		<html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetPeriodSequenceNumber" />
		<html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelCategoryCode" />
		<html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetThirdPartyCostShareAmount" />
		<html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetOriginSequenceNumber" />
		<html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelSequenceNumber" />
		<html:hidden property="document.budget.nonpersonnelItem[${ctr}].budgetNonpersonnelDescription" />
	</logic:iterate>

	<div id="workarea"><kra-b:budgetCostShareInstitutionDirect /> <kra-b:budgetCostShareIndirect />
	<kra-b:budgetCostShare3rdPartyDirect /> <kra-b:budgetCostShareTotals /></div>

	<div align="center"><kul:documentControls transactionalDocument="false"
		saveButtonOverride="saveBudgetCostShare" suppressRoutingControls="true" /></div>

</kul:documentPage>
