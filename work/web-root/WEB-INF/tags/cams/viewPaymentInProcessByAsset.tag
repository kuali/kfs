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
<%@ attribute name="assetPaymentDetail" type="java.util.List" required="true" description="In process asset payments list" %>
<%@ attribute name="assetPaymentAssetDetail" type="java.util.List" required="true" description="List of asset in payment document" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>

<c:if test="${ (fn:length(assetPaymentDetail) > 0) && (fn:length(assetPaymentAssetDetail) > 0) }">
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPaymentDetail.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	
	<c:set var="documentTotal" value="${KualiForm.document.sourceTotal}" />
	<c:set var="totalHistoricalAmount" value="${KualiForm.document.assetsTotalHistoricalCost}"/>
	
	<kul:tab tabTitle="In Process Payments by Asset" defaultOpen="${!defaultTabHide}" useCurrentTabIndexAsKey="false">
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true" /></th>				
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.expenditureFinancialDocumentPostedDate}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.amount}" readOnly="true" /></th>
			</tr>
			
			<c:set var="assetPos" value="-1" />			
			<c:forEach var="paymentAsset" items="${assetPaymentAssetDetail}">
	 			<c:set var="assetPos" value="${assetPos+1}" />
			 	<c:set var="paymentPos" value="${-1}" />
			 	
			 	<c:set var="percentage" value="${KualiForm.document.assetPaymentAssetDetail[assetPos].previousTotalCostAmount / totalHistoricalAmount }"/>
			 		 	
		 		<fmt:formatNumber var="rowTotal" value="${documentTotal * percentage }" maxFractionDigits="2" minFractionDigits="2"/>			 		 				 		 	
							
				<c:forEach var="payment" items="${assetPaymentDetail}">
				 	<c:set var="paymentPos" value="${paymentPos+1}" />	 	
					<tr>
		 				<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentAssetDetail[${assetPos}].capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true"/></td>								
		 				<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].chartOfAccountsCode" attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true"/></td>										
						<td class="grid">
							<kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].accountNumber" attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true">								
		            		</kul:htmlControlAttribute>
				      	</td>		
						<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].subAccountNumber" attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].financialObjectCode" attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].financialSubObjectCode" attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].projectCode" attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].expenditureFinancialDocumentPostedDate" attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].financialDocumentPostingYear" attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true"/></td>								
						<td class="grid"><kul:htmlControlAttribute property="document.assetPaymentDetail[${paymentPos}].financialDocumentPostingPeriodCode" attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true"/></td>								
						<td class="grid" align="right">${rowTotal}</td>								
					</tr>
				</c:forEach>					
			</c:forEach>
		</table>
		</div>
</kul:tab>
</c:if>