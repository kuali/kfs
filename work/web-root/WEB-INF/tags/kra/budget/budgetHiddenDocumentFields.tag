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
<%@ include file="/jsp/core/tldHeader.jsp" %>
<%@ attribute name="includeDocumenHeaderIdFields" required="false" %>
<%@ attribute name="includeTaskPeriodLists" required="false" %>
<%@ attribute name="excludeBudgetParameteres" required="false" %>
<%@ attribute name="excludeIndirectCost" required="false" %>

<c:set var="excludeBudgetParameteres" value="${not empty excludeBudgetParameteres}"/>

    <html:hidden property="document.budget.documentNumber" />
    <html:hidden property="document.budget.versionNumber" />
    <html:hidden property="document.objectId" />
    <html:hidden property="document.documentHeader.financialDocumentStatusCode" />
		
    <c:if test="${includeDocumenHeaderIdFields == 'true' || includeDocumenHeaderIdFields == 'TRUE'}">
      <html:hidden property="document.documentHeader.versionNumber" />
      <html:hidden property="document.documentHeader.documentNumber" />
    </c:if>

    <c:if test="${includeTaskPeriodLists == 'true' || includeTaskPeriodLists== 'TRUE'}">
      <!-- save state for Budget Tasks -->
      <logic:iterate id="tasks" name="KualiForm" property="document.budget.tasks" indexId="ctr">
        <html:hidden property="document.budget.task[${ctr}].documentNumber" />
        <html:hidden property="document.budget.task[${ctr}].budgetTaskSequenceNumber" />
        <html:hidden property="document.budget.task[${ctr}].budgetTaskName" />
        <html:hidden property="document.budget.task[${ctr}].versionNumber" />
        <html:hidden property="document.budget.task[${ctr}].objectId" />
      </logic:iterate>
      <!-- save state for Budget Periods -->
      <logic:iterate id="periods" name="KualiForm" property="document.budget.periods" indexId="ctr">
        <html:hidden property="document.budget.period[${ctr}].documentNumber" />
        <html:hidden property="document.budget.period[${ctr}].budgetPeriodSequenceNumber" />
        <html:hidden property="document.budget.period[${ctr}].budgetPeriodBeginDate" />
        <html:hidden property="document.budget.period[${ctr}].budgetPeriodEndDate" />
        <html:hidden property="document.budget.period[${ctr}].versionNumber" />
        <html:hidden property="document.budget.period[${ctr}].objectId" />
      </logic:iterate>
    </c:if>
    
    <c:if test="${!excludeBudgetParameteres}">
      <html:hidden property="document.budget.projectDirector.universalUser.personName" />
      <html:hidden property="document.budget.projectDirector.universalUser.personUniversalIdentifier"/>
      <html:hidden property="document.budget.projectDirector.personUniversalIdentifier"/>
      <html:hidden property="document.budget.budgetAgencyNumber" />
      <html:hidden property="document.budget.budgetAgency.fullName" />

      <html:hidden property="document.budget.budgetPersonnelInflationRate" />
      <html:hidden property="document.budget.budgetNonpersonnelInflationRate" />
      
      
      <html:hidden property="document.budget.agencyModularIndicator" />
      
      <html:hidden property="document.documentHeader.financialDocumentDescription" />
      
    </c:if>
    
    <html:hidden property="document.budget.agencyToBeNamedIndicator" />
    <html:hidden property="document.budget.projectDirectorToBeNamedIndicator" />
    <html:hidden property="auditActivated" />
    
    <kul:hiddenDocumentFields isFinancialDocument="false" isTransactionalDocument="false"/>
    