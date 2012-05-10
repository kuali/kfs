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
		
