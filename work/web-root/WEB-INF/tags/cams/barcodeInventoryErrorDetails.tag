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
<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />

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
				<!-- kul:htmlAttributeHeaderCell literalLabel="Action" width="5%"/-->
			</tr>
			
			<c:out value="${readOnly}"/>
			
			<c:set var="lineNumber" value="${0}"/>
			<logic:iterate id="detail" name="KualiForm" property="document.barcodeInventoryErrorDetail" indexId="ctr">
				<c:set var="status" value="${detail.errorCorrectionStatusCode}"/>			
            	<c:if test="${(status == CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR) || readOnly}">
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

				<!-- c:out value="${key}"/-->
				
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
	<kul:tab tabTitle="Global Replace" defaultOpen="true">
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
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.assetTagNumber}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.assetTagNumber}" property="currentTagNumber"/>
					</td>
	
			        <!-- th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.assetTagNumber}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.assetTagNumber}" property="newTagNumber"/>
					</td-->
			        <th align=right valign=middle class="grid"><div align="right">&nbsp</div></th>				
					<td align=left class="${cssClass}">&nbsp</td>
	
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.uploadScanIndicator}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.uploadScanIndicator}" property="currentScanCode"/>
					</td>
			        <th align=right valign=middle class="grid"><div align="right">&nbsp</div></th>				
					<td align=left class="${cssClass}">&nbsp</td>
	
			        <!-- th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.uploadScanIndicator}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.uploadScanIndicator}" property="newScanCode"/>
					</td-->
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.campusCode}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.campusCode}" property="currentCampusCode"/>
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.campusCode}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.campusCode}" property="newCampusCode"/>
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.buildingCode}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.buildingCode}" property="currentBuildingNumber"/>
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.buildingCode}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.buildingCode}" property="newBuildingNumber"/>
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.buildingRoomNumber}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.buildingRoomNumber}" property="currentRoom"/>
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.buildingRoomNumber}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.buildingRoomNumber}" property="newRoom"/>
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.buildingSubRoomNumber}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.buildingSubRoomNumber}" property="currentSubroom"/>
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.buildingSubRoomNumber}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.buildingSubRoomNumber}" property="newSubroom"/>
					</td>
				</tr>				
	
				<tr>
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.assetConditionCode}" readOnly="true"/></div></th>
					<td align=left class="${cssClass}">&nbsp				
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.assetConditionCode}" property="currentConditionCode"/>
					</td>
	
			        <th align=right valign=middle class="grid"><div align="right"><kul:htmlAttributeLabel attributeEntry="${bcieDetailAttributes.assetConditionCode}" readOnly="true"/></div></th>				
					<td align=left class="${cssClass}">&nbsp
						<kul:htmlControlAttribute attributeEntry="${bcieDetailAttributes.assetConditionCode}" property="newConditionCode"/>
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