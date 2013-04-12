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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this doc's fields." %>
<%@ attribute name="vendorQuoteAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="isPurchaseOrderAwarded" required="true" description="has the PO been awarded?" %>

<c:set var="tabindexOverrideBase" value="20" />

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="poInProcess" value="${KualiForm.document.applicationDocumentStatus eq 'In Process'}" />
<c:set var="poOpen" value="${KualiForm.document.applicationDocumentStatus eq 'Open'}" />
<c:set var="poOutForQuote" value="${KualiForm.document.applicationDocumentStatus eq 'Out for Quote'}" />

<c:set var="quoteOpen" value="${poOutForQuote || poOpen || isPurchaseOrderAwarded}" />
<c:set var="quoteEditable" value="${poOutForQuote && !isPurchaseOrderAwarded && fullEntryMode}" />
<c:set var="quoteInitable" value="${poInProcess && fullEntryMode && !isPurchaseOrderAwarded}" />
<c:set var="tabEdited" value="${(not empty KualiForm.editingMode['quoteTabEdited'])}" />

<kul:tab tabTitle="Quote" defaultOpen="false" highlightTab="${tabEdited}"  tabErrorKey="${PurapConstants.QUOTE_TAB_ERRORS}">
	<div class="tab-container" align=center>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Quotes Section">			
	 	
		<!--  if quote tab is open, then display the contents -->
	  	<c:if test="${quoteOpen}">
		
		<tr>
			<td colspan="4" class="subhead">
				<span class="subhead-left">General Information</span>
				<c:if test="${fullEntryMode}">
					<span class="subhead-right">
						<html:image property="methodToCall.printPoQuoteList"
								src="${ConfigProperties.externalizable.images.url}tinybutton-prntquolist.gif"
								alt="print quote list" title="print quote list"
								styleClass="tinybutton" onclick="excludeSubmitRestriction=true"/>
					</span>
				</c:if>
			</td>
		</tr>
		
        <tr>
             <th align=right valign=middle class="bord-l-b">
                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderQuoteInitializationDate}" /></div>
             </th>
             <td align=left valign=middle class="datacell">
                 <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderQuoteInitializationDate}" 
                 	property="document.purchaseOrderQuoteInitializationDate" readOnly="true" />
             </td>
             <th align=right valign=middle class="bord-l-b" rowspan="3">
                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderQuoteVendorNoteText}" /></div>
             </th>
             <td align=left valign=middle class="datacell" rowspan="3">
                 <kul:htmlControlAttribute 
                 	attributeEntry="${documentAttributes.purchaseOrderQuoteVendorNoteText}" property="document.purchaseOrderQuoteVendorNoteText" 
                 	readOnly="${!quoteEditable}" tabindexOverride="${tabindexOverrideBase + 3}"/>
             </td>
        </tr>
        
        <tr>
             <th align=right valign=middle class="bord-l-b">
                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderQuoteDueDate}" /></div>
             </th>
             <td align=left valign=middle class="datacell">
                 <kul:htmlControlAttribute 
                 	attributeEntry="${documentAttributes.purchaseOrderQuoteDueDate}" property="document.purchaseOrderQuoteDueDate" 
                 	readOnly="${!quoteEditable}" tabindexOverride="${tabindexOverrideBase + 0}"/>
             </td>
        </tr>
        
        <tr>
             <th align=right valign=middle class="bord-l-b">
                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderQuoteTypeCode}" /></div>
             </th>
             <td align=left valign=middle class="datacell">
                 <kul:htmlControlAttribute 
                 	attributeEntry="${documentAttributes.purchaseOrderQuoteTypeCode}" 
                 	property="document.purchaseOrderQuoteTypeCode" 
                 	extraReadOnlyProperty="document.purchaseOrderQuoteTypeDescription"
                 	readOnly="${!quoteEditable}" tabindexOverride="${tabindexOverrideBase + 0}"/>
             </td>
        </tr>

		<tr>
			<td colspan="4" class="subhead">
				<span class="subhead-left">Vendor Information</span>
				<!--  if quote tab is editable, then display the quote list lookup -->
				<c:if test="${quoteEditable}">
				<span class="subhead-right">
					<html:image property="methodToCall.performLookup.(!!org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteList!!).(((purchaseOrderQuoteListIdentifier:document.purchaseOrderQuoteListIdentifier)))" 
								src="${ConfigProperties.externalizable.images.url}tinybutton-selquolist.gif" 
								alt="Search for a Quote List" border="0"
								styleClass="tinybutton" align="middle" />
				</span>
				</c:if>
			</td>
		</tr>

		<!--  if quote tab is editable, then display the addLine -->
		<c:if test="${quoteEditable}">
        <tr>
			<td colspan="4" class="subhead">
				<span class="subhead-left">New Vendor</span>
				<span class="subhead-right">
	            	<html:image property="methodToCall.addVendor"
								src="${ConfigProperties.externalizable.images.url}tinybutton-addvendor.gif"
								alt="add vendor" title="add vendor"
								styleClass="tinybutton" align="middle" />
			</td>			
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorName}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorName}" property="newPurchaseOrderVendorQuote.vendorName" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>
                <kul:lookup  boClassName="org.kuali.kfs.vnd.businessobject.VendorDetail" 
                    lookupParameters="'Y':activeIndicator, 'PO':vendorHeader.vendorTypeCode"
                    fieldConversions="vendorName:newPurchaseOrderVendorQuote.vendorName,vendorHeaderGeneratedIdentifier:newPurchaseOrderVendorQuote.vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:newPurchaseOrderVendorQuote.vendorDetailAssignedIdentifier,defaultAddressLine1:newPurchaseOrderVendorQuote.vendorLine1Address,defaultAddressLine2:newPurchaseOrderVendorQuote.vendorLine2Address,defaultAddressCity:newPurchaseOrderVendorQuote.vendorCityName,defaultAddressPostalCode:newPurchaseOrderVendorQuote.vendorPostalCode,defaultAddressStateCode:newPurchaseOrderVendorQuote.vendorStateCode"/>
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
            	<c:out value="${newPurchaseOrderVendorQuote.vendorNumber}" />
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorLine1Address}" property="newPurchaseOrderVendorQuote.vendorLine1Address" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>
                <c:if test="${quoteEditable and (not empty KualiForm.newPurchaseOrderVendorQuote.vendorLine1Address)}">                
				   <kul:lookup  boClassName="org.kuali.kfs.vnd.businessobject.VendorAddress" 
		              readOnlyFields="active, vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes"
		              lookupParameters="'Y':active,newPurchaseOrderVendorQuote.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,newPurchaseOrderVendorQuote.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier"
                      fieldConversions="vendorAddressGeneratedIdentifier:newPurchaseOrderVendorQuote.vendorAddressGeneratedIdentifier,vendorLine1Address:newPurchaseOrderVendorQuote.vendorLine1Address,vendorLine2Address:newPurchaseOrderVendorQuote.vendorLine2Address,vendorCityName:newPurchaseOrderVendorQuote.vendorCityName,vendorStateCode:newPurchaseOrderVendorQuote.vendorStateCode,vendorZipCode:newPurchaseOrderVendorQuote.vendorPostalCode,vendorCountryCode:newPurchaseOrderVendorQuote.vendorCountryCode,vendorFaxNumber:newPurchaseOrderVendorQuote.vendorFaxNumber,vendorAttentionName:newPurchaseOrderVendorQuote.vendorAttentionName"/>
  		    	</c:if>		
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorPhoneNumber}" property="newPurchaseOrderVendorQuote.vendorPhoneNumber" 
                	tabindexOverride="${tabindexOverrideBase + 7}"/>
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorLine2Address}" property="newPurchaseOrderVendorQuote.vendorLine2Address" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorFaxNumber}" property="newPurchaseOrderVendorQuote.vendorFaxNumber" 
                	tabindexOverride="${tabindexOverrideBase + 7}"/>
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorCityName}" />/
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorStateCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorCityName}" property="newPurchaseOrderVendorQuote.vendorCityName" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>/
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorStateCode}" property="newPurchaseOrderVendorQuote.vendorStateCode" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorAttentionName}" property="newPurchaseOrderVendorQuote.vendorAttentionName" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>
            </td>
        </tr>
        <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" />/
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorQuoteAttributes.vendorCountryCode}" />
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorPostalCode}" property="newPurchaseOrderVendorQuote.vendorPostalCode" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>
                <kul:htmlControlAttribute 
                	attributeEntry="${vendorQuoteAttributes.vendorCountryCode}" property="newPurchaseOrderVendorQuote.vendorCountryCode" 
                	tabindexOverride="${tabindexOverrideBase + 5}"/>
            </td>
            <td colspan="2">&nbsp;</td>
        </tr>
		</c:if>

		<c:set var="isAnyQuoteTransmitted" value="false" />
		<logic:iterate indexId="ctr" name="KualiForm" property="document.purchaseOrderVendorQuotes" id="quoteLine">
			<c:set var="isAnyQuoteTransmitted" value="${not empty quoteLine.purchaseOrderQuoteTransmitTimestamp}" />
		    <purap:quoteVendor
		        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}"
		        vendorQuoteAttributes="${DataDictionary.PurchaseOrderVendorQuote.attributes}"
		        isSysVendor="${not empty quoteLine.vendorHeaderGeneratedIdentifier}"
		        isPurchaseOrderAwarded="${isPurchaseOrderAwarded}"
				isAwarded="${not empty quoteLine.purchaseOrderQuoteAwardTimestamp}"
				isTransmitPrintDisplayed="${quoteLine.transmitPrintDisplayed}"
				isTrasnmitted="${not empty quoteLine.purchaseOrderQuoteTransmitTimestamp}"
				isPdfDisplayedToUserOnce="${quoteLine.pdfDisplayedToUserOnce}"
		        ctr="${ctr}" /> 
		</logic:iterate>

		<!--  if quote tab is editable, then display the complete quote button -->
		<c:if test="${quoteEditable}">
		<tr>
			<td colspan="5">
				<div align="center">
					<html:image property="methodToCall.completeQuote"
								src="${ConfigProperties.externalizable.images.url}tinybutton-completequote.gif"
								alt="complete quote" title="complete quote"
								styleClass="tinybutton" />
					<c:if test="${not isAnyQuoteTransmitted}">
					<html:image property="methodToCall.cancelQuote"
								src="${ConfigProperties.externalizable.images.url}tinybutton-cancelquote.gif"
								alt="cancel quote" title="cancel quote"
								styleClass="tinybutton" />
					</c:if>
				</div>
			</td>
		</tr>
		</c:if>

		</c:if>

		<!--  only if PO is in process and editable to the user, display the init quote button -->
		<c:if test="${quoteInitable}">
		<tr>
			<td colspan="5" class="subhead"> 
				<span class="subhead-right">
					<html:image property="methodToCall.initiateQuote"
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
