<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/jsp/modules/research/routingform/RoutingFormResearchRisks.jsp,v $
 
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
	htmlFormAction="researchRoutingFormResearchRisks"
	headerDispatch="save" feedbackKey="app.krafeedback.link"
	headerTabActive="researchrisks">
	
	 <kra-rf:routingFormHiddenDocumentFields />
	
	
	<div class="msg-excol">
	<div class="right">
		<div class="excol"><input name="imageField" type="image"
			class="tinybutton" src="images/tinybutton-expandall.gif"> <input
			name="imageField" type="image" class="tinybutton"
			src="images/tinybutton-collapseall.gif">
		</div>
	</div>
	</div>

	<table width="100%" cellpadding="0" cellspacing="0">
    <tr>
      <td class="column-left"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
      <td><div id="workarea">
	
<kra-rf:routingFormMultiLine tabTitle="Human Subjects">
</kra-rf:routingFormMultiLine>

          <!-- TAB -->
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">

            <tr>
              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle">Animals</td>
             <td class="tabtable1-mid1"><label>
                <input name="RadioGroup2" type="radio" class="nobord" value="radio">
                yes</label>
                <label>
                <input name="RadioGroup2" type="radio" class="nobord" value="radio">
                no</label></td>

              <td class="tabtable2-mid"><a id="A2" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F2" vspace=6></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G2" style="display: none;">
            <div class="h2-container"> <span class="subhead-left">
              <h2>Animals</h2>

              </span> </div>
           
            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=5 class="tab-subhead"><span class="left">Insert Study </span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>

                <th> <div align="center">Approval Status </div></th>
                <th> <div align="center">Study Number</div></th>
                <th><div align="center">Approval Date </div></th>
                <th >Action</th>
              </tr>
              <tr>

                <th scope="row">add:</th>
                <td class="infoline"><div align="center">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value="" selected="selected"></option>
                      <option value="P">PENDING</option>
                      <option value="A">APPROVED</option>
                    </select>

                </div></td>
                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="12">
                </div></td>
                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="12">
                    <img src="images/cal.gif" width="16" height="16"></div></td>
                <td class="infoline"><div align=center><a href="ib-multi09.html"><img src="images/tinybutton-add1.gif" alt="add" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>

              <tr>
                <th scope="row"><div align="center">1</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P" selected>PENDING</option>
                      <option value="A">APPROVED</option>

                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="2723456" size="12">
                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>

                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th  scope="row"><div align="center">2</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P">PENDING</option>

                      <option value="A" selected>APPROVED</option>
                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="4838685" size="12">
                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">

                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row">3</th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">

                      <option value=""></option>
                      <option value="P" selected>PENDING</option>
                      <option value="A">APPROVED</option>
                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="9345745" size="12">
                </span> </div></td>

                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="center">4</div></th>

                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P">PENDING</option>
                      <option value="A" selected>APPROVED</option>
                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">

                    <input name="textfield" type="text" value="2845345" size="12">
                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>

              <tr>
                <th  scope="row"><div align="center">5</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P" selected>PENDING</option>
                      <option value="A">APPROVED</option>

                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="3456622" size="12">
                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>

                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
            </table>
         </div>
          <!-- TAB -->
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>
              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle">Biosafety</td>

                <td class="tabtable1-mid1"><label>
                <input name="RadioGroup3" type="radio" class="nobord" value="radio">
                yes</label>
                <label>
                <input name="RadioGroup3" type="radio" class="nobord" value="radio">
                no</label></td>
              <td class="tabtable2-mid"><a id="A3" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F3" vspace=6></a> </td>

              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G3" style="display: none;">
            <div class="h2-container">
              <h2>Biosafety</h2>
            </div>
        
            <table cellpadding=0 cellspacing="0"  summary="">

              <tr>
                <td colspan=5 class="tab-subhead"><span class="left">Insert Study </span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th> <div align="center">Approval Status </div></th>
                <th> <div align="center">Study Number</div></th>

                <th><div align="center">Approval Date </div></th>
                <th >Action</th>
              </tr>
              <tr>
                <th scope="row">add:</th>
                <td class="infoline"><div align="center">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">

                      <option value="" selected="selected"></option>
                      <option value="P">PENDING</option>
                      <option value="A">APPROVED</option>
                    </select>
                </div></td>
                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="12">
                </div></td>

                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="12">
                    <img src="images/cal.gif" width="16" height="16"></div></td>
                <td class="infoline"><div align=center><a href="ib-multi09.html"><img src="images/tinybutton-add1.gif" alt="add" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="center">1</div></th>
                <td><div align="center"><span class="infoline">

                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P" selected>PENDING</option>
                      <option value="A">APPROVED</option>
                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="2723456" size="12">

                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>

                <th  scope="row"><div align="center">2</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P">PENDING</option>
                      <option value="A" selected>APPROVED</option>
                    </select>

                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="4838685" size="12">
                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>

              </tr>
              <tr>
                <th scope="row">3</th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P" selected>PENDING</option>
                      <option value="A">APPROVED</option>

                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="9345745" size="12">
                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>

                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="center">4</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">
                      <option value=""></option>
                      <option value="P">PENDING</option>

                      <option value="A" selected>APPROVED</option>
                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="2845345" size="12">
                </span> </div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">

                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th  scope="row"><div align="center">5</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="prtcl_aprv_pnd_ind_param.0" value="">

                      <option value=""></option>
                      <option value="P" selected>PENDING</option>
                      <option value="A">APPROVED</option>
                    </select>
                </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="3456622" size="12">
                </span> </div></td>

                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="12/14/05" size="12">
                    <img src="images/cal.gif" width="16" height="16"> </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
            </table>
          </div>
          <!-- TAB -->

          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>
              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle">Human Tissue/Fluids</td>
                 <td class="tabtable1-mid1"><label>
                <input name="RadioGroup4" type="radio" class="nobord" value="radio">
                yes</label>
                <label>
                <input name="RadioGroup4" type="radio" class="nobord" value="radio">

                no</label></td>
              <td class="tabtable2-mid"><a id="A4" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F4" vspace=6></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G4" style="display: none;">
            <div class="h2-container">
              <h2>Human Tissue/Fluids</h2>

            </div>
            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <th>Describe:</th>
                <td><span class="infoline">
                  <textarea name="textfield" cols="60" rows="5"></textarea>
                </span></td>
              </tr>

            </table>
            </div>
          <!-- TAB -->
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>
              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle">Pathogenic Agents</td>
                 <td class="tabtable1-mid1"><label>
                <input name="RadioGroup5" type="radio" class="nobord" value="radio">

                yes</label>
                <label>
                <input name="RadioGroup5" type="radio" class="nobord" value="radio">
                no</label></td>
              <td class="tabtable2-mid"><a id="A5" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F5" vspace=6></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>

          </table>
          <div class="tab-container" align="center" id="G5" style="display: none;">
            <div class="h2-container">
              <h2>Pathogenic Agents</h2>
            </div>
            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <th>Describe:</th>

                <td><span class="infoline">
                  <textarea name="textfield" cols="60" rows="5"></textarea>
                </span></td>
              </tr>
            </table>
          </div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>

              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle">Financial Conflict of Interest</td>
             <td class="tabtable1-mid1"><label>
                <input name="RadioGroup6" type="radio" class="nobord" value="radio">
                yes</label>
                <label>
                <input name="RadioGroup6" type="radio" class="nobord" value="radio">
                no</label></td>

              <td class="tabtable2-mid"><a id="A6" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F6" vspace=6></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G6" style="display: none;">
            <div class="h2-container">
              <h2>Financial Conflict of Interest</h2>
            </div>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <th>Identify Names:</th>
                <td><span class="infoline">
                  <textarea name="textfield" cols="60" rows="5"></textarea>
                </span></td>
              </tr>
            </table>

          </div>
          <!-- TAB -->
          <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
            <tr>
              <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
              <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
            </tr>
          </table>
        </div>
		
        <div class="globalbuttons"> <img src="images/buttonsmall_save.gif" alt="save" width="53" height="18" hspace="5" border="0"><img src="images/buttonsmall_cancel.gif" alt="cancel" width="66" height="18" hspace="5" border="0"> </div></td>
      <td class="column-right"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
    </tr>
  </table>
	
</kul:documentPage>