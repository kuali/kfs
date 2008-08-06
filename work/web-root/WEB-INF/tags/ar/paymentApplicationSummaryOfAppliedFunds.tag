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
<%@ attribute name="hasRelatedCashControlDocument" required="true"
	description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>

<c:set var="isCustomerSelected"
	value="${!empty KualiForm.document.accountsReceivableDocumentHeader.customerNumber}" />

<kul:tab tabTitle="Summary of Applied Funds"
	defaultOpen="${isCustomerSelected}"
	tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">
	<div class="tab-container" align="center">
		<c:choose>
			<c:when test="${!isCustomerSelected}">
		    		No Customer Selected
		    	</c:when>
			<c:otherwise>
				<table width="100%" cellpadding="0" cellspacing="0"
					class="datatable">
					<tr>
						<td style='vertical-align: top;' colspan='2'>
							<c:choose>
								<c:when test="${empty KualiForm.document.appliedPayments}">
								   		No applied payments.
								   	</c:when>
								<c:otherwise>
									<table width="100%" cellpadding="0" cellspacing="0"
										class="datatable">
										<tr>
											<td colspan='4' class='tab-subhead'>
												Unapplied Funds
											</td>
										</tr>
										<tr>
											<th>
												Invoice Nbr
											</th>
											<th>
												Item #
											</th>
											<th>
												Inv Item Desc
											</th>
											<th>
												Applied Amount
											</th>
										</tr>
										<logic:iterate id="appliedPayment" name="KualiForm"
											property="document.appliedPayments" indexId="ctr">


											<tr>
												<html:hidden
													property="document.appliedPayment[${ctr}].documentNumber" />
												<html:hidden
													property="document.appliedPayment[${ctr}].versionNumber" />
												<html:hidden
													property="document.appliedPayment[${ctr}].objectId" />
												<html:hidden
													property="document.appliedPayment[${ctr}].paidAppliedItemNumber" />
												<html:hidden
													property="document.appliedPayment[${ctr}].universityFiscalYear" />
												<html:hidden
													property="document.appliedPayment[${ctr}].universityFiscalPeriodCode" />
												<td>
													<c:out
														value="${appliedPayment.financialDocumentReferenceInvoiceNumber}" />
													<html:hidden
														property="document.appliedPayment[${ctr}].financialDocumentReferenceInvoiceNumber" />
												</td>
												<td>
													<kul:htmlControlAttribute
														attributeEntry="${invoicePaidAppliedAttributes.invoiceItemNumber}"
														property="document.appliedPayment[${ctr}].invoiceItemNumber"
														readOnly="true" />
												</td>
												<td>
													<c:out
														value="${appliedPayment.invoiceItem.financialDocumentLineDescription}" />
													<html:hidden
														property="document.appliedPayment[${ctr}].invoiceItem.financialDocumentLineDescription" />
												</td>
												<td>
													$
													<c:out value="${appliedPayment.invoiceItemAppliedAmount}" />
													<html:hidden
														property="document.appliedPayment[${ctr}].invoiceItemAppliedAmount" />
												</td>
											</tr>
										</logic:iterate>

									</table>
								</c:otherwise>
							</c:choose>
						</td>
						<td valign='top'>
							<table class='datatable'>
								<tr>
									<c:if
										test="${!hasRelatedCashControlDocument and 0 lt KualiForm.document.totalUnappliedFunds}">
										<th class='tab-subhead'>
											Total Unapplied Funds
										</th>
										<th class='tab-subhead'>
											Balance to be Applied
										</th>
									</c:if>
									<c:if test="${hasRelatedCashControlDocument}">
										<th class='tab-subhead'>
											Cash Control
										</th>
										<th class='tab-subhead'>
											Balance to be Applied
										</th>
									</c:if>
									<th class='tab-subhead'>
										Applied Amount
									</th>
								</tr>
								<tr>
									<c:if
										test="${!hasRelatedCashControlDocument and 0 lt KualiForm.document.totalUnappliedFunds}">
										<td>
											$
											<c:out value="${KualiForm.document.totalUnappliedFunds}" />
										</td>
										<td>
											$
											<c:out
												value="${KualiForm.document.totalUnappliedFundsToBeApplied}" />
										</td>
									</c:if>
									<c:if test="${hasRelatedCashControlDocument}">
										<td>
											$
											<c:out
												value="${KualiForm.document.documentHeader.financialDocumentTotalAmount}" />
										</td>
										<td>
											$
											<c:out value="${KualiForm.document.totalToBeApplied}" />
										</td>
									</c:if>
									<td>
										$
										<c:out value="${KualiForm.document.totalAppliedAmount}" />
									</td>
								</tr>
							</table>
						<td>
					</tr>
				</table>
			</c:otherwise>
		</c:choose>
	</div>
</kul:tab>
