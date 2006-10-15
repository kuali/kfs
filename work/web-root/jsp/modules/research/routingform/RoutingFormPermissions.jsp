<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/jsp/modules/research/routingform/RoutingFormPermissions.jsp,v $
 
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
	htmlFormAction="researchRoutingFormPermissions"
	headerDispatch="save" feedbackKey="app.krafeedback.link"
	headerTabActive="permissions">
	
	<div class="msg-excol">
  <div class="left-errmsg">
    <div class="error"></div>
  </div>
  <div class="right">
    <div class="excol"></div>
  </div>
</div>
<table width="100%" cellpadding="0" cellspacing="0">
  <tr>

    <td class="column-left"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
    <td><table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
        <tbody>
          <tr>
            <td><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"></td>
            <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"></td>
          </tr>
        </tbody>
      </table>

      <div id="workarea" >
        <div class="tab-container" align="center">
          <table cellpadding=0 cellspacing="0"  summary="view/edit ad hoc recipients">
            <tbody>
              <tr>
                <td colspan=8 class="subhead"><span class="subhead-left"> Ad Hoc Permissions</span> <span class="subhead-right"></span></td>
              </tr>

              <tr>
                <th scope=col>&nbsp;</th>
                <th width="50%" scope=col><div align="left">Name</div></th>
                <th scope=col>Chart </th>
                <th scope=col>Org</th>
                <th scope=col>Role</th>
                <th scope=col>Primary/Delegate</th>

                <th scope=col>Type</th>
                <th scope=col>Actions</th>
              </tr>
              <tr>
                <th scope=col>add:</th>
                <td nowrap class="infoline">(select) &nbsp;&nbsp; by person
                    <input name="imageField" type=image class="tinybutton" src="images/searchicon.gif" alt="person lookup" align="middle">

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; by org
        <input name="imageField" type=image class="tinybutton" src="images/searchicon.gif" alt="person lookup" align="middle">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="infoline"><div align="center">--</div></td>
                <td class="infoline"><div align="center">--</div></td>
                <td class="infoline"><div align="center">--</div></td>
                <td class="infoline"><div align="center">--</div></td>
                <td class="infoline"><div align="center">

                    <select name="newAdHocRoutePerson.actionRequested" id="action requested">
                      <option value="K" selected>READ</option>
                      <option value="A">MOD</option>
                    </select>
                </div></td>
                <td class="infoline"><div align=center>
                    <input name="imageField" type=image class="tinybutton" src="images/tinybutton-add1.gif" alt="add">
                </div></td>

              </tr>
              <tr>
                <th scope=row><div align=center>1</div></th>
                <td>CRAFT, CHRISTOPHER B</td>
                <td nowrap ><label> </label>
                    <label>BL</label>
                </td>

                <td>SPEA<span class="nowrap"> </span> <span class="fineprint"></span></td>
                <td>Adhoc</td>
                <td>Primary</td>
                <td><select name="newAdHocRoutePerson.actionRequested" id="action requested">
                    <option value="K" selected>READ</option>
                    <option value="A">MOD</option>

                </select></td>
                <td><div align=center> <a href="gec-adhoc.html"><img src="images/tinybutton-delete1.gif" alt="delete" width="40" height="15" border="0"></a> </div></td>
              </tr>
              <tr>
                <th scope=row><div align=center>2</div></th>
                <td>HEATH, BREANNE</td>
                <td nowrap ><label> </label>

                    <label>BL</label>
                </td>
                <td>MRES<span class="nowrap"> </span> <span class="fineprint"></span></td>
                <td>Adhoc</td>
                <td>Primary</td>
                <td><select name="newAdHocRoutePerson.actionRequested" id="action requested">

                    <option value="K" selected>READ</option>
                    <option value="A">MOD</option>
                </select></td>
                <td><div align=center> <a href="gec-adhoc.html"><img src="images/tinybutton-delete1.gif" alt="delete" width="40" height="15" border="0"></a> </div></td>
              </tr>
              <tr>
                <th scope=row><div align=center>3</div></th>

                <td>BRENNER, MARK L</td>
                <td nowrap ><label> </label>
                    <label>BL</label>
                </td>
                <td>RESG<span class="fineprint"></span></td>
                <td>Adhoc</td>

                <td>Primary</td>
                <td><select name="newAdHocRoutePerson.actionRequested" id="action requested">
                    <option value="K" selected>READ</option>
                    <option value="A">MOD</option>
                </select></td>
                <td><div align=center> <a href="gec-adhoc.html"><img src="images/tinybutton-delete1.gif" alt="delete" width="40" height="15" border="0"></a> </div></td>

              </tr>
              <tr>
                <th scope=row>&nbsp;</th>
                <td>&bull; ARTMEIER, MICHELLE ANN</td>
                <td nowrap >BL</td>
                <td>RESG</td>
                <td>Adhoc</td>

                <td>Delegate</td>
                <td>READ</td>
                <td><div align=center> <a href="gec-adhoc.html"><img src="images/tinybutton-delete2.gif" alt="delete" width="40" height="15" border="0"></a> </div></td>
              </tr>
              <tr>
                <th scope=row>&nbsp;</th>
                <td>&bull; MCKEOUGH, PAMELA M</td>

                <td nowrap >BL</td>
                <td>RESG</td>
                <td>Adhoc</td>
                <td>Delegate</td>
                <td>READ</td>
                <td><div align=center> <a href="gec-adhoc.html"><img src="images/tinybutton-delete2.gif" alt="delete" width="40" height="15" border="0"></a> </div></td>

              </tr>
              <tr>
                <th scope=row>&nbsp;</th>
                <td>&bull; HOSIER, ELIZABETH A</td>
                <td nowrap >BL</td>
                <td>RESG</td>
                <td>Adhoc</td>

                <td>Delegate</td>
                <td>READ</td>
                <td><div align=center> <a href="gec-adhoc.html"><img src="images/tinybutton-delete2.gif" alt="delete" width="40" height="15" border="0"></a> </div></td>
              </tr>
              <tr>
                <th scope=row>4</th>

                <td>TALBOTT, JOHN WILLIAM</td>
                <td nowrap >IN</td>
                <td>DMOO</td>
                <td>Adhoc</td>
                <td>Primary</td>
                <td><select name="newAdHocRoutePerson.actionRequested" id="action requested">

                    <option value="K" selected>READ</option>
                    <option value="A">MOD</option>
                </select></td>
                <td><div align=center> <a href="gec-adhoc.html"><img src="images/tinybutton-delete1.gif" alt="delete" width="40" height="15" border="0"></a> </div></td>
              </tr>
            </tbody>
          </table>

        </div>
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
      </div></td>
    <td class="column-right"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>

  </tr>
</table>
	
</kul:documentPage>