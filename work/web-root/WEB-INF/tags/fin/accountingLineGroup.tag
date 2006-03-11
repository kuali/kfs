<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="logic" uri="/tlds/struts-logic.tld" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<%@ attribute name="isSource" required="true"
              description="Boolean whether this group is of source or target lines." %>
<%@ attribute name="rightColumnCount" required="true"
              description="4 less than the total number of columns in the
              accounting lines table.  The total depends on the number
              of optional fields and whether there is an action button column." %>
<%@ attribute name="editingMode" required="true" type="java.util.Map"%>
<%@ attribute name="editableAccounts" required="true" type="java.util.Map"
              description="Map of Accounts which this user is allowed to edit; only used if editingMode != fullEntry " %>
<%@ attribute name="editableFields" required="false" type="java.util.Map"
              description="Map of accounting line fields which this user is allowed to edit" %>
<%@ attribute name="optionalFields" required="false"
              description="A comma separated list of names of accounting line fields
              to be appended to the required field columns, before the amount column.
              The optional columns appear in both source and target groups
              of accounting lines." %>
<%@ attribute name="extraRowFields" required="false"
              description="A comma seperated list of names of any non-standard fields
              required on this group of accounting lines for this eDoc.
              See accountingLineRow.tag for details." %>

<%@ attribute name="extraHiddenFields" required="false"
              description="A comma seperated list of names of any accounting line fields
              that should be added to the list of normally hidden fields
              for the existing (but not the new) accounting lines." %>

<%@ attribute name="debitCreditAmount" required="false"
              description="boolean whether the amount column is displayed as
              separate debit and credit columns, and the totals as the form's
              currency formatted debit and credit totals.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>
<%@ attribute name="useCurrencyFormattedTotal" required="false"
              description="boolean indicating that the form's currency formatted total
              should be displayed instead of the document's source or target total.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="includeObjectTypeCode" required="false"
              description="boolean indicating that the object type code column should be displayed.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<c:set var="sourceOrTarget" value="${isSource ? 'source' : 'target'}"/>
<c:set var="baselineSourceOrTarget" value="${isSource ? 'baselineSource' : 'baselineTarget'}"/>
<c:set var="capitalSourceOrTarget" value="${isSource ? 'Source' : 'Target'}"/>
<c:set var="dataDictionaryEntryName" value="${capitalSourceOrTarget}AccountingLine"/>
<c:set var="totalName" value="currencyFormatted${capitalSourceOrTarget}Total"/>
<c:set var="accountingLineAttributes" value="${DataDictionary[dataDictionaryEntryName].attributes}" />
<c:set var="hasActionsColumn" value="${empty editingMode['viewOnly']}"/>
<c:set var="totalColumnWidth" value="${rightColumnCount + (hasActionsColumn ? 1 : 2)}" />

<c:set var="displayHidden" value="false" />

<c:set var="errorPattern" value="${isSource ? Constants.SOURCE_ACCOUNTING_LINE_ERROR_PATTERN : Constants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN}"/>
<%-- need var titleName because the EL + operator is arithmetic only, not String concat --%>
<c:set var="titleName" value="${sourceOrTarget}AccountingLinesSectionTitle"/>
<c:set var="sectionTitle" value="${KualiForm.document[titleName]}"/>

<c:choose>
    <c:when test="${empty sectionTitle}">
        <%-- JournalVoucher has only one group of accounting lines with an empty titleName. --%>
        <c:set var="errorSectionTitle" value="this"/>
    </c:when>
    <c:otherwise>
        <c:set var="errorSectionTitle" value='"${sectionTitle}"'/>
    </c:otherwise>
</c:choose>

<kul:displayIfErrors keyMatch="${errorPattern}">
    <tr>
        <td class="error" colspan="${totalColumnWidth}">
            <kul:errors keyMatch="${errorPattern}" errorTitle='Errors found in ${errorSectionTitle} section:'/>
        </td>
    </tr>    
</kul:displayIfErrors>

<fin:accountingLineImportRow
    rightColumnCount="${rightColumnCount}"
    isSource="${isSource}"
    editingMode="${editingMode}"
    sectionTitle="${sectionTitle}"/>

<tr>
    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.chartOfAccountsCode}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.accountNumber}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.subAccountNumber}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialObjectCode}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialSubObjectCode}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.projectCode}" rowspan="2"/>
    <c:if test="${includeObjectTypeCode}">
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.objectTypeCode}" rowspan="2" forceRequired="true" />
    </c:if>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.organizationReferenceId}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.budgetYear}" rowspan="2"/>
    <c:forTokens items="${optionalFields}" delims=" ," var="currentField">
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes[currentField]}" rowspan="2"/>
    </c:forTokens>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.amount}" rowspan="${debitCreditAmount ? 1 : 2}" colspan="${debitCreditAmount ? 2 : 1}"/>

    <c:if test="${hasActionsColumn}">
        <kul:htmlAttributeHeaderCell literalLabel="Actions" rowspan="2"/>
    </c:if>
