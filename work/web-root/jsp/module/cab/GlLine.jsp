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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<script>
		function selectAll(all, styleid){
		var elms = document.getElementsByTagName("input");
		for(var i=0; i< elms.length; i++){
			if(elms[i].id !=null  && elms[i].id==styleid && !elms[i].disabled){
				elms[i].checked = all.checked;
			}
		}
	}

<c:if test="${!empty KualiForm.currDocNumber}">
	var popUpurl = 'cabGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.currDocNumber}';
	window.open(popUpurl, "${KualiForm.currDocNumber}");
</c:if>
	
</script>
<kul:page showDocumentInfo="false" htmlFormAction="cabGlLine" renderMultipart="true"
	showTabButtons="true" docTitle="General Ledger Processing" 
	transactionalDocument="false" headerDispatch="true" headerTabActive="true"
	sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="start" >
	
	<kul:tabTop tabTitle="Financial Document Capital Asset Info" defaultOpen="true">
		<div class="tab-container" align=center>
		<c:set var="CapitalAssetInformationAttributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
		<c:set var="CapitalAssetInformationDetailAttributes" value="${DataDictionary.CapitalAssetInformationDetail.attributes}" />
			<c:if test="${!empty KualiForm.capitalAssetInformation }">
	    	<div align="center" vAlign="middle">
	    	<c:if test="${KualiForm.capitalAssetInformation.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR}" >
	    		<h3><c:out value="${KFSConstants.CapitalAssets.CREATE_CAPITAL_ASSETS_TAB_TITLE}"/></h3>
	    	</c:if>
	    	<c:if test="${KualiForm.capitalAssetInformation.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR}" >
	    		<h3><c:out value="${KFSConstants.CapitalAssets.MODIFY_CAPITAL_ASSETS_TAB_TITLE}"/></h3>
	    	</c:if>
			<table width="100%" cellpadding="0" cellspacing="0" class="datatable">			
				<tr>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true" /></th>
					<td class="grid" width="25%">
					<html:link target="_blank" href="cabGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.capitalAssetInformation.documentNumber}">
					<kul:htmlControlAttribute property="capitalAssetInformation.documentNumber" attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true"/>
					</html:link>
					</td>			
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetLineNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" readOnly="true"/></td>
				</tr>
				<tr>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true" /></th>
					<td class="grid" width="25%">
						<c:if test="${!empty KualiForm.capitalAssetInformation.capitalAssetNumber}">
							<kul:inquiry boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" keyValues="capitalAssetNumber=${KualiForm.capitalAssetInformation.capitalAssetNumber}" render="true">
								<kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true"/>
							</kul:inquiry>
						</c:if>
						&nbsp;
					</td>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetTypeCode" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true"/></td>
				</tr>
				<tr>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.vendorName" attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true"/></td>
				
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetQuantity" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true"/></td>
				</tr>
				<tr>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerName" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true"/></td>
				
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerModelNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true"/></td>				
				</tr>
				<tr>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetDescription" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true"/></td>
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineAmount}" readOnly="true" /></th>
					<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetLineAmount" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineAmount}" readOnly="true"/></td>
				</tr>
				<tr>
					<c:set var="distributionAmountCode" value="${KualiForm.capitalAssetInformation.distributionAmountCode}" />
					<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE}">
						<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_DESCRIPTION}" />
					</c:if>
					<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE}">
						<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_DESCRIPTION}" />
					</c:if>
				
					<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.distributionAmountCode}" readOnly="true" /></th>
					<td class="grid" width="25%"><div><c:out value="${distributionAmountDescription}"/></div></td>
				
					<td colSpan="2">&nbsp;</td>
				</tr>
				<tr>
					<td colSpan="4">
						<cab:groupAccountingLinesDetails capitalAssetInformation="${KualiForm.capitalAssetInformation}" capitalAssetPosition="1" showViewButton="false"/>
					</td>
				</tr>
				<tr>
					<td colSpan="4">
						<c:if test="${!empty KualiForm.capitalAssetInformation.capitalAssetInformationDetails}">
						<div class="tab-container" align="center">
							<table width="80%" cellpadding="0" cellspacing="0" class="datatable"  align="center"
	       						style="width: 80%; text-align: left;">
								<tr>
									<td class="tab-subhead" colspan="6">Capital Asset Tag/Location Details</td>
								</tr>	
								<tr>
									<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.campusCode}" hideRequiredAsterisk="true" scope="col"/>
									<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.buildingCode}" hideRequiredAsterisk="true" scope="col"/>
									<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.buildingRoomNumber}" hideRequiredAsterisk="true" scope="col"/>
									<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.buildingSubRoomNumber}" hideRequiredAsterisk="true" scope="col"/>
									<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetTagNumber}" hideRequiredAsterisk="true" scope="col"/>
									<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetSerialNumber}" hideRequiredAsterisk="true" scope="col"/>
								</tr>			
							<c:forEach var="assetDetail" items="${KualiForm.capitalAssetInformation.capitalAssetInformationDetails}" varStatus="current">
								<tr>
									<td class="grid" width="15%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].campusCode" attributeEntry="${CapitalAssetInformationDetailAttributes.campusCode}" readOnly="true"/></td>			
									<td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].buildingCode" attributeEntry="${CapitalAssetInformationDetailAttributes.buildingCode}" readOnly="true"/></td>
									<td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].buildingRoomNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.buildingRoomNumber}" readOnly="true"/></td>
									<td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].buildingSubRoomNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.buildingSubRoomNumber}" readOnly="true"/></td>
									<td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].capitalAssetTagNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetTagNumber}" readOnly="true"/></td>
									<td class="grid" width="17%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetInformationDetails[${current.index}].capitalAssetSerialNumber" attributeEntry="${CapitalAssetInformationDetailAttributes.capitalAssetSerialNumber}" readOnly="true"/></td>
								</tr>
							</c:forEach>
							</table>
						</div>
						</c:if>
					</td>
				</tr>		
			</table>
			</div>
	</c:if>
	</div>
		<c:choose> 
			<c:when test="${KualiForm.generalLedgerEntry.generalLedgerAccountIdentifier == KualiForm.primaryGlAccountId && KualiForm.generalLedgerEntry.active}">
				<c:set var="allowSubmit" value="true" />
			</c:when>
			<c:when test="${!KualiForm.generalLedgerEntry.active}">
				<a href="cabGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.generalLedgerEntry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}" target="${KualiForm.generalLedgerEntry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}">						
					${KualiForm.generalLedgerEntry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}</a>
			</c:when>
			<c:otherwise> 
				<c:set var="allowSubmit" value="true" />
			</c:otherwise>
		</c:choose>
	
	</kul:tabTop>
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
        	<c:if test="${!empty allowSubmit}">	        
	    		<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_createasset.gif" property="methodToCall.submitAssetGlobal" title="Add Assets" alt="Add Assets"/>
	    		<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_applypayment.gif" property="methodToCall.submitPaymentGlobal" title="Add Payments" alt="Add Payments"/>
	    	</c:if>
	    	<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_reload.gif" property="methodToCall.reload" title="Reload" alt="Reload"/>
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="Cancel" alt="Cancel"/>
        </c:if>		
    </div>
</kul:page>
