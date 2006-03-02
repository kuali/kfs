<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>

<%@ attribute name="accountingLine" required="true"
              description="The name in the form of the accounting line
              being edited or displayed by this row." %>
<%@ attribute name="baselineAccountingLine" required="false"
              description="The name in the form of the baseline accounting line
              from before the most recent edit of this row,
              to put in hidden fields for comparison or reversion." %>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="accountingLineIndex" required="false" description="index of this accountingLine in the corresponding form list" %>

<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell.
              This is used to distinguish the look of the add row from the already-added rows." %>
<%@ attribute name="rowHeader" required="true"
              description="The value of the header cell of this row.
              It would be 'add:' or the number of this row's accounting line within its group." %>

<%@ attribute name="actionGroup" required="true" description="The name of the group of action buttons to be displayed; valid values are newLine and existingLine." %>
<%@ attribute name="actionInfix" required="true" description="Infix used to build method names which will be invoked by the buttons in this actionGroup" %>

<%@ attribute name="readOnly" required="true" %>
<%@ attribute name="editableFields" required="false" type="java.util.Map"
              description="Map of accounting line fields which this user is allowed to edit" %>
<%@ attribute name="hiddenFields" required="true"
              description="A comma separated list of names of accounting line fields
              to be put in hidden fields on this form." %>
<%@ attribute name="rightColumnCount" required="true"
              description="4 less than the total number of columns in the
              accounting lines table.  The total depends on the number
              of optional fields and whether there is an action button column." %>

<%@ attribute name="optionalFields" required="false"
              description="A comma separated list of names of accounting line fields
              to be appended to the required field columns, before the amount column." %>

<%@ attribute name="extraRowFields" required="false"
              description="A comma seperated list of names of any non-standard fields
              required by this accounting line.  They are placed on a second row, sharing the
              same row header and action (if any). Each field includes its own label and takes up two columns.
              Note that since there are only 8 standard fields, no more than 4+(optionalFieldsCount/2)
              extra fields can be properly formatted in the table.
              This attribute requires the extraRowLabelFontWeight attribute." %>
<%@ attribute name="extraRowLabelFontWeight" required="false"
              description="The font weight for the in-cell labels on the extra row, e.g., bold or normal.
              Providing this attribute without the extraRowFields attribute has no effect.  Changing the weight
              allows the labels in the 'add' row to correspond to the weight of the column headers." %>

<%@ attribute name="debitCreditAmount" required="false"
              description="boolean whether the amount column is displayed as
              separate debit and credit columns.
              If true, debitCellProperty and creditCellProperty are required.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>
<%@ attribute name="debitCellProperty" required="false"
              description="the name of the form property to display or edit in the debit amount column"%>
<%@ attribute name="creditCellProperty" required="false"
              description="the name of the form property to display or edit in the credit amount column"%>

<%@ attribute name="includeObjectTypeCode" required="false"
              description="boolean indicating that the object type code column should be displayed.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="displayHidden" required="false" description="display values of hidden fields" %>
<%@ attribute name="decorator" required="false" description="propertyName of the AccountingLineDecorator associated with this accountingLine" %>

<%@ attribute name="accountingLineValuesMap" required="true" type="java.util.Map" 
              description="map of the accounting line primitive fields and values" %>

<c:set var="rowCount" value="${empty extraRowFields ? 1 : 2}"/>

<tr>
<kul:htmlAttributeHeaderCell literalLabel="${rowHeader}" scope="row" rowspan="${rowCount}">
    <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
    <c:forTokens var="hiddenField" items="${hiddenFields}" delims=",">
        <fin:hiddenAccountingLineField
            accountingLine="${accountingLine}"
            hiddenField="${hiddenField}"
            displayHidden="${displayHidden}"
            />
        <c:if test="${!empty baselineAccountingLine}">
            <fin:hiddenAccountingLineField
                accountingLine="${baselineAccountingLine}"
                isBaseline="true"
                hiddenField="${hiddenField}"
                displayHidden="${displayHidden}"
                />
        </c:if>
    </c:forTokens>
</kul:htmlAttributeHeaderCell>

<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="chartOfAccountsCode"
    detailFunction="loadChartInfo"
    detailField="chart.finChartOfAccountDescription"
    attributes="${accountingLineAttributes}"
    lookup="false"
    inquiry="true"
    boClassName="Chart"
    conversionField="chartOfAccountsCode"
    readOnly="${readOnly&&(empty editableFields['chartOfAccountsCode'])}"
    displayHidden="${displayHidden}"
    accountingLineValuesMap="${accountingLineValuesMap}"
    />

