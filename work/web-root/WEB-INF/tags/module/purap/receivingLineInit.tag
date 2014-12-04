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
              
<kul:tabTop tabTitle="Line Item Receiving Initiation" defaultOpen="true" tabErrorKey="${PurapConstants.RECEIVING_LINE_INIT_TAB_ERRORS}">
	
    <div class="tab-container" align=center>
            <h3>Line Item Receiving Initiation</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Receiving Line Init Section">

            <tr>
                <th align=right valign=middle class="bord-l-b" style="width: 50%;">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier" readOnly="${KualiForm.fromPurchaseOrder}" />
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReceivedDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReceivedDate}" property="document.shipmentReceivedDate" datePicker="true" />
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" property="document.shipmentPackingSlipNumber" />
                </td>
            </tr>                
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" property="document.shipmentBillOfLadingNumber" />
                </td>
            </tr>                
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.carrierCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.carrierCode}" property="document.carrierCode" />
                </td>
            </tr>
		</table> 
    </div>

</kul:tabTop>
