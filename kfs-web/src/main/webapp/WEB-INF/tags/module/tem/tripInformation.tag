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
