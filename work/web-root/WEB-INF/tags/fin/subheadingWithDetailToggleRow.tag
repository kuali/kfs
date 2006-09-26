<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ attribute name="columnCount" required="true"
    description="Total number of columns in the accounting lines table,
    to be spanned by this row." %>
<%@ attribute name="subheading" required="true"
    description="Tab subheading to display, typically redundant with the tab heading." %>
<tr>
  <td colspan="${columnCount}" class="subhead">
    <span class="subhead-left">${subheading}</span>
    <span class="subhead-right">
      <html:hidden name="KualiForm" property="hideDetails"/>
      <c:if test="${!empty KualiForm.hideDetails}">
        <c:set var="toggle" value="${KualiForm.hideDetails ? 'show' : 'hide'}"/>
        <html:image property="methodToCall.${toggle}Details" src="images/det-${toggle}.gif"
                    alt="${toggle} transaction details" title="${toggle} transaction details" styleClass="tinybutton"/>
      </c:if>
    </span>
  </td>
</tr>