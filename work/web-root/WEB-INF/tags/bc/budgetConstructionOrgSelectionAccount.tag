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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<div class="tab-container" align="center"> 
    <div class="tab-container-error">
    </div>
    <table cellpadding=0 cellspacing="0"  summary="">
		<tr>
	        <td class="subhead" colspan="1">Selection Operation </td>
      	</tr>
          <tr>
			<td class="grid" valign="center" rowspan="1" colspan="6">
              <div align="center">
                 <br/>
                 <html:image property="methodToCall.performShowBudgetDocs" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Show Documents" alt="Show Documents" styleClass="tinybutton" />
                 <br/>&nbsp;
              </div>
           </td>
       </tr>
    </table>
</div> 
 