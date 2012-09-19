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

<kul:tab tabTitle="Wire Transfer" defaultOpen="${!empty KualiForm.document.dvWireTransfer.disbursementVoucherBankName}" tabErrorKey="${KFSConstants.DV_WIRETRANSFER_TAB_ERRORS}">
	<c:set var="wireTransAttributes" value="${DataDictionary.DisbursementVoucherWireTransfer.attributes}" />
    <div class="tab-container" align=center > 
    <h3>Wire Transfer</h3>
	<table cellpadding=0 class="datatable" summary="Wire Transfer Section">
            
               <tr>
                <td colspan=4 align=left valign=middle class="datacell"><bean:write name="KualiForm" property="wireChargeMessage" /></td>
              </tr>
            
               <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbursementVoucherAutomatedClearingHouseProfileNumber}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbursementVoucherAutomatedClearingHouseProfileNumber}" property="document.dvWireTransfer.disbursementVoucherAutomatedClearingHouseProfileNumber" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbursementVoucherWireTransferFeeWaiverIndicator}"/> </div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbursementVoucherWireTransferFeeWaiverIndicator}" property="document.dvWireTransfer.disbursementVoucherWireTransferFeeWaiverIndicator" readOnly="${!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
             
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbursementVoucherBankName}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbursementVoucherBankName}" property="document.dvWireTransfer.disbursementVoucherBankName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrAdditionalWireText}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrAdditionalWireText}" property="document.dvWireTransfer.disbVchrAdditionalWireText" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrBankRoutingNumber}"/>
                    <c:if test="${fullEntryMode||wireEntryMode||frnEntryMode}">
                        <br> *required for US bank
                    </c:if>
                </div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrBankRoutingNumber}" property="document.dvWireTransfer.disbVchrBankRoutingNumber" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrAttentionLineText}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrAttentionLineText}" property="document.dvWireTransfer.disbVchrAttentionLineText" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrBankCityName}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrBankCityName}" property="document.dvWireTransfer.disbVchrBankCityName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrCurrencyTypeCode}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrCurrencyTypeCode}" property="document.dvWireTransfer.disbVchrCurrencyTypeCode" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrBankStateCode}"/>
                    <c:if test="${fullEntryMode||wireEntryMode||frnEntryMode}">
                        <br> *required for US bank
                    </c:if>
                </div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrBankStateCode}" property="document.dvWireTransfer.disbVchrBankStateCode" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrCurrencyTypeName}"/></div></th>
                <td class="datacell" colspan="3">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrCurrencyTypeName}" property="document.dvWireTransfer.disbVchrCurrencyTypeName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrBankCountryCode}"/></div></th>
                <td class="datacell" colspan="3">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrBankCountryCode}" property="document.dvWireTransfer.disbVchrBankCountryCode" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrPayeeAccountNumber}"/></div></th>
                <td class="datacell" colspan="3">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrPayeeAccountNumber}" property="document.dvWireTransfer.disbVchrPayeeAccountNumber"
                   readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}" />
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbursementVoucherPayeeAccountName}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbursementVoucherPayeeAccountName}" property="document.dvWireTransfer.disbursementVoucherPayeeAccountName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <td class="datacell" colspan="2">
                  <bean:message key="message.wiretransfer.fee"/>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
</kul:tab>
