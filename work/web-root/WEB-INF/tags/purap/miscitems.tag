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
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<tr>
    <td colspan="10" class="subhead">
        <span class="subhead-left">Misc Items</span>
    </td>
</tr>
<tr>
    <kul:htmlAttributeHeaderCell colspan="5" attributeEntry="${itemAttributes.itemTypeCode}" />
    <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
    <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" />
    <kul:htmlAttributeHeaderCell colspan="2" literalLabel="&nbsp;" />
</tr>
<logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
    <%-- to ensure order this should pull out items from APC instead of this--%>
    <c:if test="${itemLine.itemType.itemTypeAboveTheLineIndicator != true}">
        <tr>
            <td colspan="10" class="tab-subhead" style="border-right: none;">
                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemType.itemTypeDescription" readOnly="${true}" />
                <!-- TODO need the show/hide? -->
            </td>
        </tr>
        <tr>
            <td class="infoline" colspan="5">
                <html:hidden property="document.item[${ctr}].itemIdentifier" />
                <html:hidden property="document.item[${ctr}].versionNumber" />
                <html:hidden property="document.item[${ctr}].itemTypeCode" />
                <html:hidden property="document.item[${ctr}].itemType.itemTypeCode" />
                <html:hidden property="document.item[${ctr}].itemType.itemTypeDescription" />
                <html:hidden property="document.item[${ctr}].itemType.active" />
                <html:hidden property="document.item[${ctr}].itemType.quantityBasedGeneralLedgerIndicator" />
                <html:hidden property="document.item[${ctr}].itemType.itemTypeAboveTheLineIndicator" />
                <div align="right">
                    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemType.itemTypeDescription" readOnly="${true}" />
                </div>
            </td>
            <td class="infoline">
                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" readOnly="${not fullEntryMode}" />
            </td>
            <td class="infoline">
                &nbsp;
            </td>
            <td class="infoline">
                <div align="right">
                    <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" readOnly="${not fullEntryMode}" />
                </div>
            </td>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" colspan="2" />
        </tr>

        <purap:puraccountingLineCams 
            editingMode="${KualiForm.editingMode}" 
            editableAccounts="${KualiForm.editableAccounts}" 
            sourceAccountingLinesOnly="true" 
            optionalFields="accountLinePercent"
            extraHiddenFields=",accountIdentifier,itemIdentifier" 
            accountingLineAttributes="${accountingLineAttributes}" 
            accountPrefix="document.item[${ctr}]." 
            hideTotalLine="true" 
            hideFields="amount" 
            accountingAddLineIndex="${ctr}" 
            suppressCams="${true}" />

    </c:if>
</logic:iterate>

