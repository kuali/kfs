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
<%@ attribute name="frameHeight" required="false" %>

<c:if test="${empty frameHeight || frameHeight == 0}">
  <c:set var="frameHeight" value="500"/>
</c:if>

<iframe src="${channelUrl}" onload='<c:if test="${ConfigProperties.test.mode ne 'true'}">setIframeAnchor("iframeportlet")</c:if>' name="iframeportlet" id="iframeportlet" style="height: ${frameHeight}px; width: 100%" title="E-Doc" frameborder="0" height="${frameHeight}px" scrolling="auto" width="100%"></iframe>                   

<%-- 
  May want to move this to a script a js file at some point.
  Right now though this is very specific to this tag.  This was
  very simple logic until it came to supporting IE :-(
--%>
<script type="text/javascript">
  /* <![CDATA[ */
  /** "namespacing" the portlet resize elements. */
  var org$kuali$rice$portletResize = function() {

    //this isn't perfect as this depends on platform, themes, browser, etc.
    var SCROLLBAR_HEIGHT = 20;

    /** get horizontal scrollbar height. */
    function getHorScrollBarHeight() {
      return SCROLLBAR_HEIGHT;
    }

    /** gets the portlet iframe. */
    function getPortlet() {
      return document.getElementById('iframeportlet');
    }

    /** gets the portlet container. */
    function getPortletContainer() {
      return document.getElementById('iframe_portlet_container_div');
    }

    /** gets the current height of the passed in frame in numeric form (ex: 500). Could generate a permission exception.  */
    function getFrameHeight(frame) {
      if (frame.contentWindow){
        return frame.contentWindow.document.body.scrollHeight;
      } else {
        //using the offsetHeight to set the correct height for IE
        return frame.contentDocument.body.offsetHeight;
      }
    }

    /** sets the portlet container's height. */
    function setContainerHeight() {
      //reset the height to shrink the scroll height.  For the usecase where the portlet's contents got smaller.
      getPortlet().style.height = '${frameHeight}px';
   	  getPortlet().height = '${frameHeight}px';
      
      var height = '${frameHeight}';
      try {
         height = getFrameHeight(getPortlet());
      } catch (e) {
        //fallback for crossdomain permission problems.
        height = '${frameHeight}';
      }

      //set the portlet & portlet container to be the same height - not using 100% for the portlet to avoid the inner scrollbar
      try {
      	getPortletContainer().style.height = height + 'px';
      } catch ( ex ) {
          // do nothing, we can't get to the container
      }
      getPortlet().style.height = (height + getHorScrollBarHeight()) + 'px';
      getPortlet().height = (height + getHorScrollBarHeight()) + 'px';
    }
    
    /** 
      * Checks if the portlet container's height has changed.
      * If a permission exception occurs then the function returns true.
      * This check is necessary for IE - otherwise control is lost on
      * dropdown menus when the portlet is resized - weird.
     */
    function hasContainerHeightChanged() {
      var height;
      var conHeight;
      try {
        height = getFrameHeight(getPortlet());
        conHeight = getPortletContainer().style.height;
      } catch (e) {
      	return true;
      }
      return conHeight != height + 'px';
    }

    /* resizes the portlet container to fit the size of the porlet. */
    function resizePortletContainer() {
      if (hasContainerHeightChanged()) {
        setContainerHeight();
      }

      //width handling needs some work
      //if (hasContainerWidthChanged()) {
        //setContainerWidth();
      //}
    }

    //registering event handlers...
    var frame = getPortlet();
    var prevPortletLoadEvent = frame.onload ? frame.onload : function () {};
    frame.onload = function () {prevPortletLoadEvent(); resizePortletContainer(); };

    var prevPortletResizeEvent = frame.onresize ? frame.onresize : function () {};
    var onresize = function () {prevPortletResizeEvent(); resizePortletContainer(); };
    //IE may not raise an onresize event for frames...not a big deal b/c of the setInterval logic
    frame.onresize = onresize;

    //this is necessary because dynamically generated content on the page does not trigger
    //an onresize event
    setInterval (onresize, 500);

    //no public functions at this time...
    return {

    };

  //executing the function...
  }();
  /* ]]> */
</script>