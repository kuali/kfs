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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="bc" tagdir="/WEB-INF/tags/bc" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>

<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell." %>
<%@ attribute name="dataFieldCssClass" required="false"
              description="The name of the CSS class for this data field." %>
<%@ attribute name="field" required="true"
              description="The name of the field of the accounting line being edited or displayed by this cell.
              Combined with the accountingLine, this identifies the value (i.e., the data) of this cell." %>
<%@ attribute name="attributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for the field in this cell." %>
<%@ attribute name="readOnly" required="true" %>

<%@ attribute name="fieldAlign" required="false"
              description="div alignment. default is align=left" %>

<%@ attribute name="accountingLine" required="false"
              description="This is normally the name in the form of the accounting line
              being edited or displayed by the row containing this cell.
              Also it is always the key to the DataDictionary attributes entry for editing or displaying.
              Required if the cellProperty attribute is not given,
              or if detailField or boClassSimpleName is given." %>

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

<%@ attribute name="lookup" required="false"
              description="Boolean indicating whether this cell should have a lookup icon if it's writable.
              If true, the boClassSimpleName attribute at least is also required." %>
<%@ attribute name="inquiry" required="false"
              description="Boolean indicating whether this cell should have an inquiry link if it's writable.
              If true, the boClassSimpleName attribute at least is also required." %>

<%@ attribute name="boClassSimpleName" required="false"
              description="The simple name of the business object class to perform a lookup or inquiry.
              This does not include the package name." %>
<%@ attribute name="boPackageName" required="false"
              description="The name of the package containing the business object class to perform a lookup or inquiry.
              If this attribute is missing, it defaults to 'org.kuali.module.chart.bo'." %>
<%@ attribute name="conversionField" required="false"
              description="The name of the field in the business object corresponding to
              this cell's field  in the accounting line.
              This may be used to return a lookup value from the BO, or generate an inquiry.
              For a lookup, the value of this data cell becomes the value of this field.
              If not provided, this attribute defaults to the same value as the field attribute." %>

<%@ attribute name="labelFontWeight" required="false"
              description="The font weight for the in-cell label, e.g., bold or normal.  Providing this
              attribute causes the cell to render a label for the field.
              This attribute is provided for cells on extra rows.  Changing the weight
              allows the labels in the 'add' row to correspond to the weight of the column headers." %>

<%@ attribute name="displayHidden" required="false"
              description="display hidden values (for debugging)." %>

<%@ attribute name="overrideField" required="false"
              description="base name of the accountingLine field to check and display if needed." %>
  
<%@ attribute name="lookupOrInquiryKeys" required="false"
              description="comma separated list of inquiry key names in the accountingLineValuesMap" %>
<%@ attribute name="lookupUnkeyedFieldConversions" required="false"
			  description="lookup field conversions; use this instead of lookupOrInquiryKeys when property names don't match" %>
<%@ attribute name="accountingLineValuesMap" required="false" type="java.util.Map"
              description="map of the accounting line primitive fields and values, for inquiry keys" %>
<%@ attribute name="inquiryExtraKeyValues" required="false"
              description="ampersand separated list of inquiry key=value pairs not in accountingLineValuesMap" %>
<%@ attribute name="lookupAnchor" required="false" description="return to this anchor after lookup" %>

<%@ attribute name="rowSpan" required="false"
              description="row span for the data cell" %>
<%@ attribute name="colSpan" required="false"
              description="col span for the data cell" %>
<%@ attribute name="anchor" required="false"
              description="adds a named anchor inside the header cell.
              Also if lookup is true, returns to this anchor, unless lookupAnchor is set." %>
<%@ attribute name="detailFields" required="false"
              description="The name of multiple fields in the business object containing details to be display.
			  Any supplied field that starts with a semicolon will be treated as a text field, rather 
			  than a database field. The semicolon will be ignored in the output." %>
<%@ attribute name="formattedNumberValue" required="false"
              description="number to format instead of property" %>
<%@ attribute name="fieldTrailerValue" required="false"
              description="Extra text added right after the field" %>

<c:if test="${empty fieldAlign}">
    <c:set var="fieldAlign" value="left"/>
</c:if>
<c:set var="qualifiedField" value="${accountingLine}.${field}"/>
<c:if test="${empty cellProperty}">
    <c:set var="cellProperty" value="${qualifiedField}"/>
</c:if>
<c:if test="${empty conversionField}">
    <c:set var="conversionField" value="${field}"/>
</c:if>
<c:if test="${empty dataFieldCssClass}">
    <c:set var="dataFieldCssClass" value=""/>
</c:if>
<c:choose>
    <c:when test="${empty boPackageName}">
        <c:set var="boClassName" value="org.kuali.module.chart.bo.${boClassSimpleName}"/>
    </c:when>
    <c:otherwise>
        <c:set var="boClassName" value="${boPackageName}.${boClassSimpleName}"/>
    </c:otherwise>
