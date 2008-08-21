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
<%@ attribute name="assetPaymentDetails" type="java.util.List" required="true" description="In process asset payments list" %>
<%@ attribute name="assetPaymentAssetDetail" type="org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail" required="true" description="Asset payment in process list" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>

<c:if test="${ (fn:length(assetPaymentDetails) > 0) }">
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPaymentDetail.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	
	<c:set var="totalHistoricalAmount" value="${KualiForm.document.assetsTotalHistoricalCost}"/>
	<c:set var="documentTotal" value="${KualiForm.document.sourceTotal}" />
	
	<kul:tab tabTitle="In Process Payments" defaultOpen="${!defaultTabHide}" useCurrentTabIndexAsKey="true">
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
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.expenditureFinancialDocumentTypeCode}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.expenditureFinancialDocumentPostedDate}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.transferPaymentIndicator}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.amount}" readOnly="true" /></th>
				</tr>
				
				<c:set var="totalPayments" value="${0.00}" />
				<c:forEach var="payment" items="${assetPaymentDetails}">
					<c:set var="allocatedAmount" value="${0.00}"/>		
					<c:if test="${totalHistoricalAmount > 0 }">
						<c:set var="previousTotalCost" value="${assetPaymentAssetDetail.previousTotalCostAmount}" />
			    	    <c:set var="percentage" value="${previousTotalCost / totalHistoricalAmount }"/>
				        <c:set var="allocatedAmount" value="${documentTotal * percentage}"/>
					</c:if>
					<c:set var="amount" value="${payment.amount}" />
					<c:set var="totalPayments" value="${allocatedAmount + totalPayments}" />
									 	
					<tr>
		 				<td class="grid">${payment.chartOfAccountsCode}</td>
						<td class="grid">${payment.accountNumber}</td>
						<td class="grid">${payment.subAccountNumber}</td>								
						<td class="grid">${payment.financialObjectCode}</td>								
						<td class="grid">${payment.financialSubObjectCode}</td>								
						<td class="grid">${payment.projectCode}</td>								
						<td class="grid">${payment.organizationReferenceId}</td>								
						<td class="grid">${payment.documentNumber}</td>								
						<td class="grid">${payment.expenditureFinancialDocumentTypeCode}</td>								
						<td class="grid">${payment.purchaseOrderNumber}</td>								
						<td class="grid">${payment.requisitionNumber}</td>								
						<td class="grid">${payment.expenditureFinancialDocumentPostedDate}</td>								
						<td class="grid">${payment.financialDocumentPostingYear}</td>								
						<td class="grid">${payment.financialDocumentPostingPeriodCode}</td>								
						<td class="grid">${payment.transferPaymentIndicator}</td>								
						<td class="grid" align="right"><fmt:formatNumber value="${allocatedAmount}" maxFractionDigits="2" minFractionDigits="2"/></td>
					</tr>
				</c:forEach>
				
				<tr>
					<kul:htmlAttributeHeaderCell colspan="15" literalLabel="Payment(s) Total:" align="right"/>
					<td class="grid" align="right"><fmt:formatNumber value="${totalPayments}" maxFractionDigits="2" minFractionDigits="2"/></td>
					
				</tr>									
				<tr>
					<kul:htmlAttributeHeaderCell  literalLabel="Historical Cost:" align="right" colspan="15"/></th>
					<td class="grid" align="right">
						<fmt:formatNumber value="${previousTotalCost}" maxFractionDigits="2" minFractionDigits="2"/>					
					</td>					
				</tr>									
				<tr>
					<kul:htmlAttributeHeaderCell colspan="15" literalLabel="New Total:" align="right"/>
					<td class="grid" align="right">
						<fmt:formatNumber value="${totalPayments + previousTotalCost}" maxFractionDigits="2" minFractionDigits="2"/>
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>