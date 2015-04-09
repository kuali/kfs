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
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:set var="invoiceGeneralDetailAttributes" value="${DataDictionary.InvoiceGeneralDetail.attributes}" />
<c:set var="documentAttributes" value="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}" />
<c:set var="readOnlyForFinal" value="${readOnly || not KualiForm.document.finalizable}" />
<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />
<kul:tab tabTitle="General" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
			<tr>
				<td colspan="4" class="subhead">Billing Summary</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.proposalNumber}" labelFor="document.invoiceGeneralDetail.proposalNumber" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.proposalNumber.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.proposalNumber}" property="document.invoiceGeneralDetail.proposalNumber" readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.awardTotal}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.awardTotal.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.awardTotal}" property="document.invoiceGeneralDetail.awardTotal"
							readOnly="true" />
					</div>
				</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.awardDateRange}" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.awardDateRange.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.awardDateRange}"
							property="document.invoiceGeneralDetail.awardDateRange" readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.totalPreviouslyBilled}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.totalPreviouslyBilled.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.totalPreviouslyBilled}" property="document.invoiceGeneralDetail.totalPreviouslyBilled"
							readOnly="true" />
					</div>
				</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.billingFrequencyCode}" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingFrequencyCode.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.billingFrequencyCode}"
							property="document.invoiceGeneralDetail.billingFrequencyCode" readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.totalAmountBilledToDate}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.totalAmountBilledToDate.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.totalAmountBilledToDate}"
							property="document.invoiceGeneralDetail.totalAmountBilledToDate" readOnly="true" />
						&nbsp;&nbsp;&nbsp;
						<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber && KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
							<c:if test="${empty KualiForm.document.invoiceMilestones}">
								<c:if test="${empty KualiForm.document.invoiceBills}">
									<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_calculate.gif" styleClass="tinybutton"
										property="methodToCall.recalculateTotalAmountBilledToDate" title="relcalculate" alt="recalculate" />
								</c:if>
							</c:if>
						</c:if>
					</div>
				</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.billingPeriod}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingPeriod.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.billingPeriod}"
							property="document.invoiceGeneralDetail.billingPeriod" readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.amountRemainingToBill}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.amountRemainingToBill.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.amountRemainingToBill}"
							property="document.invoiceGeneralDetail.amountRemainingToBill" readOnly="true" />
					</div>
				</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.finalBillIndicator}" />
					</div>
				</th>
				<c:choose>
					<c:when test="${KualiForm.document.invoiceReversal}">
						<td align=left valign=middle class="datacell" style="width: 25%;" />
					</c:when>
					<c:otherwise>
						<td align=left valign=middle class="datacell" style="width: 25%;">
							<div id="document.finalBill.div">
								<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.finalBillIndicator}" property="document.invoiceGeneralDetail.finalBillIndicator"
									readOnly="${readOnlyForFinal}" />
							</div>
						</td>
					</c:otherwise>
				</c:choose>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.instrumentTypeCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.instrumentTypeCode.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.instrumentTypeCode}"
							property="document.invoiceGeneralDetail.instrumentTypeCode" readOnly="true" />
					</div>
				</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.lastBilledDate}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.lastBilledDate.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.lastBilledDate}"
							property="document.invoiceGeneralDetail.lastBilledDate" readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.costShareAmount}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" colspan="3">
					<div id="document.costShareAmount.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.costShareAmount}"
							property="document.invoiceGeneralDetail.costShareAmount" readOnly="${readOnly}" />
					</div>
				</td>
			</tr>
			<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">
				<tr>
					<td colspan="4" class="subhead">Customer Information</td>
				</tr>
				<tr>
					<th align=right valign=middle class="bord-l-b" style="width: 25%;">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.customerNumber}"
								labelFor="document.accountsReceivableDocumentHeader.customerNumber" />
						</div>
					</th>
					<td valign=top>
            			<c:if test="${not empty KualiForm.document.accountsReceivableDocumentHeader.customerNumber}">
		                <kul:inquiry boClassName="org.kuali.kfs.module.ar.businessobject.Customer" keyValues="customerNumber=${KualiForm.document.accountsReceivableDocumentHeader.customerNumber}" render="true">
		                    <kul:htmlControlAttribute 
		                    	attributeEntry="${arDocHeaderAttributes.customerNumber}" property="document.accountsReceivableDocumentHeader.customer.customerNumber" 
		                    	readOnly="true" tabindexOverride="${tabindexOverrideBase + 0}"/>
		                </kul:inquiry>
	                </c:if>
          			</td>
					<th align=right valign=middle class="bord-l-b" style="width: 25%;">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerName}" />
						</div>
					</th>
					<td align=left valign=middle class="datacell" style="width: 25%;">
						<div id="document.accountsReceivableDocumentHeader.customer.customerName.div">
							<kul:htmlControlAttribute attributeEntry="${documentAttributes.customerName}"
								property="document.accountsReceivableDocumentHeader.customer.customerName" readOnly="true" />
						</div>
					</td>
					
				</tr>
			</c:if>	
		</table>
	</div>

</kul:tab>
