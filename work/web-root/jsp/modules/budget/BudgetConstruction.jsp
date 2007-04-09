<%--
 Copyright 2007 The Kuali Foundation.
 
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
<c:set var="budgetConstructionAttributes"
	value="${DataDictionary['KualiBudgetConstructionDocument'].attributes}" />

<kul:page showDocumentInfo="true"
	htmlFormAction="budgetBudgetConstruction" renderMultipart="true"
	showTabButtons="true"
	docTitle="Budget Construction Document"
    transactionalDocument="false"
	>

	<kul:hiddenDocumentFields
		isFinancialDocument="true"
		isTransactionalDocument="false" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
<%--
		includePostingYear="true"
        postingYearOnChange="submitForm()"
        includePostingYearRefresh="true"
        postingYearAttributes="${DataDictionary.KualiBudgetAdjustmentDocument.attributes}" />
--%>
    <bc:systemInformation />

    <bc:budgetConstructionRevenueLines />

    <bc:budgetConstructionExpenditureLines />

	<kul:notes />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="false"
		suppressRoutingControls="true" viewOnly="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
<SCRIPT type="text/javascript">
  subObjectCodeNameSuffix = ".financialSubObject.financialSubObjectCdshortNm";
  var kualiForm = document.forms['KualiForm'];
  var kualiElements = kualiForm.elements;
</SCRIPT>
</kul:page>
