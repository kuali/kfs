<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="capitalAssetInfo" required="true" type="java.lang.Object"
	description="The capital asset info object containing the data being displayed"%>
<%@ attribute name="capitalAssetInfoName" required="true" 
	description="The name of the capital asset info object"%>	
<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>	
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
<c:set var="dataCellCssClass" value="datacell"/>


<table class="datatable" border="0" cellpadding="0" cellspacing="0" width="100%" summary="Capital Asset Information">
   	<tr>
   		<td colspan="2" class="tab-subhead" style="border-right: medium none;">Retrieve Asset to be Updated</td>
   	</tr>
   	<tr>                        
	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetNumber}" horizontal="true" width="50%"/>        
	    <fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetNumber" lookup="true" inquiry="true"
			boClassSimpleName="CapitalAssetManagementAsset" boPackageName="org.kuali.kfs.integration.cam"
			lookupUnkeyedFieldConversions="campusTagNumber:${capitalAssetInfoName}.capitalAssetTagNumber,"
			lookupOrInquiryKeys="capitalAssetNumber"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>
   	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTagNumber}" align="right"/>
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetTagNumber" conversionField="campusTagNumber" lookup="true" inquiry="false"
			boClassSimpleName="CapitalAssetManagementAsset" boPackageName="org.kuali.kfs.integration.cam"
			lookupOrInquiryKeys="capitalAssetNumber"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>			
   	</tr>   
   	<tr>
   		<td colspan="2" ><br/></td>
   	</tr>
</table>
		
<table class="datatable" border="0" cellpadding="0" cellspacing="0" width="100%" summary="Capital Asset Information">
   <tr>
   		<td colspan="5" class="tab-subhead" style="border-right: medium none;">Extra Fields for Adding Asset</td>
   </tr>
   <tr>                        
	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.campusCode}" labelFor="${capitalAssetInfoName}.campusCode"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingCode}" labelFor="${capitalAssetInfoName}.buildingCode"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingRoomNumber}" labelFor="${capitalAssetInfoName}.buildingRoomNumber"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingSubRoomNumber}" labelFor="${capitalAssetInfoName}.buildingSubRoomNumber"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.vendorNumber}" useShortLabel="false" labelFor="${capitalAssetInfoName}.vendorNumber"/>
   </tr>
   
   <tr>
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="campusCode" lookup="true" inquiry="true"
			boClassSimpleName="Campus" boPackageName="org.kuali.rice.kns.bo"
			lookupOrInquiryKeys="campusCode"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>	
		
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="buildingCode" lookup="true" inquiry="true"
			boClassSimpleName="Building" boPackageName="org.kuali.kfs.sys.businessobject"
			lookupOrInquiryKeys="campusCode,buildingCode"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>
		
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="buildingRoomNumber" lookup="true" inquiry="true"
			boClassSimpleName="Room" boPackageName="org.kuali.kfs.sys.businessobject"
			lookupOrInquiryKeys="campusCode,buildingCode,buildingRoomNumber"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>	
		
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="buildingSubRoomNumber" lookup="false" inquiry="false"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>					

		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="vendorNumber" lookup="true" inquiry="true" disabled="true"
			boClassSimpleName="VendorDetail" boPackageName="org.kuali.kfs.vnd.businessobject"
			lookupOrInquiryKeys="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}" />																			 
   </tr>
   
   <tr><td colspan="5" class="infoline"><center><br/>
	   	<table style="border-top: 1px solid rgb(153, 153, 153); width: 90%;" cellpadding="0" cellspacing="0" class="datatable">       
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTypeCode}" align="right" width="25%"/>		
				<fin:dataCell dataCellCssClass="${dataCellCssClass}"
					businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
					field="capitalAssetTypeCode" lookup="true" inquiry="true"
					boClassSimpleName="CapitalAssetManagementAssetType" boPackageName="org.kuali.kfs.integration.cam" 
					lookupOrInquiryKeys="capitalAssetTypeCode"
					businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>	
					
			  	<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetDescription}" align="right" rowspan="7" width="25%"/>
				<fin:dataCell dataCellCssClass="${dataCellCssClass}"
					businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
					field="capitalAssetDescription" lookup="false" inquiry="false" rowSpan="6"/>					
			</tr>				
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTagNumber}" align="right"/>
				<fin:dataCell dataCellCssClass="${dataCellCssClass}"
					businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
					field="capitalAssetTagNumber" lookup="false" inquiry="false"/>			
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetQuantity}" align="right"/>
				<fin:dataCell dataCellCssClass="${dataCellCssClass}"
					businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
					field="capitalAssetQuantity" lookup="false" inquiry="false"/>
			</tr>        
			<tr>
			   	<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerName}" align="right" />
			    <fin:dataCell dataCellCssClass="${dataCellCssClass}"
					businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
					field="capitalAssetManufacturerName" lookup="false" inquiry="false"/>
			</tr>
			<tr> 
			   	<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerModelNumber}" align="right" />      	
				<fin:dataCell dataCellCssClass="${dataCellCssClass}"
					businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
					field="capitalAssetManufacturerModelNumber" lookup="false" inquiry="false"/>
		    </tr>
		    <tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetSerialNumber}" align="right" />	     				
				<fin:dataCell dataCellCssClass="${dataCellCssClass}"
					businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
					field="capitalAssetSerialNumber" lookup="false" inquiry="false"/>
		    </tr>		    
	   	</table><br/><br/>
   </center></td></tr>
</table>