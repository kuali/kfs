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

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="camsEquipmentLoanOrReturn"
	documentTypeName="EquipmentLoanOrReturnDocument" renderMultipart="true"
	showTabButtons="true">

	<c:set var="eqipAttributes" value="${DataDictionary.EquipmentLoanOrReturnDocument.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />
	<c:set var="displayNewLoanTab" value="${KualiForm.editingMode['displayNewLoanTab']}" scope="request"/>
	
	<html:hidden property="document.asset.capitalAssetNumber" />
	<html:hidden property="document.assetHeader.capitalAssetNumber" />
	<html:hidden property="document.assetHeader.documentNumber" />
	<html:hidden property="document.assetHeader.versionNumber" />	
	<html:hidden property="document.borrowerUniversalIdentifier" /> 
	<kul:hiddenDocumentFields isFinancialDocument="false" />

    <kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<kul:tab tabTitle="Asset" defaultOpen="true"> 
	    <div class="tab-container" align="center">
	      	<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      		<tr>
					<td class="tab-subhead"  width="100%" colspan="2">Asset Information</td>
				</tr>	
		     	<tr>
		      		<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetNumber}" /></th>
		      		<td class="grid" width="75%"><kul:htmlControlAttribute property="document.asset.capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}"/>  </td>
		     	</tr>
		      	<tr>
		      		<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" /></th>
		      		<td class="grid" width="75%"><kul:htmlControlAttribute property="document.asset.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>		      	
		     	</tr>		    
		 	</table>   
      	</div>
	</kul:tab>

	<kul:tab tabTitle="Equipment Loan" defaultOpen="true"> 
	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
                <td colspan="4" class="tab-subhead">Equipment Loan Information</td>
			</tr>	
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerUniversalIdentifier}" /></th>
		      	<td class="grid" width="25%">
				<kul:user userIdFieldName="document.borrowerUniversalUser.personUserIdentifier" universalIdFieldName="document.borrowerUniversalIdentifier" userNameFieldName="document.borrowerUniversalUser.personName" label="User" 
				lookupParameters="document.borrowerUniversalUser.personUserIdentifier:personUserIdentifier,document.borrowerUniversalIdentifier:personUniversalIdentifier,document.borrowerUniversalUser.personName:personName" 
				fieldConversions="personUserIdentifier:document.borrowerUniversalUser.personUserIdentifier,personUniversalIdentifier:document.borrowerUniversalIdentifier,personName:document.borrowerUniversalUser.personName" 
				userId="${KualiForm.document.borrowerUniversalUser.personUserIdentifier}" universalId="${KualiForm.document.borrowerUniversalIdentifier}" userName="${KualiForm.document.borrowerUniversalUser.personName}"/>
		      	<kul:htmlControlAttribute property="document.borrowerUniversalUser.personName" attributeEntry="${eqipAttributes.borrowerUniversalIdentifier}" />		      	


				</td>

				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.loanDate}"/></th>
                <td class="grid" width="25%"> 	                        
                	<kul:htmlControlAttribute attributeEntry="${eqipAttributes.loanDate}" property="document.loanDate" readOnly="true" />
                </td>                          
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.expectedReturnDate}" /></th>
                <td class="grid" width="25%"><kul:dateInput attributeEntry="${eqipAttributes.expectedReturnDate}" property="document.expectedReturnDate"/> </td>
	            <c:choose>
	                <c:when test="${displayNewLoanTab}">
						<th class="grid" width="25%" align="right" colspan="2"></th>
	                </c:when>
	                <c:otherwise>
					    <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.loanReturnDate}" /></th>
			            <td class="grid" width="25%"><kul:dateInput attributeEntry="${eqipAttributes.loanReturnDate}" property="document.loanReturnDate"/></td> 
	                </c:otherwise>
	            </c:choose>
       		</tr>		    
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.signatureCode}"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.signatureCode" attributeEntry="${eqipAttributes.signatureCode}"/></td>								
				<td class="grid" width="25%" colspan="2"></td>
			</tr>
		</table>   
	</kul:tab>

	<kul:tab tabTitle="Borrower's Address" defaultOpen="true"> 
	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
				<td class="tab-subhead"  width="50%" colspan="2">Borrower</td>
				<td class="tab-subhead"  width="50%" colspan="2">Stored at</td>				
			</tr>	
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerAddress}"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerAddress" attributeEntry="${eqipAttributes.borrowerAddress}"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageAddress}"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageAddress" attributeEntry="${eqipAttributes.borrowerStorageAddress}"/></td>								
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerCityName}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerCityName" attributeEntry="${eqipAttributes.borrowerCityName}"/></td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageCityName}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageCityName" attributeEntry="${eqipAttributes.borrowerStorageCityName}"/></td>		      	
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStateCode}"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStateCode" attributeEntry="${eqipAttributes.borrowerStateCode}"/>								
                </td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageStateCode}"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageStateCode" attributeEntry="${eqipAttributes.borrowerStorageStateCode}"/>								
                </td>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerZipCode}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerZipCode" attributeEntry="${eqipAttributes.borrowerZipCode}"/></td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageZipCode}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageZipCode" attributeEntry="${eqipAttributes.borrowerStorageZipCode}"/></td>		      	
		    </tr>		    
		    <tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerCountryCode}"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerCountryCode" attributeEntry="${eqipAttributes.borrowerCountryCode}"/>								
				</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageCountryCode}"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageCountryCode" attributeEntry="${eqipAttributes.borrowerStorageCountryCode}"/>								
				</td>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerPhoneNumber}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerPhoneNumber" attributeEntry="${eqipAttributes.borrowerPhoneNumber}"/></td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStoragePhoneNumber}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStoragePhoneNumber" attributeEntry="${eqipAttributes.borrowerStoragePhoneNumber}"/></td>		      	
		    </tr>		    
		  </table>   
        </div>
	  </kul:tab>
    
    <cams:assetLocation defaultTabHide="true" />  
	<cams:organizationInfo defaultTabHide="true"/>

	<cams:viewAssetDetails defaultTabHide="true" /> 
	<cams:viewPayments 		defaultTabHide="true" assetPayments="${KualiForm.document.asset.assetPayments}" />	
  
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>