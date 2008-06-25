<%--
 Copyright 2007 The Kuali Foundation.
 
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

<kul:tab tabTitle="Payee Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_PAYEE_TAB_ERRORS},document.dvPayeeDetail.disbursementVoucherPayeeTypeCode">
	<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />
    <div class="tab-container" align=center > 
        <h3>Payee Information</h3>
	<table cellpadding=0 class="datatable" summary="Payee Section">
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}" property="document.dvPayeeDetail.disbVchrPayeeIdNumber" readOnly="true" />
                &nbsp;(${KualiForm.document.dvPayeeDetail.disbursementVoucherPayeeTypeName})
				<html:hidden property="document.dvPayeeDetail.disbursementVoucherPayeeTypeCode" />
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right">City:
              </div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeCityName}" property="document.dvPayeeDetail.disbVchrPayeeCityName" readOnly="${!payeeEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeePersonName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeePersonName}" property="document.dvPayeeDetail.disbVchrPayeePersonName" readOnly="true"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right">State:
              </div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeStateCode}" property="document.dvPayeeDetail.disbVchrPayeeStateCode" readOnly="${!payeeEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right">Address Line 1:</div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeLine1Addr}" property="document.dvPayeeDetail.disbVchrPayeeLine1Addr" readOnly="${!payeeEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right">Zip Code:
              </div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeZipCode}" property="document.dvPayeeDetail.disbVchrPayeeZipCode" readOnly="${!payeeEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right">Address Line 2:</div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeLine2Addr}" property="document.dvPayeeDetail.disbVchrPayeeLine2Addr" readOnly="${!payeeEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right">Country:</div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeCountryCode}" property="document.dvPayeeDetail.disbVchrPayeeCountryCode" readOnly="${!payeeEntryMode}"/>  
              </td>
            </tr>
     </table>
     </div>
</kul:tab>
