<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="selectedTab" required="true" %>

<div id="navcontainer">
  <ul id="navlist">
    <li id="active"><a href="portal.do?selectedTab=edocs" id="current">Kuali E-Docs</a></li>
  </ul>
</div>
