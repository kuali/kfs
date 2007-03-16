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
	
	<kra:kraAdHocRecipients adhocType="A" adhocLabel="Recipients" disableActionRequested="true" actionRequestedMessage="(upon completion)" actionRequestedDefault="${Constants.WORKFLOW_FYI_REQUEST}" editingMode="${KualiForm.editingMode}"/>
	
	<kul:routeLog />
	
	<kul:panelFooter />

	<div align="center"><kul:documentControls transactionalDocument="false" suppressRoutingControls="true" viewOnly="${KualiForm.editingMode['viewOnly']}"/>
	</div>

</kul:documentPage>