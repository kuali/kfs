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

<c:set var="closeAttributes"
	value="${DataDictionary['CloseDocument'].attributes}" />
		
<kul:page showDocumentInfo="true"
	htmlFormAction="cgClose"
    renderMultipart="true"
	docTitle="Post-Award Close"
    transactionalDocument="false">

    <kul:hiddenDocumentFields isTransactionalDocument="false" />
    <kul:documentOverview editingMode="${KualiForm.editingMode}" />
    <kul:tab tabTitle="Close" defaultOpen="true"
             tabErrorKey="document.userInitiatedCloseDate,document.closeOnOrBeforeDate">
        <c:set var="closeAttributes" value="${DataDictionary.Close.attributes}" />

         <div class="tab-container" align="center">

            <fieldset>
                <legend><b>Perform Close</b></legend>
                <table>
                    <tr>
                        <th style="text-align: right;"><kul:htmlAttributeLabel attributeEntry="${closeAttributes.userInitiatedCloseDate}" useShortLabel="true" /></th>
                        <td style="width:50%">
                            <kul:dateInputNoAttributeEntry property="document.userInitiatedCloseDate" maxLength="10" size="10" />
                        </td>
                    </tr>
                    <tr>
                        <th style="text-align: right;"><kul:htmlAttributeLabel attributeEntry="${closeAttributes.closeOnOrBeforeDate}" useShortLabel="true" /></th>
                        <td style="width:50%">
                            <kul:dateInputNoAttributeEntry property="document.closeOnOrBeforeDate"  maxLength="10" size="10" />
                        </td>
                    </tr>
                </table>
            </fieldset>
            <fieldset>
                <legend><b>Close Information</b></legend>
                <table>
                    <tr>
                        <th style="text-align: right;">Date of Last Close:</th>
                        <td style="width:50%">${KualiForm.mostRecentClose.userInitiatedCloseDate}&nbsp;</td>
                    </tr>
                    <tr>
                        <th style="text-align: right;">Award Records Closed:</th>
                        <td style="width:50%">${KualiForm.mostRecentClose.awardClosedCount}&nbsp;</td>
                    </tr>
                    <tr>
                        <th style="text-align: right;">Proposal Records Closed:</th>
                        <td style="width:50%">${KualiForm.mostRecentClose.proposalClosedCount}&nbsp;</td>
                    </tr>
                </table>
            </fieldset>
        </div>
    </kul:tab>
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />

    <kul:documentControls transactionalDocument="true" />

</kul:page>