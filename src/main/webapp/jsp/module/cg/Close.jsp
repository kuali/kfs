<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
