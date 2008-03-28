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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for item's fields." %>
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
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderRetransmissionMethodCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderRetransmissionMethodCode}" property="document.purchaseOrderRetransmissionMethodCode" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorFaxNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorFaxNumber}" property="document.vendorFaxNumber"  />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.retransmitHeader}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.retransmitHeader}" property="document.retransmitHeader" />
                </td>
            </tr>

		</table> 

        <table width="100%" border=0 align=center cellpadding=0 cellspacing=0 class="bord-r-t" id="itemsTable">
            <tbody>
                <tr>
                    <th align=left valign=top class="datacell" colspan=8>
                        <p><strong><br>Items<br><br></strong></p>
                    </th>
                </tr>
                <tr>
                    <th align=left valign=top class="datacell" colspan=8>
                        <input type="image" name="methodToCall.selectAllForRetransmit" src="${ConfigProperties.externalizable.images.url}tinybutton-selectall.gif" alt="Select all items." hspace=4 align=absmiddle>
		  		        &nbsp;&nbsp;
                        <input type="image" name="methodToCall.deselectAllForRetransmit" src="${ConfigProperties.externalizable.images.url}tinybutton-deselectall.gif" alt="Deselect all items." hspace=4 align=absmiddle>
                    </th>
                </tr>
                <tr>
                    <th width=20 align=center><b>Select</b></th>
                    <th width=20 align=center><b>Item Type Code</b></th>
                    <th width=72 align=center><b>Qty</b></th>
                    <th width=48 align=center><b>UOM</b></th>
                    <th width=112 align=center><b>Catalog Number</b></th>
                    <th align=center><b>Description</b></th>
                    <th width=89 align=center><b>Unit Cost</b></th>
                    <th width=141 align=center><b>Extended Cost</b></th>
                </tr>
                <logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
                    <c:if test="${ ((!itemLine.itemType.itemTypeAboveTheLineIndicator and (not empty itemLine.itemDescription)) or (itemLine.itemType.itemTypeAboveTheLineIndicator and itemLine.itemActiveIndicator)) }" >
                        <tr>
			
                            <kul:htmlAttributeHeaderCell scope="row">
                                <html:hidden property="document.item[${ctr}].documentNumber" />
           		                <html:hidden property="document.item[${ctr}].itemIdentifier" />
           		                <html:hidden property="document.item[${ctr}].itemLineNumber" />
           		                <html:hidden property="document.item[${ctr}].itemQuantity" />
                                <html:hidden property="document.item[${ctr}].versionNumber" />
                                <div align="center"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemSelectedForRetransmitIndicator}" property="document.item[${ctr}].itemSelectedForRetransmitIndicator" /></div>
                            </kul:htmlAttributeHeaderCell>

                            <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemTypeCode" /></td>
                            <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemQuantity}" property="document.item[${ctr}].itemQuantity" /></td>			
 				            <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="document.item[${ctr}].itemUnitOfMeasureCode" /></td>
 				            <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemCatalogNumber}" property="document.item[${ctr}].itemCatalogNumber" /></td>
 				            <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" /></td>
 				            <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" /></td>
 				            <td class="datacell"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.extendedPrice}" property="document.item[${ctr}].extendedPrice" /></td>
                        </tr>
                    </c:if>
                </logic:iterate>
            </tbody>
        </table>

    </div>

</kul:tab>
