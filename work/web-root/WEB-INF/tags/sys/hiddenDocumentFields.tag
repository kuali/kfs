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
<%@ attribute name="isTransactionalDocument" required="false" %>
<%@ attribute name="isFinancialDocument" required="false" %>
<%@ attribute name="excludePostingYear" required="false" %>
<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />


<%-- set default values --%>
<c:if test="${empty isFinancialDocument}">
    <c:set var="isFinancialDocument" value="true" />
</c:if>
<c:if test="${empty isTransactionalDocument}">
    <c:set var="isTransactionalDocument" value="true" />
</c:if>
	
<kul:hiddenDocumentFields includeDocumentHeaderFields="${isTransactionalDocument}" includeEditMode="${isTransactionalDocument}"/>


<c:if test="${isTransactionalDocument && isFinancialDocument}">
    <c:if test="${!excludePostingYear}">
        <html:hidden property="document.postingYear" /> 
    </c:if>
    <html:hidden property="document.postingPeriodCode" /> 	

 </c:if>
