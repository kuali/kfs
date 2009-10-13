<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<%@ attribute name="editingMode" required="true" type="java.util.Map"%>

<c:set var="allowAdditionalDeposits" value="${editingMode['allowAdditionalDeposits']}" />

<kul:tab tabTitle="Deposits" defaultOpen="true" tabErrorKey="${KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS}">    
    <div class="tab-container" align=center>
        <c:if test="${allowAdditionalDeposits}">
            <div align=left style="padding-left: 10px">
                <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_addInterimDeposit.gif" style="border: none" property="methodToCall.addInterimDeposit" title="create interim" alt="create interim deposit"/>
                <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_addFinalDeposit.gif" style="border: none" property="methodToCall.addFinalDeposit" title="create final deposit" alt="create final deposit"/>
            </div>
            <br>
        </c:if>

        <logic:iterate indexId="ctr" name="KualiForm" property="document.deposits" id="currentDeposit">
            <c:if test="${ctr == 0}">
                <hr>
            </c:if>

            <fp:deposit editingMode="${editingMode}" depositIndex="${ctr}" deposit="${currentDeposit}" />
                    
            <hr>
        </logic:iterate>
        
        <c:if test="${KualiForm.lastInterimDepositFinalizable}">
          <div align="left" style="padding-left: 10px">
            <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_finalizedepos.gif" style="border: none" property="methodToCall.finalizeLastInterimDeposit" title="make last interim deposit final" alt="make last interim deposit final" />
          </div>
        </c:if>
    </div>
</kul:tab>
