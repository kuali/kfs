<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin"%>
<%@ taglib prefix="purap" tagdir="/WEB-INF/tags/purap"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>

<%@ attribute name="itemsAttributes" required="true" type="java.util.Map" description="A parameter to specify an data dictionary entry for items to get cams fields."%>
<%@ attribute name="camsAttributes" required="true" type="java.util.Map" description="A parameter to specify an data dictionary entry for a sub-classed cams data."%>
<%@ attribute name="ctr" required="true" description="item count"%>


<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
<c:set var="currentTab" value="${KualiForm.tabStateJstl}" />
<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />

<%-- default to closed --%>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="false" />
    </c:when>
    <c:when test="${!empty currentTab}">
        <c:set var="isOpen" value="${currentTab.open}" />
    </c:when>
</c:choose>

<html:hidden property="tabState[${currentTabIndex}].open" value="${isOpen}" />

<table cellpadding="0" cellspacing="0" class="datatable" >
    <tr>
        <td colspan="4" class="subhead">
            <span class="subhead-left">CAMS Detail
                <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
                    <html:image property="methodToCall.toggleTab.tab${currentTabIndex}" src="images/tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${currentTabIndex}-imageToggle" onclick="javascript: return toggleTab(document, ${currentTabIndex}); " />
                </c:if>
                <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                    <html:image property="methodToCall.toggleTab.tab${currentTabIndex}" src="images/tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${currentTabIndex}-imageToggle" onclick="javascript: return toggleTab(document, ${currentTabIndex}); " />
                </c:if>
            </span>
        </td>
    </tr>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
    <tbody style="display: none;" id="tab-${currentTabIndex}-div">
</c:if>

    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${itemsAttributes.capitalAssetTransactionTypeCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute attributeEntry="${itemsAttributes.capitalAssetTransactionTypeCode}" property="document.item[${ctr}].capitalAssetTransactionTypeCode" readOnly="${not (fullEntryMode or amendmentEntry)}" />
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsAttributes.addCapitalAssetNumber}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <!-- kul:htmlControlAttribute attributeEntry="${camsAttributes.addCapitalAssetNumber}" property="document.addCapitalAssetNumber" readOnly="${not (fullEntryMode or amendmentEntry)}" / -->&nbsp;
            <html:image property="methodToCall.addAsset" src="images/tinybutton-add1.gif" alt="Insert an Asset" title="Add an Asset" styleClass="tinybutton" />
        </td>
    </tr>

    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${itemsAttributes.itemCapitalAssetNoteText}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute attributeEntry="${itemsAttributes.itemCapitalAssetNoteText}" property="document.item[${ctr}].itemCapitalAssetNoteText" readOnly="${not (fullEntryMode or amendmentEntry)}" />
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsAttributes.capitalAssetNumber}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <!-- TODO add logic to loop through assets on item -->
            <!-- for:each -->
                <!-- kul:htmlControlAttribute attributeEntry="${camsAttributes.addCapitalAssetNumber}" property="document.addCapitalAssetNumber" readOnly="${not (fullEntryMode or amendmentEntry)}" / -->&nbsp;
                <html:image property="methodToCall.deleteAsset" src="images/tinybutton-delete1.gif" alt="Remove an Asset" title="Delete an Asset" styleClass="tinybutton" />
            <!-- /for:each -->
        </td>
    </tr>

<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
    </tbody>
</c:if>

</table>
            




