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
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.TEMPLATE_HEADER_TAB}" altText="page help"/>
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
	<div class="tab-container"  align="center">
		<table cellpadding=0  summary="view/edit ad hoc recipients">
			<tbody>
				<tr>
					<td colspan=5 class="subhead">
						<span class="subhead-left"> Template</span>
					</td>
				</tr>
				<tr>
					<td colspan="5"  scope=col>
						<div align="center"><br>
							Once you click the &quot;Template&quot; button, the new Routing Form will display. <br><br>
							Copy Primary Delivery Address to the templated routing form?
							<html:multibox property="templateAddress" value="Y"/> <br>
							Copy Ad-Hoc Permissions to the templated routing form?
							<html:multibox property="templateAdHocPermissions" value="Y"/> <br>
							Copy Ad-Hoc Approvers to the templated budget?
							<html:multibox property="templateAdHocApprovers" value="Y"/> <br><br>
						</div>
					</td>
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
</div>

<div id="globalbuttons" class="globalbuttons">
	<html:image src="images/buttonsmall_template.gif" styleClass="globalbuttons" property="methodToCall.doTemplate" alt="Copy current document" />
</div>
