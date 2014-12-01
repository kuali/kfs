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

<%@ attribute name="accountingLine" required="true"
              description="The name in the form of the accounting line." %>
<%@ attribute name="hiddenField" required="true"
              description="the name of an accounting line field
              to be put in a hidden form field by this tag." %>
<%@ attribute name="displayHidden" required="false"
              description="display hidden values (for debugging).
              This information is also available from the Firefox Web Developer extension,
              but that includes more detail and requires even more horizontal space." %>
<%@ attribute name="isBaseline" required="false"
              description="if displayed, distinguish baseline values
              from normal values by background color." %>
<%@ attribute name="value" required="false"
              description="sets the hidden field to this value" %>

<c:if test="${displayHidden}">
    <span style="background: ${isBaseline ? 'blue' : 'green'}">
        <c:out value="${hiddenField}"/> =</c:if
><c:choose
    ><c:when test="${empty value}"
        ><html:hidden write="${displayHidden}" property="${accountingLine}.${hiddenField}"
    /></c:when
    ><c:otherwise
        ><html:hidden write="${displayHidden}" property="${accountingLine}.${hiddenField}" value="${value}"
    /></c:otherwise
></c:choose><c:if test="${displayHidden}">;<br/>
    </span>
</c:if>
