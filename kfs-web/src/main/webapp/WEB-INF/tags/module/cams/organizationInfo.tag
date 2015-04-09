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

