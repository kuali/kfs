<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib uri="/tlds/struts-html-el.tld" prefix="html-el"%>
<%@ taglib uri="/tlds/displaytag.tld" prefix="display"%>
<%@ taglib uri="/tlds/struts-bean-el.tld" prefix="bean-el"%>
<%@ taglib tagdir="/WEB-INF/tags/gl/glcp" prefix="glcp"%>

<%@ attribute name="originEntries" required="true" type="java.util.List" description="The list of OriginEntries that we'll iterate to display." %>

<c:if test="${empty originEntries}">
    No Origin Entries found.
</c:if>
<c:if test="${!empty originEntries}">
    <kul:tableRenderPagingBanner pageNumber="${KualiForm.originEntrySearchResultTableMetadata.viewedPageNumber}"
            totalPages="${KualiForm.originEntrySearchResultTableMetadata.totalNumberOfPages}"
            firstDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}" lastDisplayedRow="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}"
            resultsActualSize="${KualiForm.originEntrySearchResultTableMetadata.resultsActualSize}" resultsLimitedSize="${KualiForm.originEntrySearchResultTableMetadata.resultsLimitedSize}"
            buttonExtraParams=".anchor${currentTabIndex}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.originEntrySearchResultTableMetadata.columnToSortIndex}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.sortDescending" value="${KualiForm.originEntrySearchResultTableMetadata.sortDescending}"/>
    <input type="hidden" name="originEntrySearchResultTableMetadata.viewedPageNumber" value="${KualiForm.originEntrySearchResultTableMetadata.viewedPageNumber}"/>
    <table class="datatable-100" id="originEntry" cellpadding="0" cellspacing="0">
        <thead>
            <tr>
                <c:if test="${KualiForm.editableFlag == true and KualiForm.showOutputFlag == false}">
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
                    <th>&nbsp;</th>
                </c:if>
                <c:forEach items="${KualiForm.tableRenderColumnMetadata}" var="column" varStatus="columnLoopStatus">
                    <th class="sortable">
                        <c:if test="${column.sortable}">
                            <input name="methodToCall.sort.<c:out value="${columnLoopStatus.index}"/>.anchor${currentTabIndex}.x" type="image" src="images/sort.gif" alt="Sort column ${column.columnTitle}" valign="bottom" title="Sort column ${column.columnTitle}">
                        </c:if>
                        <c:if test="${!column.sortable}">
                            &nbsp;
                        </c:if>
                    </th>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${originEntries}" var="originEntry" varStatus="loopStatus" 
                    begin="${KualiForm.originEntrySearchResultTableMetadata.firstRowIndex}" end="${KualiForm.originEntrySearchResultTableMetadata.lastRowIndex}">
                <c:set var="rowclass" value="odd"/>
                <c:if test="${loopStatus.count % 2 == 0}">
                    <c:set var="rowclass" value="even"/>
                </c:if>
                <tr class="${rowclass}">
                    <c:if test="${KualiForm.editableFlag == true and KualiForm.showOutputFlag == false}">
                        <td>
                            <html:image property="methodToCall.editManualEntry.entryId${originEntry.entryId}.anchor${currentTabIndex}" src="images/tinybutton-edit1.gif" styleClass="tinybutton" alt="edit" />
                            <html:image property="methodToCall.deleteManualEntry.entryId${originEntry.entryId}.anchor${currentTabIndex}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" />
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
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntryDescription}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionLedgerEntryAmount}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionDebitCreditCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.organizationDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.projectCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.organizationReferenceId}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialDocumentTypeCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialSystemOriginationCode}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.referenceFinancialDocumentNumber}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.financialDocumentReversalDate}" />&nbsp;</td>
                    <td class="infocell"><c:out value="${originEntry.transactionEncumbranceUpdateCode}" />&nbsp;</td>
                </tr>
            </c:forEach>
        <tbody>
    </table>
</c:if>
