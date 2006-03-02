<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ attribute name="property" required="true"
              description="the fully qualified name of the property being displayed by this cell.
              This could be in the document instead of the accounting line." %>
<%@ attribute name="textStyle" required="true" %>
<%@ attribute name="inquiry" required="false"
              description="Boolean indicating whether this cell should have an inquiry link if it's writable.
              If true, the boClassName, field, and conversionField attributes at least are required." %>
<%@ attribute name="boClassName" required="false" %>
<%@ attribute name="field" required="false"
              description="if the inquiry attribute is true, this must be the name of a field in the accounting line
              that is being displayed by this cell." %>
<%@ attribute name="conversionField" required="false"
              description="The name of the field in the business object corresponding to
              this cell's field  in the accounting line. This may be used to generate an inquiry." %>
<%@ attribute name="inquiryKeys" required="false"
              description="comma separated list of inquiry key names in the accountingLineValuesMap,
              in addition to the field attribute" %>
<%@ attribute name="accountingLineValuesMap" required="false" type="java.util.Map"
              description="map of the accounting line primitive fields and values, for inquiry keys" %>
<%@ attribute name="inquiryExtraKeyValues" required="false"
              description="ampersand separated list of inquiry key=value pairs not in accountingLineValuesMap" %>

<c:set var="aKeyIsMissing" value="${empty accountingLineValuesMap[field]}"/>
<c:set var="keyValues" value="${conversionField}=${accountingLineValuesMap[field]}"/>
<c:forTokens var="key" items="${inquiryKeys}" delims=",">
    <c:set var="aKeyIsMissing" value="${missingKey || empty accountingLineValuesMap[key]}"/>
    <c:set var="keyValues" value="${keyValues}&${key}=${accountingLineValuesMap[key]}"/>
</c:forTokens>
<c:set var="keyValues" value="${keyValues}${empty inquiryExtraKeyValues ? '' : '&'}${inquiryExtraKeyValues}"/>
<c:set var="canRenderInquiry" value="${not empty keyValues && not aKeyIsMissing}"/>

<kul:inquiry
    boClassName="${boClassName}"
    keyValues="${keyValues}"
    render="${inquiry && canRenderInquiry}"
    >
    <html:hidden write="true" property="${property}" style="${textStyle}" />
</kul:inquiry>
