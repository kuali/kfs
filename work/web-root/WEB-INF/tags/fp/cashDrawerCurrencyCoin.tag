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

<%@ attribute name="cashDrawerProperty" required="true" %>
<%@ attribute name="readOnly" required="false" %>
<%@ attribute name="showCashDrawerSummary" required="false" %>

<c:if test="${empty showCashDrawerSummary}">
  <c:set var="showCashDrawerSummary" value="false" />
</c:if>

<c:set var="cashDrawerAttributes" value="${DataDictionary.CashDrawer.attributes}" />
<c:set var="cashDrawer" value="${cashDrawerProperty}" />


  <h3>Cash Drawer Currency/Coin</h3>

<table border="0" cellspacing="0" cellpadding="0" class="datatable" width="100%">
  <tr>
    <th colspan="4" class="tab-subhead">Currency</th>
    <th colspan="3" class="tab-subhead">Coin</th>
  </tr>
        <tr>
          <th style="border-right: 0 none black; width: 2em">&nbsp;</th>
          <th style="border-left: 0 none black">&nbsp;</th>
          <th>Count</th>
          <th>Amount</th>
          <th>&nbsp;</th>
          <th>Count</th>
          <th>Amount</th>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.hundredDollarCount" attributeEntry="${cashDrawerAttributes.hundredDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.hundredDollarCount" attributeEntry="${cashDrawerAttributes.hundredDollarCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentHundredDollarAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentHundredDollarAmount" /></span></td>
          <td>
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.hundredCentCount" attributeEntry="${cashDrawerAttributes.hundredCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.hundredCentCount" attributeEntry="${cashDrawerAttributes.hundredCentCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentHundredCentAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentHundredCentAmount" /></span></td>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.fiftyDollarCount" attributeEntry="${cashDrawerAttributes.fiftyDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.fiftyDollarCount" attributeEntry="${cashDrawerAttributes.fiftyDollarCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentFiftyDollarAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentFiftyDollarAmount" /></span></td>
          <td>
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.fiftyCentCount" attributeEntry="${cashDrawerAttributes.fiftyCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.fiftyCentCount" attributeEntry="${cashDrawerAttributes.fiftyCentCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentFiftyCentAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentFiftyCentAmount" /></span></td>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.twentyDollarCount" attributeEntry="${cashDrawerAttributes.twentyDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.twentyDollarCount" attributeEntry="${cashDrawerAttributes.twentyDollarCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentTwentyDollarAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentTwentyDollarAmount" /></span></td>
          <td>
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.twentyFiveCentCount" attributeEntry="${cashDrawerAttributes.twentyFiveCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.twentyFiveCentCount" attributeEntry="${cashDrawerAttributes.twentyFiveCentCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentTwentyFiveCentAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentTwentyFiveCentAmount" /></span></td>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.tenDollarCount" attributeEntry="${cashDrawerAttributes.tenDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.tenDollarCount" attributeEntry="${cashDrawerAttributes.tenDollarCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentTenDollarAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentTenDollarAmount" /></span></td>
          <td>
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.tenCentCount" attributeEntry="${cashDrawerAttributes.tenCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.tenCentCount" attributeEntry="${cashDrawerAttributes.tenCentCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentTenCentAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentTenCentAmount" /></span></td>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.fiveDollarCount" attributeEntry="${cashDrawerAttributes.fiveDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.fiveDollarCount" attributeEntry="${cashDrawerAttributes.fiveDollarCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentFiveDollarAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentFiveDollarAmount" /></span></td>
          <td>
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.fiveCentCount" attributeEntry="${cashDrawerAttributes.fiveCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.fiveCentCount" attributeEntry="${cashDrawerAttributes.fiveCentCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentFiveCentAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentFiveCentAmount" /></span></td>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.twoDollarCount" attributeEntry="${cashDrawerAttributes.twoDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.twoDollarCount" attributeEntry="${cashDrawerAttributes.twoDollarCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentTwoDollarAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentTwoDollarAmount" /></span></td>
           <td>
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.oneCentCount" attributeEntry="${cashDrawerAttributes.oneCentCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.oneCentCount" attributeEntry="${cashDrawerAttributes.oneCentCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentOneCentAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentOneCentAmount" /></span></td>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.oneDollarCount" attributeEntry="${cashDrawerAttributes.oneDollarCount}" />
          </td>
          <td>
            <kul:htmlControlAttribute property="${cashDrawerProperty}.oneDollarCount" attributeEntry="${cashDrawerAttributes.oneDollarCount}" readOnly="${readOnly}" />
          </td>
          <td><span id="${cashDrawerProperty}.financialDocumentOneDollarAmount.span">$<bean:write name="KualiForm" property="${cashDrawerProperty}.financialDocumentOneDollarAmount" /></span></td>
          <td>
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.financialDocumentOtherCentAmount" attributeEntry="${cashDrawerAttributes.financialDocumentOtherCentAmount}" />
          </td>
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${cashDrawerProperty}.financialDocumentOtherCentAmount" attributeEntry="${cashDrawerAttributes.financialDocumentOtherCentAmount}" readOnly="${readOnly}" />
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <kul:htmlAttributeLabel labelFor="${cashDrawerProperty}.financialDocumentOtherDollarAmount" attributeEntry="${cashDrawerAttributes.financialDocumentOtherDollarAmount}" />
          </td>
          <td>&nbsp;</td>
          <td>
            $<kul:htmlControlAttribute property="${cashDrawerProperty}.financialDocumentOtherDollarAmount" attributeEntry="${cashDrawerAttributes.financialDocumentOtherDollarAmount}" readOnly="${readOnly}" />
          </td>
          <td colspan="3">&nbsp;</td>
        </tr>
        
        <%-- cash drawer summary information --%>
        <c:if test="${showCashDrawerSummary}">
        <c:choose>
          <c:when test="${KualiForm.cashDrawerSummary.depositsFinal}">
            <tr>
              <td colspan="7" class="tab-subhead">Cash Management Closing Drawer</td>
            </tr>

		    				<tr>
                <td rowspan="4">&nbsp;</td>
			  				<td colspan="4" class="infoline">Currency</td>
			  				<td style="text-align: right" colspan="2"></td>
		    				</tr>

		    				<tr>
			  				<td colspan="4" class="infoline">Coin</td>
			  				<td style="text-align: right" colspan="2"></td>
		    				</tr>

		    				<tr>
			  				<td colspan="4" class="infoline">Items in Process</td>
			  				<td style="text-align: right" colspan="2"></td>
		    				</tr>

		    				<tr>
			  				<td colspan="4" class="infoline"><strong>Cash Drawer Total</strong></td>
			  				<td style="text-align: right" colspan="2"></td>
		    				</tr>
          </c:when>
          <c:otherwise>
            <tr>
              <td colspan="7" class="tab-subhead">Cash Drawer Totals</td>
            </tr>

		    				<tr>
			  				<td rowspan="5">&nbsp;</td>
			  				<td colspan="4" class="infoline">Miscellaneous Checks</td>
			  				<td style="text-align: right" colspan="2">$
			  				<bean:write name="KualiForm" property="cashDrawerSummary.undepositedCashieringChecksTotal"/></td>
		    				</tr>

		    				<tr>
			  				<td colspan="4" class="infoline">Currency</td>
			  				<td style="text-align: right" colspan="2">$
			  				<bean:write name="KualiForm" property="cashDrawerSummary.cashDrawerCurrencyTotal"/></td>
		    				</tr>

		    				<tr>
			  				<td colspan="4" class="infoline">Coin</td>
			  				<td style="text-align: right" colspan="2">$
			  				<bean:write name="KualiForm" property="cashDrawerSummary.cashDrawerCoinTotal"/></td>
		    				</tr>

		    				<tr>
			  				<td colspan="4" class="infoline">Items in Process</td>
			  				<td style="text-align: right" colspan="2">$
			  				<bean:write name="KualiForm" property="cashDrawerSummary.openItemsTotal"/></td>
		    				</tr>

		    				<tr>
			  				<td colspan="4" class="infoline"><strong>Cash Drawer Total</strong></td>
			  				<td style="text-align: right" colspan="2">$
			  				<bean:write name="KualiForm" property="cashDrawerSummary.cashDrawerTotal"/></td>
		    				</tr>
          </c:otherwise>
        </c:choose>
        </c:if>
      </table>
