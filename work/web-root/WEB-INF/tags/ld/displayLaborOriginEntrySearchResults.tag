<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="laborOriginEntries" required="true" type="java.util.List" description="The list of LaborOriginEntries that we'll iterate to display." %>

<c:if test="${empty laborOriginEntries}">
    No Origin Entries found.
</c:if>setid
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
                <c:forEach items="${KualiForm.laborTableRenderColumnMetadata}" var="column">
                    <th class="sortable">
                        <c:out value="${column.columnTitle}"/><c:if test="${empty column.columnTitle}">$nbsp;</c:if>
                    </th>
                </c:forEach>
            </tr>
            <tr>
                <c:if test="${KualiForm.editableFlag == true and KualiForm.showOutputFlag == false}">
                    <th>&nbsp;</th>
                </c:if>
                <c:forEach items="${KualiForm.laborTableRenderColumnMetadata}" var="column" varStatus="columnLoopStatus">
                    <th class="sortable">
                        <c:if test="${column.sortable}">
                            <input name="methodToCall.sort.<c:out value="${columnLoopStatus.index}"/>.anchor${currentTabIndex}.x" type="image" src="${ConfigProperties.kr.externalizable.images.url}sort.gif" alt="Sort column ${column.columnTitle}" valign="bottom" title="Sort column ${column.columnTitle}">
                        </c:if>
                        <c:if test="${!column.sortable}">
                            &nbsp;
                        </c:if>
                    </th>
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
                            <html:image property="methodToCall.editManualEntry.entryId${laborOriginEntry.entryId}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif" styleClass="tinybutton" alt="edit" />
                            <html:image property="methodToCall.deleteManualEntry.entryId${laborOriginEntry.entryId}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" />
                        </td>
                    </c:if>
                    <td class="infocell"><c:out value="${laborOriginEntry.universityFiscalYear}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.chartOfAccountsCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.accountNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.subAccountNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.financialObjectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.financialSubObjectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.financialBalanceTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.financialObjectTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.universityFiscalPeriodCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.financialDocumentTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.financialSystemOriginationCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.documentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.transactionLedgerEntrySequenceNumber}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.positionNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.projectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.transactionLedgerEntryDescription}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.transactionLedgerEntryAmount}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.transactionDebitCreditCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.transactionDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.organizationDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.organizationReferenceId}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.referenceFinancialDocumentTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.referenceFinancialSystemOriginationCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.referenceFinancialDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.financialDocumentReversalDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${laborOriginEntry.transactionEncumbranceUpdateCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.transactionPostingDate}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.payPeriodEndDate}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.transactionTotalHours}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.payrollEndDateFiscalYear}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.payrollEndDateFiscalPeriodCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.emplid}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.employeeRecord}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.earnCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.payGroup}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.salaryAdministrationPlan}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.grade}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.runIdentifier}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.laborLedgerOriginalChartOfAccountsCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.laborLedgerOriginalAccountNumber}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.laborLedgerOriginalSubAccountNumber}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.laborLedgerOriginalFinancialObjectCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.laborLedgerOriginalFinancialSubObjectCode}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.hrmsCompany}" />&nbsp;</td>
					<td class="infocell"><c:out value="${laborOriginEntry.setid}" />&nbsp;</td>
				</tr>
            </c:forEach>
        <tbody>
    </table>
</c:if>
