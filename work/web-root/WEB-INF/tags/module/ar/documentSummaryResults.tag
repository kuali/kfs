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

<%@ attribute name="lookupResultsProperty" required="true" type="java.lang.String" description="The property name of the results to iterate through" %>
<%@ attribute name="lookupResultTitleProperties" required="true" type="java.lang.String" description="Properties for the values from a single row which will become part of a title" %>
<%@ attribute name="tabTitleName" required="false" type="java.lang.String" description="A fuller description for the row" %>

<logic:iterate id="result" name="KualiForm" property="${lookupResultsProperty}" indexId="ctr">
	<c:set var="useTabTop" value="${ctr == 0}" />
	
	<%-- generate tab titles --%>
	<c:set var="tabTitleValues" value=""/>
	<c:forTokens items="${lookupResultTitleProperties}" delims=";" var="titleProperty">
		<c:set var="tabTitleValues" value="${tabTitleValues},${result[titleProperty]}"/>
	</c:forTokens>
	<c:set var="tabTitleValues" value="${fn:substring(tabTitleValues,1,fn:length(tabTitleValues))}"/>
	<c:if test="${!empty tabTitleName}">
		<c:set var="tabTitleName" value="${tabTitleName} "/>
	</c:if>
	<c:set var="tabTitle" value="${tabTitleName}${tabTitleValues}" />
	
	<c:set var="propertyName" scope="request" value="${lookupResultsProperty}[${ctr}]"/>
	<c:set var="tabErrorKey" value="${requestScope.propertyName}.*" />

	<div id="workarea">
		<c:choose>
			<c:when test="${useTabTop}">
				<kul:tabTop tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="${tabErrorKey}">
					<jsp:doBody/>
				</kul:tabTop>
			</c:when>
			<c:otherwise>
				<kul:tab tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="${tabErrorKey}">
					<jsp:doBody/>
				</kul:tab>
			</c:otherwise>
		</c:choose>
	</div>
</logic:iterate>
