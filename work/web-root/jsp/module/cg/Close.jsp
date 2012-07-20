<%--
 Copyright 2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:page showDocumentInfo="true"
	htmlFormAction="cgClose"
    renderMultipart="true"
	docTitle="Proposal/Award Close"
    transactionalDocument="false">

    <sys:hiddenDocumentFields isFinancialDocument="false" />
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    <kul:tab tabTitle="Close" defaultOpen="true"
             tabErrorKey="document.userInitiatedCloseDate,document.closeOnOrBeforeDate">
        <c:set var="closeAttributes" value="${DataDictionary.ProposalAwardCloseDocument.attributes}" />

         <div class="tab-container" align="center">

            <fieldset>
                <legend><b>Perform Close</b></legend>
                <table>
                    <tr>
                        <th style="text-align: right;"><kul:htmlAttributeLabel attributeEntry="${closeAttributes.userInitiatedCloseDate}" labelFor="document.userInitiatedCloseDate" useShortLabel="true" /></th>
                       	 <c:if test="${readOnly}">
                        <td style="width:50%">
                            	${KualiForm.document.userInitiatedCloseDate}&nbsp;
                        	</td>
						</c:if>
						<c:if test="${!readOnly}">
							<td style="width:50%">
                            <kul:dateInputNoAttributeEntry property="document.userInitiatedCloseDate" maxLength="10" size="10" />
                        </td>
                       	</c:if>
                    </tr>
                    <tr>
                        <th style="text-align: right;"><kul:htmlAttributeLabel attributeEntry="${closeAttributes.closeOnOrBeforeDate}" labelFor="document.closeOnOrBeforeDate" useShortLabel="true" /></th>
                       		<c:if test="${readOnly}">
                       	 		<td style="width:50%">
                            		${KualiForm.document.closeOnOrBeforeDate}&nbsp;
                        		</td>
							</c:if>
							<c:if test="${!readOnly}">
                        <td style="width:50%">
                            <kul:dateInputNoAttributeEntry property="document.closeOnOrBeforeDate"  maxLength="10" size="10" />
                        </td>
                     	  	</c:if>
                     
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
    <kul:superUserActions />
    <kul:panelFooter />

    <sys:documentControls transactionalDocument="true" />

</kul:page>
