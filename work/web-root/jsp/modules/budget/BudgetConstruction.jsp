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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<c:set var="budgetConstructionAttributes"
	value="${DataDictionary['BudgetConstructionDocument'].attributes}" />

<c:if test="${KualiForm.pickListClose}">
<kul:page showDocumentInfo="false"
	htmlFormAction="budgetBudgetConstruction" renderMultipart="false"
	showTabButtons="false"
	docTitle="Budget Construction Document"
    transactionalDocument="false"
	>
</kul:page>
</c:if>

<c:if test="${!KualiForm.pickListClose}">
<kul:page showDocumentInfo="true"
	htmlFormAction="budgetBudgetConstruction" renderMultipart="true"
	showTabButtons="true"
	docTitle="Budget Construction Document"
    transactionalDocument="false"
	>

	<kul:hiddenDocumentFields
		isFinancialDocument="true"
		isTransactionalDocument="false" />
	<html-el:hidden name="KualiForm" property="returnAnchor" />
	<html-el:hidden name="KualiForm" property="returnFormKey" />
	<html-el:hidden name="KualiForm" property="pickListMode" />
	<html-el:hidden name="KualiForm" property="budgetConstructionDocument.benefitsCalcNeeded" />
	<html-el:hidden name="KualiForm" property="budgetConstructionDocument.monthlyBenefitsCalcNeeded" />
	<html-el:hidden name="KualiForm" property="universityFiscalYear" />
	<html-el:hidden name="KualiForm" property="chartOfAccountsCode" />
	<html-el:hidden name="KualiForm" property="accountNumber" />
	<html-el:hidden name="KualiForm" property="subAccountNumber" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
<%--
		includePostingYear="true"
        postingYearOnChange="submitForm()"
        includePostingYearRefresh="true"
        postingYearAttributes="${DataDictionary.BudgetAdjustmentDocument.attributes}" />
--%>
    <bc:systemInformation />

    <bc:budgetConstructionRevenueLines />

    <bc:budgetConstructionExpenditureLines />
    
    <bc:budgetConstructionReportDumpLines />

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
</c:if>
