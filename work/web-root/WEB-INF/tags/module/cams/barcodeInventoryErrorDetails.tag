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
<c:set var="bcieDocumentAttributes" value="${DataDictionary.BarcodeInventoryErrorDocument.attributes}" />
<c:set var="bcieDetailAttributes" value="${DataDictionary.BarcodeInventoryErrorDetail.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />


<kul:tab tabTitle="Barcode Inventory Error(s)" defaultOpen="true" tabErrorKey="commonErrorSection">
	<div id="barcodeInventoryDetails" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Barcode Inventory Error(s)">
			<tr>
				<td colspan="11" class="subhead">Barcode Inventory Error(s)</td>				
			</tr>
			<tr>
			    <!-- Columns Header -->
				<!-- kul:htmlAttributeHeaderCell literalLabel="Line#" /-->
            	<c:if test="${!readOnly}">				
					<td align="right" class="${cssClass}" width="1%"><html:checkbox property="selectAllCheckbox" onclick="selectAllCheckboxes(document.forms[0])" /></td>
				</c:if>

				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadRowNumber}" width="1%" />
				
				<c:if test="${readOnly}">
					<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.errorCorrectionStatusCode}" width="5%" />				
				</c:if>
																		
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.assetTagNumber}"  width="6%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadScanIndicator}" width="3%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.uploadScanTimestamp}" width="10%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.campusCode}" width="6%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingCode}" width="9%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingRoomNumber}" width="8%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.buildingSubRoomNumber}" width="5%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.assetConditionCode}" width="5%"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${bcieDetailAttributes.errorDescription}" width="20%"/>
			</tr>
			
			<c:set var="isDocumentCorrected" value="${KualiForm.document.documentCorrected}"/>
			
			<c:set var="lineNumber" value="${0}"/>
			<logic:iterate id="detail" name="KualiForm" property="document.barcodeInventoryErrorDetail" indexId="ctr">
				<c:set var="status" value="${detail.errorCorrectionStatusCode}"/>
				
				<c:if test="${isDocumentCorrected}">
					<c:set var="lineNumber" value="${lineNumber + 1}"/>
				</c:if>
				<c:if test="${not isDocumentCorrected and status == CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR}">
					<c:set var="lineNumber" value="${lineNumber + 1}"/>
				</c:if>
				
				<cams:barcodeInventoryErrorDetail
						barcodeInventoryDetailAttributes="${bcieDetailAttributes}"					
						propertyName="document.barcodeInventoryErrorDetail[${ctr}]"
						readOnly="${readOnly}" 
						cssClass="datacell"
						lineNumber="${lineNumber}" 
						rowNumber="${detail.uploadRowNumber}"
						status="${status}"
				/>
            	
<!--  We don't need to display the error message text on a page because they are being already displayed in a textbox.-->
				<c:set var="keyMatch" value="document.barcodeInventoryErrorDetail[${ctr}]*"/>            	            	
				<c:forEach items="${ErrorPropertyList}" var="key">
				  <c:if test="${not KualiForm.displayedErrors[key]}">            	            	
		              <c:forEach items="${fn:split(keyMatch,',')}" var="prefix">
			                <c:if test="${(fn:endsWith(prefix,'*') && fn:startsWith(key,fn:replace(prefix,'*',''))) || (key == prefix)}">
			                 	<c:set target="${KualiForm.displayedErrors}" property="${key}" value="true"/>                 
			                </c:if>
		              </c:forEach>
				  </c:if>
				</c:forEach>
				
<!--  ********************************************************************************************************************************** -->
            	
			</logic:iterate>
		</table>

	<c:if test="${!readOnly}">						
			<table table cellpadding="0" cellspacing="0" border=0> 		
				<tr>
				<th width="50%" class="grid" align="right">
					<html:image property="methodToCall.validateLines" src="${ConfigProperties.externalizable.images.url}tinybutton-validate.gif"
					    		alt="Validates selected lines"
					        	styleClass="tinybutton" align="center"/>&nbsp &nbsp &nbsp
				</td>
				<th width="50%" class="grid" align="left">&nbsp &nbsp &nbsp				
					<html:image property="methodToCall.deleteLines" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
					    		alt="Delete selected lines"
					        	styleClass="tinybutton" align="center"/>
				</td>	
				</tr>			
			</table>
	</c:if>
			
	</div>
