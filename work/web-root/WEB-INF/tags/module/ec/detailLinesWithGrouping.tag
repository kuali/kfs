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

<%@ tag description="display all detail lines of current document in a table. The detail lines will be displayed in two groups: federal and nonfederal" %>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="id" required="false"
    description="The unique id of the table, which will be used to identify the table in the HTML DOM tree" %> 
        
<%@ attribute name="detailLines" required="true" type="java.util.List"
    description="The detail lines being displayed" %>             
<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for the line fields."%>              

<%@ attribute name="detailFieldNames" required="true"
    description="The names of the fields that will be displayed. The attribute can hold multiple filed names, which are separated by commas." %>
<%@ attribute name="detailFieldNamesWithHiddenFormWhenReadonly" required="false"
	description="The names of the fields that will have hidden forms when the fields are readonly. The attribute can hold multiple filed names, which are separated by commas."%>	             
<%@ attribute name="hiddenFieldNames" required="false"
    description="The names of the fields that can be rendered as hidden fields. The attribute can hold multiple filed names, which are separated by commas." %> 
<%@ attribute name="detailLineFormName" required="true"
	description="The name  of the detail line"%>   
	 
<%@ attribute name="inquirableUrl" required="false" type="java.util.List"
    description="The list of URLs for the inquirable fields" %>  
<%@ attribute name="fieldInfo" required="false" type="java.util.List"
    description="The descriptive information of the fields in the detail lines" %>
<%@ attribute name="relationshipMetadata" required="false" type="java.util.Map"
	description="This is a Map that holds a property name list of the primary key of the referenced class for each eligible field. The value of the attribute is used to build quick finder for the eligible fields."%>				                 

<%@ attribute name="editableFieldNames" required="false"
    description="The names of the fields that can be editable. The attribute can hold multiple filed names, which are separated by commas." %>
<%@ attribute name="extraEditableFieldNames" required="false"
    description="The names of the fields that can be editable. The attribute can hold multiple filed names, which are separated by commas." %> 
                   
<%@ attribute name="onchangeForEditableFieldNames" required="false"
    description="The function names that retrives the information of the given editable fields. The attribute can hold multiple filed names, which are separated by commas." %> 
<%@ attribute name="onchangeForExtraEditableFieldNames" required="false"
    description="The function names that retrives the information of the given extra editable fields. The attribute can hold multiple filed names, which are separated by commas." %>  
                
<%@ attribute name="onchangeableInfoFieldNames" required="false"
    description="The names of the fields that can be used to hold the descriptive information of the editable fields. The attribute can hold multiple filed names, which are separated by commas." %> 
<%@ attribute name="onchangeableExtraInfoFieldNames" required="false"
    description="The names of the fields that can be used to hold the descriptive information of the extra editable fields. The attribute can hold multiple filed names, which are separated by commas." %> 
    
<%@ attribute name="sortableFieldNames" required="false"
    description="The names of the fields that can be sorted" %>              
                                        
<%@ attribute name="hasActions" required="false"
    description="Determine if a user can take an action on the given line" %>
<%@ attribute name="actionSuffix" required="false"
    description="the suffix of the actions" %>    
<%@ attribute name="readOnlySection" required="false"
    description="Determine if the detail lines will be rended as a readonly section" %>	              
	
<%@ attribute name="ferderalTotalFieldNames" required="false"
	description="Indicate the field names that hold the federal funding total amount. The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="nonFerderalTotalFieldNames" required="false"
	description="Indicate the field names that hold the nonfederal funding total amount. The attribute can hold multiple filed names, which are separated by commas."%>
<%@ attribute name="grandTotalFieldNames" required="false"
	description="Indicate the field names that hold the grand total amount. The attribute can hold multiple filed names, which are separated by commas."%>			              

<c:set var="commaDeliminator" value=","/>
<c:set var="semicolonDeliminator" value=";"/>

<c:set var="countOfColumns" value="${fn:length(fn:split(detailFieldNames, commaDeliminator))}" />
<c:set var="countOfColumns" value="${hasActions ? countOfColumns + 2: countOfColumns + 1}" />

<c:set var="countOfTotalColumns" value="${fn:length(fn:split(grandTotalFieldNames, commaDeliminator))}" />
<c:set var="countOfTotalColumns" value="${hasActions ? countOfTotalColumns + 2 : countOfTotalColumns + 1}" />

<c:set var="colspanOfTotalLabel" value="${countOfColumns - countOfTotalColumns + 1}"/>

<c:set var="completeEditableFieldNames" value="${extraEditableFieldNames}${commaDeliminator}${editableFieldNames}"/>
<c:set var="completeOnchangeForEditableFieldNames" value="${onchangeForExtraEditableFieldNames}${commaDeliminator}${onchangeForEditableFieldNames}" />
<c:set var="completeOnchangeableInfoFieldNames" value="${onchangeableExtraInfoFieldNames}${commaDeliminator}${onchangeableInfoFieldNames}" />

<c:set var="actionForExistingLine" value="recalculate${actionSuffix},revert${actionSuffix}" />
<c:set var="actionForExistingLineImageFileName" value="tinybutton-recalculate.gif,tinybutton-revert1.gif" />

<c:set var="actionForNewLine" value="recalculate${actionSuffix},delete${actionSuffix}" />
<c:set var="actionForNewLineImageFileName" value="tinybutton-recalculate.gif,tinybutton-delete1.gif" />

<c:set var="countOfFerderalFunding" value="0"/>
<c:set var="countOfOtherFunding" value="0"/> 
<c:forEach var="detailLine" items="${detailLines}" varStatus="status">
	<c:if test="${detailLine.federalOrFederalPassThroughIndicator}">
		<c:set var="countOfFerderalFunding" value="${countOfFerderalFunding + 1}"/>
	</c:if>
	<c:if test="${!detailLine.federalOrFederalPassThroughIndicator}">
		<c:set var="countOfOtherFunding" value="${countOfOtherFunding + 1}"/>
	</c:if>
