<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Overview"
	htmlFormAction="researchBudgetOverview" showTabButtons="true"
	headerDispatch="overview" headerTabActive="overview"
	feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="true" />

	<kra-b:budgetOverview />

        <div id="globalbuttons" class="globalbuttons">
          <c:if test="${KualiForm.documentActionFlags.canRoute}">
	          <html:image src="images/buttonsmall_complete.gif" styleClass="globalbuttons" property="methodToCall.route" alt="Complete Budget" />
	        </c:if>
        </div>

</kul:documentPage>
