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

<%@ tag description="render the given field in the given detail line"%>

<%@ attribute name="detailLine" required="true" type="java.lang.Object"
	description="The detail line object containing the data being displayed"%>
<%@ attribute name="detailLineFormName" required="true"
	description="The name  of the detail line"%>
<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for all detail line fields."%>
<%@ attribute name="detailFieldNames" required="true"
	description="The names of the fields that will be displayed . The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="hiddenFieldNames" required="false"
	description="The names of the fields that will be rendered as hidden inputs. The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="editableFieldNames" required="false"
	description="The names of the fields that can be editable. The attribute can hold multiple filed names, which are separated by commas. If the value of the attribute is empty, all fields specified in the detailFieldNames attribute will be readonly."%>
<%@ attribute name="onblurForEditableFieldNames" required="false"
	description="The JavaScript function names associated with the editable fields. The functions will be executed to retrieve the information of the given editable fields when  onBlur events happen. The order of the functions must match the names in the attribute editableFieldNames."%>
<%@ attribute name="onblurableInfoFieldNames" required="false"
	description="The names of the fields will display the descriptive information retrieved by onBlur events."%>

<%@ attribute name="fieldInfo" required="false" type="java.util.Map"
	description="The descriptive information of the readonly fields. The information is stored in a Map and prepared in Action Form class."%>
<%@ attribute name="inquirableUrl" required="false" type="java.util.Map"
	description="The URL Map for the inquirable fields. The inquirable fields can be defined in Action Form class. If a readonly field is inquirable, a corresponding URL will be rendered with the field."%>
<%@ attribute name="relationshipMetadata" required="false"
	type="java.util.Map"
	description="This is a Map that holds a property name list of the primary key of the referenced class for each eligible field. The value of the attribute is used to build quick finder for the eligible fields."%>	
<%@ attribute name="hasActions" required="false"
	description="To determine if a user can tak an action on the given detail line . If true, the  given actions can be rendered with the detail line."%>

<!-- populate the table with the given deatil lines -->
<c:forTokens var="fieldName" items="${detailFieldNames}" delims=","
	varStatus="status">
	<c:set var="editable"
		value="${not empty fieldName && fn:contains(editableFieldNames, fieldName)}" />

	<c:forTokens var="onblurOfInfoField"
		items="${onblurableInfoFieldNames}" delims=","
		varStatus="onblurInfoStatus">
		<c:if test="${status.index == onblurInfoStatus.index}">
			<c:set var="onblurableInfoFieldName"
				value="${detailLineFormName}.${onblurOfInfoField}" />
		</c:if>
	</c:forTokens>

	<c:forTokens var="onblurOfField" items="${onblurForEditableFieldNames}"
		delims="," varStatus="onblurStatus">
		<c:if test="${status.index == onblurStatus.index}">
			<c:set var="onblur"
				value="${onblurOfField}(this.name, '${onblurableInfoFieldName}');" />
		</c:if>
	</c:forTokens>

	<td class="datacell-nowrap">
	<c:set var="percent"
		value="${fn:contains(fieldName, 'Percent') ? '%' : '' }" />

	<er:detailLineDataCell fieldValue="${detailLine[fieldName]}${percent}"
		fieldFormName="${detailLineFormName}.${fieldName}"
		fieldFormNamePrefix="${detailLineFormName}"
		infoFieldFormName="${onblurableInfoFieldName}"
		attributeEntry="${attributes[fieldName]}"
		inquirableUrl="${inquirableUrl[fieldName]}"
		fieldInfo="${fieldInfo[fieldName]}"
		relationshipMetadata = "${relationshipMetadata[fieldName]}"
		onblur="${onblur}" readOnly="${not editable}" />
	</td>
</c:forTokens>

<c:if test="${hasActions}">
	<td class="datacell-nowrap">
	<div align="center"><jsp:doBody /></div>
	</td>
</c:if>