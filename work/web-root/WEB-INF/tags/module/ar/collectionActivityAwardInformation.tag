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
<c:set var="cgInvoiceDetail"
	value="${DataDictionary['ContractsGrantsInvoiceDetail'].attributes}" />
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

<kul:tab tabTitle="Award Information" defaultOpen="true" tabErrorKey="document.proposalNumber">
	<div class="tab-container" align="center">
		<h3>Award Information</h3>
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${contractsGrantsCollectionActivityAttributes.proposalNumber}" useShortLabel="false" horizontal="true" />
				<td>
					<kul:htmlControlAttribute readOnly="true"
						attributeEntry="${awardAttributes.proposalNumber}"
						property="document.proposalNumber" forceRequired="true" /> 
					<c:if test="${not readOnly}">
						<kul:lookup
							boClassName="org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward"
							fieldConversions="proposalNumber:document.proposalNumber" />
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
	</div>
</kul:tab>
<kul:tab tabTitle="Global Collection Event" defaultOpen="true"
	tabErrorKey="document.activityCode,document.activityDate,document.activityText,document.followupDate,document.completedDate">
	<div class="tab-container" align="center">
		<h3>New</h3>
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${collectionEventAttributes.activityCode}" horizontal="true"/>
				<td>
					<div id="document.activityCode.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityCode}"
									property="document.activityCode" readOnly="${readOnly}" />
								<c:if test="${not readOnly}">
									&nbsp;
									<kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.CollectionActivityType" fieldConversions="activityCode:document.activityCode" />
								</c:if>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${collectionEventAttributes.activityDate}" horizontal="true"/>
				<td>
					<div id="document.activityDate.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityDate}"
									property="document.activityDate" readOnly="${readOnly}" />				
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${collectionEventAttributes.activityText}" horizontal="true"/>
				<td>
					<div id="document.activityText.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.activityText}"
									property="document.activityText" readOnly="${readOnly}" expandedTextArea="true" />						
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${collectionEventAttributes.followupDate}" horizontal="true"/>
				<td>
					<div id="document.followupDate.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.followupDate}"
									property="document.followupDate" readOnly="${readOnly}" />						
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${collectionEventAttributes.completedDate}" horizontal="true"/>
				<td>
					<div id="document.completedDate.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${collectionEventAttributes.completedDate}"
									property="document.completedDate" readOnly="${readOnly}" />						
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
		</table>
	</div>
</kul:tab>
<kul:tab tabTitle="Edit List of Invoices" defaultOpen="true"
	tabErrorKey="selectedInvoiceDocumentNumberList">
	<div class="tab-container" align="center">
		<c:if test="${not empty KualiForm.document.proposalNumber}">
			<c:if test="${not readOnly}">
				<table width="100%" cellpadding="0" cellspacing="0"	class="datatable">
					<tr>
						<td colspan='2' class='tab-subhead'>New</td>
					</tr>
					<tr>
						<th colspan='2'>Look Up / Add Multiple Invoices
							<kul:multipleValueLookup lookedUpCollectionName="selectedInvoiceDocumentNumberList" boClassName="org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceLookup"
								lookupParameters="document.proposalNumber:proposalNumber,document.agencyNumber:agencyNumber,document.agencyName:agencyName,document.customerNumber:customerNumber,document.customerName:customerName"/>
						</th>
					</tr>
				</table>
			</c:if>
			<c:if test="${!empty KualiForm.document.invoiceDetails}">
				<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
					<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceDetails" id="Invoice">
						<ar:collectionActivityInvoiceDetail
							invPropertyName="document.invoiceDetails[${ctr}]"
							ctr="${ctr}" readOnly="${readOnly}" />
					</logic:iterate>
				</table>
			</c:if>							
		</c:if>
	</div>
</kul:tab>
