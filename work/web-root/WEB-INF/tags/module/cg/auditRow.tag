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

<%@ attribute name="tabTitle" required="true" %>
<%@ attribute name="defaultOpen" required="true" %>
<%@ attribute name="totalErrors" required="true" %>

<c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<c:set var="incrementerDummy" value="${kfunc:incrementTabIndex(KualiForm, currentTabIndex)}" />
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, currentTabIndex)}"/>

<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="${defaultOpen}" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${(currentTab == 'OPEN')}" />
    </c:when>
</c:choose>

<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

<!-- ROW -->

<tbody>
    <tr>
	    <td class="tab-subhead">
	      	<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
	        </c:if>
	        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
	        </c:if>
	    </td>
	    <td colspan="3" class="tab-subhead" width="99%"><b>${tabTitle} (${totalErrors})</b></td>
    </tr>
</tbody>

<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	<tbody style="display: ;" id="tab-${tabKey}-div">
</c:if>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	<tbody style="display: none;" id="tab-${tabKey}-div">
</c:if>

<!-- Before the jsp:doBody of the kul:tab tag -->
<jsp:doBody/>
<!-- After the jsp:doBody of the kul:tab tag -->

</tbody>
