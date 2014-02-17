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
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="showAllPerDiemCategories" value="${KualiForm.showAllPerDiemCategories}" />
<c:set var="destinationNotFound" value="${KualiForm.document.primaryDestinationIndicator}" />
<c:set var="primaryDestinationAttributes" value="${DataDictionary.PrimaryDestination.attributes}" />
<c:set var="docType" value="${KualiForm.document.dataDictionaryEntry.documentTypeName }"/>

<h3>Trip Information Section</h3>
<table cellpadding="0" cellspacing="0" class="datatable" summary="Trip Information Section">
	<tr>
		<th class="bord-l-b" width="25%">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.tripTypeCode}" />
			</div>
		</th>
		<td class="datacell"  width="25%">
			<kul:htmlControlAttribute
			attributeEntry="${documentAttributes.tripTypeCode}"
			property="document.tripTypeCode" 
			onchange="document.getElementById('refreshPage').click();"
			readOnly="${!fullEntryMode}"/>
			<html:image property="methodToCall.recalculate" src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" alt="calculate" styleClass="tinybutton" styleId="refreshPage" style="display:none; visibility:hidden;"/>			
		</td>
		<c:choose>
			<c:when test="${blanketTravelEntryMode || blanketTravelViewMode}">
				<th class="bord-l-b" width="25%">
				<div align="right"><kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.blanketTravel}" /></div>
				</th>
				<td class="datacell" width="25%"><kul:htmlControlAttribute
					attributeEntry="${documentAttributes.blanketTravel}"
					property="document.blanketTravel" readOnly="${blanketTravelViewMode}"/>
				</td>
			</c:when>
			<c:otherwise>
				<th class="bord-l-b" width="25%">&nbsp;</th><td class="datacell" width="25%">&nbsp;</td>
			</c:otherwise>
		</c:choose>
	</tr>
	<tr>
		<th class="bord-l-b" width="25%">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.tripBegin}" />
			</div>
		</th>
		<td class="datacell" width="25%">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.tripBegin}" property="document.tripBegin" readOnly="${!fullEntryMode}" />
            <c:if test="${fullEntryMode}">
            	<img src="${ConfigProperties.kr.externalizable.images.url}cal.gif" id="document.tripBegin_datepicker" style="cursor: pointer;" title="Date selector" alt="Date selector" onmouseover="this.style.backgroundColor='red';" onmouseout="this.style.backgroundColor='transparent';"	/>
            	<script type="text/javascript">
                Calendar.setup(
                        {
                          inputField : "document.tripBegin", // ID of the input field
                          ifFormat : "%m/%d/%Y %I:%M %p", // the date format
                          button : "document.tripBegin_datepicker", // ID of the button
                          showsTime: true,
                          timeFormat: "12"
                        }
                );
            	</script>
            </c:if> 
		</td>
		<th class="bord-l-b" width="25%">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.tripEnd}" />
			</div>
		</th>
		<td class="datacell" width="25%">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.tripEnd}" property="document.tripEnd" readOnly="${!fullEntryMode}" />
            <c:if test="${fullEntryMode}">
            	<img src="${ConfigProperties.kr.externalizable.images.url}cal.gif" id="document.tripEnd_datepicker" style="cursor: pointer;" title="Date selector" alt="Date selector" onmouseover="this.style.backgroundColor='red';" onmouseout="this.style.backgroundColor='transparent';"	/>
            	<script type="text/javascript">
                Calendar.setup(
                        {
                          inputField : "document.tripEnd", // ID of the input field
                          ifFormat : "%m/%d/%Y %I:%M %p", // the date format
                          button : "document.tripEnd_datepicker", // ID of the button
                          showsTime: true,
                          timeFormat: "12"
                        }
                );
            	</script>
            </c:if> 			
		</td>
	</tr>
	<tr>
        <th class="bord-l-b">
        <div align="right"><kul:htmlAttributeLabel
            attributeEntry="${primaryDestinationAttributes.primaryDestinationName}" /></div>
        </th>
        <td class="datacell" colspan="3"><kul:htmlControlAttribute
            attributeEntry="${primaryDestinationAttributes.primaryDestinationName}"
            property="document.primaryDestinationName"
            onchange="document.getElementById('refreshPage').click();"
            readOnly="${!fullEntryMode || !destinationNotFound}" />
            <c:if test="${fullEntryMode}">
                <kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.PrimaryDestination"
                            fieldConversions="id:document.primaryDestinationId"
                            lookupParameters="document.primaryDestinationName:primaryDestinationName,document.tripTypeCode:tripTypeCode,document.primaryDestinationCountryState:countryState,document.primaryDestinationCounty:county" />
            </c:if>
        
            <c:if test="${fullEntryMode && enablePrimaryDestination}">
                <br />  
                <br />  
                <html:image property="methodToCall.enablePrimaryDestinationFields" src="${ConfigProperties.externalizable.images.url}tinybutton-destinationnotfound.gif" alt="destination not found" styleClass="tinybutton" />             
            </c:if>
            <c:if test="${enablePerDiemLookupLinks}">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.TravelPerDiem&docFormKey=88888888" target="_BLANK">Per Diem Links</a>
            </c:if>
        </td>
    </tr>
	<tr>
        <th class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel
            attributeEntry="${documentAttributes.primaryDestinationCountryState}" /></div>
        </th>
        <td class="datacell"><kul:htmlControlAttribute
            attributeEntry="${documentAttributes.primaryDestinationCountryState}"
            property="document.primaryDestinationCountryState" readOnly="${!fullEntryMode || !destinationNotFound}" /></td>
        <th class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel
            attributeEntry="${documentAttributes.primaryDestinationCounty}" /></div>
        </th>
        <td class="datacell"><kul:htmlControlAttribute
            attributeEntry="${documentAttributes.primaryDestinationCounty}"
            property="document.primaryDestinationCounty" readOnly="${!fullEntryMode || !destinationNotFound}"  /></td>
    </tr>
	<tr>
		<th class="bord-l-b">
		<div align="right"><kul:htmlAttributeLabel
			attributeEntry="${documentAttributes.tripDescription}" /></div>
		</th>
		<td class="datacell" colspan="3"><kul:htmlControlAttribute
			attributeEntry="${documentAttributes.tripDescription}"
			property="document.tripDescription"
			readOnly="${!fullEntryMode}" /></td>
	</tr>
	<c:if test="${(docType == 'TA' || docType == 'TR') && travelManager}">
	<tr>
		<th class="bord-l-b">
		<div align="right"><kul:htmlAttributeLabel
			attributeEntry="${documentAttributes.delinquentTRException}" /></div>
		</th>
		<td class="datacell" colspan="3"><kul:htmlControlAttribute
			attributeEntry="${documentAttributes.delinquentTRException}"
			property="document.delinquentTRException" />
		</td>
	</tr>
	</c:if>	
    <jsp:doBody />
</table>
