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
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>

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
                forceRequired="true"
                /></span>&nbsp;<kul:htmlControlAttribute
                property="${accountingLine}.${overrideField}"
                attributeEntry="${attributes[overrideField]}"
                readOnly="${readOnly}"
                />
        </span>
    </c:when>
    <c:otherwise>
        <fin:hiddenAccountingLineField
            accountingLine="${accountingLine}"
            isBaseline="false"
            hiddenField="${overrideField}"
            displayHidden="${displayHidden}"
            value="${!readOnly && overrideNeeded == 'No' ? 'No' : override}"
            />
    </c:otherwise>
</c:choose>
<fin:hiddenAccountingLineField
    accountingLine="${accountingLine}"
    isBaseline="false"
    hiddenField="${overrideNeededField}"
    displayHidden="${displayHidden}"
    />
<c:if test="${!empty baselineAccountingLine}">
    <%-- Add lines have no baseline. --%>
    <fin:hiddenAccountingLineField
        accountingLine="${baselineAccountingLine}"
        isBaseline="true"
        hiddenField="${overrideField}"
        displayHidden="${displayHidden}"
        />
    <fin:hiddenAccountingLineField
        accountingLine="${baselineAccountingLine}"
        isBaseline="true"
        hiddenField="${overrideNeededField}"
        displayHidden="${displayHidden}"
        />
</c:if>
