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

<script type='text/javascript'
	src="dwr/interface/CollectionActivityDocumentService.js"></script>
<script language="JavaScript" type="text/javascript"
	src="scripts/module/ar/awardObjectInfo.js"></script>

<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>

<c:set var="customerAttributes"
	value="${DataDictionary['Customer'].attributes}" />
<c:set var="customerInvoiceDetailAttributes"
	value="${DataDictionary['CustomerInvoiceDetail'].attributes}" />
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
<c:set var="eventAttributes"
	value="${DataDictionary['Event'].attributes}" />

<kul:tab tabTitle="Award Information" defaultOpen="true"
	tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.APPLY_TO_INVOICE_DETAIL_TAB}">
	<div class="tab-container" align="center">
		<h3>Award Information</h3>
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					literalLabel="Proposal Number" horizontal="true" />
				<td><c:choose>
						<c:when test="${!readOnly}">
							<c:set var="onblurForInvoice" value="loadAwardInfo( this.name);" />
						</c:when>
						<c:otherwise>
							<c:set var="onblurForInvoice" value="" />
						</c:otherwise>
					</c:choose> <kul:htmlControlAttribute readOnly="true"
						attributeEntry="${awardAttributes.proposalNumber}"
						property="selectedProposalNumber" forceRequired="true" /> <c:if
						test="${readOnly ne true}">
						<kul:lookup
							boClassName="org.kuali.kfs.module.cg.businessobject.Award"
							fieldConversions="proposalNumber:selectedProposalNumber" />
					</c:if></td>
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

			<c:if test="${readOnly ne true}">
				<tr>
					<td colspan='2'>
						<center>
							<html:image property="methodToCall.loadInvoices"
								src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
								alt="Load Invoices" title="Load Invoices"
								styleClass="tinybutton" />
						</center>
					</td>
				</tr>
			</c:if>
		</table>


		<c:choose>
			<c:when
				test="${empty KualiForm.selectedProposalNumber || KualiForm.methodToCall == 'refresh'}">
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${empty KualiForm.selectedInvoiceApplication}">
						<div>
							<br /> No Invoices Found
						</div>
					</c:when>
					<c:otherwise>
							<table>
			<tr>
				<td colspan='2' class='tab-subhead'><label
					for="selectedInvoiceDocumentNumber">Global Events</label></td>
			</tr>
			<tr>
				<c:choose>
					<c:when
						test="${empty KualiForm.selectedProposalNumber || KualiForm.methodToCall == 'refresh'}">
					</c:when>
					<c:otherwise>
						<td colspan='2'>
							<table width='100%' cellpadding='0' cellspacing='0'
								class='datatable' id="selectedInvoiceEventDetails">
								<tr>
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.eventCode}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.activityCode}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.activityDate}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.activityText}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.followupInd}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.followupDate}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.completedInd}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.completedDate}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.postedDate}" />
									<kul:htmlAttributeHeaderCell
										attributeEntry="${eventAttributes.userPrincipalId}" />
									<kul:htmlAttributeHeaderCell literalLabel="Invoices Selected" />
									<kul:htmlAttributeHeaderCell literalLabel="Actions" />
								</tr>
								<tr>
									<td></td>
								</tr>
								<c:if test="${true}">
									<ar:globalEventDetails propertyName="document.globalEvent"
										eventAttributes="${eventAttributes}" addLine="true"
										readOnly="false" rowHeading="add" cssClass="infoline"
										actionMethod="addGlobalEvent"
										actionAlt="Add"
										actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" />
								</c:if>
							</table>
						</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</table>

						<table width="100%" cellpadding="0" cellspacing="0"
							class="datatable">
							<tr id='beta_zeta'>
								<td>
									<table width="100%" cellpadding="0" cellspacing="0"
										class="datatable">
										<tr>
											<td colspan='2' class='tab-subhead'><label
												for="selectedInvoiceDocumentNumber">Invoices</label> <c:if
													test="${readOnly ne true}">
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
												</c:if></td>
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
														<th>Invoice Number</th>
														<th>Account Number</th>
														<th>Invoice Date</th>
														<th>Billing Period</th>
														<th>Invoice Amount</th>
														<th>Billing Frequency</th>
														<th>Payment Amount</th>
														<th>Last Payment Date</th>
														<th>Balance Due</th>
														<th>Age</th>
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
																attributeEntry="${invoiceGeneralDetailAttributes.billingFrequency}"
																property="selectedInvoiceApplication.invoiceGeneralDetail.billingFrequency"
																readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${cgInvoiceAttributes.paymentAmount}"
																property="selectedInvoicePaymentAmount" readOnly="true" /></td>
														<td><kul:htmlControlAttribute
																attributeEntry="${cgInvoiceAttributes.paymentDate}"
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
												for="selectedInvoiceDocumentNumber">Events</label></td>
										</tr>
										<tr>
											<c:choose>
												<c:when
													test="${!KualiForm.document.selectedInvoiceApplication.showEventsInd}">
													<td colspan='2'>Events can not be displayed.</td>
												</c:when>
												<c:otherwise>
													<td colspan='2'>
														<table width='100%' cellpadding='0' cellspacing='0'
															class='datatable' id="selectedInvoiceEventDetails">
															<tr>
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.eventCode}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.activityCode}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.activityDate}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.activityText}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.followupInd}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.followupDate}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.completed}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.completedDate}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.postedDate}" />
																<kul:htmlAttributeHeaderCell
																	attributeEntry="${eventAttributes.userPrincipalId}" />
																<kul:htmlAttributeHeaderCell literalLabel="Actions" />
															</tr>
															<c:if
																test="${!empty KualiForm.selectedInvoiceApplication}">
																<ar:eventDetails propertyName="document.newEvent"
																	eventAttributes="${eventAttributes}" addLine="true"
																	readOnly="${readOnly}" rowHeading="add"
																	cssClass="infoline" actionMethod="addEvent"
																	actionAlt="Add Event"
																	actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" />
																<logic:iterate id="event" name="KualiForm"
																	property="document.selectedInvoiceEvents" indexId="ctr">
																	<ar:eventDetails
																		propertyName="document.selectedInvoiceEvents[${ctr}]"
																		eventAttributes="${eventAttributes}" addLine="false"
																		readOnly="${readOnly}" rowHeading="${ctr+1}"
																		cssClass="datacell"
																		actionMethod="editEvent.line${ctr}"
																		actionAlt="Edit Event"
																		actionImage="${ConfigProperties.externalizable.images.url}tinybutton-save.gif" />
																</logic:iterate>
															</c:if>
														</table>
													</td>
												</c:otherwise>
											</c:choose>
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
	<SCRIPT type="text/javascript">
		var kualiForm = document.forms['KualiForm'];
		var kualiElements = kualiForm.elements;
	</SCRIPT>
</kul:tab>
