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
 
 KFSMI-5067 Fixed the ability to edit the special handling tab when specialHandlingChangingEntryMode variable is true
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
