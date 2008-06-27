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
<c:set var="bcieDetailAttributes" value="${DataDictionary.BarcodeInventoryErrorDetail.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['viewOnly']}" />
<!-- kul:tab tabTitle="Barcode Inventory Error(s)" defaultOpen="true" tabErrorKey="${CamsConstants.BarcodeInventoryError.DETAIL_ERRORS}"-->
<kul:tab tabTitle="Barcode Inventory Error(s)" defaultOpen="true" >
	<div id="barcodeInventoryDetails" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Barcode Inventory Error(s)">
			<tr>
				<td colspan="11" class="subhead">Barcode Inventory Error(s)</td>
			</tr>
			<tr>
			    <!-- Columns Header -->
				<!-- kul:htmlAttributeHeaderCell literalLabel="Line#" /-->
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadRowNumber}" width="1%" />
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.assetTagNumber}"  width="6%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadScanIndicator}" width="3%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadScanTimestamp}" width="10%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.campusCode}" width="6%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingCode}" width="9%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingRoomNumber}" width="8%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingSubRoomNumber}" width="5%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.assetConditionCode}" width="5%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.errorDescription}" width="20%"/>
				<kul:htmlAttributeHeaderCell literalLabel="Action" width="5%"/>
			</tr>
			<logic:iterate id="detail" name="KualiForm" property="document.barcodeInventoryErrorDetail" indexId="ctr">
				<c:set var="status" value="${detail.errorCorrectionStatusCode}"/>
            	<c:if test="${status == CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR}">
					<cams:barcodeInventoryErrorDetail
						barcodeInventoryDetailAttributes="${bcieDetailAttributes}"					
						propertyName="document.barcodeInventoryErrorDetail[${ctr}]"
						readOnly="${!readOnly}" 
						cssClass="datacell"
						lineNumber="${ctr}" />
            	</c:if>
			</logic:iterate>
		</table>
	</div>
</kul:tab>
