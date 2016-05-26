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

<%@ attribute name="currencyProperty" required="true" %>
<%@ attribute name="coinProperty" required="true" %>
<%@ attribute name="confirmedCurrencyProperty" required="false" %>
<%@ attribute name="confirmedCoinProperty" required="false" %>
<%@ attribute name="readOnly" required="false" %>
<%@ attribute name="editingMode" required="false" type="java.util.Map" %>
<%@ attribute name="confirmMode" required="false" %>
<%@ attribute name="confirmed" required="false" %>

<c:if test="${!readOnly && !empty editingMode}">
  <c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
</c:if>

<%-- FIXME FIXED by KFSCNTRB-1793 
	We should show both the original and confirmed details after CashManagerment confirmation; 	only that neither column would be editable.
--%>	 
<c:set var="showConfirm" value="${confirmMode || confirmed}" />

<c:set var="currencyAttributes" value="${DataDictionary.CurrencyDetail.attributes}" />
<c:set var="coinAttributes" value="${DataDictionary.CoinDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="20" />
<c:set var="displayCashReceiptDenominationDetail" value="${KualiForm.displayCashReceiptDenominationDetail}" />

      <table border="0" cellspacing="0" cellpadding="0" class="datatable" width="100%">
        <c:if test="${showConfirm}">
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
          <c:if test="${displayCashReceiptDenominationDetail or showConfirm}">
          	<th>Count</th>
          </c:if>
          <th>Amount</th>
          <c:if test="${showConfirm}">
          	<th>Count</th>
          	<th>Amount</th>
          </c:if>
          <th>&nbsp;</th>
          <c:if test="${displayCashReceiptDenominationDetail or showConfirm}">
          	<th>Count</th>
          </c:if>
          <th>Amount</th>
          <c:if test="${showConfirm}">
          	<th>Count</th>
        	<th>Amount</th>
          </c:if>
        </tr>
        
   <c:choose>
	<c:when test="${displayCashReceiptDenominationDetail}">
        <tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.hundredDollarCount" attributeEntry="${currencyAttributes.hundredDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${currencyProperty}.hundredDollarCount" attributeEntry="${currencyAttributes.hundredDollarCount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}"/>
          </td>
          <td><span id="${currencyProperty}.financialDocumentHundredDollarAmount.span">$<bean:write name="KualiForm" property="${currencyProperty}.financialDocumentHundredDollarAmount" /></span></td>
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.hundredDollarCount" attributeEntry="${currencyAttributes.hundredDollarCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.hundredCentCount" attributeEntry="${coinAttributes.hundredCentCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.fiftyDollarCount" attributeEntry="${currencyAttributes.fiftyDollarCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.fiftyCentCount" attributeEntry="${coinAttributes.fiftyCentCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.twentyDollarCount" attributeEntry="${currencyAttributes.twentyDollarCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.twentyFiveCentCount" attributeEntry="${coinAttributes.twentyFiveCentCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.tenDollarCount" attributeEntry="${currencyAttributes.tenDollarCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.tenCentCount" attributeEntry="${coinAttributes.tenCentCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.fiveDollarCount" attributeEntry="${currencyAttributes.fiveDollarCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.fiveCentCount" attributeEntry="${coinAttributes.fiveCentCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.twoDollarCount" attributeEntry="${currencyAttributes.twoDollarCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCoinProperty}.oneCentCount" attributeEntry="${coinAttributes.oneCentCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
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
          <c:if test="${showConfirm}">
          <td>
            <kul:htmlControlAttribute property="${confirmedCurrencyProperty}.oneDollarCount" attributeEntry="${currencyAttributes.oneDollarCount}" readOnly="${!confirmMode}" tabindexOverride="${tabindexOverrideBase}"/>
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
          <c:if test="${showConfirm}">
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${confirmedCoinProperty}.financialDocumentOtherCentAmount" attributeEntry="${coinAttributes.financialDocumentOtherCentAmount}" readOnly="${!confirmMode}"/>
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
          <c:if test="${showConfirm}">
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${confirmedCurrencyProperty}.financialDocumentOtherDollarAmount" attributeEntry="${currencyAttributes.financialDocumentOtherDollarAmount}" readOnly="${!confirmMode}"/>
          </td>
          </c:if>
          <td colspan="5">&nbsp;</td>
        </tr>
        
        <tr>
          <td class="total-line" colspan="2">&nbsp;</td>
          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${currencyProperty}.totalAmount" /></strong></td>
          <c:if test="${showConfirm}">
          <td class="total-line" colspan="1">&nbsp;</td>
          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${confirmedCurrencyProperty}.totalAmount" /></strong></td>
          </c:if>
          <td class="total-line" colspan="2">&nbsp;</td>
          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${coinProperty}.totalAmount" /></strong></td>
          <c:if test="${showConfirm}">
          <td class="total-line" colspan="1">&nbsp;</td>
          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${confirmedCoinProperty}.totalAmount" /></strong></td>
          </c:if>
        </tr>
       </c:when>
       <c:otherwise>
       	<tr>
          <td>
            <kul:htmlAttributeLabel labelFor="${currencyProperty}.financialDocumentDollarAmount" attributeEntry="${currencyAttributes.financialDocumentDollarAmount}" />
          </td>
          <c:if test="${showConfirm}">
			<td>&nbsp;</td>
		  </c:if>
          <td>
            $<kul:htmlControlAttribute property="${currencyProperty}.financialDocumentDollarAmount" attributeEntry="${currencyAttributes.financialDocumentDollarAmount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase}" />
          </td>
          <c:if test="${showConfirm}">
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${confirmedCurrencyProperty}.financialDocumentDollarAmount" attributeEntry="${currencyAttributes.financialDocumentDollarAmount}" readOnly="${!confirmMode}"/>
          </td>
          </c:if>
          <td>
            <kul:htmlAttributeLabel labelFor="${coinProperty}.financialDocumentCentAmount" attributeEntry="${coinAttributes.financialDocumentCentAmount}" />
          </td>
          <c:if test="${showConfirm}">
		  	<td>&nbsp;</td>
		  </c:if>
          <td>
            $<kul:htmlControlAttribute property="${coinProperty}.financialDocumentCentAmount" attributeEntry="${coinAttributes.financialDocumentCentAmount}" readOnly="${readOnly}" tabindexOverride="${tabindexOverrideBase + 5}"/>
          </td>
          <c:if test="${showConfirm}">
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${confirmedCoinProperty}.financialDocumentCentAmount" attributeEntry="${coinAttributes.financialDocumentCentAmount}" readOnly="${!confirmMode}"/>
          </td>
          </c:if>
        </tr>
        <tr>
          <c:choose>
          	<c:when test="${showConfirm}">
          	  <td class="total-line" colspan="2">&nbsp;</td>
	          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${currencyProperty}.totalAmount" /></strong></td>
	          <td class="total-line" colspan="1">&nbsp;</td>
	          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${confirmedCurrencyProperty}.totalAmount" /></strong></td>
	          <td class="total-line" colspan="2">&nbsp;</td>
	          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${coinProperty}.totalAmount" /></strong></td>
	          <td class="total-line" colspan="1">&nbsp;</td>
	          <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${confirmedCoinProperty}.totalAmount" /></strong></td>
          	</c:when>
          	<c:otherwise>
          	  <td class="total-line" colspan="">&nbsp;</td>
          	  <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${currencyProperty}.totalAmount" /></strong></td>
          	  <td class="total-line" colspan="">&nbsp;</td>
          	  <td class="total-line"><strong>Total: <bean:write name="KualiForm" property="${coinProperty}.totalAmount" /></strong></td>
          	</c:otherwise>
          </c:choose>
        </tr>
      </c:otherwise>
    </c:choose>
  </table>
