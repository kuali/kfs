<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%--
  HACK: CashManagementDocument isn't a transactionalDocument, but its XML file claims that it is,
  which is why this JSP abuses some of the standard transactionalDocument tags
--%>
<c:set var="allowAdditionalDeposits" value="${KualiForm.editingMode['allowAdditionalDeposits']}" />
<c:set var="showDeposits" value="${allowAdditionalDeposits || (!empty KualiForm.document.deposits)}" />

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashManagement" documentTypeName="CashManagementDocument" renderMultipart="true" showTabButtons="true">
    <sys:hiddenDocumentFields isFinancialDocument="false"/>
    
    <sys:documentOverview editingMode="${KualiForm.editingMode}"/>
    
    <c:if test="${!empty KualiForm.document.checks}">
      <logic:iterate indexId="ctr" name="KualiForm" property="document.checks" id="currentCheck">
        <fp:hiddenCheckLine propertyName="document.checks[${ctr}]" displayHidden="false" />
      </logic:iterate>
    </c:if>
    
    <fp:cashDrawerActivity/>
    
    <c:if test="${!showDeposits}">
        <kul:hiddenTab forceOpen="true" />
    </c:if>
    <c:if test="${showDeposits}">
        <fp:deposits editingMode="${KualiForm.editingMode}"/>
    </c:if>

    <fp:cashieringActivity />
    
    <c:if test="${!empty KualiForm.recentlyClosedItemsInProcess}">
      <fp:recentlyClosedMiscAdvances />
    </c:if>

    <c:if test="${KualiForm.document.bankCashOffsetEnabled}" >
        <gl:generalLedgerPendingEntries />
    </c:if>
    
    <kul:notes/>
    <kul:adHocRecipients />
    <kul:routeLog/>
    <kul:superUserActions />
    <kul:panelFooter/>
    
    <sys:documentControls transactionalDocument="false"/>
</kul:documentPage>
