<%--
 Copyright 2009 The Kuali Foundation
 
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
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
<c:set var="assetPaymentAssetDetailAttributes" value="${DataDictionary.AssetPaymentAssetDetail.attributes}" />
<c:set var="totalHistoricalAmount" value="${KualiForm.document.assetsTotalHistoricalCost}"/>
<c:set var="globalTotalAllocated" 	   value="${0.00}"/>
<c:set var="globalTotalHistoricalCost" value="${0.00}"/>
<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>

<c:set var="assetAllocationLabel" value="${KualiForm.allocationLabel}"/>
<c:set var="isAllocationEditable" value="${KualiForm.allocationEditable}"/>
<c:set var="isAllocationEditablePct" value="${KualiForm.allocationEditablePct}"/>
<c:set var="sourceLineCount" value="${KualiForm.sourceLineCount}"/>



<logic:iterate id="assetPaymentAssetDetail" name="KualiForm" property="document.assetPaymentAssetDetail" indexId="ctr"> 
		<c:set var="capitalAssetNumber" value="${KualiForm.document.assetPaymentAssetDetail[ctr].capitalAssetNumber}"/>
		<c:set var="assetObject" value="document.assetPaymentAssetDetail[${ctr}].asset" />
		<c:set var="assetValue" value="${KualiForm.document.assetPaymentAssetDetail[ctr].asset}" />
		<c:set var="assetPayments" value="${KualiForm.document.assetPaymentAssetDetail[ctr].asset.assetPayments}" />
		<c:set var="assetPaymentsAssetDetail" value="${KualiForm.document.assetPaymentAssetDetail[ctr]}" />
		
		<c:set var="totalAllocated" value="${0.00}"/>
		<c:set var="newTotal" value="${KualiForm.document.assetPaymentAssetDetail[ctr].newTotal}" />
		<c:set var="previousCost" value="${KualiForm.document.assetPaymentAssetDetail[ctr].previousTotalCostAmount}"/>
		
		<c:set var="totalAllocated" value="${KualiForm.document.assetPaymentAssetDetail[ctr].allocatedAmount}"/>		
		<c:if test="${totalAllocated != 0.00 }">
	    	<fmt:formatNumber var="sTotalAllocated" value="${totalAllocated }" maxFractionDigits="2" minFractionDigits="2" type="number"/>			 		 	
			<fmt:parseNumber value="${sTotalAllocated}" type="number" var="totalAllocated"/>
		</c:if>

		<table borders="0" cellpadding="0" cellspacing="0">
		<tr>
		<td style="padding: 0px;border-bottom-style: solid; border-bottom-width: 2px;border-top-style: solid; border-top-width: 1px;">
		
		<table cellpadding="0" cellspacing="0" class="datatable" summary="AssetSummary" borders="1">
				<tr>					
					<kul:htmlAttributeHeaderCell width="10%" align="center" attributeEntry="${assetAttributes.capitalAssetNumber}"/>
					<kul:htmlAttributeHeaderCell width="42%" align="center" attributeEntry="${assetAttributes.capitalAssetDescription}"/>
					<kul:htmlAttributeHeaderCell width="10%" align="center" attributeEntry="${accountAttributes.organizationCode}"/>
					<kul:htmlAttributeHeaderCell width="10%" align="center" attributeEntry="${assetPaymentAssetDetailAttributes.previousTotalCostAmount}"/>

			        <th class="grid" width="10%" align="center" style="padding: border-top-style:solid;">${assetAllocationLabel}</th>
			        <th class="grid" width="10%" align="center">New Total</th>
					<th class="grid" width="8%" align="center"><c:if test="${!viewOnly}">Actions</c:if></th>			
				</tr>
		
			    <tr>
			      	<td class="grid" width="10%">
			      		<kul:htmlControlAttribute property="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true" readOnlyBody="true">
							<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.Asset" keyValues="capitalAssetNumber=${KualiForm.document.assetPaymentAssetDetail[ctr].capitalAssetNumber}" render="true">
		              			<html:hidden write="true" property="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber" />
			           		</kul:inquiry>&nbsp;
			       		</kul:htmlControlAttribute>
			   		</td>
		   			
					<td class="grid" width="40%">
						<kul:htmlControlAttribute property="${assetObject}.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/>
					</td>
					   		
			        <td class="grid" width="10%"><div align="center">
			        	${KualiForm.document.assetPaymentAssetDetail[ctr].asset.organizationOwnerAccount.organizationCode}</div> 
				    </td>
				    
			        <td class="grid" width="10%"><div align="right">
				        <kul:htmlControlAttribute property="document.assetPaymentAssetDetail[${ctr}].previousTotalCostAmount" attributeEntry="${assetPaymentAssetDetailAttributes.previousTotalCostAmount}" readOnly="true"/>
			        </div></td>

					<%-- Allocated Amount  --%>
					<td class="grid" width="10%">
						<div align="right">
							<c:if test="${isAllocationEditable}">
								<c:if test="${isAllocationEditablePct}">
									<kul:htmlControlAttribute 
										property="document.assetPaymentAssetDetail[${ctr}].allocatedUserValuePct" 
										attributeEntry="${assetPaymentAssetDetailAttributes.allocatedUserValuePct}" 
										styleClass="amount"  
										readOnly="${viewOnly && (sourceLineCount > 0)}"/>
								</c:if>
								<c:if test="${!isAllocationEditablePct}">
									<kul:htmlControlAttribute 
										property="document.assetPaymentAssetDetail[${ctr}].allocatedUserValue" 
										attributeEntry="${assetPaymentAssetDetailAttributes.allocatedUserValue}" 
										styleClass="amount"  
										readOnly="${viewOnly && (sourceLineCount > 0)}"/>	
								</c:if>
							</c:if>
							<c:if test="${!isAllocationEditable}">
								<kul:htmlControlAttribute property="document.assetPaymentAssetDetail[${ctr}].allocatedAmount" attributeEntry="${assetPaymentAssetDetailAttributes.allocatedAmount}" readOnly="true"/>
							</c:if>
						</div>
					</td>

			        <td class="grid" width="10%"><div align="right">${newTotal}</div></td>
			               		
					<th class="datacell" rowspan="" nowrap="nowrap" width="5%">
						<c:if test="${!viewOnly}">			               		
				            <div align="center">
					            <c:if test="${isAllocationEditable && !readOnly}">
					            	<html:image property="methodToCall.updateAssetPaymentAssetDetail.line${ctr}" src="${ConfigProperties.externalizable.images.url}tinybutton-updateview.gif" styleClass="tinybutton" alt="Update Allocations" title="Update Allocations"/>
					            	<br/> 					            	
					            </c:if>
				            	<html:image property="methodToCall.deleteAssetPaymentAssetDetail.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="Delete asset" title="Delete asset"/>
				            </div>
						</c:if>			        				            
			        </th>
			    </tr>
		</table>

		<c:set var="globalTotalAllocated"	   value="${globalTotalAllocated + totalAllocated}"/>
		<c:set var="globalTotalHistoricalCost" value="${globalTotalHistoricalCost + previousCost}"/>

		<kul:tab tabTitle="Asset Information" defaultOpen="false" useCurrentTabIndexAsKey="true" tabErrorKey="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber*"> 		    
			<div class="tab-container" align="center" id="tab-AssetInformation-div">
				<table cellpadding="0" cellspacing="0" class="datatable" summary="Asset">
						<tr>
							<td  colspan="12" style="padding: 0px;">		
					
								<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
									<tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" /></th>
										<td class="grid" width="25%">
											<kul:htmlControlAttribute property="${assetObject}.organizationOwnerAccountNumber" attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" readOnlyBody="true">								
												<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}&amp;accountNumber=${assetValue.organizationOwnerAccountNumber}" render="true">
						                			<html:hidden write="true" property="${assetObject}.organizationOwnerAccountNumber" />
						                		</kul:inquiry>&nbsp;
						            		</kul:htmlControlAttribute>
								      	</td>
											
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" /></th>
										<td class="grid" width="25%">
											<kul:htmlControlAttribute property="${assetObject}.organizationOwnerChartOfAccountsCode" attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" readOnlyBody="true">								
								      			<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}" render="true">
						                			<html:hidden write="true" property="${assetObject}.organizationOwnerChartOfAccountsCode" />
						                		</kul:inquiry>&nbsp;
						            		</kul:htmlControlAttribute>
								      	</td>
									</tr>			    
					
								    <tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true" /></th>
										<td class="grid" width="25%">
										   	<kul:htmlControlAttribute property="${assetObject}.capitalAssetTypeCode" attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true" readOnlyBody="true"> 
												<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.AssetType" keyValues="capitalAssetTypeCode=${assetValue.capitalAssetTypeCode}" render="true">
								            		<html:hidden write="true" property="${assetObject}.capitalAssetTypeCode" />
								                </kul:inquiry>&nbsp;
								            </kul:htmlControlAttribute>
										 </td>
												 
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.vendorName}" readOnly="true" /></th>
										<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetObject}.vendorName" attributeEntry="${assetAttributes.vendorName}" readOnly="true"/></td>								
								    </tr>
								            
								    <tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true" /></th>
										<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetObject}.capitalAssetInServiceDate" attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true"/></td>								
							
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerName}" readOnly="true" /></th>
										<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetObject}.manufacturerName" attributeEntry="${assetAttributes.manufacturerName}" readOnly="true"/></td>		
								    </tr>
								    <tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true" /></th>
										<td class="grid" width="75%" colspan="3" ><kul:htmlControlAttribute property="${assetObject}.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>							
								    </tr>	            			            
								</table>
						  </td>
						</tr>
				</table>				
				<cams:viewPayments defaultTabHide="true" assetPayments="${assetPayments}"	assetValueObj="${assetObject}" assetValue="${assetValue}"/>	
				
				<cams:viewPaymentInProcess defaultTabHide="true" assetPaymentDetails="${KualiForm.document.sourceAccountingLines}" assetPaymentAssetDetail="${assetPaymentsAssetDetail}" assetPaymentsTotal="${totalAllocated}" assetPaymentDistribution="${KualiForm.document.assetPaymentDistributor.assetPaymentDistributions}"/>
			</div>
		</kul:tab>
		</td>
		</tr>
		</table>				 
</logic:iterate>

<c:if test="${fn:length(KualiForm.document.assetPaymentAssetDetail) > 0}">
	<table cellpadding="0" cellspacing="0" class="datatable" summary="AssetSummary" borders="1">
		<tr>
			<kul:htmlAttributeHeaderCell colspan="3" literalLabel="Grand Total:" align="right" width="62%"/>
			<th class="grid" align="right" width="10%"><fmt:formatNumber value="${globalTotalHistoricalCost}" maxFractionDigits="2" minFractionDigits="2"/></th>
			<th class="grid" align="right" width="10%"><fmt:formatNumber value="${globalTotalAllocated}" maxFractionDigits="2" minFractionDigits="2"/></th>
			<th class="grid" align="right" width="10%"><fmt:formatNumber value="${globalTotalAllocated + globalTotalHistoricalCost}" maxFractionDigits="2" minFractionDigits="2"/></th>
			<th class="grid" width="8%">&nbsp;</th>	
		</tr>
	</table>
</c:if>	
