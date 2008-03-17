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

<%@ attribute name="itemsAttributes" required="true" type="java.util.Map" description="A parameter to specify an data dictionary entry for items to get cams fields."%>
<%@ attribute name="camsAttributes" required="true" type="java.util.Map" description="A parameter to specify an data dictionary entry for a sub-classed cams data."%>
<%@ attribute name="ctr" required="true" description="item count"%>

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
<c:set var="tabTitle" value="CAMS-${currentTabIndex}" />
<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
<c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
<!--  hit form method to increment tab index -->
<c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />

<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />

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

<table cellpadding="0" cellspacing="0" class="datatable" >
    <tr>
        <td colspan="4" class="subhead">
            <span class="subhead-left">Capital Asset Detail
                <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
                    <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
                </c:if>
                <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                    <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
                </c:if>
            </span>
        </td>
    </tr>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
    <tbody style="display: none;" id="tab-${tabKey}-div">
</c:if>

    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${itemsAttributes.capitalAssetTransactionTypeCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute attributeEntry="${itemsAttributes.capitalAssetTransactionTypeCode}" 
            	property="document.items[${ctr}].capitalAssetTransactionTypeCode"
            	extraReadOnlyProperty="document.items[${ctr}].capitalAssetTransactionType.capitalAssetTransactionTypeDescription" 
            	readOnly="${not (fullEntryMode or amendmentEntry)}" />
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${itemsAttributes.addCapitalAssetNumber}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute attributeEntry="${itemsAttributes.addCapitalAssetNumber}" property="document.items[${ctr}].addCapitalAssetNumber" readOnly="${not (fullEntryMode or amendmentEntry)}"/>&nbsp;
            <c:if test="${fullEntryMode or amendmentEntry}">
            	<html:image property="methodToCall.addAsset.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Asset" title="Add an Asset" styleClass="tinybutton"/>
            </c:if>
        </td>
    </tr>

    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${itemsAttributes.itemCapitalAssetNoteText}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute attributeEntry="${itemsAttributes.itemCapitalAssetNoteText}" property="document.items[${ctr}].itemCapitalAssetNoteText" readOnly="${not (fullEntryMode or amendmentEntry)}" />
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsAttributes.capitalAssetNumber}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <c:forEach var="capitalAsset" items="${KualiForm.document.items[ctr].purchasingItemCapitalAssets}" varStatus="assetCtr">
                ${capitalAsset.capitalAssetNumber}&nbsp;                
                <html:image property="methodToCall.deleteAsset.line${ctr}.asset${assetCtr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Remove an Asset" title="Delete an Asset" styleClass="tinybutton" /><br/>
            </c:forEach>
            <html:hidden property="purchasingItemCapitalAssets" value="${KualiForm.document.items[ctr].purchasingItemCapitalAssets}" />
        </td>
    </tr>

<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
    </tbody>
</c:if>

</table>
            




