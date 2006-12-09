<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>

<%@ attribute name="dataCellCssClass" required="true" description="The name of the CSS class for this data cell. This is used to distinguish the look of the add row from the already-added rows." %>
<%@ attribute name="actionGroup" required="true" description="The name of the group of action buttons to be displayed; valid values are newLine, newGroupLine, and existingLine." %>
<%@ attribute name="actionInfix" required="true" description="Infix used to build method names which will be invoked by the buttons in this actionGroup, e.g., Source or Target" %>
<%@ attribute name="accountingAddLineIndex" required="false" description="index for multiple add new source lines, required for the newGroupLine actionGroup" %>
<%@ attribute name="accountingLineIndex" required="false" description="index of this accountingLine in the corresponding form list, required for the existingLine actionGroup" %>
<%@ attribute name="decorator" required="false" description="propertyName of the AccountingLineDecorator associated with this accountingLine, required for the existingLine actionGroup" %>

<c:choose>
    <c:when test="${actionGroup == 'newLine' || actionGroup == 'newGroupLine'}" >
        <c:set var="insertMethod" value="insert${actionInfix}Line" />
        <c:if test="${actionGroup == 'newGroupLine'}" >
            <c:set var="insertMethod" value="${insertMethod}.line${accountingAddLineIndex}" />
        </c:if>

        <td class="${dataCellCssClass}" nowrap><div align="center">
            <html:image property="methodToCall.${insertMethod}.anchoraccounting${actionInfix}Anchor" src="images/tinybutton-add1.gif" title="Add an Accounting Line" alt="Add an Accounting Line" styleClass="tinybutton"/>
            <fin:accountingLineDataCellDetail/></div>
        </td>
    </c:when>

    <c:when test="${actionGroup == 'existingLine'}" >
        <c:set var="revertible">
            <bean:write name="KualiForm" property="${decorator}.revertible" />
        </c:set>
        <c:set var="deleteMethod" value="delete${actionInfix}Line.line${accountingLineIndex}" />
        <c:set var="revertMethod" value="revert${actionInfix}Line.line${accountingLineIndex}" />
        <c:set var="balanceInquiryMethod" value="performBalanceInquiryFor${actionInfix}Line.line${accountingLineIndex}" />

        <td class="${dataCellCssClass}" nowrap>
            <div align="center">
                <%-- persist accountingLineDecorator --%>
                <html:hidden name="KualiForm" property="${decorator}.revertible" />

                <html:image property="methodToCall.${deleteMethod}.anchoraccounting${actionInfix}Anchor" src="images/tinybutton-delete1.gif" title="Delete Accounting Line ${accountingLineIndex+1}" alt="Delete Accounting Line ${accountingLineIndex+1}" styleClass="tinybutton"/>
                <c:if test="${revertible}">
                    <br>
                    <html:image property="methodToCall.${revertMethod}.anchoraccounting${actionInfix}${actionGroup}LineAnchor${0 + accountingLineIndex}" src="images/tinybutton-revert1.gif" title="Revert Accounting Line ${accountingLineIndex+1}" alt="Revert Accounting Line ${accountingLineIndex+1}" styleClass="tinybutton"/>
                </c:if>
                <br>
                <html:image property="methodToCall.${balanceInquiryMethod}.anchoraccounting${actionInfix}${actionGroup}LineAnchor${0 + accountingLineIndex}" src="images/tinybutton-balinquiry.gif" title="Balance Inquiry For Line ${accountingLineIndex+1}" alt="Balance Inquiry For Line ${accountingLineIndex+1}" styleClass="tinybutton" />
            </div>
        </td>
    </c:when>
</c:choose>
