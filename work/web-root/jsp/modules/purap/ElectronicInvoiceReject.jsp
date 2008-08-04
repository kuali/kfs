<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="ElectronicInvoiceRejectDocument"
	htmlFormAction="purapElectronicInvoiceReject" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<kfs:hiddenDocumentFields excludePostingYear="true" isFinancialDocument="false" />

	<kfs:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true"
        fiscalYearReadOnly="${not KualiForm.editingMode['allowPostingYearEntry']}"
        postingYearAttributes="${DataDictionary.ElectronicInvoiceRejectDocument.attributes}" >

    	<purap:purapDocumentDetail
	    	documentAttributes="${DataDictionary.ElectronicInvoiceRejectDocument.attributes}"
	    	detailSectionLabel="Requisition Detail"
	    	editableFundingSource="true" />
    </kfs:documentOverview>
	
    <html:hidden property="document.vendorHeaderGeneratedIdentifier" />
    <html:hidden property="document.vendorDetailAssignedIdentifier" />

	<kul:tab tabTitle="Caomparison Data" defaultOpen="TRUE" tabErrorKey="">
	    <div class="tab-container" align=center>

			<logic:iterate indexId="ctr" name="KualiForm" property="document.electronicInvoiceRejectReasons" id="reason">
				<html:hidden write="true" property="document.electronicInvoiceRejectReasons[${ctr}].invoiceRejectReasonDescription" /><br />
			</logic:iterate>
			
    		<c:if test="${document.researchIndicator}">
				This reject document is currently being researched. See the notes below for more detail.<br />
			</c:if>
			
 	        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
	            <tr>
	                <td colspan="4" class="subhead">Electronic Invoice Data</td>
	            </tr>
	
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorDunsNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" colpsan="3">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendor.vendorName}" property="document.vendorDunsNumber" readOnly="${not fullEntryMode}" />
	                </td>
	            </tr>
	
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${vendorAttributes.vendorName}" property="document.electronicInvoiceLoadSummary.vendorName" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceNumber}" property="document.invoiceNumber" readOnly="${not fullEntryMode}" />
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceNumberOverrideIndicator}" property="document.invoiceNumberOverrideIndicator" readOnly="${not fullEntryMode}" />
	                </td>
	            </tr>
	
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderId}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderId}" property="document.purchaseOrderId" readOnly="${not fullEntryMode}" />
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceDate}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceDate}" property="document.invoiceDate" readOnly="${not fullEntryMode}" />
	                </td>
	            </tr>

	            <tr>
	                <td colspan="4">
						<table cellpadding="0" cellspacing="0" class="datatable" summary="Items section">
							<tr>
					            <kul:htmlAttributeHeaderCell literalLabel="Items:"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.unitOfMeasureCode}"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" />				
							</tr>

							<logic:iterate indexId="ctr" name="KualiForm" property="document.electronicInvoiceRejectItems" id="itemLine">
								<tr>
									<td>&nbsp;</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.itemLineNumber}"
										    property="document.electronicInvoiceRejectItems[${ctr}].itemLineNumber"
										    readOnly="${not fullEntryMode}" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemUnitOfMeasureCode}"
										    property="document.electronicInvoiceRejectItems[${ctr}].invoiceItemUnitOfMeasureCode"
										    readOnly="${not fullEntryMode}" />
					                    <kul:htmlControlAttribute attributeEntry="${itemAttributes.invoiceItemUnitOfMeasureCodeOverrideIndicator}" property="document.electronicInvoiceRejectItems[${ctr}].invoiceItemUnitOfMeasureCodeOverrideIndicator" readOnly="${not fullEntryMode}" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceCatalogNumber}"
										    property="document.electronicInvoiceRejectItems[${ctr}].invoiceCatalogNumber"
										    readOnly="true" />
					                    <kul:htmlControlAttribute attributeEntry="${itemAttributes.invoiceCatalogNumberOverrideIndicator}" property="document.electronicInvoiceRejectItems[${ctr}].invoiceCatalogNumberOverrideIndicator" readOnly="${not fullEntryMode}" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.itemReferenceDescription}"
										    property="document.electronicInvoiceRejectItems[${ctr}].itemReferenceDescription"
										    readOnly="true" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceUnitPrice}"
										    property="document.electronicInvoiceRejectItems[${ctr}].invoiceUnitPrice"
										    readOnly="${not fullEntryMode}" />
									</td>
								</tr>
							</logic:iterate>

							<tr>
								<td colspan="6">subtotal:</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceSubtotalAmount}"
									    property="document.totalAmount"
									    readOnly="true" />
								</td>
							</tr>
							<tr>
								<td colspan="6">spcl handling:</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceSpecialHandlingAmount}"
									    property="document.invoiceSpecialHandlingAmount"
									    readOnly="${not fullEntryMode}" />
								</td>
							</tr>
							<tr>
								<td colspan="6">shipping</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceShippingAmount}"
									    property="document.invoiceShippingAmount"
									    readOnly="${not fullEntryMode}" />
								</td>
							</tr>
							<tr>
								<td colspan="6">tax</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceTaxAmount}"
									    property="document.invoiceTaxAmount"
									    readOnly="${not fullEntryMode}" />
								</td>
							</tr>
							<tr>
								<td colspan="6">discount</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceDiscountAmount}"
									    property="document.invoiceDiscountAmount"
									    readOnly="${not fullEntryMode}" />
								</td>
							</tr>
							<tr>
								<td colspan="6">total</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${documentAttributes.invoiceNetAmount}"
									    property="document.netAmount"
									    readOnly="true" />
								</td>
							</tr>
						</table>
					</td>
	            </tr>

	            <tr>
	                <td colspan="4" class="subhead">Purchase Order Data</td>
	            </tr>

    		<c:when test="${empty document.purchaseOrderDocument}">
	            <tr>
	                <td align=center valign=middle class="datacell" colpsan="4">
		    			No matcing purchase order found.
	                </td>
	            </tr>
    		</c:when>
    		<c:otherwise>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorDunsNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" colpsan="3">
	                    <kul:htmlControlAttribute attributeEntry="${vendorAttributes.vendorDunsNumber}" property="document.vendorDetail.vendorDunsNumber" readOnly="true" />
	                </td>
	            </tr>
	
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${vendorAttributes.vendorName}" property="document.electronicInvoiceLoadSummary.vendorName" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.status}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.status}" property="document.purchaseOrderDocument.status" readOnly="true" />
	                </td>
	            </tr>

	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.purapDocumentIdentifier}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.purapDocumentIdentifier}" property="document.purchaseOrderDocument.getPurapDocumentIdentifier" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.fundingSourceCode}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.fundingSourceCode}" property="document.purchaseOrderDocument.fundingSourceCode" readOnly="true" />
	                </td>
	            </tr>

	            <tr>
	                <td colspan="4">
						<table cellpadding="0" cellspacing="0" class="datatable" summary="Items section">
							<tr>
					            <kul:htmlAttributeHeaderCell literalLabel="Items:"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
					            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.unitOfMeasureCode}"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
								<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" />				
							</tr>

							<logic:iterate indexId="ctr" name="KualiForm" property="document.purchaseOrderDocument.items" id="itemLine">
								<tr>
									<td>&nbsp;</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.itemLineNumber}"
										    property="document.purchaseOrderDocument.items[${ctr}].itemLineNumber"
										    readOnly="${not fullEntryMode}" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceItemUnitOfMeasureCode}"
										    property="document.purchaseOrderDocument.items[${ctr}].itemUnitOfMeasureCode"
										    readOnly="${not fullEntryMode}" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceCatalogNumber}"
										    property="document.purchaseOrderDocument.items[${ctr}].itemCatalogNumber"
										    readOnly="true" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.itemReferenceDescription}"
										    property="document.purchaseOrderDocument.items[${ctr}].itemDescription"
										    readOnly="true" />
									</td>
									<td class="infoline">
									    <kul:htmlControlAttribute
										    attributeEntry="${itemAttributes.invoiceUnitPrice}"
										    property="document.purchaseOrderDocument.items[${ctr}].itemUnitPrice"
										    readOnly="${not fullEntryMode}" />
									</td>
								</tr>
							</logic:iterate>

							<tr>
								<td colspan="6">freight:</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${purchaseOrderAttributes.freightAmount}"
									    property="document.purchaseOrderDocument.freightAmount"
									    readOnly="true" />
								</td>
							</tr>
							<tr>
								<td colspan="6">shipping and handling</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${purchaseOrderAttributes.shippingAmount}"
									    property="document.purchaseOrderDocument.shippingAmount"
									    readOnly="${not fullEntryMode}" />
								</td>
							</tr>
							<tr>
								<td colspan="6">total amount</td>
								<td>
								    <kul:htmlControlAttribute
									    attributeEntry="${purchaseOrderAttributes.totalDollarAmount}"
									    property="document.purchaseOrderDocument.totalDollarAmount"
									    readOnly="true" />
								</td>
							</tr>
						</table>
					</td>
	            </tr>
				</c:otherwise>

	        </table>
	
	    </div>
	</kul:tab>
	
	
	<kul:tab tabTitle="Addresses" defaultOpen="TRUE" tabErrorKey="">
	    <div class="tab-container" align=center>			
	
	        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
	            <tr>
	                <td colspan="4" class="subhead">Electronic Invoice Data</td>
	            </tr>
	            <tr>
	                <th colspan="2">Ship To:</th>
	                <th colspan="2">Bill To:</th>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressName}" property="document.shipToAddressName" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressName}" property="document.billToAddressName" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressLine1}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressLine1}" property="document.shipToAddressLine1" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressLine1}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressLine1}" property="document.billToAddressLine1" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressLine2}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressLine2}" property="document.shipToAddressLine2" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressLine2}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressLine2}" property="document.billToAddressLine2" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressLine3}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressLine3}" property="document.shipToAddressLine3" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressLine3}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressLine3}" property="document.billToAddressLine3" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressCityName}" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressStateCode}" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressPostalCode}" />
						</div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressCityName}" property="document.shipToAddressCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressStateCode}" property="document.shipToAddressStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressPostalCode}" property="document.shipToAddressPostalCode" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressCityName}" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressStateCode}" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressPostalCode}" />
						</div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressCityName}" property="document.billToAddressCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressStateCode}" property="document.billToAddressStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressPostalCode}" property="document.billToAddressPostalCode" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
				    <html:hidden property="document.shipToAddressType" />
				    <html:hidden property="document.billToAddressType" />
				    <html:hidden property="document.shipToAddressCountryCode" />
				    <html:hidden property="document.billToAddressCountryCode" />

	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipToAddressCountryName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipToAddressCountryName}" property="document.shipToAddressCountryName" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billToAddressCountryName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billToAddressCountryName}" property="document.billToAddressCountryName" readOnly="true" />
	                </td>
	            </tr>
	
	            <tr>
	                <td colspan="4" class="subhead">Purchase Order Data</td>
	            </tr>
	            <tr>
	                <th colspan="2">Delivery:</th>
	                <th colspan="2">Billing:</th>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryToName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryToName}" property="document.purchaseOrder.deliveryToName" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingName}" property="document.purchaseOrder.billingName" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine1Address}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine1Address}" property="document.purchaseOrder.deliveryBuildingLine1Address" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingLine1Address}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingLine1Address}" property="document.purchaseOrder.billingLine1Address" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine2Address}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryBuildingLine2Address}" property="document.purchaseOrder.deliveryBuildingLine2Address" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingLine2Address}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingLine2Address}" property="document.purchaseOrder.billingLine2Address" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryCityName}" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryStateCode}" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryPostalCode}" />
						</div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryCityName}" property="document.purchaseOrder.deliveryCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryStateCode}" property="document.purchaseOrder.deliveryStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryPostalCode}" property="document.purchaseOrder.deliveryPostalCode" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right">
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingCityName}" />,&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingStateCode}" />&nbsp;
	                    	<kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingPostalCode}" />
						</div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingCityName}" property="document.purchaseOrder.billingCityName" readOnly="true" />,&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingStateCode}" property="document.purchaseOrder.billingStateCode" readOnly="true" />&nbsp;
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingPostalCode}" property="document.purchaseOrder.billingPostalCode" readOnly="true" />
	                </td>
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.deliveryCountryCode}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.deliveryCountryCode}" property="document.purchaseOrder.deliveryCountryCode" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${purchaseOrderAttributes.billingCountryCode}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${purchaseOrderAttributes.billingCountryCode}" property="document.purchaseOrder.billingCountryCode" readOnly="true" />
	                </td>
	            </tr>	
	        </table>
	
	    </div>
	</kul:tab>

    <purap:relatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    
    <purap:paymentHistory
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	            
	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kfs:documentControls transactionalDocument="true" />

</kul:documentPage>
