<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">

            <tr>
              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle">Other Institutions</td>
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
              <h2>Other Institutions</h2>
            </div>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Default Institutions</span> </td>
              </tr>
              <tr>
                <th width="50">&nbsp;</th>
                <th> <div align="center">Chart</div></th>

                <th> <div align="center">Org</div></th>
                <th >Action</th>
              </tr>
              <tr>
                <th scope="row"><div align="center">1</div></th>
                <td><div align="center"> BL</div></td>

                <td><div align="center"> CARD </div></td>
                <td><div align=center><a href="ib10c.html"><img src="images/tinybutton-delete2.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a> </div></td>
              </tr>
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Ad Hoc Institutions </span> </td>
              </tr>

              <tr>
                <th scope="row">add:</th>
                <td class="infoline"><div align="center">
                    <select name="newTargetLine.chartOfAccountsCode" tabindex="0" onchange="" onblur="loadChartInfo( this.name, 'newTargetLine.chart.finChartOfAccountDescription');" style="" class="">
                      <option value=""></option>
                      <option value="BA">BA</option>
                      <option value="BL">BL</option>

                      <option value="EA">EA</option>
                      <option value="FW">FW</option>
                      <option value="HO">HO</option>
                      <option value="IA">IA</option>
                      <option value="IN">IN</option>
                      <option value="IU">IU</option>

                      <option value="KO">KO</option>
                      <option value="NW">NW</option>
                      <option value="SB">SB</option>
                      <option value="SE">SE</option>
                      <option value="UA">UA</option>
                    </select>

                  </div></td>
                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="12">
                    <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td class="infoline"><div align=center><a href="ib-multi09.html"><img src="images/tinybutton-add1.gif" alt="add" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="center">1</div></th>

                <td><div align="center"><span class="infoline">
                    <select name="newTargetLine.chartOfAccountsCode" tabindex="0" onchange="" onblur="loadChartInfo( this.name, 'newTargetLine.chart.finChartOfAccountDescription');" style="" class="">
                      <option value=""></option>
                      <option value="BA" selected>BA</option>
                      <option value="BL">BL</option>
                      <option value="EA">EA</option>
                      <option value="FW">FW</option>

                      <option value="HO">HO</option>
                      <option value="IA">IA</option>
                      <option value="IN">IN</option>
                      <option value="IU">IU</option>
                      <option value="KO">KO</option>
                      <option value="NW">NW</option>

                      <option value="SB">SB</option>
                      <option value="SE">SE</option>
                      <option value="UA">UA</option>
                    </select>
                    </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="CARD" size="12">

                    </span> <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th  scope="row"><div align="center">2</div></th>
                <td><div align="center"><span class="infoline">
                    <select name="newTargetLine.chartOfAccountsCode" tabindex="0" onchange="" onblur="loadChartInfo( this.name, 'newTargetLine.chart.finChartOfAccountDescription');" style="" class="">

                      <option value=""></option>
                      <option value="BA">BA</option>
                      <option value="BL">BL</option>
                      <option value="EA">EA</option>
                      <option value="FW">FW</option>
                      <option value="HO">HO</option>

                      <option value="IA" selected>IA</option>
                      <option value="IN">IN</option>
                      <option value="IU">IU</option>
                      <option value="KO">KO</option>
                      <option value="NW">NW</option>
                      <option value="SB">SB</option>

                      <option value="SE">SE</option>
                      <option value="UA">UA</option>
                    </select>
                    </span></div></td>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="UITS" size="12">
                    </span> <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>

                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
            </table>
          </div>
