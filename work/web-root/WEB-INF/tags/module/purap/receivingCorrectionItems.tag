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

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentTypeName}" />
<c:set var="tabindexOverrideBase" value="10" />

<kul:tab tabTitle="Items" defaultOpen="true" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
		<tr>
			<td colspan="12" class="subhead">
			    <span class="subhead-left">Receiving Correction Items</span>
			</td>
		</tr>

		<tr>
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />				
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemOriginalReceivedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemOriginalReturnedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemOriginalDamagedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReceivedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemReturnedTotalQuantity}" />
			<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDamagedTotalQuantity}" />
		</tr>
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
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemLineNumber}"
				    property="document.item[${ctr}].itemLineNumber"
				    extraReadOnlyProperty="document.item[${ctr}].itemLineNumber"
				    readOnly="${true}" />			    
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
				    attributeEntry="${itemAttributes.itemUnitOfMeasureCode}"
				    property="document.item[${ctr}].itemUnitOfMeasureCode"
				    readOnly="${true}" />
		    </td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemOriginalReceivedTotalQuantity}"
				    property="document.item[${ctr}].itemOriginalReceivedTotalQuantity"
				    readOnly="${true}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemOriginalReturnedTotalQuantity}"
				    property="document.item[${ctr}].itemOriginalReturnedTotalQuantity"
				    readOnly="${true}" />
			</td>
			<td class="infoline">
			    <kul:htmlControlAttribute
				    attributeEntry="${itemAttributes.itemOriginalDamagedTotalQuantity}"
				    property="document.item[${ctr}].itemOriginalDamagedTotalQuantity"
				    readOnly="${true}" />
			</td>
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
		</tr>

		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			</tbody>
		</c:if>

		</logic:iterate>
		
	</table>
	</div>
</kul:tab>
