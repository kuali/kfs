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

<c:set var="purApDocumentAttributes" value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}" />
<c:set var="purApItemAssetAttributes" value="${DataDictionary.PurchasingAccountsPayableItemAsset.attributes}" />
<c:set var="financialSystemDocumentHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />

<kul:tab tabTitle="Line Items" defaultOpen="true">
<div class="tab-container" align="center">
<table width="100%" cellpadding="0" cellspacing="0" class="datatable">	
	<tr>
		<td class="tab-subhead"  width="100%" colspan="15">Line Items</td>
	</tr>	
	<tr>
		<th class="grid">&nbsp;</th>
	    <th class="grid" align="center">Invoice</th>
	    <th class="grid" align="center">Doc Type</th>
	    <th class="grid" align="center">Invoice Status</th>
	    <th class="grid" align="center">Line #</th>
	    <th class="grid" align="center">Target</th>
	    <th class="grid" align="center">Qty</th>
	    <th class="grid" align="center">Split Qty</th>
	    <th class="grid" align="center">Unit Cost</th>
	    <th class="grid" align="center">Object Code</th>
	    <th class="grid" align="center">Description</th>
	    <th class="grid" align="center">CAMS Transaction</th>
	    <th class="grid" align="center">TI</th>
	    <th class="grid" align="center">Alloc</th>
	    <th class="grid" align="center">Action</th>
	</tr>
    <c:forEach items="${KualiForm.purApDocList}" var="itemLine" >
    	<c:set var="linePos" value="${linePos+1}" />
    	<c:forEach items="${itemLine.purchasingAccountsPayableItemAssets}" var="line">
    	<c:set var="seqNbr" value="${seqNbr+1}" />
    	<c:set var="itemPos" value="${itemPos+1}" />
    	<html:hidden property="currentSeqNbr" name="currentSeqNbr" value="${seqNbr}"/>
    	<tr>
    		<td class="grid">${seqNbr}</td>
			<td class="grid"><kul:htmlControlAttribute property="purApDocList[${linePos-1}].purapDocumentIdentifier" attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}" readOnly="true"/></td>
			<td class="grid"><kul:htmlControlAttribute property="purApDocList[${linePos-1}].documentTypeCode" attributeEntry="${purApDocumentAttributes.documentTypeCode}" readOnly="true"/></td>
			<td class="grid"><kul:htmlControlAttribute property="purApDocList[${linePos-1}].documentHeader.financialDocumentStatusCode" attributeEntry="${financialSystemDocumentHeaderAttributes.financialDocumentStatusCode}" readOnly="true"/></td>
			<td class="grid"><kul:htmlControlAttribute property="purApDocList[${linePos-1}].purchasingAccountsPayableItemAssets[${itemPos -1}].itemLineNumber" attributeEntry="${purApItemAssetAttributes.itemLineNumber}" readOnly="true"/></td>
			<td class="grid"><kul:htmlControlAttribute property="purApDocList[${linePos-1}].purchasingAccountsPayableItemAssets[${itemPos -1}].target" attributeEntry="${purApItemAssetAttributes.target}"/></td>
			<td class="grid"><kul:htmlControlAttribute property="purApDocList[${linePos-1}].purchasingAccountsPayableItemAssets[${itemPos -1}].accountsPayableItemQuantity" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}" readOnly="true"/></td>
		</tr>
		</c:forEach>
		<c:set var="itemPos" value="0" />
	</c:forEach>
</table>
</div>
</kul:tab>
