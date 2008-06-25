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

<%@ attribute name="barcodeInventoryDetailAttributes" required="true" 	type="java.util.Map" description="The DataDictionary entry containing attributes for cash control detail fields."%>
<%@ attribute name="readOnly" required="true" description="determines whether the detail lines will be displayed readonly"%>
<%@ attribute name="propertyName" required="true" description="name of form property containing the cash control document"%>
<%@ attribute name="cssClass" required="true"%>

<tr>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.uploadRowNumber}"
			property="${propertyName}.uploadRowNumber"
			readOnly="true" />
	</td>

	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.assetTagNumber}"
			property="${propertyName}.assetTagNumber"
			readOnly="${readOnly}" />
	</td>
	
	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.uploadScanIndicator}"
			property="${propertyName}.uploadScanIndicator" 
			readOnly="true" />
	</td>
	
	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.uploadScanTimestamp}"
			property="${propertyName}.uploadScanTimestamp"
			readOnly="true" />
	</td>
	
	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.campusCode}"
			property="${propertyName}.campusCode"
			readOnly="${readOnly}" />
		<c:if test="${not readOnly}">
			&nbsp;
			<kul:lookup boClassName="org.kuali.core.bo.Campus" fieldConversions="campusCode:${propertyName}.campusCode" />
		</c:if>
	</td>
	
	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.buildingCode}"
			property="${propertyName}.buildingCode"
			readOnly="${readOnly}" />
			<c:if test="${not readOnly}">
				&nbsp;
				<kul:lookup boClassName="org.kuali.kfs.bo.Building" 
				fieldConversions="buildingCode:${propertyName}.buildingCode,campusCode:${propertyName}.campusCode" 
				lookupParameters="${propertyName}.buildingCode:buildingCode,${propertyName}.campusCode:campusCode" />				
			</c:if>
	</td>
	
	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.buildingRoomNumber}"
			property="${propertyName}.buildingRoomNumber"
			readOnly="${readOnly}" />
			<c:if test="${not readOnly}">
				&nbsp;
				<kul:lookup boClassName="org.kuali.kfs.bo.Room" 
				fieldConversions="buildingCode:${propertyName}.buildingCode,campusCode:${propertyName}.campusCode,buildingRoomNumber:${propertyName}.buildingRoomNumber" 
				lookupParameters="${propertyName}.buildingCode:buildingCode,${propertyName}.campusCode:campusCode" />				
			</c:if>			
	</td>
	
	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.buildingSubRoomNumber}"
			property="${propertyName}.buildingSubRoomNumber"
			readOnly="${readOnly}" />

	</td>
	
	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.assetConditionCode}"
			property="${propertyName}.assetConditionCode"
			readOnly="${readOnly}" />
		<c:if test="${not readOnly}">
			&nbsp;
			<kul:lookup boClassName="org.kuali.module.cams.bo.AssetCondition" fieldConversions="assetConditionCode:${propertyName}.assetConditionCode"
			lookupParameters="${propertyName}.assetConditionCode:assetConditionCode" />			
		</c:if>

	</td>

	<td align=left class="${cssClass}">&nbsp
		<kul:htmlControlAttribute
			attributeEntry="${barcodeInventoryDetailAttributes.errorDescription}"
			property="${propertyName}.errorDescription"
			readOnly="${readOnly}" />
	</td>
		
         <c:if test="${not readOnly}">
               <td class="datacell">
                  	<div align="center">
                   		<html:image property="methodToCall.deleteBarcodeInventoryErrorDetail.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete a barcode inventory error line" title="Delete line" styleClass="tinybutton"/>
                  	</div>
                </td>
         </c:if>
</tr>