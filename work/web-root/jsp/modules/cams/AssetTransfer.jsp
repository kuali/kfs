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
	<c:set var="assetTransferAttributes" value="${DataDictionary.AssetTransferDocument.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />	
	<c:set var="assetOrgAttributes" value="${DataDictionary.AssetOrganization.attributes}" />
	<html:hidden property="loanNoteAdded" />
	<html:hidden property="document.asset.capitalAssetNumber" />
	<html:hidden property="document.capitalAssetNumber" />
	<html:hidden property="document.asset.expectedReturnDate" />
	<html:hidden property="document.asset.loanReturnDate" />
	<kul:hiddenDocumentFields isFinancialDocument="false" />
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
		      		<kul:htmlControlAttribute property="document.capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true" />
				</td>		      	
		     </tr>
		      <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" /></th>
		      	<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>		      	
		     </tr>		    
		  </table>   
        </div>
	  </kul:tab>
	  <kul:tab tabTitle="Asset Transfer Information" defaultOpen="true" tabErrorKey="document.organizationOwnerAccountNumber,document.organizationOwnerChartOfAccountsCode,document.transferOfFundsFinancialDocumentNumber"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Old</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">Transfer to Receiving Organization</td><td class="tab-subhead"  colspan="2" width="50%">Transfer to Receiving Organization</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.organizationOwnerChartOfAccountsCode" attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationOwnerChartOfAccountsCode" attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" />
					&nbsp;
	                <kul:lookup boClassName="org.kuali.module.chart.bo.Chart" fieldConversions="chartOfAccountsCode:document.organizationOwnerChartOfAccountsCode" lookupParameters="document.organizationOwnerChartOfAccountsCode:chartOfAccountsCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.organizationOwnerAccountNumber" attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationOwnerAccountNumber" attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.module.chart.bo.Account" fieldConversions="organizationCode:document.organizationOwnerAccount.organizationCode,accountNumber:document.organizationOwnerAccountNumber,chartOfAccountsCode:document.organizationOwnerChartOfAccountsCode" lookupParameters="document.organizationOwnerAccountNumber:accountNumber,document.organizationOwnerChartOfAccountsCode:chartOfAccountsCode" />
				</td>				
			</tr>
			<tr>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.organizationOwnerAccount.organizationCode" attributeEntry="${assetTransferAttributes.organizationCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationOwnerAccount.organizationCode" attributeEntry="${assetTransferAttributes.organizationCode}" readOnly="true" /></td>
			</tr>
			<tr>
				<td class="tab-subhead" colspan="4" width="100%">Interdepartmental Sale</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.interdepartmentalSalesIndicator}" /></th>
				<td class="grid" colspan="3"><kul:htmlControlAttribute property="document.interdepartmentalSalesIndicator" attributeEntry="${assetTransferAttributes.interdepartmentalSalesIndicator}" /></td>						
			</tr>
			<tr>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.transferOfFundsFinancialDocumentNumber}" /></th>
				<td class="grid" colspan="3"><kul:htmlControlAttribute property="document.transferOfFundsFinancialDocumentNumber" attributeEntry="${assetTransferAttributes.transferOfFundsFinancialDocumentNumber}" /></td>
			</tr>
		</table>
		</div>
	 </kul:tab>
	 <cams:viewAssetDetails defaultTabHide="true" /> 
	  <kul:tab tabTitle="Asset Location" defaultOpen="true" tabErrorKey="document.locationTabKey,document.campusCode,document.buildingCode,document.buildingRoomNumber,document.offCampus*"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Old</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>
			<!-- On campus information -->
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.campusCode}"  readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.campusCode" attributeEntry="${assetTransferAttributes.campusCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.campusCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.campusCode" attributeEntry="${assetTransferAttributes.campusCode}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.core.bo.Campus" fieldConversions="campusCode:document.campusCode" lookupParameters="document.campusCode:campusCode" />
				</td>						
			</tr>
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">On Campus</td><td class="tab-subhead"  colspan="2" width="50%">On Campus</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingCode" attributeEntry="${assetTransferAttributes.buildingCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.buildingCode" attributeEntry="${assetTransferAttributes.buildingCode}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.kfs.bo.Building" fieldConversions="buildingCode:document.buildingCode,campusCode:document.campusCode" lookupParameters="document.buildingCode:buildingCode,document.campusCode:campusCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingRoomNumber" attributeEntry="${assetTransferAttributes.buildingRoomNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.buildingRoomNumber" attributeEntry="${assetTransferAttributes.buildingRoomNumber}" />
				&nbsp;
                <kul:lookup boClassName="org.kuali.kfs.bo.Room" fieldConversions="buildingRoomNumber:document.buildingRoomNumber,buildingCode:document.buildingCode,campusCode:document.campusCode" lookupParameters="document.buildingRoomNumber:buildingRoomNumber,document.buildingCode:buildingCode,document.campusCode:campusCode" />
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.buildingSubRoomNumber" attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.buildingSubRoomNumber" attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" />
				</td>						
			</tr>
			<!-- Off campus information -->
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">Off Campus</td><td class="tab-subhead"  colspan="2" width="50%">Off Campus</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusAddress}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationStreetAddress" attributeEntry="${assetTransferAttributes.offCampusAddress}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusAddress}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusAddress" attributeEntry="${assetTransferAttributes.offCampusAddress}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCityName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationCityName" attributeEntry="${assetTransferAttributes.offCampusCityName}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCityName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusCityName" attributeEntry="${assetTransferAttributes.offCampusCityName}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusStateCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationStateCode" attributeEntry="${assetTransferAttributes.offCampusStateCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusStateCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusStateCode" attributeEntry="${assetTransferAttributes.offCampusStateCode}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.offCampusLocation.assetLocationZipCode" attributeEntry="${assetTransferAttributes.offCampusZipCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusZipCode" attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></td>						
			</tr>			
		</table>
		</div>
	</kul:tab>
	<!-- Organization Information -->
	<kul:tab tabTitle="Organization Information" defaultOpen="true" tabErrorKey="document.assetRepresentative.personUserIdentifier" > 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">			
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Old</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>					
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.organizationInventoryName" attributeEntry="${assetTransferAttributes.organizationInventoryName}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationInventoryName" attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetRepresentative.personName" attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" /></th>
				<td class="grid" width="25%">
				<kul:checkErrors keyMatch="document.assetRepresentative.personUserIdentifier" />
				<kul:user userIdFieldName="document.assetRepresentative.personUserIdentifier" universalIdFieldName="document.representativeUniversalIdentifier" userNameFieldName="document.assetRepresentative.personName" label="User" 
				lookupParameters="document.assetRepresentative.personUserIdentifier:personUserIdentifier,document.representativeUniversalIdentifier:personUniversalIdentifier,document.assetRepresentative.personName:personName" 
				fieldConversions="personUserIdentifier:document.assetRepresentative.personUserIdentifier,personUniversalIdentifier:document.representativeUniversalIdentifier,personName:document.assetRepresentative.personName" 
				userId="${KualiForm.document.assetRepresentative.personUserIdentifier}" universalId="${KualiForm.document.representativeUniversalIdentifier}" userName="${KualiForm.document.assetRepresentative.personName}" renderOtherFields="true" hasErrors="${hasErrors}" />
				</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationText}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationText" attributeEntry="${assetTransferAttributes.organizationText}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationText}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationText" attributeEntry="${assetTransferAttributes.organizationText}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationTagNumber" attributeEntry="${assetTransferAttributes.organizationTagNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationTagNumber" attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetOrgAttributes.organizationAssetTypeIdentifier}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationAssetTypeIdentifier" attributeEntry="${assetOrgAttributes.organizationAssetTypeIdentifier}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right">&nbsp;</th>
				<td class="grid" width="25%">&nbsp;</td>						
			</tr>		
		</table>
		</div>
	</kul:tab>		
	<cams:viewPayments defaultTabHide="true" assetPayments="${KualiForm.document.asset.assetPayments}" />
	<cams:viewDepreciationInfo defaultTabHide="true" />
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>