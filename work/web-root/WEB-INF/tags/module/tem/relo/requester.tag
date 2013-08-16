<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<c:set var="documentAttributes" value="${DataDictionary.TravelRelocationDocument.attributes}" />
<c:set var="requesterAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="8" />

	<h3>Requester (Person to be Reimbursed)</h3>

	<table cellpadding="0" cellspacing="0" class="datatable" summary="Requester Section">
		<c:if test="${fullEntryMode}">
        <tr>
            <th class="bord-l-b"><div align="right">Requester Lookup:</div></th>
            <td class="datacell" colspan="3">
            	<div align="left">
			    	<kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TravelerProfileForLookup" lookupParameters="document.traveler.travelerTypeCode:travelerTypeCode" />
				</div>
            </td>
        </tr>
        </c:if>
        <tr>
            <th class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${requesterAttributes.principalId}" /></div></th>
            <td class="datacell" colspan="3">
            	<div align="left">
			    	<kul:htmlControlAttribute attributeEntry="${requesterAttributes.principalId}" property="document.traveler.principalId" readOnly="true" />
				</div>
            </td>
        </tr>
        <tr>
            <th class="bord-l-b"><div align="right">Requester Type:</div></th>
            <td class="datacell" colspan="3">
            	<div align="left">
			    	<kul:htmlControlAttribute attributeEntry="${DataDictionary.TravelerType.attributes.name}" property="document.traveler.travelerType.name" readOnly="true"/>
				</div>
            </td>
        </tr>
        <tr>
        	<th class="bord-l-b">
	            <div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.temProfileId}" /> 
	           	</div>
            </th>
            <td class="datacell">
            	<kul:htmlControlAttribute attributeEntry="${requesterAttributes.principalName}" property="document.traveler.principalName" readOnly="true"/>            
            </td>
            <th>
            	<div align="right">Preparer:</div>
            </th>
            <td class="datacell">
            	${KualiForm.document.preparer}
			</td>            
		</tr>
       	<tr>
            <th class="bord-l-b">
            	<div align="right"> <kul:htmlAttributeLabel attributeEntry="${requesterAttributes.firstName}" /></div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.firstName}" property="document.traveler.firstName" readOnly="true"/>        
            </td>
            <th class="bord-l-b">
            	<div align="right"><kul:htmlAttributeLabel attributeEntry="${requesterAttributes.lastName}" /></div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.lastName}" property="document.traveler.lastName" readOnly="true"/>          
            </td>
        </tr>              
        <c:if test="${fullEntryMode}">
		<tr>
			<th class="bord-l-b"><div align="right">Address Lookup:</div> </th>
			<td class="datacell" colspan="3">
            	<div align="left">&nbsp;
			    	<kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TemProfileAddress" lookupParameters="document.traveler.principalId:principalId,document.traveler.customerNumber:customerNumber"
			    	fieldConversions="streetAddressLine1:document.traveler.streetAddressLine1,streetAddressLine2:document.traveler.streetAddressLine2,zipCode:document.traveler.zipCode,countryCode:document.traveler.countryCode,stateCode:document.traveler.stateCode,cityName:document.traveler.cityName" />
				</div>
			</td>
		</tr>
		</c:if>       
        <tr>
            <th class="bord-l-b">
	            <div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${requesterAttributes.streetAddressLine1}" />
	           	</div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.streetAddressLine1}" property="document.traveler.streetAddressLine1" readOnly="true"/>            
            </td>
            <th class="bord-l-b">
            	<div align="right">
            		<kul:htmlAttributeLabel attributeEntry="${requesterAttributes.streetAddressLine2}" />
                </div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.streetAddressLine2}" property="document.traveler.streetAddressLine2" readOnly="true"/>            
            </td>
        </tr>      
        <tr>
            <th class="bord-l-b">
            	<div align="right">
            		<kul:htmlAttributeLabel attributeEntry="${requesterAttributes.cityName}" />
                </div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.cityName}" property="document.traveler.cityName" readOnly="true"/>            
            </td>
            <th class="bord-l-b">
            	<div align="right">
            		<kul:htmlAttributeLabel attributeEntry="${requesterAttributes.stateCode}" />
                </div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.stateCode}" property="document.traveler.stateCode" readOnly="true" tabindexOverride="${tabindexOverrideBase + 5}"/>           
            </td>
        </tr>       
        <tr>
            <th class="bord-l-b">
            	<div align="right"><kul:htmlAttributeLabel attributeEntry="${requesterAttributes.countryCode}" /></div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.countryCode}" property="document.traveler.countryCode" readOnly="true" onchange="javascript: getAllStates();" tabindexOverride="${tabindexOverrideBase + 4}"/>           
            </td>
            <th class="bord-l-b">
           		<div align="right">
           			<kul:htmlAttributeLabel attributeEntry="${requesterAttributes.zipCode}" />
                </div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.zipCode}" property="document.traveler.zipCode" readOnly="true"/>
				<c:if test="${!readOnly  && fullEntryMode}">
              		<kul:lookup boClassName="org.kuali.rice.kns.bo.PostalCode" fieldConversions="postalCode:document.traveler.zipCode,postalCountryCode:document.traveler.countryCode,postalStateCode:document.traveler.stateCode,postalCityName:document.traveler.cityName" 
              		lookupParameters="document.traveler.countryCode:postalCountryCode,document.traveler.zipCode:postalCode,document.traveler.stateCode:postalStateCode,document.traveler.cityName:postalCityName" />
              	</c:if>
            </td>
        </tr>        
        <tr>
            <th class="bord-l-b">
            	<div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${requesterAttributes.emailAddress}" />
                </div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.emailAddress}" property="document.traveler.emailAddress" readOnly="true"/>            
            </td>
            <th class="bord-l-b">
            	<div align="right">
            		<kul:htmlAttributeLabel attributeEntry="${requesterAttributes.phoneNumber}" />
            	</div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${requesterAttributes.phoneNumber}" property="document.traveler.phoneNumber" readOnly="true"/>            
            </td>
        </tr>
	</table>