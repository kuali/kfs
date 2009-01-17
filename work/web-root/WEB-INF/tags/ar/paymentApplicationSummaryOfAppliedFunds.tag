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
<%@ attribute name="isCustomerSelected" required="true"
    description="Whether or not the customer is set" %>

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
								<c:when test="${empty KualiForm.document.invoicePaidApplieds}">
								   		No applied payments.
							   	</c:when>
								<c:otherwise>
									<table width="100%" cellpadding="0" cellspacing="0"
										class="datatable">
										<tr>
											<td colspan='4' class='tab-subhead'>
												Applied Funds
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
										<logic:iterate id="invoicePaidApplied" name="KualiForm"
											property="document.invoicePaidApplieds" indexId="ctr">
											<tr>
												<td>
													<c:out value="${invoicePaidApplied.financialDocumentReferenceInvoiceNumber}" />
												</td>
												<td>
													<kul:htmlControlAttribute
														attributeEntry="${invoicePaidAppliedAttributes.invoiceItemNumber}"
														property="document.invoicePaidApplied[${ctr}].invoiceItemNumber"
														readOnly="true" />
												</td>
												<td>
													<c:out value="${invoicePaidApplied.invoiceItem.financialDocumentLineDescription}" />&nbsp;
												</td>
												<td>
													$<c:out value="${invoicePaidApplied.invoiceItemAppliedAmount}" />
												</td>
											</tr>
										</logic:iterate>
									</table>
								</c:otherwise>
							</c:choose>
						</td>
						<td valign='top'>
                            <c:set var="showTUFAndBtbA"
                               value="${!hasRelatedCashControlDocument and (0 lt KualiForm.document.totalUnapplied or 0 lt KualiForm.document.nonInvoicedTotalAmount)}" />
                            <c:set var="showCCAndBtbA" value="${hasRelatedCashControlDocument}"/>
                            <table class='datatable'>
								<tr>
									<c:if test="${showTUFAndBtbA}">
										<th class='tab-subhead'>
											Total Unapplied Funds
										</th>
										<th class='tab-subhead'>
											Balance to be Applied
										</th>
									</c:if>
									<c:if test="${showCCAndBtbA}">
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
									<c:if test="${showTUFAndBtbA}">
										<td>
											$<c:out value="${KualiForm.document.totalUnapplied}" />
										</td>
										<td>
											$<c:out value="${KualiForm.document.totalBalanceToBeApplied}" />
										</td>
									</c:if>
									<c:if test="${showCCAndBtbA}">
										<td>
											$<c:out value="${KualiForm.document.documentHeader.financialDocumentTotalAmount}" />
										</td>
										<td>
											$<c:out value="${KualiForm.document.unallocatedBalance}" />
										</td>
									</c:if>
									<td>
										$<c:out value="${KualiForm.document.totalApplied}" />
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
