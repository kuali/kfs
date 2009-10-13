<%--
 Copyright 2006-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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
