<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<%@ attribute name="resultsList" required="true" type="java.util.List" description="The rows of fields that we'll iterate to display."%>

<c:if test="${empty resultsList && KualiForm.methodToCall != 'start' && KualiForm.methodToCall != 'refresh'}">
	There were no eligible awards retrieved. Please check if search criteria is valid and appropriate Billing Frequency has been set. 
</c:if>

<c:if test="${!empty resultsList}">

	<kul:tableRenderPagingBanner pageNumber="${KualiForm.viewedPageNumber}" totalPages="${KualiForm.totalNumberOfPages}"
		firstDisplayedRow="${KualiForm.firstRowIndex}" lastDisplayedRow="${KualiForm.lastRowIndex}" resultsActualSize="${KualiForm.resultsActualSize}"
		resultsLimitedSize="${KualiForm.resultsLimitedSize}" />
	<input type="hidden" name="${Constants.MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM}" value="${KualiForm.compositeSelectedObjectIds}" />
	<input type="hidden" name="${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.columnToSortIndex}" />
	${kfunc:registerEditableProperty(KualiForm, Constants.MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM)}
	${kfunc:registerEditableProperty(KualiForm, Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM)}
	
	<p>
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_selectall.gif" alt="Select all rows" title="Select all rows"
			styleClass="tinybutton"
			property="methodToCall.selectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
			value="Select All Rows" />
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_unselall.gif" alt="Unselect all rows" title="Unselect all rows"
			styleClass="tinybutton"
			property="methodToCall.unselectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
			value="Unselect All Rows" />
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_retselected.gif" styleClass="tinybutton"
			property="methodToCall.prepareToReturnSelectedResults.x" alt="Return selected results" title="Return selected results" />
	</p>

	<c:set var="numOfColumns" value="${fn:length(resultsList[0].columns)}" />

	<table cellpadding="0" class="datatable-100" cellspacing="1" id="row">
		<thead>
			<tr>
				<c:forEach items="${resultsList[0].columns}" var="column" begin="0" end="${numOfColumns}" varStatus="columnLoopStatus">
					<th class="sortable">${column.columnTitle}</th>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach items="${resultsList[0].columns}" var="column" begin="0" end="${numOfColumns}" varStatus="columnLoopStatus">
					<th class="sortable" align="center"><c:set var="sortableFieldName"
							value="methodToCall.sort.${columnLoopStatus.index}.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x" />
						<html:image property="${sortableFieldName}" src="${ConfigProperties.kr.externalizable.images.url}sort.gif"
							alt="Sort column ${column.columnTitle}" title="Sort column ${column.columnTitle}" /></th>
				</c:forEach>
			</tr>
		</thead>

		<c:set var="rowCounter" value="0" scope="request" />
		<c:forEach items="${resultsList}" var="row" varStatus="rowLoopStatus" begin="${KualiForm.firstRowIndex}" end="${KualiForm.lastRowIndex}">
			<tr class="even">
				<c:forEach items="${row.columns}" var="column" begin="0" end="${numOfColumns}">
					<td class="infocell" title="${column.propertyValue}"><c:if test="${!empty column.propertyURL}">
							<a href="<c:out value="${column.propertyURL}"/>" target="blank">
						</c:if> <c:out value="${fn:substring(column.propertyValue, 0, column.maxLength)}" /> <c:if
							test="${column.maxLength gt 0 && fn:length(column.propertyValue) gt column.maxLength}">...</c:if> <c:if test="${!empty column.propertyURL}">
							</a>
						</c:if></td>
				</c:forEach>
			</tr>
			<ar:awardResults subResultRows="${row.subResultRows}" />
		</c:forEach>
	</table>

	<p>
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_selectall.gif" alt="Select all rows" title="Select all rows"
			styleClass="tinybutton"
			property="methodToCall.selectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
			value="Select All Rows" />
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_unselall.gif" alt="Unselect all rows" title="Unselect all rows"
			styleClass="tinybutton"
			property="methodToCall.unselectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x"
			value="Unselect All Rows" />
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_retselected.gif" styleClass="tinybutton"
			property="methodToCall.prepareToReturnSelectedResults.x" alt="Return selected results" title="Return selected results" />
	</p>
</c:if>
