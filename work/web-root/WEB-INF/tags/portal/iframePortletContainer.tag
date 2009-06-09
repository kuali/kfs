<%--
 Copyright 2005-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
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
function resize_iframe() {
	var iframe = document.getElementById('iframeportlet');
	if ( !iframe ) return;
	var iframeDocElement = iframe.contentWindow.document.documentElement;
    if ( !iframeDocElement ) return;
    if ( document.all ) {
        iframe.style.height = iframeDocElement.scrollHeight + "px";
        iframe.style.width = iframeDocElement.scrollWidth + "px";
        //window.status = iframeDocElement.scrollWidth+"/"+iframeDocElement.offsetWidth+"/"+iframeDocElement.clientWidth;
    } else {
    	iframe.style.height = iframeDocElement.offsetHeight + "px";
        iframe.style.width = iframeDocElement.scrollWidth + "px";
    }
    // table
    var tableNode = iframe.parentNode.parentNode.parentNode;
    if ( tableNode ) {
        if ( tableNode.tagName == "TBODY" ) { // IE and safari insert this automatically
            tableNode = tableNode.parentNode;
        }
        tableNode.style.width = iframe.style.width;
    }
    //iframe.scrolling = "no";
    //console.log( "Set iframe dimensions to " + iframe.style.height + "/"+iframe.style.width );
}
</script>

<iframe src="${channelUrl}" onload='resize_iframe(); setIframeAnchor("iframeportlet")' name="iframeportlet" id="iframeportlet" hspace="0" vspace="0" style="height: 500px; width: 100%;" title="E-Doc" frameborder="0" scrolling="auto"></iframe>
                     