</kul:tab>

<c:if test="${!readOnly}">				
	<kul:tab tabTitle="Global Replace" defaultOpen="true" tabErrorKey="globalReplaceErrorSection">
		<div id="globalReplace" class="tab-container" align=center>
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td colspan="11" class="subhead">Global replace</td>
				</tr>
			</table>
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td  colspan="2"><div align="center"><label><strong>Search for lines containing...</strong></label></div></td>
					<td  colspan="2"><div align="center"><label><strong>...and replace them with:</strong></label></div></td>
				</tr>
				<tr>	
			        <th align=right valign=middle class="grid"><div align="right">
			        	<kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.currentTagNumber}" readOnly="true"/>
			        </th>			        
					<td align=left class="${cssClass}">&nbsp								
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.currentTagNumber}" property="document.currentTagNumber"/>
					</td>	
			        <th align=right valign=middle class="grid"><div align="right">&nbsp</div></th>				
					<td align=left class="${cssClass}">&nbsp</td>
	
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.currentScanCode}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.currentScanCode}" property="document.currentScanCode"/>
						[Y - N]
					</td>
			        <th align=right valign=middle class="grid"><div align="right">&nbsp</div></th>				
					<td align=left class="${cssClass}">&nbsp</td>	
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.currentCampusCode}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.currentCampusCode}" property="document.currentCampusCode"/>
						<kul:lookup boClassName="org.kuali.rice.location.framework.campus.CampusEbo" fieldConversions="code:document.currentCampusCode" />
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.newCampusCode}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.campusCode}" property="document.newCampusCode"/>
						<kul:lookup boClassName="org.kuali.rice.location.framework.campus.CampusEbo" fieldConversions="code:document.newCampusCode" />						
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.currentBuildingNumber}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.currentBuildingNumber}" property="document.currentBuildingNumber"/>
						<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building" 
						fieldConversions="buildingCode:document.currentBuildingNumber,campusCode:document.currentCampusCode" 
						lookupParameters="document.currentBuildingNumber:buildingCode,document.currentCampusCode:campusCode" />										
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.newBuildingNumber}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.newBuildingNumber}" property="document.newBuildingNumber"/>
						<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building" 
						fieldConversions="buildingCode:document.newBuildingNumber,campusCode:document.newCampusCode" 
						lookupParameters="document.newBuildingNumber:buildingCode,document.newCampusCode:campusCode" />																
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.currentRoom}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.currentRoom}" property="document.currentRoom"/>
						<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Room" 
						fieldConversions="buildingCode:document.currentBuildingNumber,campusCode:currentCampusCode,buildingRoomNumber:document.currentRoom" 
						lookupParameters="document.currentBuildingNumber:buildingCode,document.currentCampusCode:campusCode" />										
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.newRoom}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.newRoom}" property="document.newRoom"/>
						<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Room" 
						fieldConversions="buildingCode:document.newBuildingNumber,campusCode:document.newCampusCode,buildingRoomNumber:document.newRoom" 
						lookupParameters="document.newBuildingNumber:buildingCode,document.newCampusCode:campusCode" />																
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.currentSubroom}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.currentSubroom}" property="document.currentSubroom"/>
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.newSubroom}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.newSubroom}" property="document.newSubroom"/>
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.currentConditionCode}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.currentConditionCode}" property="document.currentConditionCode"/>
						<kul:lookup boClassName="org.kuali.kfs.module.cam.businessobject.AssetCondition" fieldConversions="assetConditionCode:document.currentConditionCode"
						lookupParameters="document.currentConditionCode:assetConditionCode" />									
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDocumentAttributes.newConditionCode}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDocumentAttributes.newConditionCode}" property="document.newConditionCode"/>
						<kul:lookup boClassName="org.kuali.kfs.module.cam.businessobject.AssetCondition" fieldConversions="assetConditionCode:document.newConditionCode"
						lookupParameters="document.newConditionCode:assetConditionCode" />															
					</td>
				</tr>
						
				<tr>
					<th colspan=4 width="100%"><div align="center">
						<html:image property="methodToCall.searchAndReplace"
				    		src="${ConfigProperties.externalizable.images.url}tinybutton-replace.gif"
				    		alt="Start replacing values"
				        	styleClass="tinybutton" align="center"/></div>
					</th>
				</tr>					
			</table>
		</div>
	</kul:tab>
</c:if>
