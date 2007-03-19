<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiRoutingFormDocument.documentTypeName}" pageName="${KraConstants.OUTPUT_HEADER_TAB}" altText="page help"/>
</div>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
  <tbody>
    <tr>
      <td><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"></td>
      <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"></td>
    </tr>
  </tbody>
</table>

<div id="workarea" >
  <div class="tab-container" align="center">
    <table cellpadding=0 cellspacing="0"  summary="">
      <tr>
        <td colspan=2 class="subhead"><span class="subhead-left"> Generate Output </span> </td>
      </tr>
      <tr align="center" valign="top">

        <td>
        	<div align="center">
        		<p><b>Generate Output for Current Routing Form</b></p>
        		<p>You will need Adobe Acrobat Reader 5.0 or above installed on your computer, in order to generate output. Please visit 
        		<a href="http://www.adobe.com/products/acrobat/" target="_new">Adobe Acrobat</a>
        		for links and installation instructions.<p/>
        	</div>
          </td>
      </tr>
    </table>
  </div>
  <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
    <tr>
      <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
      <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
    </tr>
  </table>
</div>

<div id="globalbuttons" class="globalbuttons">
  <html:image src="images/buttonsmall_genpdf.gif" styleClass="globalbuttons" property="methodToCall.pdfOutput" alt="Copy current document" onclick="excludeSubmitRestriction=true"/>
</div>

<div class="exportlinks">Export options: 
<a href="researchRoutingFormOutput.do?document.documentNumber=${KualiForm.document.documentNumber}&methodToCall=xmlOutput"><span class="export xml">XML</span></a></div>
