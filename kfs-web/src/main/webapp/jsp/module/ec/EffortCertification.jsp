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

<c:choose>
	<c:when test="${KualiForm.document.documentHeader.workflowDocument.initiated}">
		<c:set var="url" value="/effortCertificationRecreate.do" />
	</c:when>
	<c:otherwise>
		<c:set var="url" value="/effortCertificationReport.do" />
	</c:otherwise>
</c:choose>
  
<c:redirect url="${url}">
	<c:forEach var="parameter" items="${paramValues}"> 
		<c:forEach var="value" items="${parameter.value}">
			<c:param name="${parameter.key}" value="${value}"/>
		</c:forEach>
	</c:forEach>
</c:redirect>
