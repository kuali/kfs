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
<%@ tag description="A source or target group of accounting line rows.
The first row is for adding a new accounting line (if not read-only).
It's followed by 0 or more rows for the accounting lines that have already been added." %>

<%@ include file="/jsp/core/tldHeader.jsp"%>

<%@ attribute name="isSource" required="true"
              description="Boolean whether this group is of source or target lines." %>
<%@ attribute name="columnCountUntilAmount" required="true"
              description="the number of columns to the left of the amount column(s) in the
              accounting lines table.  This depends on the number
              of optional fields and whether there is an object type column." %>
<%@ attribute name="columnCount" required="true"
              description="the total number of columns in the accounting lines table." %>
<%@ attribute name="editingMode" required="true" type="java.util.Map"
              description='magic values like "viewOnly", "fullEntry", "unviewable", etc 
              which affect which rows and fields of the accounting lines are displayed or editable.
              These values might come from the AuthorizationConstants class.' %>
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

<%@ attribute name="currentBaseAmount" required="false"
              description="boolean whether the amount column is displayed as
              separate current and base columns, and the totals as the form's
              currency formatted current and base totals.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="useCurrencyFormattedTotal" required="false"
              description="boolean indicating that the form's currency formatted total
              should be displayed instead of the document's source or target total.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="includeObjectTypeCode" required="false"
              description="boolean indicating that the object type code column should be displayed.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="displayMonthlyAmounts" required="false"
              description="A boolean whether the monthy amounts table is displayed
              below each accounting line (needed for budget adjustment document).
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>
              
<%@ attribute name="accountingLineAttributes" required="false" type="java.util.Map"
              description="A parameter to specify an data dictionary entry for a sub-classed accounting line." %>            

<%@ attribute name="forcedReadOnlyFields" required="false" type="java.util.Map"
              description="map containing accounting line field names that should be marked as read only." %>
<c:set var="sourceOrTarget" value="${isSource ? 'source' : 'target'}"/>
<c:set var="baselineSourceOrTarget" value="${isSource ? 'baselineSource' : 'baselineTarget'}"/>
<c:set var="capitalSourceOrTarget" value="${isSource ? 'Source' : 'Target'}"/>
<c:set var="dataDictionaryEntryName" value="${capitalSourceOrTarget}AccountingLine"/>
<c:set var="totalName" value="currencyFormatted${capitalSourceOrTarget}Total"/>
<c:if test="${empty accountingLineAttributes}">
    <c:if test="${isSource}">
        <c:set var="accountingLineAttributes" value="${DataDictionary[KualiForm.document.sourceAccountingLineEntryName].attributes}" />
    </c:if>
    <c:if test="${!isSource}">
            <c:set var="accountingLineAttributes" value="${DataDictionary[KualiForm.document.targetAccountingLineEntryName].attributes}" />
    </c:if>

</c:if>  
<c:set var="hasActionsColumn" value="${empty editingMode['viewOnly']}"/>

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
        <td class="error" colspan="${columnCount}">
            <kul:errors keyMatch="${errorPattern}" errorTitle='Errors found in ${errorSectionTitle} section:'/>
        </td>
    </tr>    
</kul:displayIfErrors>

<fin:accountingLineImportRow
    columnCount="${columnCount}"
    isSource="${isSource}"
    editingMode="${editingMode}"
    sectionTitle="${sectionTitle}"/>

