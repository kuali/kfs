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
<c:set var="assetLocationAttributes" value="${DataDictionary.AssetLocation.attributes}" />

<kul:tab tabTitle="Asset Location" defaultOpen="${!defaultTabHide}"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">On Campus</td><td class="tab-subhead"  colspan="2" width="50%">Off Campus</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right">Campus:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.campusCode" attributeEntry="${assetAttributes.campusCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right">Name:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationContactName" attributeEntry="${assetLocationAttributes.assetLocationContactName}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right">Building Code:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingCode" attributeEntry="${assetAttributes.buildingCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right">Address:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationStreetAddress" attributeEntry="${assetLocationAttributes.assetLocationStreetAddress}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right">Building Room Number:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingRoomNumber" attributeEntry="${assetAttributes.buildingRoomNumber}" readOnly="true"/></td><%--				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetLocationAttributes.assetLocationCityName}" /></th> --%>
				<th class="grid" width="25%" align="right">City:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationCityName" attributeEntry="${assetLocationAttributes.assetLocationCityName}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.buildingSubRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingSubRoomNumber" attributeEntry="${assetAttributes.buildingRoomNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right">State:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationStateCode" attributeEntry="${assetLocationAttributes.assetLocationStateCode}" readOnly="true"/></td>
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right" colspan="2"></th>
				<th class="grid" width="25%" align="right">Zip Code:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationZipCode" attributeEntry="${assetLocationAttributes.assetLocationZipCode}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right" colspan="2"></th>
				<th class="grid" width="25%" align="right">Country:</th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationCountryCode" attributeEntry="${assetLocationCountryCode}" readOnly="true"/></td>
				</td>						
			</tr>
		</table>
		</div>
	</kul:tab>
