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
<kul:documentPage showDocumentInfo="true" htmlFormAction="camsAssetTransfer" documentTypeName="AssetTransferDocument" renderMultipart="true" showTabButtons="true">
	<kul:hiddenDocumentFields />
	<c:set var="assetTransferAttributes" value="${DataDictionary.AssetTransferDocument.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	<html:hidden property="assetTransferDocument.asset.capitalAssetNumber" />
	<html:hidden property="assetTransferDocument.assetHeader.capitalAssetNumber" />
	<html:hidden property="assetTransferDocument.assetHeader.documentNumber" />
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<kul:tab tabTitle="Asset" defaultOpen="true"> 
	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
				<td class="tab-subhead"  width="100%" colspan="4">Asset Information</td>
			</tr>	
		     <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetNumber}" /></th>
		      	<td class="grid" width="75%" colspan="3">
		      		<kul:htmlControlAttribute property="assetTransferDocument.asset.capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true" />
				</td>		      	
		     </tr>
		      <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" /></th>
		      	<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="assetTransferDocument.asset.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>		      	
		     </tr>		    
		  </table>   
        </div>
	  </kul:tab>
	  <kul:tab tabTitle="Asset Transfer Information" defaultOpen="true"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Old</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">Transfer to Receiving Organization</td><td class="tab-subhead"  colspan="2" width="50%">Transfer to Receiving Organization</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.organizationOwnerChartOfAccountsCode" attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.organizationOwnerChartOfAccountsCode" attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" />
					&nbsp;
	                <kul:lookup boClassName="org.kuali.module.chart.bo.Chart" fieldConversions="chartOfAccountsCode:assetTransferDocument.organizationOwnerChartOfAccountsCode" lookupParameters="assetTransferDocument.organizationOwnerChartOfAccountsCode:chartOfAccountsCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.organizationOwnerAccountNumber" attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.organizationOwnerAccountNumber" attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.module.chart.bo.Account" fieldConversions="organizationCode:assetTransferDocument.organizationOwnerAccount.organizationCode,accountNumber:assetTransferDocument.organizationOwnerAccountNumber,chartOfAccountsCode:assetTransferDocument.organizationOwnerChartOfAccountsCode" lookupParameters="assetTransferDocument.organizationOwnerAccountNumber:accountNumber,assetTransferDocument.organizationOwnerChartOfAccountsCode:chartOfAccountsCode" />
				</td>				
			</tr>
			<tr>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.ownerOrganizationCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.organizationOwnerAccount.organizationCode" attributeEntry="${assetTransferAttributes.ownerOrganizationCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.ownerOrganizationCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.organizationOwnerAccount.organizationCode" attributeEntry="${assetTransferAttributes.ownerOrganizationCode}" readOnly="true" /></td>
			</tr>
			<tr>
				<td class="tab-subhead" colspan="4" width="100%">Interdepartmental Sale</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.interdepartmentalSalesIndicator}" /></th>
				<td class="grid" colspan="3"><kul:htmlControlAttribute property="assetTransferDocument.interdepartmentalSalesIndicator" attributeEntry="${assetTransferAttributes.interdepartmentalSalesIndicator}" /></td>						
			</tr>
			<tr>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.transferOfFundsFinancialDocumentNumber}" /></th>
				<td class="grid" colspan="3"><kul:htmlControlAttribute property="assetTransferDocument.transferOfFundsFinancialDocumentNumber" attributeEntry="${assetTransferAttributes.transferOfFundsFinancialDocumentNumber}" /></td>
			</tr>
		</table>
		</div>
	 </kul:tab>
	 <cams:viewAssetDetails documentName="assetTransferDocument" defaultTabHide="true" />
	  <kul:tab tabTitle="Asset Location" defaultOpen="true"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Old</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>
			<!-- On campus information -->
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">On Campus</td><td class="tab-subhead"  colspan="2" width="50%">On Campus</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.campusCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.campusCode" attributeEntry="${assetAttributes.campusCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.campusCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.campusCode" attributeEntry="${assetTransferAttributes.campusCode}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.core.bo.Campus" fieldConversions="campusCode:assetTransferDocument.campusCode" lookupParameters="assetTransferDocument.campusCode:campusCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.buildingCode" attributeEntry="${assetTransferAttributes.buildingCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.buildingCode" attributeEntry="${assetTransferAttributes.buildingCode}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.kfs.bo.Building" fieldConversions="buildingCode:assetTransferDocument.buildingCode,campusCode:assetTransferDocument.campusCode" lookupParameters="assetTransferDocument.buildingCode:buildingCode,assetTransferDocument.campusCode:campusCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.buildingRoomNumber" attributeEntry="${assetTransferAttributes.buildingRoomNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.buildingRoomNumber" attributeEntry="${assetTransferAttributes.buildingRoomNumber}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.kfs.bo.Room" fieldConversions="buildingRoomNumber:assetTransferDocument.buildingRoomNumber,buildingCode:assetTransferDocument.buildingCode,campusCode:assetTransferDocument.campusCode" lookupParameters="assetTransferDocument.buildingRoomNumber:buildingRoomNumber,assetTransferDocument.buildingCode:buildingCode,assetTransferDocument.campusCode:campusCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.buildingSubRoomNumber" attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.buildingSubRoomNumber" attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" />
				</td>						
			</tr>
			<!-- Off campus information -->
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">Off Campus</td><td class="tab-subhead"  colspan="2" width="50%">Off Campus</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusAddress}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.offCampusLocation.assetLocationStreetAddress" attributeEntry="${assetTransferAttributes.offCampusAddress}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusAddress}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.offCampusAddress" attributeEntry="${assetTransferAttributes.offCampusAddress}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCityName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.offCampusLocation.assetLocationCityName" attributeEntry="${assetTransferAttributes.offCampusCityName}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCityName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.offCampusCityName" attributeEntry="${assetTransferAttributes.offCampusCityName}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusStateCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.offCampusLocation.assetLocationStateCode" attributeEntry="${assetTransferAttributes.offCampusStateCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusStateCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.offCampusStateCode" attributeEntry="${assetTransferAttributes.offCampusStateCode}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.kfs.bo.State" fieldConversions="postalStateCode:assetTransferDocument.offCampusStateCode" lookupParameters="assetTransferDocument.offCampusStateCode:postalStateCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.offCampusLocation.assetLocationZipCode" attributeEntry="${assetTransferAttributes.offCampusZipCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.offCampusZipCode" attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></td>						
			</tr>			
		</table>
		</div>
	</kul:tab>
	<!-- Organization Information -->
	<kul:tab tabTitle="Organization Information" defaultOpen="true"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Old</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>					
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.assetOrganization.organizationDescription" attributeEntry="${assetTransferAttributes.organizationInventoryName}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.organizationInventoryName" attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.assetRepresentative.personName" attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" /></th>
				<td class="grid" width="25%">						
				<kul:user userIdFieldName="assetTransferDocument.assetRepresentative.personUserIdentifier" universalIdFieldName="assetTransferDocument.representativeUniversalIdentifier" userNameFieldName="assetTransferDocument.assetRepresentative.personName" label="User" 
				lookupParameters="assetTransferDocument.assetRepresentative.personUserIdentifier:personUserIdentifier,assetTransferDocument.representativeUniversalIdentifier:personUniversalIdentifier,assetTransferDocument.assetRepresentative.personName:personName" 
				fieldConversions="personUserIdentifier:assetTransferDocument.assetRepresentative.personUserIdentifier,personUniversalIdentifier:assetTransferDocument.representativeUniversalIdentifier,personName:assetTransferDocument.assetRepresentative.personName" 
				userId="${KualiForm.assetTransferDocument.assetRepresentative.personUserIdentifier}" universalId="${KualiForm.assetTransferDocument.representativeUniversalIdentifier}" userName="${KualiForm.assetTransferDocument.assetRepresentative.personName}"/>
				</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationText}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.assetOrganization.organizationText" attributeEntry="${assetTransferAttributes.organizationText}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationText}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.organizationText" attributeEntry="${assetTransferAttributes.organizationText}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.asset.assetOrganization.organizationTagNumber" attributeEntry="${assetTransferAttributes.organizationTagNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="assetTransferDocument.organizationTagNumber" attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></td>						
			</tr>		
		</table>
		</div>
	</kul:tab>		
	<cams:viewDepreciationInfo documentName="assetTransferDocument" defaultTabHide="true" />
	<cams:viewPayments documentName="assetTransferDocument" defaultTabHide="true" assetPayments="${KualiForm.assetTransferDocument.asset.assetPayments}" />
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>