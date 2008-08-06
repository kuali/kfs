<%--
 Copyright 2005-2008 The Kuali Foundation.
 
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
<kul:page docTitle="New Assets" transactionalDocument="false"
	sessionDocument="false" headerMenuBar="" showDocumentInfo="false"
	headerTitle="New Assets" htmlFormAction="cabGlLine"
	renderMultipart="true" showTabButtons="true" headerDispatch="true"
	headerTabActive="true" feedbackKey="true" defaultMethodToCall="refresh" >
	
	<c:set var="generalLedgerEntryAttributes" value="${DataDictionary.GeneralLedgerEntry.attributes}" />
	<c:set var="glAssetAttributes" value="${DataDictionary.GeneralLedgerEntryAsset.attributes}" />
	<c:set var="glAssetDetailAttributes" value="${DataDictionary.GeneralLedgerEntryAssetDetail.attributes}" />
	<html:hidden property="generalLedgerEntry.generalLedgerAccountIdentifier" />
	<html:hidden property="newAssetIndicator" />
	<kul:tabTop tabTitle="General Ledger Entry" defaultOpen="true">
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.transactionLedgerEntrySequenceNumber}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.universityFiscalYear}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.projectCode}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.financialDocumentTypeCode}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.financialBalanceTypeCode}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.financialObjectTypeCode}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.transactionLedgerEntryAmount}" hideRequiredAsterisk="true" scope="col"/>
	            <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerEntryAttributes.transactionDebitCreditCode}" hideRequiredAsterisk="true" scope="col"/>
			</tr>
			<tr>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.transactionLedgerEntrySequenceNumber"	attributeEntry="${generalLedgerEntryAttributes.transactionLedgerEntrySequenceNumber}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.universityFiscalYear"	attributeEntry="${generalLedgerEntryAttributes.universityFiscalYear}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.chartOfAccountsCode"	attributeEntry="${generalLedgerEntryAttributes.chartOfAccountsCode}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.accountNumber"	attributeEntry="${generalLedgerEntryAttributes.accountNumber}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.subAccountNumber"	attributeEntry="${generalLedgerEntryAttributes.subAccountNumber}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.financialObjectCode"	attributeEntry="${generalLedgerEntryAttributes.financialObjectCode}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.financialSubObjectCode"	attributeEntry="${generalLedgerEntryAttributes.financialSubObjectCode}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.projectCode"	attributeEntry="${generalLedgerEntryAttributes.projectCode}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.financialDocumentTypeCode"	attributeEntry="${generalLedgerEntryAttributes.financialDocumentTypeCode}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.financialBalanceTypeCode"	attributeEntry="${generalLedgerEntryAttributes.financialBalanceTypeCode}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.financialObjectTypeCode"	attributeEntry="${generalLedgerEntryAttributes.financialObjectTypeCode}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.transactionLedgerEntryAmount"	attributeEntry="${generalLedgerEntryAttributes.transactionLedgerEntryAmount}"  readOnly="true" /></td>
				<td><kul:htmlControlAttribute property="generalLedgerEntry.transactionDebitCreditCode"	attributeEntry="${generalLedgerEntryAttributes.transactionDebitCreditCode}"  readOnly="true" /></td>
			</tr>
		</table>
		</div>
	</kul:tabTop>	
	<kul:tab tabTitle="Asset Information" defaultOpen="true">
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<th class="grid" align="right"><kul:htmlAttributeLabel attributeEntry="${glAssetAttributes.capitalAssetDescription}" /></th>
				<td class="grid"><kul:htmlControlAttribute property="newGeneralLedgerEntryAsset.capitalAssetDescription" attributeEntry="${glAssetAttributes.capitalAssetDescription}" /></td>
			</tr>
			<tr>
				<th class="grid" align="right"><kul:htmlAttributeLabel attributeEntry="${glAssetAttributes.capitalAssetTypeCode}" /></th>
				<td class="grid"><kul:htmlControlAttribute property="newGeneralLedgerEntryAsset.capitalAssetTypeCode" attributeEntry="${glAssetAttributes.capitalAssetTypeCode}" /></td>
			</tr>
			<tr>
				<th class="grid" align="right"><kul:htmlAttributeLabel attributeEntry="${glAssetAttributes.vendorName}" /></th>
				<td class="grid"><kul:htmlControlAttribute property="newGeneralLedgerEntryAsset.vendorName" attributeEntry="${glAssetAttributes.vendorName}" /></td>
			</tr>
			<tr>
				<th class="grid" align="right"><kul:htmlAttributeLabel attributeEntry="${glAssetAttributes.manufacturerName}" /></th>
				<td class="grid"><kul:htmlControlAttribute property="newGeneralLedgerEntryAsset.manufacturerName" attributeEntry="${glAssetAttributes.manufacturerName}" /></td>
			</tr>
			<tr>
				<th class="grid" align="right"><kul:htmlAttributeLabel attributeEntry="${glAssetAttributes.manufacturerModelNumber}" /></th>
				<td class="grid"><kul:htmlControlAttribute property="newGeneralLedgerEntryAsset.manufacturerModelNumber" attributeEntry="${glAssetAttributes.manufacturerModelNumber}" /></td>
			</tr>
		</table>
		</div>
	</kul:tab>
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons"><c:if
		test="${not readOnly}">
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif"
			styleClass="globalbuttons" property="methodToCall.save" title="save"
			alt="save" />

		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif"
			styleClass="globalbuttons" property="methodToCall.close"
			title="close" alt="close" />
	</c:if></div>
</kul:page>

