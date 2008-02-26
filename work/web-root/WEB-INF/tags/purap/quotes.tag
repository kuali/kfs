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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this doc's fields." %>
<%@ attribute name="vendorQuoteAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="isPurchaseOrderAwarded" required="true" description="has the PO been awarded?" %>

<kul:tab tabTitle="Quote" defaultOpen="false"
	tabErrorKey="${PurapConstants.QUOTE_TAB_ERRORS}">
	<div class="tab-container" align=center><!--  if (fullEntryMode or amendmentEntry), then display the addLine -->
	<table cellpadding="0" cellspacing="0" class="datatable"
		summary="Items Section">
		
		<c:set var="quoteOpen" value="false" />

		<!-- if status is OPEN or QUOT or vendor list is not empty -->
		<c:if test="${KualiForm.document.statusCode eq 'QUOT' || KualiForm.document.statusCode eq 'OPEN' || isPurchaseOrderAwarded}">
		<c:set var="quoteOpen" value="true" />
		<tr>
			<td colspan="5" class="subhead">
				<span class="subhead-left">General Information</span>
				<span class="subhead-right">
					<html:image
	property="methodToCall.printPoQuoteList"
	src="${ConfigProperties.externalizable.images.url}tinybutton-prntquolist.gif"
	alt="print quote list" title="print quote list"
	styleClass="tinybutton" />
				</span>
			</td>
		</tr>
        <tr>
             <th align=right valign=middle class="bord-l-b">
                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderQuoteDueDate}" /></div>
             </th>
             <td align=left valign=middle class="datacell">
                 <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderQuoteDueDate}" property="document.purchaseOrderQuoteDueDate" 
                 readOnly="${isPurchaseOrderAwarded or not preRouteChangeMode}" />
             </td>
             <th align=right valign=middle class="bord-l-b" rowspan="2">
                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderQuoteVendorNoteText}" /></div>
             </th>
             <td align=left valign=middle class="datacell" rowspan="2" colspan="2">
                 <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderQuoteVendorNoteText}" property="document.purchaseOrderQuoteVendorNoteText" 
                 readOnly="${isPurchaseOrderAwarded or not preRouteChangeMode}" />
             </td>
        </tr>
        <tr>
             <th align=right valign=middle class="bord-l-b">
                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderQuoteTypeCode}" /></div>
             </th>
             <td align=left valign=middle class="datacell">
                 <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderQuoteTypeCode}" property="document.purchaseOrderQuoteTypeCode" 
                 readOnly="${isPurchaseOrderAwarded or not preRouteChangeMode}" />
             </td>
        </tr>

		<tr>
			<td colspan="5" class="subhead">
				<span class="subhead-left">Vendor Information</span>
				<c:if test="${!isPurchaseOrderAwarded && preRouteChangeMode}">
					<span class="subhead-right">
						<input type="image" name="methodToCall.performLookup.(!!org.kuali.module.purap.bo.PurchaseOrderQuoteList!!).(((purchaseOrderQuoteListIdentifier:document.purchaseOrderQuoteListIdentifier)))"
						   src="${ConfigProperties.externalizable.images.url}tinybutton-selquolist.gif" border="0" class="tinybutton" valign="middle" alt="Search for a Quote List" title="Search for a Quote List" />
					</span>
				</c:if>
			</td>
		</tr>

		<c:if test="${!isPurchaseOrderAwarded && preRouteChangeMode}">
        <tr>
			<td colspan="5" class="subhead">
				<span class="subhead-left">New Vendor</span>
			</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorName}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorName}" property="newPurchaseOrderVendorQuote.vendorName" />
                    <kul:lookup  boClassName="org.kuali.module.vendor.bo.VendorDetail" 
                    lookupParameters="'Y':activeIndicator, 'PO':vendorHeader.vendorTypeCode"
                    fieldConversions="vendorName:newPurchaseOrderVendorQuote.vendorName,vendorHeaderGeneratedIdentifier:newPurchaseOrderVendorQuote.vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:newPurchaseOrderVendorQuote.vendorDetailAssignedIdentifier,defaultAddressLine1:newPurchaseOrderVendorQuote.vendorLine1Address,defaultAddressLine2:newPurchaseOrderVendorQuote.vendorLine2Address,defaultAddressCity:newPurchaseOrderVendorQuote.vendorCityName,defaultAddressPostalCode:newPurchaseOrderVendorQuote.vendorPostalCode,defaultAddressStateCode:newPurchaseOrderVendorQuote.vendorStateCode"/>
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
            	<c:out value="${newPurchaseOrderVendorQuote.vendorNumber}" />
		        <html:hidden property="newPurchaseOrderVendorQuote.vendorHeaderGeneratedIdentifier" />
		        <html:hidden property="newPurchaseOrderVendorQuote.vendorDetailAssignedIdentifier" />
            </td>
            <td rowspan="8"><html:image
	property="methodToCall.addVendor"
	src="${ConfigProperties.externalizable.images.url}tinybutton-addvendor.gif"
	alt="add vendor" title="add vendor"
	styleClass="tinybutton" />
			</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" property="newPurchaseOrderVendorQuote.vendorLine1Address" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" property="newPurchaseOrderVendorQuote.vendorPhoneNumber" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" property="newPurchaseOrderVendorQuote.vendorLine2Address" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" property="newPurchaseOrderVendorQuote.vendorFaxNumber" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorCityName}" />
                &nbsp;/&nbsp;
                <kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorStateCode}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorCityName}" property="newPurchaseOrderVendorQuote.vendorCityName" />
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorStateCode}" property="newPurchaseOrderVendorQuote.vendorStateCode" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" property="newPurchaseOrderVendorQuote.vendorPostalCode" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorCountryCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorCountryCode}" property="newPurchaseOrderVendorQuote.vendorCountryCode" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" property="newPurchaseOrderVendorQuote.vendorAttentionName" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuotePriceExpirationDate}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuotePriceExpirationDate}" property="newPurchaseOrderVendorQuote.purchaseOrderQuotePriceExpirationDate" />
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteStatusCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteStatusCode}" property="newPurchaseOrderVendorQuote.purchaseOrderQuoteStatusCode" />
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteRankNumber}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${vendorQuoteAttributes.purchaseOrderQuoteRankNumber}" property="newPurchaseOrderVendorQuote.purchaseOrderQuoteRankNumber" />
            </td>
        </tr>
		</c:if>

		<logic:iterate indexId="ctr" name="KualiForm" property="document.purchaseOrderVendorQuotes" id="quoteLine">
		    <purap:quoteVendor
		        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}"
		        vendorQuoteAttributes="${DataDictionary.PurchaseOrderVendorQuote.attributes}"
		        isSysVendor="${not empty quoteLine.vendorHeaderGeneratedIdentifier}"
		        isPurchaseOrderAwarded="${isPurchaseOrderAwarded}"
				isAwarded="${not empty quoteLine.purchaseOrderQuoteAwardDate}"
				isTransmitPrintDisplayed="${quoteLine.transmitPrintDisplayed}"
				isTrasnmitted="${not empty quoteLine.purchaseOrderQuoteTransmitDate}"
		        ctr="${ctr}" /> 
		</logic:iterate>

		<c:if test="${!isPurchaseOrderAwarded && preRouteChangeMode}">
		<tr>
			<td colspan="5">
				<div align="center">
					<html:image
	property="methodToCall.completeQuote"
	src="${ConfigProperties.externalizable.images.url}tinybutton-completequote.gif"
	alt="complete quote" title="complete quote"
	styleClass="tinybutton" />
					<html:image
	property="methodToCall.cancelQuote"
	src="${ConfigProperties.externalizable.images.url}tinybutton-cancelquote.gif"
	alt="cancel quote" title="cancel quote"
	styleClass="tinybutton" />
				</div>
			</td>
		</tr>
		</c:if>

		</c:if>

		<c:if test="${not quoteOpen}">
		<tr>
			<td colspan="5" class="subhead">
				<span class="subhead-right">
					<html:image
	property="methodToCall.initiateQuote"
	src="${ConfigProperties.externalizable.images.url}tinybutton-initiatequote.gif"
	alt="initiate quote" title="initiate quote"
	styleClass="tinybutton" />
				</span>
			</td>
		</tr>
		</c:if>

	</table>

	</div>
</kul:tab>
