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

<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="showAmount" required="false"
    type="java.lang.Boolean"
    description="show the amount if true else percent" %>
<%@ attribute name="mainColumnCount" required="true" %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="lockTaxAmountEntry" value="${(not empty KualiForm.editingMode['lockTaxAmountEntry'])}" />
<c:set var="clearAllTaxes" value="${(not empty KualiForm.editingMode['clearAllTaxes'])}" />
<c:set var="purapTaxEnabled" value="${(not empty KualiForm.editingMode['purapTaxEnabled'])}" />
<c:set var="tabindexOverrideBase" value="50" />
<c:set var="fullDocEntryCompleted" value="${(not empty KualiForm.editingMode['fullDocumentEntryCompleted'])}" />

<c:set var="colSpanDescription" value="3"/>
<c:if test="${purapTaxEnabled}">
	<c:set var="colSpanDescription" value="1"/>
</c:if>

<tr>
	<td colspan="${mainColumnCount}" class="subhead">
		<span class="subhead-left">Items</span>
	</td>
</tr>

<c:if test="${fullEntryMode}">
		<tr>
			<td colspan="${mainColumnCount}" class="datacell" align="right" nowrap="nowrap">
				<div align="right">
					<c:if test="${KualiForm.ableToShowClearAndLoadQtyButtons}">
						<html:image property="methodToCall.loadQty" src="${ConfigProperties.externalizable.images.url}tinybutton-loadqtyinvoiced.gif" alt="load qty invoiced" title="load qty invoiced" styleClass="tinybutton" />
						<html:image property="methodToCall.clearQty" src="${ConfigProperties.externalizable.images.url}tinybutton-clearqtyinvoiced.gif" alt="clear qty invoiced" title="clear qty invoiced" styleClass="tinybutton" />
					</c:if>
				</div>
			</td>
		</tr>
</c:if>
		
		
<%-- temporary workaround due to removing discount item --%>
<c:if test="${KualiForm.countOfAboveTheLine>=1}">
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" width="2%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.poOutstandingQuantity}" width="2%" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" width="5%" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchaseOrderItemUnitPrice}" width="5%"/>				
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" width="5%" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" width="5%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" width="5%"/>
		
		<c:if test="${purapTaxEnabled}">
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTaxAmount}" width="5%"/>		
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.totalAmount}" width="5%"/>
		</c:if>

		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" width="5%" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" width="2%" />		
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" width="45%"  colspan="${colSpanDescription}"/>
		<c:if test="${fullEntryMode}">
			<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
		</c:if>
		
	</tr>
</c:if>

<c:if test="${KualiForm.countOfAboveTheLine<1}">
	<tr>
		<th height=30 colspan="${mainColumnCount}">No items Payable</th>
	</tr>
</c:if>

