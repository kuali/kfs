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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>

<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.AUDIT_MODE_HEADER_TAB}" altText="page help"/>
</div>
      
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
	<tbody id="">
		<tr>
			<td><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3" id=""></td>
			<td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3" id=""></td>
		</tr>
	</tbody>
</table>
      
<div id="workarea">
	<div class="tab-container"  align="center">
		<div class="h2-container"> 
			<span class="subhead-left"> <h2>Audit Mode</h2> </span>
		</div>
		<table cellpadding=0 cellspacing="0"  summary="">
			<tr>
				<td>
					<div class="floaters">
						<p>You can activate an audit check to determine any errors or incomplete information. There are two types of audit errors. A hard audit error is an error that must be corrected prior to linking to Routing Form for the eventual submission into routing. A soft audit error is an error that serves as a warning only and will not prevent linking to Routing Form. </p>
						<p align="center">
							<c:choose>
								<c:when test="${KualiForm.auditActivated}"><html:image property="methodToCall.deactivate" src="images/tinybutton-deacaudit.gif" styleClass="tinybutton" /></c:when>
								<c:otherwise><html:image property="methodToCall.activate" src="images/tinybutton-activaudt.gif" styleClass="tinybutton" /></c:otherwise>
							</c:choose>
						</p>
					</div>
				</td>
			</tr>
		</table>
		<c:if test="${KualiForm.auditActivated}">
			<table cellpadding="0" cellspacing="0" summary="">
				<kra:auditSet title="Hard Errors" soft="false" auditType="hard"/>
				<kra:auditSet title="Soft Errors" soft="true" auditType="soft"/>
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
<div class="globalbuttons"> </div>
