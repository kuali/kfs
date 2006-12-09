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

<c:set var="routingFormProjectDetailsAttributes"
	value="${DataDictionary['RoutingFormDocument'].attributes}" />
<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormProjectDetails"
	headerDispatch="save" feedbackKey="app.krafeedback.link"
	headerTabActive="projectdetails" showTabButtons="true">
	
<kra-rf:routingFormHiddenDocumentFields />

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<SCRIPT type="text/javascript">
	    <!--
	        function submitForm() {
	            document.forms[0].submit();
	        }
	    //-->
	</SCRIPT>

	<kra-rf:routingFormProjectDetailsCostShare editingMode="${KualiForm.editingMode}" />

    <!-- TAB -->

	<kra-rf:routingFormProjectDetailsSubcontracts editingMode="${KualiForm.editingMode}" />

    <!-- TAB -->

	<kra-rf:routingFormProjectDetailsOtherInstitutions editingMode="${KualiForm.editingMode}" />

    <!-- TAB -->

	<kra-rf:routingFormProjectDetailsOtherProjectDetails editingMode="${KualiForm.editingMode}" />



          <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
            <tr>
              <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
              <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
            </tr>

          </table>
        </div>
        <div class="globalbuttons"> <a href="overview.html"><img src="images/buttonsmall_save.gif" alt="save" width="53" height="18" hspace="5" border="0"></a><a href="confirm-cancel.html"><img src="images/buttonsmall_cancel.gif" alt="cancel" width="66" height="18" hspace="5" border="0"></a> </div></td>
      <td class="column-right"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
    </tr>
  </table>
  
</kul:documentPage>