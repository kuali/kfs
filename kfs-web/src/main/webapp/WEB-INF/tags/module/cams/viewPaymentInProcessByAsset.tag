<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ attribute name="assetPaymentDetail" type="java.util.List" required="true" description="In process asset payments list" %>
<%@ attribute name="assetPaymentAssetDetail" type="java.util.List" required="true" description="List of asset in payment document" %>
<%@ attribute name="assetPaymentDistribution" type="java.util.Map" required="true" description="Map of asset payment distributions key by AssetPayment and AssetDetail" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>

<c:if test="${ (fn:length(assetPaymentDetail) > 0) && (fn:length(assetPaymentAssetDetail) > 0) }">
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPaymentDetail.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
		
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
        
				<c:forEach var="payment" items="${assetPaymentDetail}">
					<c:set var="line" value="${line + 1}"/>

					<c:set var="object" value="document.sourceAccountingLine[${line}]"/>
					<fmt:formatNumber var="allocatedAmount" value="${assetPaymentDistribution[payment.assetPaymentDetailKey][assetDetail]}" maxFractionDigits="2" minFractionDigits="2" type="number"/>
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
