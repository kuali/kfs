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

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<kul:errors />

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
							Once you click the &quot;Template&quot; button, the new budget will display. <br><br>
							Copy Ad-Hoc Permissions to the templated budget?
							<html:multibox property="includeAdHocPermissions" value="Y"/> <br><br>
							Copy ICR Rates from this budget to the templated budget?
							<html:multibox property="includeBudgetIdcRates" value="Y"/> <br><br>
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
