<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Non-Personnel Copy Over"
	htmlFormAction="researchBudgetNonpersonnelCopyOver"
	headerTabActive="nonpersonnel" showTabButtons="true"
	feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="true" />

	<kra-b:budgetNonpersonnelCopyOver />

	<div id="globalbuttons" class="globalbuttons" align="center"><c:if test="${!KualiForm.editingMode['viewOnly']}"><html:image
		src="images/buttonsmall_return.gif" styleClass="globalbuttons"
		property="methodToCall.returnNonpersonnel" alt="return" /></c:if> <html:image
		src="images/buttonsmall_cancel.gif" styleClass="globalbuttons"
		property="methodToCall.cancel" alt="cancel" /></div>

</kul:documentPage>