<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="accountNumber"
    detailFunction="loadAccountInfo"
    detailField="account.accountName"
    attributes="${accountingLineAttributes}"
    lookup="true"
    inquiry="true"
    boClassName="Account"
    briefLookupParameters="chartOfAccountsCode:chartOfAccountsCode"
    conversionField="accountNumber"
    readOnly="${readOnly&&(empty editableFields['accountNumber'])}"
    displayHidden="${displayHidden}"
    overrideField="accountExpiredOverride"
    inquiryValueKeys="chartOfAccountsCode"
    accountingLineValuesMap="${accountingLineValuesMap}"
    />
    
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="subAccountNumber"
    detailFunction="loadSubAccountInfo"
    detailField="subAccount.subAccountName"
    attributes="${accountingLineAttributes}"
    lookup="true"
    inquiry="true"
    boClassName="SubAccount"
    briefLookupParameters="chartOfAccountsCode:chartOfAccountsCode,accountNumber:accountNumber"
    conversionField="subAccountNumber"
    readOnly="${readOnly&&(empty editableFields['subAccountNumber'])}"
    displayHidden="${displayHidden}"
    inquiryValueKeys="chartOfAccountsCode,accountNumber"
    accountingLineValuesMap="${accountingLineValuesMap}"
    />
    
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="financialObjectCode"
    detailFunction="loadObjectInfo"
    detailFunctionExtraParam="'${KualiForm.document.postingYear}', "
    detailField="objectCode.financialObjectCodeName"
    attributes="${accountingLineAttributes}"
    lookup="true"
    inquiry="true"
    boClassName="ObjectCode"
    briefLookupParameters="chartOfAccountsCode:chartOfAccountsCode"
    conversionField="financialObjectCode"
    readOnly="${readOnly&&(empty editableFields['financialObjectCode'])}"
    displayHidden="${displayHidden}"
    overrideField="objectBudgetOverride"
    inquiryValueKeys="chartOfAccountsCode"
    accountingLineValuesMap="${accountingLineValuesMap}"
    inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.postingYear}"
    />
    
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="financialSubObjectCode"
    detailFunction="loadSubObjectInfo"
    detailFunctionExtraParam="'${KualiForm.document.postingYear}', "
    detailField="subObjectCode.financialSubObjectCodeName"
    attributes="${accountingLineAttributes}"
    lookup="true"
    inquiry="true"
    boClassName="SubObjCd"
    briefLookupParameters="chartOfAccountsCode:chartOfAccountsCode,accountNumber:accountNumber,financialObjectCode:financialObjectCode"
    conversionField="financialSubObjectCode"
    readOnly="${readOnly&&(empty editableFields['financialSubObjectCode'])}"
    displayHidden="${displayHidden}"
    inquiryValueKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
    accountingLineValuesMap="${accountingLineValuesMap}"
    inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.postingYear}"
    />
  
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="projectCode"
    detailFunction="loadProjectInfo"
    detailField="project.projectDescription"
    attributes="${accountingLineAttributes}"
    lookup="true"
    inquiry="true"
    boClassName="ProjectCode"
    briefLookupParameters=""
    conversionField="code"
    readOnly="${readOnly&&(empty editableFields['projectCode'])}"
    displayHidden="${displayHidden}"
    inquiryValueKeys="chartOfAccountsCode"
    accountingLineValuesMap="${accountingLineValuesMap}"
    />
    
<c:if test="${includeObjectTypeCode}">
    <fin:accountingLineDataCell
        dataCellCssClass="${dataCellCssClass}"
        cellAlign="center"
        accountingLine="${accountingLine}"
        baselineAccountingLine="${baselineAccountingLine}"
        field="objectTypeCode"
        detailFunction="loadObjectTypeInfo"
        detailField="objectType.name"
        attributes="${accountingLineAttributes}"
        lookup="true"
        inquiry="true"
        boClassName="ObjectType"
        briefLookupParameters=""
        conversionField="code"
        readOnly="${readOnly}"
        displayHidden="${displayHidden}"
        accountingLineValuesMap="${accountingLineValuesMap}"
        />
</c:if>
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="organizationReferenceId"
    attributes="${accountingLineAttributes}"
    readOnly="${readOnly}"
    displayHidden="${displayHidden}"
    />
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    baselineAccountingLine="${baselineAccountingLine}"
    field="budgetYear"
    attributes="${accountingLineAttributes}"
    readOnly="${readOnly}"
    displayHidden="${displayHidden}"
    />
