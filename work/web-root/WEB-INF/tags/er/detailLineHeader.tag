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

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ tag description="render the given field in the given detail line" %>
              
<%@ attribute name="attributes" required="true" type="java.util.Map"
			  description="The DataDictionary entry containing attributes for the line fields."%>              
<%@ attribute name="detailFieldNames" required="true"
              description="the names of the fields that will be displayed" %>
<%@ attribute name="hasActions" required="true"
              description="determine if a user can tak an action on the given line" %>
<%@ attribute name="index" required="false"
              description="the line index" %> 
<%@ attribute name="sortableFieldNames" required="false"
              description="the names of the fields that can be editable" %>             
<%@ attribute name="isFederalFunding" required="false"
              description="the names of the fields that can be editable" %>

<kul:htmlAttributeHeaderCell literalLabel="${index}"/>
		
<!-- render the header of the detail line table -->
<c:set var="sortMethod" value="${isFederalFunding ? 'sortFed' : 'sortOther'}"/>

<c:forTokens var="fieldName" items="${detailFieldNames}" delims=",">
	<kul:htmlAttributeHeaderCell attributeEntry="${attributes[fieldName]}">		
		<c:if test="${fn:contains(sortableFieldNames,fieldName)}">
			<html:image property="methodToCall.${sortMethod}.column${fieldName}" 
				src="kr/static/images/sort.gif" title="Sort" alt="Sort" styleClass="tinybutton" />
		</c:if>
	</kul:htmlAttributeHeaderCell>		            
</c:forTokens>

<c:if test="${hasActions}">		
	<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
</c:if>