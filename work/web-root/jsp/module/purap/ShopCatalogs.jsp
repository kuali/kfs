<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<html:html>
<head>
<script>var jsContextPath = "${pageContext.request.contextPath}";</script>

<c:forEach items="${fn:split(ConfigProperties.javascript.files, ',')}" var="javascriptFile">
	<c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
		<script language="JavaScript" type="text/javascript"
				src="${pageContext.request.contextPath}/${javascriptFile}">
		</script>
	</c:if>
</c:forEach>
</head>

<body onload="reload()">
<c:set var="frameHeight" value="1000"/>
<c:set var="channelUrl" value="${KualiForm.shopUrl}" />

<iframe src="${channelUrl}"
        onload='<c:if test="${ConfigProperties.test.mode ne 'true'}">setIframeAnchor("iframeportlet")</c:if>'
        name="iframeportlet" id="iframeportlet" style="width: 100%;"
        title="E-Doc" scrolling="auto" frameborder="0" height="${frameHeight}" width="100%"></iframe>

<script type="text/javascript">
  jQuery(function () {
    var if_height = ${frameHeight};
    var if_width;
    var channelUrlEscaped = "${channelUrl}".replace(/'/g, "\\'");
    var thisIframe = jQuery("iframe[src='" + channelUrlEscaped + "']");
    var browserIsIE8 = jQuery.browser.msie && jQuery.browser.version == 8.0;

    //find iframe source host
    var iframeSrc = "${channelUrl}";
    var regex = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im');
    var receivingMessages = false;
    var intervalId;

    if (iframeSrc.indexOf("http") == 0 || iframeSrc.indexOf("ftp") == 0) {
      iframeSrc = iframeSrc.match(regex)[1].toString();
    }
    else {
      //if it doesnt begin with http it must be local domain
      iframeSrc = window.location.host;
    }


    if (!jQuery.browser.msie) {
      jQuery(thisIframe).height(if_height);
    }

    if (iframeSrc !== window.location.host) {
      setupCrossDomainResize()
    }

    jQuery(thisIframe).load(function () {
      if (iframeSrc === window.location.host) {
        setSameDomainIframeHeight();
        intervalId = setInterval(setSameDomainIframeHeight, 500);
      }
    });

    function setupCrossDomainResize() {
      if (!browserIsIE8) {
        thisIframe.height(if_height);
      }
    }

    //a function for iframes in the same domain
    function setSameDomainIframeHeight() {
      //check every iteration to see if the iframe is no longer in the same domain
      var url = jQuery(thisIframe).attr('src');
      if ((url.indexOf("http") != 0 && url.indexOf("ftp") != 0) || url.match(regex)[1].toString() === window.location.host) {
        sameDomain = true;
        if (!browserIsIE8 && thisIframe[0] && thisIframe[0].contentWindow.document.body) {
          if_height = thisIframe[0].contentWindow.document.body.scrollHeight;
          thisIframe.height(if_height);
        }
      }
      else {
        clearInterval(intervalId);
        setupCrossDomainResize();
      }
    }

  })
  ;
</script>
</body>
</html:html>
