<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>

<%@ attribute name="editingMode" required="false" type="java.util.Map"%>
<%@ attribute name="editableAccounts" required="true" type="java.util.Map"
              description="Map of Accounts which this user is allowed to edit" %>
<%@ attribute name="editableFields" required="false" type="java.util.Map"
              description="Map of accounting line fields which this user is allowed to edit" %>

<c:forEach items="${editableAccounts}" var="account">
  <html:hidden property="editableAccounts(${account.key})" value="${account.key}"/>
</c:forEach>

<c:forEach items="${editableFields}" var="field">
  <html:hidden property="accountingLineEditableFields(${field.key})"/>
</c:forEach>

<c:set var="columnCountUntilAmount" value="9" />
<c:set var="columnCount" value="${columnCountUntilAmount + (empty editingMode['viewOnly'] ? 1 : 0)}" />

<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${Constants.ACCOUNTING_LINE_ERRORS}">
  <c:set var="transactionAttributes" value="${DataDictionary.ProcurementCardTransactionDetail.attributes}" />
	
  <div class="tab-container" align=center>
  <logic:iterate indexId="ctr" name="KualiForm" property="document.transactionEntries" id="currentTransaction">
  
    <%-- write out target (actually from lines) as hiddens since they are not displayed but need repopulated --%>
    <logic:iterate indexId="ctr1" name="KualiForm" property="document.transactionEntries[${ctr}].targetAccountingLines" id="currentLine">
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].financialDocumentNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].financialDocumentTransactionLineNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].sequenceNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].versionNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].chartOfAccountsCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].accountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].postingYear"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].financialObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].balanceTypeCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].amount"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].subAccountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].financialSubObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].projectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].organizationReferenceId"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${ctr1}].overrideCode"/>
    </logic:iterate>
  
    <table cellpadding="0" class="datatable" summary="Transaction Details">
       <html:hidden write="false" property="document.transactionEntries[${ctr}].financialDocumentNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].financialDocumentTransactionLineNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionMerchantCategoryCode"/>
       <tr>
          <td colspan="4" class="tab-subhead">Transaction Details</td>
       </tr>
       <tr>
          <th><div align=left><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionDate}"/></div></th>
          <td valign=top><html:hidden write="true" property="document.transactionEntries[${ctr}].transactionDate"/></td>
          <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionReferenceNumber}"/></div></th>
          <td valign=top><html:hidden write="true" property="document.transactionEntries[${ctr}].transactionReferenceNumber"/></td>
       <tr>  
          <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionVendorName}"/></div></th> 
          <td valign=top><html:hidden write="true" property="document.transactionEntries[${ctr}].transactionVendorName"/></td>
          <th colspan="2"> <div align=left><a href="${KualiForm.disputeURL}" target="_blank">DISPUTE</a></div></th>
       </tr>   
    </table>    
            
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
      <fin:subheadingWithDetailToggleRow columnCount="${columnCount}" subheading="Transaction #${currentTransaction[ctr].transactionReferenceNumber} Accounting Lines"/>
      
      <fin:accountingLineImportRow
          columnCount="${columnCount}"
          isSource="true"
          editingMode="${editingMode}"
          sectionTitle="To"/>
    
      <tr>
        <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.chartOfAccountsCode}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.accountNumber}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.subAccountNumber}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialObjectCode}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialSubObjectCode}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.projectCode}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.organizationReferenceId}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.budgetYear}" rowspan="2"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.amount}" rowspan="2"/>

        <c:if test="${hasActionsColumn}">
          <kul:htmlAttributeHeaderCell literalLabel="Actions" rowspan="2"/>
        </c:if>
     </tr>
     
     <c:if test="${empty editingMode['viewOnly']}">
       <c:set var="valuesMap" value="${KualiForm.newSourceLine[ctr].valuesMap}"/>
       <fin:accountingLineRow
          accountingLine="newSourceLine[${ctr}]"
          accountingLineAttributes="${accountingLineAttributes}"
          dataCellCssClass="infoline"
          rowHeader="add"
          actionGroup="newAddLine"
          actionInfix="source"
          accountingAddLineIndex="${ctr2}"
          optionalFields=""
          extraRowFields=""
          extraRowLabelFontWeight="bold"
          readOnly="false"
          debitCreditAmount="false"
          hiddenFields="postingYear,overrideCode,financialDocumentTransactionLineNumber"
          columnCountUntilAmount="${columnCountUntilAmount}"
          debitCellProperty="newSourceLineDebit"
          creditCellProperty="newSourceLineCredit"
          includeObjectTypeCode="false"
          displayHidden="false"
          accountingLineValuesMap="${valuesMap}"
          />   
     </c:if>
     
     <logic:iterate indexId="ctr2" name="KualiForm" property="document.transactionEntries[${ctr}].sourceAccountingLines" id="currentLine">
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
          accountingLine="document.transactionEntries[${ctr}].sourceAccountingLine[${ctr2}]"
          baselineAccountingLine="${baselineSourceOrTarget}AccountingLine[${ctr}]"
          accountingLineIndex="${ctr2}"
          accountingLineAttributes="${accountingLineAttributes}"
          dataCellCssClass="datacell"
          rowHeader="${ctr2+1}"
          actionGroup="existingLine"
          actionInfix="source"
          optionalFields=""
          extraRowFields=""
          extraRowLabelFontWeight="normal"
          readOnly="${!accountIsEditable}"
          editableFields="${editableFields}"
          debitCreditAmount=""
          hiddenFields="postingYear,overrideCode,sequenceNumber,financialDocumentTransactionLineNumber,versionNumber,financialDocumentNumber"
          columnCountUntilAmount="${columnCountUntilAmount}"
          debitCellProperty="journalLineHelper[${ctr2}].debit"
          creditCellProperty="journalLineHelper[${ctr2}].credit"
          includeObjectTypeCode="false"
          displayHidden="false"
          decorator="sourceLineDecorator[${ctr2}]"
          accountingLineValuesMap="${currentLine.valuesMap}"
          />
     </logic:iterate>
     <tr>
        <td class="total-line" colspan="${columnCountUntilAmount}">&nbsp;</td>
        <td class="total-line" style="border-left: 0px;"><strong>Total: $${KualiForm[totalName]}</strong></td>
        <td class="total-line" style="border-left: 0px;">&nbsp;</td>
     </tr>
    </table>
   </logic:iterate> 
  </div>
  <SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>
</kul:tab>