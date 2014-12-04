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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="isCreditMemo" required="false" description="Indicates whether the tag is being used in the context of a credit memo document." %>

<c:set var="fullDocumentEntryCompleted" value="${not empty KualiForm.editingMode['fullDocumentEntryCompleted']}" />
<c:set var="purapTaxEnabled" value="${(not empty KualiForm.editingMode['purapTaxEnabled'])}" />
<c:set var="tabindexOverrideBase" value="50" />

<c:set var="mainColumnCount" value="13"/>
<c:set var="colSpanItemType" value="6"/>
<c:set var="colSpanExtendedPrice" value="1"/>
<c:if test="${purapTaxEnabled}">
	<c:set var="colSpanDescription" value="3"/>
</c:if>	
<c:if test="${!purapTaxEnabled}">
	<c:set var="colSpanDescription" value="5"/>
</c:if>	

<kul:tab tabTitle="Process Items" defaultOpen="true" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
	<c:if test="${!KualiForm.document.inquiryRendered}">
	    <div align="left">
	        Object Code and Sub-Object Code inquiries and descriptions have been removed because this is a prior year document.
        </div>
        <br>
    </c:if>		
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
	
	    <c:if test="${empty isCreditMemo or !isCreditMemo}" >
			<c:set var="colSpanItemType" value="4"/>			
    		<purap:purPOLineItemTotals documentAttributes="${documentAttributes}" mainColumnCount="${mainColumnCount}" />
	    	<purap:paymentRequestItems 
		    	itemAttributes="${itemAttributes}"
	    		accountingLineAttributes="${accountingLineAttributes}" 
	    		showAmount="${fullDocumentEntryCompleted}" 
	    		mainColumnCount="${mainColumnCount}" />
		</c:if>

        <!--  replace literal with PurapConstants once exported -->
	    <c:if test="${isCreditMemo && !(KualiForm.document.creditMemoType eq 'Vendor')}" >
	    		<purap:creditMemoItems 
		    		itemAttributes="${itemAttributes}"
	    			accountingLineAttributes="${accountingLineAttributes}" 
	    			mainColumnCount="${mainColumnCount}" />
		</c:if>

		<!-- BEGIN SUBTOTAL SECTION -->
		<c:set var="colSpanTotalLabel" value="${mainColumnCount-colSpanDescription-colSpanExtendedPrice}"/>
		<c:set var="colSpanTotalAmount" value="${colSpanExtendedPrice}"/>
		<c:set var="colSpanTotalBlank" value="${colSpanDescription}"/>					
		<c:if test="${purapTaxEnabled}">		
			<c:set var="colSpanTotalAmount" value="1"/>
			<c:set var="colSpanTotalLabel" value="${mainColumnCount-colSpanTotalBlank-colSpanTotalAmount}"/>		
		</c:if>
		
		<tr>
			<td align=right width='75%' scope="row" class="datacell" colspan="${colSpanTotalLabel}">
			    <div align="right">
			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.lineItemTotal}" skipHelpUrl="true"/></b>&nbsp;
			    </div>
			</td>
			<td valign=middle class="datacell" colspan="${colSpanTotalAmount}">
			    <div align="right"><b>
                    <kul:htmlControlAttribute
                        attributeEntry="${documentAttributes.lineItemTotal}"
                        property="document.lineItemPreTaxTotal"
                        readOnly="true" />&nbsp; </b>
                </div>
			</td>
			<td class="datacell" colspan="${colSpanTotalBlank}">&nbsp;</td>
		</tr>
		<!-- END SUBTOTAL SECTION -->
		
		<c:set var="showInvoiced" value="${empty isCreditMemo or !isCreditMemo}" />
		<purap:miscitems 
			documentAttributes="${documentAttributes}"
			itemAttributes="${itemAttributes}" 
			accountingLineAttributes="${accountingLineAttributes}" 
			overrideTitle="Additional Charges" 
			showAmount="${fullDocumentEntryCompleted}"
			showInvoiced="${showInvoiced}"
			specialItemTotalType="DISC" 
			mainColumnCount="${mainColumnCount}"
			colSpanItemType="${colSpanItemType}" 
			colSpanDescription="${colSpanDescription}" 
			colSpanExtendedPrice="${colSpanExtendedPrice}"
			colSpanAmountPaid="0">
			<jsp:attribute name="specialItemTotalOverride">
				<tr>
					<td align=right width='75%' scope="row" class="datacell" colspan="${colSpanTotalLabel}">
			 			<div align="right">
			        		<b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grandTotalExcludingDiscount}" skipHelpUrl="true" /></b>&nbsp;
			    		</div>
					</td>
					<td valign=middle class="datacell" colspan="${colSpanTotalAmount}">
		    		    <div align="right"><b>
		    		    	<c:choose>
		    		    		<c:when test="${isCreditMemo && !(KualiForm.document.creditMemoType eq 'Vendor')}" >
		                            <kul:htmlControlAttribute
		                                attributeEntry="${DataDictionary.PaymentRequestDocument.attributes.grandTotalExcludingDiscount}"
		                                property="document.grandTotal"
		                                readOnly="true" />&nbsp; </b>
								</c:when>
                            	<c:otherwise>
		                            <kul:htmlControlAttribute
		                                attributeEntry="${DataDictionary.PaymentRequestDocument.attributes.grandTotalExcludingDiscount}"
		                                property="document.grandPreTaxTotalExcludingDiscount"
		                                readOnly="true" />&nbsp; </b>
								</c:otherwise>
							</c:choose>
                        </div>
					</td>	
					<td class="datacell" colspan="${colSpanTotalBlank}">
						&nbsp;
					</td>
				</tr>
			</jsp:attribute>
		</purap:miscitems>
		
		<c:set var="taxInfoViewable" value="${KualiForm.editingMode['taxInfoViewable']}" scope="request" />
    	<c:set var="taxAreaEditable" value="${KualiForm.editingMode['taxAreaEditable']}" scope="request" />
		<c:if test="${taxInfoViewable || taxAreaEditable}" >
			<purap:taxitems 
				documentAttributes="${documentAttributes}"
				itemAttributes="${itemAttributes}" 
				accountingLineAttributes="${accountingLineAttributes}" 
				overrideTitle="Tax Withholding Charges" 
				mainColumnCount="${mainColumnCount}"
				colSpanItemType="${colSpanItemType}" 
				colSpanExtendedPrice="${colSpanExtendedPrice}" >
			</purap:taxitems>
		</c:if> 
		
		<!-- BEGIN TOTAL SECTION -->
		<c:if test="${purapTaxEnabled}">		
		<tr>
			<td align=right width='75%' scope="row" class="datacell" colspan="${colSpanTotalLabel}">
			    <div align="right">
					<c:if test="${(empty isCreditMemo or !isCreditMemo) and purapTaxEnabled and KualiForm.document.useTaxIndicator}" >
					<b>[Vendor Remit Amount]</b>
					</c:if>

			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grandPreTaxTotal}" skipHelpUrl="true" /></b>&nbsp;
			    </div>
			</td>
			<td valign=middle class="datacell" colspan="${colSpanTotalAmount}">
			    <div align="right"><b>
                    <kul:htmlControlAttribute
                        attributeEntry="${documentAttributes.grandPreTaxTotal}"
                        property="document.grandPreTaxTotal"
                        readOnly="true" />&nbsp; </b>
                </div>
			</td>
			<td valign="middle" class="datacell" colspan="${colSpanTotalBlank}">
				&nbsp;
			</td>
		</tr>
		
		<tr>
			<td align=right width='75%' scope="row" class="datacell" colspan="${colSpanTotalLabel}">
			    <div align="right">
			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grandTaxAmount}" skipHelpUrl="true" /></b>&nbsp;
			    </div>
			</td>
			<td valign=middle class="datacell" colspan="${colSpanTotalAmount}">
			    <div align="right"><b>
                    <kul:htmlControlAttribute
                        attributeEntry="${documentAttributes.grandTaxAmount}"
                        property="document.grandTaxAmount"
                        readOnly="true" />&nbsp; </b>
                </div>
			</td>
			<td valign="middle" class="datacell" colspan="${colSpanTotalBlank}">
				&nbsp;
			</td>
		</tr>
		</c:if>

		<tr>
			<td align=right width='75%' scope="row" class="datacell" colspan="${colSpanTotalLabel}">
			    <div align="right">
					<c:if test="${(empty isCreditMemo or !isCreditMemo) and purapTaxEnabled and !KualiForm.document.useTaxIndicator}" >
					<b>[Vendor Remit Amount]</b>
					</c:if>

			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grandTotal}" skipHelpUrl="true" /></b>&nbsp;
			    </div>
			</td>
			<td valign=middle class="datacell" colspan="${colSpanTotalAmount}">
			    <div align="right"><b>
                    <kul:htmlControlAttribute
                        attributeEntry="${documentAttributes.grandTotal}"
                        property="document.grandTotal"
                        readOnly="true" />&nbsp; </b>
                </div>
			</td>
			<td class="datacell" colspan="${colSpanTotalBlank}">
              <c:if test="${empty isCreditMemo or !isCreditMemo}" >
	              <c:if test="${empty KualiForm.document.recurringPaymentTypeCode and !empty KualiForm.editingMode['allowClosePurchaseOrder']}" >
	              	<kul:htmlControlAttribute
					    attributeEntry="${documentAttributes.closePurchaseOrderIndicator}"
					    property="document.closePurchaseOrderIndicator"
					    readOnly="false" tabindexOverride="${tabindexOverrideBase + 0}" />
					    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.closePurchaseOrderIndicator}" skipHelpUrl="true" noColon="true" />
	              </c:if>
	              <c:if test="${not empty KualiForm.document.recurringPaymentTypeCode and not fullDocumentEntryCompleted}">
	                Recurring PO
	              </c:if>
              </c:if>
              <c:if test="${isCreditMemo}" >
	              <c:if test="${empty KualiForm.document.paymentRequestDocument.recurringPaymentTypeCode and !empty KualiForm.editingMode['allowReopenPurchaseOrder']}">
	                <kul:htmlControlAttribute
					    attributeEntry="${documentAttributes.reopenPurchaseOrderIndicator}"
					    property="document.reopenPurchaseOrderIndicator"
					    readOnly="false" tabindexOverride="${tabindexOverrideBase + 0}"/>
					    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.reopenPurchaseOrderIndicator}" skipHelpUrl="true" noColon="true" />
	              </c:if>
	              <c:if test="${not empty KualiForm.document.paymentRequestDocument.recurringPaymentTypeCode and not fullDocumentEntryCompleted}">
	              	Recurring PO
	              </c:if>			
              </c:if>		
			</td>
		</tr>		
		<!-- END TOTAL SECTION -->
		
	</table>

	</div>
</kul:tab>
		
