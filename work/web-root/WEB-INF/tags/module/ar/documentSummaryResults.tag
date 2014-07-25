<%--
 Copyright 2009-2014 The Kuali Foundation
 
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