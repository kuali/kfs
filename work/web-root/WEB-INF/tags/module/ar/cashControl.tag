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

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.bankCode}"
					horizontal="true" forceRequired="true" labelFor="document.bankCode" />

				<td class="datacell-nowrap">
					<c:choose>
						<c:when test="${editBankCode}">
							<kul:htmlControlAttribute
								attributeEntry="${documentAttributes.bankCode}"
								property="document.bankCode"
								forceRequired="true" />
						</c:when>
						<c:otherwise>
							<c:out value="${KualiForm.document.bankCode}" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>

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
