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
<script language="JavaScript" type="text/javascript" src="scripts/cab/selectCheckBox.js"></script>

<c:set var="purApDocumentAttributes" value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}" />
<c:set var="purApItemAssetAttributes" value="${DataDictionary.PurchasingAccountsPayableItemAsset.attributes}" />
<c:set var="purApLineAssetAccountsAttributes" value="${DataDictionary.PurchasingAccountsPayableLineAssetAccount.attributes}" />
<c:set var="generalLedgerAttributes" value="${DataDictionary.GeneralLedgerEntry.attributes}" />
<c:set var="financialSystemDocumentHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />
<c:set var="genericAttributes" value="${DataDictionary.GenericAttributes.attributes}" />

<kul:tab tabTitle="Line Items" defaultOpen="true">
<div class="tab-container" align="center">
<table width="100%" cellpadding="0" cellspacing="0" class="datatable">	
	<tr>
		<td class="tab-subhead"  width="100%" colspan="17">Line Items</td>
	</tr>	
	<tr>
		<TH class="grid" align="center"><INPUT TYPE="checkbox" id="all" NAME="all" onclick="selectSources(this);" > Source</TH>
		<th class="grid" align="center">Target
		<kul:htmlAttributeHeaderCell literalLabel="Seq #"/>
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}"/>
  		<th class="grid" align="center">Doc Type
  		<th class="grid" align="center">Invoice Status
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.itemLineNumber}"/>
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/>
  		<th class="grid" align="center">Split Qty
  		<th class="grid" align="center">Unit Cost
  		<th class="grid" align="center">Object Code
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.capitalAssetDescription}"/>
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}"/>
	    <th class="grid" align="center">TI</th>
	    <th class="grid" align="center">Action</th>
	</tr>
    <c:forEach items="${KualiForm.purApDocs}" var="purApDoc" >
    	<c:set var="docPos" value="${docPos+1}" />
    	<c:set var="linePos" value="0" />
    	<c:forEach items="${purApDoc.assetLineItems}" var="assetLine" >
	    	<c:set var="seq" value="${seq+1}" />
    		<c:set var="linePos" value="${linePos+1}" />
    		<c:set var="color" value="black" />
    		<tr style="color:${color}">
	    		<td><INPUT TYPE="checkbox" id="src${seq-1}" NAME="src${seq-1}" onclick="toggle('src${seq-1}','trg${seq-1}');" ></td>
				<td><INPUT TYPE="checkbox" id="trg${seq-1}" NAME="trg${seq-1}"  onclick="toggle('trg${seq-1}','src${seq-1}');"></td>
	    		<td class="infoline"><c:out value="${seq}"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].purapDocumentIdentifier" attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentTypeCode" attributeEntry="${purApDocumentAttributes.documentTypeCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentHeader.financialDocumentStatusCode" attributeEntry="${financialSystemDocumentHeaderAttributes.financialDocumentStatusCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].assetLineItems[${linePos-1}].itemLineNumber" attributeEntry="${purApItemAssetAttributes.itemLineNumber}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].assetLineItems[${linePos-1}].accountsPayableItemQuantity" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].assetLineItems[${linePos-1}].accountsPayableItemQuantity" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].assetLineItems[${linePos-1}].unitCost" attributeEntry="${genericAttributes.genericAmount}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].assetLineItems[${linePos-1}].firstFincialObjectCode" attributeEntry="${generalLedgerAttributes.financialObjectCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].assetLineItems[${linePos-1}].capitalAssetDescription" attributeEntry="${purApItemAssetAttributes.capitalAssetDescription}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].assetLineItems[${linePos-1}].capitalAssetTransactionTypeCode" attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}" readOnly="true"/></td>
				<c:choose>
					<c:when test="${assetLine.itemAssignedToTradeInIndicator}">
					<td class="infoline">Y
					</c:when>
					<c:otherwise>
					<td class="infoline">N
					</c:otherwise>
				</c:choose>
				<td class="infoline" align="center"><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-split.gif" styleClass="tinybutton" property="methodToCall.splitItem.doc${docPos-1}.line${linePos-1}" title="Split" alt="Split" />
				<c:if test="${assetLine.accountsPayableItemQuantity < 1 }">
					<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-percentpayment.gif" styleClass="tinybutton" property="methodToCall.percentPayment.doc${docPos-1}.line${linePos-1}" title="Percent Payment" alt="Percent Payment" />
				</c:if>
				</td>
			</tr>
		</c:forEach>
		<c:set var="linePos" value="0" />
    	<c:forEach items="${purApDoc.additionalChargeLineItems}" var="assetLine" varStatus="size">
	    	<c:set var="seq" value="${seq+1}" />
    		<c:set var="linePos" value="${linePos+1}" />
    		<c:set var="color" value="blue" />
    		<tr style="color:${color}">
    			<c:if test="${linePos-1 == 0}" >
	    			<td rowspan="${purApDoc.additionalChargeLineItemsSize}"><INPUT TYPE="checkbox" id="addl${seq-1}" NAME="addl${seq-1}"></td>
	    		</c:if>
				<td>&nbsp;</td>
	    		<td class="infoline"><c:out value="${seq}"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].purapDocumentIdentifier" attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentTypeCode" attributeEntry="${purApDocumentAttributes.documentTypeCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentHeader.financialDocumentStatusCode" attributeEntry="${financialSystemDocumentHeaderAttributes.financialDocumentStatusCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].additionalChargeLineItems[${linePos-1}].itemLineNumber" attributeEntry="${purApItemAssetAttributes.itemLineNumber}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].additionalChargeLineItems[${linePos-1}].accountsPayableItemQuantity" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}" readOnly="true"/></td>
				<td class="infoline">&nbsp;</td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].additionalChargeLineItems[${linePos-1}].unitCost" attributeEntry="${genericAttributes.genericAmount}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].additionalChargeLineItems[${linePos-1}].firstFincialObjectCode" attributeEntry="${generalLedgerAttributes.financialObjectCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].additionalChargeLineItems[${linePos-1}].capitalAssetDescription" attributeEntry="${purApItemAssetAttributes.capitalAssetDescription}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].additionalChargeLineItems[${linePos-1}].capitalAssetTransactionTypeCode" attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}" readOnly="true"/></td>
				<c:choose>
					<c:when test="${assetLine.itemAssignedToTradeInIndicator}">
					<td class="infoline">Y
					</c:when>
					<c:otherwise>
					<td class="infoline">N
					</c:otherwise>
				</c:choose>
				<td class="infoline" align="center"><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-split.gif" styleClass="tinybutton" property="methodToCall.splitItem.doc${docPos-1}.line${linePos-1}" title="Split" alt="Split" />
				<c:if test="${assetLine.accountsPayableItemQuantity < 1 }">
					<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-percentpayment.gif" styleClass="tinybutton" property="methodToCall.percentPayment.doc${docPos-1}.line${linePos-1}" title="Percent Payment" alt="Percent Payment" />
				</c:if>
				</td>
			</tr>
		</c:forEach>
		<c:set var="linePos" value="0" />
    	<c:forEach items="${purApDoc.tradeInLineItems}" var="assetLine" varStatus="size">
	    	<c:set var="seq" value="${seq+1}" />
    		<c:set var="linePos" value="${linePos+1}" />
    		<c:set var="color" value="blue" />
    		<tr style="color:${color}">
	    		<td><INPUT TYPE="checkbox" id="addl${seq-1}" NAME="addl${seq-1}"></td>
				<td>&nbsp</td>
	    		<td class="infoline"><c:out value="${seq}"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].purapDocumentIdentifier" attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentTypeCode" attributeEntry="${purApDocumentAttributes.documentTypeCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].documentHeader.financialDocumentStatusCode" attributeEntry="${financialSystemDocumentHeaderAttributes.financialDocumentStatusCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].tradeInLineItems[${linePos-1}].itemLineNumber" attributeEntry="${purApItemAssetAttributes.itemLineNumber}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].tradeInLineItems[${linePos-1}].accountsPayableItemQuantity" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}" readOnly="true"/></td>
				<td class="infoline">&nbsp;</td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].tradeInLineItems[${linePos-1}].unitCost" attributeEntry="${genericAttributes.genericAmount}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].tradeInLineItems[${linePos-1}].firstFincialObjectCode" attributeEntry="${generalLedgerAttributes.financialObjectCode}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].tradeInLineItems[${linePos-1}].capitalAssetDescription" attributeEntry="${purApItemAssetAttributes.capitalAssetDescription}" readOnly="true"/></td>
				<td class="infoline"><kul:htmlControlAttribute property="purApDocs[${docPos-1}].tradeInLineItems[${linePos-1}].capitalAssetTransactionTypeCode" attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}" readOnly="true"/></td>
				<c:choose>
					<c:when test="${assetLine.itemAssignedToTradeInIndicator}">
					<td class="infoline">Y
					</c:when>
					<c:otherwise>
					<td class="infoline">N
					</c:otherwise>
				</c:choose>
				<td class="infoline" align="center"><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-split.gif" styleClass="tinybutton" property="methodToCall.splitItem.doc${docPos-1}.line${linePos-1}" title="Split" alt="Split" />
				<c:if test="${assetLine.accountsPayableItemQuantity < 1 }">
					<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-percentpayment.gif" styleClass="tinybutton" property="methodToCall.percentPayment.doc${docPos-1}.line${linePos-1}" title="Percent Payment" alt="Percent Payment" />
				</c:if>
				</td>
			</tr>
		</c:forEach>
	</c:forEach>
	<tr>
		<td class="grid" colspan="16">
		<div align="center">
			<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-merge.gif" styleClass="tinybutton" property="methodToCall.merge" title="merge" alt="merge" onclick="merge();"/>&nbsp;&nbsp;&nbsp;
			<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-allocate.gif" styleClass="tinybutton" property="methodToCall.allocate" title="allocate" alt="allocate" onclick="allocate();"/>
		</div>
		</td>
	</tr>
</table>
</div>
</kul:tab>
