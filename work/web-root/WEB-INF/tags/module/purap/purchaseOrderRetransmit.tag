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
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for item's fields." %>
<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>
             
     

<kul:tab tabTitle="Purchase Order Retransmit" defaultOpen="true" tabErrorKey="${PurapConstants.PO_RETRANSMIT_SELECT_TAB_ERRORS}" >
	
    <div class="tab-container" align=center>
            <h3>Purchase Order Retransmit</h3>

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
	                    <html:image property="methodToCall.selectAllForRetransmit" 
	                    			src="${ConfigProperties.externalizable.images.url}tinybutton-selectall.gif" 
									alt="Select all items." align="absmiddle"/>
						&nbsp;&nbsp;
	  	  		        <html:image property="methodToCall.deselectAllForRetransmit" 
	            			        src="${ConfigProperties.externalizable.images.url}tinybutton-deselectall.gif" 
									alt="Deselect all items." align="absmiddle"/>   
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
                    <c:if test="${ ((itemLine.itemType.additionalChargeIndicator and (not empty itemLine.itemDescription)) or (itemLine.itemType.lineItemIndicator and itemLine.itemActiveIndicator)) }" >
                        <tr>
			
                            <kul:htmlAttributeHeaderCell scope="row">
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
