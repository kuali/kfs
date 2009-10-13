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
              
<kul:tabTop tabTitle="Bulk Receiving Initiation" defaultOpen="true" tabErrorKey="${PurapConstants.BULK_RECEIVING_INIT_TAB_ERRORS}">
	
    <div class="tab-container" align=center>
            <h3>Bulk Receiving Initiation</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Receiving Line Init Section">

            <tr>
                <th align=right valign=middle class="bord-l-b" style="width: 50%;">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier" />
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
