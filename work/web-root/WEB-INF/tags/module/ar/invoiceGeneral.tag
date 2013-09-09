<%--
 Copyright 2006-2009 The Kuali Foundation
 
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
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:set var="invoiceGeneralDetailAttributes" value="${DataDictionary.InvoiceGeneralDetail.attributes}" />
<c:set var="documentAttributes" value="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}" />
<c:set var="readOnlyForFinal" value="${readOnly || not KualiForm.document.finalizable}" />
<kul:tab tabTitle="General" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
			<tr>
				<td colspan="4" class="subhead">Invoice Lines</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.proposalNumber}" labelFor="document.proposalNumber" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.proposalNumber.div">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.proposalNumber}" property="document.proposalNumber" readOnly="true" />
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
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.comment}" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.comment.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.comment}" property="document.invoiceGeneralDetail.comment"
							readOnly="true" />
					</div>
				</td>

			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.finalBill}" />
					</div>
				</th>
				<c:choose>
					<c:when test="${KualiForm.document.invoiceReversal}">
						<td align=left valign=middle class="datacell" style="width: 25%;" />
					</c:when>
					<c:otherwise>
						<td align=left valign=middle class="datacell" style="width: 25%;">
							<div id="document.finalBill.div">
								<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.finalBill}" property="document.invoiceGeneralDetail.finalBill"
									readOnly="${readOnlyForFinal}" />
							</div>
						</td>
					</c:otherwise>
				</c:choose>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.billingFrequency}" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingFrequency.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.billingFrequency}"
							property="document.invoiceGeneralDetail.billingFrequency" readOnly="true" />
					</div>
				</td>

			</tr>
			<tr>

				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.contractGrantType}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.contractGrantType.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.contractGrantType}"
							property="document.invoiceGeneralDetail.contractGrantType" readOnly="true" />
					</div>
				</td>
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
			</tr>
			<tr>
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

				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.newTotalBilled}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.newTotalBilled.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.newTotalBilled}"
							property="document.invoiceGeneralDetail.newTotalBilled" readOnly="true" />
						&nbsp;&nbsp;&nbsp;
						<c:if test="${!empty KualiForm.document.proposalNumber && KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
							<c:if test="${empty KualiForm.document.invoiceMilestones}">
								<c:if test="${empty KualiForm.document.invoiceBills}">
									<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_calculate.gif" styleClass="tinybutton"
										property="methodToCall.recalculateNewTotalBilled" title="relcalculate" alt="recalculate" />
								</c:if>
							</c:if>
						</c:if>
					</div>
				</td>
			</tr>
			<tr>
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
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.billedToDate}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billedToDate.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.billedToDate}" property="document.invoiceGeneralDetail.billedToDate"
							readOnly="true" />
					</div>
				</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.costShareAmount}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.costShareAmount.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.costShareAmount}"
							property="document.invoiceGeneralDetail.costShareAmount" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.lastBilledDate}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billedToDate.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.lastBilledDate}"
							property="document.invoiceGeneralDetail.lastBilledDate" readOnly="true" />
					</div>
				</td>
			</tr>
		</table>
	</div>
</kul:tab>
