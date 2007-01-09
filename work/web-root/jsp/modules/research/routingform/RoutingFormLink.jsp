<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
	htmlFormAction="researchRoutingFormLink"
	headerDispatch="save" feedbackKey="app.krafeedback.link"
	headerTabActive="link">
  
  <kra-rf:routingFormHiddenDocumentFields />

	<div id="workarea" >

  <kul:tabTop tabTitle="Budget Link" defaultOpen="true">
  
          <div class="tab-container" align="center">
            <div class="h2-container">
              <h2>Budget Link</h2>
            </div>
            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Select A Budget</span> </td>
              </tr>
              <tr>
                <th align=right valign=middle width="25%">Budget Document Number:</th>
                <td colspan="3" align=left valign=middle nowrap >                  <input name="textfield" type="text" size="12">
                </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <td colspan="3" align=left valign=middle nowrap >
                  [load button]
                </td>
              </tr>
            </table>
            <br/>



            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Select Budget Periods</span> </td>
              </tr>
              <tr>
                <th class="bord-l-b">Period</th>
                <th class="bord-l-b">Direct Cost</th>
                <th class="bord-l-b">Indirect Cost</th>
                <th class="bord-l-b">Select</th>
              </tr>

              <tr>
                      <td class="datacell"><div class="nowrap" align="center"><strong>1</strong><span class="fineprint"><br>
                          (1/13/07 - 
                           1/14/07)</span></div>
                      </td>
                      <td class="datacell">
                        <div align="right">
                          23,456.00
                        </div>
                      </td>

                      
                      <td class="datacell">
                        <div align="right">
                          12,345.00</div>
                      </td>
                      
                      <td class="datacell"><div align="center"><input type="checkbox" name="selectPeriod" /></div></td>
                    </tr>
                    
                    <tr>
                      <td colspan="74" class="infoline" height="30"><div align="center">[link selected periods]</div></td>
                    </tr>
                  </table>


  </div>
  
  </kul:tabTop>

  <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
    <tr>
      <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
      <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
    </tr>
  </table>  
  
  </div>
	
</kul:documentPage>