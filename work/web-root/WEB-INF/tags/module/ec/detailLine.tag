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

<%@ tag description="render the given field in the given detail line"%>

<%@ attribute name="detailLine" required="true" type="java.lang.Object"
	description="The detail line object containing the data being displayed"%>
<%@ attribute name="detailLineFormName" required="true"
	description="The name  of the detail line"%>
<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for all detail line fields."%>
<%@ attribute name="detailFieldNames" required="true"
	description="The names of the fields that will be displayed . The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="detailFieldNamesWithHiddenFormWhenReadonly" required="false"
	description="The names of the fields that will have hidden forms when the fields are readonly. The attribute can hold multiple filed names, which are separated by commas."%>	
<%@ attribute name="hiddenFieldNames" required="false"
	description="The names of the fields that will be rendered as hidden inputs. The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="editableFieldNames" required="false"
	description="The names of the fields that can be editable. The attribute can hold multiple filed names, which are separated by commas. If the value of the attribute is empty, all fields specified in the detailFieldNames attribute will be readonly."%>
<%@ attribute name="onchangeForEditableFieldNames" required="false"
	description="The JavaScript function names associated with the editable fields. The functions will be executed to retrieve the information of the given editable fields when  onChange events happen. The order of the functions must match the names in the attribute editableFieldNames."%>
<%@ attribute name="onchangeableInfoFieldNames" required="false"
	description="The names of the fields will display the descriptive information retrieved by onChange events."%>

<%@ attribute name="fieldInfo" required="false" type="java.util.Map"
	description="The descriptive information of the readonly fields. The information is stored in a Map and prepared in Action Form class."%>
<%@ attribute name="inquirableUrl" required="false" type="java.util.Map"
	description="The URL Map for the inquirable fields. The inquirable fields can be defined in Action Form class. If a readonly field is inquirable, a corresponding URL will be rendered with the field."%>
<%@ attribute name="relationshipMetadata" required="false" type="java.util.Map"
	description="This is a Map that holds a property name list of the primary key of the referenced class for each eligible field. The value of the attribute is used to build quick finder for the eligible fields."%>	

<%@ attribute name="hasActions" required="false"
	description="To determine if a user can take an action on the given detail line. If true, the given actions can be rendered with the detail line."%>
<%@ attribute name="actions" required="false"
	description="The actions that can be taken on the given detail line."%>
<%@ attribute name="actionImageFileNames" required="false"
	description="The graphic representations of the given actions."%>	
<%@ attribute name="index" required="false"
	description="The index of the detail line object containing the data being displayed"%>
<%@ attribute name="readOnlySection" required="false"
    description="determine if the container of current detail line is read-only or not" %>			

<c:set var="commaDeliminator" value=","/>
	
<c:forTokens var="fieldName" items="${hiddenFieldNames}" delims=",">
	<html:hidden property="${detailLineFormName}.${fieldName}" />
</c:forTokens>

<c:set var="onchangeForEditableFieldNamesArray" value="${fn:split(onchangeForEditableFieldNames, commaDeliminator)}" />
<c:set var="onchangeableInfoFieldNamesArray" value="${fn:split(onchangeableInfoFieldNames, commaDeliminator)}" />
					
<!-- populate the table with the given deatil lines -->
<c:forTokens var="fieldName" items="${detailFieldNames}" delims=","	varStatus="status">
	<c:set var="editable" value="${not empty fieldName && fn:contains(editableFieldNames, fieldName)}" />
	<c:set var="withHiddenFormWhenReadonly"	value="${!editable && fn:contains(detailFieldNamesWithHiddenFormWhenReadonly, fieldName)}"/>
	
	<c:set var="onchangeableInfoFieldName" value="" />
	<c:set var="onchange"	value="" />
	
	<c:set var="onchangeIndex" value="-1" />
	<c:if test="${editable}">
		<c:forTokens var="editableFieldName" items="${editableFieldNames}" delims="," varStatus="editableStatus">
			<c:if test="${editableFieldName == fieldName}">
				<c:set var="onchangeIndex" value="${editableStatus.index}" />
			</c:if>
		</c:forTokens>
	</c:if>
	
	<c:if test="${editable && onchangeIndex >=0}">
		<c:set var="tempInfoFieldName" value="${onchangeableInfoFieldNamesArray[onchangeIndex]}" />
		<c:set var="onchangeableInfoFieldName" value="${detailLineFormName}.${tempInfoFieldName}" />
		<c:set var="onchangeableInfoFieldName" value="${fn:length(tempInfoFieldName) > 0 ? onchangeableInfoFieldName : ''}" />
		<c:set var="onchange" value="${onchangeForEditableFieldNamesArray[onchangeIndex]}(this.name, '${onchangeableInfoFieldName}');" />
	</c:if>
	
	<td class="datacell-nowrap">	
		<ec:detailLineDataCell fieldValue="${detailLine[fieldName]}"
			fieldFormName="${detailLineFormName}.${fieldName}"
			withHiddenForm="${withHiddenFormWhenReadonly}"
			fieldFormNamePrefix="${detailLineFormName}"
			infoFieldFormName="${onchangeableInfoFieldName}"
			attributeEntry="${attributes[fieldName]}"
			inquirableUrl="${inquirableUrl[fieldName]}"
			fieldInfo="${fieldInfo[fieldName]}"
			relationshipMetadata = "${relationshipMetadata[fieldName]}"
			onchange="${onchange}" readOnly="${not editable}" readOnlySection="${readOnlySection}"/>			
		
		<c:if test="${fn:contains(fieldName, 'accountNumber') && detailLine.accountExpiredOverrideNeeded && detailLine.newLineIndicator}">
			<ec:expiredAccountOverride detailLineFormName="${detailLineFormName}" attributes="${attributes}" readOnly="${not editable}" />
		</c:if>
	</td>
</c:forTokens>

<c:if test="${hasActions}">
	<td class="datacell-nowrap">
		<div align="center">
			<c:forTokens var="action" items="${actions}" delims="," varStatus="actionStatus">
				<c:set var="actionImageFileName" value=""/>
				
				<c:forTokens var="imageFileName" items="${actionImageFileNames}" delims="," varStatus="imageStatus">
					<c:if test="${actionStatus.index == imageStatus.index}">
						<c:set var="actionImageFileName" value="${imageFileName}"/>
					</c:if>
				</c:forTokens>
				
				<ec:detailLineActionDataCell action="${action}.line${index}" imageFileName="${actionImageFileName}"/>
			</c:forTokens>		
		</div>
	</td>
</c:if>
