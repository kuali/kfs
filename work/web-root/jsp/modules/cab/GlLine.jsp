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
	window.open(popUpurl, popUpurl);
</c:if>
	
</script>
<kul:page showDocumentInfo="false" htmlFormAction="cabGlLine" renderMultipart="true"
	showTabButtons="true" docTitle="General Ledger Processing" 
	transactionalDocument="false" headerDispatch="true" headerTabActive="true"
	sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="start" >
	
	<kul:tabTop tabTitle="Financial Document Capital Asset Info" defaultOpen="true">
		<div class="tab-container" align=center>
		<c:set var="CapitalAssetInformationAttributes"	value="${DataDictionary.CapitalAssetInformation.attributes}" />	
		<c:set var="CapitalAssetInformationDetailAttributes"	value="${DataDictionary.CapitalAssetInformationDetail.attributes}" />
			<c:if test="${!empty KualiForm.capitalAssetInformation }">
			<table width="100%" cellpadding="0" cellspacing="0" class="datatable">			
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true" /></th>
				<td class="grid" width="25%">
				<html:link href="cabGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.capitalAssetInformation.documentNumber}">
				<kul:htmlControlAttribute property="capitalAssetInformation.documentNumber" attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true"/>
				</html:link>
				</td>			
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true" /></th>
				<td class="grid" width="25%">
				<kul:inquiry boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" keyValues="capitalAssetNumber=${KualiForm.capitalAssetInformation.capitalAssetNumber}" render="true">
				<kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true"/>
				</kul:inquiry>
				</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetTypeCode" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.vendorName" attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetQuantity" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerName" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerModelNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true"/></td>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetDescription" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true"/></td>
			</tr>			
		</table>
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
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
		</c:if>
		</div>
	</kul:tabTop>
	<kul:tab tabTitle="GL Entry Processing" defaultOpen="true">
		<div class="tab-container" align=center>
		<c:set var="entryAttributes"	value="${DataDictionary.GeneralLedgerEntry.attributes}" />
		<table width="95%" border="0" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
					<th><html:checkbox property="selectAllGlEntries" onclick="selectAll(this,'glselect');" />Select</th>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalYear}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalPeriodCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialDocumentTypeCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSystemOriginationCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.documentNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntryDescription}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.organizationDocumentNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.organizationReferenceId}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.referenceFinancialSystemOriginationCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.referenceFinancialDocumentNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.amount}" hideRequiredAsterisk="true" scope="col"/>
				</tr>
		<html:hidden property="primaryGlAccountId"/>
		<c:set var="pos" value="-1" />		   			 
    	<c:forEach var="entry" items="${KualiForm.relatedGlEntries}">
	 	<c:set var="pos" value="${pos+1}" />    	
			<tr>
				<td class="grid">
					<c:choose> 
					<c:when test="${entry.generalLedgerAccountIdentifier == KualiForm.primaryGlAccountId}">
						<html:checkbox property="relatedGlEntry[${pos}].selected" disabled="true" />
					</c:when>
					<c:when test="${!entry.active}">
						<a href="cabGlLine.do?methodToCall=viewDoc&documentNumber=${entry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}" target="_blank">						
						${entry.generalLedgerEntryAssets[0].capitalAssetManagementDocumentNumber}</a>
					</c:when>
					<c:otherwise> 
						<html:checkbox styleId="glselect" property="relatedGlEntry[${pos}].selected"/>
					</c:otherwise>
					</c:choose>
					<html:hidden property="relatedGlEntry[${pos}].generalLedgerAccountIdentifier"/>
				</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].universityFiscalYear" 
				attributeEntry="${entryAttributes.universityFiscalYear}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].universityFiscalPeriodCode" 
				attributeEntry="${entryAttributes.universityFiscalPeriodCode}" readOnly="true"/></td>
				<td class="grid">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${entry.chartOfAccountsCode}" render="true">
					<kul:htmlControlAttribute property="relatedGlEntry[${pos}].chartOfAccountsCode" 
					attributeEntry="${entryAttributes.chartOfAccountsCode}" readOnly="true"/>
				</kul:inquiry>
				</td>
				<td class="grid">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${entry.chartOfAccountsCode}&accountNumber=${entry.accountNumber}" render="true">
					<kul:htmlControlAttribute property="relatedGlEntry[${pos}].accountNumber" 
					attributeEntry="${entryAttributes.accountNumber}" readOnly="true"/>
					</kul:inquiry>
				</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].subAccountNumber" 
				attributeEntry="${entryAttributes.subAccountNumber}" readOnly="true"/></td>
				<td class="grid">
				<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" keyValues="universityFiscalYear=${entry.universityFiscalYear}&chartOfAccountsCode=${entry.chartOfAccountsCode}&financialObjectCode=${entry.financialObjectCode}" render="true">
				<kul:htmlControlAttribute property="relatedGlEntry[${pos}].financialObjectCode" 
				attributeEntry="${entryAttributes.financialObjectCode}" readOnly="true"/>
				</kul:inquiry>
				</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].financialSubObjectCode" 
				attributeEntry="${entryAttributes.financialSubObjectCode}" readOnly="true"/></td>
				<td class="grid">
				<kul:inquiry boClassName="org.kuali.rice.kns.bo.DocumentType" keyValues="documentTypeCode=${entry.financialDocumentTypeCode}" render="true">
				<kul:htmlControlAttribute property="relatedGlEntry[${pos}].financialDocumentTypeCode" 
				attributeEntry="${entryAttributes.financialDocumentTypeCode}" readOnly="true"/>
				</kul:inquiry>
				</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].financialSystemOriginationCode" 
				attributeEntry="${entryAttributes.financialSystemOriginationCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].documentNumber" 
				attributeEntry="${entryAttributes.documentNumber}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].transactionLedgerEntryDescription" 
				attributeEntry="${entryAttributes.transactionLedgerEntryDescription}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].organizationDocumentNumber" 
				attributeEntry="${entryAttributes.organizationDocumentNumber}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].organizationReferenceId" 
				attributeEntry="${entryAttributes.organizationReferenceId}" readOnly="true"/>&nbsp;</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].referenceFinancialSystemOriginationCode" 
				attributeEntry="${entryAttributes.referenceFinancialSystemOriginationCode}" readOnly="true"/>&nbsp;</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].referenceFinancialDocumentNumber" 
				attributeEntry="${entryAttributes.referenceFinancialDocumentNumber}" readOnly="true"/>&nbsp;</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntry[${pos}].amount" 
				attributeEntry="${entryAttributes.amount}" readOnly="true"/></td>
			</tr>
		</c:forEach>   			
    	</table>
		</div>
	</kul:tab>
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">	        
	    	<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_createasset.gif" property="methodToCall.submitAssetGlobal" title="Add Assets" alt="Add Assets"/>
	    	<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_applypayment.gif" property="methodToCall.submitPaymentGlobal" title="Add Payments" alt="Add Payments"/>
	    	<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_reload.gif" property="methodToCall.reload" title="Reload" alt="Reload"/>
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="Cancel" alt="Cancel"/>
        </c:if>		
    </div>
</kul:page>