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

<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>

<c:set var="arGenericAttributes"
	value="${DataDictionary['ArGenericAttributes'].attributes}" />
<c:set var="customerAttributes"
	value="${DataDictionary['Customer'].attributes}" />
<c:set var="cgInvoiceAttributes"
	value="${DataDictionary['ContractsGrantsInvoiceDocument'].attributes}" />
<c:set var="invoiceAccountDetailAttributes"
	value="${DataDictionary['InvoiceAccountDetail'].attributes}" />
<c:set var="invoiceGeneralDetailAttributes"
	value="${DataDictionary['InvoiceGeneralDetail'].attributes}" />
<c:set var="awardAttributes"
	value="${DataDictionary['Award'].attributes}" />
<c:set var="agencyAttributes"
	value="${DataDictionary['Agency'].attributes}" />
<c:set var="collectionEventAttributes"
	value="${DataDictionary['CollectionEvent'].attributes}" />
<c:set var="contractsGrantsCollectionActivityAttributes"
	value="${DataDictionary.CollectionActivityDocument.attributes}"/>

<kul:tab tabTitle="Award Information" defaultOpen="true"
	tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.APPLY_TO_INVOICE_DETAIL_TAB}">
	<div class="tab-container" align="center">
		<h3>Award Information</h3>
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${contractsGrantsCollectionActivityAttributes.proposalNumber}" useShortLabel="false" horizontal="true" />
				<td>
					<kul:htmlControlAttribute readOnly="true"
						attributeEntry="${awardAttributes.proposalNumber}"
						property="selectedProposalNumber" forceRequired="true" /> 
					<c:if test="${not readOnly}">
						<kul:lookup
							boClassName="org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward"
							fieldConversions="proposalNumber:selectedProposalNumber" />
					</c:if>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Agency Number"
					horizontal="true" />
				<td>
					<div id="document.agencyNumber.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${awardAttributes.agencyNumber}"
							property="document.agencyNumber" />
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Agency Name"
					horizontal="true" />
				<td>
					<div id="document.agencyName.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${agencyAttributes.fullName}"
							property="document.agencyName" />
					</div>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Customer Number"
					horizontal="true" />
				<td>
					<div id="document.customerNumber.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${customerAttributes.customerNumber}"
							property="document.customerNumber" />
					</div>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Customer Name"
					horizontal="true" />
				<td>
					<div id="document.customerName.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${customerAttributes.customerName}"
							property="document.customerName" />
					</div>
				</td>
			</tr>
		</table>

		<c:choose>
			<c:when
				test="${empty KualiForm.selectedProposalNumber}">
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${empty KualiForm.selectedInvoiceApplication}">
						<div>
							<br /> No Invoices Found
						</div>
					</c:when>
					<c:otherwise>
						<c:if test="${not readOnly}">
							<table>
								<tr>
									<td colspan='2' class='tab-subhead'><label
										for="selectedInvoiceDocumentNumber">Global Collection Events</label></td>
								</tr>
								<tr>
									<c:if test="${not empty KualiForm.selectedProposalNumber}">
										<td colspan='2'>
											<table width='100%' cellpadding='0' cellspacing='0'
												class='datatable' id="selectedInvoiceCollectionEventDetails">
												<tr>
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.collectionEventCode}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.activityCode}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.activityDate}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.activityText}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.followup}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.followupDate}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.completed}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.completedDate}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.postedDate}" />
													<kul:htmlAttributeHeaderCell
														attributeEntry="${collectionEventAttributes.userPrincipalId}" />
													<kul:htmlAttributeHeaderCell literalLabel="Invoices Selected" />
													<kul:htmlAttributeHeaderCell literalLabel="Actions" />
												</tr>
												<tr>
													<td></td>
												</tr>
												<ar:collectionEventDetails propertyName="document.globalCollectionEvent"
													collectionEventAttributes="${collectionEventAttributes}" addLine="true"
													readOnly="${readOnly}" rowHeading="add" cssClass="infoline"
													actionMethod="addGlobalCollectionEvent"
													actionAlt="Add"
													actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
													includeMultipleInvoiceLookup="true" />
											</table>
										</td>
									</c:if>
								</tr>
							</table>
						</c:if>
						<table width="100%" cellpadding="0" cellspacing="0"
							class="datatable">
							<tr id='beta_zeta'>
								<td>
									<table width="100%" cellpadding="0" cellspacing="0"
										class="datatable">
										<tr>
											<td colspan='2' class='tab-subhead'><label
												for="selectedInvoiceDocumentNumber">Invoices</label>
												<c:if test="${not readOnly}">
													<html:select property="selectedInvoiceDocumentNumber">
														<c:forEach items="${KualiForm.cgInvoices}"
															var="invoiceApplication">
															<html:option value="${invoiceApplication.documentNumber}">
																<c:out value="${invoiceApplication.documentNumber}" />
															</html:option>
														</c:forEach>
													</html:select>
													<c:if test="${!empty cgInvoices}">
														<logic:iterate id="invoiceApplication" name="KualiForm"
															property="cgInvoices" indexId="ctr">
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
											<th colspan='2' class='tab-subhead'><c:out
													value="Invoice ${KualiForm.selectedInvoiceDocumentNumber}" />
												&nbsp; <c:if
													test="${!empty KualiForm.previousInvoiceDocumentNumber}">
													<html:image property="methodToCall.goToPreviousInvoice"
														src="${ConfigProperties.externalizable.images.url}tinybutton-prev.gif"
														alt="Go To Previous Invoice"
														title="Go To Previous Invoice" styleClass="tinybutton" />
												</c:if> <c:if
													test="${!empty KualiForm.previousInvoiceDocumentNumber && !empty KualiForm.nextInvoiceDocumentNumber}">|</c:if>
												<c:if test="${!empty KualiForm.nextInvoiceDocumentNumber}">
													<html:image property="methodToCall.goToNextInvoice"
														src="${ConfigProperties.externalizable.images.url}tinybutton-next.gif"
														alt="Go To Next Invoice" title="Go To Next Invoice"
														styleClass="tinybutton" />
												</c:if></th>
										</tr>
										<tr>
											<td colspan='2'>
												<table width='100%' cellpadding='0' cellspacing='0'
													class='datatable'>
													<tr>
														<th><kul:htmlAttributeLabel attributeEntry="${cgInvoiceAttributes.organizationInvoiceNumber}" labelFor="document.organizationInvoiceNumber" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${invoiceAccountDetailAttributes.accountNumber}" labelFor="document.invoiceAccountDetailAttributes.accountNumber" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${cgInvoiceAttributes.billingDate}" labelFor="document.billingDate" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.billingPeriod}" labelFor="document.invoiceGeneralDetail.billingPeriod" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${cgInvoiceAttributes.sourceTotal}" labelFor="document.sourceTotal" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${invoiceGeneralDetailAttributes.billingFrequencyCode}" labelFor="invoiceGeneralDetail.billingFrequencyCode" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${cgInvoiceAttributes.paymentAmount}" labelFor="document.paymentAmount" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${arGenericAttributes.paymentDate}" labelFor="document.paymentDate" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${cgInvoiceAttributes.balanceDue}" labelFor="document.balanceDue" /></th>
														<th><kul:htmlAttributeLabel attributeEntry="${cgInvoiceAttributes.age}" labelFor="document.age" /></th>
													</tr>
													<tr>
														<td><a
															href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.selectedInvoiceDocumentNumber}&command=displayDocSearchView"
															target="blank"> <kul:htmlControlAttribute
																	attributeEntry="${cgInvoiceAttributes.organizationInvoiceNumber}"
																	property="selectedInvoiceDocumentNumber"
																	readOnly="true" />
														</a></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${invoiceAccountDetailAttributes.accountNumber}"
																property="selectedInvoiceApplication.accountDetails[0].accountNumber"
																readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${cgInvoiceAttributes.billingDate}"
																property="selectedInvoiceApplication.billingDate"
																readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${invoiceGeneralDetailAttributes.billingPeriod}"
																property="selectedInvoiceApplication.invoiceGeneralDetail.billingPeriod"
																readOnly="true" /></td>
														<td style="text-align: right;"><kul:htmlControlAttribute
																attributeEntry="${cgInvoiceAttributes.sourceTotal}"
																property="selectedInvoiceTotalAmount" readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${invoiceGeneralDetailAttributes.billingFrequencyCode}"
																property="selectedInvoiceApplication.invoiceGeneralDetail.billingFrequencyCode"
																readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${cgInvoiceAttributes.paymentAmount}"
																property="selectedInvoicePaymentAmount" readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${arGenericAttributes.paymentDate}"
																property="selectedInvoicePaymentDate" readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${cgInvoiceAttributes.balanceDue}"
																property="selectedInvoiceBalanceDue" readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${cgInvoiceAttributes.age}"
																property="selectedInvoiceApplication.age"
																readOnly="true" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td colspan='2' class='tab-subhead'><label
												for="selectedInvoiceDocumentNumber">Collection Events</label></td>
										</tr>
										<tr>
											<td colspan='2'>
												<table width='100%' cellpadding='0' cellspacing='0'
													class='datatable' id="selectedInvoiceCollectionEventDetails">
													<tr>
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.collectionEventCode}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.activityCode}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.activityDate}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.activityText}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.followup}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.followupDate}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.completed}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.completedDate}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.postedDate}" />
														<kul:htmlAttributeHeaderCell
															attributeEntry="${collectionEventAttributes.userPrincipalId}" />
														<kul:htmlAttributeHeaderCell literalLabel="Actions" />
													</tr>
													<c:if test="${!empty KualiForm.selectedInvoiceApplication}">
														<c:if test="${not readOnly}">
															<ar:collectionEventDetails propertyName="document.newCollectionEvent"
																collectionEventAttributes="${collectionEventAttributes}" addLine="true"
																readOnly="${readOnly}" rowHeading="add"
																cssClass="infoline" actionMethod="addCollectionEvent"
																actionAlt="Add Collection Event"
																actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" />
														</c:if>
														<logic:iterate id="collectionEvent" name="KualiForm"
															property="document.selectedInvoiceEvents" indexId="ctr">
															<ar:collectionEventDetails
																propertyName="document.selectedInvoiceEvents[${ctr}]"
																collectionEventAttributes="${collectionEventAttributes}" addLine="false"
																readOnly="${readOnly}" rowHeading="${ctr+1}"
																cssClass="datacell"
																actionMethod="editCollectionEvent.line${ctr}"
																actionAlt="Edit Collection Event"
																actionImage="${ConfigProperties.externalizable.images.url}tinybutton-save.gif" />
														</logic:iterate>
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
			</c:otherwise>
		</c:choose>
	</div>
</kul:tab>
