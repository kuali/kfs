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
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@page import="java.util.ArrayList, java.util.Iterator, java.util.List, 
                org.kuali.module.pdp.bo.Bank, org.kuali.module.pdp.form.customerprofile.CustomerProfileForm,
                org.kuali.module.pdp.bo.Bank, org.kuali.module.pdp.form.customerprofile.CustomerBankForm" %>
<app:getBank active="Y" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head>
    <html:base />
    <title>Customer Profile Maintenance</title>
  </head>
  <body>
<h1><strong>Customer Profile Maintenance</strong></h1>
<br>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
<html:form action="/pdp/customerprofilesave">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <tr>
       <td width="20">&nbsp;</td>
       <td>
         <br>
          <font color="#800000">
            <html:errors/>
          <br>
        </font>
      </td>
    </tr>
  </table>
    <br><br>
    <html:hidden property="id" />
    <table width="75%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t" align="center">
    <tbody>
      <tr align="left">
        <th align="left" valign="top" nowrap="true" colspan="4">
          <strong>
          <logic:empty name="PdpCustomerProfileForm" property="id"><div align="center">New Customer Profile</div></logic:empty>
          <logic:notEmpty name="PdpCustomerProfileForm" property="id">
            <logic:equal name="PdpCustomerProfileForm" property="id" value="0"><div align="center">New Customer Profile</div></logic:equal>
            <logic:notEqual name="PdpCustomerProfileForm" property="id" value="0"><div align="center">Customer Profile ID: <c:out value="${CustomerProfileForm.id}"/></div></logic:notEqual>
          </logic:notEmpty>
          </strong>
        </th>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          <font color=red>*</font> Chart:
         </th>
        <td align=left class="datacell">
          <html:hidden property="version" />
          <logic:empty name="PdpCustomerProfileForm" property="id">
            <html:text property="chartCode" tabindex="1" maxlength="2"  />
          </logic:empty>
          <logic:notEmpty name="PdpCustomerProfileForm" property="id">
            <bean:write name="PdpCustomerProfileForm" property="chartCode" />
            <html:hidden property="chartCode" />
          </logic:notEmpty>
        </td>
        <th align=right valign="top" nowrap="true">
          <font color=red>*</font> Default Chart:
         </th>
        <td align=left class="datacell" >
          <html:text property="defaultChartCode" tabindex="23" maxlength="2"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          <font color=red>*</font> Organization:
         </th>
        <td align=left class="datacell" >
          <logic:empty name="PdpCustomerProfileForm" property="id">
            <html:text property="orgCode" tabindex="2" maxlength="4"  />
          </logic:empty>
          <logic:notEmpty name="PdpCustomerProfileForm" property="id">
            <bean:write name="PdpCustomerProfileForm" property="orgCode" />
            <html:hidden property="orgCode" />
          </logic:notEmpty>
        </td>
        <th align=right valign="top" nowrap="true">
           <font color=red>*</font> Default Account #:
         </th>
        <td align=left class="datacell">
           <html:text property="defaultAccountNumber" tabindex="24" maxlength="7" />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          <font color=red>*</font> Sub-Unit:
         </th>
        <td align=left class="datacell" >
          <logic:empty name="PdpCustomerProfileForm" property="id">
            <html:text property="subUnitCode" tabindex="3" maxlength="4"  />
          </logic:empty>
          <logic:notEmpty name="PdpCustomerProfileForm" property="id">
            <bean:write name="PdpCustomerProfileForm" property="subUnitCode" />
            <html:hidden property="subUnitCode" />
          </logic:notEmpty>
        </td>
        <th align=right valign="top" nowrap="true">
           <font color=red>*</font> Default Sub Account #:
         </th>
        <td align=left class="datacell">
           <html:text property="defaultSubAccountNumber" tabindex="25" maxlength="5" />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Campus Process Location:
         </th>
        <td align=left class="datacell">
          <html:text property="defaultPhysicalCampusProcessingCode" tabindex="4" maxlength="2"  />
        </td>
         <th align=right valign="top" nowrap="true">
          <font color=red>*</font> Default Object Code:
         </th>
        <td align=left class="datacell">
          <html:text property="defaultObjectCode" tabindex="26" maxlength="4" />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Description:
         </th>
        <td align=left class="datacell">
          <html:text property="customerDescription" tabindex="5" maxlength="50"  />
        </td>
        <th align=right valign="top" nowrap="true">
          <font color=red>*</font> Default Sub Object Code:
         </th>
        <td align=left class="datacell" >
          <html:text property="defaultSubObjectCode" tabindex="27" maxlength="3"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Primary Contact Name:
         </th>
        <td align=left class="datacell">
          <html:text property="contactFullName" tabindex="6" maxlength="50" size="50" />
        </td>
        <th align=right valign="top" nowrap="true">
          Address Line 1:
         </th>
        <td align=left class="datacell">
          <html:text property="address1" tabindex="28" maxlength="55"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Customer Process E-Mail Address:  
         </th>
        <td align=left class="datacell" >
          <html:text property="processingEmailAddr" tabindex="7" maxlength="200" size="50" />
        </td>
        <th align=right valign="top" nowrap="true">
          Address Line 2:
         </th>
        <td align=left class="datacell">
          <html:text property="address2" tabindex="29" maxlength="55"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Payment Threshold Amount:
         </th>
        <td align=left class="datacell" >
          <html:text property="paymentThresholdAmount" tabindex="8" maxlength="14"  />
        </td>
        <th align=right valign="top" nowrap="true">
          Address Line 3:
         </th>
        <td align=left class="datacell">
          <html:text property="address3" tabindex="30" maxlength="55"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Payment Threshold E-Mail Address:  
         </th>
        <td align=left class="datacell" >
          <html:text property="paymentThresholdEmailAddress" tabindex="9" maxlength="200" size="50" />
        </td>
        <th align=right valign="top" nowrap="true">
          Address Line 4:
         </th>
        <td align=left class="datacell">
          <html:text property="address4" tabindex="31" maxlength="55"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          File Threshold Amount:  
         </th>
        <td align=left class="datacell" >
          <html:text property="fileThresholdAmount" tabindex="10" maxlength="14"  />
        </td>
        <th align=right valign="top" nowrap="true">
          City:  
         </th>
        <td align=left class="datacell" >
          <html:text property="city" tabindex="32" maxlength="30"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          File Threshold E-Mail Address:  
         </th>
        <td align=left class="datacell" >
          <html:text property="fileThresholdEmailAddress" tabindex="11" maxlength="200" size="50" />
        </td>
        <th align=right valign="top" nowrap="true">
          State:  
         </th>
        <td align=left class="datacell" >
          <html:text property="state" tabindex="33" maxlength="30"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Advice Heading Text:
         </th>
        <td align=left class="datacell">
          <html:text property="adviceHeaderText" tabindex="12" maxlength="400"  />
        </td>
        <th align=right valign="top" nowrap="true">
          Zip:  
         </th>
        <td align=left class="datacell" >
          <html:text property="zipCode" tabindex="34" maxlength="20"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Advice Subject Line Text:
         </th>
        <td align=left class="datacell">
          <html:text property="adviceSubjectLine" tabindex="13" maxlength="40"  />
        </td>
        <th align=right valign="top" nowrap="true">
          Country:  
         </th>
        <td align=left class="datacell" >
          <html:text property="countryName" tabindex="35" maxlength="30"  />
        </td>
      </tr>
      <tr>
         <th align=right valign="top" nowrap="true">
          Advice Return / Cancel E-Mail Address:
         </th>
        <td align=left class="datacell">
          <html:text property="adviceReturnEmailAddr" tabindex="14" maxlength="200" size="50" />
        </td>
        <th align=right valign="top" nowrap="true">
          Check Header Note Line 1:
         </th>
        <td align=left class="datacell">
          <html:text property="checkHeaderNoteTextLine1" tabindex="36" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          ACH Payment Detail Description:  
         </th>
        <td align=left class="datacell" >
          <html:text property="achPaymentDescription" tabindex="15" maxlength="100"  />
        </td>
        <th align=right valign="top" nowrap="true">
          Check Header Note Line 2:
         </th>
        <td align=left class="datacell">
          <html:text property="checkHeaderNoteTextLine2" tabindex="37" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Accounting Edit Required:
         </th>
        <td align=left class="datacell" >
          <logic:equal name="PdpCustomerProfileForm" property="accountingEditRequired" value="true" >
            <html:select size="1" property="accountingEditRequired" value="Y" tabindex="16">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:equal>
          <logic:notEqual name="PdpCustomerProfileForm" property="accountingEditRequired" value="true" >
            <html:select size="1" property="accountingEditRequired" value="N" tabindex="16">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:notEqual>
        </td>
        <th align=right valign="top" nowrap="true">
          Check Header Note Line 3:
         </th>
        <td align=left class="datacell">
          <html:text property="checkHeaderNoteTextLine3" tabindex="38" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Payee ID Required:
         </th>
        <td align=left class="datacell">
          <logic:equal name="PdpCustomerProfileForm" property="payeeIdRequired" value="true" >
            <html:select size="1" property="payeeIdRequired" value="Y" tabindex="17">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:equal>
          <logic:notEqual name="PdpCustomerProfileForm" property="payeeIdRequired" value="true" >
            <html:select size="1" property="payeeIdRequired" value="N" tabindex="17">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:notEqual>
        </td>
        <th align=right valign="top" nowrap="true">
          Check Header Note Line 4:
         </th>
        <td align=left class="datacell">
          <html:text property="checkHeaderNoteTextLine4" tabindex="39" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Ownership Codes Required:  
         </th>
        <td align=left class="datacell" >
          <logic:equal name="PdpCustomerProfileForm" property="ownershipCodeRequired" value="true" >
            <html:select size="1" property="ownershipCodeRequired" value="Y" tabindex="18">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:equal>
          <logic:notEqual name="PdpCustomerProfileForm" property="ownershipCodeRequired" value="true" >
            <html:select size="1" property="ownershipCodeRequired" value="N" tabindex="18">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:notEqual>
        </td>
        <th align=right valign="top" nowrap="true">
          Additional Check Note Line 1:  
         </th>
        <td align=left class="datacell" >
          <html:text property="additionalCheckNoteTextLine1" tabindex="40" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          ACH Advice Flag:
         </th>
        <td align=left class="datacell">
          <logic:equal name="PdpCustomerProfileForm" property="adviceCreate" value="true" >
            <html:select size="1" property="adviceCreate" value="Y" tabindex="19">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:equal>
          <logic:notEqual name="PdpCustomerProfileForm" property="adviceCreate" value="true" >
            <html:select size="1" property="adviceCreate" value="N" tabindex="19">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:notEqual>
        </td>
        <th align=right valign="top" nowrap="true">
          Additional Check Note Line 2:  
         </th>
        <td align=left class="datacell" >
          <html:text property="additionalCheckNoteTextLine2" tabindex="41" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Employee Check Indicator:
         </th>
        <td align=left class="datacell">
          <logic:equal name="PdpCustomerProfileForm" property="employeeCheck" value="true" >
            <html:select size="1" property="employeeCheck" value="Y" tabindex="20">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:equal>
          <logic:notEqual name="PdpCustomerProfileForm" property="employeeCheck" value="true" >
            <html:select size="1" property="employeeCheck" value="N" tabindex="20">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:notEqual>
        </td>
        <th align=right valign="top" nowrap="true">
          Additional Check Note Line 3:  
         </th>
        <td align=left class="datacell" >
          <html:text property="additionalCheckNoteTextLine3" tabindex="42" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          NRA Check Indicator:  
         </th>
        <td align=left class="datacell">
          <logic:equal name="PdpCustomerProfileForm" property="nraReview" value="true" >
            <html:select size="1" property="nraReview" value="Y" tabindex="21">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:equal>
          <logic:notEqual name="PdpCustomerProfileForm" property="nraReview" value="true" >
            <html:select size="1" property="nraReview" value="N" tabindex="21">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:notEqual>
         </td>
        <th align=right valign="top" nowrap="true">
          Additional Check Note Line 4:  
         </th>
        <td align=left class="datacell" >
          <html:text property="additionalCheckNoteTextLine4" tabindex="43" maxlength="90"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Relieve Liabilities:
        </th>
        <td align=left class="datacell">
          <logic:equal name="PdpCustomerProfileForm" property="relieveLiabilities" value="true" >
            <html:select size="1" property="relieveLiabilities" value="Y" tabindex="21">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:equal>
          <logic:notEqual name="PdpCustomerProfileForm" property="relieveLiabilities" value="true" >
            <html:select size="1" property="relieveLiabilities" value="N" tabindex="21">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:notEqual>
        </td>
        <th align=right valign="top" nowrap="true">
          Direct Deposit Sign-Up Code <br>(PSD Transaction Code):
         </th>
        <td align=left class="datacell">
          <html:text property="psdTransactionCode" tabindex="44" maxlength="4"  />
        </td>
      </tr>
      <tr>
        <th align=right valign="top" nowrap="true">
          Profile Active Indicator:  
        </th>
        <td align=left class="datacell" >
          <logic:empty name="PdpCustomerProfileForm" property="id">
            <html:select size="1" property="customerActive" value="Y" tabindex="22">
              <html:option value="N">No</html:option>
              <html:option value="Y">Yes</html:option>
            </html:select>
          </logic:empty>
          <logic:notEmpty name="PdpCustomerProfileForm" property="id">
            <logic:equal name="PdpCustomerProfileForm" property="id" value="0">
              <html:select size="1" property="customerActive" value="Y" tabindex="22">
                <html:option value="N">No</html:option>
                <html:option value="Y">Yes</html:option>
              </html:select>
            </logic:equal>
            <logic:notEqual name="PdpCustomerProfileForm" property="id" value="0"> 
              <logic:equal name="PdpCustomerProfileForm" property="customerActive" value="true" >
                <html:select size="1" property="customerActive" value="Y" tabindex="22">
                  <html:option value="N">No</html:option>
                  <html:option value="Y">Yes</html:option>
                </html:select>
              </logic:equal>
              <logic:notEqual name="PdpCustomerProfileForm" property="customerActive" value="true" >
                <html:select size="1" property="customerActive" value="N" tabindex="22">
                  <html:option value="N">No</html:option>
                  <html:option value="Y">Yes</html:option>
                </html:select>
              </logic:notEqual>
            </logic:notEqual>
          </logic:notEmpty>
        </td>
        <th align=right valign="top" nowrap="true">&nbsp;</th>
        <td align=left class="datacell" >&nbsp;</td>
      </tr>
    </tbody>
  </table>
  <br>
  <table width="50%" border=0 cellpadding=3 cellspacing=0 class="bord-r-t" align="center">
    <tbody>
      <tr valign=middle align=left>
        <th align="center" valign="middle" nowrap=nowrap>
          Disbursement Type
        </th>
        <th align="center" valign="middle" nowrap=nowrap>
          Bank
        </th>
      </tr>
      <logic:iterate id="CustomerBankType" name="PdpCustomerProfileForm" property="customerBankForms" indexId="i">
        <tr>
          <td align=right class="datacell">
          <c:out value="${CustomerBankType.disbursementDescription}"/>
          <html:hidden property="customerBankForms[${i}].disbursementDescription"/>
          <html:hidden property="customerBankForms[${i}].disbursementTypeCode"/>
          </td>
          <td align=left class="datacell">
            <html:select size="1" property="customerBankForms[${i}].bankId">
              <html:option value="none">None</html:option>
              
              
              <% 
                  //  In this section we are taking a list of all active banks and getting
                 //  each according to their DisbursementType
                 
                 List disbursementTypeBankList = new ArrayList();
                 CustomerProfileForm cpf = (CustomerProfileForm)request.getAttribute("PdpCustomerProfileForm");
                 List bankList = (List)pageContext.getAttribute("BankList");
                 String disbursementTypeCode = ((CustomerBankForm)cpf.getCustomerBankForms()[i.intValue()]).getDisbursementTypeCode();
                 for(Iterator iter = bankList.iterator(); iter.hasNext();) {
                   Bank b = (Bank)iter.next();
                   if (b.getDisbursementType().getCode().equals(disbursementTypeCode)) {
                     disbursementTypeBankList.add(b);
                   }
                 }
                 request.setAttribute("disbursementBanks",disbursementTypeBankList);
              %>
              <html:optionsCollection name="disbursementBanks" value="id" label="name" />
            </html:select>
          </td>
        </tr>
      </logic:iterate>
    </tbody>
  </table>
  <br>
  <table cellpadding=0 width="100%" cellspacing=0 border=0>
    <tbody>
      <tr valign=middle align=left>
        <td align="center" valign="middle" nowrap=nowrap>
          <input type="image" name="btnSave" src="<%= request.getContextPath().toString() %>/pdp/images/button_save.gif" alt="Save" >
          <logic:empty name="PdpCustomerProfileForm" property="id">
            <!-- <html:reset>Clear</html:reset> -->
            <input type="image" name="btnClear" src="<%= request.getContextPath().toString() %>/pdp/images/button_clearfields.gif" alt="Clear" >
          </logic:empty>
          <input type="image" name="btnCancel" src="<%= request.getContextPath().toString() %>/pdp/images/button_cancel.gif" alt="Cancel" > 
        </td>
      </tr>
    <tr valign="middle" align="left">
       <td align="right" nowrap="nowrap"><font color="red">*</font> Required Field</td>
       <td>&nbsp;</td>
    </tr>
    </tbody>
  </table>
</html:form>
<c:import url="/backdoor.jsp"/>
  </body>
</html:html>
