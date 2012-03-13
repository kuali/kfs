<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>	

<script language="JavaScript" type="text/javascript" src="dwr/interface/VendorService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vnd/objectInfo.js"></script>
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
<c:set var="dataCellCssClass" value="datacell" />
<c:set var="capitalAssetInfoName" value="document.capitalAssetInformation" />

<c:set var="totalColumnSpan" value="8"/>
<c:set var="amountReadOnly" value="${readOnly}" />
<c:if test="${KualiForm.distributeEqualAmount}">
	<c:set var="amountReadOnly" value="true" />
</c:if>

<table class="datatable" cellpadding="0" cellspacing="0" summary="Capital Asset Information">
    <tr>
   		<td colspan="8" class="tab-subhead" style="border-top: medium;">Create New Assets</td>
    </tr>
	<tr>
   		<td colspan="3" class="tab-subhead" style="border-top: medium;">
   			<br/>System Control Amount: <c:out value="${KualiForm.systemControlAmount}" />
   		</td>
	   	<c:set var="totalColumnSpan" value="${totalColumnSpan-4}"/>
	   	<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}" >
	   		<c:set var="totalColumnSpan" value="4"/>
	   	</c:if>
	   	<c:if test="${KualiForm.createdAssetsControlAmount == 0.00}" >
	   		<c:set var="totalColumnSpan" value="4"/>
	   	</c:if>
	   	<td colspan="${totalColumnSpan}" class="tab-subhead" style="border-top: medium;">
	   		<br/>System Control Remainder Amount: <c:out value="${KualiForm.createdAssetsControlAmount}" />
	   	</td>
   		<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}">
	   		<td colspan="1" class="tab-subhead" style="border-top: medium;"><br/>
				<div align="center" valign="bottom">	
					<html:image property="methodToCall.redistributeCreateCapitalAssetAmount" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-redtotamt.gif" 
						title="Redistribute Total Amount for new capital assets"
						alt="Redistribute Total Amount for new capital assets" styleClass="tinybutton" />	
				</div>
	   		</td>
   		</c:if>
  	</tr>
	<c:forEach items="${KualiForm.document.capitalAssetInformation}" var="detailLine" varStatus="status">
		<c:set var="distributionAmountCode" value="${detailLine.distributionAmountCode}" />
		<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE}">
			<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_DESCRIPTION}" />
			<c:set var="amountReadOnly" value="true" />
		</c:if>
		<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE}">
			<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_DESCRIPTION}" />
			<c:set var="amountReadOnly" value="false" />
		</c:if>

		<c:if test="${detailLine.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR}">
			<tr><td colspan="8">
	     	<div align="center" vAlign="middle">
	     		<h3>Capital Asset for Accounting Lines</h3>
				<c:if test="${not empty detailLine.capitalAssetAccountsGroupDetails}" >
					<tr><td colSpan="8"><center><br/>
					<fp:capitalAssetAccountsGroupDetails capitalAssetAccountsGroupDetails="${detailLine.capitalAssetAccountsGroupDetails}" 
						capitalAssetAccountsGroupDetailsName="${capitalAssetInfoName}[${status.index}].capitalAssetAccountsGroupDetails" readOnly="${readOnly}"
						capitalAssetAccountsGroupDetailsIndex="${status.index}"/>
					<br/></center></td></tr>
			    </c:if>
				</div></td>	
		   </tr>
		   
			<tr>
				<td colspan="8">
		     		<div align="center" valign="middle">
			     		<table datatable style="border-top: 1px solid rgb(153, 153, 153); width: 96%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
						   <tr> 
								<kul:htmlAttributeHeaderCell literalLabel=""/>	   
						   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetQuantity}" labelFor="${capitalAssetInfoName}.capitalAssetQuantity"/> 
						   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTypeCode}" labelFor="${capitalAssetInfoName}.capitalAssetTypeCode"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.vendorName}" labelFor="${capitalAssetInfoName}.vendorName"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerName}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerName"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerModelNumber}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerModelNumber"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.distributionAmountCode}" labelFor="${capitalAssetInfoName}.distributionAmountCode"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetLineAmount}" labelFor="${capitalAssetInfoName}.capitalAssetLineAmount"/>
						   </tr>
		 
						   <tr>
						   		<kul:htmlAttributeHeaderCell literalLabel="${detailLine.capitalAssetLineNumber}"/>	
								<fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
									field="capitalAssetQuantity" lookup="false" inquiry="false" />
					
								<fp:dataCell dataCellCssClass="${dataCellCssClass}"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
									field="capitalAssetTypeCode" lookup="true" inquiry="true"
									boClassSimpleName="CapitalAssetManagementAssetType" boPackageName="org.kuali.kfs.integration.cam" 
									lookupOrInquiryKeys="capitalAssetTypeCode"
									businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>		
									   
								<fp:dataCell dataCellCssClass="${dataCellCssClass}"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
									field="vendorName" lookup="true" inquiry="true" disabled="true"
									boClassSimpleName="VendorDetail" boPackageName="org.kuali.kfs.vnd.businessobject"
									lookupOrInquiryKeys="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier,vendorName"
									businessObjectValuesMap="${capitalAssetInfo.valuesMap}" />	
								
								<fp:dataCell dataCellCssClass="${dataCellCssClass}"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
									field="capitalAssetManufacturerName" lookup="false" inquiry="false"/>
								
								<fp:dataCell dataCellCssClass="${dataCellCssClass}"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
									field="capitalAssetManufacturerModelNumber" lookup="false" inquiry="false"/>
								
								<td>	
									<div><c:out value="${distributionAmountDescription}"/></div>
								</td>	
								
								<fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${amountReadOnly}"
									field="capitalAssetLineAmount" lookup="false" inquiry="false" />
									
						   </tr>
							<c:if test="${readOnly}">
							<c:set var="assetDescription" value="${detailLine.capitalAssetDescription}" />
							   <tr>
									<th class="grid" width="5%" align="right"><kul:htmlAttributeLabel attributeEntry="${attributes.capitalAssetDescription}" readOnly="true" /></th>
									<td class="grid" width="95%" colspan="7"><kul:htmlControlAttribute property="${capitalAssetInfoName}[${status.index}].capitalAssetDescription" attributeEntry="${attributes.capitalAssetDescription}" readOnly="true"/></td>
							   </tr>
							</c:if>
							<c:if test="${!readOnly}">
							   <tr>
							 		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetDescription}" rowspan="${readOnly ? 1 : 2 }"/>
									<fp:dataCell dataCellCssClass="${dataCellCssClass}"
										businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="false" disabled="false"
										field="capitalAssetDescription" lookup="false" inquiry="false" colSpan="6" rowSpan="2"/>
									<c:if test="${!readOnly}">
										<kul:htmlAttributeHeaderCell literalLabel="Action"/>
									</c:if>
							   </tr>
						   </c:if>
							<c:if test="${!readOnly}">
								<tr>
									<td class="infoline">  
										<div align="center" valign="middle">
											<html:image property="methodToCall.insertCapitalAssetInfo.line${status.index}.Anchor" 
												src="${ConfigProperties.externalizable.images.url}tinybutton-insert.gif" 
												title="Add the capital Asset Information"
												alt="Add the capital Asset Information" styleClass="tinybutton" />&nbsp;	
											<html:image property="methodToCall.deleteCapitalAssetInfo.line${status.index}.Anchor" 
												src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" 
												title="Delete the capital Asset Information"
												alt="Delete the capital Asset Information" styleClass="tinybutton" />&nbsp;
											<html:image property="methodToCall.clearCapitalAssetInfo.line${status.index}.Anchor" 
												src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" 
												title="Clear the capital Asset Information"
												alt="Clear the capital Asset Information" styleClass="tinybutton" />
											<br /><br />
											<html:image property="methodToCall.addCapitalAssetTagLocationInfo.line${status.index}.Anchor" 
												src="${ConfigProperties.externalizable.images.url}tinybutton-addtaglocation.gif" 
												title="Add the capital Asset tag and location"
												alt="Add the capital Asset tag and location" styleClass="tinybutton" />	
										</div>
									</td>
								</tr>
							</c:if>
							
							<c:if test="${not empty detailLine.capitalAssetInformationDetails}" >
							   <tr><td colSpan="8"><center><br/>
									<fp:capitalAssetInfoDetail capitalAssetInfoDetails="${detailLine.capitalAssetInformationDetails}" 
										capitalAssetInfoDetailsName="${capitalAssetInfoName}[${status.index}].capitalAssetInformationDetails" readOnly="${readOnly}"
										capitalAssetInfoIndex="${status.index}"/>
								<br/></center></td></tr>
							</c:if>
						</table>
					</div>
		   		</td>
			</tr>
		</c:if>
	</c:forEach>	
</table>