<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
	
	<c:if test="${itemLine.itemType.lineItemIndicator == true}">
		<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
		<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
        
        <c:choose>
            <c:when test="${itemLine.objectId == null}">
                <c:set var="newObjectId" value="<%= (new org.kuali.rice.kns.util.Guid()).toString()%>" />
                <c:set var="tabKey" value="Item-${newObjectId}" />
            </c:when>
            <c:when test="${itemLine.objectId != null}">
                <c:set var="tabKey" value="Item-${itemLine.objectId}" />
            </c:when>
        </c:choose>
    
        <!--  hit form method to increment tab index -->
        <c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
        <c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

		<%-- default to closed --%>
		<c:choose>
		<c:when test="${empty currentTab}">
			<c:set var="isOpen" value="false" />
		</c:when>
		<c:when test="${!empty currentTab}">
			<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
		</c:when>
		</c:choose>

		<tr>
			<td class="infoline" nowrap="nowrap" rowspan="2">
				  &nbsp;<b><bean:write name="KualiForm" property="document.item[${ctr}].itemLineNumber"/></b> 
			</td>
			<td class="infoline">
				<c:choose>
				<c:when test="${KualiForm.document.items[ctr].itemType.quantityBasedGeneralLedgerIndicator}">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.poOutstandingQuantity}"
				    property="document.item[${ctr}].poOutstandingQuantity"
				    readOnly="true" />
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
				</c:choose>
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
				    property="document.item[${ctr}].itemUnitOfMeasureCode"
				    readOnly="true" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.purchaseOrderItemUnitPrice}"
				    property="document.item[${ctr}].purchaseOrderItemUnitPrice"
				    readOnly="true" />
		    </td>				    
			<td class="infoline">
			    <div align="right">
			        <kul:htmlControlAttribute
				        attributeEntry="${itemAttributes.itemQuantity}"
				        property="document.item[${ctr}].itemQuantity"
				        readOnly="${ (not (fullEntryMode) or (fullDocEntryCompleted)) or (KualiForm.document.items[ctr].itemType.amountBasedGeneralLedgerIndicator) }" 
				        tabindexOverride="${tabindexOverrideBase + 0}" />				        
				</div>
			</td>
			<td class="infoline">
			    <div align="right">
                    <c:if test="${KualiForm.document.items[ctr].itemType.quantityBasedGeneralLedgerIndicator}">
                        <kul:htmlControlAttribute
                            attributeEntry="${itemAttributes.itemUnitPrice}"
                            property="document.item[${ctr}].itemUnitPrice"
                            readOnly="${(not (fullEntryMode) or (fullDocEntryCompleted))}" 
                            tabindexOverride="${tabindexOverrideBase + 0}" />
                    </c:if>
                    <c:if test="${KualiForm.document.items[ctr].itemType.amountBasedGeneralLedgerIndicator}">
                        <kul:htmlControlAttribute
                            attributeEntry="${itemAttributes.itemUnitPrice}"
                            property="document.item[${ctr}].poOutstandingAmount"
                            readOnly="true" />
                    </c:if>
				</div>
			</td>
			<td class="infoline">
			    <div align="right">
			        <kul:htmlControlAttribute
				        attributeEntry="${itemAttributes.extendedPrice}"
				        property="document.item[${ctr}].extendedPrice" 
				        readOnly="${(not (fullEntryMode) or (fullDocEntryCompleted))}" 
				        tabindexOverride="${tabindexOverrideBase + 0}" />
			    </div>
			</td>

			<c:if test="${purapTaxEnabled}">
			<td class="infoline">
			    <div align="right">
			        <kul:htmlControlAttribute
				        attributeEntry="${itemAttributes.itemTaxAmount}"
				        property="document.item[${ctr}].itemTaxAmount" 
				        readOnly="${not (fullEntryMode) or lockTaxAmountEntry}" 
				        tabindexOverride="${tabindexOverrideBase + 0}" />
			    </div>
			</td>			
			<td class="infoline">
			    <div align="right">
			        <kul:htmlControlAttribute
				        attributeEntry="${itemAttributes.totalAmount}"
				        property="document.item[${ctr}].totalAmount" 
				        readOnly="true" />
			    </div>
			</td>
			</c:if>

			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemCatalogNumber}"
				    property="document.item[${ctr}].itemCatalogNumber"
				    readOnly="true" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}"
				    property="document.item[${ctr}].itemAssignedToTradeInIndicator"
				    readOnly="true" />
			</td>			    
			<td  class="infoline" colspan="${colSpanDescription}">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemDescription}"
				    property="document.item[${ctr}].itemDescription"
				    readOnly="true" />
			</td>	
			
			<c:if test="${fullEntryMode}">
				<td class="infoline"> 
					<div style="text-align: center;">
						<html:image property="methodToCall.recalculateItemAccountsAmounts.line${ctr}.Anchor" 
							src="${ConfigProperties.externalizable.images.url}tinybutton-calculate.gif" 
							title="Recalculate Item's accounts amounts distributions"
							alt="Recalculate Item's accounts amounts distributions" styleClass="tinybutton" />&nbsp;	
						<html:image property="methodToCall.restoreItemAccountsAmounts.line${ctr}.Anchor" 
							src="${ConfigProperties.externalizable.images.url}tinybutton-restore.gif" 
							title="Restore Item's accounts percents/amounts from Purchase Order"
							alt="Restore Item's accounts percents/amounts from Purchase Order" styleClass="tinybutton" />
					</div>
				</td>										
		</c:if>		
					
		</tr>
		
		<c:set var="hideFields" value="amount" />
		<c:if test="${showAmount}">
			<c:set var="hideFields" value="" />
		</c:if>		
		<purap:purapGeneralAccounting
			accountPrefix="document.item[${ctr}]." 
			itemColSpan="${mainColumnCount-1}" />	
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			</tbody>
		</c:if>
	</c:if>
</logic:iterate>

<c:if test="${(fullEntryMode) and (clearAllTaxes) and (purapTaxEnabled)}">
	<tr>
		<th height=30 colspan="${mainColumnCount}">
			<html:image 
			    property="methodToCall.clearAllTaxes" 
			    src="${ConfigProperties.externalizable.images.url}tinybutton-clearalltax.gif" 
			    alt="Clear all tax" 
			    title="Clear all tax" styleClass="tinybutton" />
			 </div>
	 	</th>
	 </tr>
</c:if>	
		
<tr>
	<th height=30 colspan="${mainColumnCount}">&nbsp;</th>
</tr>
