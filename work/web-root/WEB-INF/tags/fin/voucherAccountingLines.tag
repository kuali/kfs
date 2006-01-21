<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>

<%@ attribute name="optionalFields" required="false"
              description="A comma separated list of names of accounting line fields
              to be appended to the required field columns, before the amount column.
              The optional columns appear in both source and target groups
              of accounting lines." %>
<%@ attribute name="editingMode" required="false" type="java.util.Map"%>
<%@ attribute name="editableAccounts" required="true" type="java.util.Map"
              description="Map of Accounts which this user is allowed to edit" %>

<c:set var="debitCreditAmount" value="${KualiForm.selectedBalanceType.financialOffsetGenerationIndicator}" />

<c:set var="extraHiddenFields" value=",balanceTypeCode,debitCreditCode,encumbranceUpdateCode"/>
<c:if test="${debitCreditAmount}">
    <c:set var="extraHiddenFields" value="${extraHiddenFields},amount"/>
</c:if>
<c:set var="externalEncumbranceFields" value="referenceOriginCode,referenceNumber,referenceTypeCode"/>
<c:choose>
    <c:when test="${KualiForm.selectedBalanceType.code==Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE}">
        <c:set var="extraSourceRowFields" value="${externalEncumbranceFields}"/>
    </c:when>
    <c:otherwise>
        <c:set var="extraHiddenFields" value="${extraHiddenFields},${externalEncumbranceFields}"/>
    </c:otherwise>
</c:choose>

<fin:accountingLines
    editingMode="${editingMode}"
    editableAccounts="${editableAccounts}"
    optionalFields="${optionalFields}"
    sourceAccountingLinesOnly="true"
    extraSourceRowFields="${extraSourceRowFields}"
    useCurrencyFormattedTotal="true"
    includeObjectTypeCode="true"
    debitCreditAmount="${debitCreditAmount}"
    extraHiddenFields="${extraHiddenFields}"
    />
