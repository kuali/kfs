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
<%@ attribute name="hasRelatedCashControlDocument" required="true"
	description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>
<%@ attribute name="isCustomerSelected" required="true"
    description="Whether or not the customer is set" %>
<c:set var="docHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />

<kul:tab tabTitle="Summary of Applied Funds"
	defaultOpen="${isCustomerSelected}"
	tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">
	<div class="tab-container" align="center">
		<c:choose>
			<c:when test="${!isCustomerSelected}">
		    		No Customer Selected
	    	</c:when>
			<c:otherwise>
			    <h3>Summary of Applied Funds</h3>
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
													<c:out value="${invoicePaidApplied.invoiceItemNumber}"/>
												</td>
												<td>
													<c:out value="${invoicePaidApplied.invoiceDetail.invoiceItemDescription}" />&nbsp;
												</td>
												<td style="text-align: right;">
													$<c:out value="${invoicePaidApplied.invoiceItemAppliedAmount}" />
												</td>
											</tr>
										</logic:iterate>
									</table>
								</c:otherwise>
							</c:choose>
						</td>
						<td valign='top'>
                            <c:set var="showCCAndBtbA" value="${hasRelatedCashControlDocument}"/>
                            <table class='datatable'>
								<tr>
									<td colspan='3' class='tab-subhead'>
										Unapplied Funds
									</td>
								</tr>
								<tr>
									<c:if test="${!showCCAndBtbA}">
		                        	    <c:if test="${readOnly ne true}">
											<th class='tab-subhead'>
												Total Unapplied Funds
											</th>
											<th class='tab-subhead'>
												Open Amount
											</th>
										</c:if>
									</c:if>
									<c:if test="${showCCAndBtbA}">
										<th class='tab-subhead'>
											Cash Control
										</th>
										<th class='tab-subhead'>
											Open Amount
										</th>
									</c:if>
									<th class='tab-subhead'>
										Applied Amount
                                    </th>
								</tr>
								<tr>
								    <c:if test="${!showCCAndBtbA}">
		                        	    <c:if test="${readOnly ne true}">
											<td style="text-align: right;">
												${KualiForm.totalFromControl}
												<!--$<c:out value="${KualiForm.totalFromControl}" />-->
											</td>
											<td style="text-align: right;">
												${KualiForm.unallocatedBalance}												
												<!--$<c:out value="${KualiForm.unallocatedBalance}" />-->
											</td>
										</c:if>
									</c:if>
									<c:if test="${showCCAndBtbA}">
										<td style="text-align: right;">											 
											 ${KualiForm.document.documentHeader.financialDocumentTotalAmount}
										</td>
										<td style="text-align: right;">
											${KualiForm.unallocatedBalance}									
											<!--$<c:out value="${KualiForm.unallocatedBalance}" />-->
										</td>
									</c:if>
									<td style="text-align: right;">
										${KualiForm.totalApplied}								
										<!--$<c:out value="${KualiForm.totalApplied}" />-->
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
