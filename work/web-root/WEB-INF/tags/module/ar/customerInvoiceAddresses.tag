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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<c:set var="customerAddressAttributes" value="${DataDictionary.CustomerAddress.attributes}" />              
<c:set var="tabindexOverrideBase" value="200" />

<%@ attribute name="readOnly" required="true" description="used to decide editability of overview fields" %>

<kul:tab tabTitle="Billing/Shipping" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_ADDRESS}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
        
	        <tr>
				<td colspan="4" class="subhead">Bill To Address</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerBillToAddressIdentifier}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.customerBillToAddressIdentifier}"
						property="document.customerBillToAddressIdentifier"
						tabindexOverride="${tabindexOverrideBase}"
						readOnly="${readOnly}" />
					<c:if test="${not readOnly}">
					    &nbsp;
					    <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.CustomerAddress"
							fieldConversions="customerAddressIdentifier:document.customerBillToAddressIdentifier"
							lookupParameters="document.accountsReceivableDocumentHeader.customerNumber:customerNumber" />
							&nbsp;
						<html:image property="methodToCall.refreshBillToAddress"
							src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif"
							tabindex="-1"
							title="Refresh Bill to Address" alt="Refresh Bill To Address"
							styleClass="tinybutton" />
					</c:if>			
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerCityName}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingCityName.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingCityName}"
						property="document.billingCityName"
						tabindexOverride="${tabindexOverrideBase} + 5"
						readOnly="true" />
					</div>
				</td>
			</tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressTypeCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingAddressTypeCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingAddressTypeCode}"
						property="document.billingAddressTypeCode"
						tabindexOverride="${tabindexOverrideBase} + 10"
						readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerStateCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingStateCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingStateCode}"
						property="document.billingStateCode"
						tabindexOverride="${tabindexOverrideBase} + 15"
						readOnly="true" />
					</div>
				</td>
			</tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressName}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingAddressName.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingAddressName}"
						property="document.billingAddressName"
						tabindexOverride="${tabindexOverrideBase} + 20"
						readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerZipCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingZipCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingZipCode}"
						property="document.billingZipCode"
						tabindexOverride="${tabindexOverrideBase} + 25"
						readOnly="true" />
					</div>
				</td>
			</tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerLine1StreetAddress}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingLine1StreetAddress.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingLine1StreetAddress}"
						property="document.billingLine1StreetAddress"
						tabindexOverride="${tabindexOverrideBase} + 30"
						readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressInternationalProvinceName}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingAddressInternationalProvinceName.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingAddressInternationalProvinceName}"
						property="document.billingAddressInternationalProvinceName"
						tabindexOverride="${tabindexOverrideBase} + 35"
						readOnly="true" />
					</div>
				</td>
			</tr>

			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerLine2StreetAddress}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingLine2StreetAddress.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingLine2StreetAddress}"
						property="document.billingLine2StreetAddress"
						tabindexOverride="${tabindexOverrideBase} + 40"
						readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerInternationalMailCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingInternationalMailCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingInternationalMailCode}"
						property="document.billingInternationalMailCode"
						tabindexOverride="${tabindexOverrideBase} + 45"
						readOnly="true" />
					</div>
				</td>
			</tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerEmailAddress}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingEmailAddress.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingEmailAddress}"
						property="document.billingEmailAddress"
						tabindexOverride="${tabindexOverrideBase} + 50"
						readOnly="true" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerCountryCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingCountryCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.billingCountryCode}"
						property="document.billingCountryCode"
						tabindexOverride="${tabindexOverrideBase} + 55"
						readOnly="true" />
					</div>
				</td>
			</tr>
			
			
	        <tr>
				<td colspan="4" class="subhead">Ship To Address</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerShipToAddressIdentifier}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.customerShipToAddressIdentifier}"
						property="document.customerShipToAddressIdentifier"
						tabindexOverride="${tabindexOverrideBase} + 60"
						readOnly="${readOnly}" />
					<c:if test="${not readOnly}">
					    &nbsp;
					    <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.CustomerAddress"
							fieldConversions="customerAddressIdentifier:document.customerShipToAddressIdentifier"
							lookupParameters="document.accountsReceivableDocumentHeader.customerNumber:customerNumber" />
							&nbsp;
						<html:image property="methodToCall.refreshShipToAddress"
							src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif"
							title="Refresh Bill to Address" alt="Refresh Bill To Address"
							tabindex="-1"
							styleClass="tinybutton" />
					</c:if>			
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerCityName}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingCityName.div">
					<kul:htmlControlAttribute
						attributeEntry="${customerAddressAttributes.customerCityName}"
						property="document.shippingCityName"
						tabindexOverride="${tabindexOverrideBase} + 65"
						readOnly="true" />
                     </div>
				</td>
			</tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressTypeCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingAddressTypeCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${customerAddressAttributes.customerAddressTypeCode}"
						property="document.shippingAddressTypeCode"
						tabindexOverride="${tabindexOverrideBase} + 70"
						readOnly="true" />
                     </div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerStateCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingStateCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${customerAddressAttributes.customerStateCode}"
						property="document.shippingStateCode"
						tabindexOverride="${tabindexOverrideBase} + 75"
						readOnly="true" />
                     </div>   
				</td>
			</tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressName}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingAddressName.div">
					<kul:htmlControlAttribute
						attributeEntry="${customerAddressAttributes.customerAddressName}"
						property="document.shippingAddressName"
						tabindexOverride="${tabindexOverrideBase} + 80"
						readOnly="true" />
                     </div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerZipCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingZipCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${customerAddressAttributes.customerZipCode}"
						property="document.shippingZipCode"
						tabindexOverride="${tabindexOverrideBase} + 85"
						readOnly="true" />
                     </div>
				</td>
			</tr>

			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerLine1StreetAddress}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingLine1StreetAddress.div">
					<kul:htmlControlAttribute
						attributeEntry="${customerAddressAttributes.customerLine1StreetAddress}"
						property="document.shippingLine1StreetAddress"
						tabindexOverride="${tabindexOverrideBase} + 90"
						readOnly="true" />
                     </div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressInternationalProvinceName}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingAddressInternationalProvinceName.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.shippingAddressInternationalProvinceName}"
						property="document.shippingAddressInternationalProvinceName"
						tabindexOverride="${tabindexOverrideBase} + 95"
						readOnly="true" />
                     </div>
				</td>
			</tr>

			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerLine2StreetAddress}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingLine2StreetAddress.div">
					<kul:htmlControlAttribute
						attributeEntry="${customerAddressAttributes.customerLine2StreetAddress}"
						property="document.shippingLine2StreetAddress"
						tabindexOverride="${tabindexOverrideBase} + 100"
						readOnly="true" />
                     </div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerInternationalMailCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingInternationalMailCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.shippingInternationalMailCode}"
						property="document.shippingInternationalMailCode"
						tabindexOverride="${tabindexOverrideBase} + 105"
						readOnly="true" />
                     </div>
				</td>
			</tr>

			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerEmailAddress}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingEmailAddress.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.shippingEmailAddress}"
						property="document.shippingEmailAddress"
						tabindexOverride="${tabindexOverrideBase} + 110"
						readOnly="true" />
                     </div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerCountryCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
                    <div id="document.shippingCountryCode.div">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.shippingCountryCode}"
						property="document.shippingCountryCode"
						tabindexOverride="${tabindexOverrideBase} + 115"
						readOnly="true" />
                     </div>
				</td>
			</tr>
        </table>
    </div>
</kul:tab>
