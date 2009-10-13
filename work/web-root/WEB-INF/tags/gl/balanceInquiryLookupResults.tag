<%--
 Copyright 2007 The Kuali Foundation
 
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

<%@ attribute name="resultsList" required="true" type="java.util.List" description="The rows of fields that we'll iterate to display." %>

<c:set var="imageSelectAll" value="${empty image ? 'buttonsmall_selectall.gif' : image}"/>
<c:set var="imageUnselectAll" value="${empty image ? 'buttonsmall_unselall.gif' : image}"/>
<c:set var="imageSort" value="${empty image ? 'sort.gif' : image}"/>

<c:if test="${empty resultsList && KualiForm.methodToCall != 'start' && KualiForm.methodToCall != 'refresh'}">
	There were no results found.
</c:if>

<c:if test="${!empty resultsList}">
    <c:if test="${KualiForm.searchUsingOnlyPrimaryKeyValues}">
    	<bean-el:message key="lookup.using.primary.keys" arg0="${KualiForm.primaryKeyFieldLabels}"/>
    	<br/><br/>
    </c:if>
	<c:choose>
		<c:when test="${param['d-16544-e'] == null}">
			<kul:tableRenderPagingBanner pageNumber="${KualiForm.viewedPageNumber}" totalPages="${KualiForm.totalNumberOfPages}"
				firstDisplayedRow="${KualiForm.firstRowIndex}" lastDisplayedRow="${KualiForm.lastRowIndex}" resultsActualSize="${KualiForm.resultsActualSize}"
				resultsLimitedSize="${KualiForm.resultsLimitedSize}"
				buttonExtraParams=".${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}"/>
			<input type="hidden" name="${Constants.MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM}" value="${KualiForm.compositeSelectedObjectIds}"/>
			<input type="hidden" name="${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.columnToSortIndex}"/>

			<p>
				<c:set var="balanceInquirySelectAllButtonName" value="methodToCall.selectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x" />
					${kfunc:registerEditableProperty(KualiForm, balanceInquirySelectAllButtonName)}
					<input type="image" tabindex="${tabindex}" name="${balanceInquirySelectAllButtonName}"
   						src="${ConfigProperties.kr.externalizable.images.url}${imageSelectAll}" alt="Select all rows" title="Select all rows" border="0" class="tinybutton" valign="middle"/>
				<c:set var="balanceInquiryUnselectAllButtonName" value="methodToCall.unselectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x" />
					${kfunc:registerEditableProperty(KualiForm, balanceInquiryUnselectAllButtonName)}
					<input type="image" tabindex="${tabindex}" name="${balanceInquiryUnselectAllButtonName}"
   						src="${ConfigProperties.kr.externalizable.images.url}${imageUnselectAll}" alt="Unselect all rows" title="Unselect all rows" border="0" class="tinybutton" valign="middle"/>
			</p>
			
            <c:set var="numOfMonthField" value="14" scope="request" />            
            <c:set var="numOfNonMonthField" value="${fn:length(resultsList[0].columns) - numOfMonthField}" scope="request" />
            
			<table cellpadding="0" class="datatable-100" cellspacing="1" id="row">
				<thead>
					<tr>
			    		<c:forEach items="${resultsList[0].columns}" var="column" begin="0" end="${numOfNonMonthField}" varStatus="columnLoopStatus">
							<th class="sortable">
								${column.columnTitle}
							</th>
						</c:forEach>
					</tr>
					<tr>
						<c:forEach items="${resultsList[0].columns}" var="column" begin="0" end="${numOfNonMonthField}" varStatus="columnLoopStatus">
							<th class="sortable" align="center">
							<c:set var="sortButtonName" value="methodToCall.sort.${columnLoopStatus.index}.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x" />
								   ${kfunc:registerEditableProperty(KualiForm, sortButtonName)}
								   <input type="image" tabindex="${tabindex}" name="${sortButtonName}"
										  src="${ConfigProperties.kr.externalizable.images.url}${imageSort}" alt="Sort column ${column.columnTitle}" 
										  title="Sort column ${column.columnTitle}" border="0" valign="middle"/>
							</th>
						</c:forEach>
					</tr>
				</thead>

		        <c:set var="rowCounter" value="0" scope="request" />
				<c:forEach items="${resultsList}" var="row" varStatus="rowLoopStatus" begin="${KualiForm.firstRowIndex}" end="${KualiForm.lastRowIndex}">
					<tr class="even">
						<c:forEach items="${row.columns}" var="column" begin="0" end="${numOfNonMonthField}">
							<td class="infocell" title="${column.propertyValue}">
								<c:if test="${!empty column.propertyURL}">
									<a href="<c:out value="${column.propertyURL}"/>" target="blank">
								</c:if>
								
								<c:out value="${fn:substring(column.propertyValue, 0, column.maxLength)}"/>
								<c:if test="${column.maxLength gt 0 && fn:length(column.propertyValue) gt column.maxLength}">...</c:if>
								
								<c:if test="${!empty column.propertyURL}"></a></c:if>
							</td>
						</c:forEach>
					</tr>
					
                    <tr>
                        <td colspan="${numOfNonMonthField + 1}"><br/>
                            <center>
                            <table class="datatable-80" cellspacing="0" cellpadding="0" width="100%">
                                <c:forEach var="column" items="${row.columns}" begin="${numOfNonMonthField + 1}" varStatus="columnStatus">
                                    <c:if test="${(columnStatus.index - numOfNonMonthField) % 4 == 1}">
                                	<tr>
                                    </c:if>
                                    
                                    <c:if test="${(columnStatus.index - numOfNonMonthField + 1) eq numOfMonthField}">
                                        <td colspan="6"></td>                                      
                                    </c:if>
                                    
                                    <th class="infocell" style="text-align: left; width: 10%; white-space: nowrap;">                                    	
                                    	<c:set var="monthlyAmount" value="${fn:replace(column.propertyValue, ',', '')}"/>
                                    	
                                    	<!-- restore the negtive number -->
                                    	<c:if test="${fn:contains(monthlyAmount, '(')}">
                                    		<c:set var="monthlyAmount" value="${fn:replace(monthlyAmount, '(', '-')}"/>
                                    		<c:set var="monthlyAmount" value="${fn:replace(monthlyAmount, ')', '')}"/>
                                    	</c:if>
                                    	
                                    	<fmt:formatNumber var="amount" value="${monthlyAmount}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/>
                                    	<c:set var="monthlyAmount" value="${fn:replace(amount, '.', '')}"/>

                                    	<c:set var="objectIdPrefix" value="${row.objectId}.${column.propertyName}" />
                                    	<c:set var="objectId" value="${objectIdPrefix}.${monthlyAmount}" />
                                    	
                                    	<c:set var="checked" value="${empty KualiForm.compositeObjectIdMap[objectId] ? '' : 'checked=checked'}" />
                                    	<c:set var="disabled" value="${amount != 0.0 ? '' : 'disabled=disabled'}" />
                                    	
						                <c:set var="checkBoxObjectIdName" value="${Constants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX}${objectId}" />                    	
											${kfunc:registerEditableProperty(KualiForm, checkBoxObjectIdName)}
											<input type="checkbox" name="${checkBoxObjectIdName}" title="${column.columnTitle}" value="checked" ${disabled} ${checked}>
												${column.columnTitle}
											 </input>
                                    	<input type="hidden" name="${Constants.MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX}${objectId}" value="onscreen"/>
                                   	</th>
                                    
                                    <td class="numbercell" width="10%">
                                    	<a href="${column.propertyURL}" target="blank">${column.propertyValue}</a>
                                    </td>

                                    <c:if test="${(columnStatus.index -numOfNonMonthField) % 4 == 0}">
                                		</tr>
                                    </c:if>
                                </c:forEach>
                            </table>
                            </center><br/>
                        </td>
                    </tr>
				</c:forEach>
			</table>
			
			<p>
				<c:set var="balanceInquirySelectAllButtonName" value="methodToCall.selectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x" />
					${kfunc:registerEditableProperty(KualiForm, balanceInquirySelectAllButtonName)}
					<input type="image" tabindex="${tabindex}" name="${balanceInquirySelectAllButtonName}"
   						src="${ConfigProperties.kr.externalizable.images.url}${imageSelectAll}" alt="Select all rows" title="Select all rows" border="0" class="tinybutton" valign="middle"/>
				<c:set var="balanceInquiryUnselectAllButtonName" value="methodToCall.unselectAll.${Constants.METHOD_TO_CALL_PARM12_LEFT_DEL}${KualiForm.searchUsingOnlyPrimaryKeyValues}${Constants.METHOD_TO_CALL_PARM12_RIGHT_DEL}.x" />
					${kfunc:registerEditableProperty(KualiForm, balanceInquiryUnselectAllButtonName)}
					<input type="image" tabindex="${tabindex}" name="${balanceInquiryUnselectAllButtonName}"
   						src="${ConfigProperties.kr.externalizable.images.url}${imageUnselectAll}" alt="Unselect all rows" title="Unselect all rows" border="0" class="tinybutton" valign="middle"/>
			
			</p>
			<kul:multipleValueLookupExportBanner/>
		</c:when>
		<c:otherwise>
			<display:table class="datatable-100" cellspacing="0"
				requestURIcontext="false" cellpadding="0" name="${reqSearchResults}"
				id="row" export="true" pagesize="100">
				<c:forEach items="${row.columns}" var="column" varStatus="loopStatus">
					<display:column class="${colClass}" sortable="${column.sortable}"
								title="${column.columnTitle}" comparator="${column.comparator}"
								maxLength="${column.maxLength}"><c:out value="${column.propertyValue}" escapeXml="false" default="" /></display:column>
				</c:forEach>
			</display:table>
		</c:otherwise>
	</c:choose>
</c:if>
