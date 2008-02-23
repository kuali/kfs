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
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="vendorReadOnly" value="${(not empty KualiForm.editingMode['lockVendorEntry'])}" />
<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="editPreExtract"	value="${(not empty KualiForm.editingMode['editPreExtract'])}" />
<c:set var="currentUserCampusCode" value="${UserSession.universalUser.campusCode}" />
<c:set var="extraPrefix" value="${displayPurchaseOrderFields or displayPaymentRequestFields ? 'document' : 'document.vendorDetail'}" /> 

<c:set var="extraPrefixShippingTitle" value="document.vendorDetail" />

<kul:tab tabTitle="Vendor" defaultOpen="true" tabErrorKey="${PurapConstants.VENDOR_ERRORS}">
    <div class="tab-container" align=center>
        <html:hidden property="document.vendorHeaderGeneratedIdentifier" />
        <html:hidden property="document.vendorDetailAssignedIdentifier" />
		
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
            <tr>
                <td colspan="4" class="subhead">Vendor Address</td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b" width="25%">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="${not (fullEntryMode or amendmentEntry) or vendorReadOnly}" />
                    <c:if test="${(fullEntryMode or amendmentEntry)}" >
                        <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorDetail" 
                        lookupParameters="'Y':activeIndicator, 'PO':vendorHeader.vendorTypeCode"
                        fieldConversions="vendorHeaderGeneratedIdentifier:document.vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:document.vendorDetailAssignedIdentifier,defaultAddressLine1:document.vendorLine1Address,defaultAddressLine2:document.vendorLine2Address,defaultAddressCity:document.vendorCityName,defaultAddressPostalCode:document.vendorPostalCode,defaultAddressStateCode:document.vendorStateCode,defaultAddressInternationalProvince:document.vendorAddressInternationalProvinceName,defaultAddressCountryCode:document.vendorCountryCode"/>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b" width="25%">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCityName}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorDetail.vendorNumber" readOnly="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorStateCode}" /><br> *required for US</div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine1Address}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.vendorLine1Address" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    <c:if test="${(fullEntryMode or amendmentEntry) and vendorReadOnly}">
                        <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorAddress" 
                        readOnlyFields="active, vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes"
                        lookupParameters="'Y': active,document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" 
                        fieldConversions="vendorAddressGeneratedIdentifier:document.vendorAddressGeneratedIdentifier"/>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
					<kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" />
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" property="document.vendorAddressInternationalProvinceName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine2Address}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPostalCode}" /><br> *required for US</div>
                </th>
				<td align=left valign=middle class="datacell">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" readOnly="${not (fullEntryMode or amendmentEntry)}" />
				</td>
            </tr>
            
            <tr>
            	<th align=right valign=middle class="bord-l-b">
            	</th>
            	<td align=left valign=middle class="datacell">
            	</td>
            	<th align=right valign=middle class="bord-l-b">
            		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCountryCode}" /></div>
            	</th>
            	<td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode"
                    extraReadOnlyProperty="document.vendorCountry.postalCountryName" 
                    readOnly="${not (fullEntryMode or amendmentEntry)}" />
            	</td>
            </tr>

	</table>
    </div>
</kul:tab>