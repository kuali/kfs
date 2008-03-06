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

<%@ tag description="display all detail lines of current document in a table" %>

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="detailLines" required="true" type="java.util.List"
              description="the detail lines being displayed" %>             
<%@ attribute name="attributes" required="true" type="java.util.Map"
			  description="The DataDictionary entry containing attributes for the line fields."%>              
<%@ attribute name="detailFieldNames" required="true"
              description="the names of the fields that will be displayed" %>
<%@ attribute name="hiddenFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
<%@ attribute name="editableFieldNames" required="false"
              description="the names of the fields that can be editable" %>  
<%@ attribute name="inquirableUrl" required="false" type="java.util.List"
              description="the names of the fields that can be inquirable" %>  
<%@ attribute name="fieldInfo" required="false" type="java.util.List"
              description="the information of the fields in the detail lines" %>
<%@ attribute name="relationshipMetadata" required="false"
	type="java.util.Map"
	description="This is a Map that holds a property name list of the primary key of the referenced class for each eligible field. The value of the attribute is used to build quick finder for the eligible fields."%>				                 
              
<%@ attribute name="onblurForEditableFieldNames" required="false"
              description="the funation names that retrives the information of the given editable fields" %> 
<%@ attribute name="onblurableInfoFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
<%@ attribute name="hasActions" required="false"
              description="determine if a user can tak an action on the given line" %>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<er:detailLineHeader attributes="${attributes}"
			detailFieldNames="${detailFieldNames}"
			hasActions="${hasActions}"/>
	</tr> 

	<!-- populate the table with the given deatil lines -->
	<c:forEach var="detailLine" items="${detailLines}" varStatus="status">
		<tr>
			<kul:htmlAttributeHeaderCell literalLabel="${status.index + 1}">
				<c:forTokens var="fieldName" items="${hiddenFieldNames}" delims=",">
					<html:hidden property="document.effortCertificationDetailLines[${status.index}].${fieldName}" />
				</c:forTokens>
			</kul:htmlAttributeHeaderCell>
			
			<er:detailLine detailLine="${detailLine}" 
				detailLineFormName="document.effortCertificationDetailLines[${status.index}]"
				attributes="${attributes}"
				detailFieldNames="${detailFieldNames}"
				hiddenFieldNames="${hiddenFieldNames}"
				editableFieldNames="${editableFieldNames}"
				onblurForEditableFieldNames="${onblurForEditableFieldNames}"
				onblurableInfoFieldNames="${onblurableInfoFieldNames}"
				inquirableUrl="${inquirableUrl[status.index]}"
				fieldInfo="${fieldInfo[status.index]}"
				relationshipMetadata ="${relationshipMetadata}" 
				hasActions="${hasActions}"/>			
		</tr>
	</c:forEach>	
</table>       