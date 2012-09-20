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
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="travelAdvanceAttributes" value="${DataDictionary.TravelAdvance.attributes}" />
<c:set var="advanceAttributes" value="${DataDictionary.AdvancePaymentReason.attributes}" />
<c:set var="docType" value="${KualiForm.document.dataDictionaryEntry.documentTypeName }" />
<c:set var="policyDisabled" value="${!KualiForm.waitingOnTraveler && !fullEntryMode}" />

<kul:tab tabTitle="Travel Advances" defaultOpen="${fn:length(KualiForm.document.travelAdvances) > 0}" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_ADVANCE_ERRORS}">
	<div class="tab-container" align="left">
		<h3>Travel Advances</h3>
		<c:if
			test="${fn:length(KualiForm.document.travelAdvances) == 0 || (docType == 'TAA' && KualiForm.multipleAdvances)}">
			<table cellpadding="0" cellspacing="0" class="datatable"
				summary="Travel Advance Section">
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}" />
						</div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute
							attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}"
							property="newTravelAdvanceLine.travelAdvanceRequested"
							readOnly="${!fullEntryMode}" />
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arCustomerId}" />
						</div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute
							attributeEntry="${travelAdvanceAttributes.arCustomerId}"
							property="newTravelAdvanceLine.arCustomerId" readOnly="true" />
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}" />
						</div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute
							attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}"
							property="newTravelAdvanceLine.arInvoiceDocNumber"
							readOnly="true" />
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.dueDate}" />
						</div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute
							attributeEntry="${travelAdvanceAttributes.dueDate}"
							property="newTravelAdvanceLine.dueDate" datePicker="true"
							readOnly="${!fullEntryMode}" />
					</td>
				</tr>
				<c:if test="${KualiForm.showPaymentMethods}">
					<tr>
						<th class="bord-l-b">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.paymentMethod}" />
							</div>
						</th>
						<td class="datacell"><kul:htmlControlAttribute
								attributeEntry="${travelAdvanceAttributes.paymentMethod}"
								property="newTravelAdvanceLine.paymentMethod"
								readOnly="${!fullEntryMode}" />
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}" />
						</div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute
							attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}"
							property="newTravelAdvanceLine.advancePaymentReasonCode"
							readOnly="${!fullEntryMode}" />
					</td>
				</tr>
				<c:if test="${KualiForm.showPolicy}">
					<tr>
						<th class="bord-l-b">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}" />
							</div>
						</th>
						<td class="datacell">
							<kul:htmlControlAttribute
								attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}"
								property="newTravelAdvanceLine.travelAdvancePolicy"
								readOnly="${!fullEntryMode}" />${KualiForm.policyURL}
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.additionalJustification}" />
						</div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute
							attributeEntry="${travelAdvanceAttributes.additionalJustification}"
							property="newTravelAdvanceLine.additionalJustification"
							readOnly="${!fullEntryMode}" />
					</td>
				</tr>
				<tr>
					<td colspan="2" class="infoline">
						<c:if test="${fullEntryMode}">
							<div align=center>
								<html:image
									src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
									styleClass="tinybutton"
									property="methodToCall.addTravelAdvanceLine"
									alt="Add Travel Advance Line" title="Add Travel Advance Line" />
							</div>
						</c:if>
					</td>
				</tr>

			</table>
		</c:if>
		<c:if test="${fn:length(KualiForm.document.travelAdvances) > 0}">
			<table cellpadding="0" cellspacing="0" class="datatable">
				<logic:iterate indexId="ctr" name="KualiForm" property="document.travelAdvances" id="currentLine">
					<c:set var="exists" value="${currentLine.arInvoiceDocNumber!=null}" />
					<tr>
						<th scope="row" rowspan="4">
							<div align="right">
								<kul:htmlControlAttribute
									attributeEntry="${travelAdvanceAttributes.financialDocumentLineNumber}"
									property="document.travelAdvances[${ctr}].financialDocumentLineNumber"
									readOnly="true" />
							</div>
						</th>
						<th class="bord-l-b">
							<div align="left">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}" noColon="true" />
							</div>
						</th>
						<th class="bord-l-b">
							<div align="left">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}" noColon="true" />
							</div>
						</th>
						<th class="bord-l-b">
							<div align="left">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arCustomerId}" noColon="true" />
							</div>
						</th>
						<th class="bord-l-b">
							<div align="left">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.dueDate}" noColon="true" />
							</div>
						</th>
						<c:if test="${KualiForm.showPaymentMethods}">
							<th class="bord-l-b">
								<div align="left">
									<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.paymentMethod}" noColon="true" />
								</div>
							</th>
						</c:if>
						<c:if test="${KualiForm.showPolicy}">
							<th class="bord-l-b">
								<div align="left">
									<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}" noColon="true" />
								</div>
							</th>
						</c:if>
						<c:if test="${!exists && fullEntryMode}">
							<td rowspan="4" valign="middle">
								<div align=center>
									<html:image
										src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
										styleClass="tinybutton"
										property="methodToCall.deleteTravelAdvanceLine.line${ctr}"
										alt="Delete Travel Advance Line"
										title="Delete Travel Advance Line" />
								</div>
							</td>
						</c:if>
					</tr>
					<tr>
						<td valign=top nowrap>
							<div align="center">
								<kul:htmlControlAttribute
									attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}"
									property="document.travelAdvances[${ctr}].travelAdvanceRequested"
									readOnly="${exists || !fullEntryMode}" />
							</div>
						</td>
						<td valign=top>
							<kul:htmlControlAttribute
								attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}"
								property="document.travelAdvances[${ctr}].arInvoiceDocNumber"
								readOnly="true" />
						</td>
						<td valign=top>
							<kul:htmlControlAttribute
								attributeEntry="${travelAdvanceAttributes.arCustomerId}"
								property="document.travelAdvances[${ctr}].arCustomerId"
								readOnly="true" />
						</td>
						<td valign=top>
							<kul:htmlControlAttribute
								attributeEntry="${travelAdvanceAttributes.dueDate}"
								property="document.travelAdvances[${ctr}].dueDate"
								readOnly="${exists || !fullEntryMode}" />
						</td>
						<c:if test="${KualiForm.showPaymentMethods}">
							<td valign=top>
								<kul:htmlControlAttribute
									attributeEntry="${travelAdvanceAttributes.paymentMethod}"
									property="document.travelAdvances[${ctr}].paymentMethod"
									readOnly="${exists || !fullEntryMode}" />
							</td>
						</c:if>
						<c:if test="${KualiForm.showPolicy}">
							<td valign=top>
								*<kul:htmlControlAttribute
									attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}"
									property="document.travelAdvances[${ctr}].travelAdvancePolicy"
									readOnly="${policyDisabled}" />
							</td>
						</c:if>
					</tr>
					<c:set var="colSpan" value="${1}" />
					<c:set var="colSpan" value="${KualiForm.showPolicy?colSpan+1:colSpan}" />
					<c:set var="colSpan" value="${KualiForm.showPaymentMethods?colSpan+1:colSpan}" />
					<tr>
						<th class="bord-l-b" colspan="3">
							<div align="left">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}" noColon="true" />
							</div>
						</th>
						<th class="bord-l-b" colspan="${colSpan}">
							<div align="left">
								<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.additionalJustification}" noColon="true" />
							</div>
						</th>
					</tr>
					<tr>
						<td valign="top" colspan="3">
							<kul:htmlControlAttribute
								attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}"
								property="document.travelAdvances[${ctr}].advancePaymentReasonCode"
								readOnly="${!fullEntryMode}" />
						</td>
						<td valign="top" colspan="${colSpan}">
							<kul:htmlControlAttribute
								attributeEntry="${travelAdvanceAttributes.additionalJustification}"
								property="document.travelAdvances[${ctr}].additionalJustification"
								readOnly="${!fullEntryMode}" />
						</td>
					</tr>
				</logic:iterate>
			</table>
			<c:if test="${KualiForm.showPolicy}">
			<div align="right">${KualiForm.policyURL}</c:if>
			</div>
		</c:if>
	</div>
</kul:tab>
