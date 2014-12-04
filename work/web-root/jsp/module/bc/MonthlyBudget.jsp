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
<c:set var="budgetConstructionMonthlyAttributes"
	value="${DataDictionary['BudgetConstructionMonthly'].attributes}" />

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetMonthlyBudget" renderMultipart="true"
	showTabButtons="true"
	docTitle="BC Monthly"
    transactionalDocument="false"
	>

    <html:hidden property="mainWindow" />

    <c:set var="readOnly" value="${KualiForm.monthlyReadOnly}" />

    <bc:monthlyBudget readOnly="${readOnly}" />
	<kul:panelFooter />

    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${!readOnly}">
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif" styleClass="globalbuttons" property="methodToCall.save" title="save" alt="save"/>
	    </c:if>
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.close" title="close" alt="close"/>
    </div>

</kul:page>
