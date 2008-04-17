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

<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>

<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />

<kul:tab tabTitle="Asset Detail Information" defaultOpen="${!defaultTabHide}">		
    <div class="tab-container" align=center > 
	    <div class="h2-container">
	        <h2>Asset Information</h2>
	    </div>
		<table cellpadding=0 class="datatable" summary="Asset Information Section">
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}"/></div></th>
	        <td align=left valign=middle class="grid">
	            <kul:htmlControlAttribute attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" property="document.asset.organizationOwnerChartOfAccountsCode" readOnly="${!fullEntryMode}"/>  
	        </td>
	                      
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerAccountNumber}"/></div></th>
	        <td align=left valign=middle class="grid">
	        	<kul:htmlControlAttribute attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" property="document.asset.organizationOwnerAccountNumber" readOnly="${!fullEntryMode}"/>
	        </td>
	      </tr>
	
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.agencyNumber}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.agencyNumber}" property="document.asset.agencyNumber" readOnly="${!fullEntryMode}"/>
	        </td>
	      
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerAccount.organizationCode}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.organizationOwnerAccount.organizationCode}" property="document.asset.organizationOwnerAccount.organizationCode" readOnly="${!fullEntryMode}"/>  
	        </td>
	      </tr>
	            
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.campusTagNumber}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.campusTagNumber}" property="document.asset.campusTagNumber" readOnly="${!fullEntryMode}"/>  
	        </td>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.serialNumber}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.serialNumber}" property="document.asset.serialNumber" readOnly="${!fullEntryMode}"/>
	        </td>
	      </tr>
	            
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.acquisitionTypeCode}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.acquisitionTypeCode}" property="document.asset.acquisitionTypeCode" readOnly="${!fullEntryMode}"/>  
	        </td>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.inventoryStatusCode}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.inventoryStatusCode}" property="document.asset.inventoryStatusCode" readOnly="${!fullEntryMode}"/>  
	        </td>
	      </tr>
	
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.createDate}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.createDate}" property="document.asset.createDate" readOnly="${!fullEntryMode}"/>  
	        </td>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetInServiceDate}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.capitalAssetInServiceDate}" property="document.asset.capitalAssetInServiceDate" readOnly="${!fullEntryMode}"/>  
	        </td>
	      </tr>
	
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.lastInventoryDate}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.lastInventoryDate}" property="document.asset.lastInventoryDate" readOnly="${!fullEntryMode}"/>  
	        </td>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.depreciationDate}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.lastInventoryDate}" property="document.asset.depreciationDate" readOnly="${!fullEntryMode}"/>  
	        </td>
	      </tr>
	
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetTypeCode}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.capitalAssetTypeCode}" property="document.asset.capitalAssetTypeCode" readOnly="${!fullEntryMode}"/>  
	        </td>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.assetOrganization.organizationText}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.assetOrganization.organizationText}" property="document.asset.assetOrganization.organizationText" readOnly="${!fullEntryMode}"/>  
	        </td>
	      </tr>
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.vendorName}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.vendorName}" property="document.asset.vendorName" readOnly="${!fullEntryMode}"/>  
	        </td>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerName}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.manufacturerName}" property="document.asset.manufacturerName" readOnly="${!fullEntryMode}"/>  
	        </td>
	      </tr>


	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.conditionCode}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.conditionCode}" property="document.asset.conditionCode" readOnly="${!fullEntryMode}"/>  
	        </td>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerModelNumber}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.manufacturerModelNumber}" property="document.asset.manufacturerModelNumber" readOnly="${!fullEntryMode}"/>  
	        </td>
	      </tr>

	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.totalCostAmount}"/></div></th>
	        <td align=left valign=middle class="grid">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.totalCostAmount}" property="document.asset.totalCostAmount" readOnly="${!fullEntryMode}"/>  
	        </td>
			<th align=right valign=middle class="grid"><div align="right">&nbsp;</div></th>
	        <th align=right valign=middle class="grid"><div align="right">&nbsp;</div></th>
	      </tr>
	      
	      <tr>
	        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}"/></div></th>
	        <td align=left valign=middle class="grid" colspan="3">
	           <kul:htmlControlAttribute attributeEntry="${assetAttributes.asset.capitalAssetDescription}" property="document.asset.capitalAssetDescription" readOnly="${!fullEntryMode}"/>          
	        </td>
	      </tr>
	      
	     </table>
     </div>
</kul:tab>