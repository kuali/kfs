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
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">
	<kul:tab tabTitle="Transmission Details" defaultOpen="true" tabErrorKey="document.invoiceAddressDetails*">
		<c:set var="invoiceAddressDetailsAttributes" value="${DataDictionary.InvoiceAddressDetail.attributes}" />
		
		<div class="tab-container" align="center">
			<h3>Transmission Details</h3>
			<table cellpadding=0 class="datatable" summary="Transmission Details section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.customerAddressTypeCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.customerAddressName}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell literalLabel="Address" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.customerInvoiceTemplateCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.invoiceTransmissionMethodCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.customerEmailAddress}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.initialTransmissionDate}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell literalLabel="Actions" />
				</tr>
				
				<c:forEach var="invoiceAddressDetail" items="${KualiForm.document.invoiceAddressDetails}" varStatus="loopCounter" >
    				<tr>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerAddressTypeCode}"
								property="document.invoiceAddressDetails[${loopCounter.index}].customerAddressTypeCode" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerAddressName}"
								property="document.invoiceAddressDetails[${loopCounter.index}].customerAddressName" readOnly="true" /></td>
						<td class="datacell">
							${invoiceAddressDetail.customerLine1StreetAddress}<br/>
							<c:if test="${not empty invoiceAddressDetail.customerLine2StreetAddress}">${invoiceAddressDetail.customerLine2StreetAddress}<br/></c:if>
							<c:if test="${not empty invoiceAddressDetail.customerCityName}">${invoiceAddressDetail.customerCityName},</c:if>
							<c:if test="${not empty invoiceAddressDetail.customerStateCode || not empty invoiceAddressDetail.customerZipCode}">${invoiceAddressDetail.customerStateCode}, ${invoiceAddressDetail.customerZipCode}<br/></c:if>
							<c:if test="${not empty invoiceAddressDetail.customerAddressInternationalProvinceName || not empty invoiceAddressDetail.customerInternationalMailCode}">${invoiceAddressDetail.customerAddressInternationalProvinceName}, ${invoiceAddressDetail.customerInternationalMailCode}<br/></c:if>
							<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerCountryCode}"	property="document.invoiceAddressDetails[${loopCounter.index}].customerCountryCode" readOnly="true" />
						</td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerInvoiceTemplateCode}"
								property="document.invoiceAddressDetails[${loopCounter.index}].customerInvoiceTemplateCode" readOnly="${readOnly}" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.invoiceTransmissionMethodCode}"
								property="document.invoiceAddressDetails[${loopCounter.index}].invoiceTransmissionMethodCode" readOnly="${readOnly}"/></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerEmailAddress}"
								property="document.invoiceAddressDetails[${loopCounter.index}].customerEmailAddress" readOnly="${readOnly}" forceRequired="true"/></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.initialTransmissionDate}"
								property="document.invoiceAddressDetails[${loopCounter.index}].initialTransmissionDate" readOnly="true"/></td>
						<c:choose>
							<c:when test="${KualiForm.showTransmissionDateButton}">
								<c:choose>
									<c:when test="${empty invoiceAddressDetail.initialTransmissionDate}">
										<td class="datacell-nowrap">
											<html:image property="methodToCall.setInitialTransmissionDate.line${loopCounter.index}"
												src="${ConfigProperties.externalizable.images.url}tinybutton-settransmissiondate.gif"
												alt="Set Transmission Date"
												title="Set Transmission Date"
												styleClass="tinybutton" />
										</td>
									</c:when>
									<c:otherwise>
										<td class="datacell-nowrap">
											<html:image property="methodToCall.clearInitialTransmissionDate.line${loopCounter.index}"
												src="${ConfigProperties.externalizable.images.url}tinybutton-cleartransmissiondate.gif"
												alt="Clear Transmission Date"
												title="Clear Transmission Date"
												styleClass="tinybutton" />
										</td>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<td>&nbsp;</td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</table>
		</div>
	</kul:tab>
</c:if>
