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
<%@ attribute name="assetPayments" type="java.util.List" required="true" description="Asset payments list" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPayment.attributes}" />
<c:set var="pos" value="-1" />
<kul:tab tabTitle="Asset Payments" defaultOpen="${!defaultTabHide}">
	<c:forEach var="payment" items="${assetPayments}">
	 	<c:set var="pos" value="${pos+1}" />
	 	<c:set var="tabKey" value="${payment.paymentSequenceNumber}-${payment.financialDocumentPostingDate}-${payment.accountChargeAmount}" />
	 	<kul:subtab width="100%" lookedUpCollectionName="org.kuali.module.cams.bo.AssetPayment" subTabTitle="${tabKey}" useCurrentTabIndexAsKey="false">
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true" /></th>
					<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].chartOfAccountsCode" attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].accountNumber" attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].subAccountNumber" attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].financialObjectCode" attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].projectCode" attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].organizationReferenceId" attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].documentNumber" attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].financialDocumentTypeCode" attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].purchaseOrderNumber" attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].requisitionNumber" attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].financialDocumentPostingDate" attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].financialDocumentPostingYear" attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].financialDocumentPostingPeriodCode" attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].transferPaymentCode" attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].accountChargeAmount" attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.accumulatedPrimaryDepreciationAmount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].accumulatedPrimaryDepreciationAmount" attributeEntry="${assetPaymentAttributes.accumulatedPrimaryDepreciationAmount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.yearToDate}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].yearToDate" attributeEntry="${assetPaymentAttributes.yearToDate}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.previousYearPrimaryDepreciationAmount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].previousYearPrimaryDepreciationAmount" attributeEntry="${assetPaymentAttributes.previousYearPrimaryDepreciationAmount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period1Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period1Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period1Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period2Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period2Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period2Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period3Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period3Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period3Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period4Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period4Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period4Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period5Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period5Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period5Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period6Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period6Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period6Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period7Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period7Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period7Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period8Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period8Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period8Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period9Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period9Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period9Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period10Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period10Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period10Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period11Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period11Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period11Depreciation1Amount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetPaymentAttributes.period12Depreciation1Amount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.assetPayments[${pos}].period12Depreciation1Amount" attributeEntry="${assetPaymentAttributes.period12Depreciation1Amount}" readOnly="true"/></td>								
			</tr>			
		</table>
		</div>
		</kul:subtab>
	</c:forEach>
</kul:tab>