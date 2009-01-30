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
	htmlFormAction="researchBudgetParameters"
	headerDispatch="saveParameters"
	headerTabActive="parameters"
	showTabButtons="true" renderMultipart="true"
	auditCount="${AuditErrors['parametersAuditErrors'].size}">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS_LESS_DOCUMENT}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="false"
		excludeBudgetParameteres="true" />

	<html:hidden property="document.budgetTaskNextSequenceNumber" />
	<html:hidden property="document.budgetPeriodNextSequenceNumber" />
	<html:hidden property="document.personnelNextSequenceNumber" />
	<html:hidden property="document.nonpersonnelNextSequenceNumber" />
	<html:hidden property="document.institutionCostShareNextSequenceNumber" />
	<html:hidden property="document.thirdPartyCostShareNextSequenceNumber" />
	
	<kra-b:budgetParameters />

	<kfs:documentControls transactionalDocument="false"
		saveButtonOverride="saveParameters" suppressRoutingControls="true" viewOnly="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<SCRIPT type="text/javascript">
var kualiForm = document.forms['KualiForm'];
var kualiElements = kualiForm.elements;
</SCRIPT>
<script language="javascript" src="scripts/research/researchDocument.js"></script>
<script language="javascript" src="dwr/interface/AgencyService.js"></script>
<script language="javascript" src="dwr/interface/PersonService.js"></script>
</kul:documentPage>
