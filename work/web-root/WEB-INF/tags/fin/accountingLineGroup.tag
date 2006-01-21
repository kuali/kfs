<%@ taglib prefix="c" uri="/tlds/c.tld" %>
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
<c:set var="capitalSourceOrTarget" value="${isSource ? 'Source' : 'Target'}"/>
<c:set var="dataDictionaryEntryName" value="${capitalSourceOrTarget}AccountingLine"/>
<c:set var="totalName" value="${sourceOrTarget}Total"/>
<c:set var="accountingLineAttributes" value="${DataDictionary[dataDictionaryEntryName].attributes}" />
<c:set var="hasActionsColumn" value="${empty editingMode['viewOnly']}"/>

<fin:accountingLineImportRow rightColumnCount="${rightColumnCount}" isSource="${isSource}" editingMode="${editingMode}"/>

<tr>
    <th class="bord-l-b">&nbsp;</th>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.chartOfAccountsCode}" useShortLabel="true" /></th>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.accountNumber}" useShortLabel="true" /></th>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.subAccountNumber}" useShortLabel="true" /></th>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.financialObjectCode}" useShortLabel="true" /></th>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.financialSubObjectCode}" useShortLabel="true" /></th>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.projectCode}" useShortLabel="true" /></th>
    <c:if test="${includeObjectTypeCode}">
        <th class="bord-l-b"><font color="red">*</font>&nbsp;<kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.objectTypeCode}" useShortLabel="true" /></th>
    </c:if>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.organizationReferenceId}" useShortLabel="true" /></th>
    <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.budgetYear}" useShortLabel="true" /></th>
    <c:forTokens items="${optionalFields}" delims=" ," var="currentField">
        <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes[currentField]}" useShortLabel="true" /></th>
    </c:forTokens>
    <c:choose>
        <c:when test="${!debitCreditAmount}" >
            <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${accountingLineAttributes.amount}" useShortLabel="true" /></th>
        </c:when>
        <c:otherwise>
            <th class="bord-l-b">${ConfigProperties.label.document.journalVoucher.accountingLine.debit}</th>
            <th class="bord-l-b">${ConfigProperties.label.document.journalVoucher.accountingLine.credit}</th>
        </c:otherwise>
    </c:choose>

    <c:if test="${hasActionsColumn}">
        <th class="bord-l-b">Actions</th>
    </c:if>
</tr>
<c:if test="${empty editingMode['viewOnly']}">
    <fin:accountingLineRow
        accountingLine="new${capitalSourceOrTarget}Line"
        accountingLineAttributes="${accountingLineAttributes}"
        dataCellCssClass="infoline"
        rowHeader="add:"
        actionMethodToCall="insert${capitalSourceOrTarget}Line"
        actionAlt="insert"
        actionImageSrc="images/tinybutton-add1.gif"
        optionalFields="${optionalFields}"
        extraRowFields="${extraRowFields}"
        extraRowLabelFontWeight="bold"
        readOnly="false"
        debitCreditAmount="${debitCreditAmount}"
        hiddenFields="postingYear"
        rightColumnCount="${rightColumnCount}"
        debitCellProperty="newSourceLineDebit"
        creditCellProperty="newSourceLineCredit"
        includeObjectTypeCode="${includeObjectTypeCode}"
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
            <c:set var="accountIsEditable" value="${!empty editableAccounts[currentLine.accountKey]}" />
        </c:otherwise>
    </c:choose>

    <fin:accountingLineRow
        accountingLine="document.${sourceOrTarget}AccountingLine[${ctr}]"
        accountingLineAttributes="${accountingLineAttributes}"
        dataCellCssClass="datacell"
        rowHeader="${ctr+1}"
        actionMethodToCall="delete${capitalSourceOrTarget}Line.line${ctr}"
        actionAlt="delete"
        actionImageSrc="images/tinybutton-delete1.gif"
        optionalFields="${optionalFields}"
        extraRowFields="${extraRowFields}"
        extraRowLabelFontWeight="normal"
        readOnly="${!accountIsEditable}"
        editableFields="${editableFields}"
        debitCreditAmount="${debitCreditAmount}"
        hiddenFields="postingYear,sequenceNumber,versionNumber,financialDocumentNumber${extraHiddenFields}"
        rightColumnCount="${rightColumnCount}"
        debitCellProperty="journalLineHelper[${ctr}].debit"
        creditCellProperty="journalLineHelper[${ctr}].credit"
        includeObjectTypeCode="${includeObjectTypeCode}"
        />
</logic:iterate>

<%-- hidden accountingLines for comparison during updates --%>
<c:set var="baselineSourceOrTarget" value="${isSource ? 'baselineSource' : 'baselineTarget'}"/>
<logic:iterate indexId="ctr" name="KualiForm" property="${baselineSourceOrTarget}AccountingLines" id="currentLine">
    <fin:hiddenAccountingLineRow accountingLine="${baselineSourceOrTarget}AccountingLine[${ctr}]" optionalFields="${optionalFields}" hiddenFields="postingYear,sequenceNumber,versionNumber,financialDocumentNumber${extraHiddenFields}" includeObjectTypeCode="${includeObjectTypeCode}" />
</logic:iterate>

<tr>
    <td class="total-line" colspan="${rightColumnCount + (hasActionsColumn ? 1 : 2)}">&nbsp;</td>
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
    <td class="total-line"><strong>Total: $${KualiForm.document[totalName]}</strong></td>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
    <td class="total-line">&nbsp;</td>
</tr>
