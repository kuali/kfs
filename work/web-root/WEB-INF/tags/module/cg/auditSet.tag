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

<%@ attribute name="title" required="true" %>
<%@ attribute name="soft" required="true" %>
<%@ attribute name="auditType" required="true" %>

<tr><td colspan="3" class="subhead">${title}</td></tr>
<c:set var="found" value="${false}"/>
<c:forEach items="${AuditErrors}" var="cluster">
	<c:if test="${cluster.value.softAudits == soft && cluster.value.size != 0}">
		<c:if test="${!found}"><c:set var="found" value="${true}"/></c:if>
		<cg:auditRow tabTitle="${cluster.value.label}" defaultOpen="false" totalErrors="${cluster.value.size}">
			<cg:auditErrors cluster="${cluster.key}" isLink="true"/>
		</cg:auditRow>
	</c:if>
</c:forEach>
<c:if test="${!found}">
	<tr>
		<td colspan="3" height="70" align=left valign=middle class="datacell">
			<div align="center">No ${auditType} audit errors present.</div>
		</td>
	</tr>
</c:if>
