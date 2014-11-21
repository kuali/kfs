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

<kul:tab tabTitle="Special Handling" defaultOpen="${!empty KualiForm.document.dvPayeeDetail.disbVchrSpecialHandlingPersonName}" tabErrorKey="${KFSConstants.DV_SPECHAND_TAB_ERRORS}">
	<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />
    <div class="tab-container" align=center > 
<h3>Send Check To</h3>
	<table cellpadding=0 class="datatable" summary="Special Handling Section">          
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrSpecialHandlingPersonName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrSpecialHandlingPersonName}" property="document.dvPayeeDetail.disbVchrSpecialHandlingPersonName" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrSpecialHandlingCityName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrSpecialHandlingCityName}" property="document.dvPayeeDetail.disbVchrSpecialHandlingCityName" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrSpecialHandlingLine1Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrSpecialHandlingLine1Addr}" property="document.dvPayeeDetail.disbVchrSpecialHandlingLine1Addr" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrSpecialHandlingStateCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrSpecialHandlingStateCode}" property="document.dvPayeeDetail.disbVchrSpecialHandlingStateCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrSpecialHandlingLine2Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrSpecialHandlingLine2Addr}" property="document.dvPayeeDetail.disbVchrSpecialHandlingLine2Addr" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrSpecialHandlingZipCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrSpecialHandlingZipCode}" property="document.dvPayeeDetail.disbVchrSpecialHandlingZipCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"></th>
              <td align=left valign=middle class="datacell">
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrSpecialHandlingCountryCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrSpecialHandlingCountryCode}" property="document.dvPayeeDetail.disbVchrSpecialHandlingCountryCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) && !paymentHandlingEntryMode}"/>  
              </td>
            </tr>
     </table>
     </div>
</kul:tab>
