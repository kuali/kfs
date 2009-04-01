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
	documentTypeName="RoutingFormDocument"
	htmlFormAction="researchRoutingFormApprovals"
	headerDispatch="save" headerTabActive="approvals" showTabButtons="true">
	
	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />
	
	<div align="right">
		<kul:help documentTypeName="${DataDictionary.RoutingFormDocument.documentTypeName}" pageName="Approvals" altText="page help"/>
	</div>	
	
	<kra:kraAdHocRecipients adhocType="A" adhocLabel="Requests" excludeActionRequested="false" disableActionRequested="true" actionRequestedDefault="${Constants.WORKFLOW_APPROVE_REQUEST}" editingMode="${KualiForm.editingMode}"/>
	
	<kul:routeLog />
	
	<kul:panelFooter />
	
	<c:if test="${KualiForm.numAuditErrors != 0}">
		<div style="color:orange;font-weight:bold"><center>ATTENTION: You must fix ${KualiForm.numAuditErrors} error(s) before routing this Routing Form. Navigate to the "Audit Mode" tab to view all of the errors.</center></div>
	</c:if>
	
	<div style="color:green;font-weight:bold"><br/><center>${KualiForm.approvalsMessage}</center></div>
	
	<sys:documentControls transactionalDocument="false" suppressRoutingControls="${KualiForm.numAuditErrors != 0}" viewOnly="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	
</kul:documentPage>
