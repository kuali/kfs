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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="deliveryReadOnly" required="false"
              description="Boolean to indicate if delivery tab fields are read only" %>              
              
<c:set var="notOtherDeliveryBuilding" value="${not KualiForm.document.deliveryBuildingOther}" />
<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:if test="${empty deliveryReadOnly}">
	<c:set var="deliveryReadOnly" value="false" />
</c:if>

<kul:tab tabTitle="Delivery" defaultOpen="false" tabErrorKey="${PurapConstants.DELIVERY_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Delivery</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Delivery Section">
            <tr>
 				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryBuildingName}" 
                    	property="document.deliveryBuildingName"
                    	onchange="submitForm()"
                    	readOnly="true"/>&nbsp;
                     <!-- TODO: figure out how to add fullEntryMode to this (initial try wasn't working) -->
                    <c:if test="${(notOtherDeliveryBuilding && fullEntryMode) && not(deliveryReadOnly)}">
                    	<kul:lookup boClassName="org.kuali.kfs.bo.Building"
                    		lookupParameters="document.deliveryCampusCode:campusCode"
                    		fieldConversions="buildingName:document.deliveryBuildingName,campusCode:document.deliveryCampusCode,buildingStreetAddress:document.deliveryBuildingLine1Address,buildingAddressCityName:document.deliveryCityName,buildingAddressStateCode:document.deliveryStateCode,buildingAddressZipCode:document.deliveryPostalCode"/>
                    </c:if>
                </td>           
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToName}" 
                    	property="document.deliveryToName" readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                    <c:if test="${fullEntryMode && not(deliveryReadOnly)}">
                        <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" 
                        	fieldConversions="personName:document.deliveryToName,personEmailAddress:document.deliveryToEmailAddress,personLocalPhoneNumber:document.deliveryToPhoneNumber"/>
                    </c:if>
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCampusCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryCampusCode}" 
                    	property="document.deliveryCampusCode" 
                    	readOnly="${notOtherDeliveryBuilding || deliveryReadOnly}"/>                
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToPhoneNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToPhoneNumber}" 
                    	property="document.deliveryToPhoneNumber" readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>           	
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingOther}"/></div>
				</th>
				<td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingOther}" 
                    	property="document.deliveryBuildingOther"  readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>&nbsp;
                    <c:if test="${(fullEntryMode or amendmentEntry) && not(deliveryReadOnly)}">
                    	<html:image property="methodToCall.refreshDeliveryBuilding" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" alt="refresh" styleClass="tinybutton"/>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToEmailAddress}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToEmailAddress}" 
                    	property="document.deliveryToEmailAddress" readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" 
                    	property="document.deliveryBuildingLine1Address"  readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>			
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDate}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryRequiredDate}" datePicker="true" 
                    	property="document.deliveryRequiredDate" readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine2Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine2Address}" 
                    	property="document.deliveryBuildingLine2Address" readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}" 
                    	property="document.deliveryRequiredDateReasonCode"
                    	extraReadOnlyProperty="document.deliveryRequiredDateReason.deliveryRequiredDateReasonDescription" 
                    	readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" 
                    	property="document.deliveryBuildingRoomNumber" readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>			
                <th align=right valign=middle class="bord-l-b" rowspan="4">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/></div>
                </th>
                <td align=left valign=middle class="datacell"  rowspan="4">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}" 
                    	property="document.deliveryInstructionText" readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCityName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}" 
                    	property="document.deliveryCityName" readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
            </tr>
            <tr>			
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryStateCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}" 
                    	property="document.deliveryStateCode" readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryPostalCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}" 
                    	property="document.deliveryPostalCode" readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}"/>
                </td>
			</tr>
        </table>
    </div>
</kul:tab>
