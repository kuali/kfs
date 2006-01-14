<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>

<%@ attribute name="accountingLine" required="false"
              description="The name in the form of the accounting line
              being edited or displayed by the row containing the cell containing this detail." %>
<%@ attribute name="detailField" required="false"
              description="The name of the accounting line field containing the detailed description
              of the value of this cell.  This attribute requires the accountingLine attribute.
              If this attribute is empty, this tag generates a blank line in the same CSS class,
              for consistent spacing." %>

<c:if test="${!KualiForm.hideDetails}">
    <br/>
    <c:if test="${!empty detailField}">
        <div id="${accountingLine}.${detailField}.div" class="fineprint">  <%-- objectInfo.js uses this div name. --%>
            <bean:write name="KualiForm" property="${accountingLine}.${detailField}"/>&nbsp;
        </div>
    </c:if>
</c:if>
<c:if test="${!empty detailField}">
  <c:catch var="e">
 	<html:hidden property="${accountingLine}.${detailField}"/>
  </c:catch>
  <c:if test="${e!=null}">
    <input type="hidden" name="${accountingLine}.${detailField}" value="test"/>
  </c:if>
</c:if>