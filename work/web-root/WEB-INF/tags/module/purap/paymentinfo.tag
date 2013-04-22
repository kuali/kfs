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

<%@ attribute name="displayPurchaseOrderFields" required="false"
    description="Boolean to indicate if PO specific fields should be displayed" %>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
    description="The DataDictionary entry containing attributes for this row's fields." %>             

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="amendmentEntry" value="${(!empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="lockB2BEntry" value="${(not empty KualiForm.editingMode['lockB2BEntry'])}" />
<c:set var="tabEdited" value="${(not empty KualiForm.editingMode['paymentTabEdited'])}" />
<c:set var="tabindexOverrideBase" value="70" />

<kul:tab tabTitle="Payment Info" defaultOpen="false" highlightTab="${tabEdited}" tabErrorKey="${PurapConstants.PAYMENT_INFO_TAB_ERRORS}">
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
