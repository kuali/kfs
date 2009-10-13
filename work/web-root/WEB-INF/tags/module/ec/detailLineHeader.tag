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

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ tag description="render the header of the detail line table" %>
              
<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for the line fields."%>              
<%@ attribute name="detailFieldNames" required="true"
    description="The names of the fields that will be displayed" %>
<%@ attribute name="hasActions" required="true"
    description="Determine if a user can tak an action on the given line" %>
<%@ attribute name="index" required="false"
    description="The line index" %> 
<%@ attribute name="sortableFieldNames" required="false"
    description="the names of the fields that can be sorted" %>             

<kul:htmlAttributeHeaderCell literalLabel="${index}"/>
		
<!-- render the header of the detail line table -->
<c:forTokens var="fieldName" items="${detailFieldNames}" delims=",">
	<kul:htmlAttributeHeaderCell attributeEntry="${attributes[fieldName]}">		
		<c:if test="${fn:contains(sortableFieldNames,fieldName)}">
			&nbsp;<html:image property="methodToCall.sortDetailLineByColumn.${fieldName}" 
				src="${ConfigProperties.kr.externalizable.images.url}sort.gif" 
				title="Sort by ${attributes[fieldName].label}" 
				alt="Sort by ${attributes[fieldName].label}" 
				styleClass="tinybutton" />
		</c:if>
	</kul:htmlAttributeHeaderCell>		            
</c:forTokens>

<c:if test="${hasActions}">		
	<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
</c:if>
