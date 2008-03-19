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

<%@ attribute name="id" required="false"
              description="the detail lines being displayed" %> 
<%@ attribute name="detailLines" required="true" type="java.util.List"
              description="the detail lines being displayed" %>             
<%@ attribute name="attributes" required="true" type="java.util.Map"
			  description="The DataDictionary entry containing attributes for the line fields."%>              
<%@ attribute name="detailFieldNames" required="true"
              description="the names of the fields that will be displayed" %>
<%@ attribute name="detailFieldNamesWithHiddenFormWhenReadonly" required="false"
			  description="The names of the fields that will have hidden forms when the fields are readonly. The attribute can hold multiple filed names, which are separated by commas."%>	             
<%@ attribute name="hiddenFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
<%@ attribute name="inquirableUrl" required="false" type="java.util.List"
              description="the names of the fields that can be inquirable" %>  
<%@ attribute name="fieldInfo" required="false" type="java.util.List"
              description="the information of the fields in the detail lines" %>
<%@ attribute name="relationshipMetadata" required="false"
	type="java.util.Map"
	description="This is a Map that holds a property name list of the primary key of the referenced class for each eligible field. The value of the attribute is used to build quick finder for the eligible fields."%>				                 

<%@ attribute name="editableFieldNames" required="false"
              description="the names of the fields that can be editable" %>
<%@ attribute name="extraEditableFieldNames" required="false"
              description="the names of the fields that can be editable" %>                
<%@ attribute name="onblurForEditableFieldNames" required="false"
              description="the funation names that retrives the information of the given editable fields" %> 
<%@ attribute name="onblurForExtraEditableFieldNames" required="false"
              description="the funation names that retrives the information of the given editable fields" %>              
<%@ attribute name="onblurableInfoFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
<%@ attribute name="onblurableExtraInfoFieldNames" required="false"
              description="the names of the fields that can be editable" %> 
<%@ attribute name="sortableFieldNames" required="false"
              description="the names of the fields that can be editable" %>              
                                        
<%@ attribute name="hasActions" required="false"
              description="determine if a user can tak an action on the given line" %>
<%@ attribute name="readOnlySection" required="false"
              description="determine if the field woulb be rendered as read-only or not" %>	              
	
<%@ attribute name="ferderalTotalFieldNames" required="false"
	description="To determine if a user can tak an action on the given detail line . If true, the  given actions can be rendered with the detail line."%>
<%@ attribute name="nonFerderalTotalFieldNames" required="false"
	description="To determine if a user can tak an action on the given detail line . If true, the  given actions can be rendered with the detail line."%>
<%@ attribute name="grandTotalFieldNames" required="false"
	description="To determine if a user can tak an action on the given detail line . If true, the  given actions can be rendered with the detail line."%>			              

<c:set var="commaDeliminator" value=","/>
<c:set var="semicolonDeliminator" value=";"/>

<c:set var="countOfColumns" value="${fn:length(fn:split(detailFieldNames, commaDeliminator))}" />
<c:set var="countOfColumns" value="${hasActions ? countOfColumns + 2: countOfColumns + 1}" />

<c:set var="countOfTotalColumns" value="${fn:length(fn:split(grandTotalFieldNames, commaDeliminator))}" />
<c:set var="countOfTotalColumns" value="${hasActions ? countOfTotalColumns + 2 : countOfTotalColumns + 1}" />

<c:set var="colspanOfTotalLabel" value="${countOfColumns - countOfTotalColumns + 1}"/>

<c:set var="completeEditableFieldNames" value="${extraEditableFieldNames}${commaDeliminator}${editableFieldNames}"/>
<c:set var="completeOnblurForEditableFieldNames" value="${onblurForExtraEditableFieldNames}${commaDeliminator}${onblurForEditableFieldNames}" />
<c:set var="completeOnblurableInfoFieldNames" value="${onblurableExtraInfoFieldNames}${commaDeliminator}${onblurableInfoFieldNames}" />

<c:set var="actionForExistingLine" value="recalculate,revert" />
<c:set var="actionForExistingLineImageFileName" value="tinybutton-recalculate.gif,tinybutton-revert1.gif" />