<c:forTokens items="${optionalFields}" delims=" ," var="currentField">
    <fin:accountingLineDataCell
        dataCellCssClass="${dataCellCssClass}"
        cellAlign="right"
        accountingLine="${accountingLine}"
        baselineAccountingLine="${baselineAccountingLine}"
        field="${currentField}"
        attributes="${accountingLineAttributes}"
        readOnly="${readOnly}"
        displayHidden="${displayHidden}"
        />
</c:forTokens>
<c:choose>
    <c:when test="${!debitCreditAmount}" >
        <fin:accountingLineDataCell
            dataCellCssClass="${dataCellCssClass}"
            cellAlign="right"
            accountingLine="${accountingLine}"
            baselineAccountingLine="${baselineAccountingLine}"
            field="amount"
            attributes="${accountingLineAttributes}"
            readOnly="${readOnly&&(empty editableFields['amount'])}"
            displayHidden="${displayHidden}"
            />
    </c:when>
    <c:otherwise>
        <fin:accountingLineDataCell
            dataCellCssClass="${dataCellCssClass}"
            cellAlign="right"
            cellProperty="${debitCellProperty}"
            attributes="${accountingLineAttributes}"
            field="amount"
            readOnly="${readOnly&&(empty editableFields['amount'])}"
            />
        <fin:accountingLineDataCell
            dataCellCssClass="${dataCellCssClass}"
            cellAlign="right"
            cellProperty="${creditCellProperty}"
            attributes="${accountingLineAttributes}"
            field="amount"
            readOnly="${readOnly&&(empty editableFields['amount'])}"
            />
    </c:otherwise>
</c:choose>
<c:if test="${!readOnly}">
    <c:choose>
        <c:when test="${actionGroup == 'newLine' }" >
            <c:set var="insertMethod" value="insert${actionInfix}Line" />

            <td rowspan="${rowCount}" class="${dataCellCssClass}" nowrap><div align="center">
                <html:image property="methodToCall.${insertMethod}" src="images/tinybutton-add1.gif" alt="insert" styleClass="tinybutton"/>
                <fin:accountingLineDataCellDetail/></div>
            </td>
        </c:when>

        <c:when test="${actionGroup == 'existingLine'}" >
            <c:set var="revertible">
                <bean:write name="KualiForm" property="${decorator}.revertible" />
            </c:set>
            <c:set var="deleteMethod" value="delete${actionInfix}Line.line${accountingLineIndex}" />
            <c:set var="revertMethod" value="revert${actionInfix}Line.line${accountingLineIndex}" />
            <c:set var="balanceInquiryMethod" value="performBalanceInquiryFor${actionInfix}Line.line${accountingLineIndex}" />

            <td rowspan="${rowCount}" class="${dataCellCssClass}" nowrap>
                <div align="center">
                <%-- persist accountingLineDecorator --%>
		        <html:hidden name="KualiForm" property="${decorator}.revertible" />

		        <html:image property="methodToCall.${deleteMethod}" src="images/tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/>
        		<c:if test="${revertible}">
            		<br>
            		<html:image property="methodToCall.${revertMethod}" src="images/tinybutton-revert1.gif" alt="revert" styleClass="tinybutton"/>
        		</c:if>
        		<br>
        		<html:image property="methodToCall.${balanceInquiryMethod}" src="images/tinybutton-balinquiry.gif" alt="balance inquiry" styleClass="tinybutton" />
                </div>
            </td>
        </c:when>
    </c:choose>
</c:if>
</tr>
<c:if test="${!empty extraRowFields}">
    <tr>
        <c:set var="extraRowFieldCount" value="0"/>
        <c:forTokens items="${extraRowFields}" delims="," var="extraField">
            <c:set var="extraRowFieldCount" value="${extraRowFieldCount + 1}"/>
            <fin:accountingLineDataCell
                dataCellCssClass="${dataCellCssClass}"
                cellAlign="center"
                accountingLine="${accountingLine}"
                baselineAccountingLine="${baselineAccountingLine}"
                field="${extraField}"
                attributes="${accountingLineAttributes}"
                labelFontWeight="${extraRowLabelFontWeight}"
                readOnly="${readOnly}"
                displayHidden="${displayHidden}"
                />
        </c:forTokens>
        <td class="${dataCellCssClass}"
            colspan="${rightColumnCount + 2 - (2 * extraRowFieldCount) - (readOnly ? 1 : 0)}">&nbsp;</td>
    </tr>
</c:if>
