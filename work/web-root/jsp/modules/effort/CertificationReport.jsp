<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<c:set var="documentTypeName" value="EffortCertificationDocument"/>
<c:set var="htmlFormAction" value="effortCertificationRecreate"/>

<c:set var="readOnly" value="${KualiForm.editingMode[EffortConstants.EffortCertificationEditMode.VIEW_ONLY] == true}" />
<c:set var="fullEntry" value="${KualiForm.editingMode[EffortConstants.EffortCertificationEditMode.FULL_ENTRY] == true}" />
<c:set var="projectEntry" value="${KualiForm.editingMode[EffortConstants.EffortCertificationEditMode.PROJECT_ENTRY] == true}" />
<c:set var="expenseEntry" value="${KualiForm.editingMode[EffortConstants.EffortCertificationEditMode.EXPENSE_ENTRY] == true}" />

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="effortCertificationReport"
	documentTypeName="${documentTypeName}" renderMultipart="true"
	showTabButtons="true">
	
	<html:hidden property="document.effortCertificationReportNumber" />
    <html:hidden property="document.effortCertificationDocumentCode" />
    <html:hidden property="document.universityFiscalYear" />
    <html:hidden property="document.emplid" />
    <html:hidden property="sortOrder" />
		 		
	<kul:hiddenDocumentFields isFinancialDocument="false" />
		
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<er:reportInformation />
	
    <c:set var="hiddenFieldNames" value="effortCertificationDocumentCode,totalOriginalPayrollAmount"/>
	<c:forTokens var="fieldName" items="${hiddenFieldNames}" delims=",">	
		<input type="hidden" name="document.${fieldName}" id="document.${fieldName}" value="${KualiForm.document[fieldName]}"/>		  
	</c:forTokens>
 
	<er:detailTab/>

	<kul:notes />
	
	<kul:adHocRecipients />
	
	<kul:routeLog />
	
	<kul:panelFooter />
	
	<kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
