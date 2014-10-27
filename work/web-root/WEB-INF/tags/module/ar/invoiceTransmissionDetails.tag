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
							<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerLine1StreetAddress}"
								property="document.invoiceAddressDetails[${loopCounter.index}].customerLine1StreetAddress" readOnly="true" /><br/>
							<c:if test="${not empty invoiceAddressDetail.customerLine2StreetAddress}">
								<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerLine2StreetAddress}"
									property="document.invoiceAddressDetails[${loopCounter.index}].customerLine2StreetAddress" readOnly="true" /><br/>
							</c:if>
							<c:if test="${not empty invoiceAddressDetail.customerCityName || not empty invoiceAddressDetail.customerStateCode || not empty invoiceAddressDetail.customerZipCode}">
								<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerCityName}"
									property="document.invoiceAddressDetails[${loopCounter.index}].customerCityName" readOnly="true" />, 
								<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerStateCode}"
									property="document.invoiceAddressDetails[${loopCounter.index}].customerStateCode" readOnly="true" />,
								<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerZipCode}"
									property="document.invoiceAddressDetails[${loopCounter.index}].customerZipCode" readOnly="true" /><br/>								 
							</c:if>
							<c:if test="${not empty invoiceAddressDetail.customerAddressInternationalProvinceName || not empty invoiceAddressDetail.customerInternationalMailCode}">
								<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerAddressInternationalProvinceName}"
									property="document.invoiceAddressDetails[${loopCounter.index}].customerAddressInternationalProvinceName" readOnly="true" />, 
								<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerInternationalMailCode}"
									property="document.invoiceAddressDetails[${loopCounter.index}].customerInternationalMailCode" readOnly="true" /><br/>
							</c:if>
							<kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerCountryCode}"
								property="document.invoiceAddressDetails[${loopCounter.index}].customerCountryCode" readOnly="true" />
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
