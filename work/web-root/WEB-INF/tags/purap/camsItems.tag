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
<%@ attribute name="camsItemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<c:set var="lockCamsEntry" value="${(not empty KualiForm.editingMode['lockCamsEntry'])}" />

    <table cellpadding="0" cellspacing="0" class="datatable" summary="CAMS Items">	
	<tr>
		<td colspan="12" class="subhead">CAMS Items</td>
	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
		<kul:htmlAttributeHeaderCell><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" useShortLabel="true" /></kul:htmlAttributeHeaderCell>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.commodityCode}" nowrap="true" />
		<kul:htmlAttributeHeaderCell><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemDescription}"/></kul:htmlAttributeHeaderCell>
		<kul:htmlAttributeHeaderCell nowrap="true"><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitPrice}"/></kul:htmlAttributeHeaderCell>				
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" nowrap="true" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}" nowrap="true" />
	</tr>

<logic:iterate indexId="ctr" name="KualiForm" property="document.purchasingCapitalAssetItems" id="itemLine">


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
		    Item ${ctr+1}
		</td>
	</tr>

	<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
		<tbody style="display: none;" id="tab-${tabKey}-div">
	</c:if>

	<tr>
        <td class="infoline" rowspan="2" valign="middle">
        	<b><kul:htmlControlAttribute attributeEntry="${itemAttributes.itemLineNumber}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemLineNumber" readOnly="${true}"/></b>
        </td>
		<td class="infoline">
		    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemType.itemTypeDescription" readOnly="${true}"/>
	    </td>
		<td class="infoline">
		    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemQuantity}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemQuantity" readOnly="${true}"/>
	    </td>
		<td class="infoline">
		    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemUnitOfMeasureCode" readOnly="${true}"/>
	    </td>
		<td class="infoline">
		    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemCatalogNumber}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemCatalogNumber" readOnly="${true}"/>
	    </td>
        <td class="infoline">
            <c:set var="commodityCodeField"  value="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.purchasingCommodityCode" />
            <c:set var="commodityDescriptionField"  value="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.commodityCode.commodityDescription" />
            <kul:htmlControlAttribute attributeEntry="${itemAttributes.commodityCode}" 
                property="${commodityCodeField}" 
                onblur="loadCommodityCodeInfo( '${commodityCodeField}', '${commodityDescriptionField}' );${onblur}" readOnly="${true}" />
		</td>			    
		<td class="infoline">
		   <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemDescription" readOnly="${true}"/>
	    </td>
		<td class="infoline">
		    <div align="right">
		        <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemUnitPrice" readOnly="${true}"/>
			</div>
		</td>
		<td class="infoline">
			    <div align="right">
			        <kul:htmlControlAttribute attributeEntry="${itemAttributes.extendedPrice}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.extendedPrice" readOnly="true"/>
			</div>
		</td>
		<td class="infoline">
			<div align="center">
				<kul:htmlControlAttribute attributeEntry="${itemAttributes.itemRestrictedIndicator}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingItem.itemRestrictedIndicator" readOnly="${true}"/>
			</div>
		</td>		
	</tr>


	<!-- Cams Tab -->
    <c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="tabTitle" value="AccountingLines-${currentTabIndex}" />
    <c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
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
	
    <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

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
			    	CAMs
			    </div>
			</th>
		</tr>
	
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
		    <tr style="display: none;"  id="tab-${tabKey}-div">
		</c:if>   
	        <th colspan="10" style="padding:0;">
				<purap:camsDetail camsItemAttributes="${camsItemAttributes}" ctr="${ctr}" camsSystemAttributes="${camsSystemAttributes}" camsAssetAttributes="${camsAssetAttributes}" camsLocationAttributes="${camsLocationAttributes}" />
	        </th>
	    
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
		    </tr>
		</c:if>
	
		</table>
	</td>
	</tr>

</logic:iterate>
	</table>
