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

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>

<script type="text/javascript">
/**
 * Creates a link element from the given url.  This link element can be used to extract partial link information
 * such as: host, hostname, pathname, port, protocol, and search.
 *
 * @param href - relative or absolute url for which the link element should be created
 * @return {Element} - the link element
 */
function getLocation(href) {
    var location = document.createElement("a");
    location.href = href;
    // IE doesn't populate all link properties when setting .href with a relative URL, however .href will return an
    // absolute URL which then can be used on itself to populate these additional fields.
    if (location.host == "") {
      location.href = location.href;
    }
    return location;
};

/**
 * easyXDM is used to enable portal resizing when the content is on a different server url.
 *
 * The portal needs to know where to find the resize_intermediate.html file on the remote server.  This means that the
 * application contexts of the local and remote content need to be specified in the configuration parameters.  These
 * parameters start with "context.names.". A specific suffix is not important.  For applications that use a standalone
 * rice server "context.names.rice" would need to be specified.  By default context.names.app is set to app.context.name.
 */
  var channelLocation = getLocation("${channelUrl}");
  var contextNames = new Array();
  <c:forEach var="contextName" items="${ConfigProperties.context.names}">
      <c:if test="${not empty contextName.value}">
          contextNames.push("<c:out value="${contextName.value}" />");
      </c:if>
  </c:forEach>
  var swf;
  var remote = channelLocation.protocol + '//' + channelLocation.host + "/";
  //Preserve the server's context name in url path. IE pathname has a leading slash
  for (var i = 0; i < contextNames.length; i++) {
    if (channelLocation.pathname.lastIndexOf(contextNames[i], 0) === 0
            || channelLocation.pathname.lastIndexOf("/" + contextNames[i], 0) === 0) {
      remote += contextNames[i] + "/";
      break;
    }
  }

  // Add leading slash, only IE has a leading slash
  var channelPathAndSearch = channelLocation.pathname + channelLocation.search;
  if (channelPathAndSearch.substr(0,1) != "/") {
    channelPathAndSearch = "/" + channelPathAndSearch;
  }

  swf = remote + "rice-portal/scripts/easyXDM/easyxdm.swf";
  remote += "rice-portal/scripts/easyXDM/resize_intermediate.html?url="
          + encodeURIComponent(channelPathAndSearch);

  new easyXDM.Socket(/** The configuration */{
    remote: remote,
    swf: swf,
    container: "embedded",
    props: {
      style: {
        width: "100%",
        height: screen.height - 350 + "px"  // initial height set to a reasonable value in case messaging doesn't work
      }
    },
    onMessage: function(message, origin) {
      var availableHeight = jQuery(window).height() - 250;
      if (availableHeight > message) {
        this.container.getElementsByTagName("iframe")[0].style.height = availableHeight + "px";
      } else {
        this.container.getElementsByTagName("iframe")[0].style.height = message + "px";
      }
    }
  });

</script>

<div id="embedded">
</div>
