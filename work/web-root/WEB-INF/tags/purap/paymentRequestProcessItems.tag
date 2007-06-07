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

<kul:tab tabTitle="Process Items" defaultOpen="false" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
		
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
	
		<purap:purPOLineItemTotals documentAttributes="${documentAttributes}" />

		<purap:paymentRequestItems 
			itemAttributes="${itemAttributes}"
			accountingLineAttributes="${accountingLineAttributes}" />

		<!-- BEGIN TOTAL SECTION -->
		<tr>
			<td align=right width='75%' colspan=7 scope="row" class="datacell">
			    <div align="right">
			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.lineItemTotal}" skipHelpUrl="true" noColon="true" /></b>
			    </div>
			</td>
			<td valign=middle class="datacell">
			    <div align="right"><b>$${KualiForm.document.lineItemTotal}</b></div>
			</td>
			<td colspan=2 class="datacell">&nbsp;</td>
		</tr>
		<!-- END TOTAL SECTION -->

		<purap:miscitems 
			itemAttributes="${itemAttributes}" 
			accountingLineAttributes="${accountingLineAttributes}" 
			overrideTitle="Additional Charges" />

		<!-- BEGIN TOTAL SECTION -->
		<tr>
			<td align=right width='75%' colspan=7 scope="row" class="datacell">
			    <div align="right">
			        <b><kul:htmlAttributeLabel attributeEntry="${documentAttributes.grandTotal}" skipHelpUrl="true" noColon="true" /></b>
			    </div>
			</td>
			<td valign=middle class="datacell">
			    <div align="right"><b>$${KualiForm.document.grandTotal}</b></div>
			</td>
			<td colspan=2 class="datacell">&nbsp;</td>
		</tr>
		<!-- END TOTAL SECTION -->

	</table>

	</div>
</kul:tab>
		