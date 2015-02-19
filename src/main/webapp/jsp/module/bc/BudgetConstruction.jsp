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
<c:set var="budgetConstructionAttributes"
	value="${DataDictionary['BudgetConstructionDocument'].attributes}" />

<c:if test="${KualiForm.pickListClose}">
<kul:page showDocumentInfo="false"
	htmlFormAction="budgetBudgetConstruction" renderMultipart="false"
	showTabButtons="false"
	docTitle="Budget Construction Document"
    transactionalDocument="true"
	>
    <div id="globalbuttons" class="globalbuttons">
	    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" 
	        onclick="window.close();return true;" property="methodToCall.performLost" title="close the window" alt="close the window"/>		
    </div>
</kul:page>
</c:if>

<c:if test="${!KualiForm.pickListClose}">
<kul:page showDocumentInfo="true"
	htmlFormAction="budgetBudgetConstruction" renderMultipart="true"
	showTabButtons="true"
	docTitle="Budget Construction Document"
    transactionalDocument="true"
	>
    <html:hidden property="mainWindow" />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

    <c:if test="${!KualiForm.securityNoAccess}">
    <bc:systemInformation />

    <bc:budgetConstructionRevenueLines />

    <bc:budgetConstructionExpenditureLines />
    
	<kul:notes />
    </c:if>

	<kul:routeLog />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="false"
		suppressRoutingControls="true" />

<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
<SCRIPT type="text/javascript">
  subObjectCodeNameSuffix = ".financialSubObject.financialSubObjectCdshortNm";
  var kualiForm = document.forms['KualiForm'];
  var kualiElements = kualiForm.elements;
</SCRIPT>
</kul:page>
</c:if>
