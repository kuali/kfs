<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
<%@ attribute name="chkcount" required="true" description="The total check number"%>
<%@ attribute name="docPos" required="true" description="The index of the CAB PurAp Document"%>
<%@ attribute name="linePos" required="true" description="The index of CAB PurAp item asset"%>
<%@ attribute name="itemLine" required="true" type="org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset" %>
<%@ attribute name="purApDocLine" required="true" type="org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument" %>
<script language="JavaScript" type="text/javascript" src="scripts/module/cab/selectCheckBox.js"></script>

<c:set var="purApDocumentAttributes" value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}" />
<c:set var="purApItemAssetAttributes" value="${DataDictionary.PurchasingAccountsPayableItemAsset.attributes}" />
<c:set var="purApLineAssetAccountsAttributes" value="${DataDictionary.PurchasingAccountsPayableLineAssetAccount.attributes}" />
<c:set var="generalLedgerAttributes" value="${DataDictionary.GeneralLedgerEntry.attributes}" />
<c:set var="financialSystemDocumentHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />
<c:set var="genericAttributes" value="${DataDictionary.GenericAttributes.attributes}" />
<c:set var="CapitalAssetInformationAttributes"	value="${DataDictionary.CapitalAssetInformation.attributes}" />	
<c:set var="dateFormatPattern" value="MM/dd/yyyy"/>

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
<c:set var="assetItemStr" value="purApDoc[${docPos-1}].purchasingAccountsPayableItemAsset[${linePos-1}]" />
<tr style="color:${color}">
	<c:choose>
	<c:when test="${itemLine.active && !itemLine.additionalChargeNonTradeInIndicator && !itemLine.tradeInAllowance}">
		<td rowspan="2"><html:checkbox styleId="systemCheckbox" property="${assetItemStr}.selectedValue" /></td>
	</c:when>
	<c:otherwise>
		<td rowspan="2">&nbsp;</td>
	</c:otherwise>
	</c:choose>
	
	<c:if test="${!itemLine.active}">	
	    <td class="infoline" align="center">
	    	<html:link target="_blank" href="cabPurApLine.do?methodToCall=viewDoc&documentNumber=${itemLine.capitalAssetManagementDocumentNumber}">
				${itemLine.capitalAssetManagementDocumentNumber }
			</html:link>
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
	</c:if>	
			
	<td class="infoline">
		${purApDocLine.purapDocumentIdentifier}
		<c:if test="${!empty itemLine.paymentRequestIdentifier}">
		-${itemLine.paymentRequestIdentifier}
		</c:if>
	</td>
	<td class="infoline">${purApDocLine.documentTypeCode}</td>
	<td class="infoline">${purApDocLine.statusDescription}</td>
	<c:choose>
		<c:when test="${!empty itemLine.itemLineNumber}">
		<td class="infoline">
		<c:set var="preTagUrl" value="${itemLine.preTagInquiryUrl}" />
		<c:choose>
		<c:when test="${!empty preTagUrl}" >
		<a href="${ConfigProperties.application.url}/${preTagUrl }" target="_blank">${itemLine.itemLineNumber}</a>
		</c:when>
		<c:otherwise>
			${itemLine.itemLineNumber}&nbsp;
		</c:otherwise>
		</c:choose>		
		</td>
		</c:when>
		<c:otherwise>
		<td class="infoline">${itemLine.itemTypeCode}</td>
		</c:otherwise>
	</c:choose>
	<td class="infoline">${itemLine.accountsPayableItemQuantity }</td>
	<td class="infoline">
		<c:if test="${itemLine.active }">
			<kul:htmlControlAttribute property="${assetItemStr}.splitQty" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/>
		</c:if>
		&nbsp;
	</td>
	<td class="infoline">${itemLine.unitCost}</td>
	<td class="infoline">${itemLine.firstFincialObjectCode }</td>
	<td class="infoline">
		<c:choose>
		<c:when test="${itemLine.active }">
			<kul:htmlControlAttribute property="${assetItemStr}.accountsPayableLineItemDescription" attributeEntry="${purApItemAssetAttributes.accountsPayableLineItemDescription}"/>
		</c:when>
		<c:otherwise>
			${itemLine.accountsPayableLineItemDescription }
		</c:otherwise>
		</c:choose>
	</td>
	
	<td class="infoline">
     		<kul:htmlControlAttribute property="${assetItemStr}.capitalAssetTransactionTypeCode" attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}" readOnly="true" readOnlyBody="true">
				<kul:inquiry boClassName="org.kuali.kfs.module.cab.businessobject.AssetTransactionType" keyValues="capitalAssetTransactionTypeCode=${itemLine.capitalAssetTransactionTypeCode}" render="true">
             			<html:hidden write="true" property="${assetItemStr}.capitalAssetTransactionTypeCode" />
           		</kul:inquiry>&nbsp;
       		</kul:htmlControlAttribute>
		<br></br>
		<c:forEach items="${itemLine.purApItemAssets}" var="purApItemAsset">
		<c:set var="i" value="${i+1}" />
			${purApItemAsset.capitalAssetNumber}&nbsp;
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
		<!-- c:if test="${!itemLine.additionalChargeNonTradeInIndicator && !itemLine.tradeInAllowance}" -->
		<c:if test="${!itemLine.tradeInAllowance}">
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
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.transactionDate}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.universityFiscalYear}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${generalLedgerAttributes.universityFiscalPeriodCode}" hideRequiredAsterisk="true"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${purApLineAssetAccountsAttributes.itemAccountTotalAmount}" hideRequiredAsterisk="true"/>
		</tr>
		<c:set var="acctId" value="0" />
		<c:forEach items="${itemLine.purchasingAccountsPayableLineAssetAccounts}" var="payment" >
		<tr>
			<c:set var="acctId" value="${acctId+1}"/>
			<td class="infoline">&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.chartOfAccountsCode}&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.accountNumber}&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.subAccountNumber}&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.financialObjectCode}&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.financialSubObjectCode}&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.projectCode}&nbsp;</td>
			<td class="infoline">
				<c:choose>
				<c:when test="${!empty KualiForm.purchaseOrderInquiryUrl }">
					<a href="${ConfigProperties.application.url}/${KualiForm.purchaseOrderInquiryUrl }" target="_blank">${KualiForm.purchaseOrderIdentifier}</a>							
				</c:when>
				<c:otherwise>
					${KualiForm.purchaseOrderIdentifier}&nbsp;
				</c:otherwise>
				</c:choose>
			</td>
			<td class="infoline">
				<html:link target="_blank" href="cabPurApLine.do?methodToCall=viewDoc&documentNumber=${payment.generalLedgerEntry.documentNumber}">
					${payment.generalLedgerEntry.documentNumber}
				</html:link>
			</td>
			<td class="infoline">${payment.generalLedgerEntry.financialDocumentTypeCode}&nbsp;</td>
			<td class="infoline" align="left"><fmt:formatDate value="${payment.generalLedgerEntry.transactionDate}" pattern="${dateFormatPattern}"/>&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.universityFiscalYear}&nbsp;</td>
			<td class="infoline">${payment.generalLedgerEntry.universityFiscalPeriodCode}&nbsp;</td>
			<td class="infoline">${payment.itemAccountTotalAmount}&nbsp;</td>
		</tr>
		</c:forEach>
		<th colspan="13" style="text-align: right;">Total:</th>
		<th>${itemLine.totalCost}</th>
		</tbody>
	</table>
	</td>
</tr>
