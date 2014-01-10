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

<%@ attribute name="documentAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>
<%@ attribute name="showGenerateButton" required="true"
	description="If document generate button is in view mode"%>
<%@ attribute name="editPaymentMedium" required="true"
	description="If document edit medium is in edit mode"%>
<%@ attribute name="editRefDocNbr" required="true"
	description="If document reference document number is in edit mode"%>
<%@ attribute name="editBankCode" required="true"
	description="If document bank code is in edit mode"%>
<%@ attribute name="showBankCode" required="false"
	description="If document bank code is in edit mode"%>

<c:set var="arDocHeaderAttributes"
	value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<kul:tab tabTitle="General Info" defaultOpen="true"
	tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">
	<div class="tab-container" align=center>
			<h3>General Info</h3>
		<table cellpadding="0" cellspacing="0" summary="General Info">

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${arDocHeaderAttributes.processingChartOfAccCodeAndOrgCode}"
					horizontal="true" width="50%" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${arDocHeaderAttributes.processingChartOfAccCodeAndOrgCode}"
						property="processingChartOfAccCodeAndOrgCode" readOnly="true" />
				</td>
			</tr>
			<c:if test="${showBankCode}" >
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.bankCode}"
					horizontal="true" forceRequired="true" labelFor="document.bankCode" />

				<c:choose>
					<c:when test="${editBankCode}">
					<sys:bankControl property="document.bankCode" objectProperty="document.bank" 
									 depositOnly="false" disbursementOnly="false" 
									 readOnly="${readOnly}" style="datacell-nowrap" />
					</c:when>
					<c:otherwise>
					<td class="datacell-nowrap">
						<c:out value="${KualiForm.document.bankCode}" />
					</td>
					</c:otherwise>
				</c:choose>
			</tr>
			</c:if>

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.customerPaymentMediumCode}"
					horizontal="true" forceRequired="true" labelFor="document.customerPaymentMediumCode" />

				<td class="datacell-nowrap">
					<c:choose>
						<c:when test="${editPaymentMedium}">
							<kul:htmlControlAttribute
								attributeEntry="${documentAttributes.customerPaymentMediumCode}"
								property="document.customerPaymentMediumCode"
								onchange="submitForm()" forceRequired="true" />
						</c:when>
						<c:otherwise>
							<c:out value="${KualiForm.document.customerPaymentMedium.customerPaymentMediumDescription}" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.invoiceDocumentType}"
					horizontal="true" forceRequired="true"
					labelFor="document.invoiceDocumentType" useShortLabel="false" />
				<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${documentAttributes.invoiceDocumentType}"
						property="document.invoiceDocumentType" onchange="submitForm()"
						forceRequired="true" /></td>
			</tr>
			<c:choose>
				<c:when
					test="${!empty KualiForm.document.invoiceDocumentType && KualiForm.document.invoiceDocumentType == 'CINV'}">

					<tr>
						<kul:htmlAttributeHeaderCell
							attributeEntry="${documentAttributes.letterOfCreditCreationType}"
							horizontal="true" labelFor="document.letterOfCreditCreationType"
							useShortLabel="false" />
						<td class="datacell-nowrap"><kul:htmlControlAttribute
								attributeEntry="${documentAttributes.letterOfCreditCreationType}"
								property="document.letterOfCreditCreationType" onchange="submitForm()" />

						</td>
					</tr>

					<c:choose>
						<c:when
							test="${!empty KualiForm.document.letterOfCreditCreationType && KualiForm.document.letterOfCreditCreationType == CGConstants.LOC_BY_LOC_FUND}">

							<tr>
								<kul:htmlAttributeHeaderCell
									attributeEntry="${documentAttributes.letterOfCreditFundCode}"
									horizontal="true" labelFor="document.letterOfCreditFundCode"
									useShortLabel="false" />
								<td class="datacell-nowrap"><kul:htmlControlAttribute
										attributeEntry="${documentAttributes.letterOfCreditFundCode}"
										property="document.letterOfCreditFundCode" /></td>
							</tr>
						</c:when>
						<c:when
							test="${!empty KualiForm.document.letterOfCreditCreationType && KualiForm.document.letterOfCreditCreationType == CGConstants.LOC_BY_LOC_FUND_GRP}">


							<tr>
								<kul:htmlAttributeHeaderCell
									attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}"
									horizontal="true"
									labelFor="document.letterOfCreditFundGroupCode"
									useShortLabel="false" />
								<td class="datacell-nowrap"><kul:htmlControlAttribute
										attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}"
										property="document.letterOfCreditFundGroupCode" />

								</td>
							</tr>
						</c:when>
					</c:choose>
				</c:when>
			</c:choose>

			<c:if test="${KualiForm.cashPaymentMediumSelected}">
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${documentAttributes.referenceFinancialDocumentNumber}"
						horizontal="true" />

					<td class="datacell-nowrap">
						<kul:htmlControlAttribute
							attributeEntry="${documentAttributes.referenceFinancialDocumentNumber}"
							property="document.referenceFinancialDocumentNumber"
							readOnly="${not editRefDocNbr}" forceRequired="true" />
					</td>
				</tr>
			</c:if>

			<c:if test="${showGenerateButton}">
				<tr>
					<kul:htmlAttributeHeaderCell
						literalLabel="Generate General Ledger Pending Entries:"
						horizontal="true" />
					<td class="datacell-nowrap">
						<html:image property="methodToCall.generateGLPEs"
							src="${ConfigProperties.externalizable.images.url}tinybutton-generate.gif"
							alt="Generate General Ledger Pending Entries"
							title="Generate General Ledger Pending Entries"
							styleClass="tinybutton" />
					</td>
				</tr>
			</c:if>
		</table>
	</div>
</kul:tab>
