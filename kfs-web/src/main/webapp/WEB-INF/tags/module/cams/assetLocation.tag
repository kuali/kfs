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
<c:set var="assetLocationAttributes" value="${DataDictionary.AssetLocation.attributes}" />
<c:set var="assetTransferAttributes" value="${DataDictionary.AssetTransferDocument.attributes}" />
<c:set var="assetValue" value="${KualiForm.document.asset}" />


<kul:tab tabTitle="Asset Location" defaultOpen="${!defaultTabHide}">    
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">On Campus</td><td class="tab-subhead"  colspan="2" width="50%">Off Campus</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right">Campus:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.campusCode" attributeEntry="${assetAttributes.campusCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusName}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationContactName" attributeEntry="${assetLocationAttributes.assetLocationContactName}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingCode}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingCode" attributeEntry="${assetAttributes.buildingCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusAddress}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationStreetAddress" attributeEntry="${assetLocationAttributes.assetLocationStreetAddress}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingRoomNumber}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingRoomNumber" attributeEntry="${assetAttributes.buildingRoomNumber}" readOnly="true"/></td><%--				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetLocationAttributes.assetLocationCityName}" /></th> --%>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCityName}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationCityName" attributeEntry="${assetLocationAttributes.assetLocationCityName}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingSubRoomNumber" attributeEntry="${assetAttributes.buildingRoomNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusStateCode}" /></th>  
				<td class="grid" width="25%">
					<kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationStateCode" attributeEntry="${assetLocationAttributes.assetLocationStateCode}" readOnly="true" readOnlyBody="true">
						<kul:inquiry boClassName="org.kuali.rice.location.framework.state.StateEbo" keyValues="stateCode=${assetValue.offCampusLocation.assetLocationState.code}&amp;postalCountryCode=${assetValue.offCampusLocation.assetLocationCountryCode}" render="true">
                			<html:hidden write="true" property="document.asset.offCampusLocation.assetLocationState.code" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right" colspan="2"></th>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></th>  
				<td class="grid" width="25%">
					<kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationZipCode" attributeEntry="${assetLocationAttributes.assetLocationZipCode}" readOnly="true" readOnlyBody="true">
						<kul:inquiry boClassName="org.kuali.rice.location.framework.postalcode.PostalCodeEbo" keyValues="postalCode=${assetValue.offCampusLocation.postalZipCode.code}&amp;postalCountryCode=${assetValue.offCampusLocation.assetLocationCountryCode}" render="true">
                			<html:hidden write="true" property="document.asset.offCampusLocation.postalZipCode.code" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
				</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right" colspan="2"></th>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCountryCode}" /></th>  
				<td class="grid" width="25%">
					<kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationCountry.postalCountryName" attributeEntry="${assetLocationAttributes.assetLocationCountryCode}" readOnly="true" readOnlyBody="true">
						<kul:inquiry boClassName="org.kuali.rice.location.framework.country.CountryEbo" keyValues="postalCountryCode=${assetValue.offCampusLocation.assetLocationCountryCode}" render="true">
                			<html:hidden write="true" property="document.asset.offCampusLocation.assetLocationCountry.name" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
				</td>
			</tr>
		</table>
		</div>
	</kul:tab>
