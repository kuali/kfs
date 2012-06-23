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
<c:set var="amountReadOnly" value="${readOnly}" />
<c:if test="${KualiForm.distributeEqualAmount}">
	<c:set var="amountReadOnly" value="true" />
</c:if>
<c:set var="totalColumnSpan" value="7"/>

<table class="datatable" cellpadding="0" cellspacing="0" summary="Capital Asset Information">
	<tr>
		<td colspan="7" class="tab-subhead" style="border-top: medium;">Modify Assets</td>
	</tr>
	<tr>
		<td colspan="2" class="tab-subhead" style="border-top: medium;">
	   		<br/>System Control Amount: <c:out value="${KualiForm.systemControlAmount}" />
	   	</td>
	   	<c:set var="totalColumnSpan" value="${totalColumnSpan-2}"/>
	   	<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}" >
	   		<c:set var="totalColumnSpan" value="2"/>
	   	</c:if>
	   	<c:if test="${KualiForm.createdAssetsControlAmount == 0.00}" >
	   		<c:set var="totalColumnSpan" value="3"/>
	   	</c:if>
	   	
	   	<td colspan="${totalColumnSpan}" class="tab-subhead" style="border-top: medium;">
	   		<br/>System Control Remainder Amount: <c:out value="${KualiForm.createdAssetsControlAmount}" />
	   	</td>
	   	<c:if test="${KualiForm.createdAssetsControlAmount != 0.00}" >
	   		<td colspan="1" class="tab-subhead" style="border-top: medium;"><br/>
				<div align="center" valign="bottom">	
					<html:image property="methodToCall.redistributeModifyCapitalAssetAmount" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-redtotamt.gif" 
						title="Redistribute Total Amount for modify capital assets"
						alt="Redistribute Total Amount for modify capital assets" styleClass="tinybutton" />
				</div>
			</td>	
	   		<td colspan="1" class="tab-subhead" style="border-top: medium;">
	   			<div align="right">
	   				<br/>Lookup/Add Multiple Capital Asset Lines <kul:multipleValueLookup boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" lookedUpCollectionName="capitalAssetManagementAssets" />
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

		<c:if test="${detailLine.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR}">
			<tr><td colspan="8">
	     	<div align="center" valign="middle">
	     		<h3>Capital Asset for Accounting Line</h3>
				<c:if test="${not empty detailLine.capitalAssetAccountsGroupDetails}" >
					<tr><td colSpan="8"><center><br/>
					<fp:capitalAssetAccountsGroupDetails capitalAssetAccountsGroupDetails="${detailLine.capitalAssetAccountsGroupDetails}" 
						capitalAssetAccountsGroupDetailsName="${capitalAssetInfoName}[${status.index}].capitalAssetAccountsGroupDetails" readOnly="${readOnly}"
						capitalAssetAccountsGroupDetailsIndex="${status.index}"/>
					<br/></center></td></tr>
			    </c:if>
		   </tr>
			<tr>
				<td colspan="8">
		     		<div align="center" valign="middle">
			     		<table datatable style="border-top: 1px solid rgb(153, 153, 153); width: 60%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
						   <tr>
								<kul:htmlAttributeHeaderCell literalLabel=""/>	   
						   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetNumber}" labelFor="${capitalAssetInfoName}.capitalAssetNumber"/> 
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.distributionAmountCode}" labelFor="${capitalAssetInfoName}.distributionAmountCode"/>
								<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetLineAmount}" labelFor="${capitalAssetInfoName}.capitalAssetLineAmount"/>
								<c:if test="${!readOnly}">
									<kul:htmlAttributeHeaderCell literalLabel="Action"/>
								</c:if>
						   </tr>
						   <tr>
						   		<kul:htmlAttributeHeaderCell literalLabel="${detailLine.capitalAssetLineNumber}"/></td>

						   		<fp:dataCell dataCellCssClass="${dataCellCssClass}"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
									field="capitalAssetNumber" lookup="true" inquiry="true"
									boClassSimpleName="CapitalAssetManagementAsset" boPackageName="org.kuali.kfs.integration.cam"
									lookupUnkeyedFieldConversions="capitalAssetNumber:${capitalAssetInfoName}.capitalAssetTagNumber,"
									lookupOrInquiryKeys="capitalAssetNumber"
									businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/></td>

								<td>	
									<div><c:out value="${distributionAmountDescription}"/></div>
								</td>	
									
								<fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
									businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${amountReadOnly}"
									field="capitalAssetLineAmount" lookup="false" inquiry="false" /></td>
									
								<c:if test="${!readOnly}">
									<td class="infoline"> 
											<div style="text-align: center;">
												<html:image property="methodToCall.refreshCapitalAssetModify.line${status.index}.Anchor" 
													src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif" 
													title="Refresh Modify capital Asset Information"
													alt="Refresh Modify capital Asset Information" styleClass="tinybutton" />&nbsp;	
												<html:image property="methodToCall.deleteCapitalAssetModify.line${status.index}.Anchor" 
													src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" 
													title="Delete the capital Asset Information"
													alt="Delete the capital Asset Information" styleClass="tinybutton" />&nbsp;
												<html:image property="methodToCall.clearCapitalAssetModify.line${status.index}.Anchor" 
													src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" 
													title="Clear the capital Asset Information"
													alt="Clear the capital Asset Information" styleClass="tinybutton" />
											</div>
									</td>										
								</c:if>		
						   </tr>
						</table>
					</div>
		   		</td>
			</tr>
		</c:if>
	</c:forEach>	
</table>
