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

<%@ attribute name="detailLine" required="true" type="java.lang.Object"
              description="the detail lines being displayed" %>
<%@ attribute name="detailLineFormName" required="true"
              description="the detail lines being displayed" %>               
<%@ attribute name="attributes" required="true" type="java.util.Map"
			  description="The DataDictionary entry containing attributes for the line fields."%>              
<%@ attribute name="detailFieldNames" required="true"
              description="the names of the fields that will be displayed" %>
<%@ attribute name="hiddenFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
<%@ attribute name="editableFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
<%@ attribute name="onblurForEditableFieldNames" required="false"
              description="the funation names that retrives the information of the given editable fields" %> 
<%@ attribute name="onblurableInfoFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
               
<%@ attribute name="fieldInfo" required="false" type="java.util.Map"
              description="the descriptive information of the field that will be displayed" %>                         
<%@ attribute name="inquirableUrl" required="false" type="java.util.Map"
              description="determine if the given field is inquirable" %> 
<%@ attribute name="primaryKeysOfDetailLineFields" required="false" type="java.util.Map"
			  description="The DataDictionary entry containing attributes for the line fields."%>  
<%@ attribute name="hasActions" required="false"
              description="determine if a user can tak an action on the given line" %>			               
		
<!-- populate the table with the given deatil lines -->
<c:forTokens var="fieldName" items="${detailFieldNames}" delims="," varStatus="status">
	<c:set var="editable" value="${not empty fieldName && fn:contains(editableFieldNames, fieldName)}" />
	
	<c:forTokens var="onblurOfInfoField" items="${onblurableInfoFieldNames}" delims="," varStatus="onblurInfoStatus">
		<c:if test="${status.index == onblurInfoStatus.index}">
			<c:set var="onblurableInfoFieldName" value="${detailLineFormName}.${onblurOfInfoField}"/>
		</c:if>
	</c:forTokens>
	
	<c:forTokens var="onblurOfField" items="${onblurForEditableFieldNames}" delims="," varStatus="onblurStatus">
		<c:if test="${status.index == onblurStatus.index}">
			<c:set var="onblur" value="${onblurOfField}(this.name, '${onblurableInfoFieldName}');"/>
		</c:if>
	</c:forTokens>
	
	<td class="datacell-nowrap">
		<c:set var="percent" value="${fn:contains(fieldName, 'Percent') ? '%' : '' }" />
		
        <er:detailLineDataCell
			fieldValue="${detailLine[fieldName]}${percent}"
			fieldFormName="${detailLineFormName}.${fieldName}"
			fieldFormNamePrefix="${detailLineFormName}"
			infoFieldFormName="${onblurableInfoFieldName}"
	        attributeEntry="${attributes[fieldName]}"
	        inquirableUrl="${inquirableUrl[fieldName]}"
		    fieldInfo="${fieldInfo[fieldName]}"
		    primaryKeys="${primaryKeysOfDetailLineFields[fieldName]}"
	        onblur="${onblur}" 
	        readOnly="${not editable}" />   				                     		
    </td>
</c:forTokens>

<c:if test="${hasActions}">
	<td class="datacell-nowrap">
		<div align="center"><jsp:doBody/></div>
	</td>
</c:if>