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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormNotes" headerDispatch="save"
	feedbackKey="app.krafeedback.link" headerTabActive="notes">
	
  <div class="right">
         
  </div>
</div>
<table cellpadding="0" cellspacing="0" width="100%">
    <tbody><tr>
        <td width="1%"><img src="images/pixel_clear.gif" alt="" height="20" width="20"></td>
        <td>

<div id="workarea">


	<table class="tab" summary="" cellpadding="0" cellspacing="0"
		width="100%">
		<tbody>
			<tr>
				<td class="tabtable1-left"><img src="images/tab-topleft.gif"
					alt="" align="absmiddle" height="29" width="12"> <a name="0"></a>
				Notes and Attachments (0)</td>
				<td class="tabtable1-mid"><input
					name="methodToCall.toggleTab.tab0" src="images/tinybutton-hide.gif"
					onclick="javascript: return toggleTab(document, 0); "
					class="tinybutton" id="tab-0-imageToggle" alt="hide" type="image">

				</td>
				<td class="tabtable1-right"><img src="images/tab-topright.gif"
					alt="" align="middle" height="29" width="12"></td>
			</tr>
		</tbody>
	</table>

	<div style="display: block;" id="tab-0-div"><!-- display errors for this tab -->

	<div class="tab-container-error">
	<div class="left-errmsg-tab"></div>
	</div>

	<!-- Before the jsp:doBody of the kul:tab tag -->

	<div class="tab-container" id="G4" align="center">

	<div class="h2-container">
	<h2>Notes and Attachments</h2>
	</div>
	<table class="datatable" summary="view/add notes" cellpadding="0"
		cellspacing="0">
		<tbody>

			<tr>

				<th rowspan="1" colspan="1" scope="col" align="left">&nbsp;

				&nbsp;</th>

				<th rowspan="1" colspan="1" scope="col" align="left">&nbsp; <a
					href="help.do?methodToCall=getAttributeHelpText&amp;businessObjectClassName=org.kuali.core.document.DocumentNote&amp;attributeName=finDocNotePostedDttmStamp"
					tabindex="32767" target="helpWindow">Posted Timestamp </a></th>

				<th rowspan="1" colspan="1" scope="col" align="left">&nbsp; <a
					href="help.do?methodToCall=getAttributeHelpText&amp;businessObjectClassName=org.kuali.core.document.DocumentNote&amp;attributeName=finDocumentAuthorUniversalId"
					tabindex="32767" target="helpWindow">Author </a></th>

				<th rowspan="1" colspan="1" scope="col" align="left">&nbsp; <font
					color="">*&nbsp;</font><a
					href="help.do?methodToCall=getAttributeHelpText&amp;businessObjectClassName=org.kuali.core.document.DocumentNote&amp;attributeName=financialDocumentNoteText"
					tabindex="32767" target="helpWindow">Note Text </a></th>

				<th rowspan="1" colspan="1" scope="col" align="left">&nbsp; <a
					href="help.do?methodToCall=getAttributeHelpText&amp;businessObjectClassName=org.kuali.core.document.DocumentNote&amp;attributeName=attachment"
					tabindex="32767" target="helpWindow">Attached File </a></th>

				<th rowspan="1" colspan="1" scope="col">&nbsp; Actions</th>

			</tr>
			<tr>

				<th rowspan="1" colspan="1" scope="row">&nbsp; add:</th>

				<td class="infoline">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td class="infoline">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td class="infoline"><textarea
					name="newDocumentNote.financialDocumentNoteText" tabindex="0"
					cols="50" rows="3" style=""
					id="newDocumentNote.financialDocumentNoteText"></textarea></td>
				<td class="infoline">
				<div align="center"><br>
				<input size="30" value="" type="text"><br>
				<br>
				<input name="methodToCall.cancelAttachment"
					src="images/tinygrey-cancel.gif" class="tinybutton" alt="cancel"
					type="image"></div>

				</td>

				<td class="infoline">
				<div align="center"><input name="methodToCall.insertNote"
					src="images/tinybutton-add1.gif" class="tinybutton" alt="insert"
					type="image"></div>
				</td>
			</tr>

		</tbody>
	</table>
	</div>

	<!-- After the jsp:doBody of the kul:tab tag --></div>

	<table class="b3" summary="" border="0" cellpadding="0" cellspacing="0"
		width="100%">

		<tbody>
			<tr>
				<td class="footer" align="left"><img
					src="images/pixel_clear.gif" alt="" class="bl3" height="14"
					width="12"></td>
				<td class="footer-right" align="right"><img
					src="images/pixel_clear.gif" alt="" class="br3" height="14"
					width="12"></td>
			</tr>
		</tbody>
	</table>
	
	</div>

	<div class="left-errmsg"></div>

	<p>&nbsp;</p>

	</td>
	<td width="20"><img src="images/pixel_clear.gif" alt=""
		height="20" width="20"></td>
	</tr>
	</tbody>
	</table>

</kul:documentPage>
