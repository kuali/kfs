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
<%@ attribute name="documentName" type="java.lang.String" required="true" description="Name of the document in the form" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<kul:tab tabTitle="Asset Depreciation Information" defaultOpen="${!defaultTabHide}"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.primaryDepreciationMethodCode}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.primaryDepreciationMethodCode" attributeEntry="${assetAttributes.primaryDepreciationMethodCode}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right">Depreciable Lifelimit:</th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.capitalAssetType.depreciableLifeLimit" attributeEntry="${assetAttributes.capitalAssetType.depreciableLifeLimit}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.baseAmount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.baseAmount" attributeEntry="${assetAttributes.baseAmount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.salvageAmount}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.salvageAmount" attributeEntry="${assetAttributes.salvageAmount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.accumulatedDepreciation}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.accumulatedDepreciation" attributeEntry="${assetAttributes.accumulatedDepreciation}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.bookValue}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.bookValue" attributeEntry="${assetAttributes.bookValue}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.yearToDateDepreciation}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.yearToDateDepreciation" attributeEntry="${assetAttributes.yearToDateDepreciation}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.currentMonthDepreciation}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.currentMonthDepreciation" attributeEntry="${assetAttributes.currentMonthDepreciation}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.prevYearDepreciation}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${documentName}.asset.prevYearDepreciation" attributeEntry="${assetAttributes.prevYearDepreciation}" readOnly="true"/></td>								
			</tr>
		</table>
		</div>
</kul:tab>