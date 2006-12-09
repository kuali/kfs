<%--
 Copyright 2006 The Kuali Foundation.
 
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
	
	<div align="right">
		<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.PERSONNEL_HEADER_TAB}" altText="page help"/>
	</div>

  <c:if test="${! viewOnly}">
    <kra-b:budgetPersonnelAdd />
  </c:if>
	<kra-b:budgetPersonnel />

  <c:if test="${! viewOnly}">
  	<c:set var="extraButtonSource" value="images/buttonsmall_deletesel.gif"/>
  	<c:set var="extraButtonProperty" value="methodToCall.deletePersonnel"/>
  	<c:set var="extraButtonAlt" value="delete"/>
  </c:if>  
  
  	<p>
	<kul:documentControls 
		transactionalDocument="false" 
		saveButtonOverride="savePersonnel" 
		suppressRoutingControls="true"
		extraButtonSource="${extraButtonSource}"
		extraButtonProperty="${extraButtonProperty}"
		extraButtonAlt="${extraButtonAlt}"
		viewOnly="${KualiForm.editingMode['viewOnly']}"
		/>
	</p>


</kul:documentPage>
<!-- END budgetPersonnel.jsp -->
