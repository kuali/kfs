<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ attribute name="camsItemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="ctr" required="true" description="item count"%>
<%@ attribute name="ctr2" required="true" description="item count"%>

<table class="datatable" summary="" border="0" cellpadding="0" cellspacing="0" style="width:100%">
<tr>
	<td colspan="4" class="subhead">
		<span class="left">Locations</span>
		<span class="right"><html:image property="methodToCall.deleteCapitalAssetLocation.(((${ctr}))).((#${ctr2}#))" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete a Asset Location" title="Delete a Asset Location" styleClass="tinybutton" /></span>
	</td>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.itemQuantity}" align="right" />
	<td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.itemQuantity}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].itemQuantity" readOnly="true"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetLine1Address" readOnly="true"/>
	</td>
    </tr>
    <tr>
    	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingCode}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingCode}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].buildingCode" readOnly="true"/>
        <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
        	lookupParameters="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].campusCode:campusCode"
        	fieldConversions="buildingCode:document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].buildingCode,campusCode:document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].campusCode,buildingStreetAddress:document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetLine1Address,buildingAddressCityName:document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetCityName,buildingAddressStateCode:document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetStateCode,buildingAddressZipCode:document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetPostalCode"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetCityName}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetCityName}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetCityName" readOnly="true"/>
	</td>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.campusCode}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.campusCode}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].campusCode" readOnly="true"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" align="right" />
	<td class="datacell">
    	<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetStateCode" readOnly="true"/>
	</td>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingRoomNumber}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingRoomNumber}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].buildingRoomNumber" readOnly="true"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations[${ctr2}].capitalAssetPostalCode" readOnly="true"/>
	</td>
</tr>
</table>