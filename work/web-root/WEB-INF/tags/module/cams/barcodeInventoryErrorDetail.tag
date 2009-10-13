<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<%@ attribute name="barcodeInventoryDetailAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for cash control detail fields."%>
<%@ attribute name="readOnly" required="true" description="determines whether the detail lines will be displayed readonly"%>
<%@ attribute name="propertyName" required="true" description="name of form property containing the cash control document"%>
<%@ attribute name="cssClass" required="true"%>
<%@ attribute name="lineNumber" required="true"%>
<%@ attribute name="rowNumber" required="true"%>
<%@ attribute name="status" required="true"%>

<c:if test="${(status == CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR) || readOnly}">
	<tr>	
	   	<c:if test="${!readOnly}">				
			<td align="right" class="${cssClass}">
				<html:checkbox property="rowCheckbox" value="${rowNumber}"/>
			</td>
		</c:if>
				
		<td align="right" class="${cssClass}">
		    ${lineNumber}	
		</td>
		
	   	<c:if test="${readOnly}">				
			<td align="right" class="${cssClass}">${CamsConstants.BarCodeInventoryError.statusDescription[status]}&nbsp</td>			
		</c:if>

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
				readOnly="true"/>
		</td>
		
		<td align=left class="${cssClass}">&nbsp
			<kul:htmlControlAttribute
				attributeEntry="${barcodeInventoryDetailAttributes.campusCode}"
				property="${propertyName}.campusCode"
				readOnly="${readOnly}" />
			<c:if test="${not readOnly}">
				&nbsp;
				<kul:lookup boClassName="org.kuali.rice.kns.bo.Campus" fieldConversions="campusCode:${propertyName}.campusCode" />
			</c:if>
		</td>
		
		<td align=left class="${cssClass}">&nbsp
			<kul:htmlControlAttribute
				attributeEntry="${barcodeInventoryDetailAttributes.buildingCode}"
				property="${propertyName}.buildingCode"
				readOnly="${readOnly}" />
				<c:if test="${not readOnly}">
					&nbsp;
					<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building" 
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
					<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Room" 
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
				<kul:lookup boClassName="org.kuali.kfs.module.cam.businessobject.AssetCondition" fieldConversions="assetConditionCode:${propertyName}.assetConditionCode"
				lookupParameters="${propertyName}.assetConditionCode:assetConditionCode" />			
			</c:if>	
		</td>
		
		<td align=left class="${cssClass}">&nbsp
			<kul:htmlControlAttribute
				attributeEntry="${barcodeInventoryDetailAttributes.errorDescription}"
				property="${propertyName}.errorDescription"
				readOnly="${readOnly}" />
		</td>
	</tr>
</c:if>
