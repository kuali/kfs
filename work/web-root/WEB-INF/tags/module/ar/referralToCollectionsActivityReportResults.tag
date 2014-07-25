<%--
 Copyright 2007-2014 The Kuali Foundation
 
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

<c:if test="${!empty reqSearchResultsSize}">
    <c:set var="offset" value="0"/>
    <display:table class="datatable-100" cellspacing="0" cellpadding="0" name="${reqSearchResults}"
		id="row" export="true" pagesize="100" offset="${offset}"
		requestURI="collectionActivityReportLookup.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">
       <c:forEach items="${row.columns}" var="column">
		<c:choose>
			<c:when test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">
				<display:column class="numbercell" sortable="true" decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator" title="${column.columnTitle}" 
				comparator="${column.comparator}"><c:choose>
					<c:when test="${column.propertyURL != \"\"}"><a href="<c:out value="${column.propertyURL}"/>" title="${column.propertyValue}" target="blank"><c:out value="${column.propertyValue}" /></a></c:when><c:otherwise><c:out value="${column.propertyValue}" /></c:otherwise></c:choose>
				</display:column>
			</c:when>
			<c:otherwise><display:column class="infocell"
		        sortable="${column.sortable}"
		        decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
		        title="${column.columnTitle}"
		        comparator="${column.comparator}">
				<c:choose>
					<c:when test="${column.propertyURL != \"\"}">
						<a href="<c:out value="${column.propertyURL}"/>" title="${column.propertyValue}" target="blank"><c:out value="${column.propertyValue}" /></a>
					</c:when><c:otherwise>
						<c:out value="${column.propertyValue}" />
					</c:otherwise>
				</c:choose>
				</display:column>
			</c:otherwise>
		</c:choose>	
	</c:forEach>
    </display:table>
</c:if>