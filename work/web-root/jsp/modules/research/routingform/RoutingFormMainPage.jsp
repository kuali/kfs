<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/jsp/modules/research/routingform/RoutingFormMainPage.jsp,v $
 
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

<c:set var="routingFormMainPageAttributes"
	value="${DataDictionary['RoutingFormDocument'].attributes}" />
<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormMainPage" headerDispatch="save"
	feedbackKey="app.krafeedback.link" headerTabActive="mainpage"
	showTabButtons="true">

<kra-rf:routingFormHiddenDocumentFields excludeRoutingFormMainPage="true" />

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS_LESS_DOCUMENT}" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<SCRIPT type="text/javascript">
	    <!--
	        function submitForm() {
	            document.forms[0].submit();
	        }
	    //-->
	</SCRIPT>

	<kra-rf:routingFormMainPageAgencyDeliveryInfo editingMode="${KualiForm.editingMode}" />

    <!-- TAB -->

	<kra-rf:routingFormMainPagePersonnel editingMode="${KualiForm.editingMode}" />

    <!-- TAB -->

	<kra-rf:routingFormMainPageSubmissionDetails editingMode="${KualiForm.editingMode}" />

    <!-- TAB -->

	<!-- kra-rf:routingFormMainPageCustomAttributes editingMode="${KualiForm.editingMode}" /-->

    <!-- TAB -->

          <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
            <tr>
              <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
              <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
            </tr>

          </table>
        </div>
        <kul:documentControls transactionalDocument="false" suppressRoutingControls="true" viewOnly="${KualiForm.editingMode['viewOnly']}" />
      <td class="column-right"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>
    </tr>
  </table>
</kul:documentPage>

