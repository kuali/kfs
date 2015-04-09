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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%><%--

 --%><%@ attribute name="url" required="true" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="displayTitle" required="false" %>
<%@ attribute name="prefix" required="false" %>

<c:set var="backdoorPortalUrlAddition" value="" />
<c:set var="backdoorMainUrlAddition" value="" />
<c:if test="${UserSession.backdoorInUse}">
	<%-- Can't add this.  If on the main (portal) request, it assumes this was a
	 backdoor login request and appends an additional parameter which causes some forms to blow 
	<c:set var="backdoorPortalUrlAddition" value="&backdoorId=${UserSession.principalName}" />
	 --%>
	<c:choose>
		<c:when test="${fn:contains(url,'?')}">
			<c:set var="backdoorMainUrlAddition" value="&backdoorId=${UserSession.principalName}" />
		</c:when>
		<c:otherwise>
			<c:set var="backdoorMainUrlAddition" value="?backdoorId=${UserSession.principalName}" />
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${displayTitle}" >
  <a class="portal_link" href="${prefix}portal.do?channelTitle=${title}&channelUrl=${url}${backdoorMainUrlAddition}"  title="${title}">${title}</a>
</c:if>
<c:if test="${! displayTitle}" >
  <a class="portal_link" href="${prefix}portal.do?channelTitle=${title}&channelUrl=${url}${backdoorMainUrlAddition}" title="${title}"><jsp:doBody/></a>
</c:if>
