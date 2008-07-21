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
              
<kul:tab tabTitle="Delivery" defaultOpen="true" tabErrorKey="${PurapConstants.BULK_RECEIVING_DELIVERY_TAB_ERRORS}">
    <div class="tab-container" align=center>
    
        <h3>Delivery Information</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Delivery Section">
        <%-- If PO available, display the delivery information from the PO --%>
        	<c:if test="${isPOAvailable}">
	        	<html:hidden property="document.preparerPersonName" />
	        	<html:hidden property="document.deliveryCampusCode" />
	        	<html:hidden property="document.institutionContactName" />
	        	<html:hidden property="document.institutionContactPhoneNumber" />
	        	<html:hidden property="document.institutionContactEmailAddress" />
	        	<html:hidden property="document.deliveryToName" />
	        	<html:hidden property="document.requestorPersonEmailAddress" />
	        	
	        	<tr>
	        		<th align=right valign=middle  class="bord-l-b">
	                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell"> 
	                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingName}" property="document.deliveryBuildingName" readOnly="true" /><br>
	                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" property="document.deliveryBuildingLine1Address" readOnly="true" />&nbsp;
	                   	<c:if test="${! empty KualiForm.document.deliveryBuildingLine2Address}">                   	
	                   		<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine2Address}" property="document.deliveryBuildingLine2Address" readOnly="true" />,&nbsp;
	                   	</c:if>
		            	<c:out value="Room "/><kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" property="document.deliveryBuildingRoomNumber" readOnly="true" /><br>
	                   	<c:if test="${! empty KualiForm.document.deliveryCityName}">                   	
	    	           		<kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}" property="document.deliveryCityName" readOnly="true" />,&nbsp;
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.deliveryStateCode}">                   	
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}" property="document.deliveryStateCode" readOnly="true" />&nbsp;
	                  	</c:if>
	                   	<c:if test="${! empty KualiForm.document.deliveryPostalCode}">                   	
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}" property="document.deliveryPostalCode" readOnly="true" />
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.deliveryCountryCode}">                   	
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCountryCode}" property="document.deliveryCountryCode" readOnly="true" />
	                   	</c:if>
	            	</td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}" 
	                    	property="document.deliveryInstructionText" readOnly="${true}"/>
	                </td>
	            </tr>
	            <tr>
	            	<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.preparerPersonName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.preparerPersonName}" 
	                    	property="document.preparerPersonName" readOnly="${true}"/>
	                </td>
	                <th align=right valign=middle class="bord-l-b" rowspan="3">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}"/></div>
	                </th>
	                <td align=right valign=middle class="bord-l-b" rowspan="3">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}" 
	                    	property="document.deliveryAdditionalInstructionText" readOnly="${not (fullEntryMode)}"/>
	                </td>
	            </tr>
				<tr>
		            <th align=left valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonName}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonName}" 
	                    	property="document.requestorPersonName" readOnly="${true}"/>
	                </td>
	            </tr>
        		<tr>
        			<th align=left valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonPhoneNumber}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonPhoneNumber}" 
	                    	property="document.requestorPersonPhoneNumber" readOnly="${true}"/>
	                </td>
	            </tr>
	            <tr>
	            	<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCampusName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCampusName}" property="document.deliveryCampus.campusName" readOnly="true" />
               	 	</td>
               	 	<th align=right valign=middle class="bord-l-b">
	                </th>
	                <td align=left valign=middle class="datacell">
               	 	</td>
	            </tr>
        	</c:if>
        	
        	
        	<%-- If PO not available --%>
        	
        	<c:if test="${!isPOAvailable}">
        	
	            <tr>
	 				<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute 
	                    	attributeEntry="${documentAttributes.deliveryBuildingName}" 
	                    	property="document.deliveryBuildingName"
	                    	readOnly="true"/>&nbsp;
	                    <c:if test="${fullEntryMode}">
	                    	<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
	                    		lookupParameters="document.deliveryCampus:campusCode"
	                    		fieldConversions="buildingName:document.deliveryBuildingName,campusCode:document.deliveryCampusCode,buildingStreetAddress:document.deliveryBuildingLine1Address,buildingAddressCityName:document.deliveryCityName,buildingAddressStateCode:document.deliveryStateCode,buildingAddressZipCode:document.deliveryPostalCode"/>
	                    </c:if>
	                </td>           
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToName}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToName}" 
	                    	property="document.deliveryToName" readOnly="${not (fullEntryMode)}"/>
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
	                    	readOnly="${true}"/>                
	                </td>           	
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToPhoneNumber}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToPhoneNumber}" 
	                    	property="document.deliveryToPhoneNumber" readOnly="${not (fullEntryMode)}"/>
	                </td>
	            </tr>
				
				<tr>
					<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" 
	                    	property="document.deliveryBuildingLine1Address"  readOnly="true"/>
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToEmailAddress}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToEmailAddress}" 
	                    	property="document.deliveryToEmailAddress" readOnly="${not (fullEntryMode)}"/>
	                </td>
				</tr>
				
				<tr>
					<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine2Address}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine2Address}" 
	                    	property="document.deliveryBuildingLine2Address" readOnly="${not (fullEntryMode)}"/>
	                </td>			
	                <th align=right valign=middle class="bord-l-b" rowspan="3">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell"  rowspan="3">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}" 
	                    	property="document.deliveryInstructionText" readOnly="${not (fullEntryMode)}"/>
	                </td>
				</tr>
				
				<tr>
					<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" 
	                    	property="document.deliveryBuildingRoomNumber" readOnly="${not (fullEntryMode)}"/>
	                </td>	
				</tr>
				
				<tr>
					<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCityName}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}" 
	                    	property="document.deliveryCityName" readOnly="true"/>
	                </td>
	            </tr>
	            <tr>			
					<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryStateCode}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}" 
	                    	property="document.deliveryStateCode" readOnly="true"/>
	                </td>
	                <th align=right valign=middle class="bord-l-b" rowspan="3">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell"  rowspan="3">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryAdditionalInstructionText}" 
	                    	property="document.deliveryAdditionalInstructionText" readOnly="${not (fullEntryMode)}"/>
	                </td>	
				</tr>
				<tr>
					<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryPostalCode}"/></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}" 
	                    	property="document.deliveryPostalCode" readOnly="true"/>
	                </td>
				</tr>
				<tr>
					<th align=right valign=middle class="bord-l-b">
                    	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactName}" /></div>
                 	</th>
                	<td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactName}" property="document.institutionContactName" readOnly="${not (fullEntryMode)}" />
	                    <c:if test="${(fullEntryMode)}" >
	                        <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personName:document.institutionContactName,personLocalPhoneNumber:document.institutionContactPhoneNumber,personEmailAddress:document.institutionContactEmailAddress" /></div>
	                    </c:if>
                	</td>
				</tr>
				<tr>
					<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactPhoneNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactPhoneNumber}" property="document.institutionContactPhoneNumber" readOnly="${not (fullEntryMode)}" />
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactEmailAddress}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactEmailAddress}" property="document.institutionContactEmailAddress" readOnly="${not (fullEntryMode)}" />
	                </td>
				</tr>
			</c:if>	
        </table>
	</div>
</kul:tab>
