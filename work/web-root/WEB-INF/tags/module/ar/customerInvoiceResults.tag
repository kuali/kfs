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

<c:set var="documentAttributes" value="${DataDictionary.CustomerInvoiceDocument.attributes}" />
<%@ attribute name="subResultRows" required="true" type="java.util.List" description="The rows of fields that we'll iterate to display." %>

<tr>
	<td colspan="5">
		<br />
			<center>
			<table class="datatable-80" cellspacing="0" cellpadding="0" width="100%">
				<thead>
					<tr>
						<th>
							Write Off
						</th>
			    		<c:forEach items="${subResultRows[0].columns}" var="column" begin="0" end="${fn:length(subResultRows[0].columns)}" varStatus="columnLoopStatus">
							<th class="sortable">
								${column.columnTitle}
							</th>
						</c:forEach>
					</tr>
				</thead>

				<c:forEach items="${subResultRows}" var="row" varStatus="rowLoopStatus" begin="0" end="${fn:length(subResultRows)}">
					<tr>
					    <td>
							<c:set var="objectId" value="${row.objectId}" />
							<c:set var="checked" value="${empty KualiForm.compositeObjectIdMap[objectId] ? '' : 'checked=checked'}" />
							<c:set var="disabled" value="${true ? '' : 'disabled=disabled'}" />							
							<c:set var="checkBoxName" value="${Constants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX}${objectId}" />
							<c:set var="hiddenFieldName" value="${Constants.MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX}${objectId}" />
							<input type="checkbox" title="${column.columnTitle}" name="${checkBoxName}" value="checked" ${disabled} ${checked}/>
							${kfunc:registerEditableProperty(KualiForm, checkBoxName)}
							<input type="hidden" name="${hiddenFieldName}" value="onscreen"/>
							${kfunc:registerEditableProperty(KualiForm, hiddenFieldName)}
						</td>
						<c:forEach items="${row.columns}" var="column" begin="0" end="${fn:length(row.columns)}">
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
				</c:forEach>
			</table>
			</center>
		<br />
	</td>
</tr>
