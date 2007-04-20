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

<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>
             
                           
<kul:tab tabTitle="Purchase Order Retransmit" defaultOpen="true" >
	
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Purchase Order Retransmit</h2>
        </div>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Purchase Order Retransmit Section">
            <tr>
                <th align=left valign=top class="datacell" colspan=6>
                    <br><p><strong>&nbsp;&nbsp;Please accept or change the Transmission Method, Fax Number and PO Header, and check the line item(s) to be transmitted.</strong><br><br></p>
                </th>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" property="document.purchaseOrderTransmissionMethodCode" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorFaxNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorFaxNumber}" property="document.vendorFaxNumber"  />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right">PO Header:</div>
                </th>
                <td align=left valign=middle class="datacell">
                   Retransmission of Purchase Order
                </td>
            </tr>

		</table> 

        <table width="100%" border=0 align=center cellpadding=0 cellspacing=0 class="bord-r-t" id="itemsTable">
            <tbody>
                <tr>
                    <th align=left valign=top class="datacell" colspan=7>
                        <p><strong><br>Items<br><br></strong></p>
                    </th>
                </tr>
                <tr>
                    <th align=left valign=top class="datacell" colspan=7>
                        <input type="image" name="btnSelectAll" src="images/buttonsmall_selectall.gif" alt="Select all items." hspace=4 align=absmiddle>
		  		        &nbsp;&nbsp;
                        <input type="image" name="btnDeselectAll" src="images/buttonsmall_deselect.gif" alt="Deselect all items." hspace=4 align=absmiddle>
                    </th>
                </tr>
                <tr>
                    <th width=20 align=center><b>Select</b></th>
                    <th width=72 align=center><b>Qty</b></th>
                    <th width=48 align=center><b>UOM</b></th>
                    <th width=112 align=center><b>Catalog Number</b></th>
                    <th align=center><b>Description</b></th>
                    <th width=89 align=center><b>Unit Cost</b></th>
                    <th width=141 align=center><b>Extended Cost</b></th>
                </tr>
                <nested:iterate name="KualiForm" property="purchaseOrderDocument.items" id="item" indexId="itemIndex">
                    <c:if test="${ (item.active) and (not item.retransmitDisplay) }">
                        <tr>
                            <td valign="top" align="left" class="datacell" nowrap="nowrap">
                                <html:multibox property="retransmitItemsSelected" value="${item.itemLineNumber}">
                                    <bean:write name="item"/>
                                </html:multibox>
                            </td>
                            <td valign="top" align="left" class="datacell" nowrap="nowrap">
                                <bean:write name="item" property="itemOrderedQuantity"/>
                            </td>
                            <td valign="top" align="left" class="datacell" nowrap="nowrap">
                                <bean:write name="item" property="itemUnitOfMeasureCode"/>
                            </td>
                            <td valign="top" align="left" class="datacell" nowrap="nowrap">
                                <bean:write name="item" property="itemCatalogNumber"/>
                            </td>
                            <td valign="top" align="left" class="datacell">
                                <bean:write name="item" property="itemDescription"/>
                            </td>
                            <td valign="top" align="left" class="datacell" nowrap="nowrap">
                                <bean:write name="item" property="itemUnitPrice"/>
                            </td>
                            <td valign="top" align="left" class="datacell" nowrap="nowrap">
                                <bean:write name="item" property="extendedPrice"/>
                            </td>
                        </tr>
                    </c:if>
                </nested:iterate>
            </tbody>
        </table>

    </div>

</kul:tab>
