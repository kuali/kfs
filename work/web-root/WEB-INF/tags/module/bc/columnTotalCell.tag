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

<%@ attribute name="cellProperty" required="true"
              description="the fully qualified name of the property being displayed by this cell.
              This could be in the document instead of the accounting line." %>
<%@ attribute name="textStyle" required="true" %>
<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell." %>

<%@ attribute name="rowSpan" required="false"
              description="row span for the data cell" %>
<%@ attribute name="colSpan" required="false"
              description="col span for the data cell" %>
<%@ attribute name="fieldAlign" required="false"
              description="div alignment. default is align=left" %>
<%@ attribute name="formattedNumberValue" required="false"
              description="number to format instead of property" %>
<%@ attribute name="disableHiddenField" required="false"
              description="determine whether the hidden field is needed for the given cell property" %>              


<c:if test="${empty fieldAlign}">
    <c:set var="fieldAlign" value="left"/>
</c:if>
<c:set var="rowSpan" value="${empty rowSpan ? 1 : rowSpan}"/>
<c:set var="colSpan" value="${empty colSpan ? 1 : colSpan}"/>

<td class="${dataCellCssClass}" valign="top" rowspan="${rowSpan}" colspan="${colSpan}">
<div style="text-align: ${fieldAlign};">
<strong>
<span class="nowrap">
    <c:choose>
        <c:when test="${empty formattedNumberValue}">
            <bean:write name="KualiForm" property="${cellProperty}"/>&nbsp;
        </c:when>
        <c:otherwise>
            ${formattedNumberValue}
        </c:otherwise>
    </c:choose>
</span>
</strong>
</div>
</td>
