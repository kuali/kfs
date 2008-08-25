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
<html:hidden property="additionalChargeIndicator" />
<kul:tab tabTitle="Line Items" defaultOpen="true" tabErrorKey="purApDocs*">
<div class="tab-container" align="center">
<table width="100%" cellpadding="0" cellspacing="0" class="datatable">	
	<tr>
		<td class="tab-subhead"  width="100%" colspan="17">Line Items</td>
	</tr>	
	<tr>
		<th class="grid" align="center"><input type="checkbox" id="all" name="all" onclick="selectSources(this);" >Select</th>
		<kul:htmlAttributeHeaderCell literalLabel="Seq #"/>
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}"/>
  		<th class="grid" align="center">Doc Type
  		<th class="grid" align="center">Invoice Status
  		<th class="grid" align="center">Line #
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/>
  		<th class="grid" align="center">Split Qty
  		<th class="grid" align="center">Unit Cost
  		<th class="grid" align="center">Object Code
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.accountsPayableLineItemDescription}"/>
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}"/>
	    <th class="grid" align="center">TI</th>
	    <th class="grid" align="center">Action</th>
	</tr>
    <c:forEach items="${KualiForm.purApDocs}" var="purApDoc" >
    	<c:set var="docPos" value="${docPos+1}" />
    	<c:set var="linePos" value="0" />
    	<html:hidden property="purApDocs[${docPos-1}].versionNumber" />
    	<html:hidden property="purApDocs[${docPos-1}].active" />
    	<c:forEach items="${purApDoc.purchasingAccountsPayableItemAssets}" var="assetLine" >
	    	<c:set var="seq" value="${seq+1}" />
    		<c:set var="linePos" value="${linePos+1}" />
    		<cab:purApLineDetail seq="${seq}" docPos="${docPos}" linePos="${linePos}" itemLine="${assetLine}">
    		</cab:purApLineDetail>
		</c:forEach>
	</c:forEach>
	<tr>
		<td class="grid" colspan="16">
		<div align="center">
			<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-merge.gif" styleClass="tinybutton" property="methodToCall.merge" title="merge" alt="merge" onclick="merge();"/>&nbsp;&nbsp;&nbsp;
		</div>
		</td>
	</tr>
</table>
</div>
</kul:tab>
