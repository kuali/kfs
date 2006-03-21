<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="selectedTab" required="true" %>

<div id="navcontainer">
  <ul id="navlist">

    <%-- Main Menu --%>
    <c:if test='${selectedTab == "portalMainMenuBody"}'>
        <li id="active"><a href="portal.do?selectedTab=portalMainMenuBody" id="current">Main Menu</a></li>
    </c:if>
    <c:if test='${selectedTab != "portalMainMenuBody"}'>
        <c:if test="${empty selectedTab}">
            <li id="active"><a href="portal.do?selectedTab=portalMainMenuBody" id="current">Main Menu</a></li>
        </c:if>
        <c:if test="${!empty selectedTab}">
            <li><a href="portal.do?selectedTab=portalMainMenuBody">Main Menu</a></li>
        </c:if>
    </c:if>


    <%-- Administration --%>
    <c:if test='${selectedTab == "portalAdministrationBody"}'>
        <li id="active"><a href="portal.do?selectedTab=portalAdministrationBody" id="current">Administration</a></li>
    </c:if>
    <c:if test='${selectedTab != "portalAdministrationBody"}'>
        <li><a href="portal.do?selectedTab=portalAdministrationBody">Administration</a></li>
    </c:if>

    <%-- Future Modules --%>
    <c:if test='${selectedTab == "portalFutureModulesBody"}'>
        <li id="active"><a href="portal.do?selectedTab=portalFutureModulesBody" id="current">Future Modules</a></li>
    </c:if>
    <c:if test='${selectedTab != "portalFutureModulesBody"}'>
        <li><a href="portal.do?selectedTab=portalFutureModulesBody">Future Modules</a></li>
    </c:if>
    
  </ul>
</div>
