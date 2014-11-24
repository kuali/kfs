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

<%@ tag description="render the totals of the given field in the given detail lines" %>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="index" required="false"
    description="The order of the detail line that contains the field being rendered" %>
<%@ attribute name="readOnlySection" required="false"
    description="Determine if the field woulb be rendered as read-only or not" %>	    
<%@ attribute name="totalFieldNames" required="true"
	description="The names of the total fields that will be displayed . The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="hasActions" required="false"
	description="Determine if a user can take an action on the detail line. If true, the  given actions can be rendered with the detail line."%>

<c:set var="readonlySuffix" value="${readOnlySection ? '.readonly' : ''}" /> 

<c:forTokens var="fieldName" items="${totalFieldNames}" delims=","	varStatus="status">
	<c:set var="percent" value="${fn:contains(fieldName, 'Percent') ? '%' : '' }" />
	<td class="infoline">
		<div id="document.${fieldName}${readonlySuffix}" class="right">
			<c:choose>
				<c:when test="${empty percent}">
					<fmt:formatNumber value="${KualiForm.document[fieldName]}" currencySymbol="" type="currency"/>
				</c:when>
				<c:otherwise>
					<fmt:formatNumber value="${KualiForm.document[fieldName]}" type="number"/>${percent}
				</c:otherwise>
			</c:choose>
		</div>
	</td>
</c:forTokens>

<c:if test="${hasActions}">
	<td class="infoline">
		<div align="center"><jsp:doBody /></div>
	</td>
</c:if>
