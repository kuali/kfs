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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="displayPaymentRequestInvoiceInfoFields" required="false"
              description="Boolean to indicate if Invoice Info PREQ specific fields should be displayed" %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="fullDocumentEntryCompleted" value="${not empty KualiForm.editingMode['fullDocumentEntryCompleted']}" />
<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}" />
<c:set var="editPreExtract"	value="${(not empty KualiForm.editingMode['editPreExtract'])}" />
<c:set var="processedUsingPdp" value="${KualiForm.document.paymentMethod.processedUsingPdp}" />
<c:set var="tabindexOverrideBase" value="40" />

<kul:tab tabTitle="Invoice Info" defaultOpen="true" tabErrorKey="${PurapConstants.PAYMENT_REQUEST_INVOICE_TAB_ERRORS}">
    <div class="tab-container" align=center>
            <h3>Invoice Info</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Info Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.invoiceNumber}" property="document.invoiceNumber" 
                   		readOnly="${not displayInitTab}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier"
                   		readOnly="true" tabindexOverride="${tabindexOverrideBase + 3}"/>
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestPayDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.paymentRequestPayDate}" property="document.paymentRequestPayDate" datePicker="true" 
                   		readOnly="${not (fullEntryMode or editPreExtract) or not processedUsingPdp}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                   &nbsp; &nbsp;<kul:htmlControlAttribute 
                   					attributeEntry="${documentAttributes.immediatePaymentIndicator}" property="document.immediatePaymentIndicator" 
                   					readOnly="${not (fullEntryMode or editPreExtract) or not processedUsingPdp}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                   (Immediate Pay)
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.purchaseOrderNotes}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderNotes}" property="document.purchaseOrderNotes" 
                   readOnly="true" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.invoiceDate}" property="document.invoiceDate" datePicker="true" 
                   		readOnly="${not displayInitTab}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.paymentRequestCostSourceCode}" readOnly="${not displayInitTab}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentRequestCostSourceCode}" 
                   property="document.paymentRequestCostSourceCode" 
                   extraReadOnlyProperty="document.paymentRequestCostSource.purchaseOrderCostSourceDescription"
                   readOnly="true" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderEndDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute  attributeEntry="${documentAttributes.purchaseOrderEndDate}" property="document.purchaseOrderDocument.purchaseOrderEndDate" readOnly="true" />
               </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.recurringPaymentTypeCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.recurringPaymentTypeCode}" 
                   property="document.recurringPaymentTypeCode"
                   extraReadOnlyProperty="document.recurringPaymentType.recurringPaymentTypeDescription"  
                   readOnly="true"/>
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentAttachmentIndicator}"  readOnly="${not (fullEntryMode or editPreExtract)}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.paymentAttachmentIndicator}" property="document.paymentAttachmentIndicator"  
                   		readOnly="${not (fullEntryMode or editPreExtract) or not processedUsingPdp}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<c:choose>
                	<c:when test="${not fullDocumentEntryCompleted}">
					<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorInvoiceAmount}" useShortLabel="true"/></div>
					</c:when>
					<c:otherwise>
					&nbsp;
					</c:otherwise>
					</c:choose>
                </th>
                <td align=left valign=middle class="datacell">
                	<c:choose>
                	<c:when test="${not fullDocumentEntryCompleted}">                
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorInvoiceAmount}" property="document.vendorInvoiceAmount" readOnly="true" />
					</c:when>
					<c:otherwise>
					&nbsp;
					</c:otherwise>
					</c:choose>                	
                </td>
            </tr>
			<tr>
                    <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.extractedTimestamp}" /></div>
	                </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.extractedTimestamp}" property="document.extractedTimestamp" readOnly="${true}" />
                        <c:if test="${not empty KualiForm.document.extractedTimestamp}">
                           <purap:disbursementInfo sourceDocumentNumber="${KualiForm.document.documentNumber}" sourceDocumentType="${KualiForm.document.documentType}" />                        
						</c:if>
                    </td>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.accountsPayableApprovalTimestamp}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute 
                        	attributeEntry="${documentAttributes.accountsPayableApprovalTimestamp}" property="document.accountsPayableApprovalTimestamp" 
                        	readOnly="${not displayInitTab}" tabindexOverride="${tabindexOverrideBase + 3}"/>
                    </td>
                
            </tr>
			
			<tr>
	                <sys:bankLabel align="right"/>
							
					<%-- Changed editability of the bank field to lock down after full entry (initiation) if the payment 			
					will *not* be processed by PDP.  (When not, the GL entries which affect the bank accounts are created as 			
					part of the main document, not by PDP, and there is no good way to reverse them out. --%> 			
					<c:set var="canEditBank" value="${fullEntryMode or (editPreExtract and KualiForm.document.paymentMethod.processedUsingPdp)}" /> 			
					<c:set var="canEditPaymentMethod" value="${fullEntryMode}" /> 
	
                    <sys:bankControl property="document.bankCode" objectProperty="document.bank" readOnly="${not canEditBank}"/>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentMethodCode}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
						<kul:htmlControlAttribute 	
						attributeEntry="${documentAttributes.paymentMethodCode}" property="document.paymentMethodCode" 			
						readOnly="${not canEditPaymentMethod}" tabindexOverride="${tabindexOverrideBase + 4}" 			
						onchange="paymentMethodChanged( this.value );" />
                    </td>
                
            </tr>
			<c:if test="${(fullEntryMode or editPreExtract)}">
		        <script type="text/javascript" src="dwr/interface/PaymentMethodGeneralLedgerPendingEntryService.js"></script>
		        <script type="text/javascript">
	                function paymentMethodChanged(selectedMethod) {
		                
                        if ( selectedMethod != "" ) {
							 var dwrReply = {
						         callback:function(data) {
							         if ( data != null && typeof data == 'object' ) {
				                         setRecipientValue( "document.bankCode", data.bankCode );
				                         setRecipientValue( "document.bank", data.bankName );
					                 } else {
				                         setRecipientValue( "document.bankCode", "" );
				                         setRecipientValue( "document.bank", "" );
					                 }
						         },
						         errorHandler:function( errorMessage ) {
					                 window.status = errorMessage;
						         }
							 };
                             PaymentMethodGeneralLedgerPendingEntryService.getBankForPaymentMethod( selectedMethod, dwrReply );
                        }
	                }
		        </script>
			</c:if>
		</table>
    </div>
</kul:tab>
