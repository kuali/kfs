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
<%@ attribute name="chkcount" required="true" description="The total check number"%>
<%@ attribute name="docPos" required="true" description="The index of the CAB PurAp Document"%>
<%@ attribute name="linePos" required="true" description="The index of CAB PurAp item asset"%>
<%@ attribute name="itemLine" required="true" type="org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset" description="determine row span number for additional charge"%>
<script language="JavaScript" type="text/javascript" src="scripts/cab/selectCheckBox.js"></script>

<c:set var="purApDocumentAttributes" value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}" />
<c:set var="purApItemAssetAttributes" value="${DataDictionary.PurchasingAccountsPayableItemAsset.attributes}" />
<c:set var="purApLineAssetAccountsAttributes" value="${DataDictionary.PurchasingAccountsPayableLineAssetAccount.attributes}" />
<c:set var="generalLedgerAttributes" value="${DataDictionary.GeneralLedgerEntry.attributes}" />
<c:set var="financialSystemDocumentHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />
<c:set var="genericAttributes" value="${DataDictionary.GenericAttributes.attributes}" />
<c:set var="CapitalAssetInformationAttributes"	value="${DataDictionary.CapitalAssetInformation.attributes}" />	

<c:choose>
	<c:when test="${itemLine.tradeInAllowance}">
		<c:set var="color" value="red" />
	</c:when>
	<c:otherwise>
		<c:choose>
		<c:when test="${itemLine.additionalChargeNonTradeInIndicator}">
			<c:set var="color" value="blue" />
		</c:when>
		<c:otherwise>
			<c:set var="color" value="black" />
		</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<c:set var="assetItemStr" value="purApDocs[${docPos-1}].purchasingAccountsPayableItemAssets[${linePos-1}]" />
