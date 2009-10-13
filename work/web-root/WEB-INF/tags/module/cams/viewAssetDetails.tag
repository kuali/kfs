<%--
 Copyright 2006-2009 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<%@ attribute name="assetValueObj" type="java.lang.String" required="false" description="Asset object name" %>
<%@ attribute name="assetValue" type="org.kuali.kfs.module.cam.businessobject.Asset" required="false" description="Asset object value" %>
<c:if test="${assetValueObj==null}">
	<c:set var="assetValueObj" value="document.asset" />
	<c:set var="assetValue" value="${KualiForm.document.asset}" />
</c:if>

<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
	<kul:tab tabTitle="Asset" defaultOpen="${!defaultTabHide}" tabErrorKey="document.asset.capitalAssetNumber,commonErrorSection"> 
		<div class="tab-container" align="center">		
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
	      	<tr>
				<td class="tab-subhead"  width="100%" colspan="4">Asset Information</td>
			</tr>	
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true"/></th>
		      	<td class="grid" width="75%" colspan="3">
		      		<kul:htmlControlAttribute property="${assetValueObj}.capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true" readOnlyBody="true">
						<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.Asset" keyValues="capitalAssetNumber=${assetValue.capitalAssetNumber}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.capitalAssetNumber" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
		    </tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true" /></th>
				<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="${assetValueObj}.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" /></th>
				<td class="grid" width="25%">
					<kul:htmlControlAttribute property="${assetValueObj}.organizationOwnerChartOfAccountsCode" attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" readOnlyBody="true">								
		      			<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=" render="true">
                			<html:hidden write="true" property="${assetValueObj}.organizationOwnerChartOfAccountsCode" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" /></th>
				<td class="grid" width="25%">
					<kul:htmlControlAttribute property="${assetValueObj}.organizationOwnerAccountNumber" attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" readOnlyBody="true">								
						<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}&amp;accountNumber=${assetValue.organizationOwnerAccountNumber}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.organizationOwnerAccountNumber" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.agencyNumber}" readOnly="true" /></th>
				<td class="grid" width="25%">
					<kul:htmlControlAttribute property="${assetValueObj}.agencyNumber" attributeEntry="${assetAttributes.agencyNumber}" readOnly="true" readOnlyBody="true">								
						<kul:inquiry boClassName="org.kuali.kfs.module.cg.businessobject.Agency" keyValues="agencyNumber=${assetValue.agencyNumber}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.agencyNumber" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
		        <th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${accountAttributes.organizationCode}" readOnly="true" /></th>
	   	        <td class="grid" width="25%">
	   	        	<kul:htmlControlAttribute property="${assetValueObj}.organizationOwnerAccount.organizationCode" attributeEntry="${assetAttributes.organizationOwnerAccount.organizationCode}" readOnly="true" readOnlyBody="true"> 
						<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}&amp;accountNumber=${assetValue.organizationOwnerAccountNumber}&amp;organizationCode=${assetValue.organizationOwnerAccount.organizationCode}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.organizationOwnerAccount.organizationCode" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.inventoryStatusCode}" readOnly="true" /></th>
				<td class="grid" width="25%">
	   	        	<kul:htmlControlAttribute property="${assetValueObj}.inventoryStatusCode" attributeEntry="${assetAttributes.inventoryStatusCode}" readOnly="true" readOnlyBody="true"> 
						<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.AssetStatus" keyValues="inventoryStatusCode=${assetValue.inventoryStatusCode}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.inventoryStatusCode" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.conditionCode}" readOnly="true" /></th>
				<td class="grid" width="25%">
	   	        	<kul:htmlControlAttribute property="${assetValueObj}.conditionCode" attributeEntry="${assetAttributes.conditionCode}" readOnly="true" readOnlyBody="true"> 
						<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.AssetCondition" keyValues="assetConditionCode=${assetValue.conditionCode}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.conditionCode" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.acquisitionTypeCode}" readOnly="true" /></th>
				<td class="grid" width="25%">
	   	        	<kul:htmlControlAttribute property="${assetValueObj}.acquisitionTypeCode" attributeEntry="${assetAttributes.acquisitionTypeCode}" readOnly="true" readOnlyBody="true"> 
						<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType" keyValues="acquisitionTypeCode=${assetValue.acquisitionTypeCode}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.acquisitionTypeCode" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true" /></th>
				<td class="grid" width="25%">
	   	        	<kul:htmlControlAttribute property="${assetValueObj}.capitalAssetTypeCode" attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true" readOnlyBody="true"> 
						<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.AssetType" keyValues="capitalAssetTypeCode=${assetValue.capitalAssetTypeCode}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.capitalAssetTypeCode" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.vendorName}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.vendorName" attributeEntry="${assetAttributes.vendorName}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerName}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.manufacturerName" attributeEntry="${assetAttributes.manufacturerName}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerModelNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.manufacturerModelNumber" attributeEntry="${assetAttributes.manufacturerModelNumber}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.serialNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.serialNumber" attributeEntry="${assetAttributes.serialNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.campusTagNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.campusTagNumber" attributeEntry="${assetAttributes.campusTagNumber}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.oldTagNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.oldTagNumber" attributeEntry="${assetAttributes.oldTagNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.governmentTagNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.governmentTagNumber" attributeEntry="${assetAttributes.governmentTagNumber}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.nationalStockNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.nationalStockNumber" attributeEntry="${assetAttributes.nationalStockNumber}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.capitalAssetInServiceDate" attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.createDate}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.createDate" attributeEntry="${assetAttributes.createDate}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
				<td class="grid" width="25%">
	   	        	<kul:htmlControlAttribute property="${assetValueObj}.financialDocumentPostingYear" attributeEntry="${assetAttributes.financialDocumentPostingYear}" readOnly="true" readOnlyBody="true"> 
						<kul:inquiry boClassName="org.kuali.kfs.sys.businessobject.SystemOptions" keyValues="universityFiscalYear=${assetValue.financialDocumentPostingYear}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.financialDocumentPostingYear" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.lastInventoryDate}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.lastInventoryDate" attributeEntry="${assetAttributes.lastInventoryDate}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
				<td class="grid" width="25%">
	   	        	<kul:htmlControlAttribute property="${assetValueObj}.financialDocumentPostingPeriodCode" attributeEntry="${assetAttributes.financialDocumentPostingPeriodCode}" readOnly="true" readOnlyBody="true"> 
						<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.AccountingPeriod" keyValues="universityFiscalYear=${assetValue.financialDocumentPostingYear}&amp;universityFiscalPeriodCode=${assetValue.financialDocumentPostingPeriodCode}" render="true">
                			<html:hidden write="true" property="${assetValueObj}.financialDocumentPostingPeriodCode" />
                		</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
		      	</td>
				<th class="grid" width="50%" colspan="2"></th>
			</tr>

		</table>
		</div>
	</kul:tab>
