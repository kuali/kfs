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
<%@ attribute name="assetPayments" type="java.util.List" required="true" description="Payments list" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<%@ attribute name="assetValueObj" type="java.lang.String" required="false" description="Asset object name" %>
<%@ attribute name="assetValue" type="org.kuali.kfs.module.cam.businessobject.Asset" required="false" description="Asset object value" %>

<c:if test="${fn:length(assetPayments) <= CamsConstants.Asset.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY}">
	<c:if test="${assetValueObj==null}">
		<c:set var="assetValueObj" value="document.asset" />
		<c:set var="assetValue" value="${KualiForm.document.asset}" />
	</c:if>
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPayment.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	<c:set var="pos" value="-1" />

	<kul:tab tabTitle="Processed Payments" defaultOpen="${!defaultTabHide}" useCurrentTabIndexAsKey="true">
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true" /></th>
			</tr>
			<c:set var="totalPayments" value="${0}" />
			<c:forEach var="payment" items="${assetPayments}">
			
				<c:set var="totalPayments" value="${totalPayments + payment.accountChargeAmount}"/>									 				
			 	<c:set var="pos" value="${pos+1}" />
			 	<c:set var="posValue" value="${pos}" />			 	
					<tr>
		 				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].chartOfAccountsCode" attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true"/></td>									
						<td class="grid">
							<kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].accountNumber" attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" readOnlyBody="true">								
								<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${assetValue.assetPayments[posValue].chartOfAccountsCode}&amp;accountNumber=${assetValue.assetPayments[posValue].accountNumber}" render="true">
		                			<html:hidden write="true" property="${assetValueObj}.assetPayments[${pos}].accountNumber" />
		                		</kul:inquiry>&nbsp;
		            		</kul:htmlControlAttribute>
				      	</td>		
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].subAccountNumber" attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialObjectCode" attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialSubObjectCode" attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].projectCode" attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].organizationReferenceId" attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].documentNumber" attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentTypeCode" attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].purchaseOrderNumber" attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].requisitionNumber" attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingDate" attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingYear" attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingPeriodCode" attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].transferPaymentCode" attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true"/></td>								
						<td class="grid"><div align="right"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].accountChargeAmount" attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true"/></div></td>								
					</tr>
			</c:forEach>
			<tr>
				<th class="grid" align="right" colspan="15"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetAttributes.paymentTotalCost}" readOnly="true" /></th>								
				<td class="grid"><div align="right"><fmt:formatNumber value="${totalPayments}" maxFractionDigits="2" minFractionDigits="2"/></div></td>
			</tr>						
		</table>
		</div>
</kul:tab>
</c:if>
<cams:assetPaymentsLookupLink capitalAssetNumber="${assetValue.capitalAssetNumber}"/>
