<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
				<kul:lookup boClassName="org.kuali.rice.location.framework.campus.CampusEbo" fieldConversions="code:${propertyName}.campusCode" />
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
