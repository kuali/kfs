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
	htmlFormAction="researchRoutingFormResearchRisks" headerDispatch="save"
	feedbackKey="app.krafeedback.link" headerTabActive="researchrisks">

	<kra-rf:routingFormHiddenDocumentFields />


	<div class="msg-excol">
		<div class="right">
			<div class="excol">
				<input name="imageField" type="image" class="tinybutton"
					src="images/tinybutton-expandall.gif">
				<input name="imageField" type="image" class="tinybutton"
					src="images/tinybutton-collapseall.gif">
			</div>
		</div>
	</div>

	<table width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td class="column-left">
				<img src="images/pixel_clear.gif" alt="" width="20" height="20">
			</td>
			<td>
				<div id="workarea">

					<kra-rf:routingFormResearchRisksMultiLine>
					</kra-rf:routingFormResearchRisksMultiLine>

					<kra-rf:routingFormResearchRisksDescrption>
					</kra-rf:routingFormResearchRisksDescrption>

				</div>
			</td>
		</tr>
	</table>
</kul:documentPage>