<tr>
    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" rowspan="2" anchor="accounting${capitalSourceOrTarget}Anchor"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.chartOfAccountsCode}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.accountNumber}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.subAccountNumber}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialObjectCode}" hideRequiredAsterisk="${ !(empty forcedReadOnlyFields[accountingLineAttributes.financialObjectCode.name])}" rowspan="2"/>
    <c:if test="${includeObjectTypeCode}">
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.objectTypeCode}" rowspan="2" forceRequired="true" />
    </c:if>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialSubObjectCode}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.projectCode}" rowspan="2"/>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.organizationReferenceId}" rowspan="2"/>
    <c:forTokens items="${optionalFields}" delims=" ," var="currentField">
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes[currentField]}" rowspan="2"/>
    </c:forTokens>
    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.amount}" rowspan="${debitCreditAmount || currentBaseAmount ? 1 : 2}" colspan="${debitCreditAmount || currentBaseAmount ? 2 : 1}"/>

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
    <c:when test="${currentBaseAmount}" >
        <tr>
            <kul:htmlAttributeHeaderCell literalLabel="Current"/>
            <kul:htmlAttributeHeaderCell literalLabel="Base"/>
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
        rowHeader="add"
        actionGroup="newLine"
        actionInfix="${capitalSourceOrTarget}"
        optionalFields="${optionalFields}"
        extraRowFields="${extraRowFields}"
        extraRowLabelFontWeight="bold"
        readOnly="false"
        debitCreditAmount="${debitCreditAmount}"
        currentBaseAmount="${currentBaseAmount}"
        hiddenFields="postingYear,overrideCode"
        columnCountUntilAmount="${columnCountUntilAmount}"
        debitCellProperty="newSourceLineDebit"
        creditCellProperty="newSourceLineCredit"
        currentCellProperty="new${capitalSourceOrTarget}Line.currentBudgetAdjustmentAmount"
        baseCellProperty="new${capitalSourceOrTarget}Line.baseBudgetAdjustmentAmount"
        includeObjectTypeCode="${includeObjectTypeCode}"
        displayHidden="${displayHidden}"
        accountingLineValuesMap="${valuesMap}"
        forcedReadOnlyFields="${forcedReadOnlyFields}"
        />

    <c:if test="${displayMonthlyAmounts}">
        <fin:budgetAdjustmentMonthlyAmountLines 
            isSource="${isSource}" 
            accountingLine="new${capitalSourceOrTarget}Line"/>
    </c:if>

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
        currentBaseAmount="${currentBaseAmount}"
        hiddenFields="postingYear,overrideCode,sequenceNumber,versionNumber,documentNumber${extraHiddenFields}"
        columnCountUntilAmount="${columnCountUntilAmount}"
        debitCellProperty="voucherLineHelper[${ctr}].debit"
        creditCellProperty="voucherLineHelper[${ctr}].credit"
        currentCellProperty="document.${sourceOrTarget}AccountingLine[${ctr}].currentBudgetAdjustmentAmount"
        baseCellProperty="document.${sourceOrTarget}AccountingLine[${ctr}].baseBudgetAdjustmentAmount"
        includeObjectTypeCode="${includeObjectTypeCode}"
        displayHidden="${displayHidden}"
        decorator="${sourceOrTarget}LineDecorator[${ctr}]"
        accountingLineValuesMap="${currentLine.valuesMap}"
        forcedReadOnlyFields="${forcedReadOnlyFields}"
        />

    <c:if test="${displayMonthlyAmounts}">
        <fin:budgetAdjustmentMonthlyAmountLines 
            isSource="${isSource}" 
            accountingLine="document.${sourceOrTarget}AccountingLine[${ctr}]"
            baselineAccountingLine="${baselineSourceOrTarget}AccountingLine[${ctr}]"/>
    </c:if>

</logic:iterate>

<tr>
    <td class="total-line" colspan="${columnCountUntilAmount}">&nbsp;</td>
    <c:choose>
        <c:when test="${debitCreditAmount}" >
            <%-- from JournalVoucherForm --%>
            <td class="total-line" style="border-left: 0px;"><strong>Debit Total: $${KualiForm.currencyFormattedDebitTotal}</strong></td>
            <td class="total-line" style="border-left: 0px;"><strong>Credit Total: $${KualiForm.currencyFormattedCreditTotal}</strong></td>
        </c:when>
        <c:when test="${currentBaseAmount}" >
            <!-- KULEDOCS-1631: Removed Current & Base total as requested. -->
            <c:if test="${isSource}">
              <td class="total-line" style="border-left: 0px;">&nbsp;<!-- <strong>Current Total: $${KualiForm.document.currencyFormattedSourceCurrentBudgetTotal}</strong> --></td>
              <td class="total-line" style="border-left: 0px;">&nbsp;<!-- <strong>Base Total: $${KualiForm.document.currencyFormattedSourceBaseBudgetTotal}</strong> --></td>
            </c:if>
            <c:if test="${!isSource}">
              <td class="total-line" style="border-left: 0px;">&nbsp;<!-- <strong>Current Total: $${KualiForm.document.currencyFormattedTargetCurrentBudgetTotal}</strong> --></td>
              <td class="total-line" style="border-left: 0px;">&nbsp;<!-- <strong>Base Total: $${KualiForm.document.currencyFormattedTargetBaseBudgetTotal}</strong> --></td>
            </c:if>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${useCurrencyFormattedTotal}" >
                    <%-- from JournalVoucherForm --%>
                    <td class="total-line" style="border-left: 0px;"><strong>Total: $${KualiForm.currencyFormattedTotal}</strong></td>
                </c:when>
                <c:otherwise>
                <td class="total-line" style="border-left: 0px;"><strong>Total: $${KualiForm[totalName]}</strong></td>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
    <td class="total-line" style="border-left: 0px;">&nbsp;</td>
</tr>