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

<kul:tab tabTitle="Special Handling" defaultOpen="true" tabErrorKey="${KFSConstants.TRVL_SPECHAND_TAB_ERRORS}">
	<c:set var="travelPaymentAttributes" value="${DataDictionary.TravelPayment.attributes}" />
    <div class="tab-container" align=center > 
<h3>Send Check To</h3>
	<table cellpadding=0 class="datatable" summary="Special Handling Section">          
            <tr>
              <th class="bord-l-b">&nbsp;</th>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingCityName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingCityName}" property="document.travelPayment.specialHandlingCityName" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingLine1Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingLine1Addr}" property="document.travelPayment.specialHandlingLine1Addr" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingStateCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingStateCode}" property="document.travelPayment.specialHandlingStateCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingLine2Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingLine2Addr}" property="document.travelPayment.specialHandlingLine2Addr" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingZipCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingZipCode}" property="document.travelPayment.specialHandlingZipCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"></th>
              <td align=left valign=middle class="datacell">
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingCountryCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingCountryCode}" property="document.travelPayment.specialHandlingCountryCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
     </table>
     </div>
</kul:tab>
