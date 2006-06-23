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
<%@ attribute name="displayExternalEncumbranceFields" 
       description="A flag used to determine if External Encumbrance Fields should be displayed" 
              type="java.lang.Boolean" required="false"%>
<%@ attribute name="isDebitCreditAmount" 
       description="A flag used to determine if debit/credit fields should be displayed" 
              type="java.lang.Boolean" required="false"%>
<%@ attribute name="includeObjectTypeCode" required="false"
              description="boolean indicating that the object type code column should be displayed.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<c:set var="debitCreditAmount" value="${isDebitCreditAmount}" />

<c:set var="extraHiddenFields" value=",balanceTypeCode,debitCreditCode,encumbranceUpdateCode"/>
<c:if test="${debitCreditAmount}">
    <c:set var="extraHiddenFields" value="${extraHiddenFields},amount"/>
</c:if>
<c:set var="externalEncumbranceFields" value="referenceOriginCode,referenceTypeCode,referenceNumber"/>
<c:choose>
    <c:when test="${displayExternalEncumbranceFields}">
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
    includeObjectTypeCode="${includeObjectTypeCode}"
    debitCreditAmount="${debitCreditAmount}"
    extraHiddenFields="${extraHiddenFields}"
    />