</c:choose>
<c:set var="rowSpan" value="${empty rowSpan ? 1 : rowSpan}"/>
<c:set var="colSpan" value="${empty colSpan ? 1 : colSpan}"/>
<c:set var="useXmlHttp" value="${(!readOnly) && (!empty detailFunction)}" />
<%-- test to see if we are dealing with the extra JV fields here --%>
<c:set var="specialRequiredField" value="${(field eq 'referenceOriginCode') || (field eq 'referenceNumber') || (field eq 'referenceTypeCode')}" />
<td class="${dataCellCssClass}" valign="top" rowspan="${rowSpan}" colspan="${colSpan}">
<div align="${fieldAlign}">
    <c:if test="${not empty anchor}">
    	<a name="${anchor}"></a>
    </c:if>
<span class="nowrap">
    <c:if test="${!empty labelFontWeight}">
        <span style="font-weight: ${labelFontWeight}">
            <kul:htmlAttributeLabel
                attributeEntry="${attributes[field]}"
                useShortLabel="true"
                forceRequired="${specialRequiredField}"
                />
        </span>
        <%-- The following nbsp has breakable space around it, but nevertheless it accomplishes something,
            by preventing this label from touching its input control (consistent with the cellpadding for
            labels that are not in the same cell as their input control).  --%>
        &nbsp;
    </c:if>

    <c:choose>
        <c:when test="${useXmlHttp}">
            <c:set var="onblur" value="${detailFunction}(${detailFunctionExtraParam} this.name, '${accountingLine}.${detailField}');"/>
        </c:when>
        <c:otherwise>
            <c:set var="onblur" value=""/>
        </c:otherwise>
    </c:choose>
    <kul:htmlControlAttribute
        property="${cellProperty}"
        attributeEntry="${attributes[field]}"
        onblur="${onblur}"
        readOnly="${readOnly}"
        readOnlyBody="true"
        styleClass="${dataFieldCssClass}"
        >
        <bc:pbglLineReadOnlyCellProperty
            property="${cellProperty}"
            textStyle="${textStyle}"
            inquiry="${inquiry}"
            boClassName="${boClassName}"
            field="${field}"
            conversionField="${conversionField}"
            inquiryKeys="${lookupOrInquiryKeys}"
            accountingLineValuesMap="${accountingLineValuesMap}"
            inquiryExtraKeyValues="${inquiryExtraKeyValues}"
            formattedNumberValue="${formattedNumberValue}"
            />
    </kul:htmlControlAttribute>${fieldTrailerValue}

    <%-- lookup control --%>
    <c:if test="${!readOnly}">
        <c:if test="${lookup}">
            <c:if test="${empty lookupAnchor}">
                <c:set var="lookupAnchor" value="${anchor}"/>
            </c:if>
            <%-- todo: this lookup to field conversion swapping in accountingLineLookup.tag --%>
            <c:set var="lookupParameters" value=""/>
            <c:set var="fieldConversions" value="${lookupUnkeyedFieldConversions}"/>
            <c:forTokens var="key" items="${lookupOrInquiryKeys}" delims=",">
                <c:set var="withAccountingLine" value="${accountingLine}.${key}"/>
                <c:if test="${!empty lookupParameters}">
                    <c:set var="lookupParameters" value="${lookupParameters},"/>
                </c:if>
                <c:set var="lookupParameters" value="${lookupParameters}${withAccountingLine}:${key}"/>
                <c:set var="fieldConversions" value="${fieldConversions}${key}:${withAccountingLine},"/>
            </c:forTokens>
            <kul:lookup
                boClassName="${boClassName}"
                fieldConversions="${fieldConversions}${conversionField}:${qualifiedField}"
                lookupParameters="${lookupParameters}" fieldLabel="${attributes[field].shortLabel}"
                anchor="${lookupAnchor}"
                />
        </c:if>
    </c:if>
</span>
</div>
<c:if test="${!empty baselineAccountingLine}">
    <bc:hiddenPbglLineField
        accountingLine="${baselineAccountingLine}"
        isBaseline="true"
        hiddenField="${field}"
        displayHidden="${displayHidden}"
        />
</c:if>
<c:if test="${!empty overrideField}">
    <bc:pbglLineOverrideField
        overrideField="${overrideField}"
        attributes="${attributes}"
        readOnly="${readOnly}"
        accountingLine="${accountingLine}"
        baselineAccountingLine="${baselineAccountingLine}"
        displayHidden="${displayHidden}"
        />
</c:if>
<bc:pbglLineDataCellDetail
    detailField="${detailField}"
    accountingLine="${accountingLine}"
    detailFields="${detailFields}"
    />
</td>
