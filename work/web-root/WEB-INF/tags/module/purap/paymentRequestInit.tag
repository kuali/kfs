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
