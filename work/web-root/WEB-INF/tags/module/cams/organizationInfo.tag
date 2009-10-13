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
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="assetOrganizationAttributes" value="${DataDictionary.AssetOrganization.attributes}" />
<c:set var="assetGlobalDetailAttributes" value="${DataDictionary.AssetGlobalDetail.attributes}" />

	<kul:tab tabTitle="Organization" defaultOpen="${!defaultTabHide}" > 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">			
			<tr>
                <td colspan="4" class="tab-subhead">Organization Information</td>
			</tr>					
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationInventoryName}" /></th> 
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.organizationInventoryName" attributeEntry="${assetAttributes.organizationInventoryName}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetGlobalDetailAttributes.representativeUniversalIdentifier}" /></th> 
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetRepresentative.name" attributeEntry="${assetAttributes.representativeUniversalIdentifier}" readOnly="true"/></td>
			</tr>

			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetOrganizationAttributes.organizationText}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationText" attributeEntry="${assetOrganizationAttributes.organizationText}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetOrganizationAttributes.organizationTagNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationTagNumber" attributeEntry="${assetOrganizationAttributes.organizationTagNumber}" readOnly="true"/></td>
			</tr>		
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetGlobalDetailAttributes.organizationAssetTypeIdentifier}" /></th> 
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationAssetTypeIdentifier" attributeEntry="${assetOrganizationAttributes.organizationAssetTypeIdentifier}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right" colspan="2"></th>
            </tr>
		</table>
		</div>
	</kul:tab>	

