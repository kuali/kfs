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
<%@ attribute name="assetPaymentDetails" type="java.util.List" required="true" description="In process asset payments list" %>
<%@ attribute name="assetPaymentAssetDetail" type="org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail" required="true" description="Asset payment in process list" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<%@ attribute name="assetPaymentsTotal" type="java.lang.Number" required="true" description="Show tab contents indicator" %>
<%@ attribute name="assetPaymentDistribution" type="java.util.Map" required="true" description="Map of asset payment distributions key by AssetPayment and AssetDetail" %>


<c:set var="numberOfUnallocatedPayments" value="${fn:length(assetPaymentDetails)}"/>


<c:if test="${ (fn:length(assetPaymentDetails) > 0) }">
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPaymentDetail.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />	
	<c:set var="totalHistoricalAmount" value="${KualiForm.document.assetsTotalHistoricalCost}"/>			
	<c:set var="documentTotal" value="${KualiForm.document.sourceTotal}" />
	<c:set var="dateFormatPattern" value="MM/dd/yyyy"/>
	
	
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
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.postingYear}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.postingPeriodCode}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.transferPaymentIndicator}" readOnly="true" /></th>
					<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.amount}" readOnly="true" /></th>
				</tr>
				
				<c:set var="totalPayments" value="${0.00}" />
				<c:set var="line" value="${-1}"/>
				<c:forEach var="payment" items="${assetPaymentDetails}">
					<c:set var="line" value="${line + 1}"/>
					<c:set var="object" value="document.sourceAccountingLine[${line}]"/>
					<c:if test="${totalHistoricalAmount > 0 }">
						<c:choose>
							<c:when test="${assetPaymentAssetDetail.previousTotalCostAmount == null}">
								<c:set var="previousTotalCost" value="${0}" />
							</c:when>
							<c:otherwise>
								<c:set var="previousTotalCost" value="${assetPaymentAssetDetail.previousTotalCostAmount}" />								
							</c:otherwise>
						</c:choose>
					</c:if>

					<c:set var="allocatedAmount" value="${assetPaymentDistribution[payment.assetPaymentDetailKey][assetPaymentAssetDetail]}" />				
					<c:set var="totalPayments" value="${allocatedAmount + totalPayments}"/>									 	
					<tr>
		 				<td class="grid"><kul:htmlControlAttribute property="${object}.chartOfAccountsCode" attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true"/></td>								
						<td class="grid">
							<kul:htmlControlAttribute property="${object}.accountNumber" attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" readOnlyBody="true">								
								<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${KualiForm.document.sourceAccountingLines[line].chartOfAccountsCode}&amp;accountNumber=${KualiForm.document.sourceAccountingLines[line].accountNumber}" render="true">
		                			<html:hidden write="true" property="${object}.accountNumber" />
        		        		</kul:inquiry>&nbsp;
            				</kul:htmlControlAttribute>
		      			</td>
						<td class="grid"><c:out value="${payment.subAccountNumber}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.financialObjectCode}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.financialSubObjectCode}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.projectCode}"/>&nbsp;</td>
						<td class="grid"><c:out value="${payment.organizationReferenceId}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.expenditureFinancialDocumentNumber}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.expenditureFinancialDocumentTypeCode}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.purchaseOrderNumber}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.requisitionNumber}"/>&nbsp;</td>								
						<td class="grid"><fmt:formatDate value="${payment.expenditureFinancialDocumentPostedDate}" pattern="${dateFormatPattern}"/>&nbsp;</td>
						<td class="grid"><c:out value="${payment.postingYear}"/>&nbsp;</td>								
						<td class="grid"><c:out value="${payment.postingPeriodCode}"/>&nbsp;</td>														
						<td class="grid"><kul:htmlControlAttribute property="${object}.transferPaymentIndicator" attributeEntry="${assetPaymentAttributes.transferPaymentIndicator}" readOnly="true"/></td>
						<td class="grid"><div align="right"><fmt:formatNumber value="${allocatedAmount}" maxFractionDigits="2" minFractionDigits="2"/></div>&nbsp;</td>
					</tr>
				</c:forEach>
				
				<tr>
					<kul:htmlAttributeHeaderCell colspan="15" literalLabel="Payment(s) Total:" align="right"/>
					<td class="grid"><div align="right"><fmt:formatNumber value="${totalPayments}" maxFractionDigits="2" minFractionDigits="2"/></div></td>					
				</tr>									
				<tr>
					<kul:htmlAttributeHeaderCell  literalLabel="Historical Cost:" align="right" colspan="15"/></th>
					<td class="grid"><div align="right">
						<fmt:formatNumber value="${previousTotalCost}" maxFractionDigits="2" minFractionDigits="2"/></div>					
					</td>					
				</tr>									
				<tr>
					<kul:htmlAttributeHeaderCell colspan="15" literalLabel="New Total:" align="right"/>
					<td class="grid"><div align="right">
						<fmt:formatNumber value="${totalPayments + previousTotalCost}" maxFractionDigits="2" minFractionDigits="2"/></div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>
