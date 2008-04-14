<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="displayPaymentRequestInvoiceInfoFields" required="false"
              description="Boolean to indicate if Invoice Info PREQ specific fields should be displayed" %>

<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}" />
<c:set var="editPreExtract"	value="${(not empty KualiForm.editingMode['editPreExtract'])}" />

<kul:tab tabTitle="Invoice Info" defaultOpen="true" tabErrorKey="document.paymentRequestPayDate">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Invoice Info</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Info Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceNumber}" property="document.invoiceNumber" readOnly="${not displayInitTab}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier"
                   		readOnly="${not displayInitTab}" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestPayDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentRequestPayDate}" property="document.paymentRequestPayDate" datePicker="true" readOnly="${not (fullEntryMode or editPreExtract)}" />
                   &nbsp; &nbsp;<kul:htmlControlAttribute attributeEntry="${documentAttributes.immediatePaymentIndicator}" property="document.immediatePaymentIndicator" readOnly="${not (fullEntryMode or editPreExtract)}" />
                   (immediate Pay)
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
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceDate}" property="document.invoiceDate" datePicker="true" readOnly="${not displayInitTab}" />
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
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentAttachmentIndicator}" property="document.paymentAttachmentIndicator"  readOnly="${not (fullEntryMode or editPreExtract)}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<c:choose>
                	<c:when test="${KualiForm.fullDocumentEntryCompleted eq false}">
					<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorInvoiceAmount}" useShortLabel="true"/></div>
					</c:when>
					<c:otherwise>
					&nbsp;
					</c:otherwise>
					</c:choose>
                </th>
                <td align=left valign=middle class="datacell">
                	<c:choose>
                	<c:when test="${KualiForm.fullDocumentEntryCompleted eq false}">                
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
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.extractedDate}" /></div>
	                </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.extractedDate}" property="document.extractedDate" readOnly="${true}" />
                        <c:if test="${not empty KualiForm.document.extractedDate}">
 	        			   <c:url var="page" value="/pdp/epicpaymentdetail.do">
	        			     <c:param name="sourceDocNbr" value="${KualiForm.document.documentNumber}"/>
	        			     <c:param name="docTypeCode" value="PREQ"/>
	        			   </c:url>
	        			   <c:url var="image" value="/pdp/images/tinybutton-disbursinfo.gif"/>
						   &nbsp;<a href="${page}" target="_pdp"><img src="${image}" border="0"/></a>
						</c:if>
                    </td>
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                
            </tr>
			
            
		</table> 
		
		

    </div>
</kul:tab>
