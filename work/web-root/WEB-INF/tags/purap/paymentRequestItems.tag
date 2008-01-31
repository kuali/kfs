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

<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="showAmount" required="false"
    type="java.lang.Boolean"
    description="show the amount if true else percent" %>


<tr>
	<td colspan="10" class="subhead">
		<span class="subhead-left">Edit Items</span>
	</td>
</tr>

<%-- temporary workaround due to removing discount item --%>
<c:if test="${KualiForm.countOfAboveTheLine>=1}">
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" width="2%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.poOutstandingQuantity}" width="12%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" width="12%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchaseOrderItemUnitPrice}" width="12%"/>				
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" width="12%"/>				
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" width="12%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" width="12%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" width="12%"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" width="25%"/>
	</tr>
</c:if>

<c:if test="${KualiForm.countOfAboveTheLine<1}">
	<tr>
		<th height=30 colspan="10">No items Payable</th>
	</tr>
</c:if>

<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
	
	<c:if test="${itemLine.itemType.itemTypeAboveTheLineIndicator == true}">
		<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
		<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
        
        <c:choose>
            <c:when test="${itemLine.objectId == null}">
                <c:set var="newObjectId" value="<%= (new org.kuali.core.util.Guid()).toString()%>" />
                <c:set var="tabKey" value="Item-${newObjectId}" />
                <html:hidden property="document.item[${ctr}].objectId" value="${newObjectId}" />
            </c:when>
            <c:when test="${itemLine.objectId != null}">
                <c:set var="tabKey" value="Item-${itemLine.objectId}" />
                <html:hidden property="document.item[${ctr}].objectId" /> 
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

		<html:hidden property="tabStates(${tabKey})" value="${isOpen}" />

		<tr>
			<td class="infoline" nowrap="nowrap">
			    <html:hidden property="document.item[${ctr}].itemIdentifier" /> 
			    <html:hidden property="document.item[${ctr}].purapDocumentIdentifier" /> 
			    <html:hidden property="document.item[${ctr}].versionNumber" /> 
			    <html:hidden property="document.item[${ctr}].itemTypeCode" /> 
			    <html:hidden property="document.item[${ctr}].itemType.itemTypeCode" /> 
			    <html:hidden property="document.item[${ctr}].itemType.itemTypeDescription" />
			    <html:hidden property="document.item[${ctr}].itemType.active" />
			    <html:hidden property="document.item[${ctr}].itemType.quantityBasedGeneralLedgerIndicator" />
			    <html:hidden property="document.item[${ctr}].itemType.itemTypeAboveTheLineIndicator" />
			    &nbsp;<b><html:hidden write="true" property="document.item[${ctr}].itemLineNumber" /></b> 
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
				        readOnly="${ not (fullEntryMode) or (KualiForm.document.items[ctr].itemType.amountBasedGeneralLedgerIndicator) }" />				        
				</div>
			</td>
			<td class="infoline">
			    <div align="right">
                    <c:if test="${KualiForm.document.items[ctr].itemType.quantityBasedGeneralLedgerIndicator}">
                        <kul:htmlControlAttribute
                            attributeEntry="${itemAttributes.itemUnitPrice}"
                            property="document.item[${ctr}].itemUnitPrice"
                            readOnly="${not (fullEntryMode) or (KualiForm.document.items[ctr].itemType.amountBasedGeneralLedgerIndicator) }" />
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
				        readOnly="${not (fullEntryMode)}" />
			    </div>
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemCatalogNumber}"
				    property="document.item[${ctr}].itemCatalogNumber"
				    readOnly="true" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemDescription}"
				    property="document.item[${ctr}].itemDescription"
				    readOnly="true" />
			</td>
			
			<c:if test="${(not (fullEntryMode))}">
				<td class="infoline">
				    <div align="center">&nbsp;</div>
				</td>
			</c:if>
		</tr>
		<c:set var="optionalFields" value="accountLinePercent" />
		<c:set var="extraHiddenFields" value=",accountIdentifier,itemIdentifier,amount" />
		<c:set var="hideFields" value="amount" />
		<c:if test="${showAmount}">
			<c:set var="optionalFields" value="" />
			<c:set var="extraHiddenFields" value=",accountIdentifier,itemIdentifier,accountLinePercent" />
			<c:set var="hideFields" value="" />
		</c:if>
		<purap:puraccountingLineCams
			editingMode="${KualiForm.editingMode}"
			editableAccounts="${KualiForm.editableAccounts}"
			sourceAccountingLinesOnly="true"
			optionalFields="${optionalFields}"
			extraHiddenFields="${extraHiddenFields}"
			accountPrefix="document.item[${ctr}]." hideTotalLine="true"
			accountingLineAttributes="${accountingLineAttributes}" 
			hideFields="${hideFields}" 
			accountingAddLineIndex="${ctr}" 
			suppressCams="${true}" 
			overrideTitle="Item Accounting Lines"/>
	
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			</tbody>
		</c:if>
	</c:if>
</logic:iterate>

<tr>
	<th height=30 colspan="10">&nbsp;</th>
</tr>