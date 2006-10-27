<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormProjectDetailsOtherProjectDetails.tag,v $
 
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
              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle">Other Project Details</td>

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
          <div class="tab-container" align="center" id="G4" style="display: none">
            <div class="h2-container">
              <h2>Other Project Details</h2>
            </div>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td><br>
                  <table width="100%" cellpadding="0"  cellspacing="0" class="nobord">
                    <tr>
                      <td width="20%" class="nobord"><div align="center">
                          <label>
                          <input type="radio" name="RadioGroup5" value="radio">
                          yes</label>

                          <label>
                          <input type="radio" name="RadioGroup5" value="radio">
                          no</label>
                        </div></td>
                      <td class="nobord">Is this project off campus? </td>
                    </tr>
                    <tr>
                      <td class="nobord"><div align="center">

                          <label>
                          <input type="radio" name="RadioGroup6" value="radio">
                          yes</label>
                          <label>
                          <input type="radio" name="RadioGroup6" value="radio">
                          no</label>
                        </div></td>
                      <td class="nobord">Is program income as anticipated? </td>

                    </tr>
                    <tr>
                      <td class="nobord"><div align="center">
                          <label>
                          <input type="radio" name="RadioGroup7" value="radio">
                          yes</label>
                          <label>
                          <input type="radio" name="RadioGroup7" value="radio">

                          no</label>
                        </div></td>
                      <td class="nobord">Have inventions been conceived or reduced to practice under prior research on this project?</td>
                    </tr>
                    <tr>
                      <td class="nobord"><div align="center">
                          <label>
                          <input type="radio" name="RadioGroup8" value="radio">

                          yes</label>
                          <label>
                          <input type="radio" name="RadioGroup8" value="radio">
                          no</label>
                        </div></td>
                      <td class="nobord">Will new space or remodeling be required?</td>
                    </tr>

                    <tr>
                      <td class="nobord"><div align="center">
                          <label>
                          <input type="radio" name="RadioGroup9" value="radio">
                          yes</label>
                          <label>
                          <input type="radio" name="RadioGroup9" value="radio">
                          no</label>

                        </div></td>
                      <td class="nobord">Will this project involve collaborative activities with foreign partners or will it have an international focus? </td>
                    </tr>
                    <tr>
                      <td class="nobord"><div align="center">
                          <label>
                          <input type="radio" name="RadioGroup10" value="radio">
                          yes</label>

                          <label>
                          <input type="radio" name="RadioGroup10" value="radio">
                          no</label>
                        </div></td>
                      <td class="nobord">Does this project involve foreign travel?</td>
                    </tr>
                  </table>
                  <br>

                </td>
              </tr>
            </table>
          </div>
