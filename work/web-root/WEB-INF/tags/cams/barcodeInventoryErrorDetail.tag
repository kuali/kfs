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

<%@ attribute name="barcodeInventoryDetailAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for cash control detail fields."%>
<%@ attribute name="readOnly" required="true" description="determines whether the detail lines will be displayed readonly"%>
<%@ attribute name="propertyName" required="true" description="name of form property containing the cash control document"%>
<%@ attribute name="cssClass" required="true"%>
<%@ attribute name="lineNumber" required="true"%>
<%@ attribute name="rowNumber" required="true"%>
<%@ attribute name="status" required="true"%>

<html:hidden property="${propertyName}.errorCorrectionStatusCode"/>
<html:hidden property="${propertyName}.documentNumber" />
<html:hidden property="${propertyName}.versionNumber" />
<html:hidden property="${propertyName}.objectId" />
<html:hidden property="${propertyName}.uploadRowNumber"/>
<html:hidden property="${propertyName}.inventoryCorrectionTimestamp"/>
<html:hidden property="${propertyName}.correctorUniversalIdentifier"/>

<c:if test="${(status != CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR)}">
	<html:hidden property="${propertyName}.assetTagNumber"/>
	<html:hidden property="${propertyName}.uploadScanIndicator"/>
	<html:hidden property="${propertyName}.uploadScanTimestamp"/>
	<html:hidden property="${propertyName}.campusCode"/>
	<html:hidden property="${propertyName}.buildingCode"/>
	<html:hidden property="${propertyName}.buildingRoomNumber"/>
	<html:hidden property="${propertyName}.assetConditionCode"/>
</c:if>

<c:if test="${(status == CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR) || readOnly}">
	<tr>	
	   	<c:if test="${!readOnly}">				
			<td align="right" class="${cssClass}">
				<html:checkbox property="rowCheckbox" value="${rowNumber}"/>
			</td>
		</c:if>
				
		<td align="right" class="${cssClass}">
		    ${lineNumber}	
		</td>
	   	<c:if test="${readOnly}">&nbsp				
			<td align="right" class="${cssClass}">
                	<c:choose>
                        <c:when test="${status == CamsConstants.BarcodeInventoryError.STATUS_CODE_DELETED}">
                            Deleted
                        </c:when>
                        <c:when test="${status == CamsConstants.BarcodeInventoryError.STATUS_CODE_CORRECTED}">
                            Corrected
                        </c:when>
                        <c:when test="${status == CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR}">
                            Error
                        </c:when>                        
                    </c:choose>
	        </td>
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