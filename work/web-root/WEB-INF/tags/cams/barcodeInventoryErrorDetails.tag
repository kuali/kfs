<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<%@ attribute name="bcieDetailAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for cash control detail fields."%>
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<kul:tab tabTitle="Barcode Inventory Error(s)" defaultOpen="true" tabErrorKey="${CamsConstants.BarcodeInventoryError.DETAIL_ERRORS}">
	<div id="barcodeInventoryDetails" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Barcode Inventory Error(s)">
			<tr>
				<td colspan="8" class="subhead">
					Barcode Inventory Error(s)
				</td>
			</tr>
			<tr>
			    <!-- Columns Header -->
				<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadRowNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.assetTagNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadScanIndicator}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadScanTimestamp}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.campusCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingRoomNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingSubRoomNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.assetConditionCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.errorCorrectionStatusCode}" />
			</tr>
			<logic:iterate id="detail" name="KualiForm" property="document.inventoryUploadErrorDetails" indexId="ctr">
				<cm:barcodeInventoryErrorDetail
					propertyName="document.inventoryUploadErrorDetail[${ctr}]"
					bcieDetailAttributes="${bcieDetailAttributes}"
					readOnly="${!readOnly}" 
					cssClass="datacell"/>
			</logic:iterate>
		</table>
	</div>
</kul:tab>
