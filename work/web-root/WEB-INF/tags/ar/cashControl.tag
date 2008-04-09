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

<%@ attribute name="documentAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<c:set var="arDocHeaderAttributes"
	value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<html:hidden
	property="document.accountsReceivableDocumentHeader.documentNumber" />
<html:hidden
	property="document.accountsReceivableDocumentHeader.objectId" />
<html:hidden
	property="document.accountsReceivableDocumentHeader.versionNumber" />
<html:hidden
	property="document.accountsReceivableDocumentHeader.processingChartOfAccountCode" />
<html:hidden
	property="document.accountsReceivableDocumentHeader.processingOrganizationCode" />
<html:hidden property="hasGeneratedGLPEs" />
<html:hidden property="cashPaymentMediumSelected" />
<html:hidden property="documentSubmitted" />

<kul:tab tabTitle="General Info" defaultOpen="true"
	tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">
	<div class="tab-container" align=center>
		<div class="h2-container">
			<h2>
				General Info
			</h2>
		</div>

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
					attributeEntry="${documentAttributes.customerPaymentMediumCode}"
					horizontal="true" forceRequired="true" />

				<td class="datacell-nowrap">
					<c:choose>
						<c:when
							test="${not (KualiForm.hasGeneratedGLPEs or (KualiForm.documentSubmitted and KualiForm.cashPaymentMediumSelected))}">
							<kul:htmlControlAttribute
								attributeEntry="${documentAttributes.customerPaymentMediumCode}"
								property="document.customerPaymentMediumCode"
								readOnly="${KualiForm.hasGeneratedGLPEs or (KualiForm.documentSubmitted and KualiForm.cashPaymentMediumSelected)}"
								readOnlyAlternateDisplay="${document.customerPaymentMedium.customerPaymentMediumDescription}"
								onchange="submitForm()" />
						</c:when>
						<c:otherwise>
					 ${KualiForm.document.customerPaymentMedium.customerPaymentMediumDescription}
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
							readOnly="${KualiForm.documentSubmitted}" />
					</td>
				</tr>
			</c:if>

			<c:if
				test="${not KualiForm.hasGeneratedGLPEs and KualiForm.documentSubmitted and not KualiForm.cashPaymentMediumSelected}">
				<tr>
					<kul:htmlAttributeHeaderCell
						literalLabel="Generate General Ledger Pending Entries"
						horizontal="true" />
					<td class="datacell-nowrap">
						<html:image property="methodToCall.generateGLPEs"
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
							alt="Generate General Ledger Pending Entries"
							title="Generate General Ledger Pending Entries"
							styleClass="tinybutton" />
					</td>
				</tr>
			</c:if>
		</table>
	</div>
</kul:tab>
