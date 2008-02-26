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

<c:set var="numericFormatter" value="org.kuali.core.web.format.CurrencyFormatter, org.kuali.core.web.format.IntegerFormatter"/>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
		
		<!-- render the header of the detail line table -->
		<c:forTokens var="fieldName" items="${detailFieldNames}" delims=",">
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes[fieldName]}"/>
		</c:forTokens>
	</tr> 

	<!-- populate the table with the given deatil lines -->
	<c:forEach var="detailLine" items="${detailLines}" varStatus="status">
		<tr>
			<kul:htmlAttributeHeaderCell literalLabel="${status.index + 1}">
			<c:forTokens var="fieldName" items="${hiddenFieldNames}" delims=",">
					<html:hidden property="document.effortCertificationDetailLines[${status.index}].${fieldName}" />
			</c:forTokens>
			</kul:htmlAttributeHeaderCell>
			
			<c:forTokens var="fieldName" items="${detailFieldNames}" delims=",">
				<c:set var="percent" value="${fn:contains(fieldName, 'Percent') ? '%' : '' }" />
				
				<td class="datacell-nowrap">
					<er:detailLineDataCell
						index="${status.index}" 
						fieldValue="${detailLine[fieldName]}${percent}"
						fieldFormName="document.effortCertificationDetailLines[${status.index}].${fieldName}"
				        attributeEntry="${attributes[fieldName]}"
				        inquirableUrl="${inquirableUrl[status.index][fieldName]}"
				        fieldInfo="${fieldInfo[status.index][fieldName]}"
				        readOnly="true" />
			    </td>
			</c:forTokens>
		</tr>
	</c:forEach>	
</table>       