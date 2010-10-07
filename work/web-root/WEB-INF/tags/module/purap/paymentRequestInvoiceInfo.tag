<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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
                   		readOnly="${not (fullEntryMode or editPreExtract)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                   &nbsp; &nbsp;<kul:htmlControlAttribute 
                   					attributeEntry="${documentAttributes.immediatePaymentIndicator}" property="document.immediatePaymentIndicator" 
                   					readOnly="${not (fullEntryMode or editPreExtract)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
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
                   		readOnly="${not (fullEntryMode or editPreExtract)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
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
                    <sys:bankControl property="document.bankCode" objectProperty="document.bank" readOnly="${not (fullEntryMode or editPreExtract)}"/>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right">&nbsp;</div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        &nbsp;
                    </td>
                
            </tr>
            
		</table> 
		
		

    </div>
</kul:tab>
