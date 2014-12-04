<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<script>
<c:if test="${!empty KualiForm.currDocNumber}">
	var popUpurl = 'cabCapitalAssetInformation.do?methodToCall=viewDoc&documentNumber=${KualiForm.currDocNumber}';
	window.open(popUpurl, "${KualiForm.currDocNumber}");
</c:if>
	
</script>
<kul:page showDocumentInfo="false" htmlFormAction="cabCapitalAssetInformation" renderMultipart="true"
	showTabButtons="true" docTitle="Capital Asset Information Processing" 
	transactionalDocument="false" headerDispatch="true" headerTabActive="true"
	sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="start" >
	
	<kul:tabTop tabTitle="Financial Document Capital Asset Info" defaultOpen="true">
		<div class="tab-container" align=center>
		<c:set var="CapitalAssetInformationAttributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
		<c:set var="dataCellCssClass" value="datacell" />
		<c:set var="capitalAssetInfoName" value="KualiForm.capitalAssetInformation" />
	    <div align="center" vAlign="middle">
	    <h3>Capital Assets for Accounting Line</h3>
		<table datatable style="border-top: 1px solid rgb(153, 153, 153); width: 100%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
			<tr>
			 	<c:if test="${!readOnly}">
					<kul:htmlAttributeHeaderCell literalLabel="Action"/>
				</c:if>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetActionIndicator}" labelFor="${capitalAssetInfoName}.capitalAssetActionIndicator"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" labelFor="${capitalAssetInfoName}.capitalAssetLineNumber"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" labelFor="${capitalAssetInfoName}.capitalAssetNumber"/> 
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" labelFor="${capitalAssetInfoName}.capitalAssetQuantity"/> 
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" labelFor="${capitalAssetInfoName}.capitalAssetTypeCode"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.vendorName}" labelFor="${capitalAssetInfoName}.vendorName"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerName"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerModelNumber"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" labelFor="${capitalAssetInfoName}.capitalAssetDescription"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.distributionAmountCode}" labelFor="${capitalAssetInfoName}.distributionAmountCode"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineAmount}" labelFor="${capitalAssetInfoName}.capitalAssetLineAmount"/>
			</tr>
			<c:set var="capitalAssetPosition" value="0" />			
			<c:forEach items="${KualiForm.capitalAssetInformation}" var="detailLine" varStatus="status">
			<c:set var="capitalAssetPosition" value="${capitalAssetPosition+1}" />			
			<tr>
				<c:if test="${!readOnly}">
					<c:if test="${detailLine.capitalAssetProcessedIndicator == true}" >
				    	<td class="grid" align="center"><div vAlign="middle"><b>Processed</b></div></td>
					</c:if>	
					<c:if test="${detailLine.capitalAssetProcessedIndicator == false}" >
				    	<td class="grid" align="center">
			    			<html:link target="_blank" href="cabGlLine.do?methodToCall=process&documentNumber=${detailLine.documentNumber}&generalLedgerAccountIdentifier=${KualiForm.generalLedgerAccountIdentifier}&capitalAssetLineNumber=${detailLine.capitalAssetLineNumber}">
								Process
							</html:link>
						</td>
					</c:if>
				</c:if>
				<td class="grid"><div>
					<c:set var="assetActionType" value="${detailLine.capitalAssetActionIndicator}" />
				    	<c:if test="${assetActionType eq KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR}">
					    	<c:out value="${KFSConstants.CapitalAssets.CREATE_CAPITAL_ASSETS_TAB_TITLE}" />
					    </c:if>
				        <c:if test="${assetActionType eq KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR}">
					    	<c:out value="${KFSConstants.CapitalAssets.MODIFY_CAPITAL_ASSETS_TAB_TITLE}" />
					    </c:if>
				</div></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetLineNumber" 
					attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" readOnly="true"/></td>
				<td class="grid">
					<c:if test="${!empty detailLine.capitalAssetNumber}">
						<kul:inquiry boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" keyValues="capitalAssetNumber=${detailLine.capitalAssetNumber}" render="true">
							<kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true"/>
						</kul:inquiry>
					</c:if>&nbsp;
				</td>	
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetQuantity" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetTypeCode" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].vendorName" 
								attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetManufacturerName" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetManufacturerModelNumber" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetDescription" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true"/></td>
				
				<c:set var="distributionAmountCode" value="${detailLine.distributionAmountCode}" />
				<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE}">
					<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_DESCRIPTION}" />
				</c:if>
				<c:if test="${distributionAmountCode eq KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE}">
					<c:set var="distributionAmountDescription" value="${KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_DESCRIPTION}" />
				</c:if>
				
				<td>	
					<div><c:out value="${distributionAmountDescription}"/></div>
				</td>	
				
				<td class="grid">
					<div align="right" valign="middle">
						<kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetLineAmount" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineAmount}" readOnly="true"/>
					</div></td>
			</tr>
			<tr>
				<td colspan="11">
					<cab:groupAccountingLinesDetails capitalAssetInformation="${detailLine}" capitalAssetPosition="${capitalAssetPosition}" showViewButton="true"/>
				</td>
			</tr>
			</c:forEach>	
	    </table>
		</div>
	</kul:tabTop>
	
	<kul:panelFooter />	
	
	<%--
	<div id="globalbuttons" class="globalbuttons">
	    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_reload.gif" styleClass="globalbuttons" property="methodToCall.reload" title="reload" alt="reload"/>
    </div>	
    --%>
</kul:page>
