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

<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<kul:tab tabTitle="Apply to Invoice Detail" defaultOpen="true"
	tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.APPLY_TO_INVOICE_DETAIL_TAB}">
	<div class="tab-container" align="center">
	    <h3>Apply to Invoice Detail</h3>
        <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<th>
					Customer
				</th>
				<td>
					<kul:htmlControlAttribute
						readOnly="${readOnly}"
						attributeEntry="${customerAttributes.customerNumber}"
						property="selectedCustomerNumber" />
					<c:if test="${readOnly ne true}">
						<kul:lookup
							boClassName="org.kuali.kfs.module.ar.businessobject.Customer"
							fieldConversions="customerNumber:selectedCustomerNumber" />
					</c:if>
				</td>
			</tr>
			<tr>
				<th width='50%'>
					Invoice
				</th>
				<td>
					<kul:checkErrors keyMatch="${KFSConstants.PaymentApplicationTabErrorCodes.APPLY_TO_INVOICE_DETAIL_TAB}"/>
					<c:choose>						
						<c:when test="${empty KualiForm.enteredInvoiceDocumentNumber || hasErrors }">
							<kul:htmlControlAttribute
								readOnly="${readOnly}"
								attributeEntry="${invoiceAttributes.organizationInvoiceNumber}"
								property="enteredInvoiceDocumentNumber" />
						</c:when>
						<c:otherwise>
							<a href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.enteredInvoiceDocumentNumber}&command=displayDocSearchView" target="blank">
								<kul:htmlControlAttribute
									readOnly="true"
									attributeEntry="${invoiceAttributes.organizationInvoiceNumber}"
									property="enteredInvoiceDocumentNumber" />
							</a>
						</c:otherwise>	
					</c:choose>
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
				test="${empty KualiForm.selectedCustomerNumber}">
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
												<c:forEach items="${KualiForm.invoiceApplications}" var="invoiceApplication">
													<html:option value="${invoiceApplication.documentNumber}">
														<c:out value="${invoiceApplication.documentNumber}" />
													</html:option>
												</c:forEach>
											</html:select>
											<c:if test="${!empty invoiceApplications}">
												<logic:iterate id="invoiceApplication" name="KualiForm"
													property="invoiceApplications" indexId="ctr">
												</logic:iterate>
											</c:if>
											<html:image property="methodToCall.goToInvoice"
												src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
												alt="Go To Invoice" title="Go To Invoice"
												styleClass="tinybutton" />
										</c:if>
									</td>
								</tr>
								<tr>
									<th colspan='2' class='tab-subhead'>
										<c:out value="Invoice ${KualiForm.selectedInvoiceDocumentNumber}" />
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
													labelFor="selectedInvoiceApplication.accountsReceivableDocumentHeader.customer.customerName"
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
													<a href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.enteredInvoiceDocumentNumber}&command=displayDocSearchView" target="blank">
														<kul:htmlControlAttribute
															attributeEntry="${invoiceAttributes.organizationInvoiceNumber}"
															property="selectedInvoiceDocumentNumber" readOnly="true" />
													</a>
												</td>
												<td>
													<c:out value="${selectedInvoiceApplication.invoice.invoiceHeaderText}" />
												</td>
												<td style="text-align: right;">
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.openAmount}"
														property="selectedInvoiceBalance" readOnly="true" />
												</td>
												<td rowspan='2' style='vertical-align: top; text-align: right;'>
													<kul:htmlControlAttribute
														attributeEntry="${invoiceAttributes.openAmount}"
														property="amountAppliedDirectlyToInvoice" readOnly="true" />
												</td>
											</tr>
											<tr>
												<td>
													<c:out value="${selectedInvoiceApplication.invoice.billingDate}"/>
												</td>
												<td>
													<c:out value="${selectedInvoiceApplication.invoice.accountsReceivableDocumentHeader.customer.customerName}" />
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
														class='datatable' id="selectedCustomerInvoiceDetails">
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
														<c:if test="${!empty KualiForm.selectedInvoiceDetailApplications}">
														<logic:iterate id="invoiceDetailApplication" name="KualiForm"
															property="selectedInvoiceDetailApplications" indexId="ctr">
															<tr>
																<td>
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.sequenceNumber}"
																		property="selectedInvoiceDetailApplications[${ctr}].sequenceNumber"
																		readOnly="true" />
																</td>
																<td>
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.chartOfAccountsCode}"
																		property="selectedInvoiceDetailApplications[${ctr}].chartOfAccountsCode"
																		readOnly="true" />
																</td>
																<td>
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.accountNumber}"
																		property="selectedInvoiceDetailApplications[${ctr}].accountNumber"
																		readOnly="true" />
																</td>
																<td>
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}"
																		property="selectedInvoiceDetailApplications[${ctr}].invoiceItemDescription"
																		readOnly="true" />
																</td>
																<td style="text-align: right;">
																	<kul:htmlControlAttribute
																		attributeEntry="${customerInvoiceDetailAttributes.amount}"
																		property="selectedInvoiceDetailApplications[${ctr}].amount"
																		readOnly="true" />
																</td>
																<td style="text-align: right;">
																	<c:out value="${selectedInvoiceDetailApplication.amountOpen}"/>
																		
																</td>
																<td style="text-align: right;">
																	<kul:htmlControlAttribute
																		readOnly="${readOnly}"
																		styleClass="amount" 
																		disabled="${invoiceDetailApplication.fullApply || invoiceDetailApplication.invoiceQuickApplied}" 
																		attributeEntry="${customerInvoiceDetailAttributes.amountApplied}"
																		property="selectedInvoiceDetailApplications[${ctr}].amountApplied" />
																</td>
																<c:if test="${readOnly ne true}">
																	<td>
																		<center>
																			<kul:htmlControlAttribute
																				readOnly="${readOnly}"
																				disabled="${invoiceDetailApplication.invoiceQuickApplied}" 
																				attributeEntry="${customerInvoiceDetailAttributes.taxableIndicator}" 
																				property="selectedInvoiceDetailApplications[${ctr}].fullApply" />
																		</center>
																	</td>
																</c:if>
															</tr>
														</logic:iterate>
														</c:if>
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
