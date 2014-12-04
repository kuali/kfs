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
              
<c:set var="additionalChargesExist" value="${KualiForm.document.additionalChargesExist}" />

<kul:tabTop tabTitle="Split a PO" defaultOpen="true" tabErrorKey="${PurapConstants.SPLIT_PURCHASE_ORDER_TAB_ERRORS}">

    <div class="tab-container" align=center>
            <h3>Split a PO</h3>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Preliminaries">

            <tr>
            	<th align=right valign=middle class="bord-l-b" width="50%">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.copyingNotesWhenSplitting}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="50%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.copyingNotesWhenSplitting}" property="document.copyingNotesWhenSplitting" />
                </td>
            </tr>
            
            <c:if test="${additionalChargesExist}">
	            <tr>
	            	<th align=right valign=middle class="bord-l-b" width="50%">
	            		<div align="right"><c:out value="${PurapConstants.PODocumentsStrings.SPLIT_ADDL_CHARGES_WARNING_LABEL}"/></div>
	            	</th>
	            	<td align=left valign=middle class="datacell" width="50%">
	            		<h3><c:out value="${PurapConstants.PODocumentsStrings.SPLIT_ADDL_CHARGES_WARNING}"/></h3>
	            		<table cellpadding="0" cellspacing="0" class="datatable" summary="Additional Charges Summary">
	            			<tr>
	            				<kul:htmlAttributeHeaderCell literalLabel="Item Type" />
	            				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" />
	            			</tr>
	            			<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
	            				<c:choose>
        							<c:when test="${itemLine.itemType.additionalChargeIndicator == true}">
        								<tr>
        									<td class="datacell">
											    <kul:htmlControlAttribute
												    attributeEntry="${itemAttributes.itemTypeCode}"
												    property="document.item[${ctr}].itemTypeCode"
												    extraReadOnlyProperty="document.item[${ctr}].itemType.itemTypeDescription" readOnly="true" />
											</td>
											<td class="datacell">
							    				<div align="right">
							        				<kul:htmlControlAttribute
								        				attributeEntry="${itemAttributes.extendedPrice}"
								        				property="document.item[${ctr}].extendedPrice" readOnly="true" />
							    				</div>
											</td>
										</tr>
									</c:when>
								</c:choose>
					         </logic:iterate>
	            		</table>
	            	</td>
	            </tr>
	        </c:if>
        </table>
        
        	<h3>Splitting Item Selection</h3>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Splitting Item Selection">
        	<tr>
        		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.movingToSplit}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
				<kul:htmlAttributeHeaderCell literalLabel="Item Type" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchasingCommodityCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" />
        	</tr>
        	<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
        		<c:if test="${itemLine.itemType.lineItemIndicator == true}">
       				<tr>
			        	<td class="datacell">
			        		<div align="center">
				        		<kul:htmlControlAttribute
									attributeEntry="${itemAttributes.movingToSplit}"
									property="document.item[${ctr}].movingToSplit" readOnly="false" />
							</div>
			        	</td>
		        		<td class="datacell">
		        			<div align="center">
		        				&nbsp;<b><html:hidden write="true" property="document.item[${ctr}].itemLineNumber" /></b>&nbsp;
		        			</div>
		        		</td>
						<td class="datacell">
						    <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemTypeCode}"
							    property="document.item[${ctr}].itemTypeCode"
							    extraReadOnlyProperty="document.item[${ctr}].itemType.itemTypeDescription" readOnly="true" />
						</td>
						<td class="datacell">
						    <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemQuantity}"
							    property="document.item[${ctr}].itemQuantity" readOnly="true" />
						</td>
						<td class="datacell">
						    <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
							    property="document.item[${ctr}].itemUnitOfMeasureCode" readOnly="true" />
					    </td>
						<td class="datacell">
						    <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemCatalogNumber}"
							    property="document.item[${ctr}].itemCatalogNumber" readOnly="true" />
					    </td>
					    <td class="datacell">
					    	<kul:htmlControlAttribute 
	                            attributeEntry="${itemAttributes.purchasingCommodityCode}" 
	                            property="document.item[${ctr}].purchasingCommodityCode" readOnly="true"/>
	                    </td>
						<td class="datacell">
							 <kul:htmlControlAttribute
							    attributeEntry="${itemAttributes.itemDescription}"
							    property="document.item[${ctr}].itemDescription" readOnly="true" />
						</td>
						<td class="datacell">
						    <div align="right">
						        <kul:htmlControlAttribute
							        attributeEntry="${itemAttributes.itemUnitPrice}"
							        property="document.item[${ctr}].itemUnitPrice" readOnly="true" />
							</div>
						</td>
						<td class="datacell">
						    <div align="right">
						        <kul:htmlControlAttribute
							        attributeEntry="${itemAttributes.extendedPrice}"
							        property="document.item[${ctr}].extendedPrice" readOnly="true" />
						    </div>
						</td>
	                    <td class="datacell">
						    <div align="right">
						        <kul:htmlControlAttribute
							        attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}"
							        property="document.item[${ctr}].itemAssignedToTradeInIndicator" readOnly="true" />
						    </div>
						</td>
					</tr>					 			        	
				</c:if>
	        </logic:iterate>
        </table>
    </div>
    
</kul:tabTop>
