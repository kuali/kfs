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

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />

<kul:tab tabTitle="Items" defaultOpen="true" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
		<tr>
			<td colspan="12" class="subhead">
			    <span class="subhead-left">Receive Items</span>
			</td>
		</tr>

		<tr>
			<td colspan="12" class="datacell" align="right" nowrap="nowrap">
				<div align="right">
					<c:if test="${KualiForm.ableToShowClearAndLoadQtyButtons}">
						<html:image property="methodToCall.loadQty" src="${ConfigProperties.externalizable.images.url}tinybutton-loadqtyreceived.gif" alt="load qty received" title="load qty received" styleClass="tinybutton" />
						<html:image property="methodToCall.clearQty" src="${ConfigProperties.externalizable.images.url}tinybutton-clearqtyreceived.gif" alt="clear qty received" title="clear qty received" styleClass="tinybutton" />
					</c:if>
				</div>
			</td>
		</tr>

		<tr>
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemOrderedQuantity}" />						
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />			
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedPriorQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedToBeQuantity}" />		
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReturnedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDamagedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReasonAddedCode}" />
			<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
		</tr>
		<tr>
            <td class="infoline">
                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemLineNumber}" property="newReceivingLineItemLine.itemLineNumber" readOnly="${true}"/>
            </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemCatalogNumber}" property="newReceivingLineItemLine.itemCatalogNumber" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="newReceivingLineItemLine.itemDescription" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemOrderedQuantity}" property="newReceivingLineItemLine.itemOrderedQuantity" readOnly="${true}"/>
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="newReceivingLineItemLine.itemUnitOfMeasureCode" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedPriorQuantity}" property="newReceivingLineItemLine.itemReceivedPriorQuantity" readOnly="${true}"/>
		    </td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedToBeQuantity}" property="newReceivingLineItemLine.itemReceivedToBeQuantity" readOnly="${true}"/>
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedTotalQuantity}" property="newReceivingLineItemLine.itemReceivedTotalQuantity" />
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReturnedTotalQuantity}" property="newReceivingLineItemLine.itemReturnedTotalQuantity" />
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDamagedTotalQuantity}" property="newReceivingLineItemLine.itemDamagedTotalQuantity" />
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReasonAddedCode}" property="newReceivingLineItemLine.itemReasonAddedCode" />
			</td>

			<td class="infoline">
			    <div align="center">
			        <html:image property="methodToCall.addItem" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item" title="Add an Item" styleClass="tinybutton" />
			    </div>
			</td>						
		</tr>

		<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
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
					<c:set var="isOpen" value="true" />
				</c:when>
				<c:when test="${!empty currentTab}">
					<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
				</c:when>
			</c:choose>

			<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

		<tr>
			<td colspan="12" class="tab-subhead" style="border-right: none;">				 
		    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
			    <html:image
				    property="methodToCall.toggleTab.tab${tabKey}"
				    src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif"
				    alt="hide" title="toggle" styleClass="tinybutton"
				    styleId="tab-${tabKey}-imageToggle"
				    onclick="javascript: return toggleTab(document, '${tabKey}'); " />
		    </c:if> 
		    <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			    <html:image
	  			    property="methodToCall.toggleTab.tab${tabKey}"
				    src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif"
				    alt="show" title="toggle" styleClass="tinybutton"
				    styleId="tab-${tabKey}-imageToggle"
				    onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			</c:if>
			</td>
		</tr>

		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			<tbody style="display: none;" id="tab-${tabKey}-div">
		</c:if>

		<tr>			
				<td class="infoline" nowrap="nowrap">
						<html:hidden property="document.item[${ctr}].itemTypeCode" />
					    <html:hidden property="document.item[${ctr}].receivingLineItemIdentifier" />
					    <html:hidden property="document.item[${ctr}].versionNumber" /> 
					    &nbsp;<b><html:hidden write="true" property="document.item[${ctr}].itemLineNumber" /></b>&nbsp; 
					</td>		
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemCatalogNumber}"
				    property="document.item[${ctr}].itemCatalogNumber"
				    extraReadOnlyProperty="document.item[${ctr}].itemCatalogNumber"
				    readOnly="${true}" />
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemDescription}"
				    property="document.item[${ctr}].itemDescription"
				    readOnly="${true}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemOrderedQuantity}"
				    property="document.item[${ctr}].itemOrderedQuantity"
				    readOnly="${true}" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
				    property="document.item[${ctr}].itemUnitOfMeasureCode"
				    readOnly="${true}" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReceivedPriorQuantity}"
				    property="document.item[${ctr}].itemReceivedPriorQuantity"
				    readOnly="${true}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReceivedToBeQuantity}"
				    property="document.item[${ctr}].itemReceivedToBeQuantity"
				    readOnly="${true}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReceivedTotalQuantity}"
				    property="document.item[${ctr}].itemReceivedTotalQuantity"
				    readOnly="${not (fullEntryMode)}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReturnedTotalQuantity}"
				    property="document.item[${ctr}].itemReturnedTotalQuantity"
				    readOnly="${not (fullEntryMode)}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemDamagedTotalQuantity}"
				    property="document.item[${ctr}].itemDamagedTotalQuantity"
				    readOnly="${not (fullEntryMode)}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReasonAddedCode}"
				    property="document.item[${ctr}].itemReasonAddedCode"
				    readOnly="${not (fullEntryMode) or itemLine.itemLineNumber != null}" />
			</td>

			<c:if test="${(fullEntryMode or (itemLine.itemLineNumber == null))}">
			<td class="infoline">
			    <div align="center">
			        <html:image
				        property="methodToCall.deleteItem.line${ctr}"
				        src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
				        alt="Delete Item ${ctr+1}" title="Delete Item ${ctr+1}"
				        styleClass="tinybutton" />
				</div>
			</td>
			</c:if>
		</tr>

		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			</tbody>
		</c:if>

		</logic:iterate>
		
	</table>
	</div>
</kul:tab>