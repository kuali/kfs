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
