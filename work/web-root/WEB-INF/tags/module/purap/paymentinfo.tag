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

<%@ attribute name="displayPurchaseOrderFields" required="false"
    description="Boolean to indicate if PO specific fields should be displayed" %>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
    description="The DataDictionary entry containing attributes for this row's fields." %>             

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="amendmentEntry" value="${(!empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="lockB2BEntry" value="${(not empty KualiForm.editingMode['lockB2BEntry'])}" />
<c:set var="tabindexOverrideBase" value="70" />

<kul:tab tabTitle="Payment Info" defaultOpen="false" tabErrorKey="${PurapConstants.PAYMENT_INFO_TAB_ERRORS}">
    <div class="tab-container" align=center>

    <c:if test="${!lockB2BEntry}" >
            <h3>Payment Info</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Payment Info Section">
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.recurringPaymentTypeCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.recurringPaymentTypeCode}" property="document.recurringPaymentTypeCode"
                   		extraReadOnlyProperty="document.recurringPaymentType.recurringPaymentTypeDescription" 
                   		readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.purchaseOrderBeginDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell"> from:
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.purchaseOrderBeginDate}" property="document.purchaseOrderBeginDate" datePicker="true" 
                   		readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                 	&nbsp;&nbsp;
                  to:
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderEndDate}" property="document.purchaseOrderEndDate" datePicker="true" 
                    	readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
               </td> 
            </tr>
            <c:if test="${displayPurchaseOrderFields}">
                <tr>
                    <th align=left valign=middle colspan="2" class="bord-l-b"> Please provide the following recurring payment information if the type of recurring payment is Fixed Schedule, Fixed Amount</th>
                </tr> 
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                       <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.recurringPaymentAmount}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell"> Amount:
                       <kul:htmlControlAttribute 
                       		attributeEntry="${documentAttributes.recurringPaymentAmount}" property="document.recurringPaymentAmount" 
                       		readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                        &nbsp;&nbsp;
                        First Payment Date:
                        <kul:htmlControlAttribute 
                        	attributeEntry="${documentAttributes.recurringPaymentDate}" property="document.recurringPaymentDate" datePicker="true" 
                        	readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                        &nbsp;&nbsp;
                        Frequency:
                       <kul:htmlControlAttribute 
                       		attributeEntry="${documentAttributes.recurringPaymentFrequencyCode}" property="document.recurringPaymentFrequencyCode" 
                       		readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                    </td> 
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                       <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.initialPaymentAmount}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell"> Amount:
                       <kul:htmlControlAttribute 
                       		attributeEntry="${documentAttributes.initialPaymentAmount}" property="document.initialPaymentAmount" 
                       		readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                        &nbsp;&nbsp;
                      Date:
                        <kul:htmlControlAttribute 
                        	attributeEntry="${documentAttributes.initialPaymentDate}" property="document.initialPaymentDate" datePicker="true" 
                        	readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                   </td> 
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                       <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.finalPaymentAmount}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell"> Amount:
                       <kul:htmlControlAttribute 
                       		attributeEntry="${documentAttributes.finalPaymentAmount}" property="document.finalPaymentAmount" 
                       		readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                        &nbsp;&nbsp;
                      Date:
                       <kul:htmlControlAttribute 
                       		attributeEntry="${documentAttributes.finalPaymentDate}" property="document.finalPaymentDate" datePicker="true" 
                       		readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                   </td> 
                </tr>
            </c:if>
		</table> 
    </c:if>
		
		
           	<h3>Billing Address</h3>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Payment Info Section">	 
			<tr>
                <th align=right valign=top  class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billingName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingName}" property="document.billingName" readOnly="true" /><br>
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingLine1Address}" property="document.billingLine1Address" readOnly="true" /><br>
	        		<c:if test="${!empty documentAttributes.billingLine2Address}">
	                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.billingLine2Address}" property="document.billingLine2Address" readOnly="true" /><br>
	        		</c:if>
               		<kul:htmlControlAttribute attributeEntry="${documentAttributes.billingCityName}" property="document.billingCityName" readOnly="true" />,&nbsp;
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingStateCode}" property="document.billingStateCode" readOnly="true" />&nbsp;&nbsp;
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingPostalCode}" property="document.billingPostalCode" readOnly="true" /><br>
             		<c:if test="${!empty documentAttributes.billingCountryCode}">       
               			<kul:htmlControlAttribute attributeEntry="${documentAttributes.billingCountryCode}" 
               				property="document.billingCountryCode" 
               				extraReadOnlyProperty="document.billingCountryName"                				
               				readOnly="true" />
            		</c:if>
            	</td>
            </tr>
        </table>

    </div>
</kul:tab>
