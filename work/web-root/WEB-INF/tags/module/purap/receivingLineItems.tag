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

<script language="JavaScript" type="text/javascript" src="dwr/interface/ItemUnitOfMeasureService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/purap/objectInfo.js"></script>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentTypeName}" />
<c:set var="colCount" value="12"/>

<c:if test="${KualiForm.stateFinal}">
	<c:set var="colCount" value="10"/>
</c:if>
<c:set var="tabindexOverrideBase" value="20" />

<kul:tab tabTitle="Items" defaultOpen="true" tabErrorKey="${PurapConstants.LINEITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
		<tr>
			<td colspan="${colCount}" class="subhead">
			    <span class="subhead-left">Receiving Line Items</span>
			</td>
		</tr>

		<c:if test="${fullEntryMode}">
		<tr>
			<td colspan="${colCount}" class="datacell" align="right" nowrap="nowrap">
				<div align="right">
					<c:if test="${KualiForm.ableToShowClearAndLoadQtyButtons}">
						<html:image property="methodToCall.loadQty" src="${ConfigProperties.externalizable.images.url}tinybutton-loadqtyreceived.gif" alt="load qty received" title="load qty received" styleClass="tinybutton" />
						<html:image property="methodToCall.clearQty" src="${ConfigProperties.externalizable.images.url}tinybutton-clearqtyreceived.gif" alt="clear qty received" title="clear qty received" styleClass="tinybutton" />
					</c:if>
					<c:if test="${KualiForm.hideAddUnorderedItem}">
						<html:image property="methodToCall.showAddUnorderedItem"
							src="${ConfigProperties.externalizable.images.url}tinybutton-addunorditem.gif"
							alt="add unordered item" title="add unordered item"
							styleClass="tinybutton" />
					</c:if>
					<c:if test="${!KualiForm.hideAddUnorderedItem}">
						<img src="${ConfigProperties.externalizable.images.url}tinybutton-addunorditem-grey.gif"
							alt="add unordered item" border="0" styleClass="tinybutton" />
					</c:if>
				</div>
			</td>
		</tr>
		</c:if>
		
		<!--  Column Names -->
		<tr>
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemOrderedQuantity}" />						
			<kul:htmlAttributeHeaderCell><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" useShortLabel="true"/></kul:htmlAttributeHeaderCell>

			<c:if test="${KualiForm.stateFinal == false}">
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedPriorQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedToBeQuantity}" />		
			</c:if>

			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReturnedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDamagedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReasonAddedCode}" />
			<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
		</tr>
		
		<!--  New Receiving Line Item -->
		<c:if test="${fullEntryMode and !KualiForm.hideAddUnorderedItem}">
		<tr>
            <td class="infoline">
                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemLineNumber}" property="newLineItemReceivingItemLine.itemLineNumber" readOnly="${true}"/>
            </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemCatalogNumber}" property="newLineItemReceivingItemLine.itemCatalogNumber" tabindexOverride="${tabindexOverrideBase + 0}"/>
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="newLineItemReceivingItemLine.itemDescription" tabindexOverride="${tabindexOverrideBase + 0}"/>
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemOrderedQuantity}" property="newLineItemReceivingItemLine.itemOrderedQuantity" readOnly="${true}"/>
		    </td>
			<td class="infoline" nowrap="nowrap">
                <c:set var="itemUnitOfMeasureCodeField"  value="newLineItemReceivingItemLine.itemUnitOfMeasureCode" />
                <c:set var="itemUnitOfMeasureDescriptionField"  value="newLineItemReceivingItemLine.itemUnitOfMeasure.itemUnitOfMeasureDescription" />
                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" 
                    property="${itemUnitOfMeasureCodeField}" 
                    onblur="loadItemUnitOfMeasureInfo( '${itemUnitOfMeasureCodeField}', '${itemUnitOfMeasureDescriptionField}' );${onblur}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.UnitOfMeasure" 
                    fieldConversions="itemUnitOfMeasureCode:newLineItemReceivingItemLine.itemUnitOfMeasureCode"
                    lookupParameters="'Y':active"/>     
                <div id="newLineItemReceivingItemLine.itemUnitOfMeasure.itemUnitOfMeasureDescription.div" class="fineprint">
                    <html:hidden write="true" property="${itemUnitOfMeasureDescriptionField}"/>&nbsp;        
                </div>     
            </td>        

			<c:if test="${KualiForm.stateFinal == false}">

			<td class="infoline">
			    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedPriorQuantity}" property="newLineItemReceivingItemLine.itemReceivedPriorQuantity" readOnly="${true}"/>
		    </td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedToBeQuantity}" property="newLineItemReceivingItemLine.itemReceivedToBeQuantity" readOnly="${true}"/>
			</td>
			</c:if>
			
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReceivedTotalQuantity}" property="newLineItemReceivingItemLine.itemReceivedTotalQuantity" tabindexOverride="${tabindexOverrideBase + 0}"/>
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReturnedTotalQuantity}" property="newLineItemReceivingItemLine.itemReturnedTotalQuantity" tabindexOverride="${tabindexOverrideBase + 0}"/>
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDamagedTotalQuantity}" property="newLineItemReceivingItemLine.itemDamagedTotalQuantity" tabindexOverride="${tabindexOverrideBase + 0}"/>
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemReasonAddedCode}" property="newLineItemReceivingItemLine.itemReasonAddedCode" tabindexOverride="${tabindexOverrideBase + 0}"/>
			</td>

			<td class="infoline">
			    <div align="center">
			        <html:image property="methodToCall.addItem" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item" title="Add an Item" styleClass="tinybutton" />
			    </div>
			</td>						
		</tr>
		</c:if>
		
		<!-- Existing Receiving Line Items -->
		<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
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
					<c:set var="isOpen" value="true" />
				</c:when>
				<c:when test="${!empty currentTab}">
					<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
				</c:when>
			</c:choose>

		<tr>
			<td colspan="${colCount}" class="tab-subhead" style="border-right: none;">				 
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
				    &nbsp;<b><bean:write name="KualiForm" property="document.item[${ctr}].itemLineNumber"/></b>&nbsp; 
			</td>		
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemCatalogNumber}"
				    property="document.item[${ctr}].itemCatalogNumber"
				    extraReadOnlyProperty="document.item[${ctr}].itemCatalogNumber"
				    readOnly="${((itemLine.itemTypeCode eq 'ITEM') or not (fullEntryMode))}" />
			</td>
			<td class="infoline">
				<kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemDescription}"
				    property="document.item[${ctr}].itemDescription"
				    readOnly="${((itemLine.itemTypeCode eq 'ITEM') or not (fullEntryMode))}" />
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
	                onblur="loadItemUnitOfMeasureInfo( 'document.item[${ctr}].itemUnitOfMeasureCode', 'document.item[${ctr}].itemUnitOfMeasure.itemUnitOfMeasureDescription' );${onblur}"
	                readOnly="${((itemLine.itemTypeCode eq 'ITEM') or not (fullEntryMode))}"
	                tabindexOverride="${tabindexOverrideBase + 0}"/>
	                <c:if test="${((!itemLine.itemTypeCode eq 'ITEM') && fullEntryMode)}">
	                	<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.UnitOfMeasure" 
                                fieldConversions="itemUnitOfMeasureCode:document.item[${ctr}].itemUnitOfMeasureCode"
                                lookupParameters="'Y':active"/>
                    </c:if> 
	            	<div id="document.item[${ctr}].itemUnitOfMeasure.itemUnitOfMeasureDescription.div" class="fineprint">
	                	<html:hidden write="true" property="document.item[${ctr}].itemUnitOfMeasure.itemUnitOfMeasureDescription"/>&nbsp;  
	            	</div>   
	            	
		    </td>

			<c:if test="${KualiForm.stateFinal == false}">
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
			</c:if>
			
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReceivedTotalQuantity}"
				    property="document.item[${ctr}].itemReceivedTotalQuantity"
				    readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReturnedTotalQuantity}"
				    property="document.item[${ctr}].itemReturnedTotalQuantity"
				    readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemDamagedTotalQuantity}"
				    property="document.item[${ctr}].itemDamagedTotalQuantity"
				    readOnly="${not (fullEntryMode)}" tabindexOverride="${tabindexOverrideBase + 0}"/>
			</td>
			<td class="infoline">				
				<kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemReasonAddedCode}"	
				    property="document.item[${ctr}].itemReasonAddedCode"
				    extraReadOnlyProperty="document.item[${ctr}].itemReasonAdded.itemReasonAddedDescription"			    				    
				    readOnly="${not (fullEntryMode) or itemLine.itemLineNumber != null}" tabindexOverride="${tabindexOverrideBase + 0}"/>					
			</td>
			<td class="infoline">
			    &nbsp;
			</td>
		</tr>

		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			</tbody>
		</c:if>

		</logic:iterate>
		
	</table>
	</div>
</kul:tab>
