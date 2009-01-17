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

<c:set var="arDocHeaderAttributes"
	value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

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
						attributeEntry="${customerAttributes.customerNumber}"
						property="document.accountsReceivableDocumentHeader.customerNumber" />
					<kul:lookup
						boClassName="org.kuali.kfs.module.ar.businessobject.Customer"
						fieldConversions="customerNumber:document.accountsReceivableDocumentHeader.customerNumber" />
				</td>
			</tr>
			<tr>
				<th width='50%'>
					Invoice
				</th>
				<td>
					<kul:htmlControlAttribute
						attributeEntry="${invoiceAttributes.organizationInvoiceNumber}"
						property="enteredInvoiceDocumentNumber" />
				</td>
			</tr>
			<tr>
				<td colspan='2'>
					<center>
						<html:image property="methodToCall.loadInvoices"
							src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
							alt="Load Invoices" title="Load Invoices" styleClass="tinybutton" />
					</center>
				</td>
			</tr>
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
										<select id="selectedInvoiceDocumentNumber" name="selectedInvoiceDocumentNumber">
											<c:forEach items="${KualiForm.invoices}" var="invoice">
												<c:choose>
													<c:when
														test="${invoice.documentNumber eq KualiForm.selectedInvoiceDocumentNumber}">
														<option selected>
															<c:out value="${invoice.documentNumber}" />
														</option>
													</c:when>
													<c:otherwise>
														<option>
															<c:out value="${invoice.documentNumber}" />
														</option>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</select>
										<logic:iterate id="invoices" name="KualiForm"
											property="invoices" indexId="ctr">
										</logic:iterate>
										<html:image property="methodToCall.goToInvoice"
											src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
											alt="Go To Invoice" title="Go To Invoice"
											styleClass="tinybutton" />
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
													literalLabel="Balance/Total" horizontal="true" />
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
															<th>
																Apply Full Amount
															</th>
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
																				property="customerInvoiceDetail[${ctr}].sequenceNumber"
																				readOnly="true" />
																		</td>
																		<td>
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.chartOfAccountsCode}"
																				property="customerInvoiceDetail[${ctr}].chartOfAccountsCode"
																				readOnly="true" />
																		</td>
																		<td>
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.accountNumber}"
																				property="customerInvoiceDetail[${ctr}].accountNumber"
																				readOnly="true" />
																		</td>
																		<td>
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}"
																				property="customerInvoiceDetail[${ctr}].invoiceItemDescription"
																				readOnly="true" />
																		</td>
																		<td style="text-align: right;">
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.amount}"
																				property="customerInvoiceDetail[${ctr}].amount"
																				readOnly="true" />
																		</td>
																		<td style="text-align: right;">
																			<kul:htmlControlAttribute
																				attributeEntry="${customerInvoiceDetailAttributes.balance}"
																				property="customerInvoiceDetail[${ctr}].amountOpen"
																				readOnly="true" />
																		</td>
																		<td style="text-align: right;">
																			<kul:htmlControlAttribute
																				styleClass="amount"
																				attributeEntry="${customerInvoiceDetailAttributes.amountApplied}"
																				property="customerInvoiceDetail[${ctr}].amountApplied" />
																		</td>
																		<td>
																			<center>
																				<html:checkbox title="Apply Full Amount" property="customerInvoiceDetail[${ctr}].fullApply" value="true" />
																			</center>
																		</td>
																	</tr>
																</c:otherwise>
															</c:choose>
														</logic:iterate>
													</table>
												</td>
											</tr>
											<tr>
												<td style='text-align: right;' colspan='4'>
													<html:image property="methodToCall.applyAllAmounts"
														src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif"
														alt="Apply" title="Apply" styleClass="tinybutton" />
												</td>
											</tr>
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
