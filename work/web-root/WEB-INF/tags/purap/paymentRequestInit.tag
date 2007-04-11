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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="displayPaymentRequestInitFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>

<kul:tabTop tabTitle="Payment Request Init" defaultOpen="true" >
	
	

    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Payment Request Init</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Payment Request Init Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier"
                   		readOnly="${not displayInitTab}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceNumber}" property="document.invoiceNumber"  />
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceDate}" property="document.invoiceDate" datePicker="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.vendorInvoiceAmount}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorInvoiceAmount}" property="document.vendorInvoiceAmount"  />
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentSpecialHandlingInstructionLine1Text}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentSpecialHandlingInstructionLine1Text}" property="document.paymentSpecialHandlingInstructionLine1Text" />
                   <br/> 
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentSpecialHandlingInstructionLine2Text}" property="document.paymentSpecialHandlingInstructionLine2Text" />
                   <br/>
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentSpecialHandlingInstructionLine3Text}" property="document.paymentSpecialHandlingInstructionLine3Text" />
                </td>
                <th align=right valign=middle class="bord-l-b" colspan="2">
                   <div align="right">&nbsp;</div>
                </th>
            </tr>
		</table> 
		
		

    </div>

</kul:tabTop>
