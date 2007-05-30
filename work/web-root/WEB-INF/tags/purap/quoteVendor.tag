<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this doc's fields." %>
<%@ attribute name="vendorQuoteAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="ctr" required="true" description="vendor count"%>

        <tr>
			<td colspan="5" class="subhead">
				<span class="subhead-left">Vendor ${ctr + 1}</span>
			</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorName}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorName}" property="document.purchaseOrderVendorQuote[${ctr}].vendorName" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorHeaderGeneratedIdentifier}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorHeaderGeneratedIdentifier}" property="document.purchaseOrderVendorQuote[${ctr}].vendorHeaderGeneratedIdentifier" />
            </td>
            <td rowspan="9"><html:image
	property="methodToCall.deleteVendor.line${ctr}"
	src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
	alt="delete vendor" title="delete vendor"
	styleClass="tinybutton" />
			</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" property="document.purchaseOrderVendorQuote[${ctr}].vendorLine1Address" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" property="document.purchaseOrderVendorQuote[${ctr}].vendorPhoneNumber" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" property="document.purchaseOrderVendorQuote[${ctr}].vendorLine2Address" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" property="document.purchaseOrderVendorQuote[${ctr}].vendorFaxNumber" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorCityName}" />
                &nbsp;/&nbsp;
                <kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorStateCode}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorCityName}" property="document.purchaseOrderVendorQuote[${ctr}].vendorCityName" />
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorStateCode}" property="document.purchaseOrderVendorQuote[${ctr}].vendorStateCode" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" property="document.purchaseOrderVendorQuote[${ctr}].vendorPostalCode" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitTypeCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitTypeCode}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteTransmitTypeCode" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" property="document.purchaseOrderVendorQuote[${ctr}].vendorAttentionName" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitDate}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteTransmitDate}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteTransmitDate" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuotePriceExpirationDate}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuotePriceExpirationDate}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuotePriceExpirationDate" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteStatusCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteStatusCode}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteStatusCode" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteRankNumber}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteRankNumber}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteRankNumber" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                Award:
            </th>
            <td align=left valign=middle class="datacell">
				&nbsp;
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteAwardDate}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteAwardDate}" property="document.purchaseOrderVendorQuote[${ctr}].purchaseOrderQuoteAwardDate" />
            </td>
        </tr>
