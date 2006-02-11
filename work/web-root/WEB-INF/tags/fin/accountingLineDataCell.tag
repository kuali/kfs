<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>

<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell." %>
<%@ attribute name="cellAlign" required="true"
              description="The alignment attribute for this data cell." %>
<%@ attribute name="field" required="true"
              description="The name of the field of the accounting line being edited or displayed by this cell.
              Combined with the accountingLine, this identifies the value (i.e., the data) of this cell." %>
<%@ attribute name="attributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for the field in this cell." %>
<%@ attribute name="readOnly" required="true" %>

<%@ attribute name="accountingLine" required="false"
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

<%@ attribute name="cellProperty" required="false"
              description="This is the name in the form of the property
              being edited or displayed by this cell.
              Required if accountingLine attribute is not given." %>

<%@ attribute name="detailFunction" required="false"
              description="The name of the JavaScript function to asynchronously
              update the detailed description of the value in this data cell on blur.
              This attribute requires the detailField attribute." %>
<%@ attribute name="detailFunctionExtraParam" required="false"
              description="The value of an extra parameter required by some of the detail JavaScript functions." %>
<%@ attribute name="detailField" required="false"
              description="The name of the field in the business object containing the detail to be displayed." %>

<%@ attribute name="boClassName" required="false"
              description="The name of the business object class to perform a lookup.
              This attribute requires the briefLookupParameters and conversionField attributes." %>
<%@ attribute name="briefLookupParameters" required="false"
              description="A comma-separated list of pairs of names for  generating the lookup request.
              Each pair is separated by a colon. The left side of the pair is the name of a field in the
              accounting line being edited by the row containing this cell. The right side of the pair
              is the name of the corresponding field in the named business object.
              This attribute requires the boClassName and conversionField attributes." %>
<%@ attribute name="conversionField" required="false"
              description="The name of the field in the business object to be returned from the lookup.
              The value of this data cell becomes the value of this field.
              This attribute requires the boClassName and briefLookupParameters attributes." %>

<%@ attribute name="labelFontWeight" required="false"
              description="The font weight for the in-cell label, e.g., bold or normal.  Providing this
              attribute causes the cell to render a label for the field, span 2 columns,  and surpress
              the detail line.  This attribute is provided for cells on extra rows.  Changing the weight
              allows the labels in the 'add' row to correspond to the weight of the column headers." %>

<%@ attribute name="displayHidden" required="false"
              description="display hidden values (for debugging)." %>

<%@ attribute name="overrideField" required="false"
              description="base name of the accountingLine field to check and display if needed." %>

<c:set var="qualifiedField" value="${accountingLine}.${field}"/>
<c:if test="${empty cellProperty}">
    <c:set var="cellProperty" value="${qualifiedField}"/>
</c:if>
<c:set var="columnCount" value="${empty labelFontWeight ? 1 : 2}"/>
<c:set var="useXmlHttp" value="${(!readOnly) && (!empty detailFunction)}" />
<%-- test to see if we are dealing with the extra JV fields here --%>
<c:set var="specialRequiredField" value="${(field eq 'referenceOriginCode') || (field eq 'referenceNumber') || (field eq 'referenceTypeCode')}" />
<td class="${dataCellCssClass}" valign="top" colspan="${columnCount}">
<span class="nowrap">
    <c:if test="${!empty labelFontWeight}">
        <span style="font-weight: ${labelFontWeight}"><kul:htmlAttributeLabel attributeEntry="${attributes[field]}" useShortLabel="true" forceRequired="${specialRequiredField}" /></span>
        <%-- The following nbsp has breakable space around it, but nevertheless it accomplishes something,
            by preventing this label from touching its input control (consistent with the cellpadding for
            labels that are not in the same cell as their input control).  --%>
        &nbsp;
    </c:if>

    <%-- data cell (with and without XMLHTTP-invoking javascript) --%>
    <c:if test="${!useXmlHttp}">
        <kul:htmlControlAttribute
            property="${cellProperty}"
            attributeEntry="${attributes[field]}"
            readOnly="${readOnly}"
            />
    </c:if>
    <c:if test="${useXmlHttp}">
        <kul:htmlControlAttribute
            property="${cellProperty}"
            attributeEntry="${attributes[field]}"
            onblur="${detailFunction}(${detailFunctionExtraParam} this.name, '${accountingLine}.${detailField}');"
            readOnly="${readOnly}"
            />
    </c:if>

    <%-- lookup control --%>
    <c:if test="${!readOnly}">
        <c:if test="${!empty boClassName}">
            <%-- todo: this lookup to field conversion swapping in performLookup() or lookup.tag --%>
            <c:set var="lookupParameters" value=""/>
            <c:set var="fieldConversions" value=""/>
            <c:forTokens var="mappedPair" items="${briefLookupParameters}" delims=",">
                <c:set var="split" value="${fn:split(mappedPair,':')}"/>
                <c:set var="withAccountingLine" value="${accountingLine}.${split[0]}"/>
                <c:if test="${!empty lookupParameters}">
                    <c:set var="lookupParameters" value="${lookupParameters},"/>
                </c:if>
                <c:set var="lookupParameters" value="${lookupParameters}${withAccountingLine}:${split[1]}"/>
                <c:set var="fieldConversions" value="${fieldConversions}${split[1]}:${withAccountingLine},"/>
            </c:forTokens>
            <kul:lookup
                boClassName="org.kuali.module.chart.bo.${boClassName}"
                fieldConversions="${fieldConversions}${conversionField}:${qualifiedField}"
                lookupParameters="${lookupParameters}"
                />
        </c:if>
    </c:if>
</span>
<c:if test="${!empty overrideField}">
    <span class="nowrap">
        <c:set var="overrideNeededField" value="${overrideField}Needed"/>
        <bean:define
            id="overrideNeeded"
            property="${accountingLine}.${overrideNeededField}"
            name="KualiForm"
            />
        <c:choose>
            <c:when test="${overrideNeeded == 'Yes'}">  <%-- case sensitive.  Why is this attribute being String-ified? --%>
                <br/>
                <span style="font-weight: normal"><kul:htmlAttributeLabel
                    attributeEntry="${attributes[overrideField]}"
                    useShortLabel="true"
                    forceRequired="true"
                    /></span>&nbsp;<kul:htmlControlAttribute
                    property="${accountingLine}.${overrideField}"
                    attributeEntry="${attributes[overrideField]}"
                    readOnly="${readOnly}"
                    />
            </c:when>
            <c:otherwise>
                <fin:hiddenAccountingLineField
                    accountingLine="${accountingLine}"
                    isBaseline="false"
                    hiddenField="${overrideField}"
                    displayHidden="${displayHidden}"
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
                hiddenField="${overrideNeededField}"
                displayHidden="${displayHidden}"
                />
            <fin:hiddenAccountingLineField
                accountingLine="${baselineAccountingLine}"
                isBaseline="true"
                hiddenField="${overrideField}"
                displayHidden="${displayHidden}"
                />
        </c:if>
    </span>
</c:if>
    <c:if test="${empty labelFontWeight}">
        <fin:accountingLineDataCellDetail
            detailField="${detailField}"
            accountingLine="${accountingLine}"
            />
    </c:if>
    <c:if test="${!empty baselineAccountingLine}">
        <fin:hiddenAccountingLineField
            accountingLine="${baselineAccountingLine}"
            isBaseline="true"
            hiddenField="${field}"
            displayHidden="${displayHidden}"
            />
    </c:if>
</td>
