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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="deliveryReadOnly" required="false"
              description="Boolean to indicate if delivery tab fields are read only" %>              
<%@ attribute name="showDefaultBuildingOption" required="false"
              description="Boolean to indicate if user should be allowed to set their default building" %>              

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="notOtherDeliveryBuilding" value="${not KualiForm.document.deliveryBuildingOtherIndicator}" />
<c:set var="contentReadOnly" value="${(not empty KualiForm.editingMode['lockContentEntry'])}" />
<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="lockB2BEntry" value="${(not empty KualiForm.editingMode['lockB2BEntry'])}" />
<c:if test="${empty deliveryReadOnly}">
	<c:set var="deliveryReadOnly" value="false" />
</c:if>
<c:set var="displayReceivingAddress" value="${(not empty KualiForm.editingMode['displayReceivingAddress'])}" />
<c:set var="lockAddressToVendor" value="${(not empty KualiForm.editingMode['lockAddressToVendor'])}" />
<c:set var="tabindexOverrideBase" value="20" />

<kul:tab tabTitle="Delivery" defaultOpen="true" tabErrorKey="${PurapConstants.DELIVERY_TAB_ERRORS}">
    <div class="tab-container" align=center>
    
    	<!---- Final Delivery ---->
            <h3>Final Delivery</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Final Delivery Section">
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCampusCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                        attributeEntry="${documentAttributes.deliveryCampusCode}" 
                        property="document.deliveryCampusCode" readOnly="true" tabindexOverride="${tabindexOverrideBase + 0}"/>&nbsp;                
                    <c:if test="${(fullEntryMode or amendmentEntry) && !deliveryReadOnly}">
                        <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.CampusParameter"
                            lookupParameters="document.deliveryCampusCode:campusCode"
                            fieldConversions="campusCode:document.deliveryCampusCode"/>
                    </c:if>
                </td>               
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryToName}" property="document.deliveryToName" 
                    	readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
                    <c:if test="${fullEntryMode && !deliveryReadOnly}">
                        <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" 
                        	fieldConversions="name:document.deliveryToName,emailAddress:document.deliveryToEmailAddress,phoneNumber:document.deliveryToPhoneNumber"/>
                    </c:if>
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                        attributeEntry="${documentAttributes.deliveryBuildingName}" 
                        property="document.deliveryBuildingName" readOnly="true" tabindexOverride="${tabindexOverrideBase + 0}"/>&nbsp;
                        <c:if test="${hasErrors}">
              				<kul:fieldShowErrorIcon />
            			</c:if>
                    <c:if test="${(fullEntryMode or amendmentEntry) && !deliveryReadOnly}">
                        <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
                            lookupParameters="document.deliveryCampusCode:campusCode"
                            fieldConversions="buildingCode:document.deliveryBuildingCode,buildingName:document.deliveryBuildingName,campusCode:document.deliveryCampusCode,buildingStreetAddress:document.deliveryBuildingLine1Address,buildingAddressCityName:document.deliveryCityName,buildingAddressStateCode:document.deliveryStateCode,buildingAddressZipCode:document.deliveryPostalCode,buildingAddressCountryCode:document.deliveryCountryCode"/>&nbsp;&nbsp;
                        <html:image property="methodToCall.useOtherDeliveryBuilding" src="${ConfigProperties.externalizable.images.url}tinybutton-buildingnotfound.gif" alt="building not found" styleClass="tinybutton" />&nbsp;
                        <c:if test="${showDefaultBuildingOption && notOtherDeliveryBuilding && !contentReadOnly}" >
                            <html:image property="methodToCall.setAsDefaultBuilding" src="${ConfigProperties.externalizable.images.url}tinybutton-setbuilding.gif" alt="set as default building" styleClass="tinybutton" />
                        </c:if>
                    </c:if>
                </td>           
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToPhoneNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryToPhoneNumber}" property="document.deliveryToPhoneNumber" 
                    	readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
            </tr>
			<tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" property="document.deliveryBuildingLine1Address"  
                    	readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>           
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToEmailAddress}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryToEmailAddress}" property="document.deliveryToEmailAddress" 
                    	readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
			</tr>
			<tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine2Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryBuildingLine2Address}" property="document.deliveryBuildingLine2Address" 
                    	readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <c:if test="${!lockB2BEntry}">
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDate}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryRequiredDate}" datePicker="true" property="document.deliveryRequiredDate" 
	                    readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </c:if>
                <c:if test="${lockB2BEntry}">
                    <th align=right valign=middle class="bord-l-b" rowspan="7">&nbsp;</th>
                    <td align=left valign=middle class="datacell" rowspan="7">&nbsp;</td>
                </c:if>
			</tr>
			<tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" property="document.deliveryBuildingRoomNumber" 
                    	readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 0}"/>
			        <c:if test="${(fullEntryMode or amendmentEntry) && !deliveryReadOnly && notOtherDeliveryBuilding && (not empty KualiForm.document.deliveryBuildingCode)}">
			            <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Room" 
			                readOnlyFields="buildingCode,campusCode"
			                lookupParameters="'Y':active,document.deliveryBuildingCode:buildingCode,document.deliveryCampusCode:campusCode"
			                fieldConversions="buildingRoomNumber:document.deliveryBuildingRoomNumber"/>
			        </c:if>
                </td>           
                <c:if test="${!lockB2BEntry}">
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute 
	                    	attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}" property="document.deliveryRequiredDateReasonCode"
	                        extraReadOnlyProperty="document.deliveryRequiredDateReason.deliveryRequiredDateReasonDescription" 
	                        readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
                </c:if>
			</tr>
			<tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCityName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryCityName}" property="document.deliveryCityName" 
                    	readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <c:if test="${!lockB2BEntry}">
                    <th align=right valign=middle class="bord-l-b" rowspan="4">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/></div>
                    </th>
                    <td align=left valign=middle class="datacell"  rowspan="4">
                        <kul:htmlControlAttribute 
                        	attributeEntry="${documentAttributes.deliveryInstructionText}" property="document.deliveryInstructionText" 
                        	readOnly="${not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
                    </td>
                </c:if>
			</tr>
            <tr>			
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryStateCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryStateCode}" property="document.deliveryStateCode" 
                    	readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryPostalCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryPostalCode}" property="document.deliveryPostalCode" 
                    	readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCountryCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCountryCode}" 
                    	property="document.deliveryCountryCode" 
                    	extraReadOnlyProperty="document.deliveryCountryName"
                    	readOnly="${notOtherDeliveryBuilding or not (fullEntryMode or amendmentEntry) or deliveryReadOnly}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
			</tr>
        </table>

	   	<c:if test="${displayReceivingAddress}">    
	   	
    	<!---- Receiving Address ---->
            <h3>Receiving Address</h3>

		<table cellpadding="0" cellspacing="0" class="datatable" summary="Receiving Address Section">	 
			<tr>
                <th align=right valign=middle  class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.receivingName}" /></div>
                </th>
                <td align=left valign=middle class="datacell"> 
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.receivingName}" property="document.receivingName" readOnly="true" /><br>
                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.receivingLine1Address}" property="document.receivingLine1Address" readOnly="true" /><br>
                   	<c:if test="${! empty KualiForm.document.receivingLine2Address}">                   	
                   		<kul:htmlControlAttribute attributeEntry="${documentAttributes.receivingLine2Address}" property="document.receivingLine2Address" readOnly="true" /><br>
                   	</c:if>
  	           		<kul:htmlControlAttribute attributeEntry="${documentAttributes.receivingCityName}" property="document.receivingCityName" readOnly="true" />,&nbsp;
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.receivingStateCode}" property="document.receivingStateCode" readOnly="true" />&nbsp;
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.receivingPostalCode}" property="document.receivingPostalCode" readOnly="true" />
                   	<c:if test="${! empty KualiForm.document.receivingCountryCode}">                   	
	            		<br><kul:htmlControlAttribute attributeEntry="${documentAttributes.receivingCountryCode}" 
	            			property="document.receivingCountryCode" 
	            			extraReadOnlyProperty="document.receivingCountryName"
	            			readOnly="true" />
                   	</c:if>
            	</td>
                <td align=left valign=middle class="datacell">
                    <c:if test="${fullEntryMode || amendmentEntry}" > 
                    	<kul:lookup boClassName="org.kuali.kfs.module.purap.businessobject.ReceivingAddress"
                    		lookupParameters="'Y':active,document.chartOfAccountsCode:chartOfAccountsCode,document.organizationCode:organizationCode"
                    		fieldConversions="receivingName:document.receivingName,receivingCityName:document.receivingCityName,receivingLine1Address:document.receivingLine1Address,receivingLine2Address:document.receivingLine2Address,receivingCityName:document.receivingCityName,receivingStateCode:document.receivingStateCode,receivingPostalCode:document.receivingPostalCode,receivingCountryCode:document.receivingCountryCode,useReceivingIndicator:document.addressToVendorIndicator"/>
                    </c:if>            		
            	</td>
            </tr>
        </table>
        
    	<!---- Address To Vendor ---->
            <h3>Address To Vendor</h3>
        
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Address To Vendor Section">
			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right">
						<c:choose>
							<c:when test="${(fullEntryMode || amendmentEntry) && !lockAddressToVendor}" >
								<kul:htmlAttributeLabel attributeEntry="${documentAttributes.addressToVendorIndicator}" />
							</c:when>
							<c:otherwise>
								Use Receiving Address as Shipping Address Presented to Vendor?
							</c:otherwise>
						</c:choose>
					</div>
				</th>
				<td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.addressToVendorIndicator}" property="document.addressToVendorIndicator" 
                    	readOnly="${!(fullEntryMode || amendmentEntry) || lockAddressToVendor}" tabindexOverride="${tabindexOverrideBase + 5}"/><br>				
					<!--
					<c:choose>
						<c:when test="${KualiForm.document.addressToVendorIndicator == 'true'}">
							&nbsp;<input type=radio title="${documentAttributes.addressToVendorIndicator.label} - Receiving Address" name="document.addressToVendorIndicator" value="true" checked />&nbsp;Receiving Address&nbsp;
							&nbsp;<input type=radio title="${documentAttributes.addressToVendorIndicator.label} - Final Delivery Address" name="document.addressToVendorIndicator" value="false" />&nbsp;Final Delivery Address&nbsp;
						</c:when>
						<c:otherwise>
							&nbsp;<input type=radio title="${documentAttributes.addressToVendorIndicator.label} - Receiving Address" name="document.addressToVendorIndicator" value="false" />&nbsp;Receiving Address&nbsp;
							&nbsp;<input type=radio title="${documentAttributes.addressToVendorIndicator.label} - Final Delivery Address" name="document.addressToVendorIndicator" value="true" checked />&nbsp;Final Delivery Address&nbsp;
						</c:otherwise>
					</c:choose>
					-->
            	</td>
            </tr>
        </table>
					
		</c:if>            		

	</div>
</kul:tab>

