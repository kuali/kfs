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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="displayRequisitionFields" required="false"
              description="A boolean as to whether the document is a Requisition."%>
             
<c:set var="notOtherDeliveryBuilding" value="${KualiForm.notOtherDeliveryBuilding}" />


<kul:tab tabTitle="Delivery" defaultOpen="true" tabErrorKey="${PurapConstants.DELIVERY_TAB_ERRORS}">
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
                    <c:if test="${notOtherDeliveryBuilding}">
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
                    	property="document.deliveryToName" readOnly="${not fullEntryMode}"/>
                    <c:if test="${fullEntryMode}">
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
                    	onchange="submitForm()" 
                    	readOnly="${notOtherDeliveryBuilding}"/>                
                </td>           	
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToEmailAddress}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToEmailAddress}" 
                    	property="document.deliveryToEmailAddress" readOnly="${not fullEntryMode}"/>
                </td>
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingOther}"/></div>
				</th>
				<td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingOther}" 
                    	property="document.deliveryBuildingOther"  readOnly="${not fullEntryMode}"/>&nbsp;
                    <c:if test="${fullEntryMode}">
                    	<html:image property="methodToCall.refreshDeliveryBuilding" src="images/buttonsmall_refresh.gif" alt="refresh" styleClass="tinybutton"/>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToPhoneNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToPhoneNumber}" 
                    	property="document.deliveryToPhoneNumber" readOnly="${not fullEntryMode}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" 
                    	property="document.deliveryBuildingLine1Address"  readOnly="${not fullEntryMode}"/>
                </td>			
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDate}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryRequiredDate}" datePicker="true" 
                    	property="document.deliveryRequiredDate" readOnly="${not fullEntryMode}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine2Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine2Address}" 
                    	property="document.deliveryBuildingLine2Address" readOnly="${not fullEntryMode}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}" 
                    	property="document.deliveryRequiredDateReasonCode" readOnly="${not fullEntryMode}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" 
                    	property="document.deliveryBuildingRoomNumber" readOnly="${not fullEntryMode}"/>
                </td>			
                <th align=right valign=middle class="bord-l-b" rowspan="4">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/></div>
                </th>
                <td align=left valign=middle class="datacell"  rowspan="4">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}" 
                    	property="document.deliveryInstructionText" readOnly="${not fullEntryMode}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCityName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}" 
                    	property="document.deliveryCityName" readOnly="${not fullEntryMode}"/>
                </td>
            </tr>
            <tr>			
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryStateCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}" 
                    	property="document.deliveryStateCode" readOnly="${not fullEntryMode}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryPostalCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}" 
                    	property="document.deliveryPostalCode" readOnly="${not fullEntryMode}"/>
                </td>
			</tr>
        </table>
    </div>
</kul:tab>
