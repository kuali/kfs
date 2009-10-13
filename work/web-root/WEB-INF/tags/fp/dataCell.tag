<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell." %>
<%@ attribute name="dataFieldCssClass" required="false"
              description="The name of the CSS class for this data field." %>
              
<%@ attribute name="field" required="true"
              description="The name of the field of  the business object being edited or displayed by this cell.
              Combined with the businessObjectFormName, this identifies the value (i.e., the data) of this cell." %>
<%@ attribute name="attributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for the field in this cell." %>
<%@ attribute name="readOnly" required="true" %>
<%@ attribute name="disabled" required="false" %>

<%@ attribute name="businessObjectFormName" required="true"
              description="This is normally the name in the form of the business object
              being edited or displayed by the row containing this cell.
              Also it is always the key to the DataDictionary attributes entry for editing or displaying." %>

<%@ attribute name="lookup" required="false"
              description="Boolean indicating whether this cell should have a lookup icon if it's writable.
              If true, the boClassSimpleName attribute at least is also required." %>
<%@ attribute name="inquiry" required="false"
              description="Boolean indicating whether this cell should have an inquiry link if it's writable.
              If true, the boClassSimpleName attribute at least is also required." %>

<%@ attribute name="boClassFullName" required="false"
              description="The full name of the business object class to perform a lookup or inquiry.
              This does include the package name." %>
<%@ attribute name="boClassSimpleName" required="false"
              description="The simple name of the business object class to perform a lookup or inquiry.
              This does not include the package name." %>
<%@ attribute name="boPackageName" required="false"
              description="The name of the package containing the business object class to perform a lookup or inquiry.
              If this attribute is missing, it defaults to 'org.kuali.kfs.coa.businessobject'." %>
<%@ attribute name="conversionField" required="false"
              description="The name of the field in the business object corresponding to
              this cell's field  in the business Object.
              This may be used to return a lookup value from the BO, or generate an inquiry.
              For a lookup, the value of this data cell becomes the value of this field.
              If not provided, this attribute defaults to the same value as the field attribute." %>
  
<%@ attribute name="lookupOrInquiryKeys" required="false"
              description="comma separated list of inquiry key names in the businessObjectValuesMap" %>
<%@ attribute name="lookupUnkeyedFieldConversions" required="false"
			  description="lookup field conversions; use this instead of lookupOrInquiryKeys when property names don't match" %>
<%@ attribute name="lookupParameters" required="false"
			  description="lookup parameters; use this to manually add lookupParameters" %>

<%@ attribute name="businessObjectValuesMap" required="false" type="java.util.Map"
              description="map of the business object primitive fields and values, for inquiry keys" %>
<%@ attribute name="inquiryExtraKeyValues" required="false"
              description="ampersand separated list of inquiry key=value pairs not in businessObjectValuesMap" %>
              
<%@ attribute name="detailFunction" required="false"
              description="The name of the JavaScript function to asynchronously
              update the detailed description of the value in this data cell on blur.
              This attribute requires the detailField attribute." %>
<%@ attribute name="detailFunctionExtraParam" required="false"
              description="The value of an extra parameter required by some of the detail JavaScript functions." %>
<%@ attribute name="detailField" required="false"
              description="The name of the field in the business object containing the detail to be displayed." %>  
<%@ attribute name="detailFields" required="false"
              description="The name of multiple fields in the business object containing details to be display.
			  Any supplied field that starts with a semicolon will be treated as a text field, rather 
			  than a database field. The semicolon will be ignored in the output." %>            

<%@ attribute name="rowSpan" required="false" description="row span for the data cell" %>
<%@ attribute name="colSpan" required="false" description="column span for the data cell" %>

<c:set var="qualifiedField" value="${businessObjectFormName}.${field}"/>
<c:if test="${empty cellProperty}">
    <c:set var="cellProperty" value="${qualifiedField}"/>
</c:if>
<c:if test="${empty conversionField}">
    <c:set var="conversionField" value="${field}"/>
</c:if>
<c:if test="${empty dataFieldCssClass}">
    <c:set var="dataFieldCssClass" value=""/>
</c:if>
<c:if test="${empty lookupParameters}">
    <c:set var="lookupParameters" value=""/>
