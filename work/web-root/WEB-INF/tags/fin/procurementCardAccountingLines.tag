<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld"  %>
<%@ taglib prefix="logic" uri="/tlds/struts-logic.tld" %>

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
<c:set var="columnCount" value="${columnCountUntilAmount + 1 + (empty editingMode['viewOnly'] ? 1 : 0)}" />
<c:set var="accountingLineAttributes" value="${DataDictionary['SourceAccountingLine'].attributes}" />

<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${Constants.ACCOUNTING_LINE_ERRORS}">
  <c:set var="transactionAttributes" value="${DataDictionary.ProcurementCardTransactionDetail.attributes}" />
	
  <div class="tab-container" align=center>
  <logic:iterate indexId="ctr" name="KualiForm" property="document.transactionEntries" id="currentTransaction">
  
    <%-- write out target (actually from lines) as hiddens since they are not displayed but need repopulated --%>
    <logic:iterate indexId="tCtr" name="KualiForm" property="document.transactionEntries[${ctr}].targetAccountingLines" id="currentLine">
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].financialDocumentNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].financialDocumentTransactionLineNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].sequenceNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].versionNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].chartOfAccountsCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].accountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].postingYear"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].financialObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].balanceTypeCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].amount"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].subAccountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].financialSubObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].projectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].organizationReferenceId"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].targetAccountingLines[${tCtr}].overrideCode"/>
    </logic:iterate>
  
    <table cellpadding="0" class="datatable" summary="Transaction Details">
       <html:hidden write="false" property="document.transactionEntries[${ctr}].financialDocumentNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].financialDocumentTransactionLineNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionMerchantCategoryCode"/>
       
       <fin:subheadingWithDetailToggleRow columnCount="4" subheading="Transaction #${currentTransaction.transactionReferenceNumber}"/>

       <tr>
          <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionDate}"/></div></th>
          <td valign=top><html:hidden write="true" property="document.transactionEntries[${ctr}].transactionDate"/></td>
          <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionReferenceNumber}"/></div></th>
          <td valign=top>
            <kul:inquiry boClassName="org.kuali.module.financial.bo.ProcurementCardTransactionDetail" 
               keyValues="financialDocumentNumber=${currentTransaction.financialDocumentNumber}&financialDocumentTransactionLineNumber=${currentTransaction.financialDocumentTransactionLineNumber}" 
               render="true">
              <html:hidden write="true" property="document.transactionEntries[${ctr}].transactionReferenceNumber"/>
            </kul:inquiry>
          </td>
       <tr>  
          <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionVendorName}"/></div></th> 
          <td valign=top><html:hidden write="true" property="document.transactionEntries[${ctr}].transactionVendorName"/></td>
          <th colspan="2"> <div align="left"><a href="${KualiForm.disputeURL}" target="_blank"><img src="images/buttonsmall_dispute.gif"/></a></div></th>
       </tr>   
    </table>   

            
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
       <tr>
          <td colspan="${columnCount}" class="tab-subhead">Accounting Lines</td>
       </tr>
      
      <tr>
        <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.chartOfAccountsCode}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.accountNumber}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.subAccountNumber}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialObjectCode}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialSubObjectCode}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.projectCode}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.organizationReferenceId}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.budgetYear}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.amount}"/>

        <c:if test="${hasActionsColumn}">
          <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
        </c:if>
     </tr>
     
     <c:if test="${empty editingMode['viewOnly']}">
       <c:set var="valuesMap" value="${KualiForm.newSourceLines[ctr].valuesMap}"/>
       <fin:accountingLineRow
          accountingLine="newSourceLines[${ctr}]"
          accountingLineAttributes="${accountingLineAttributes}"
          dataCellCssClass="infoline"
          rowHeader="add"
          actionGroup="newAddLine"
          actionInfix="source"
          accountingAddLineIndex="${currentTransaction.transactionReferenceNumber}"
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
     
     <logic:iterate indexId="sCtr" name="KualiForm" property="document.transactionEntries[${ctr}].sourceAccountingLines" id="currentLine">
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
          accountingLine="document.transactionEntries[${ctr}].sourceAccountingLines[${sCtr}]"
          baselineAccountingLine="document.transactionEntries[${ctr}].sourceAccountingLines[${sCtr}]"
          accountingLineIndex="${sCtr}"
          accountingLineAttributes="${accountingLineAttributes}"
          dataCellCssClass="datacell"
          rowHeader="${sCtr+1}"
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
          debitCellProperty="journalLineHelper[${sCtr}].debit"
          creditCellProperty="journalLineHelper[${sCtr}].credit"
          includeObjectTypeCode="false"
          displayHidden="false"
          decorator="sourceLineDecorator[${sCtr}]"
          accountingLineValuesMap="${currentLine.valuesMap}"
          />
     </logic:iterate>
     <tr>
        <td class="total-line" colspan="${columnCountUntilAmount}">&nbsp;</td>
        <td class="total-line" style="border-left: 0px;"><strong>Total: $${KualiForm.document.transactionEntries[ctr].sourceTotal}</strong></td>
        <c:if test="empty editingMode['viewOnly']">
          <td class="total-line" style="border-left: 0px;">&nbsp;</td>
        </c:if>
     </tr>
    </table>
    
    <br/>
   </logic:iterate> 
  </div>
  <SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>
</kul:tab>