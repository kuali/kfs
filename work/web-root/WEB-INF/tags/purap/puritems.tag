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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<%@ taglib tagdir="/WEB-INF/tags/fin" prefix="fin"%>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>

<kul:tab tabTitle="Items" defaultOpen="true">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Items</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
		<tr>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}"/>			
            <%--<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}"/>--%>
            <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}"/>
			<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
 		</tr>
 		<tr>
 		<kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
 		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemLineNumber}" property="newPurchasingItemLine.itemLineNumber" /></td>
        <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="newPurchasingItemLine.itemTypeCode" /></td>
 		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemQuantity}" property="newPurchasingItemLine.itemQuantity" /></td>
 		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="newPurchasingItemLine.itemUnitOfMeasureCode" /></td>
		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemCatalogNumber}" property="newPurchasingItemLine.itemCatalogNumber" /></td>
		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="newPurchasingItemLine.itemDescription" /></td>
 		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="newPurchasingItemLine.itemUnitPrice" /></td>
 		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.extendedPrice}" property="newPurchasingItemLine.extendedPrice" /></td>
        <%--<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="newPurchasingItemLine.itemRestrictedIndicator" /></td>--%>
        <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" property="newPurchasingItemLine.itemAssignedToTradeInIndicator" /></td>
		<td class="infoline"><div align="center"><html:image property="methodToCall.addItem" src="images/tinybutton-add1.gif" alt="Insert an Item" title="Add an Item" styleClass="tinybutton"/></div></td>
		</tr>
		<tr><td width="100%" colspan="12"><!-- begin accounting lines -->
		<span align="center">
		
		<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" sourceAccountingLinesOnly="true" optionalFields="accountLinePercent"
		accountingLineAttributes="${accountingLineAttributes}" accountPrefix="newPurchasingItemLine." hideTotalLine="true" hideFields="amount" accountingAddLineIndex="-1"/>
		
		</span>
		</tr></td><!-- end accounting line -->					
		<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
			<tr>
			
				<kul:htmlAttributeHeaderCell scope="row">
           		    <html:hidden property="document.item[${ctr}].itemIdentifier" />
                    <html:hidden property="document.item[${ctr}].versionNumber" />
                    <div align="center"><html:image property="methodToCall.editItem" src="images/tinybutton-edit1.gif" alt="Edit an Item" title="Edit an Item" styleClass="tinybutton"/></div>
                </kul:htmlAttributeHeaderCell>
			
 				<td class="infoline"><html:hidden write="true" property="document.item[${ctr}].itemLineNumber" /><html:image property="methodToCall.editItem" src="images/purap-up.gif" alt="Move Item Up" title="Move Item Up" styleClass="tinybutton"/><html:image property="methodToCall.editItem" src="images/purap-down.gif" alt="Move Item Down" title="Move Item Down" styleClass="tinybutton"/></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemTypeCode" /></td>
 				<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemQuantity}" property="document.item[${ctr}].itemQuantity" /></td>
		 		<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="document.item[${ctr}].itemUnitOfMeasureCode" /></td>
				<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemCatalogNumber}" property="document.item[${ctr}].itemCatalogNumber" /></td>
				<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" /></td>
 				<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" /></td>
 				<td class="infoline"><html:hidden write="true" property="document.item[${ctr}].extendedPrice" />&nbsp;</td>
                <%--<td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="newPurchasingItemLine.itemRestrictedIndicator" /></td>--%>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" property="newPurchasingItemLine.itemAssignedToTradeInIndicator" /></td>
				<td class="infoline"><div align="center"><html:image property="methodToCall.deleteItem.line${ctr}" src="images/tinybutton-delete1.gif" alt="Delete Item ${ctr+1}" title="Delete Item ${ctr+1}" styleClass="tinybutton"/></div></td>
			</tr>
			<tr>
				<td width="100%" colspan="12">
				
					<fin:accountingLines editingMode="${KualiForm.editingMode}"
						editableAccounts="${KualiForm.editableAccounts}" sourceAccountingLinesOnly="true" optionalFields="accountLinePercent" extraHiddenFields=",accountIdentifier,itemIdentifier"
						accountingLineAttributes="${accountingLineAttributes}" accountPrefix="document.item[${ctr}]." hideTotalLine="true" hideFields="amount" accountingAddLineIndex="${ctr}"/>					
		
				</td>
			</tr>
				
		</logic:iterate>

<!-- BEGIN TOTAL SECTION -->
        <tr>
          <th height=30 colspan="12">&nbsp;</td>
        </tr>
        
        <tr>
          <td colspan="12" class="subhead">
            <span class="subhead-left">Totals</span>
            <span class="subhead-right">&nbsp;</span>
          </td>
        </tr>

        <tr>
            <th align=right width='75%' colspan=8 scope="row"><b>TOTAL:</b></th>
            <td align=left valign=middle colspan=4 class="datacell"><b>$${KualiForm.document.totalDollarAmount}</b><td>
        </tr>

        <c:if test="${KualiForm.document.documentHeader.workflowDocument.documentType == 'KualiRequisitionDocument'}">
            <tr>
                <th align=right width='75%' colspan=8 scope="row">APO Limit:</th>
                <td align=left valign=middle colspan=4 class="datacell">$${KualiForm.document.organizationAutomaticPurchaseOrderLimit}<td>
                <html:hidden property="document.organizationAutomaticPurchaseOrderLimit" />
            </tr>
        </c:if>
<!-- END TOTAL SECTION -->

        </table>

    </div>
</kul:tab>
