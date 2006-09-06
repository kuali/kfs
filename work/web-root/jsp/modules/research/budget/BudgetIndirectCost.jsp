<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Indirect Costs"
	htmlFormAction="researchBudgetIndirectCost"
	headerDispatch="save" headerTabActive="indirectcost"
	feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="true" />

	<kra-b:budgetIndirectCost />

  <logic:iterate id="budgetIndirectCostLookup" name="KualiForm" property="document.budget.budgetIndirectCostLookups" indexId="i">
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].documentHeaderId" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetOnCampusIndicator" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetPurposeCode" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetIndirectCostRate" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].objectId" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].versionNumber" />
  </logic:iterate>

	<div align="center"><kul:documentControls transactionalDocument="false" suppressRoutingControls="true" />
	</div>

</kul:documentPage>
