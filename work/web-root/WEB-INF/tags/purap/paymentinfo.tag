<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>

<kul:tab tabTitle="Payment Info" defaultOpen="true">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Payment Info</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Payment Info Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.recurringPaymentTypeCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.recurringPaymentTypeCode}" property="document.recurringPaymentTypeCode" />
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.purchaseOrderBeginDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell"> from:
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderBeginDate}" property="document.purchaseOrderBeginDate" datePicker="true"/>
                 	&nbsp;&nbsp;
                  to:
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderEndDate}" property="document.purchaseOrderEndDate" datePicker="true"/>
               </td> 
            </tr>
		</table> 
		
		<c:if test="${displayPurchaseOrderFields}">
			 <table cellpadding="0" cellspacing="0" class="datatable" summary="Payment Info Section">
	
	            <tr>
	            	<th align=left valign=middle colspan="2" class="bord-l-b"> Please provide the following recurring payment information if the type of recurring payment is Fixed Schedule, Fixed Amount</th>
	            </tr> 
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.recurringPaymentAmount}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell"> Amount:
	                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.recurringPaymentAmount}" property="document.recurringPaymentAmount"/>
	                 	&nbsp;&nbsp;
	                  	First Payment Date:
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.recurringPaymentDate}" property="document.recurringPaymentDate" datePicker="true"/>
	              		&nbsp;&nbsp;
	              		Frequency:
	                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.recurringPaymentFrequencyCode}" property="document.recurringPaymentFrequencyCode" />
	                </td> 
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.initialPaymentAmount}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell"> Amount:
	                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.initialPaymentAmount}" property="document.initialPaymentAmount" />
	                 	&nbsp;&nbsp;
	                  Date:
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.initialPaymentDate}" property="document.initialPaymentDate" datePicker="true"/>
	               </td> 
	            </tr>
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.finalPaymentAmount}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell"> Amount:
	                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.finalPaymentAmount}" property="document.finalPaymentAmount" />
	                 	&nbsp;&nbsp;
	                  Date:
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.finalPaymentDate}" property="document.finalPaymentDate" datePicker="true"/>
	               </td> 
	            </tr>
			</table> 
		</c:if>
		
		
		<div class="h2-container">
           	<h2>Billing Address</h2>
        </div> 
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Payment Info Section">	 
			<tr>
                <th align=right valign=top  class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billingName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingName}" property="document.billingName" readOnly="true" /><br>
                 
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingLine1Address}" property="document.billingLine1Address" readOnly="true" /><br>
        
	        		<c:if test="${!empty document.billingLine2Address}">
	        	
	                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.billingLine2Address}" property="document.billingLine2Address" readOnly="true" /><br>
	                	
	        		</c:if>
	         
               		<kul:htmlControlAttribute attributeEntry="${documentAttributes.billingCityName}" property="document.billingCityName" readOnly="true" />,&nbsp;
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingStateCode}" property="document.billingStateCode" readOnly="true" />&nbsp;&nbsp;
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingPostalCode}" property="document.billingPostalCode" readOnly="true" /><br>
             		<c:if test="${!empty documentAttributes.billingCountryCode}">       
               			<kul:htmlControlAttribute attributeEntry="${documentAttributes.billingCountryCode}" property="document.billingCountryCode" readOnly="true" />
            		</c:if>
            	</td>
            </tr>
        </table>

    </div>
</kul:tab>
