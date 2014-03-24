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
<%@ attribute name="camsItemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="isRequisition" required="false" description="Determines if this is a requisition document"%>
<%@ attribute name="isPurchaseOrder" required="false" description="Determines if this is a requisition document"%>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && not empty KualiForm.editingMode['allowCapitalAssetEdit']}" />
<c:set var="tabindexOverrideBase" value="60" />
<c:set var="availabilityOnce" value="${PurapConstants.CapitalAssetAvailability.ONCE}"/>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Capital Asset Items">
	<tr>
		<td colspan="12" class="subhead">Capital Asset Items</td>
	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" hideRequiredAsterisk="true"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchasingCommodityCode}" nowrap="true" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" nowrap="true"/>				
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" nowrap="true" />
		<c:if test="${isRequisition}">
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}" nowrap="true" />
		</c:if>
	</tr>

<logic:iterate indexId="ctr" name="KualiForm" property="document.purchasingCapitalAssetItems" id="itemLine">

	<tr>
        <td class="infoline" rowspan="2" valign="middle" align="middle">
        	<b>${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemLineNumber}</b>
        </td>
		<td class="infoline">
			${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemType.itemTypeDescription}
	    </td>
		<td class="infoline">
		    ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemQuantity}
	    </td>
		<td class="infoline">
		    ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemUnitOfMeasureCode}
	    </td>
		<td class="infoline">
		    ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemCatalogNumber}
	    </td>
        <td class="infoline">	
            ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.commodityCode.commodityDescription}
		</td>			    
		<td class="infoline">
		   ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemDescription}
	    </td>
		<td class="infoline">
		    <div align="right">
		        ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemUnitPrice}
			</div>
		</td>
		<td class="infoline">
			<div align="right">
				${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.extendedPrice}
			</div>
		</td>
		<c:if test="${isRequisition}">
		<td class="infoline">
			<div align="center">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemRestrictedIndicator" readOnly="true" />
			</div>
		</td>
		</c:if>		
	</tr>

	<!-- Cams Tab -->
    <c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="tabTitle" value="CamsLines-${currentTabIndex}" />
    <c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
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
	
    <c:if test="${not isPurchaseOrder}">
    	<c:set var="itemActive" value="true"/>
    </c:if>    
	
	<c:if test="${isPurchaseOrder}">
    	<c:set var="itemActive" value="${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemActiveIndicator}"/>
    </c:if>
        
	<tr>
	<td class="infoline" valign="middle" colspan="10">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<th colspan="10" style="padding: 0px; border-right: none;">
		    <div align=left>
		  	    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
			         <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
				</c:if>
			    <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			         <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			    </c:if>
			    Capital Asset
			</div>
			</th>
	    </tr>
	
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			<tr style="display: none;"  id="tab-${tabKey}-div">
		</c:if>  
	        <th colspan="10" style="padding:0;">
				<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
		            <th width="20%" align=right valign=middle class="bord-l-b">
		               <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsItemAttributes.capitalAssetTransactionTypeCode}" /></div>
		            </th>
				    <td class="datacell">
				    <c:choose>
							<c:when test="${!empty KualiForm.document.purchasingCapitalAssetItems and ( (KualiForm.purchasingItemCapitalAssetAvailability eq availabilityOnce) or (KualiForm.purchasingCapitalAssetSystemCommentsAvailability eq availabilityOnce) or (KualiForm.purchasingCapitalAssetSystemDescriptionAvailability eq availabilityOnce) or (KualiForm.purchasingCapitalAssetSystemAvailability eq availabilityOnce) )}">
								<kul:htmlControlAttribute attributeEntry="${camsItemAttributes.capitalAssetTransactionTypeCode}" 									
									property="document.purchasingCapitalAssetItems[${ctr}].capitalAssetTransactionTypeCode"
									extraReadOnlyProperty="document.purchasingCapitalAssetItems[${ctr}].capitalAssetTransactionType.capitalAssetTransactionTypeDescription" 
									readOnly="${!itemActive or !(fullEntryMode or amendmentEntry)}" 
									tabindexOverride="${tabindexOverrideBase + 9}"/>
							</c:when>
							<c:otherwise>
								<kul:htmlControlAttribute attributeEntry="${camsItemAttributes.capitalAssetTransactionTypeCode}"									 
									property="document.purchasingCapitalAssetItems[${ctr}].capitalAssetTransactionTypeCode" 
									extraReadOnlyProperty="document.purchasingCapitalAssetItems[${ctr}].capitalAssetTransactionType.capitalAssetTransactionTypeDescription"
									readOnly="${!itemActive or !(fullEntryMode or amendmentEntry)}" 
									tabindexOverride="${tabindexOverrideBase + 0}"/>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				</table>
				<purap:camsDetail ctr="${ctr}" camsItemIndex="${ctr}" camsSystemAttributes="${camsSystemAttributes}" camsAssetAttributes="${camsAssetAttributes}" camsLocationAttributes="${camsLocationAttributes}" camsAssetSystemProperty="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem" availability="${PurapConstants.CapitalAssetAvailability.EACH}" isRequisition="${isRequisition}" isPurchaseOrder="${isPurchaseOrder}" poItemInactive="${not itemActive}"/>
	        </th>    
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
		    </tr>
		</c:if>
	
		</table>
	</td>
	</tr>
</logic:iterate>
</table>
