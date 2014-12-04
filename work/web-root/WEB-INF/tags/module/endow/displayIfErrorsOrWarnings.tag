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
<%@ tag body-content="scriptless" %> 

<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="keyMatch" required="true" %>

<%-- determine if there are any error messages matching the given key --%>
<c:set var="hasErrors" value="false" />
<c:forEach items="${fn:split(keyMatch,',')}" var="prefix">
    <c:forEach items="${ErrorPropertyList}" var="key">
        <c:if test="${(fn:endsWith(prefix,'*') && fn:startsWith(key,fn:replace(prefix,'*',''))) || (key eq prefix)}">
            <c:set var="hasErrors" value="true"/>
        </c:if>
    </c:forEach>
</c:forEach>

<%-- determine if there are any warning messages matching the given key --%>
<c:set var="hasWarnings" value="false" />
<c:forEach items="${fn:split(keyMatch,',')}" var="prefix">
    <c:forEach items="${WarningPropertyList}" var="key">
        <c:if test="${(fn:endsWith(prefix,'*') && fn:startsWith(key,fn:replace(prefix,'*',''))) || (key eq prefix)}">
            <c:set var="hasWarnings" value="true"/>
        </c:if>
    </c:forEach>
</c:forEach>

<%-- if there are matching errors or warnings, process the contained JSP --%>
<c:if test="${hasErrors or hasWarnings}">
    <jsp:doBody/>
</c:if>
