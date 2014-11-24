<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="overrideField" required="true"
              description="base name of the accountingLine field to check and display if needed." %>

<%@ attribute name="attributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for the field in this cell." %>
<%@ attribute name="readOnly" required="true" %>

<%@ attribute name="accountingLine" required="true"
              description="This is normally the name in the form of the accounting line
              being edited or displayed by the row containing this cell.
              Also it is always the key to the DataDictionary attributes entry for editing or displaying.
              Required if the cellProperty attribute is not given,
              or if detailField or boClassName is given." %>

<%@ attribute name="baselineAccountingLine" required="false"
              description="The name in the form of the baseline accounting line
              from before the most recent edit of the row containing the field of this cell,
              to put in a hidden field for comparison or reversion.
              The add lines have no previously accepted version,
              and the JournalVoucher debit and credit fields are in a helper object
              just to display the amount from the AccountingLine, so no baseline for them." %>

<%@ attribute name="displayHidden" required="false"
              description="display hidden values (for debugging)." %>

<c:set var="overrideNeededField" value="${overrideField}Needed"/>
<bean:define id="overrideNeeded" property="${accountingLine}.${overrideNeededField}" name="KualiForm" />
<bean:define id="override" property="${accountingLine}.${overrideField}" name="KualiForm" />
<c:choose>
    <%-- These "Booleans" are case sensitive.  Why are these attributes being String-ified? --%>
    <c:when test="${readOnly ? override == 'Yes' : overrideNeeded == 'Yes'}">
        <br/>
        <span class="nowrap">
            <span style="font-weight: normal"><kul:htmlAttributeLabel
                attributeEntry="${attributes[overrideField]}"
                useShortLabel="true"
                labelFor="${accountingLine}.${overrideField}" forceRequired="true"
                /></span>&nbsp;<kul:htmlControlAttribute
                property="${accountingLine}.${overrideField}"
                attributeEntry="${attributes[overrideField]}"
                readOnly="${readOnly}" forceRequired="true"
                />
        </span>
    </c:when>
    <c:otherwise>
        <bc:hiddenPbglLineField
            accountingLine="${accountingLine}"
            isBaseline="false"
            hiddenField="${overrideField}"
            displayHidden="${displayHidden}"
            value="${!readOnly && overrideNeeded == 'No' ? 'No' : override}"
            />
    </c:otherwise>
</c:choose>
<bc:hiddenPbglLineField
    accountingLine="${accountingLine}"
    isBaseline="false"
    hiddenField="${overrideNeededField}"
    displayHidden="${displayHidden}"
    />
<c:if test="${!empty baselineAccountingLine}">
    <%-- Add lines have no baseline. --%>
    <bc:hiddenPbglLineField
        accountingLine="${baselineAccountingLine}"
        isBaseline="true"
        hiddenField="${overrideField}"
        displayHidden="${displayHidden}"
        />
    <bc:hiddenPbglLineField
        accountingLine="${baselineAccountingLine}"
        isBaseline="true"
        hiddenField="${overrideNeededField}"
        displayHidden="${displayHidden}"
        />
</c:if>
