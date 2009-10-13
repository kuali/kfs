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
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="displayPaymentRequestInitFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>

<kul:tabTop tabTitle="Payment Request Initiation" defaultOpen="true" tabErrorKey="*">
	
	

    <div class="tab-container" align=center>
            <h3>Payment Request Initiation</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Payment Request Initiation Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.invoiceNumber}" property="document.invoiceNumber" />
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.invoiceDate}" property="document.invoiceDate" datePicker="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.vendorInvoiceAmount}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.vendorInvoiceAmount}" property="document.vendorInvoiceAmount" />
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.specialHandlingInstructionLine1Text}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.specialHandlingInstructionLine1Text}" property="document.specialHandlingInstructionLine1Text"  />
                   <br/> 
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.specialHandlingInstructionLine2Text}" property="document.specialHandlingInstructionLine2Text" />
                   <br/>
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.specialHandlingInstructionLine3Text}" property="document.specialHandlingInstructionLine3Text" />
                </td>
                <th align=right valign=middle class="bord-l-b" colspan="2">
                   <div align="right">&nbsp;</div>
                </th>
            </tr>
		</table> 
		
		

    </div>

</kul:tabTop>
