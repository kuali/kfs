<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="isCreditMemo" required="false" description="Indicates whether the tag is being used in the context of a credit memo document." %>

<kul:tab tabTitle="Process Items" defaultOpen="true" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
		
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
	
	    <c:set var="editingMode" value="${KualiForm.editingMode}" scope="request"/>
	    <c:set var="showAmount" value="${(!empty KualiForm.editingMode['allowAccountAmountEntry'])}" />
	    
	    <c:if test="${empty isCreditMemo or !isCreditMemo}" >
    		<purap:purPOLineItemTotals documentAttributes="${documentAttributes}" />

	    	<purap:paymentRequestItems 
		    	itemAttributes="${itemAttributes}"
	    		accountingLineAttributes="${accountingLineAttributes}" 
	    		showAmount="${showAmount}" />
		</c:if>

        <!--  replace literal with PurapConstants once exported -->
	    <c:if test="${isCreditMemo and !(KualiForm.document.creditMemoType eq 'Vendor')}" >
	    	<purap:creditMemoItems 
		    	itemAttributes="${itemAttributes}"
	    		accountingLineAttributes="${accountingLineAttributes}" />
	    </c:if>

		<!-- BEGIN TOTAL SECTION -->
		<tr>
			<td align=right width='75%' colspan="5" scope="row" class="datacell">
			    <div align="right">
			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.lineItemTotal}" skipHelpUrl="true"/></b>&nbsp;
			    </div>
			</td>
			<td valign=middle class="datacell" colspan="2">
			    <div align="right"><b>$${KualiForm.document.lineItemTotal}</b></div>
			</td>
			<td colspan=2 class="datacell">&nbsp;</td>
		</tr>
		<!-- END TOTAL SECTION -->

		<purap:miscitems 
			itemAttributes="${itemAttributes}" 
			accountingLineAttributes="${accountingLineAttributes}" 
			overrideTitle="Additional Charges" 
			showAmount="${showAmount}"
			updateExtended="${true}" />

		<!-- BEGIN TOTAL SECTION -->
		<tr>
			<td align=right width='75%' colspan="5" scope="row" class="datacell">
			    <div align="right">
			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grandTotal}" skipHelpUrl="true" /></b>&nbsp;
			    </div>
			</td>
			<td valign=middle class="datacell" colspan="2">
			    <div align="right"><b>$${KualiForm.document.grandTotal}</b></div>
			</td>
			<td colspan=2 class="datacell">
              <c:if test="${KualiForm.calculated and (empty KualiForm.document.recurringPaymentTypeCode)}">                
                <kul:htmlControlAttribute
				    attributeEntry="${documentAttributes.closeReopenPoIndicator}"
				    property="document.closeReopenPoIndicator"
				    readOnly="false" />
				    <kul:htmlAttributeLabel attributeEntry="${documentAttributes.closeReopenPoIndicator}" skipHelpUrl="true" noColon="true" />
              </c:if>
              <c:if test="${not empty KualiForm.document.recurringPaymentTypeCode}">
                Recurring PO
              </c:if>			
			</td>
		</tr>
		<!-- END TOTAL SECTION -->
		
        <c:if test="${isCreditMemo}" >
          <c:if test="${KualiForm.showTotalOverride}" >
		    <tr>
			   <td align=right width='75%' colspan="5" scope="row" class="datacell">
			      <div align="right">
			        <bean:message key="message.creditMemo.totalOverride" />:&nbsp;
			      </div>
			  </td>
			  <td valign=middle class="datacell" colspan="2">
			      <div align="left"><html:checkbox property="document.unmatchedOverride" value="Y"/></div>
		  	  </td>
			  <td colspan=2 class="datacell">&nbsp;</td>
		  </tr>
		  </c:if>
          <c:if test="${!KualiForm.showTotalOverride}" >
               <html:hidden property="document.unmatchedOverride" />
          </c:if>
        </c:if>

	</table>

	</div>
</kul:tab>
		