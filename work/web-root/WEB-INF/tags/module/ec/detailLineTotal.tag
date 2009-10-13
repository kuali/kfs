<%--
 Copyright 2005-2009 The Kuali Foundation
 
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
