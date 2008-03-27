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
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />

<kul:tab tabTitle="Asset Detail Information" defaultOpen="${!defaultTabHide}"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.organizationOwnerChartOfAccountsCode" attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.organizationOwnerAccountNumber" attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.agencyNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.agencyNumber" attributeEntry="${assetAttributes.agencyNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.acquisitionTypeCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.acquisitionTypeCode" attributeEntry="${assetAttributes.acquisitionTypeCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.inventoryStatusCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.inventoryStatusCode" attributeEntry="${assetAttributes.inventoryStatusCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.conditionCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.conditionCode" attributeEntry="${assetAttributes.conditionCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.capitalAssetTypeCode" attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.vendorName}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.vendorName" attributeEntry="${assetAttributes.vendorName}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerName}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.manufacturerName" attributeEntry="${assetAttributes.manufacturerName}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerModelNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.manufacturerModelNumber" attributeEntry="${assetAttributes.manufacturerModelNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.serialNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.serialNumber" attributeEntry="${assetAttributes.serialNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.campusTagNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.campusTagNumber" attributeEntry="${assetAttributes.campusTagNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.oldTagNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.oldTagNumber" attributeEntry="${assetAttributes.oldTagNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.governmentTagNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.governmentTagNumber" attributeEntry="${assetAttributes.governmentTagNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.nationalStockNumber}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.nationalStockNumber" attributeEntry="${assetAttributes.nationalStockNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.lastInventoryDate}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.lastInventoryDate" attributeEntry="${assetAttributes.lastInventoryDate}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.createDate}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.createDate" attributeEntry="${assetAttributes.createDate}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.financialDocumentPostingYear" attributeEntry="${assetAttributes.financialDocumentPostingYear}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.financialDocumentPostingPeriodCode" attributeEntry="${assetAttributes.financialDocumentPostingPeriodCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.capitalAssetInServiceDate" attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true"/></td>								
			</tr>
		</table>
		</div>
</kul:tab>