</c:forEach>

<c:set var="federalFund" value="${countOfFerderalFunding > 0 ? 'true' : ''}" />
<c:set var="otherFund" value="${countOfOtherFunding > 0 ? 'false' : ''}" />
<c:set var="federalFundingType" value="${federalFund}${commaDeliminator}${otherFund}" /> 
<c:set var="federalFundingIndicators" value="${fn:split(federalFundingType, commaDeliminator)}"/>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable" id="${id}">
	<c:forEach var="federalFunding" items="${federalFundingIndicators}" varStatus="indicatorStatus">
				
		<c:set var="subtotalGroup" value=""/>
		<c:set var="groupDescription" value=""/>
		<c:if test="${federalFunding}">
			<c:set var="groupDescription" value="Federal and Federal Pass Through Accounts"/>
			<c:set var="subtotalGroup" value="${ferderalTotalFieldNames}"/>
		</c:if>
		
		<c:if test="${!federalFunding}">
			<c:set var="groupDescription" value="Other Sponsored and Non-sponsored Accounts"/>
			<c:set var="subtotalGroup" value="${nonFerderalTotalFieldNames}"/>
		</c:if>
	
		<tr>
			<td colspan="${countOfColumns}" class="subhead">	
				<span class="subhead-left">${groupDescription}</span>
			</td>		
		</tr>
	
		<tr>
			<c:set var="actualSortableFieldNames" value="${indicatorStatus.index == 0 ? sortableFieldNames : '' }"/>
			<ec:detailLineHeader attributes="${attributes}"	
				detailFieldNames="${detailFieldNames}" 
				sortableFieldNames = "${actualSortableFieldNames}"
				hasActions="${hasActions}"/>
		</tr> 
	
		<!-- populate the table with the given deatil lines -->
		<c:set var="lineIndex" value="0"/>		
		<c:forEach var="detailLine" items="${detailLines}" varStatus="status">
			<c:if test="${detailLine.federalOrFederalPassThroughIndicator == federalFunding}">
			<tr>
				<c:set var="lineIndex" value="${lineIndex + 1}"/>
				<kul:htmlAttributeHeaderCell literalLabel="${lineIndex}"/>
				
				<c:set var="editable" value="${detailLine.editable || detailLine.newLineIndicator}" />
				
				<c:if test="${editable}">		
					<c:set var="existing" value="${!detailLine.newLineIndicator}" />
					<c:set var="actualEditableFieldNames" value="${existing ? editableFieldNames : completeEditableFieldNames}" />
					<c:set var="actualOnchangeForEditableFieldNames" value="${existing ? onchangeForEditableFieldNames : completeOnchangeForEditableFieldNames}" />
					<c:set var="actualOnchangeableInfoFieldNames" value="${existing ? onchangeableInfoFieldNames : completeOnchangeableInfoFieldNames}" />
					<c:set var="actions" value="${existing ? actionForExistingLine : actionForNewLine}" />
					<c:set var="actionImageFileNames" value="${existing ? actionForExistingLineImageFileName : actionForNewLineImageFileName}" />
				</c:if>
				
				<c:if test="${!editable}">		
					<c:set var="actualEditableFieldNames" value="" />
					<c:set var="actualOnchangeForEditableFieldNames" value="" />
					<c:set var="actualOnchangebleInfoFieldNames" value="" />
					<c:set var="actions" value=""/>
					<c:set var="actionImageFileNames" value=""/>
				</c:if>			
				
				<ec:detailLine detailLine="${detailLine}" 
					detailLineFormName="${detailLineFormName}[${status.index}]"
					attributes="${attributes}"
					detailFieldNames="${detailFieldNames}"
					detailFieldNamesWithHiddenFormWhenReadonly="${detailFieldNamesWithHiddenFormWhenReadonly}"
					editableFieldNames="${actualEditableFieldNames}"
					hiddenFieldNames="${hiddenFieldNames}"
					onchangeForEditableFieldNames="${actualOnchangeForEditableFieldNames}"
					onchangeableInfoFieldNames="${actualOnchangeableInfoFieldNames}"
					inquirableUrl="${inquirableUrl[status.index]}"
					fieldInfo="${fieldInfo[status.index]}"
					relationshipMetadata ="${relationshipMetadata}" readOnlySection="${readOnlySection}"
					hasActions="${hasActions}" index="${status.index}" actions="${actions}" actionImageFileNames="${actionImageFileNames}"/>			
			</tr>
			</c:if>		
		</c:forEach>
		
		<c:if test="${fn:length(subtotalGroup) > 0}" >
			<tr>
				<td colspan="${colspanOfTotalLabel}" class="infoline"><div class="right"><strong>Subtotals:</strong></div></td>
				<ec:detailLineTotal totalFieldNames="${subtotalGroup}" readOnlySection="${readOnlySection}" hasActions="${hasActions}" />						
			</tr>
		</c:if>	
	</c:forEach>

	<c:if test="${fn:length(grandTotalFieldNames) > 0}" >
		<tr>
			<td colspan="${countOfColumns}" class="subhead">	
				<span class="subhead-left">Grand Totals</span>
			</td>		
		</tr>
	
		<tr>				
			<td colspan="${colspanOfTotalLabel}" class="infoline"><div class="right"><strong>Grand Totals:</strong></div></td>
			<ec:detailLineTotal totalFieldNames="${grandTotalFieldNames}" readOnlySection="${readOnlySection}" hasActions="${hasActions}" />		
		</tr>
	</c:if>
</table>	      
