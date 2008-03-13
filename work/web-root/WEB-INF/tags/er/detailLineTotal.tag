<%--
 Copyright 2005-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>

<%@ tag description="render the given field in the given detail line" %>

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="index" required="false"
    description="the order of the detail line that contains the field being rendered" %>
<%@ attribute name="section" required="false"
    description="the order of the detail line that contains the field being rendered" %>    
<%@ attribute name="totalFieldNames" required="true"
	description="The names of the fields that will be displayed . The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="hasActions" required="false"
	description="To determine if a user can tak an action on the given detail line. If true, the  given actions can be rendered with the detail line."%>

<c:forTokens var="fieldName" items="${totalFieldNames}" delims=","	varStatus="status">
	<c:set var="percent" value="${fn:contains(fieldName, 'Percent') ? '%' : '' }" />
	<td class="infoline">
		<div id="${section}.document.${fieldName}" class="right">
			${KualiForm.document[fieldName]}${percent}
		</div>
	</td>
</c:forTokens>

<c:if test="${hasActions}">
	<td class="infoline">
		<div align="center"><jsp:doBody /></div>
	</td>
</c:if>