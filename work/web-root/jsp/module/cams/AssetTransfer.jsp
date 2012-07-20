<%--
 Copyright 2005-2008 The Kuali Foundation
 
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
<%@ page import="org.kuali.kfs.sys.context.SpringContext" %>
<%@ page import="org.kuali.kfs.coa.service.AccountService" %>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true" htmlFormAction="camsAssetTransfer" documentTypeName="AssetTransferDocument" renderMultipart="true" showTabButtons="true">
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/module/cams/assetTransfer.js"></script>   
	<c:set var="assetTransferAttributes" value="${DataDictionary.AssetTransferDocument.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />	
	<c:set var="assetOrgAttributes" value="${DataDictionary.AssetOrganization.attributes}" />	
	<c:set var="organizationAttributes" value="${DataDictionary.Organization.attributes}" />	
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	<c:set var="accountsCanCrossCharts" value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />
	<c:set var="readOnlyChartCode" value="${readOnly or !accountsCanCrossCharts}" />
	<sys:documentOverview editingMode="${KualiForm.editingMode}" viewYearEndAccountPeriod="${!empty KualiForm.documentActions[KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION]}" />
    <cams:viewAssetDetails defaultTabHide="false" /> 
	
	  <kul:tab tabTitle="Asset Transfer Information" defaultOpen="true" tabErrorKey="document.organizationOwnerAccountNumber,document.organizationOwnerChartOfAccountsCode,document.transferOfFundsFinancialDocumentNumber"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Old</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">Transfer from Originating Organization</td><td class="tab-subhead"  colspan="2" width="50%">Transfer to Receiving Organization</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.oldOrganizationOwnerChartOfAccountsCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.oldOrganizationOwnerChartOfAccountsCode" attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" /></th>
				<td class="grid" width="25%">
	                <c:if test="${readOnlyChartCode}">	
	                	<span id="document.organizationOwnerChartOfAccountsCode"><bean:write name="KualiForm" property="document.organizationOwnerChartOfAccountsCode"/></span> 
	                </c:if>
					<c:if test="${not readOnlyChartCode}">				
						<kul:htmlControlAttribute property="document.organizationOwnerChartOfAccountsCode" attributeEntry="${assetTransferAttributes.organizationOwnerChartOfAccountsCode}" readOnly="false"/>
						&nbsp; 
		                <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart" fieldConversions="chartOfAccountsCode:document.organizationOwnerChartOfAccountsCode" lookupParameters="document.organizationOwnerChartOfAccountsCode:chartOfAccountsCode" />
	                </c:if>
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.oldOrganizationOwnerAccountNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.oldOrganizationOwnerAccountNumber" attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationOwnerAccountNumber" attributeEntry="${assetTransferAttributes.organizationOwnerAccountNumber}" 
                    onblur="onblur_accountNumber(this, 'organizationOwnerChartOfAccountsCode');" 					
					readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account" fieldConversions="accountNumber:document.organizationOwnerAccountNumber,chartOfAccountsCode:document.organizationOwnerChartOfAccountsCode" lookupParameters="document.organizationOwnerAccountNumber:accountNumber,document.organizationOwnerChartOfAccountsCode:chartOfAccountsCode" />
	                </c:if>
				</td>				
			</tr>

			<tr>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${organizationAttributes.organizationCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.oldOrganizationOwnerAccount.organizationCode" attributeEntry="${organizationAttributes.organizationCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${organizationAttributes.organizationCode}"/></th>
                <td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationOwnerAccount.organizationCode" attributeEntry="${organizationAttributes.organizationCode}" readOnly="true" /></td>
 			</tr>

			<tr>
				<td class="tab-subhead" colspan="4" width="100%">Interdepartmental Sale</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.interdepartmentalSalesIndicator}" /></th>
				<td class="grid" colspan="3"><kul:htmlControlAttribute property="document.interdepartmentalSalesIndicator" attributeEntry="${assetTransferAttributes.interdepartmentalSalesIndicator}" readOnly="${readOnly}"/></td>						
			</tr>
			<tr>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.transferOfFundsFinancialDocumentNumber}" /></th>
				<td class="grid" colspan="3"><kul:htmlControlAttribute property="document.transferOfFundsFinancialDocumentNumber" attributeEntry="${assetTransferAttributes.transferOfFundsFinancialDocumentNumber}" readOnly="${readOnly}"/></td>
			</tr>
		</table>
		</div>
	 </kul:tab>
	 
    <kul:tab tabTitle="Asset Location" defaultOpen="true" tabErrorKey="document.locationTabKey,document.campusCode,document.buildingCode,document.buildingRoomNumber,document.offCampus*,assetLocationErrorSection"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<td class="tab-subhead"  colspan="2" width="50%">On Campus</td><td class="tab-subhead"  colspan="2" width="50%">Off Campus</td>
			</tr>			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.campusCode}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.campusCode" attributeEntry="${assetTransferAttributes.campusCode}" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.campus.CampusEbo" fieldConversions="code:document.campusCode" lookupParameters="document.campusCode:code" />
		            </c:if>
				</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusName}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusName" attributeEntry="${assetTransferAttributes.offCampusName}" readOnly="${readOnly}"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingCode}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.buildingCode" attributeEntry="${assetTransferAttributes.buildingCode}" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building" fieldConversions="buildingCode:document.buildingCode,campusCode:document.campusCode" lookupParameters="document.buildingCode:buildingCode,document.campusCode:campusCode" />
	                </c:if>
				</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusAddress}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusAddress" attributeEntry="${assetTransferAttributes.offCampusAddress}" readOnly="${readOnly}"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingRoomNumber}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.buildingRoomNumber" attributeEntry="${assetTransferAttributes.buildingRoomNumber}" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Room" fieldConversions="buildingRoomNumber:document.buildingRoomNumber,buildingCode:document.buildingCode,campusCode:document.campusCode" lookupParameters="document.buildingRoomNumber:buildingRoomNumber,document.buildingCode:buildingCode,document.campusCode:campusCode" />
	                </c:if>
				</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCityName}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusCityName" attributeEntry="${assetTransferAttributes.offCampusCityName}" readOnly="${readOnly}"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.buildingSubRoomNumber" attributeEntry="${assetTransferAttributes.buildingSubRoomNumber}" readOnly="${readOnly}"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusStateCode}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusStateCode" attributeEntry="${assetTransferAttributes.offCampusStateCode}" readOnly="${readOnly}"/>						
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.state.StateEbo" fieldConversions="code:document.offCampusStateCode" lookupParameters="document.offCampusCountryCode:postalCountryCode,document.offCampusStateCode:code,document.offCampusZipCode:postalCode" />
	                </c:if>
				</td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right" colspan="2"></th>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusZipCode}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusZipCode" attributeEntry="${assetTransferAttributes.offCampusZipCode}" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.postalcode.PostalCodeEbo" fieldConversions="code:document.offCampusZipCode" lookupParameters="document.offCampusCountryCode:countryCode,document.offCampusZipCode:code,document.offCampusStateCode:stateCode" />
					</c:if>		                
				</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right" colspan="2"></th>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.offCampusCountryCode}" /></th>  
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.offCampusCountryCode" attributeEntry="${assetTransferAttributes.offCampusCountryCode}" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.country.CountryEbo" fieldConversions="code:document.offCampusCountryCode" lookupParameters="document.offCampusCountryCode:code" />
	                </c:if>
				</td>
			</tr>
		</table>
		</div>
	</kul:tab>

	<!-- Organization Information -->
	<kul:tab tabTitle="Organization Information" defaultOpen="true" tabErrorKey="document.assetRepresentative.principalName" > 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">			
			<tr>
				<td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">Current</div></td><td class="tab-subhead" colspan="2" width="50%"><div class="tab-subhead-r">New</div></td>
			</tr>					
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.organizationInventoryName" attributeEntry="${assetTransferAttributes.organizationInventoryName}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationInventoryName}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationInventoryName" attributeEntry="${assetTransferAttributes.organizationInventoryName}" readOnly="${readOnly}"/></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetRepresentative.name" attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.representativeUniversalIdentifier}" /></th>
				<td class="grid" width="25%">
					<kul:checkErrors keyMatch="document.assetRepresentative.principalName" />
					<kul:user userIdFieldName="document.assetRepresentative.principalName" universalIdFieldName="document.representativeUniversalIdentifier" userNameFieldName="document.assetRepresentative.name" label="User" 
					lookupParameters="document.assetRepresentative.principalName:principalName,document.representativeUniversalIdentifier:principalId,document.assetRepresentative.name:name" 
					fieldConversions="principalName:document.assetRepresentative.principalName,principalId:document.representativeUniversalIdentifier,name:document.assetRepresentative.name" 
					userId="${KualiForm.document.assetRepresentative.principalName}" universalId="${KualiForm.document.representativeUniversalIdentifier}" userName="${KualiForm.document.assetRepresentative.name}" 
					hasErrors="${hasErrors}" readOnly="${readOnly}"/>
				</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationText}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationText" attributeEntry="${assetTransferAttributes.organizationText}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationText}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationText" attributeEntry="${assetTransferAttributes.organizationText}" readOnly="${readOnly}"/></td>						
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.asset.assetOrganization.organizationTagNumber" attributeEntry="${assetTransferAttributes.organizationTagNumber}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetTransferAttributes.organizationTagNumber}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.organizationTagNumber" attributeEntry="${assetTransferAttributes.organizationTagNumber}" readOnly="${readOnly}"/></td>						
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
	<kul:superUserActions />
	<kul:panelFooter />
	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>
