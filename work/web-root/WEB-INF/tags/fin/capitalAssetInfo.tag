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
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
<c:set var="dataCellCssClass" value="datacell"/>

<html:hidden property="${capitalAssetInfoName}.documentNumber" />
<html:hidden property="${capitalAssetInfoName}.versionNumber" />
		
<table class="datatable" border="0" cellpadding="0" cellspacing="0" width="100%" summary="Capital Asset Information">
   <tr>                        
	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.campusCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingRoomNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingSubRoomNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.vendorHeaderGeneratedIdentifier}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.vendorDetailAssignedIdentifier}" />
   </tr>
   
   <tr>
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
		field="campusCode" lookup="true" inquiry="true"
		boClassSimpleName="Campus" boPackageName="org.kuali.rice.kns.bo"/>
		
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
		field="buildingCode" lookup="true" inquiry="true"
		boClassSimpleName="Building" boPackageName="org.kuali.kfs.sys.businessobject"
		lookupOrInquiryKeys="campusCode,buildingCode"/>	
		
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
		field="buildingRoomNumber" lookup="true" inquiry="true"
		boClassSimpleName="Room" boPackageName="org.kuali.kfs.sys.businessobject"
		lookupOrInquiryKeys="campusCode,buildingCode,buildingRoomNumber"/>
		
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
		field="buildingSubRoomNumber" lookup="false" inquiry="false"/>			
		
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
		field="vendorHeaderGeneratedIdentifier" lookup="true" inquiry="true"
		boClassSimpleName="VendorDetail" boPackageName="org.kuali.kfs.vnd.businessobject"/>
		
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
		field="vendorDetailAssignedIdentifier" lookup="true" inquiry="true"
		boClassSimpleName="VendorDetail" boPackageName="org.kuali.kfs.vnd.businessobject"
		lookupOrInquiryKeys="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier"/>																 
   </tr>
   
   <tr><td colspan="6" class="infoline"><center><br/>
	   	<table style="border-top: 1px solid rgb(153, 153, 153); width: 90%;" cellpadding="0" cellspacing="0" class="datatable">       
		    <tr>
			  	<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetNumber}" align="right"/>        
			    <fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
					accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
					field="capitalAssetNumber" lookup="true" inquiry="true"
					boClassSimpleName="CapitalAssetManagementAsset" boPackageName="org.kuali.kfs.integration.cam" />
							
			  	<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetDescription}" align="right" rowspan="7"/>
				<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
					accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
					field="capitalAssetDescription" lookup="false" inquiry="false" rowSpan="7"/>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTypeCode}" align="right" />		
				<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
					accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
					field="capitalAssetTypeCode" lookup="true" inquiry="true"
					boClassSimpleName="CapitalAssetManagementAssetType" boPackageName="org.kuali.kfs.integration.cam" />
			</tr>				
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTagNumber}" align="right"/>
				<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
					accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
					field="capitalAssetTagNumber" lookup="false" inquiry="false"/>			
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetQuantity}" align="right"/>
				<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
					accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
					field="capitalAssetQuantity" lookup="false" inquiry="false"/>
			</tr>        
			<tr>
			   	<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerName}" align="right" />
			     	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
				field="capitalAssetManufacturerName" lookup="false" inquiry="false"/>
			</tr>
			<tr> 
			   	<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerModelNumber}" align="right" />      	
				<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
					accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
					field="capitalAssetManufacturerModelNumber" lookup="false" inquiry="false"/>
		    </tr>
		    <tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetSerialNumber}" align="right" />	     				
				<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
					accountingLine="${capitalAssetInfoName}" attributes="${attributes}" readOnly="false"
					field="capitalAssetSerialNumber" lookup="false" inquiry="false"/>
		    </tr>		    
	   	</table><br/><br/>
   </center></td></tr>
</table>