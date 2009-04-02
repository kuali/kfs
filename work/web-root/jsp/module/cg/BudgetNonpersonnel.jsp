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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="BudgetDocument"
	headerTitle="Research Administration - Non-Personnel Expenses"
	htmlFormAction="researchBudgetNonpersonnel" renderMultipart="true"
	headerTabActive="nonpersonnel" showTabButtons="true"
	headerDispatch="saveNonpersonnel">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<cg:budgetNonpersonnel />

	<div align="center"><sys:documentControls transactionalDocument="false"
		saveButtonOverride="saveNonpersonnel" suppressRoutingControls="true" viewOnly="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" /></div>
<SCRIPT type="text/javascript">
var kualiForm = document.forms['KualiForm'];
var kualiElements = kualiForm.elements;
</SCRIPT>
<script language="javascript" src="scripts/research/researchDocument.js"></script>
<script language="javascript" src="dwr/interface/SubcontractorService.js"></script>

</kul:documentPage>
