<%--
 Copyright 2007-2010 The Kuali Foundation
 
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

<%@ attribute name="laborOriginEntries" required="true" type="java.util.List" description="The list of LaborOriginEntries that we'll iterate to display." %>

<c:if test="${empty laborOriginEntries}">
    No Origin Entries found.
</c:if>
<c:if test="${!empty laborOriginEntries}">
    <kul:tableRenderPagingBanner pageNumber="${KualiForm.originEntrySearchResultTableMetadata.viewedPageNumber}"
            totalPages="${KualiForm.originEntrySearchResultTableMetadata.totalNumberOfPages}"
            firstDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}" lastDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}"
            resultsActualSize="${KualiForm.originEntrySearchResultTableMetadata.resultsActualSize}" resultsLimitedSize="${KualiForm.originEntrySearchResultTableMetadata.resultsLimitedSize}"
            buttonExtraParams=".anchor${currentTabIndex}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.originEntrySearchResultTableMetadata.columnToSortIndex}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.sortDescending" value="${KualiForm.originEntrySearchResultTableMetadata.sortDescending}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.viewedPageNumber" value="${KualiForm.originEntrySearchResultTableMetadata.viewedPageNumber}"/>
    <table class="datatable-100" id="laborOriginEntry" cellpadding="0" cellspacing="0">
        <thead>
            <tr>
                <c:if test="${KualiForm.editableFlag == true}">
                    <th>Manual Edit</th>
                </c:if>
                <c:forEach items="${KualiForm.tableRenderColumnMetadata}" var="column">
                    <th class="sortable">
                        <c:out value="${column.columnTitle}"/><c:if test="${empty column.columnTitle}">$nbsp;</c:if>
                    </th>
                </c:forEach>
            </tr>
            <tr>
                <c:if test="${KualiForm.editableFlag == true and KualiForm.showOutputFlag == false}">
                    <td>&nbsp;</td>
                </c:if>
                <c:forEach items="${KualiForm.tableRenderColumnMetadata}" var="column" varStatus="columnLoopStatus">
                    <td class="sortable" style="text-align: center;">
                        <c:if test="${column.sortable}">
                            <html:image property="methodToCall.sort.${columnLoopStatus.index}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}sort.gif" styleClass="tinybutton" alt="Sort column" title="Sort column ${column.columnTitle}"/>
                        </c:if>
                        <c:if test="${!column.sortable}">
                            &nbsp;
                        </c:if>
                    </td>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${laborOriginEntries}" var="originEntry" varStatus="loopStatus" 
                    begin="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}" end="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}">
                <c:set var="rowclass" value="odd"/>
                <c:if test="${loopStatus.count % 2 == 0}">
                    <c:set var="rowclass" value="even"/>
                </c:if>
                <tr class="${rowclass}">
                    <c:if test="${KualiForm.editableFlag == true and KualiForm.editMethod == 'M'}">
                        <td>
                            <html:image property="methodToCall.editManualEntry.entryId${originEntry.entryId}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="edit" title="edit" />
                            <html:image property="methodToCall.deleteManualEntry.entryId${originEntry.entryId}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" title="delete" />
                        </td>
                    </c:if>
                    <td class="infocell"><c:out value="${originEntry.universityFiscalYear}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.chartOfAccountsCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.accountNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.subAccountNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialObjectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialSubObjectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialBalanceTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialObjectTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.universityFiscalPeriodCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialDocumentTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialSystemOriginationCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.documentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntrySequenceNumber}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.positionNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.projectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntryDescription}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntryAmount}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionDebitCreditCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.organizationDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.organizationReferenceId}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialDocumentTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialSystemOriginationCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialDocumentReversalDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionEncumbranceUpdateCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.transactionPostingDate}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.payPeriodEndDate}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.transactionTotalHours}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.payrollEndDateFiscalYear}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.payrollEndDateFiscalPeriodCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.emplid}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.employeeRecord}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.earnCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.payGroup}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.salaryAdministrationPlan}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.grade}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.runIdentifier}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.laborLedgerOriginalChartOfAccountsCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.laborLedgerOriginalAccountNumber}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.laborLedgerOriginalSubAccountNumber}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.laborLedgerOriginalFinancialObjectCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.laborLedgerOriginalFinancialSubObjectCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.hrmsCompany}" />&nbsp;</td>
					<td class="infocell"><c:out value="${originEntry.setid}" />&nbsp;</td>
				</tr>
            </c:forEach>
        <tbody>
    </table>
</c:if>
