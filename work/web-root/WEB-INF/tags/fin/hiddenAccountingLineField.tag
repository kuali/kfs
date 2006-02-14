<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>

<%@ attribute name="accountingLine" required="true"
              description="The name in the form of the accounting line." %>
<%@ attribute name="hiddenField" required="true"
              description="the name of an accounting line field
              to be put in a hidden form field by this tag." %>
<%@ attribute name="displayHidden" required="false"
              description="display hidden values (for debugging).
              This information is also available from the Firefox Web Developer extension,
              but that includes more detail and requires even more horizontal space." %>
<%@ attribute name="isBaseline" required="false"
              description="if displayed, distinguish baseline values
              from normal values by background color." %>
<%@ attribute name="value" required="false"
              description="sets the hidden field to this value" %>

<c:if test="${displayHidden}">
    <span style="background: ${isBaseline ? 'blue' : 'green'}">
        <c:out value="${hiddenField}"/> =</c:if
><c:choose
    ><c:when test="${empty value}"
        ><html:hidden write="${displayHidden}" property="${accountingLine}.${hiddenField}"
    /></c:when
    ><c:otherwise
        ><html:hidden write="${displayHidden}" property="${accountingLine}.${hiddenField}" value="${value}"
    /></c:otherwise
></c:choose><c:if test="${displayHidden}">;<br/>
    </span>
</c:if>