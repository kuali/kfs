<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>
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

<c:set var="columnCountUntilAmount" value="8" />
<c:set var="columnCount" value="${columnCountUntilAmount + 1 + (empty editingMode['viewOnly'] ? 1 : 0)}" />
<c:set var="accountingLineAttributes" value="${DataDictionary['TargetAccountingLine'].attributes}" />

<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${Constants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN},document.transactionEntries*">
  <c:set var="transactionAttributes" value="${DataDictionary.ProcurementCardTransactionDetail.attributes}" />
  <c:set var="vendorAttributes" value="${DataDictionary.ProcurementCardVendor.attributes}" />
  <c:set var="cardAttributes" value="${DataDictionary.ProcurementCardHolder.attributes}" />
	
  <div class="tab-container" align=center>
  <c:set var="totalNewTargetCtr" value="0"/>
  <c:set var="baseCtr" value="0"/>
  <logic:iterate indexId="ctr" name="KualiForm" property="document.transactionEntries" id="currentTransaction">
    <table cellpadding="0" class="datatable" summary="Transaction Details">
       <html:hidden write="false" property="document.transactionEntries[${ctr}].documentNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].financialDocumentTransactionLineNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPostingDate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionOriginalCurrencyCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionBillingCurrencyCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionOriginalCurrencyAmount"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionCurrencyExchangeRate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionSettlementAmount"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionSalesTaxAmount"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionTaxExemptIndicator"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPurchaseIdentifierIndicator"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPurchaseIdentifierDescription"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionUnitContactName"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionTravelAuthorizationCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPointOfSaleCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionCycleStartDate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionCycleEndDate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].versionNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.documentNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.financialDocumentTransactionLineNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorName"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorLine1Address"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorLine2Address"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorCityName"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorStateCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorZipCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.visaVendorIdentifier"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorOrderNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.transactionMerchantCategoryCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.versionNumber"/>
       
       <%-- write out source (actually from lines) as hiddens since they are not displayed but need repopulated --%>
       <logic:iterate indexId="tCtr" name="KualiForm" property="document.transactionEntries[${ctr}].sourceAccountingLines" id="currentLine">
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].documentNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].financialDocumentTransactionLineNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].sequenceNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].versionNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].chartOfAccountsCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].accountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].postingYear"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].financialObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].balanceTypeCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].amount"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].subAccountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].financialSubObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].projectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].organizationReferenceId"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].overrideCode"/>
      </logic:iterate>
                                                                                           
       <fin:subheadingWithDetailToggleRow columnCount="4" subheading="Transaction #${currentTransaction.transactionReferenceNumber}"/>
	      <tr>
	        <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.transactionCreditCardNumber}" readOnly="true"/></div></th>
	        <td>
	          <kul:inquiry boClassName="org.kuali.module.financial.bo.ProcurementCardHolder" 
               keyValues="documentNumber=${currentTransaction.documentNumber}" 
               render="true">
	            <kul:htmlControlAttribute attributeEntry="${cardAttributes.transactionCreditCardNumber}" property="document.procurementCardHolder.transactionCreditCardNumber"
	             readOnly="true" encryptValue="${!empty KualiForm.editingMode['viewOnly']}" displayMask="****************" />
	          </kul:inquiry>
	        </td>
	      </tr>
	      <tr>
	        <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.cardHolderName}" readOnly="true"/></div></th>
	        <td><kul:htmlControlAttribute attributeEntry="${cardAttributes.cardHolderName}" property="document.procurementCardHolder.cardHolderName" readOnly="true"/></td>
            <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionTotalAmount}"/></div></th>
            <td valign=top><kul:htmlControlAttribute attributeEntry="${transactionAttributes.transactionTotalAmount}" property="document.transactionEntries[${ctr}].transactionTotalAmount" readOnly="true"/></td>
	     </tr>
       <tr>
          <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionDate}"/></div></th>
          <td valign=top><html:hidden write="true" property="document.transactionEntries[${ctr}].transactionDate"/></td>
          <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionReferenceNumber}"/></div></th>
          <td valign=top>
            <kul:inquiry boClassName="org.kuali.module.financial.bo.ProcurementCardTransactionDetail" 
               keyValues="documentNumber=${currentTransaction.documentNumber}&financialDocumentTransactionLineNumber=${currentTransaction.financialDocumentTransactionLineNumber}" 
               render="true">
              <html:hidden write="true" property="document.transactionEntries[${ctr}].transactionReferenceNumber"/>
            </kul:inquiry>
          </td>
       </tr>   
       <tr>  
          <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorName}"/></div></th> 
          <td valign=top>
            <kul:inquiry boClassName="org.kuali.module.financial.bo.ProcurementCardVendor" 
               keyValues="documentNumber=${currentTransaction.documentNumber}&financialDocumentTransactionLineNumber=${currentTransaction.financialDocumentTransactionLineNumber}" 
               render="true">
              <html:hidden write="true" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorName"/>
            </kul:inquiry>  
          </td>
          <th colspan="2"> <div align="left">
          <c:if test="${empty editingMode['viewOnly']}">
            <a href="${KualiForm.disputeURL}" target="_blank"><img src="images/buttonsmall_dispute.gif"/></a>
          </c:if>
          </div></th>
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
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.amount}"/>

        <c:if test="${hasActionsColumn}">
          <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
        </c:if>
     </tr>
     
     <c:if test="${empty editingMode['viewOnly']}">
       <c:set var="valuesMap" value="${KualiForm.newTargetLines[totalNewTargetCtr].valuesMap}"/>
       <fin:accountingLineRow
          accountingLine="newTargetLines[${totalNewTargetCtr}]"
          accountingLineAttributes="${accountingLineAttributes}"
          dataCellCssClass="infoline"
          rowHeader="add"
          actionGroup="newGroupLine"
          actionInfix="Target"
          accountingAddLineIndex="${totalNewTargetCtr}"
          optionalFields=""
          extraRowFields=""
          extraRowLabelFontWeight="bold"
          readOnly="false"
          debitCreditAmount="false"
          hiddenFields="postingYear,overrideCode"
          columnCountUntilAmount="${columnCountUntilAmount}"
          debitCellProperty="newTargetLineDebit"
          creditCellProperty="newTargetLineCredit"
          includeObjectTypeCode="false"
          displayHidden="false"
          accountingLineValuesMap="${valuesMap}"
          />  
          <html:hidden property="newTargetLines[${totalNewTargetCtr}].financialDocumentTransactionLineNumber" value="${currentTransaction.financialDocumentTransactionLineNumber}"/> 
     </c:if>
     

     <logic:iterate indexId="sCtr" name="KualiForm" property="document.transactionEntries[${ctr}].targetAccountingLines" id="currentLine">
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
                <bean:write name="KualiForm" property="baselineTargetAccountingLine[${baseCtr}].accountKey" />
            </c:set>
            <c:set var="accountIsEditable" value="${!empty editableAccounts[baselineAccountKey]}" />
         </c:otherwise>
       </c:choose>
       
       <fin:accountingLineRow
          accountingLine="document.transactionEntries[${ctr}].targetAccountingLines[${sCtr}]"
          baselineAccountingLine="baselineTargetAccountingLine[${baseCtr}]"
          accountingLineIndex="${baseCtr}"
          accountingLineAttributes="${accountingLineAttributes}"
          dataCellCssClass="datacell"
          rowHeader="${sCtr+1}"
          actionGroup="existingLine"
          actionInfix="Target"
          optionalFields=""
          extraRowFields=""
          extraRowLabelFontWeight="normal"
          readOnly="${!accountIsEditable}"
          editableFields="${editableFields}"
          debitCreditAmount=""
          hiddenFields="postingYear,overrideCode,sequenceNumber,financialDocumentTransactionLineNumber,versionNumber,documentNumber"
          columnCountUntilAmount="${columnCountUntilAmount}"
          debitCellProperty="journalLineHelper[${sCtr}].debit"
          creditCellProperty="journalLineHelper[${sCtr}].credit"
          includeObjectTypeCode="false"
          displayHidden="false"
          decorator="targetLineDecorator[${sCtr}]"
          accountingLineValuesMap="${currentLine.valuesMap}"
          />
          <c:set var="baseCtr" value="${baseCtr+1}"/>
     </logic:iterate>
     <tr>
        <td class="total-line" colspan="${columnCountUntilAmount}">&nbsp;</td>
        <td class="total-line" style="border-left: 0px;"><strong>Total: $${KualiForm.document.transactionEntries[ctr].targetTotal}</strong></td>
        <c:if test="empty editingMode['viewOnly']">
          <td class="total-line" style="border-left: 0px;">&nbsp;</td>
        </c:if>
        <td class="total-line">&nbsp;</td>
     </tr>
    </table>
    
    <br/>
    <c:set var="totalNewTargetCtr" value="${totalNewTargetCtr+1}"/>
   </logic:iterate> 
  </div>
  <SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>
</kul:tab>