<c:set var="actionForNewLine" value="recalculate,delete" />
<c:set var="actionForNewLineImageFileName" value="tinybutton-recalculate.gif,tinybutton-delete1.gif" />

<c:set var="groupDescriptions" value="Federal and Federal Pass Through Accounts,Other Sponsored and Non-sponsored Accounts"/>
<c:set var="groupDescription" value="${fn:split(groupDescriptions, commaDeliminator)}"/>

<c:set var="subtotalGroups" value="${ferderalTotalFieldNames}${semicolonDeliminator}${nonFerderalTotalFieldNames}"/>
<c:set var="subtotalGroup" value="${fn:split(subtotalGroups, semicolonDeliminator)}"/>

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
		<tr>
			<td colspan="${countOfColumns}">
				<div class="h2-container"  style="width: 100%;"><h2>${groupDescription[indicatorStatus.index]}</h2></div>
			</td>		
		</tr>
	
		<tr>
			<c:set var="actualSortableFieldNames" value="${indicatorStatus.index == 0 ? sortableFieldNames : '' }"/>
			<er:detailLineHeader attributes="${attributes}"	
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
					<c:set var="actualOnblurForEditableFieldNames" value="${existing ? onblurForEditableFieldNames : completeOnblurForEditableFieldNames}" />
					<c:set var="actualOnblurableInfoFieldNames" value="${existing ? onblurableInfoFieldNames : completeOnblurableInfoFieldNames}" />
					<c:set var="actions" value="${existing ? actionForExistingLine : actionForNewLine}" />
					<c:set var="actionImageFileNames" value="${existing ? actionForExistingLineImageFileName : actionForNewLineImageFileName}" />
				</c:if>
				
				<c:if test="${!editable}">		
					<c:set var="actualEditableFieldNames" value="" />
					<c:set var="actualOnblurForEditableFieldNames" value="" />
					<c:set var="actualOnblurableInfoFieldNames" value="" />
					<c:set var="actions" value=""/>
					<c:set var="actionImageFileNames" value=""/>
				</c:if>			
				
				<er:detailLine detailLine="${detailLine}" 
					detailLineFormName="document.effortCertificationDetailLines[${status.index}]"
					attributes="${attributes}"
					detailFieldNames="${detailFieldNames}"
					detailFieldNamesWithHiddenFormWhenReadonly="${detailFieldNamesWithHiddenFormWhenReadonly}"
					editableFieldNames="${actualEditableFieldNames}"
					hiddenFieldNames="${hiddenFieldNames}"
					onblurForEditableFieldNames="${actualOnblurForEditableFieldNames}"
					onblurableInfoFieldNames="${actualOnblurableInfoFieldNames}"
					inquirableUrl="${inquirableUrl[status.index]}"
					fieldInfo="${fieldInfo[status.index]}"
					relationshipMetadata ="${relationshipMetadata}" readOnlySection="${readOnlySection}"
					hasActions="${hasActions}" index="${status.index}" actions="${actions}" actionImageFileNames="${actionImageFileNames}"/>			
			</tr>
			</c:if>		
		</c:forEach>
		
		<tr>
			<td colspan="${colspanOfTotalLabel}" class="infoline"><div class="right"><strong>Subtotals:</strong></div></td>
			<er:detailLineTotal totalFieldNames="${subtotalGroup[indicatorStatus.index]}" readOnlySection="${readOnlySection}" hasActions="${hasActions}" />						
		</tr>	
	</c:forEach>

	<tr>
		<td colspan="${countOfColumns}">
			<div class="h2-container" style="width: 100%;"><h2>Grand Totals</h2></div>
		</td>		
	</tr>

	<tr>				
		<td colspan="${colspanOfTotalLabel}" class="infoline"><div class="right"><strong>Grand Totals:</strong></div></td>
		<er:detailLineTotal totalFieldNames="${grandTotalFieldNames}" readOnlySection="${readOnlySection}" hasActions="${hasActions}" />		
	</tr>
</table>	      