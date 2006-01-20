<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>

<%@ attribute name="accountingLine" required="true"
              description="The name in the form of the accounting line
              being edited or displayed by this row." %>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell.
              This is used to distinguish the look of the add row from the already-added rows." %>
<%@ attribute name="rowHeader" required="true"
              description="The value of the header cell of this row.
              It would be 'add:' or the number of this row's accounting line within its group." %>
<%@ attribute name="actionMethodToCall" required="true"
              description="The name of the method to request of the server
              when this row's action button is pressed, e.g., insert (i.e., add) or delete." %>
<%@ attribute name="actionImageSrc" required="true"
              description="The source file of the image to display for the action button." %>
<%@ attribute name="actionAlt" required="true"
              description="The alternate text of the action button." %>
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

<c:set var="rowCount" value="${empty extraRowFields ? 1 : 2}"/>

<c:forTokens var="hiddenField" items="${hiddenFields}" delims=",">
    <html:hidden property="${accountingLine}.${hiddenField}"/>
</c:forTokens>
<tr>
<th scope="row" rowspan="${rowCount}" class="bord-l-b"><div align="center">${rowHeader}</div></th>
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="chartOfAccountsCode"
    detailFunction="loadChartInfo"
    detailField="chart.finChartOfAccountDescription"
    attributes="${accountingLineAttributes}"
    readOnly="${readOnly&&(empty editableFields['chartOfAccountsCode'])}"
    />
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="accountNumber"
    detailFunction="loadAccountInfo"
    detailField="account.accountName"
    attributes="${accountingLineAttributes}"
    boClassName="Account"
    briefLookupParameters="chartOfAccountsCode:chartOfAccounts.chartOfAccountsCode"
    conversionField="accountNumber"
    readOnly="${readOnly&&(empty editableFields['accountNumber'])}"
    />
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="subAccountNumber"
    detailFunction="loadSubAccountInfo"
    detailField="subAccount.subAccountName"
    attributes="${accountingLineAttributes}"
    boClassName="SubAccount"
    briefLookupParameters="chartOfAccountsCode:chartOfAccounts.chartOfAccountsCode,accountNumber:account.accountNumber"
    conversionField="subAccountNumber"
    readOnly="${readOnly&&(empty editableFields['subAccountNumber'])}"
    />
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="financialObjectCode"
    detailFunction="loadObjectInfo"
    detailFunctionExtraParam="'${KualiForm.document.postingYear}', "
    detailField="objectCode.financialObjectCodeName"
    attributes="${accountingLineAttributes}"
    boClassName="ObjectCode"
    briefLookupParameters="chartOfAccountsCode:chartOfAccounts.chartOfAccountsCode"
    conversionField="financialObjectCode"
    readOnly="${readOnly&&(empty editableFields['financialObjectCode'])}"
    />
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="financialSubObjectCode"
    detailFunction="loadSubObjectInfo"
    detailFunctionExtraParam="'${KualiForm.document.postingYear}', "
    detailField="subObjectCode.financialSubObjectCodeName"
    attributes="${accountingLineAttributes}"
    boClassName="SubObjCd"
    briefLookupParameters="chartOfAccountsCode:chartOfAccounts.chartOfAccountsCode,accountNumber:account.accountNumber,financialObjectCode:financialObject.financialObjectCode"
    conversionField="financialSubObjectCode"
    readOnly="${readOnly&&(empty editableFields['financialSubObjectCode'])}"
    />
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="projectCode"
    detailFunction="loadProjectInfo"
    detailField="project.projectDescription"
    attributes="${accountingLineAttributes}"
    boClassName="ProjectCode"
    briefLookupParameters=""
    conversionField="code"
    readOnly="${readOnly&&(empty editableFields['projectCode'])}"
    />
<c:if test="${includeObjectTypeCode}">
    <fin:accountingLineDataCell
        dataCellCssClass="${dataCellCssClass}"
        cellAlign="center"
        accountingLine="${accountingLine}"
        field="objectTypeCode"
        detailFunction="loadObjectTypeInfo"
        detailField="objectType.name"
        attributes="${accountingLineAttributes}"
        boClassName="ObjectType"
        briefLookupParameters=""
        conversionField="code"
        readOnly="${readOnly}"
        />
</c:if>
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="organizationReferenceId"
    attributes="${accountingLineAttributes}"
    readOnly="${readOnly}"
    />
<fin:accountingLineDataCell
    dataCellCssClass="${dataCellCssClass}"
    cellAlign="center"
    accountingLine="${accountingLine}"
    field="budgetYear"
    attributes="${accountingLineAttributes}"
    readOnly="${readOnly}"
    />
<c:forTokens items="${optionalFields}" delims=" ," var="currentField">
    <fin:accountingLineDataCell
        dataCellCssClass="${dataCellCssClass}"
        cellAlign="right"
        accountingLine="${accountingLine}"
        field="${currentField}"
        attributes="${accountingLineAttributes}"
        readOnly="${readOnly}"
        />
</c:forTokens>
<c:choose>
    <c:when test="${!debitCreditAmount}" >
        <fin:accountingLineDataCell
            dataCellCssClass="${dataCellCssClass}"
            cellAlign="right"
            accountingLine="${accountingLine}"
            field="amount"
            attributes="${accountingLineAttributes}"
            readOnly="${readOnly&&(empty editableFields['amount'])}"
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
    <td rowspan="${rowCount}" class="${dataCellCssClass}" nowrap><div align="center">
        <html:image property="methodToCall.${actionMethodToCall}" src="${actionImageSrc}"
                    alt="${actionAlt}" styleClass="tinybutton"/>
        <fin:accountingLineDataCellDetail/></div>
    </td>
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
                field="${extraField}"
                attributes="${accountingLineAttributes}"
                labelFontWeight="${extraRowLabelFontWeight}"
                readOnly="${readOnly}"
                />
        </c:forTokens>
        <td class="${dataCellCssClass}"
            colspan="${rightColumnCount + 2 - (2 * extraRowFieldCount) - (readOnly ? 1 : 0)}">&nbsp;</td>
    </tr>
</c:if>
