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

<kul:tab tabTitle="Wire Transfer" defaultOpen="${!empty KualiForm.document.wireTransfer.bankName}" tabErrorKey="${KFSConstants.WIRETRANSFER_TAB_ERRORS}">
	<c:set var="wireTransAttributes" value="${DataDictionary.PaymentSourceWireTransfer.attributes}" />
    <div class="tab-container" align=center > 
    <h3>Wire Transfer</h3>
	<table cellpadding=0 class="datatable" summary="Wire Transfer Section">
            
               <tr>
                <td colspan=4 align=left valign=middle class="datacell"><bean:write name="KualiForm" property="wireChargeMessage" /></td>
              </tr>
            
               <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.automatedClearingHouseProfileNumber}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.automatedClearingHouseProfileNumber}" property="document.wireTransfer.automatedClearingHouseProfileNumber" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.wireTransferFeeWaiverIndicator}"/> </div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.wireTransferFeeWaiverIndicator}" property="document.wireTransfer.wireTransferFeeWaiverIndicator" readOnly="${!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
             
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.bankName}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.bankName}" property="document.wireTransfer.bankName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.additionalWireText}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.additionalWireText}" property="document.wireTransfer.additionalWireText" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.bankRoutingNumber}"/>
                    <c:if test="${fullEntryMode||wireEntryMode||frnEntryMode}">
                        <br> *required for US bank
                    </c:if>
                </div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.bankRoutingNumber}" property="document.wireTransfer.bankRoutingNumber" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.attentionLineText}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.attentionLineText}" property="document.wireTransfer.attentionLineText" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.bankCityName}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.bankCityName}" property="document.wireTransfer.bankCityName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.currencyTypeCode}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.currencyTypeCode}" property="document.wireTransfer.currencyTypeCode" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.bankStateCode}"/>
                    <c:if test="${fullEntryMode||wireEntryMode||frnEntryMode}">
                        <br> *required for US bank
                    </c:if>
                </div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.bankStateCode}" property="document.wireTransfer.bankStateCode" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.currencyTypeName}"/></div></th>
                <td class="datacell" colspan="3">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.currencyTypeName}" property="document.wireTransfer.currencyTypeName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.bankCountryCode}"/></div></th>
                <td class="datacell" colspan="3">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.bankCountryCode}" property="document.wireTransfer.bankCountryCode" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.payeeAccountNumber}"/></div></th>
                <td class="datacell" colspan="3">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.payeeAccountNumber}" property="document.wireTransfer.payeeAccountNumber"
                   readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}" />
                </td>
              </tr>
              
              <tr>
                <th scope=row class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.payeeAccountName}"/></div></th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.payeeAccountName}" property="document.wireTransfer.payeeAccountName" readOnly="${!fullEntryMode&&!wireEntryMode&&!frnEntryMode}"/>
                </td>
                <td class="datacell" colspan="2">
                  <bean:message key="message.wiretransfer.fee"/>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
</kul:tab>
