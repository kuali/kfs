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
<%@ attribute name="includeRoutingFormMainPage" required="false" %>

<c:set var="includeRoutingFormMainPage" value="${not empty includeRoutingFormMainPage ? includeRoutingFormMainPage : 'false'}"/>

   <html:hidden property="document.documentNumber" />
   <html:hidden property="document.routingFormCreateDate" />

   <html:hidden property="document.documentHeader.versionNumber" />
   <html:hidden property="document.objectId" />
   <html:hidden property="document.documentHeader.documentNumber" />
   <html:hidden property="document.documentHeader.financialDocumentStatusCode" />

    <html:hidden property="auditActivated" />
    
    <c:choose>
      <c:when test="${includeRoutingFormMainPage}">
        <html:hidden property="document.routingFormBudgetNumber" />
        <html:hidden property="document.institutionCostShareNextSequenceNumber" />
        <html:hidden property="document.otherCostShareNextSequenceNumber" />
        <html:hidden property="document.subcontractorNextSequenceNumber" />
      </c:when>
      <c:otherwise>
        <html:hidden property="document.documentHeader.financialDocumentDescription" />
      </c:otherwise>
    </c:choose>

    <html:hidden property="document.routingFormAgency.versionNumber" />
    <html:hidden property="document.routingFormBudget.versionNumber" />
    <html:hidden property="document.contractGrantProposal.versionNumber" />
    
    <kul:hiddenDocumentFields isFinancialDocument="false" isTransactionalDocument="false"/>
    