<html:hidden property="${assetItemStr}.versionNumber" />
<html:hidden property="${assetItemStr}.active" />
<html:hidden property="${assetItemStr}.documentNumber" />
<html:hidden property="${assetItemStr}.accountsPayableLineItemIdentifier" />
<html:hidden property="${assetItemStr}.capitalAssetBuilderLineNumber" />
<html:hidden property="${assetItemStr}.itemAssignedToTradeInIndicator" />
<html:hidden property="${assetItemStr}.tradeInAllowance" />
<html:hidden property="${assetItemStr}.additionalChargeNonTradeInIndicator" />
<html:hidden property="${assetItemStr}.capitalAssetManagementDocumentNumber" />
<html:hidden property="${assetItemStr}.createAssetIndicator" />
<html:hidden property="${assetItemStr}.applyPaymentIndicator" />
<html:hidden property="${assetItemStr}.purchaseOrderItemIdentifier" />
<html:hidden property="${assetItemStr}.capitalAssetSystemIdentifier" />
<tr style="color:${color}">
	<c:choose>
	<c:when test="${itemLine.active && !itemLine.additionalChargeNonTradeInIndicator && !itemLine.tradeInAllowance}">
		<td rowspan="2"><html:checkbox styleId="systemCheckbox" property="${assetItemStr}.selectedValue" /></td>
	</c:when>
	<c:otherwise>
		<td rowspan="2">&nbsp;</td>
	</c:otherwise>
	</c:choose>
	<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].purapDocumentIdentifier" attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}" readOnly="true"/></td>
	<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentTypeCode" attributeEntry="${purApDocumentAttributes.documentTypeCode}" readOnly="true"/></td>
	<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentHeader.financialDocumentStatusCode" attributeEntry="${financialSystemDocumentHeaderAttributes.financialDocumentStatusCode}" readOnly="true"/></td>
	<c:choose>
		<c:when test="${itemLine.itemLineNumber != null}">
		<td class="infoline">
		<c:set var="preTagUrl" value="${itemLine.preTagInquiryUrl}" />
		<c:if test="${preTagUrl != ''}" >
		<a href="${ConfigProperties.application.url}/${preTagUrl }" target="_blank"> 
		</c:if>
			<kul:htmlControlAttribute property="${assetItemStr}.itemLineNumber" attributeEntry="${purApItemAssetAttributes.itemLineNumber}" readOnly="true"/>
		<c:if test="${preTagUrl != ''}" >
			&nbsp;
		</a>
		</c:if>
		</td>
		</c:when>
		<c:otherwise>
		<td class="infoline"><kul:htmlControlAttribute property="${assetItemStr}.itemTypeCode" attributeEntry="${purApItemAssetAttributes.itemTypeCode}" readOnly="true"/></td>
		</c:otherwise>
	</c:choose>
	<td class="infoline"><kul:htmlControlAttribute property="${assetItemStr}.accountsPayableItemQuantity" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}" readOnly="true"/></td>
	<td class="infoline">
		<c:if test="${itemLine.active }">
			<kul:htmlControlAttribute property="${assetItemStr}.splitQty" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/>
		</c:if>
		&nbsp;
	</td>
	<td class="infoline"><kul:htmlControlAttribute property="${assetItemStr}.unitCost" attributeEntry="${genericAttributes.genericAmount}" readOnly="true"/></td>
	<td class="infoline"><kul:htmlControlAttribute property="${assetItemStr}.firstFincialObjectCode" attributeEntry="${generalLedgerAttributes.financialObjectCode}" readOnly="true"/></td>
	<td class="infoline">
		<c:choose>
		<c:when test="${itemLine.active }">
			<kul:htmlControlAttribute property="${assetItemStr}.accountsPayableLineItemDescription" attributeEntry="${purApItemAssetAttributes.accountsPayableLineItemDescription}"/>
		</c:when>
		<c:otherwise>
			<kul:htmlControlAttribute property="${assetItemStr}.accountsPayableLineItemDescription" attributeEntry="${purApItemAssetAttributes.accountsPayableLineItemDescription}" readOnly="true"/>
		</c:otherwise>
		</c:choose>
	</td>
	<td class="infoline"><kul:htmlControlAttribute property="${assetItemStr}.capitalAssetTransactionTypeCode" attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}" readOnly="true"/>
		<br></br>
		<c:forEach items="${itemLine.purApItemAssets}" var="purApItemAsset">
		<c:set var="i" value="${i+1}" />
			${purApItemAsset.capitalAssetNumber}&nbsp;
			<html:hidden property="purApDocs[${docPos-1}].purchasingAccountsPayableItemAssets[${linePos-1}].purApItemAsset[${i-1}].capitalAssetNumber" />
		</c:forEach>
	</td>
	<c:choose>
	<c:when test="${itemLine.itemAssignedToTradeInIndicator}">
		<td class="infoline">Y
	</c:when>
	<c:otherwise>
		<td class="infoline">N
	</c:otherwise>
	</c:choose>
	<c:choose>
	<c:when test="${itemLine.active }">
		<td class="infoline" align="center">
		<c:if test="${!itemLine.additionalChargeNonTradeInIndicator && !itemLine.tradeInAllowance}">
			<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-split.gif" styleClass="tinybutton" property="methodToCall.split.doc${docPos-1}.line${linePos-1}" title="Split" alt="Split" />
			<br></br>
			<c:if test="${itemLine.accountsPayableItemQuantity < 1 }">
			<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-percentpayment.gif" styleClass="tinybutton" property="methodToCall.percentPayment.doc${docPos-1}.line${linePos-1}" title="Percent Payment" alt="Percent Payment" />
			<br></br>
			</c:if>
		</c:if>
		<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-allocate.gif" styleClass="tinybutton" property="methodToCall.allocate.doc${docPos-1}.line${linePos-1}" title="allocate" alt="allocate"/>
		<br></br>
		<c:if test="${itemLine.createAssetIndicator}">
		<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-createasset.gif" styleClass="tinybutton" property="methodToCall.createAsset.doc${docPos-1}.line${linePos-1}" title="createAsset" alt="createAsset"/>
		<br></br>
		</c:if>
		<c:if test="${itemLine.applyPaymentIndicator}">
		<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-applypayment.gif" styleClass="tinybutton" property="methodToCall.applyPayment.doc${docPos-1}.line${linePos-1}" title="applyPayment" alt="applyPayment"/>
		</c:if>
		</td>
	</c:when>
	<c:otherwise>
	    <td class="infoline" align="center">
			<a href="${ConfigProperties.application.url}/en/DocHandler.do?command=displayDocSearchView&docId=${itemLine.capitalAssetManagementDocumentNumber}"  target="_blank">
				<kul:htmlControlAttribute property="${assetItemStr}.capitalAssetManagementDocumentNumber" attributeEntry="${purApItemAssetAttributes.capitalAssetManagementDocumentNumber}" readOnly="true">
				</kul:htmlControlAttribute>
			</a>&nbsp;
		</td>
		<td class="infoline" align="center">
			<c:forEach items="${itemLine.approvedAssetNumbers }" var="assetNumber" >
				<kul:inquiry boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" keyValues="capitalAssetNumber=${assetNumber}" render="true">
					${assetNumber }
				</kul:inquiry>
				&nbsp;
			</c:forEach>
			&nbsp;
		</td>
	</c:otherwise>
	</c:choose>
