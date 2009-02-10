<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="BudgetDocument"
	headerTitle="Research Administration - Cost Share"
	htmlFormAction="researchBudgetCostShare" showTabButtons="true"
	headerDispatch="saveBudgetCostShare" headerTabActive="costshare" renderMultipart="true"
	auditCount="${AuditErrors['costShareAuditErrors'].size}">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />
	
	<div align="right">
		<kul:help documentTypeName="${DataDictionary.BudgetDocument.documentTypeName}" pageName="${CGConstants.COST_SHARE_HEADER_TAB}" altText="page help"/>
	</div>

	<div id="workarea"><kra-b:budgetCostShareInstitutionDirect /> <kra-b:budgetCostShareIndirect />
	<kra-b:budgetCostShare3rdPartyDirect /> <kra-b:budgetCostShareTotals /></div>

	<div align="center"><kfs:documentControls transactionalDocument="false"
		saveButtonOverride="saveBudgetCostShare" suppressRoutingControls="true" viewOnly="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" /></div>

</kul:documentPage>
