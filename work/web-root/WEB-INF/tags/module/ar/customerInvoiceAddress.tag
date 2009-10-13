<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<%@ attribute name="subTitle" required="true" description="Sub Title for Address Sub Section"%>
<%@ attribute name="lookupFieldConversion" required="true" description="Lookup field conversion for Customer Address Lookup"%>
<%@ attribute name="refreshAction" required="true" description="Action for refreshing address information"%>		
<%@ attribute name="customerAddressObject" required="true" description="Property reference for actual address"%>
<%@ attribute name="customerAddressIdentifierAttributeEntry" required="true" description="Attribute entry for address identifer" type="java.util.Map"%>
<%@ attribute name="customerAddressIdentifierProperty" required="true" description="Property for address identifer" %>
<%@ attribute name="readOnly" required="true" description="used to decide editability of overview fields" %>

<c:set var="customerAddressAttributes" value="${DataDictionary.CustomerAddress.attributes}" />

<tr>
	<td colspan="4" class="subhead">${subTitle}</td>
</tr>
<tr>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressIdentifierAttributeEntry}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressIdentifierAttributeEntry}"
			property="${customerAddressIdentifierProperty}"
			readOnly="${readOnly}" />
		<c:if test="${not readOnly}">
			    &nbsp;
			    <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.CustomerAddress"
				fieldConversions="${lookupFieldConversion}"
				lookupParameters="document.accountsReceivableDocumentHeader.customerNumber:customerNumber" />
				&nbsp;
				<html:image property="methodToCall.${refreshAction}"
					src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif"
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
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerCityName}"
			property="${customerAddressObject}.customerCityName"
			readOnly="true" />
	</td>
</tr>

<tr>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressName}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerAddressName}"
			property="${customerAddressObject}.customerAddressName"
			readOnly="true" />
	</td>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerStateCode}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerStateCode}"
			property="${customerAddressObject}.customerStateCode"
			readOnly="true" />
	</td>
</tr>

<tr>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerAddressTypeCode}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerAddressTypeCode}"
			property="${customerAddressObject}.customerAddressTypeCode"
			readOnly="true" />
	</td>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerZipCode}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerZipCode}"
			property="${customerAddressObject}.customerZipCode"
			readOnly="true" />
	</td>
</tr>

<tr>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerLine1StreetAddress}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerLine1StreetAddress}"
			property="${customerAddressObject}.customerLine1StreetAddress"
			readOnly="true" />
	</td>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerEmailAddress}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerEmailAddress}"
			property="${customerAddressObject}.customerEmailAddress"
			readOnly="true" />
	</td>
</tr>

<tr>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		<div align="right">
			<kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerLine2StreetAddress}" />
		</div>
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		<kul:htmlControlAttribute
			attributeEntry="${customerAddressAttributes.customerLine2StreetAddress}"
			property="${customerAddressObject}.customerLine2StreetAddress"
			readOnly="true" />
	</td>
	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
		&nbsp;
	</th>
	<td align=left valign=middle class="datacell" style="width: 25%;">
		&nbsp;
	</td>
</tr>
