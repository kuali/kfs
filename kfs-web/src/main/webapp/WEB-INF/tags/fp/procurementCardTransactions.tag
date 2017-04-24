<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="editingMode" required="false" type="java.util.Map"%>
<%@ attribute name="editableAccounts" required="true" type="java.util.Map"
              description="Map of Accounts which this user is allowed to edit" %>
<%@ attribute name="editableFields" required="false" type="java.util.Map"
              description="Map of accounting line fields which this user is allowed to edit" %>

<c:set var="columnCountUntilAmount" value="8" />
<c:set var="columnCount" value="${columnCountUntilAmount + 1 + (KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] ? 1 : 0)}" />
<c:set var="accountingLineAttributes" value="${DataDictionary['TargetAccountingLine'].attributes}" />

<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN},document.transactionEntries*">
  <c:set var="transactionAttributes" value="${DataDictionary.ProcurementCardTransactionDetail.attributes}" />
  <c:set var="vendorAttributes" value="${DataDictionary.ProcurementCardVendor.attributes}" />
  <c:set var="cardAttributes" value="${DataDictionary.ProcurementCardHolder.attributes}" />
  <c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />
	
  <div class="tab-container" align="center">
  <logic:iterate indexId="ctr" name="KualiForm" property="document.transactionEntries" id="currentTransaction">
    <table cellpadding="0" class="datatable" summary="Transaction Details">
                                                                                           
       <fp:subheadingWithDetailToggleRow columnCount="4" subheading="Transaction #${currentTransaction.transactionReferenceNumber}"/>
         <tr>
           <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.transactionCreditCardNumber}" readOnly="true"/></div></th>
           <td>
             <kul:inquiry boClassName="edu.arizona.kfs.fp.businessobject.ProcurementCardHolder" keyValues="documentNumber=${currentTransaction.documentNumber}" render="true">
               <c:choose>
                 <c:when test="${KualiForm.transactionCreditCardNumbersViewStatus[ctr]}">
                   <bean:write name="KualiForm" property="document.procurementCardHolder.transactionCreditCardNumber" />
                 </c:when>
                 <c:otherwise>
                   <kul:htmlControlAttribute attributeEntry="${cardAttributes.transactionCreditCardNumber}" property="document.procurementCardHolder.transactionCreditCardNumber" readOnly="true" />
                 </c:otherwise>
               </c:choose>
             </kul:inquiry>
           </td>
           <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorName}" /></div></th>
           <td valign="top">
             <kul:inquiry boClassName="org.kuali.kfs.fp.businessobject.ProcurementCardVendor" keyValues="documentNumber=${currentTransaction.documentNumber}&financialDocumentTransactionLineNumber=${currentTransaction.financialDocumentTransactionLineNumber}" render="true">
               <bean:write name="KualiForm" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorName" />
             </kul:inquiry>
           </td>
         </tr>
         <tr>
           <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.cardHolderName}" readOnly="true"/></div></th>
           <td><kul:htmlControlAttribute attributeEntry="${cardAttributes.cardHolderName}" property="document.procurementCardHolder.cardHolderName" readOnly="true" /></td>
           <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.cardNoteText}" readOnly="true"/></div></th>
           <td><kul:htmlControlAttribute attributeEntry="${cardAttributes.cardNoteText}" property="document.procurementCardHolder.cardNoteText" readOnly="true" /></td>
         </tr>
         <tr>
           <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.cardHolderAlternateName}" readOnly="true" /></div></th>
           <td><kul:htmlControlAttribute attributeEntry="${cardAttributes.cardHolderAlternateName}" property="document.procurementCardHolder.cardHolderAlternateName" readOnly="true" /></td>
           <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionTotalAmount}" /></div></th>
           <td valign="top"><kul:htmlControlAttribute attributeEntry="${transactionAttributes.transactionTotalAmount}" property="document.transactionEntries[${ctr}].transactionTotalAmount" readOnly="true" /></td>
         </tr>
         <tr>
           <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionDate}" /></div></th>
           <td valign="top"><bean:write name="KualiForm" property="document.transactionEntries[${ctr}].transactionDate" /></td>
           <c:if test="${KualiForm.enableSalesTaxIndicator}">
             <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionSalesTaxAmount}" /></div></th>
             <td valign="top"><kul:htmlControlAttribute attributeEntry="${transactionAttributes.transactionSalesTaxAmount}" property="document.transactionEntries[${ctr}].transactionSalesTaxAmount" readOnly="true"/></td>
           </c:if>
           <c:if test="${!KualiForm.enableSalesTaxIndicator}">
             <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionSalesTaxAmount"/>
           </c:if>
         </tr>
         <tr>
           <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionPostingDate}" /></div></th>
           <td valign="top"><bean:write name="KualiForm" property="document.transactionEntries[${ctr}].transactionPostingDate" /></td>
           <c:if test="${KualiForm.enableSalesTaxIndicator}">
             <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionEditableSalesTaxAmount}"/></div></th>
             <td valign="top"><kul:htmlControlAttribute attributeEntry="${transactionAttributes.transactionEditableSalesTaxAmount}" property="document.transactionEntries[${ctr}].transactionEditableSalesTaxAmount" readOnly="${!canEdit}"/></td>
           </c:if>
           <c:if test="${!KualiForm.enableSalesTaxIndicator}">
             <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionSalesTaxAmount"/>
           </c:if>
         </tr>
         <tr>
           <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionReferenceNumber}"/></div></th>
           <td valign="top">
             <kul:inquiry boClassName="edu.arizona.kfs.fp.businessobject.ProcurementCardTransactionDetail" keyValues="documentNumber=${currentTransaction.documentNumber}&financialDocumentTransactionLineNumber=${currentTransaction.financialDocumentTransactionLineNumber}" render="true">
               <bean:write name="KualiForm" property="document.transactionEntries[${ctr}].transactionReferenceNumber" />
             </kul:inquiry>
           </td>
           <c:if test="${KualiForm.enableSalesTaxIndicator}">
             <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionTaxExemptIndicator}"/></div></th>
             <td valign=top><kul:htmlControlAttribute attributeEntry="${transactionAttributes.transactionTaxExemptIndicator}" property="document.transactionEntries[${ctr}].transactionTaxExemptIndicator" readOnly="${!canEdit}"/></td>
           </c:if>
           <c:if test="${!KualiForm.enableSalesTaxIndicator}">
             <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionTaxExemptIndicator"/>
           </c:if>
         </tr>
         <tr>
           <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionNoReceiptIndicator}"/></div></th>
           <td valign="top"><kul:htmlControlAttribute attributeEntry="${transactionAttributes.transactionNoReceiptIndicator}" property="document.transactionEntries[${ctr}].transactionNoReceiptIndicator" readOnly="${!canEdit}" /></td>
           <th colspan="2"><div align="left">
             <c:choose>
               <c:when test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
                 <a href="${KualiForm.disputeURL}" target="_blank"><img src="${ConfigProperties.externalizable.images.url}buttonsmall_dispute.gif"/></a>
               </c:when>
               <c:otherwise>&nbsp;</c:otherwise>
             </c:choose>
             </div></th>
         </tr>
    </table>   
	
	<%-- For accounting lines to tab through the fields correctly, the sys-java:accountingLineGroup needs to be in a TAB tag, and in 
	 	sys-java:accountingLines.  Nesting accountingLineGroup sets the tabindex element of the image tags very high, and the actual fields 
	 	have no tabindex attribute at all.  Without nesting both the images and fields have no tabindex attribute.  
	--%>
	<kul:subtab width="100%" noShowHideButton="true" useCurrentTabIndexAsKey="false">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newTargetLines[${ctr}]" collectionPropertyName="document.transactionEntries[${ctr}].targetAccountingLines" collectionItemPropertyName="document.transactionEntries[${ctr}].targetAccountingLines" attributeGroupName="target" />
		</sys-java:accountingLines>
	</kul:subtab>
    
    <br/>
   </logic:iterate> 
  </div>
  <SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>
</kul:tab>
