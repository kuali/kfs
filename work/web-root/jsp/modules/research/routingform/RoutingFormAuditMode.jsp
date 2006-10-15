<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/jsp/modules/research/routingform/RoutingFormAuditMode.jsp,v $
 
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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormAuditMode"
	headerDispatch="save" feedbackKey="app.krafeedback.link"
	headerTabActive="auditmode">
	
	<div class="msg-excol">
  <div class="left-errmsg">
    <div class="error"></div>
  </div>
  <div class="right">
    <div class="excol">
      <input name="imageField" type="image" class="tinybutton" src="images/tinybutton-expandall.gif" width="73" height="15" border="0">
      <input name="imageField" type="image" class="tinybutton" src="images/tinybutton-collapseall.gif" width="73" height="15" border="0">
    </div>

  </div>
</div>
<table width="100%" cellpadding="0" cellspacing="0">
  <tr>
    <td class="column-left"><img src="images/pixel_clear.gif" alt="" width="20" height="20" id=""></td>
    <td><table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
        <tbody id="">
          <tr>
            <td><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3" id=""></td>
            <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3" id=""></td>

          </tr>
        </tbody>
      </table>
      <div id="workarea">
        <div class="tab-container"  align="center">
          <div class="h2-container"> <span class="subhead-left">
            <h2>Activate Audit Mode</h2>
            </span><span class="subhead-right"> <span class="subhead"><a href="asdf.html" id=""><img src="images/my_cp_inf.gif" alt="help" width="15" height="14" border="0" align="absmiddle" id=""></a></span> </span> </div>

          <table cellpadding=0 cellspacing="0"  summary="">
            <tr>
              <td><div class="floaters">
                  <p>You can activate an audit check to determine any errors or incomplete information. There are two types of audit errors. A hard audit error is an error that must be corrected prior to linking to Routing Form for the eventual submission into routing. A soft audit error is an error that serves as a warning only and will not prevent linking to Routing Form. </p>
                  <p align="center">
                  	<c:choose>
						<c:when test="${KualiForm.auditActivated}"><html:image property="methodToCall.deactivate" src="images/tinybutton-deacaudit.gif" styleClass="tinybutton" /></c:when>
						<c:otherwise><html:image property="methodToCall.activate" src="images/tinybutton-activaudt.gif" styleClass="tinybutton" /></c:otherwise>
					</c:choose>
                  </p>
                </div></td>
            </tr>
          </table>

	<c:if test="${KualiForm.auditActivated}">
          <br>
          <br>
          <table  cellpadding="0" cellspacing="0"  summary="">
            <tr>
              <td colspan="2" class="subhead" >Hard Errors</td>
            </tr>
            <tr>
              <td width="5%" class="tab-subhead" ><a id="A32" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 align="absmiddle" id="F32"></a></td>

              <td class="tab-subhead" >Main Page (2) </td>
            </tr>
            <tbody id="G32" style="display: none;">
              <tr>
                <td>&nbsp;</td>
                <td>Audit Error: Select an agency</td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td>Audit Error: Enter a primary delivery address</td>
              </tr>
            </tbody>

            <tr>
              <td class="tab-subhead" ><a id="A33" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 align="absmiddle" id="F33"></a></td>
              <td class="tab-subhead" >Research Risks (1) </td>
            </tr>
            <tbody id="G33" style="display: none;">
              <tr>
                <td align="left" >&nbsp;</td>
                <td>Audit Error: Select either 'Yes' or 'No' for human subjects</td>
              </tr>
            </tbody>
            <tr>
              <td colspan="2" class="subhead">Soft Errors </td>
            </tr>
            <tr>
              <td class="tab-subhead"><a id="A36" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 align="absmiddle" id="F36"></a></td>
              <td class="tab-subhead">Main Page (1) </td>
            </tr>
            <tbody id="G36" style="display: none;">
              <tr>
                <td  align="left" >&nbsp;</td>
                <td>Soft Audit Error: Some soft audit error.</td>
              </tr>
            </tbody>
          </table>
          </c:if>
        </div>
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3" id=""></td>
            <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3" id=""></td>
          </tr>
        </table>
      </div>
      <div class="globalbuttons"> </div></td>
    <td class="column-right"><img src="images/pixel_clear.gif" alt="" width="20" height="20" id=""></td>
  </tr>
</table>
	
</kul:documentPage>