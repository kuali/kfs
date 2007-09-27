<%--
 Copyright 2006 The Kuali Foundation.
 
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

<kul:tab tabTitle="Special Handling" defaultOpen="false" tabErrorKey="${KFSConstants.DV_SPECHAND_TAB_ERRORS}">
	<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />
    <div class="tab-container" align=center > 
    <div class="h2-container">
<h2>Send Check To</h2>
</div>
	<table cellpadding=0 class="datatable" summary="Special Handling Section">          
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right">* <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrRemitPersonName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrRemitPersonName}" property="document.dvPayeeDetail.disbVchrRemitPersonName" readOnly="${!fullEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrRemitCityName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrRemitCityName}" property="document.dvPayeeDetail.disbVchrRemitCityName" readOnly="${!fullEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right">* <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrRemitLine1Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrRemitLine1Addr}" property="document.dvPayeeDetail.disbVchrRemitLine1Addr" readOnly="${!fullEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrRemitStateCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrRemitStateCode}" property="document.dvPayeeDetail.disbVchrRemitStateCode" readOnly="${!fullEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrRemitLine2Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrRemitLine2Addr}" property="document.dvPayeeDetail.disbVchrRemitLine2Addr" readOnly="${!fullEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrRemitZipCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrRemitZipCode}" property="document.dvPayeeDetail.disbVchrRemitZipCode" readOnly="${!fullEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"></th>
              <td align=left valign=middle class="datacell">
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrRemitCountryCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrRemitCountryCode}" property="document.dvPayeeDetail.disbVchrRemitCountryCode" readOnly="${!fullEntryMode}"/>  
              </td>
            </tr>
     </table>
     </div>
</kul:tab>
