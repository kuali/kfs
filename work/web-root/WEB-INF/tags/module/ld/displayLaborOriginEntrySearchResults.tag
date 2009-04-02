<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="laborOriginEntries" required="true" type="java.util.List" description="The list of LaborOriginEntries that we'll iterate to display." %>
<%@ attribute name="image" required="false"%>

<c:set var="imageName" value="${empty image ? 'sort.gif' : image}"/>

<c:if test="${empty laborOriginEntries}">
    No Origin Entries found.
</c:if>
<c:if test="${!empty laborOriginEntries}">
    <kul:tableRenderPagingBanner pageNumber="${KualiForm.originEntrySearchResultTableMetadata.viewedPageNumber}"
            totalPages="${KualiForm.originEntrySearchResultTableMetadata.totalNumberOfPages}"
            firstDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}" lastDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}"
            resultsActualSize="${KualiForm.originEntrySearchResultTableMetadata.resultsActualSize}" resultsLimitedSize="${KualiForm.originEntrySearchResultTableMetadata.resultsLimitedSize}"
            buttonExtraParams=".anchor${currentTabIndex}"/>
    <table class="datatable-100" id="laborOriginEntry" cellpadding="0" cellspacing="0">
        <thead>
            <tr>
                <c:if test="${KualiForm.editableFlag == true}">
                    <th>Manual Edit</th>
                </c:if>
                <c:forEach items="${KualiForm.tableRenderColumnMetadata}" var="column">
                    <th class="sortable">
                        ${column.columnTitle}<c:if test="${empty column.columnTitle}">&nbsp;</c:if>
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
							<c:set var="sortButtonName" value="methodToCall.sort.${columnLoopStatus.index}.anchor${currentTabIndex}.x" />
								   ${kfunc:registerEditableProperty(KualiForm, sortButtonName)}
								   <input type="image" tabindex="${tabindex}" name="${sortButtonName}"
										  src="${ConfigProperties.kr.externalizable.images.url}${imageName}" alt="Sort column ${column.columnTitle}" 
										  title="Sort column ${column.columnTitle}" border="0" valign="middle"/>
                        </c:if>
                        <c:if test="${!column.sortable}">
                            &nbsp;
                        </c:if>
                    </td>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${laborOriginEntries}" var="laborOriginEntry" varStatus="loopStatus" 
                    begin="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}" end="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}">
                <c:set var="rowclass" value="odd"/>
                <c:if test="${loopStatus.count % 2 == 0}">
                    <c:set var="rowclass" value="even"/>
                </c:if>
                <tr class="${rowclass}">
                    <c:if test="${KualiForm.editableFlag == true and KualiForm.editMethod == 'M'}">
                        <td>
                            <html:image property="methodToCall.editManualEntry.entryId${laborOriginEntry.entryId}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="edit" title="edit" />
                            <html:image property="methodToCall.deleteManualEntry.entryId${laborOriginEntry.entryId}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" title="delete" />
                        </td>
                    </c:if>
                    <td class="infocell">${laborOriginEntry.universityFiscalYear}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.chartOfAccountsCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.accountNumber}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.subAccountNumber}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.financialObjectCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.financialSubObjectCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.financialBalanceTypeCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.financialObjectTypeCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.universityFiscalPeriodCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.financialDocumentTypeCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.financialSystemOriginationCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.documentNumber}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.transactionLedgerEntrySequenceNumber}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.positionNumber}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.projectCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.transactionLedgerEntryDescription}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.transactionLedgerEntryAmount}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.transactionDebitCreditCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.transactionDate}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.organizationDocumentNumber}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.organizationReferenceId}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.referenceFinancialDocumentTypeCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.referenceFinancialSystemOriginationCode}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.referenceFinancialDocumentNumber}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.financialDocumentReversalDate}&nbsp;</td>
                    <td class="infocell">${laborOriginEntry.transactionEncumbranceUpdateCode}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.transactionPostingDate}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.payPeriodEndDate}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.transactionTotalHours}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.payrollEndDateFiscalYear}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.payrollEndDateFiscalPeriodCode}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.emplid}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.employeeRecord}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.earnCode}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.payGroup}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.salaryAdministrationPlan}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.grade}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.runIdentifier}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.laborLedgerOriginalChartOfAccountsCode}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.laborLedgerOriginalAccountNumber}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.laborLedgerOriginalSubAccountNumber}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.laborLedgerOriginalFinancialObjectCode}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.laborLedgerOriginalFinancialSubObjectCode}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.hrmsCompany}&nbsp;</td>
					<td class="infocell">${laborOriginEntry.setid}&nbsp;</td>
				</tr>
            </c:forEach>
        <tbody>
    </table>
</c:if>