</tr>
<tr>
	<c:set var="tabKey" value="payment-${docPos}-${linePos}"/>
	<html:hidden property="tabStates(${tabKey})" value="CLOSE" />
	<td colspan="13" style="padding:0px; border-style:none;">
	<table class="datatable" cellpadding="0" cellspacing="0" align="center"
       style="width: 100%; text-align: left;">
		<tr>
			<td colspan="14" class="tab-subhead" style="border-right: medium none;">
			<html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif"
	                                    property="methodToCall.toggleTab.tab${tabKey}"
	                                    title="toggle"
	                                    alt="show"
	                                    styleClass="tinybutton"
	                                    styleId="tab-${tabKey}-imageToggle"
	                                    onclick="javascript: return toggleTab(document, '${tabKey}'); "/>
	            View Payments
			</td>
		</tr>
		<tbody  style="display: none;" id="tab-${tabKey}-div">
		<tr>
			<kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.accountNumber}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.subAccountNumber}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.financialObjectCode}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.financialSubObjectCode}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.projectCode}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.referenceFinancialDocumentNumber}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.documentNumber}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.financialDocumentTypeCode}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.transactionPostingDate}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.universityFiscalYear}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.universityFiscalPeriodCode}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${purApLineAssetAccountsAttributes.itemAccountTotalAmount}" hideRequiredAsterisk="true"/>
		</tr>
		<c:forEach items="${itemLine.purchasingAccountsPayableLineAssetAccounts}" var="payment" >
		<tr>
			<c:set var="acctId" value="${acctId+1}"/>
			<c:set var="pmtStr" value="purApDocs[${docPos-1}].purchasingAccountsPayableItemAssets[${linePos-1}].purchasingAccountsPayableLineAssetAccounts[${acctId-1}]" />
			<html:hidden property="${pmtStr}.versionNumber" />
			<html:hidden property="${pmtStr}.active" />
			<html:hidden property="${pmtStr}.documentNumber" />
			<html:hidden property="${pmtStr}.accountsPayableLineItemIdentifier" />
			<html:hidden property="${pmtStr}.capitalAssetBuilderLineNumber" />
			<html:hidden property="${pmtStr}.generalLedgerAccountIdentifier" />
			<td class="infoline">&nbsp;</td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.chartOfAccountsCode" attributeEntry="${generalLedgerAttributes.chartOfAccountsCode}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.accountNumber" attributeEntry="${generalLedgerAttributes.accountNumber}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.subAccountNumber" attributeEntry="${generalLedgerAttributes.subAccountNumber}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.financialObjectCode" attributeEntry="${generalLedgerAttributes.financialObjectCode}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.financialSubObjectCode" attributeEntry="${generalLedgerAttributes.financialSubObjectCode}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.projectCode" attributeEntry="${generalLedgerAttributes.projectCode}" readOnly="true"/></td>
			<td class="infoline">
			<c:set var="poUrl" value="${payment.purchaseOrderInquiryUrl}" />
			<c:if test="${poUrl != ''}" >
				<a href="${ConfigProperties.application.url}/${poUrl}" target="_blank"> 
			</c:if>
				<kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.referenceFinancialDocumentNumber" attributeEntry="${generalLedgerAttributes.referenceFinancialDocumentNumber}" readOnly="true"/>
			<c:if test="${poUrl != ''}" >
				&nbsp;
				</a>
			</c:if>
			</td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.documentNumber" attributeEntry="${generalLedgerAttributes.documentNumber}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.financialDocumentTypeCode" attributeEntry="${generalLedgerAttributes.financialDocumentTypeCode}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.transactionPostingDate" attributeEntry="${generalLedgerAttributes.transactionPostingDate}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.universityFiscalYear" attributeEntry="${generalLedgerAttributes.universityFiscalYear}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.generalLedgerEntry.universityFiscalPeriodCode" attributeEntry="${generalLedgerAttributes.universityFiscalPeriodCode}" readOnly="true"/></td>
			<td class="infoline"><kul:htmlControlAttribute property="${pmtStr}.itemAccountTotalAmount" attributeEntry="${purApLineAssetAccountsAttributes.itemAccountTotalAmount}" readOnly="true"/></td>
		</tr>
		</c:forEach>
		<th colspan="13" style="text-align: right;">Total:</th>
		<th><kul:htmlControlAttribute property="${assetItemStr}.totalCost" attributeEntry="${purApLineAssetAccountsAttributes.itemAccountTotalAmount}" readOnly="true"/></th>
		</tbody>
	</table>
	</td>
</tr>