</tr>
<c:choose>
    <c:when test="${debitCreditAmount}" >
        <tr>
            <kul:htmlAttributeHeaderCell literalLabel="${ConfigProperties.label.document.journalVoucher.accountingLine.debit}"/>
            <kul:htmlAttributeHeaderCell literalLabel="${ConfigProperties.label.document.journalVoucher.accountingLine.credit}"/>
        </tr>
    </c:when>
    <c:otherwise>
        <tr></tr>
    </c:otherwise>
</c:choose>
<c:if test="${empty editingMode['viewOnly']}">
    <c:choose>
      <c:when test="${isSource}">
        <c:set var="valuesMap" value="${KualiForm.newSourceLine.valuesMap}"/>
      </c:when>
      <c:otherwise>
        <c:set var="valuesMap" value="${KualiForm.newTargetLine.valuesMap}"/>
      </c:otherwise>
    </c:choose>  
    
    <fin:accountingLineRow
        accountingLine="new${capitalSourceOrTarget}Line"
        accountingLineAttributes="${accountingLineAttributes}"
        dataCellCssClass="infoline"
        rowHeader="add:"
        actionGroup="newLine"
        actionInfix="${capitalSourceOrTarget}"
        optionalFields="${optionalFields}"
        extraRowFields="${extraRowFields}"
        extraRowLabelFontWeight="bold"
        readOnly="false"
        debitCreditAmount="${debitCreditAmount}"
        hiddenFields="postingYear,overrideCode"
        rightColumnCount="${rightColumnCount}"
        debitCellProperty="newSourceLineDebit"
        creditCellProperty="newSourceLineCredit"
        includeObjectTypeCode="${includeObjectTypeCode}"
        displayHidden="${displayHidden}"
        accountingLineValuesMap="${valuesMap}"
        />
</c:if>
<logic:iterate indexId="ctr" name="KualiForm" property="document.${sourceOrTarget}AccountingLines" id="currentLine">
    <%-- readonlyness of accountingLines depends on editingMode and user's account-list --%>
    <c:choose>
        <c:when test="${!empty editingMode['fullEntry']}">
            <c:set var="accountIsEditable" value="true" />
        </c:when>
        <c:when test="${!empty editingMode['viewOnly']||!empty editingMode['expenseSpecialEntry']}">
            <c:set var="accountIsEditable" value="false" />
        </c:when>
        <c:otherwise>
            <%-- using accountKey of baseline accountingLine, so that when the user changes to an account they can't access,
                 they'll be allowed to revert or update the line to something to which they do have access --%>
            <c:set var="baselineAccountKey">
                <bean:write name="KualiForm" property="${baselineSourceOrTarget}AccountingLine[${ctr}].accountKey" />
            </c:set>

            <c:set var="accountIsEditable" value="${!empty editableAccounts[baselineAccountKey]}" />
        </c:otherwise>
    </c:choose>

    <fin:accountingLineRow
        accountingLine="document.${sourceOrTarget}AccountingLine[${ctr}]"
        baselineAccountingLine="${baselineSourceOrTarget}AccountingLine[${ctr}]"
        accountingLineIndex="${ctr}"
        accountingLineAttributes="${accountingLineAttributes}"
        dataCellCssClass="datacell"
        rowHeader="${ctr+1}"
        actionGroup="existingLine"
        actionInfix="${capitalSourceOrTarget}"
        optionalFields="${optionalFields}"
        extraRowFields="${extraRowFields}"
        extraRowLabelFontWeight="normal"
        readOnly="${!accountIsEditable}"
        editableFields="${editableFields}"
        debitCreditAmount="${debitCreditAmount}"
        hiddenFields="postingYear,overrideCode,sequenceNumber,versionNumber,financialDocumentNumber${extraHiddenFields}"
        rightColumnCount="${rightColumnCount}"
        debitCellProperty="journalLineHelper[${ctr}].debit"
        creditCellProperty="journalLineHelper[${ctr}].credit"
        includeObjectTypeCode="${includeObjectTypeCode}"
        displayHidden="${displayHidden}"
        decorator="${sourceOrTarget}LineDecorator[${ctr}]"
        accountingLineValuesMap="${currentLine.valuesMap}"
        />
</logic:iterate>

<tr>
    <td class="total-line" colspan="${totalColumnWidth}">&nbsp;</td>
    <c:choose>
        <c:when test="${debitCreditAmount}" >
            <%-- from JournalVoucherForm --%>
            <td class="total-line"><strong>Debit Total: $${KualiForm.currencyFormattedDebitTotal}</strong></td>
            <td class="total-line"><strong>Credit Total: $${KualiForm.currencyFormattedCreditTotal}</strong></td>
        </c:when>
        <c:otherwise>
            <td  class="total-line" colspan="1">&nbsp;</td>
            <c:choose>
                <c:when test="${useCurrencyFormattedTotal}" >
                    <%-- from JournalVoucherForm --%>
                    <td  class="total-line"><strong>Total: $${KualiForm.currencyFormattedTotal}</strong></td>
                </c:when>
                <c:otherwise>
                <td class="total-line"><strong>Total: $${KualiForm[totalName]}</strong></td>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
    <td class="total-line">&nbsp;</td>
</tr>
