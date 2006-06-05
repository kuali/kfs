<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>

<%@ attribute name="tabTitle" required="true" %>
<%@ attribute name="defaultOpen" required="true" %>
<%@ attribute name="totalErrors" required="true" %>

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<c:set var="currentTab" value="${KualiForm.tabStateJstl}"/>

<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="${defaultOpen}" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${currentTab.open}" />
    </c:when>
</c:choose>

<html:hidden property="tabState[${currentTabIndex}].open" value="${isOpen}" />

<!-- ROW -->

<tbody>
    <tr>
	    <td class="tab-subhead">
	      	<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${currentTabIndex}" src="images/tinybutton-hide.gif" alt="hide" styleClass="tinybutton" onclick="javascript: if (document.forms[0].elements['tabState[${currentTabIndex}].open'].value == 'false') {document.getElementById('tab-${currentTabIndex}-div').style.display = ''; document.forms[0].elements['tabState[${currentTabIndex}].open'].value = 'true'; this.src = 'images/tinybutton-hide.gif';  return false;} else { document.getElementById('tab-${currentTabIndex}-div').style.display = 'none'; document.forms[0].elements['tabState[${currentTabIndex}].open'].value = 'false'; this.src = 'images/tinybutton-show.gif';  return false; } " />
	        </c:if>
	        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${currentTabIndex}" src="images/tinybutton-show.gif" alt="show" styleClass="tinybutton" onclick="javascript: if (document.forms[0].elements['tabState[${currentTabIndex}].open'].value == 'false') {document.getElementById('tab-${currentTabIndex}-div').style.display = ''; document.forms[0].elements['tabState[${currentTabIndex}].open'].value = 'true'; this.src = 'images/tinybutton-hide.gif';  return false;} else { document.getElementById('tab-${currentTabIndex}-div').style.display = 'none'; document.forms[0].elements['tabState[${currentTabIndex}].open'].value = 'false'; this.src = 'images/tinybutton-show.gif';  return false; } " />
	        </c:if>
	    </td>
	    <td colspan="3" class="tab-subhead" width="99%"><b>${tabTitle} (${totalErrors})</b></td>
    </tr>
</tbody>

<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	<tbody style="display: ;" id="tab-${currentTabIndex}-div">
</c:if>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	<tbody style="display: none;" id="tab-${currentTabIndex}-div">
</c:if>

<!-- Before the jsp:doBody of the kul:tab tag -->
<jsp:doBody/>
<!-- After the jsp:doBody of the kul:tab tag -->

</tbody>