</c:if>
<c:set var="rowSpan" value="${empty rowSpan ? 1 : rowSpan}"/>
<c:set var="useXmlHttp" value="${(!readOnly) && (!empty detailFunction)}" />

<c:choose>
    <c:when test="${not empty boClassFullName}">
        <c:set var="boClassName" value="${boClassFullName}"/>
    </c:when>
    <c:when test="${empty boPackageName}">
        <c:set var="boClassName" value="org.kuali.kfs.coa.businessobject.${boClassSimpleName}"/>
    </c:when>
    <c:otherwise>
        <c:set var="boClassName" value="${boPackageName}.${boClassSimpleName}"/>
    </c:otherwise>
</c:choose>
        
<td class="${dataCellCssClass}" valign="top" rowspan="${rowSpan}" colspan="${colSpan}"><span class="nowrap">
    <c:choose>
        <c:when test="${useXmlHttp}">
            <c:set var="onblur" value="${detailFunction}(${detailFunctionExtraParam} this.name, '${businessObjectFormName}.${detailField}');"/>
        </c:when>
        <c:otherwise>
            <c:set var="onblur" value=""/>
        </c:otherwise>
    </c:choose>
    
    <jsp:doBody/>
    
    <c:set var="datePicker" value="${attributes[field].validationPattern.type eq 'date' ? true : false}" />
    <kul:htmlControlAttribute
        property="${cellProperty}"
        attributeEntry="${attributes[field]}"
        onblur="${onblur}"
        readOnly="${readOnly}" disabled="${disabled}"
        readOnlyBody="true"
        styleClass="${dataFieldCssClass}"
        datePicker="${datePicker}">
                    
        <c:set var="aKeyIsMissing" value="${empty businessObjectValuesMap[field]}"/>
		<c:set var="keyValues" value="${conversionField}=${businessObjectValuesMap[field]}"/>
		<c:set var="dashedValue" value="false" />
		
		<c:forTokens var="key" items="${lookupOrInquiryKeys}" delims=",">
			<c:set var="aKeyIsMissing" value="${missingKey || empty businessObjectValuesMap[key]}"/>
			<c:set var="keyValues" value="${keyValues}&${key}=${businessObjectValuesMap[key]}"/>
			<c:if test="${!empty businessObjectValuesMap[key] && kfsfunc:matchesPattern(businessObjectValuesMap[key], '^-*$')}">
				<c:set var="dashedValue" value="true" />
			</c:if>
		</c:forTokens>
		
		<c:set var="keyValues" value="${keyValues}${empty inquiryExtraKeyValues ? '' : '&'}${inquiryExtraKeyValues}"/>
		<c:set var="canRenderInquiry" value="${not empty keyValues && not aKeyIsMissing && !dashedValue}"/>
		
		<kul:inquiry
		    boClassName="${boClassName}"
		    keyValues="${keyValues}"
		    render="${inquiry && canRenderInquiry}"
		    >
		    <html:hidden write="true" property="${cellProperty}" style="${textStyle}" />
		</kul:inquiry>&nbsp;    
    </kul:htmlControlAttribute>

    <%-- lookup control --%>
    <c:if test="${!readOnly && lookup}">
        <c:set var="fieldConversions" value="${lookupUnkeyedFieldConversions}"/>
        <c:forTokens var="key" items="${lookupOrInquiryKeys}" delims=",">
            <c:set var="businessObjectKey" value="${businessObjectFormName}.${key}"/>
            
            <c:if test="${!empty lookupParameters}">
                <c:set var="lookupParameters" value="${lookupParameters},"/>
            </c:if>
            
            <c:set var="lookupParameters" value="${lookupParameters}${businessObjectKey}:${key}"/>
            <c:set var="fieldConversions" value="${fieldConversions}${key}:${businessObjectKey},"/>
        </c:forTokens>

        <kul:lookup
            boClassName="${boClassName}"
            fieldConversions="${fieldConversions}${conversionField}:${qualifiedField}"
            lookupParameters="${lookupParameters}" fieldLabel="${attributes[field].shortLabel}" />
    </c:if>    
	</span>
	
	<fp:dataCellDetail
    	detailField="${detailField}"
    	businessObjectFormName="${businessObjectFormName}"
    	detailFields="${detailFields}"
    />
</td>

</td>
