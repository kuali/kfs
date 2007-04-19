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
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormApprovals"
	headerDispatch="save" feedbackKey="app.krafeedback.link"
	headerTabActive="approvals">
	
	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />
	
	<kra-rf:routingFormHiddenDocumentFields />
	
	<kra:kraAdHocRecipients adhocType="A" adhocLabel="Recipients" excludeActionRequested="false" disableActionRequested="true" actionRequestedDefault="${Constants.WORKFLOW_APPROVE_REQUEST}" editingMode="${KualiForm.editingMode}"/>
	
	<kul:routeLog />
	
	<kul:panelFooter />
	
	<html:hidden property="numAuditErrors"/>
	<c:if test="${KualiForm.numAuditErrors != 0}">
		<div style="color:orange;font-weight:bold"><center>ATTENTION: You must fix ${KualiForm.numAuditErrors} error(s) before routing this Routing Form. Navigate to the "Audit Mode" tab to view all of the errors.</center></div>
	</c:if>
	
	<div style="color:green;font-weight:bold"><br/><center>${KualiForm.approvalsMessage}</center></div>
	
	<kul:documentControls transactionalDocument="false" suppressRoutingControls="${KualiForm.numAuditErrors != 0}" viewOnly="${KualiForm.editingMode['viewOnly']}" />
	
</kul:documentPage>