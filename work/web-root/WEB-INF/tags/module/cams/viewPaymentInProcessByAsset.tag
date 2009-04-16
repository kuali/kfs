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
<%@ attribute name="assetPaymentDetail" type="java.util.List" required="true" description="In process asset payments list" %>
<%@ attribute name="assetPaymentAssetDetail" type="java.util.List" required="true" description="List of asset in payment document" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>

<c:if test="${ (fn:length(assetPaymentDetail) > 0) && (fn:length(assetPaymentAssetDetail) > 0) }">
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPaymentDetail.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	
	<c:set var="documentTotal" value="${KualiForm.document.sourceTotal}" />
	<c:set var="totalHistoricalAmount" value="${KualiForm.document.assetsTotalHistoricalCost}"/>

	<c:set var="numberOfAssets" value="${fn:length(KualiForm.document.assetPaymentAssetDetail)}"/>
	
	<c:set var="numberOfUnallocatedAssets" value="${numberOfAssets}"/>
	<c:set var="semicolon" value=";"/>
	<c:set var="unallocateAssetPayments" value="" />
	<c:set var="globalTotalAllocated" value="${0.00}"/>
	<c:forEach var="assetDetail" items="${assetPaymentAssetDetail}">
		<c:set var="previousTotalCost" value="${assetDetail.previousTotalCostAmount}" />

		<c:if test="${totalHistoricalAmount != 0 }">
			<c:set var="percentage" value="${previousTotalCost / totalHistoricalAmount }"/>
		</c:if>
		<c:if test="${totalHistoricalAmount == 0 }">
			<c:set var="percentage" value="${ 1 / numberOfAssets}"/>
		</c:if>
		<c:choose>
			<c:when test="${numberOfUnallocatedAssets == 1}">
	    	 	<fmt:formatNumber var="allocatedAssetAmount" value="${documentTotal - globalTotalAllocated }" maxFractionDigits="2" minFractionDigits="2" type="number"/>			 		 	
			</c:when>
			<c:otherwise>
				<c:set var="numberOfUnallocatedAssets" value="${numberOfUnallocatedAssets - 1}"/>
	    	 	<fmt:formatNumber var="allocatedAssetAmount" value="${documentTotal * percentage }" maxFractionDigits="2" minFractionDigits="2" type="number"/>			 		 	
				<fmt:parseNumber value="${allocatedAssetAmount}" type="number" var="nAllocatedAssetAmount"/>
				<c:set var="globalTotalAllocated" value="${globalTotalAllocated + nAllocatedAssetAmount}"/>
			</c:otherwise>
		</c:choose>
		<c:set var="unallocateAssetPayments" value="${unallocateAssetPayments}${semicolon}${allocatedAssetAmount}" />
	</c:forEach>
	<c:set var="unallocateAssetPayments" value="${fn:substringAfter(unallocateAssetPayments, semicolon)}" />
			
	<c:set var="dateFormatPattern" value="MM/dd/yyyy"/>
	
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
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.postingYear}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.postingPeriodCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.amount}" readOnly="true" /></th>
			</tr>
			

			<c:set var="pos" value="${-1}"/>			
			<c:forEach var="assetDetail" items="${assetPaymentAssetDetail}">
				<c:set var="pos" value="${pos + 1}"/>	

				<c:set var="line" value="${-1}"/>

				<c:set var="numberOfUnallocatedPayments" value="${fn:length(assetPaymentDetail)}"/>
	            <c:if test="${!fn:contains(unallocateAssetPayments, ';')}">
					<c:set var="oneAssetPayment" value="${unallocateAssetPayments }"/>
				</c:if>
		        <c:if test="${fn:contains(unallocateAssetPayments, ';')}">
					<c:set var="oneAssetPayment" value="${fn:substringBefore(unallocateAssetPayments, semicolon)}" />
					<c:set var="unallocateAssetPayments" value="${fn:substringAfter(unallocateAssetPayments, semicolon)}" />
	            </c:if>
				<fmt:parseNumber value="${oneAssetPayment}" type="number" var="nOneAssetPayment"  />

				<c:forEach var="payment" items="${assetPaymentDetail}">
					<c:set var="line" value="${line + 1}"/>

					<c:set var="object" value="document.sourceAccountingLine[${line}]"/>
					
					<c:set var="allocatedAmount" value="${0.00}"/>								
					<c:set var="previousTotalCost" value="${assetDetail.previousTotalCostAmount}" />

					<c:if test="${totalHistoricalAmount != 0 }">
					 	<c:set var="percentage" value="${previousTotalCost / totalHistoricalAmount }"/>
					</c:if>
					<c:if test="${totalHistoricalAmount == 0 }">
				        <c:set var="percentage" value="${ 1 / numberOfAssets}"/>
					</c:if>
					
				 	<c:set var="paymentAmount" value="${payment.amount}" />			 										 																 	

					<c:choose>
						<c:when test="${numberOfUnallocatedPayments == 1}">
						    <fmt:formatNumber var="allocatedAmount" value="${nOneAssetPayment}" maxFractionDigits="2" minFractionDigits="2"/>
						</c:when>
						<c:otherwise>			
						 	<fmt:formatNumber var="allocatedAmount" value="${paymentAmount * percentage }" maxFractionDigits="2" minFractionDigits="2" type="number"/>			 		 				 		 					
							<fmt:parseNumber value="${allocatedAmount}" type="number" var="roundAllocatedAmount" />
							<c:set var="nOneAssetPayment" value="${nOneAssetPayment - roundAllocatedAmount}" />
				
							<c:set var="numberOfUnallocatedPayments" value="${numberOfUnallocatedPayments - 1}"/>
						</c:otherwise>
					</c:choose>

					<tr>
						<td class="grid">
				      		<kul:htmlControlAttribute property="document.assetPaymentAssetDetail[${pos}].capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true" readOnlyBody="true">
								<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.Asset" keyValues="capitalAssetNumber=${KualiForm.document.assetPaymentAssetDetail[pos].capitalAssetNumber}" render="true">
			              			<html:hidden write="true" property="document.assetPaymentAssetDetail[${pos}].capitalAssetNumber" />
				           		</kul:inquiry>&nbsp;
				       		</kul:htmlControlAttribute>
			 			</td>
		 												
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
						<td class="grid"><fmt:formatDate value="${payment.expenditureFinancialDocumentPostedDate}" pattern="${dateFormatPattern}"/></td>
						<td class="grid"><c:out value="${payment.postingYear}"/></td>								
						<td class="grid"><c:out value="${payment.postingPeriodCode}"/></td>								
						<td class="grid"><div align="right">${allocatedAmount}</div></td>								
					</tr>
				</c:forEach>					
			</c:forEach>
		</table>
		</div>
</kul:tab>
</c:if>