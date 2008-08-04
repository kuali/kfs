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
	tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_CUSTOMER_INVOICE_DETAILS_ERRORS}">
	<html:hidden
		property="document.accountsReceivableDocumentHeader.documentNumber" />
	<html:hidden
		property="document.accountsReceivableDocumentHeader.objectId" />
	<html:hidden
		property="document.accountsReceivableDocumentHeader.versionNumber" />
	<html:hidden
		property="document.accountsReceivableDocumentHeader.processingChartOfAccountCode" />
	<html:hidden
		property="document.accountsReceivableDocumentHeader.processingOrganizationCode" />
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
										Invoices
										<select name="selectedInvoiceDocumentNumber">
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
											<html:hidden
												property="customerInvoiceDocument[${ctr}].documentNumber" />
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
												<td>
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.balance}"
														property="selectedInvoiceBalance" readOnly="true" />
												</td>
												<td rowspan='2' style='vertical-align: top;'>
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
												<td>
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
																Item Tot Amt
															</th>
															<th>
																Dtl Balance
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
															<html:hidden property="customerInvoiceDetail[${ctr}].documentNumber" />
															<html:hidden property="customerInvoiceDetail[${ctr}].invoiceItemDescription" />
															<html:hidden property="customerInvoiceDetail[${ctr}].appliedAmount" />
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
																<td>
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.amount}"
																		property="customerInvoiceDetail[${ctr}].amount"
																		readOnly="true" />
																</td>
																<td>
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.balance}"
																		property="customerInvoiceDetail[${ctr}].balance"
																		readOnly="true" />
																</td>
																<td>
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.amountToBeApplied}"
																		property="customerInvoiceDetail[${ctr}].amountToBeApplied" />
																</td>
																<td>
																	<input type='checkbox' name="fullApply"
																		value="${customerInvoiceDetail.sequenceNumber}">
																</td>
															</tr>
														</logic:iterate>

													</table>
												</td>
											</tr>
											<tr>
												<td style='text-align: right;' colspan='4'>
													<html:image property="methodToCall.apply"
														src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
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
