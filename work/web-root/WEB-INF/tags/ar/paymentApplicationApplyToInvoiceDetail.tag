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

<%@ attribute name="customerAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="customerInvoiceDetailAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="invoiceAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>

<c:set var="disableQuickApplyToDetails" value="${KualiForm.selectedInvoiceDocument.quickApply}" />
<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<kul:tab tabTitle="Apply to Invoice Detail" defaultOpen="true"
	tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.APPLY_TO_INVOICE_DETAIL_TAB}">
	<div class="tab-container" align="center">
        <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<th>
					Customer
				</th>
				<td>
					<kul:htmlControlAttribute
						readOnly="${readOnly}"
						attributeEntry="${customerAttributes.customerNumber}"
						property="document.accountsReceivableDocumentHeader.customerNumber" />
					<c:if test="${readOnly ne true}">
						<kul:lookup
							boClassName="org.kuali.kfs.module.ar.businessobject.Customer"
							fieldConversions="customerNumber:document.accountsReceivableDocumentHeader.customerNumber" />
					</c:if>
				</td>
			</tr>
			<tr>
				<th width='50%'>
					Invoice
				</th>
				<td>
					<kul:htmlControlAttribute
						readOnly="${readOnly}"
						attributeEntry="${invoiceAttributes.organizationInvoiceNumber}"
						property="enteredInvoiceDocumentNumber" />
				</td>
			</tr>
			<c:if test="${readOnly ne true}">
				<tr>
					<td colspan='2'>
						<center>
							<html:image property="methodToCall.loadInvoices"
								src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
								alt="Load Invoices" title="Load Invoices" styleClass="tinybutton" />
						</center>
					</td>
				</tr>
			</c:if>
		</table>
		<c:choose>
			<c:when
				test="${null == KualiForm.document.accountsReceivableDocumentHeader.customerNumber}">
			</c:when>
			<c:otherwise>
				<table width="100%" cellpadding="0" cellspacing="0"
					class="datatable">
					<tr id='beta_zeta'>
						<td>
							<table width="100%" cellpadding="0" cellspacing="0"
								class="datatable">
								<tr>
									<td colspan='2' class='tab-subhead'>
										<label for="selectedInvoiceDocumentNumber">Invoices</label>
										<c:if test="${readOnly ne true}">
											<html:select property="selectedInvoiceDocumentNumber">
												<c:forEach items="${KualiForm.invoices}" var="invoice">
													<html:option value="${invoice.documentNumber}">
														<c:out value="${invoice.documentNumber}" />
													</html:option>
												</c:forEach>
											</html:select>
											<logic:iterate id="invoices" name="KualiForm"
												property="invoices" indexId="ctr">
											</logic:iterate>
											<html:image property="methodToCall.goToInvoice"
												src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
												alt="Go To Invoice" title="Go To Invoice"
												styleClass="tinybutton" />
										</c:if>
									</td>
								</tr>
								<tr>
									<th colspan='2' class='tab-subhead'>
										<c:out
											value="Invoice ${KualiForm.selectedInvoiceDocumentNumber}" />
										&nbsp;
										<c:if test="${!empty KualiForm.previousInvoiceDocumentNumber}">
											<html:image property="methodToCall.goToPreviousInvoice"
												src="${ConfigProperties.externalizable.images.url}tinybutton-prev.gif"
												alt="Go To Previous Invoice" title="Go To Previous Invoice"
												styleClass="tinybutton" />
										</c:if>
										<c:if
											test="${!empty KualiForm.previousInvoiceDocumentNumber && !empty KualiForm.nextInvoiceDocumentNumber}">|</c:if>
										<c:if test="${!empty KualiForm.nextInvoiceDocumentNumber}">
											<html:image property="methodToCall.goToNextInvoice"
												src="${ConfigProperties.externalizable.images.url}tinybutton-next.gif"
												alt="Go To Next Invoice" title="Go To Next Invoice"
												styleClass="tinybutton" />
										</c:if>
									</th>
								</tr>
								<tr>
									<td colspan='2'>
										<table width='100%' cellpadding='0' cellspacing='0'
											class='datatable'>
											<tr>
												<kul:htmlAttributeHeaderCell
													labelFor="selectedInvoiceDocumentNumber"
													literalLabel="Invoice Number/Billing Date"
													horizontal="true" />
												<kul:htmlAttributeHeaderCell
													labelFor="selectedInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName"
													literalLabel="Invoice Header/Customer Name"
													horizontal="true" />
												<kul:htmlAttributeHeaderCell
													labelFor="selectedInvoiceBalance"
													literalLabel="Open Amount/Total" horizontal="true" />
												<kul:htmlAttributeHeaderCell
													labelFor="amountAppliedDirectlyToInvoice"
													literalLabel="Amount Applied to Invoice" horizontal="true" />
											</tr>
											<tr>
												<td>
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.organizationInvoiceNumber}"
														property="selectedInvoiceDocumentNumber" readOnly="true" />
												</td>
												<td>
													<kul:htmlControlAttribute
														attributeEntry="${arDocHeaderAttributes.financialDocumentExplanationText}"
														property="selectedInvoiceDocument.accountsReceivableDocumentHeader.financialDocumentExplanationText"
														readOnly="true" />
												</td>
												<td style="text-align: right;">
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.balance}"
														property="selectedInvoiceBalance" readOnly="true" />
												</td>
												<td rowspan='2' style='vertical-align: top; text-align: right;'>
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.balance}"
														property="amountAppliedDirectlyToInvoice" readOnly="true" />
												</td>
											</tr>
											<tr>
												<td>
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.billingDate}"
														property="selectedInvoiceDocument.billingDate"
														readOnly="true" />
												</td>
												<td>
													<kul:htmlControlAttribute
														attributeEntry="${customerAttributes.customerNumber}"
														property="selectedInvoiceDocument.accountsReceivableDocumentHeader.customer.customerName"
														readOnly="true" />
												</td>
												<td style="text-align: right;">
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.sourceTotal}"
														property="selectedInvoiceTotalAmount" readOnly="true" />
												</td>
											</tr>
											<tr>
												<td colspan='4' class='tab-subhead'>
													Invoice Detail
												</td>
											</tr>
											<tr>
												<td colspan='4'>
													<table width='100%' cellpadding='0' cellspacing='0'
														class='datatable' id="customerInvoiceDetails">
														<tr>
															<th>
																&nbsp;
															</th>
															<th>
																Chart
															</th>
															<th>
																Account
															</th>
															<th>
																Item Desc
															</th>
															<th>
																Item Total Amount
															</th>
															<th>
																Item Open Amount
															</th>
															<th>
																Apply Amount
															</th>
															<c:if test="${readOnly ne true}">
																<th>
																	Apply Full Amount
																</th>
															</c:if>
														</tr>
														<logic:iterate id="customerInvoiceDetail" name="KualiForm"
															property="customerInvoiceDetails" indexId="ctr">
															<c:set var="isDiscount" value="${customerInvoiceDetail.amount < 0}" />
															<c:choose>
																<c:when test="${isDiscount}">
																</c:when>
																<c:otherwise>
																	<tr>
																		<td>
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.sequenceNumber}"
																				property="customerInvoiceDetails[${ctr}].sequenceNumber"
																				readOnly="true" />
																		</td>
																		<td>
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.chartOfAccountsCode}"
																				property="customerInvoiceDetails[${ctr}].chartOfAccountsCode"
																				readOnly="true" />
																		</td>
																		<td>
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.accountNumber}"
																				property="customerInvoiceDetails[${ctr}].accountNumber"
																				readOnly="true" />
																		</td>
																		<td>
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}"
																				property="customerInvoiceDetails[${ctr}].invoiceItemDescription"
																				readOnly="true" />
																		</td>
																		<td style="text-align: right;">
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.amount}"
																				property="customerInvoiceDetails[${ctr}].amount"
																				readOnly="true" />
																		</td>
																		<td style="text-align: right;">
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.balance}"
																				property="customerInvoiceDetails[${ctr}].amountOpenPerCurrentPaymentApplicationDocument"
																				readOnly="true" />
																		</td>
																		<td style="text-align: right;">
																			<kul:htmlControlAttribute
																				disabled="${disableQuickApplyToDetails or customerInvoiceDetail.fullApply}"
																				readOnly="${readOnly}"
																				styleClass="amount"
																				attributeEntry="${customerInvoiceDetailAttributes.amountApplied}"
																				property="customerInvoiceDetails[${ctr}].amountApplied" />
																		</td>
																		<c:if test="${readOnly ne true}">
																			<td>
																				<center>
																					<html:checkbox disabled="${true eq disableQuickApplyToDetails}" title="Apply Full Amount" property="customerInvoiceDetails[${ctr}].fullApply" value="true" />
																				</center>
																			</td>
																		</c:if>
																	</tr>
																</c:otherwise>
															</c:choose>
														</logic:iterate>
													</table>
												</td>
											</tr>
											<c:if test="${readOnly ne true}">
											<tr>
												<td style='text-align: right;' colspan='4'>
													<html:image property="methodToCall.applyAllAmounts"
														src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif"
														alt="Apply" title="Apply" styleClass="tinybutton" />
												</td>
											</tr>
											</c:if>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>

				</table>
			</c:otherwise>
		</c:choose>
	</div>
</kul:tab>
