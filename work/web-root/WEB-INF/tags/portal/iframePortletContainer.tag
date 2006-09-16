<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>


<iframe src="${channelUrl}" onload='javascript: setFocusedIframeDimensions("iframeportlet", 500); setIframeAnchor("iframeportlet")' name="iframeportlet" id="iframeportlet" hspace="0" vspace="0" style="height: 500px;" title="E-Doc" frameborder="0" height="500" scrolling="auto" width="100%"></iframe>
                     
