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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<%@ attribute name="formActionName" required="true" description="The controller method that the lookup form should post against." %>

<kul:page lookup="true" showDocumentInfo="false"
          htmlFormAction="${formActionName}"
          headerMenuBar="${KualiForm.lookupable.htmlMenuBar}"
          headerTitle="Lookup" docTitle="" transactionalDocument="false">

    <div class="headerarea-small" id="headerarea-small">
        <h1><c:out value="${KualiForm.lookupable.title}"/> <kul:help
                resourceKey="lookupHelpText" altText="lookup help"/></h1>
    </div>

    <kul:enterKey methodToCall="search"/>

    <html-el:hidden name="KualiForm" property="backLocation"/>
    <html-el:hidden name="KualiForm" property="formKey"/>
    <html-el:hidden name="KualiForm" property="lookupableImplServiceName"/>
    <html-el:hidden name="KualiForm" property="businessObjectClassName"/>
    <html-el:hidden name="KualiForm" property="conversionFields"/>
    <html-el:hidden name="KualiForm" property="hideReturnLink"/>
    
    <kul:errors errorTitle="Errors found in Search Criteria:"/>

    <table width="100%" cellspacing="0" cellpadding="0">
        <tr>
            <td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
                                height="20"/></td>
            <td>
                <div id="lookup" align="center"><br/>
                    <br/>
                    <table class="datatable-100" align="center" cellpadding="0"
                           cellspacing="0">
                        <c:set var="FormName" value="KualiForm" scope="request"/>
                        <c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
                               scope="request"/>
                        <c:set var="ActionName" value="glModifiedInquiry.do" scope="request"/>
                        <c:set var="IsLookupDisplay" value="true" scope="request"/>

                        <kul:rowDisplay rows="${KualiForm.lookupable.rows}"/> `
						
                        <tr align=center>
                            <td height="30" colspan=2 class="infoline">
                                <html:image property="methodToCall.search" value="search"
                                            src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif"
                                            styleClass="tinybutton" alt="search" title="search" border="0"/>
                                <html:image property="methodToCall.clearValues" value="clearValues"
                                            src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif"
                                            styleClass="tinybutton" alt="clear" title="clear" border="0"/>


                                <html:image property="methodToCall.cancel" value="cancel"
                                            src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
                                            styleClass="tinybutton" alt="cancel" title="cancel" border="0"/>

                                <!-- Optional extra button -->
                               <c:set var="extraButtons"
										value="${KualiForm.extraButtons}" />
									<div id="globalbuttons" class="globalbuttons">
										<c:if test="${!empty extraButtons}">
											<c:forEach items="${extraButtons}" var="extraButton">
												<html:image src="${extraButton.extraButtonSource}"
													styleClass="globalbuttons"
													property="${extraButton.extraButtonProperty}"
													title="${extraButton.extraButtonAltText}"
													alt="${extraButton.extraButtonAltText}" />
											</c:forEach>
										</c:if>
									</div>
								</td>
                        </tr>
                    </table>
                </div>
				<jsp:doBody/>
			</td>
            <td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" height="20" width="20"></td>
        </tr>
    </table>
    <br/>
    <br/>
</kul:page>
