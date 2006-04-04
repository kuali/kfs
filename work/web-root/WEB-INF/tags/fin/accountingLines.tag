<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>

<%@ attribute name="optionalFields" required="false"
              description="A comma separated list of names of accounting line fields
              to be appended to the required field columns, before the amount column.
              The optional columns appear in both source and target groups
              of accounting lines." %>
<%@ attribute name="sourceAccountingLinesOnly" required="false"
              description="A boolean controling whether the group of
              target accounting lines will be generated.
              Null or empty is the same as false." %>
<%@ attribute name="extraSourceRowFields" required="false"
              description="A comma seperated list of names of any non-standard fields
              required on the source accounting lines of this eDoc.
              See accountingLineRow.tag for details." %>
<%@ attribute name="extraTargetRowFields" required="false"
              description="A comma seperated list of names of any non-standard fields
              required on the target accounting lines of this eDoc.
              See accountingLineRow.tag for details." %>
<%@ attribute name="editingMode" required="false" type="java.util.Map"%>
<%@ attribute name="editableAccounts" required="true" type="java.util.Map"
              description="Map of Accounts which this user is allowed to edit" %>
<%@ attribute name="editableFields" required="false" type="java.util.Map"
              description="Map of accounting line fields which this user is allowed to edit" %>
<%@ attribute name="useCurrencyFormattedTotal" required="false"
              description="boolean indicating that the form's currency formatted total
              should be displayed instead of the document's source or target total.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="includeObjectTypeCode" required="false"
              description="boolean indicating that the object type code column should be displayed.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="debitCreditAmount" required="false"
              description="boolean whether the amount column is displayed as
              separate debit and credit columns, and the totals as the form's
              currency formatted debit and credit totals.
              As with all boolean tag attributes, if it is not provided, it defaults to false." %>

<%@ attribute name="extraHiddenFields" required="false"
              description="A comma seperated list of names of any accounting line fields
              that should be added to the list of normally hidden fields
              for the existing (but not the new) accounting lines." %>

<c:forEach items="${editableAccounts}" var="account">
  <html:hidden property="editableAccounts(${account.key})" value="${account.key}"/>
</c:forEach>

<c:forEach items="${editableFields}" var="field">
  <html:hidden property="accountingLineEditableFields(${field.key})"/>
</c:forEach>

<c:set var="optionalFieldCount" value="${empty optionalFields ? 0 : fn:length(fn:split(optionalFields, ' ,'))}"/>
<c:set var="columnCountUntilAmount" value="${9
                                        + (includeObjectTypeCode ? 1 : 0)
                                        + optionalFieldCount}" />
<%-- add extra columns count for the "Action" button and/or dual amounts --%>
<c:set var="columnCount" value="${columnCountUntilAmount
                                        + (debitCreditAmount ? 2 : 1)
                                        + (empty editingMode['viewOnly'] ? 1 : 0)}" />

<kul:tab tabTitle="Accounting Lines" defaultOpen="true"
         tabErrorKey="${Constants.ACCOUNTING_LINE_ERRORS}">
  <div class="tab-container" align=center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
      <fin:subheadingWithDetailToggleRow
          columnCount="${columnCount}"
          subheading="Accounting Lines"/>
      <fin:accountingLineGroup
          isSource="true"
          columnCountUntilAmount="${columnCountUntilAmount}"
          columnCount="${columnCount}"
          optionalFields="${optionalFields}"
          extraRowFields="${extraSourceRowFields}"
          editingMode="${editingMode}"
          editableAccounts="${editableAccounts}"
          editableFields="${editableFields}"
          debitCreditAmount="${debitCreditAmount}"
          extraHiddenFields="${extraHiddenFields}"
          useCurrencyFormattedTotal="${useCurrencyFormattedTotal}"
          includeObjectTypeCode="${includeObjectTypeCode}"
          />
      <c:if test="${!sourceAccountingLinesOnly}">
        <fin:accountingLineGroup
            isSource="false"
            columnCountUntilAmount="${columnCountUntilAmount}"
            columnCount="${columnCount}"
            optionalFields="${optionalFields}"
            extraRowFields="${extraTargetRowFields}"
            editingMode="${editingMode}"
            editableAccounts="${editableAccounts}"
            editableFields="${editableFields}"
            debitCreditAmount="${debitCreditAmount}"
            extraHiddenFields="${extraHiddenFields}"
            useCurrencyFormattedTotal="${useCurrencyFormattedTotal}"
            includeObjectTypeCode="${includeObjectTypeCode}"
            />
      </c:if>
    </table>
  </div>
  <SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>
</kul:tab>