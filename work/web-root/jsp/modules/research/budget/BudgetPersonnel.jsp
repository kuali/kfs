<!-- BEGIN budgetPersonnel.jsp -->
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	htmlFormAction="researchBudgetPersonnel" showTabButtons="true"
	headerDispatch="savePersonnel" headerTabActive="personnel"
	feedbackKey="app.krafeedback.link"
	auditCount="${AuditErrors['personnelAuditErrors'].size}">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="true" />

	<html:hidden property="document.personnelNextSequenceNumber" />

  <c:if test="${! viewOnly}">
    <kra-b:budgetPersonnelAdd />
  </c:if>
	<kra-b:budgetPersonnel />

  <c:if test="${! viewOnly}">
    <html:image src="images/buttonsmall_deletesel.gif" property="methodToCall.deletePersonnel" alt="delete" styleClass="tinybutton" />
  </c:if>  
    
	<kul:documentControls transactionalDocument="false" saveButtonOverride="savePersonnel" suppressRoutingControls="true" />


</kul:documentPage>
<!-- END budgetPersonnel.jsp -->
