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

<%@ attribute name="currencyProperty" required="true" %>
<%@ attribute name="coinProperty" required="true" %>
<%@ attribute name="confirmedCurrencyProperty" required="false" %>
<%@ attribute name="confirmedCoinProperty" required="false" %>
<%@ attribute name="readOnly" required="false" %>
<%@ attribute name="editingMode" required="false" type="java.util.Map" %>
<%@ attribute name="confirmMode" required="false" %>
<%@ attribute name="totalChangeAmount" required="false" %>

<c:if test="${!readOnly && !empty editingMode}">
  <c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
</c:if>

<c:if test="${!empty totalChangeAmount}">
	<c:set var="changeMode" value="True" />
</c:if>
<c:set var="currencyAttributes" value="${DataDictionary.CurrencyDetail.attributes}" />
<c:set var="coinAttributes" value="${DataDictionary.CoinDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="20" />

      <table border="0" cellspacing="0" cellpadding="0" class="datatable" width="100%">
        <c:if test="${confirmMode && !changeMode}">
          <tr>
          	<th>&nbsp;</th>
          	<th colspan="2">Original</th>
          	<th colspan="2">Cash Manager</th>
          	<th>&nbsp;</th>
          	<th colspan="2">Original</th>
          	<th colspan="2">Cash Manager</th>
          </tr>
        </c:if>
        <tr>
          <th>&nbsp;</th>
          <th>Count</th>
          <th>Amount</th>
          <c:if test="${confirmMode && !changeMode }">
          	<th>Count</th>
          	<th>Amount</th>
          </c:if>
          <th>&nbsp;</th>
          <th>Count</th>
          <th>Amount</th>
          <c:if test="${confirmMode && !changeMode}">
          	<th>Count</th>
          	<th>Amount</th>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.hundredDollarCount" attributeEntry="${currencyAttributes.hundredDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.hundredDollarCount" attributeEntry="${currencyAttributes.hundredDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}"/>
          </td>
          <td><span id="${currencyProperty}.financialDocumentHundredDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentHundredDollarAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.hundredDollarCount" attributeEntry="${currencyAttributes.hundredDollarCount}" />
          </td>
          <td><span id="${confirmedCurrencyProperty}.financialDocumentHundredDollarAmount.span">$<bean:write name="KualiForm" property="${confirmedCurrencyProperty}.financialDocumentHundredDollarAmount" /></span></td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.hundredCentCount" attributeEntry="${coinAttributes.hundredCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${coinProperty}.hundredCentCount" attributeEntry="${coinAttributes.hundredCentCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
          </td>
          <td><span id="${coinProperty}.financialDocumentHundredCentAmount.span">$<bean:write name="KualiForm" property="${coinProperty}.financialDocumentHundredCentAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.hundredCentCount" attributeEntry="${coinAttributes.hundredCentCount}" />
          </td>
          <td><span id="${confirmedCoinProperty}.financialDocumentHundredCentAmount.span">$<bean:write name="KualiForm" property="${confirmedCoinProperty}.financialDocumentHundredCentAmount" /></span></td>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.fiftyDollarCount" attributeEntry="${currencyAttributes.fiftyDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.fiftyDollarCount" attributeEntry="${currencyAttributes.fiftyDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}"/>
          </td>
          <td><span id="${currencyProperty}.financialDocumentFiftyDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentFiftyDollarAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.fiftyDollarCount" attributeEntry="${currencyAttributes.fiftyDollarCount}" />
          </td>
          <td><span id="${confirmedCurrencyProperty}.financialDocumentFiftyDollarAmount.span">$<bean:write name="KualiForm" property="${confirmedCurrencyProperty}.financialDocumentFiftyDollarAmount" /></span></td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.fiftyCentCount" attributeEntry="${coinAttributes.fiftyCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${coinProperty}.fiftyCentCount" attributeEntry="${coinAttributes.fiftyCentCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}" />
          </td>
          <td><span id="${coinProperty}.financialDocumentFiftyCentAmount.span">$<bean:write name="KualiForm" property="${coinProperty}.financialDocumentFiftyCentAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.fiftyCentCount" attributeEntry="${coinAttributes.fiftyCentCount}" />
          </td>
          <td><span id="${confirmedCoinProperty}.financialDocumentFiftyCentAmount.span">$<bean:write name="KualiForm" property="${confirmedCoinProperty}.financialDocumentFiftyCentAmount" /></span></td>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.twentyDollarCount" attributeEntry="${currencyAttributes.twentyDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.twentyDollarCount" attributeEntry="${currencyAttributes.twentyDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}" />
          </td>
          <td><span id="${currencyProperty}.financialDocumentTwentyDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentTwentyDollarAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.twentyDollarCount" attributeEntry="${currencyAttributes.twentyDollarCount}" />
          </td>
          <td><span id="${confirmedCurrencyProperty}.financialDocumentTwentyDollarAmount.span">$<bean:write name="KualiForm" property="${confirmedCurrencyProperty}.financialDocumentTwentyDollarAmount" /></span></td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.twentyFiveCentCount" attributeEntry="${coinAttributes.twentyFiveCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${coinProperty}.twentyFiveCentCount" attributeEntry="${coinAttributes.twentyFiveCentCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}" />
          </td>
          <td><span id="${coinProperty}.financialDocumentTwentyFiveCentAmount.span">$<bean:write name="KualiForm" property="${coinProperty}.financialDocumentTwentyFiveCentAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.twentyFiveCentCount" attributeEntry="${coinAttributes.twentyFiveCentCount}" />
          </td>
          <td><span id="${confirmedCoinProperty}.financialDocumentTwentyFiveCentAmount.span">$<bean:write name="KualiForm" property="${confirmedCoinProperty}.financialDocumentTwentyFiveCentAmount" /></span></td>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.tenDollarCount" attributeEntry="${currencyAttributes.tenDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.tenDollarCount" attributeEntry="${currencyAttributes.tenDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}" />
          </td>
          <td><span id="${currencyProperty}.financialDocumentTenDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentTenDollarAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.tenDollarCount" attributeEntry="${currencyAttributes.tenDollarCount}"/>
          </td>
          <td><span id="${confirmedCurrencyProperty}.financialDocumentTenDollarAmount.span">$<bean:write name="KualiForm" property="${confirmedCurrencyProperty}.financialDocumentTenDollarAmount" /></span></td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.tenCentCount" attributeEntry="${coinAttributes.tenCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${coinProperty}.tenCentCount" attributeEntry="${coinAttributes.tenCentCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}" />
          </td>
          <td><span id="${coinProperty}.financialDocumentTenCentAmount.span">$<bean:write name="KualiForm" property="${coinProperty}.financialDocumentTenCentAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.tenCentCount" attributeEntry="${coinAttributes.tenCentCount}"/>
          </td>
          <td><span id="${confirmedCoinProperty}.financialDocumentTenCentAmount.span">$<bean:write name="KualiForm" property="${confirmedCoinProperty}.financialDocumentTenCentAmount" /></span></td>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.fiveDollarCount" attributeEntry="${currencyAttributes.fiveDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.fiveDollarCount" attributeEntry="${currencyAttributes.fiveDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}"/>
          </td>
          <td><span id="${currencyProperty}.financialDocumentFiveDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentFiveDollarAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.fiveDollarCount" attributeEntry="${currencyAttributes.fiveDollarCount}" />
          </td>
          <td><span id="${confirmedCurrencyProperty}.financialDocumentFiveDollarAmount.span">$<bean:write name="KualiForm" property="${confirmedCurrencyProperty}.financialDocumentFiveDollarAmount" /></span></td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.fiveCentCount" attributeEntry="${coinAttributes.fiveCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${coinProperty}.fiveCentCount" attributeEntry="${coinAttributes.fiveCentCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}" />
          </td>
          <td><span id="${coinProperty}.financialDocumentFiveCentAmount.span">$<bean:write name="KualiForm" property="${coinProperty}.financialDocumentFiveCentAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.fiveCentCount" attributeEntry="${coinAttributes.fiveCentCount}" />
          </td>
          <td><span id="${confirmedCoinProperty}.financialDocumentFiveCentAmount.span">$<bean:write name="KualiForm" property="${confirmedCoinProperty}.financialDocumentFiveCentAmount" /></span></td>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.twoDollarCount" attributeEntry="${currencyAttributes.twoDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.twoDollarCount" attributeEntry="${currencyAttributes.twoDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}"/>
          </td>
          <td><span id="${currencyProperty}.financialDocumentTwoDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentTwoDollarAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.twoDollarCount" attributeEntry="${currencyAttributes.twoDollarCount}" />
          </td>
          <td><span id="${confirmedCurrencyProperty}.financialDocumentTwoDollarAmount.span">$<bean:write name="KualiForm" property="${confirmedCurrencyProperty}.financialDocumentTwoDollarAmount" /></span></td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.oneCentCount" attributeEntry="${coinAttributes.oneCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${coinProperty}.oneCentCount" attributeEntry="${coinAttributes.oneCentCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}" />
          </td>
          <td><span id="${coinProperty}.financialDocumentOneCentAmount.span">$<bean:write name="KualiForm" property="${coinProperty}.financialDocumentOneCentAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.oneCentCount" attributeEntry="${coinAttributes.oneCentCount}" />
          </td>
          <td><span id="${confirmedCoinProperty}.financialDocumentOneCentAmount.span">$<bean:write name="KualiForm" property="${confirmedCoinProperty}.financialDocumentOneCentAmount" /></span></td>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.oneDollarCount" attributeEntry="${currencyAttributes.oneDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.oneDollarCount" attributeEntry="${currencyAttributes.oneDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}"/>
          </td>
          <td><span id="${currencyProperty}.financialDocumentOneDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentOneDollarAmount" /></span></td>
          <c:if test="${confirmMode && !changeMode}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.oneDollarCount" attributeEntry="${currencyAttributes.oneDollarCount}" />
          </td>
          <td><span id="${confirmedCurrencyProperty}.financialDocumentOneDollarAmount.span">$<bean:write name="KualiForm" property="${confirmedCurrencyProperty}.financialDocumentOneDollarAmount" /></span></td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.financialDocumentOtherCentAmount" attributeEntry="${coinAttributes.financialDocumentOtherCentAmount}" />
          </td>
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${coinProperty}.financialDocumentOtherCentAmount" attributeEntry="${coinAttributes.financialDocumentOtherCentAmount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
          </td>
          <c:if test="${confirmMode && !changeMode}">
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${confirmedCoinProperty}.financialDocumentOtherCentAmount" attributeEntry="${coinAttributes.financialDocumentOtherCentAmount}" />
          </td>
          </c:if>
        </tr>
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.financialDocumentOtherDollarAmount" attributeEntry="${currencyAttributes.financialDocumentOtherDollarAmount}" />
          </td>
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${currencyProperty}.financialDocumentOtherDollarAmount" attributeEntry="${currencyAttributes.financialDocumentOtherDollarAmount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}" />
          </td>
          <c:if test="${confirmMode && !changeMode}">
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${confirmedCurrencyProperty}.financialDocumentOtherDollarAmount" attributeEntry="${currencyAttributes.financialDocumentOtherDollarAmount}" />
          </td>
          </c:if>
          <td colspan="5">&nbsp;</td>
        </tr>
        <c:if test="${changeMode}">
        <tr>
        	<td class="total-line" colspan="5">&nbsp;</td>
            <td class="total-line"><strong>Total: <c:out value="${totalChangeAmount}" /></strong></td>
        </tr>
        </c:if>
      </table>
