<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<c:set var="documentAttributes" value="${DataDictionary.ElectronicInvoiceRejectDocument.attributes}" />
<c:set var="vendorAttributes" value="${DataDictionary.VendorDetail.attributes}" />
<c:set var="itemAttributes" value="${DataDictionary.ElectronicInvoiceRejectItem.attributes}" />
<c:set var="purapItemAttributes" value="${DataDictionary.PurchaseOrderItem.attributes}" />
<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}" />
<c:set var="purchaseOrderStatusAttributes" value="${DataDictionary.PurchaseOrderStatus.attributes}" />

<c:set var="fullEntryMode" value="${ KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="ElectronicInvoiceRejectDocument"
	htmlFormAction="purapElectronicInvoiceReject" renderMultipart="true"
	showTabButtons="true">

	<c:if test="${KualiForm.document.invoiceResearchIndicator}">
		NOTE: This reject document is currently being researched. See the notes below for more detail. The document will not be allowed to be routed until the research is complete.<br /><br />
	</c:if>
			
	<sys:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="false" >
    </sys:documentOverview>
	
	<kul:tab tabTitle="Comparison Data" defaultOpen="TRUE" tabErrorKey="${PurapConstants.REJECT_DOCUMENT_TAB_ERRORS}">
	    <div class="tab-container">
			<c:if test="${fn:length(KualiForm.document.invoiceRejectReasons)>0}" >
			<div class="error" align="left">Reject Reasons:</div>
			<ul>
			<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceRejectReasons" id="reason">
				<li class="error">${KualiForm.document.invoiceRejectReasons[ctr].invoiceRejectReasonDescription}</li>
			</logic:iterate>
			</ul>
			</c:if>
 	        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
	            <tr>
	                <td colspan="4" class="subhead">Electronic Invoice Data</td>
	            </tr>
	
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorDunsNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" colspan="3">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorDunsNumber}" property="document.vendorDunsNumber" readOnly="${not fullEntryMode}" />
	                </td>
	            </tr>
	
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${vendorAttributes.vendorName}" property="document.vendorDetail.vendorName" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceFileNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceFileNumber}" property="document.invoiceFileNumber" readOnly="${not fullEntryMode}" />
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceNumberAcceptIndicator}" property="document.invoiceNumberAcceptIndicator" readOnly="${not fullEntryMode}" />
	                    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceNumberAcceptIndicator}" noColon="true" />
					</td>
	            </tr>
	
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoicePurchaseOrderNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoicePurchaseOrderNumber}" property="document.invoicePurchaseOrderNumber" readOnly="${not fullEntryMode}" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceFileDate}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceFileDate}" property="document.invoiceFileDate" readOnly="${not fullEntryMode}" />
	                </td>
	            </tr>

				<c:set var="colCount" value="9" />
				<c:if test="${KualiForm.document.invoiceFileSpecialHandlingInLineIndicator || KualiForm.document.invoiceFileShippingInLineIndicator || KualiForm.document.invoiceFileDiscountInLineIndicator}">
					<c:set var="colCount" value="${colCount + 1}" />
				</c:if>

	            <tr>
	                <td colspan="4">

						<table cellpadding="0" cellspacing="0" class="datatable" summary="Items section">
				            <tr>
				                <td colspan="${colCount}" class="subhead">Electronic Invoice Items:</td>
				            </tr>
							<tr>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceReferenceItemLineNumber}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceItemQuantity}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceItemUnitOfMeasureCode}"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceItemCatalogNumber}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceReferenceItemDescription}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceItemUnitPrice}" />				
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceItemSubTotalAmount}" />				
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceItemTaxAmount}" />				
		    					<c:if test="${KualiForm.document.invoiceFileSpecialHandlingInLineIndicator || KualiForm.document.invoiceFileShippingInLineIndicator || KualiForm.document.invoiceFileDiscountInLineIndicator}">
									<th>Inline Item Values</th>
		    					</c:if>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.invoiceItemNetAmount}"/>
							</tr>
							<c:set var="colCountBeforeTotal" value="${colCount - 2}" />

							<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceRejectItems" id="itemLine">
								<tr>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceReferenceItemLineNumber}"
										    property="document.invoiceRejectItems[${ctr}].invoiceReferenceItemLineNumber"
										    readOnly="${not fullEntryMode}" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemQuantity}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemQuantity"
										    readOnly="${not fullEntryMode}" />
									</td>
									<td class="datacell" nowrap>
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemUnitOfMeasureCode}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemUnitOfMeasureCode"
										    readOnly="${true}" /><br />
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.unitOfMeasureAcceptIndicator}"
										    property="document.invoiceRejectItems[${ctr}].unitOfMeasureAcceptIndicator"
										    readOnly="${not fullEntryMode}" />
										<kul:htmlAttributeLabel attributeEntry="${itemAttributes.unitOfMeasureAcceptIndicator}" noColon="true" />
									</td>
									<td class="datacell" nowrap>
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemCatalogNumber}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemCatalogNumber"
										    readOnly="true" /><br />
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.catalogNumberAcceptIndicator}"
										    property="document.invoiceRejectItems[${ctr}].catalogNumberAcceptIndicator"
										    readOnly="${not fullEntryMode}" />
										<kul:htmlAttributeLabel attributeEntry="${itemAttributes.catalogNumberAcceptIndicator}" noColon="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceReferenceItemDescription}"
										    property="document.invoiceRejectItems[${ctr}].invoiceReferenceItemDescription"
										    readOnly="${true}" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemUnitPrice}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemUnitPrice"
										    readOnly="${not fullEntryMode}" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemSubTotalAmount}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemSubTotalAmount"
										    readOnly="${true}" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemTaxAmount}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemTaxAmount"
										    readOnly="${not fullEntryMode}" />
									</td>
		    					<c:if test="${KualiForm.document.invoiceFileSpecialHandlingInLineIndicator || KualiForm.document.invoiceFileShippingInLineIndicator || KualiForm.document.invoiceFileDiscountInLineIndicator}">
									<td class="datacell" nowrap>
					    		<c:if test="${KualiForm.document.invoiceFileSpecialHandlingInLineIndicator}">
										<kul:htmlAttributeLabel attributeEntry="${itemAttributes.invoiceItemSpecialHandlingAmount}" useShortLabel="true" />
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemSpecialHandlingAmount}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemSpecialHandlingAmount"
										    readOnly="${true}" /><br />
		    					</c:if>
		    					<c:if test="${KualiForm.document.invoiceFileShippingInLineIndicator}">
										<kul:htmlAttributeLabel attributeEntry="${itemAttributes.invoiceItemShippingAmount}" useShortLabel="true" />
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemShippingAmount}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemShippingAmount"
										    readOnly="${true}" /><br />
		    					</c:if>
		    					<c:if test="${KualiForm.document.invoiceFileDiscountInLineIndicator}">
										<kul:htmlAttributeLabel attributeEntry="${itemAttributes.invoiceItemDiscountAmount}" useShortLabel="true" />
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemDiscountAmount}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemDiscountAmount"
										    readOnly="${true}" /><br />
		    					</c:if>
									</td>
								</c:if>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemNetAmount}"
										    property="document.invoiceRejectItems[${ctr}].invoiceItemNetAmount"
										    readOnly="${true}" />
									</td>
								</tr>
							</logic:iterate>
							<tr>
								<td colspan="${colCountBeforeTotal}"></td>
								<th align="center" colspan="2">Totals:</th>
							</tr>
							<tr>
								<td colspan="${colCountBeforeTotal}"></td>
								<th align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.totalAmount}" /></th>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.totalAmount}"
									    property="document.totalAmount"
									    readOnly="true" />
								</td>
							</tr>
							<tr>
								<td colspan="${colCountBeforeTotal}">&nbsp;</td>
								<th align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceItemTaxAmount}" /></th>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceItemTaxAmount}"
									    property="document.invoiceItemTaxAmount"
									    readOnly="${not fullEntryMode || KualiForm.document.invoiceFileTaxInLineIndicator}" />									    
								</td>
							</tr>
							<tr>
								<td colspan="${colCountBeforeTotal}">&nbsp;</td>
								<th align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceItemSpecialHandlingAmount}" /></th>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceItemSpecialHandlingAmount}"
									    property="document.invoiceItemSpecialHandlingAmount"
									    readOnly="${not fullEntryMode || KualiForm.document.invoiceFileSpecialHandlingInLineIndicator}" />
								</td>
							</tr>
							<tr>
								<td colspan="${colCountBeforeTotal}">&nbsp;</td>
								<th align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceItemShippingAmount}" /></th>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceItemShippingAmount}"
									    property="document.invoiceItemShippingAmount"
									    readOnly="${not fullEntryMode || KualiForm.document.invoiceFileShippingInLineIndicator}" />
								</td>
							</tr>
							<tr>
								<td colspan="${colCountBeforeTotal}">&nbsp;</td>
								<th align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceItemDiscountAmount}" /></th>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceItemDiscountAmount}"
									    property="document.invoiceItemDiscountAmount"
									    readOnly="${not fullEntryMode || KualiForm.document.invoiceFileDiscountInLineIndicator}" />
								</td>
							</tr>
							<tr>
								<td colspan="${colCountBeforeTotal}">&nbsp;</td>
								<th align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grandTotalAmount}" /></th>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.grandTotalAmount}"
									    property="document.grandTotalAmount"
									    readOnly="true" />
								</td>
							</tr>
						</table>
					</td>
	            </tr>

	            <tr>
	                <td colspan="4" class="subhead">Purchase Order Data</td>
	            </tr>

			<c:choose>
    		<c:when test="${empty KualiForm.document.currentPurchaseOrderDocument}">
	            <tr>
	                <td align="center" valign="middle" class="datacell" colspan="4">
		    			No matching purchase order found.
	                </td>
	            </tr>
    		</c:when>
    		<c:otherwise>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorDunsNumber}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" colspan="3">
	                    <kul:htmlControlAttribute attributeEntry="${vendorAttributes.vendorDunsNumber}" property="document.vendorDetail.vendorDunsNumber" readOnly="true" />
	                </td>
	            </tr>
	
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorName}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${vendorAttributes.vendorName}" property="document.currentPurchaseOrderDocument.vendorName" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b">
	                </th>
	                <td align="left" valign="middle" class="datacell">
	                </td>
	            </tr>

	            <tr>
	                <th align="right" valign="middle" class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.purapDocumentIdentifier}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.purapDocumentIdentifier}" property="document.currentPurchaseOrderDocument.purapDocumentIdentifier" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.documentFundingSourceCode}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.documentFundingSourceCode}" property="document.currentPurchaseOrderDocument.documentFundingSourceCode" readOnly="true" />
	                </td>
	            </tr>

	            <tr>
	                <td colspan="4">
						<table cellpadding="0" cellspacing="0" class="datatable" summary="Items section">
				            <tr>
				                <td colspan="9" class="subhead">Purchase Order Items:</td>
				            </tr>
							<tr>
					            <kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.itemLineNumber}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.outstandingQuantity}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.itemUnitOfMeasureCode}"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.itemCatalogNumber}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.itemDescription}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.itemUnitPrice}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.extendedPrice}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.itemTaxAmount}"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${purapItemAttributes.totalAmount}"/>				
							</tr>

							<logic:iterate indexId="ctr" name="KualiForm" property="document.currentPurchaseOrderDocument.items" id="itemLine">
								<c:if test="${itemLine.itemType.lineItemIndicator == true}">
								<tr>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.itemLineNumber}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].itemLineNumber"
										    readOnly="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.itemQuantity}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].outstandingQuantity"
										    readOnly="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.itemUnitOfMeasureCode}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].itemUnitOfMeasureCode"
										    readOnly="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.itemCatalogNumber}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].itemCatalogNumber"
										    readOnly="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.itemDescription}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].itemDescription"
										    readOnly="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.itemUnitPrice}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].itemUnitPrice"
										    readOnly="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.extendedPrice}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].extendedPrice"
										    readOnly="true" />
									</td>									
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.itemTaxAmount}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].itemTaxAmount"
										    readOnly="true" />
									</td>
									<td class="datacell">
									    <kul:htmlControlAttribute
										    attributeEntry="${purapItemAttributes.totalAmount}"
										    property="document.currentPurchaseOrderDocument.items[${ctr}].totalAmount"
										    readOnly="true" />
									</td>
								</tr>
								</c:if>
							</logic:iterate>

						<logic:iterate indexId="ctr" name="KualiForm" property="document.currentPurchaseOrderDocument.items" id="itemLine">
							<c:if test="${itemLine.itemType.lineItemIndicator != true}">
									<tr>
										<td colspan="4">&nbsp;</td>
										<th align="right">
											<kul:htmlControlAttribute
												attributeEntry="${purapItemAttributes.itemTypeCode}"
												property="document.currentPurchaseOrderDocument.item[${ctr}].itemType.itemTypeDescription"
												readOnly="${true}" />
										</th>
										<td>
										    <kul:htmlControlAttribute attributeEntry="${purapItemAttributes.itemUnitPrice}" property="document.currentPurchaseOrderDocument.item[${ctr}].itemUnitPrice" readOnly="${true}" styleClass="amount" />
										</td>
										<td>
										    <kul:htmlControlAttribute attributeEntry="${purapItemAttributes.extendedPrice}" property="document.currentPurchaseOrderDocument.item[${ctr}].extendedPrice" readOnly="${true}" styleClass="amount" />
										</td>										
										<td>
										    <kul:htmlControlAttribute attributeEntry="${purapItemAttributes.itemTaxAmount}" property="document.currentPurchaseOrderDocument.item[${ctr}].itemTaxAmount" readOnly="${true}" styleClass="amount" />
										</td>
										<td>
										    <kul:htmlControlAttribute attributeEntry="${purapItemAttributes.totalAmount}" property="document.currentPurchaseOrderDocument.item[${ctr}].totalAmount" readOnly="${true}" styleClass="amount" />
										</td>										
									</tr>
								</c:if>
							</logic:iterate>
							<tr><td colspan="9">&nbsp;</td></tr>
							<tr>
								<td colspan="9" class="subhead">Totals</td>
							</tr>
							<tr>
								<th align="right" colspan="8">
							        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalPreTaxDollarAmount}" />
								</th>
								<td>
				                    <kul:htmlControlAttribute
				                        attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalPreTaxDollarAmount}"
				                        property="document.currentPurchaseOrderDocument.totalPreTaxDollarAmount"
				                        readOnly="true" />
								</td>
							</tr>
							<tr>
								<th align="right" colspan="8">
							        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalTaxAmount}" />
								</th>
								<td>
				                    <kul:htmlControlAttribute
				                        attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalTaxAmount}"
				                        property="document.currentPurchaseOrderDocument.totalTaxAmount"
				                        readOnly="true" />
								</td>
							</tr>							
							<tr>
								<th align="right" colspan="8">
							        <kul:htmlAttributeLabel attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalDollarAmount}" />
								</th>
								<td>
				                    <kul:htmlControlAttribute
				                        attributeEntry="${DataDictionary.RequisitionDocument.attributes.totalDollarAmount}"
				                        property="document.currentPurchaseOrderDocument.totalDollarAmount"
				                        readOnly="true" />
								</td>
							</tr>
						</table>
					</td></tr>
				</c:otherwise>
				</c:choose>
	        </table>
	
	    </div>
	</kul:tab>
	
	
	<kul:tab tabTitle="Addresses" defaultOpen="TRUE" tabErrorKey="">
	    <div class="tab-container" align="center">			
	        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
	            <tr>
	                <td colspan="4" class="subhead">Electronic Invoice Data</td>
	            </tr>
	            <tr>
	                <th colspan="2">Ship To:</th>
	                <th colspan="2">Bill To:</th>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressName}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressName}" property="document.invoiceShipToAddressName" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressName}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressName}" property="document.invoiceBillToAddressName" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressLine1}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressLine1}" property="document.invoiceShipToAddressLine1" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressLine1}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressLine1}" property="document.invoiceBillToAddressLine1" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressLine2}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressLine2}" property="document.invoiceShipToAddressLine2" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressLine2}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressLine2}" property="document.invoiceBillToAddressLine2" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressLine3}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressLine3}" property="document.invoiceShipToAddressLine3" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressLine3}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressLine3}" property="document.invoiceBillToAddressLine3" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressCityName}" useShortLabel="true" noColon="true" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressStateCode}" useShortLabel="true" noColon="true" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressPostalCode}" useShortLabel="true" />
						</div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressCityName}" property="document.invoiceShipToAddressCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressStateCode}" property="document.invoiceShipToAddressStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressPostalCode}" property="document.invoiceShipToAddressPostalCode" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressCityName}" useShortLabel="true" noColon="true" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressStateCode}" useShortLabel="true" noColon="true" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressPostalCode}" useShortLabel="true" />
						</div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressCityName}" property="document.invoiceBillToAddressCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressStateCode}" property="document.invoiceBillToAddressStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressPostalCode}" property="document.invoiceBillToAddressPostalCode" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceShipToAddressCountryName}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceShipToAddressCountryName}" property="document.invoiceShipToAddressCountryName" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceBillToAddressCountryName}" useShortLabel="true" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceBillToAddressCountryName}" property="document.invoiceBillToAddressCountryName" readOnly="true" />
	                </td>
	            </tr>
	
	            <tr>
	                <td colspan="4" class="subhead">Purchase Order Data</td>
	            </tr>
			<c:choose>
    		<c:when test="${empty KualiForm.document.currentPurchaseOrderDocument}">
	            <tr>
	                <td align="center" valign="middle" class="datacell" colspan="4">
		    			No matcing purchase order found.
	                </td>
	            </tr>
    		</c:when>
    		<c:otherwise>
	            <tr>
	                <th colspan="2">Delivery:</th>
	                <th colspan="2">Billing:</th>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryToName}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryToName}" property="document.currentPurchaseOrderDocument.deliveryToName" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingName}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingName}" property="document.currentPurchaseOrderDocument.billingName" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine1Address}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine1Address}" property="document.currentPurchaseOrderDocument.deliveryBuildingLine1Address" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingLine1Address}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingLine1Address}" property="document.currentPurchaseOrderDocument.billingLine1Address" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine2Address}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine2Address}" property="document.currentPurchaseOrderDocument.deliveryBuildingLine2Address" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingLine2Address}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingLine2Address}" property="document.currentPurchaseOrderDocument.billingLine2Address" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryCityName}" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryStateCode}" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryPostalCode}" />
						</div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryCityName}" property="document.currentPurchaseOrderDocument.deliveryCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryStateCode}" property="document.currentPurchaseOrderDocument.deliveryStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryPostalCode}" property="document.currentPurchaseOrderDocument.deliveryPostalCode" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingCityName}" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingStateCode}" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingPostalCode}" />
						</div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingCityName}" property="document.currentPurchaseOrderDocument.billingCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingStateCode}" property="document.currentPurchaseOrderDocument.billingStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingPostalCode}" property="document.currentPurchaseOrderDocument.billingPostalCode" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryCountryCode}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryCountryCode}" property="document.currentPurchaseOrderDocument.deliveryCountryCode" readOnly="true" />
	                </td>
	                <th align="right" valign="middle" class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingCountryCode}" /></div>
	                </th>
	                <td align="left" valign="middle" class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingCountryCode}" property="document.currentPurchaseOrderDocument.billingCountryCode" readOnly="true" />
	                </td>
	            </tr>
			</c:otherwise>
			</c:choose>
	        </table>
	    </div>
	</kul:tab>

    <purap:relatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    
    <purap:paymentHistory documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	            
	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}"/>

</kul:documentPage>
