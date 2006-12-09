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
	htmlFormAction="researchRoutingFormPersonnel"
	headerDispatch="save" feedbackKey="app.krafeedback.link"
	headerTabActive="personnel">
	
	<div class="msg-excol">
    <div class="left-errmsg">
      <div class="error"></div>
    </div>
    <div class="right">

      <div class="excol">
        <input name="imageField" type="image" class="tinybutton" src="images/tinybutton-expandall.gif">
        <input name="imageField" type="image" class="tinybutton" src="images/tinybutton-collapseall.gif">
      </div>
    </div>
  </div>
  <table width="100%" cellpadding="0" cellspacing="0">
    <tr>
      <td class="column-left"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>

      <td><div id="workarea">
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>
              <td class="tabtable1-left"><img src="images/tab-topleft.gif" alt="" width="12" height="29" align="absmiddle"> Fields, Brandon</td>
              <td class="tabtable1-mid1">Other Professional</td>
              <td class="tabtable1-mid"><span class="tabtable2-mid"><a id="A02" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F02" ></a></span> </td>
              <td class="tabtable1-right"><img src="images/tab-topright.gif" alt="" width="12" height="29" align="absmiddle"></td>

            </tr>
          </table>
          <!-- TAB -->
          <div class="tab-container" align="center" id="G02" style="display: none;">
            <div class="h2-container">
              <h2><span class="subhead-left"> Fields, Brandon</span></h2>
              <span class="subhead-right"> <span class="subhead"><a href="asdf.html"><img src="images/my_cp_inf.gif" alt="help" width="15" height="14" border="0" align="absmiddle"></a></span> </span> </div>

            <table cellpadding=0 cellspacing="0" class="datatable">
              <tr class="datatable">
                <th  align="right">Role:</th>
                <td >Other Professional</td>
                <th  align="right">Street 1:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber638' value='' size='16' style="" ></td>
              </tr>

              <tr class="datatable">
                <th  align="right">First Name:</th>
                <td >Brandon</td>
                <th  align="right">Street 2:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber639' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">

                <th  align="right">Middle Name:</th>
                <td >R</td>
                <th  align="right">City:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6310' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Last Name:</th>

                <td >Fields</td>
                <th  align="right">County:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6311' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Prefix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber633' value='' size='16' style="" ></td>

                <th  align="right">State:</th>
                <td ><select name="select" size="1" tabindex="6">
                    <option value="" selected>select:</option>
                    <option value="--">Out Of Country</option>
                    <option value="AK">Alaska</option>
                    <option value="AL">Alabama</option>

                    <option value="AR">Arkansas</option>
                    <option value="AZ">Arizona</option>
                    <option value="CA">California</option>
                    <option value="CO">Colorado</option>
                    <option value="CT">Connecticut</option>
                    <option value="DC">District Of Columbia</option>

                    <option value="DE">Delaware</option>
                    <option value="FL">Florida</option>
                    <option value="GA">Georgia</option>
                    <option value="HI">Hawaii</option>
                    <option value="IA">Iowa</option>
                    <option value="ID">Idaho</option>

                    <option value="IL">Illinois</option>
                    <option value="IN">Indiana</option>
                    <option value="KS">Kansas</option>
                    <option value="KY">Kentucky</option>
                    <option value="LA">Louisiana</option>
                    <option value="MA">Massachusetts</option>

                    <option value="MD">Maryland</option>
                    <option value="ME">Maine</option>
                    <option value="MI">Michigan</option>
                    <option value="MN">Minnesota</option>
                    <option value="MO">Missouri</option>
                    <option value="MS">Mississippi</option>

                    <option value="MT">Montana</option>
                    <option value="NC">North Carolina</option>
                    <option value="ND">North Dakota</option>
                    <option value="NE">Nebraska</option>
                    <option value="NH">New Hampshire</option>
                    <option value="NJ">New Jersey</option>

                    <option value="NM">New Mexico</option>
                    <option value="NV">Nevada</option>
                    <option value="NY">New York</option>
                    <option value="OH">Ohio</option>
                    <option value="OK">Oklahoma</option>
                    <option value="OR">Oregon</option>

                    <option value="PA">Pennsylvania</option>
                    <option value="PR">Puerto Rico</option>
                    <option value="RI">Rhode Island</option>
                    <option value="SC">South Carolina</option>
                    <option value="SD">South Dakota</option>
                    <option value="TN">Tennessee</option>

                    <option value="TX">Texas</option>
                    <option value="UT">Utah</option>
                    <option value="VA">Virginia</option>
                    <option value="VT">Vermont</option>
                    <option value="WA">Washington</option>
                    <option value="WI">Wisconsin</option>

                    <option value="WV">West Virginia</option>
                    <option value="WY">Wyoming</option>
                  </select></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Suffix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber634' value='' size='16' style="" ></td>

                <th  align="right">Country:</th>
                <td ><select name="select2" size="1" tabindex="9">
                    <option value="" selected>select:</option>
                    <option value="US">United States</option>
                    <option value="AF">Afghanistan</option>
                    <option value="AL">Albania</option>

                    <option value="AG">Algeria</option>
                    <option value="AQ">American Samoa</option>
                    <option value="AN">Andorra</option>
                    <option value="AO">Angola</option>
                    <option value="AV">Anguilla</option>
                    <option value="AY">Antarctica</option>

                    <option value="AC">Antigua &amp; Barbuda</option>
                    <option value="AR">Argentina</option>
                    <option value="AM">Armenia</option>
                    <option value="AA">Aruba</option>
                    <option value="AT">Ashmore &amp; Caratier Island</option>

                    <option value="AS">Australia</option>
                    <option value="AU">Austria</option>
                    <option value="AJ">Azerbaijan</option>
                    <option value="BF">Bahamas</option>
                    <option value="BA">Bahrain</option>
                    <option value="FQ">Baker Island</option>

                    <option value="BG">Bangladesh</option>
                    <option value="BB">Barbados</option>
                    <option value="BS">Bassas Da India</option>
                    <option value="BO">Belarus</option>
                    <option value="BE">Belgium</option>
                    <option value="BH">Belize</option>

                    <option value="BN">Benin</option>
                    <option value="BD">Bermuda</option>
                    <option value="BT">Bhutan</option>
                    <option value="BL">Boliva</option>
                    <option value="BK">Bosnia &amp; Hercegovina</option>

                    <option value="BC">Botswana</option>
                    <option value="BV">Bouvet Island</option>
                    <option value="BR">Brazil</option>
                    <option value="IO">British Indian Ocean Territory</option>
                    <option value="BX">Brunei</option>
                    <option value="BU">Bulgaria</option>

                    <option value="BY">Burundi</option>
                    <option value="CB">Cambodia</option>
                    <option value="CM">Cameroon</option>
                    <option value="CA">Canada</option>
                    <option value="CV">Cape Verde</option>
                    <option value="CJ">Cayman Islands</option>

                    <option value="CT">Central African Republic</option>
                    <option value="CD">Chad</option>
                    <option value="CI">Chile</option>
                    <option value="CH">China</option>
                    <option value="KT">Christmas Island - Indian Ocean</option>
                    <option value="IP">Clipperton Island</option>

                    <option value="CO">Colombia</option>
                    <option value="CN">Comoros</option>
                    <option value="CF">Congo</option>
                    <option value="CW">Cook Islands</option>
                    <option value="CR">Coral Sea Islands Territory</option>
                    <option value="CS">Costa Rica</option>

                    <option value="HR">Croatia</option>
                    <option value="CY">Cyprus</option>
                    <option value="EZ">Czech Republic</option>
                    <option value="CZ">Czechoslovakia</option>
                    <option value="DA">Denmark</option>
                    <option value="DJ">Djibouti</option>

                    <option value="DO">Dominica</option>
                    <option value="DR">Dominican Republic</option>
                    <option value="EC">Ecuador</option>
                    <option value="EG">Egypt</option>
                    <option value="ES">El Salvador</option>
                    <option value="EK">Equatorial Guinea</option>

                    <option value="ER">Eritrea</option>
                    <option value="EN">Estonia</option>
                    <option value="ET">Ethiopia</option>
                    <option value="EU">Europa Island</option>
                    <option value="FA">Falkland Islands</option>
                    <option value="FO">Faroe Islands</option>

                    <option value="FJ">Fiji</option>
                    <option value="FI">Finland</option>
                    <option value="FR">France</option>
                    <option value="FG">French Guiana</option>
                    <option value="FP">French Polynesia</option>
                    <option value="FS">French Southern &amp; Antarctic Lands</option>

                    <option value="GB">Gabon</option>
                    <option value="GA">Gambia</option>
                    <option value="GZ">Gaza Strip</option>
                    <option value="GG">Georgia</option>
                    <option value="GE">Germany</option>
                    <option value="GH">Ghana</option>

                    <option value="GI">Gibraltar</option>
                    <option value="GO">Glorioso Islands</option>
                    <option value="GR">Greece</option>
                    <option value="GL">Greenland</option>
                    <option value="GJ">Grenada</option>
                    <option value="GP">Guadeloupe</option>

                    <option value="GQ">Guam</option>
                    <option value="GT">Guatemala</option>
                    <option value="GK">Guernsey</option>
                    <option value="GV">Guinea</option>
                    <option value="PU">Guinea-Bissau</option>
                    <option value="GY">Guyana</option>

                    <option value="HA">Haita</option>
                    <option value="HM">Heard Island &amp; Mcdonald Islands</option>
                    <option value="HO">Honduras</option>
                    <option value="HK">Hong Kong</option>
                    <option value="HQ">Howland Island</option>

                    <option value="HU">Hungary</option>
                    <option value="IC">Iceland</option>
                    <option value="IN">India</option>
                    <option value="ID">Indonesia</option>
                    <option value="IZ">Iraq</option>
                    <option value="IY">Iraq-Saudi Arabia</option>

                    <option value="EI">Ireland</option>
                    <option value="IM">Isle of Man</option>
                    <option value="IS">Israel</option>
                    <option value="IT">Italy</option>
                    <option value="IV">Ivory Coast</option>
                    <option value="JM">Jamaica</option>

                    <option value="JN">Jan Mayen</option>
                    <option value="JA">Japan</option>
                    <option value="JE">Jersey</option>
                    <option value="JQ">Johnston Atoll</option>
                    <option value="JO">Jordan</option>
                    <option value="JU">Juan De Nova Island</option>

                    <option value="KZ">Kazakhstan</option>
                    <option value="CK">Keeling Island</option>
                    <option value="KE">Kenya</option>
                    <option value="KQ">Kingman Reef</option>
                    <option value="KR">Kiribati Christmas Island</option>
                    <option value="KS">Korea South</option>

                    <option value="KU">Kuwait</option>
                    <option value="KG">Kyrgyzstan</option>
                    <option value="LA">Laos</option>
                    <option value="LG">Latvia</option>
                    <option value="LE">Lebanon</option>
                    <option value="LT">Lesotho</option>

                    <option value="LI">Liberia</option>
                    <option value="LS">Liechtenstein</option>
                    <option value="LH">Lithuania</option>
                    <option value="LU">Luxembourg</option>
                    <option value="MC">Macai</option>
                    <option value="MK">Macedonia</option>

                    <option value="MA">Madagascar</option>
                    <option value="MI">Malawi</option>
                    <option value="MY">Malaysia</option>
                    <option value="MV">Maldives</option>
                    <option value="ML">Mali</option>
                    <option value="MT">Malta</option>

                    <option value="RM">Marshall Islands</option>
                    <option value="MB">Martinique</option>
                    <option value="MR">Mauritania</option>
                    <option value="MP">Mauritius</option>
                    <option value="MF">Mayotte</option>
                    <option value="MX">Mexico</option>

                    <option value="FM">Micronesia</option>
                    <option value="MQ">Midway Islands</option>
                    <option value="MD">Moldova</option>
                    <option value="MN">Monaco</option>
                    <option value="MG">Mongolia</option>
                    <option value="MH">Montserrat</option>

                    <option value="MO">Morocco</option>
                    <option value="MZ">Mozambique</option>
                    <option value="WA">Namibia</option>
                    <option value="NR">Nauru</option>
                    <option value="BQ">Navassa Island</option>
                    <option value="NP">Nepal</option>

                    <option value="NL">Netherlands</option>
                    <option value="NA">Netherlands Antilles</option>
                    <option value="NC">Netherlands Antilles</option>
                    <option value="NZ">New Zealand</option>
                    <option value="NU">Nicaragua</option>
                    <option value="NG">Niger</option>

                    <option value="NI">Nigeria</option>
                    <option value="NE">Niue</option>
                    <option value="NF">Norfolk Island</option>
                    <option value="CQ">Northern Mariana Islands</option>
                    <option value="NO">Norway</option>
                    <option value="MU">Oman</option>

                    <option value="PS">Pacific Islands, Trust Territory Of The</option>
                    <option value="PK">Pakistan</option>
                    <option value="LQ">Palmyra Atoll</option>
                    <option value="PM">Panama</option>
                    <option value="PP">Papau New Guinea</option>
                    <option value="PF">Paracel Islands</option>

                    <option value="PA">Paraguay</option>
                    <option value="PE">Peru</option>
                    <option value="RP">Philippines</option>
                    <option value="PC">Pitcairn Island</option>
                    <option value="PL">Poland</option>
                    <option value="PO">Portugal &amp; Azores</option>

                    <option value="RQ">Puerto Rico</option>
                    <option value="QA">Qatar</option>
                    <option value="RE">Reunion</option>
                    <option value="RO">Romania</option>
                    <option value="RS">Russia</option>
                    <option value="RW">Rwanda</option>

                    <option value="SM">San Marino</option>
                    <option value="TP">Sao Tome &amp; Principle</option>
                    <option value="SA">Saudi Arabia</option>
                    <option value="SG">Senegal</option>
                    <option value="SR">Serbia &amp; Montenegro</option>

                    <option value="SE">Seychelles</option>
                    <option value="SL">Sierra Leone</option>
                    <option value="SN">Singapore</option>
                    <option value="LO">Slovakia</option>
                    <option value="SI">Slovenia</option>
                    <option value="BP">Solomon Islands</option>

                    <option value="SO">Somalia</option>
                    <option value="SF">South Africa</option>
                    <option value="SP">Spain &amp; Canary Islands</option>
                    <option value="PG">Spratly Islands</option>
                    <option value="CE">Sri Lanka</option>

                    <option value="SH">St Helena</option>
                    <option value="SC">St Kitts &amp; Nevis</option>
                    <option value="ST">St Lucia</option>
                    <option value="SB">St Pierre &amp; Miquelon</option>
                    <option value="VC">St Vincent And The Grenadines </option>

                    <option value="NS">Suriname</option>
                    <option value="SV">Svalbard</option>
                    <option value="WZ">Swaziland</option>
                    <option value="SW">Sweden</option>
                    <option value="SZ">Switzerland</option>
                    <option value="TW">Taiwan</option>

                    <option value="TI">Tajikistan</option>
                    <option value="TZ">Tanzania</option>
                    <option value="TH">Thailand</option>
                    <option value="TO">Togo</option>
                    <option value="TL">Tokelau</option>
                    <option value="TN">Tonga</option>

                    <option value="TD">Trinidad &amp; Tobago</option>
                    <option value="TE">Tromelin Island</option>
                    <option value="TS">Tunisia</option>
                    <option value="TU">Turkey</option>
                    <option value="TX">Turkmenistan</option>

                    <option value="TK">Turks &amp; Caicos Island</option>
                    <option value="TV">Tuvalu</option>
                    <option value="UR">USSR</option>
                    <option value="UG">Uganda</option>
                    <option value="UP">Ukraine</option>

                    <option value="TC">United Arab Emirates</option>
                    <option value="UK">United Kingdom &amp; Northern Ireland</option>
                    <option value="UY">Uruguay</option>
                    <option value="UZ">Uzbekistan</option>
                    <option value="NH">Vanuatu</option>

                    <option value="VT">Vatican City</option>
                    <option value="VE">Venezuela</option>
                    <option value="VM">Vietnam</option>
                    <option value="VQ">Virgin Islands</option>
                    <option value="VI">Virgin Islands-British</option>
                    <option value="WQ">Wake Island</option>

                    <option value="WF">Wallis &amp; Futuna</option>
                    <option value="WE">West Bank</option>
                    <option value="WI">Western Sahara</option>
                    <option value="WS">Western Samoa</option>
                    <option value="YM">Yemen</option>

                    <option value="YO">Yugoslavia</option>
                    <option value="CG">Zaire</option>
                    <option value="ZA">Zambia</option>
                    <option value="ZB">Zimbabwe</option>
                  </select></td>
              </tr>
              <tr class="datatable">

                <th  align="right">Position Title:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber632' value='' size='16' style="" ></td>
                <th  align="right">Zip:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63112' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Organization:</th>

                <td >BL</td>
                <th  align="right">Phone:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63113' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Department:</th>
                <td >UITS</td>

                <th  align="right">Fax:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63114' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Division:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber637' value='' size='16' style="" ></td>
                <th  align="right">E-mail:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63115' value='' size='16' style="" ></td>
              </tr>
              <%--
              <tr class="datatable">
                <th  align="right">% Intellectual Credit:</th>
                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>

                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr class="datatable">
                <th  align="right">% F&amp;A Credit:</th>

                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>
                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>

                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>

                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">

                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>

                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              --%>
            </table>
          </div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>

              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle" > Land, Harold</td>
              <td class="tabtable1-mid1">Post Doctoral</td>
              <td class="tabtable2-mid"><a id="A12" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F12" ></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G12" style="display: none;">

            <div class="h2-container">
              <h2><span class="subhead-left"> Land, Harold</span></h2>
              <span class="subhead-right"> <span class="subhead"><a href="asdf.html"><img src="images/my_cp_inf.gif" alt="help" width="15" height="14" border="0" align="absmiddle"></a></span> </span> </div>
            <table cellpadding=0 cellspacing="0" class="datatable">
              <tr class="datatable">
                <th  align="right">Role:</th>

                <td >Post Doctoral</td>
                <th  align="right">Street 1:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber638' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">First Name:</th>
                <td >Harold</td>

                <th  align="right">Street 2:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber639' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Middle Name:</th>
                <td >R</td>
                <th  align="right">City:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6310' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Last Name:</th>
                <td >Land</td>
                <th  align="right">County:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6311' value='' size='16' style="" ></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Prefix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber633' value='' size='16' style="" ></td>
                <th  align="right">State:</th>
                <td ><select name="select" size="1" tabindex="6">
                    <option value="" selected>select:</option>

                    <option value="--">Out Of Country</option>
                    <option value="AK">Alaska</option>
                    <option value="AL">Alabama</option>
                    <option value="AR">Arkansas</option>
                    <option value="AZ">Arizona</option>
                    <option value="CA">California</option>

                    <option value="CO">Colorado</option>
                    <option value="CT">Connecticut</option>
                    <option value="DC">District Of Columbia</option>
                    <option value="DE">Delaware</option>
                    <option value="FL">Florida</option>
                    <option value="GA">Georgia</option>

                    <option value="HI">Hawaii</option>
                    <option value="IA">Iowa</option>
                    <option value="ID">Idaho</option>
                    <option value="IL">Illinois</option>
                    <option value="IN">Indiana</option>
                    <option value="KS">Kansas</option>

                    <option value="KY">Kentucky</option>
                    <option value="LA">Louisiana</option>
                    <option value="MA">Massachusetts</option>
                    <option value="MD">Maryland</option>
                    <option value="ME">Maine</option>
                    <option value="MI">Michigan</option>

                    <option value="MN">Minnesota</option>
                    <option value="MO">Missouri</option>
                    <option value="MS">Mississippi</option>
                    <option value="MT">Montana</option>
                    <option value="NC">North Carolina</option>
                    <option value="ND">North Dakota</option>

                    <option value="NE">Nebraska</option>
                    <option value="NH">New Hampshire</option>
                    <option value="NJ">New Jersey</option>
                    <option value="NM">New Mexico</option>
                    <option value="NV">Nevada</option>
                    <option value="NY">New York</option>

                    <option value="OH">Ohio</option>
                    <option value="OK">Oklahoma</option>
                    <option value="OR">Oregon</option>
                    <option value="PA">Pennsylvania</option>
                    <option value="PR">Puerto Rico</option>
                    <option value="RI">Rhode Island</option>

                    <option value="SC">South Carolina</option>
                    <option value="SD">South Dakota</option>
                    <option value="TN">Tennessee</option>
                    <option value="TX">Texas</option>
                    <option value="UT">Utah</option>
                    <option value="VA">Virginia</option>

                    <option value="VT">Vermont</option>
                    <option value="WA">Washington</option>
                    <option value="WI">Wisconsin</option>
                    <option value="WV">West Virginia</option>
                    <option value="WY">Wyoming</option>
                  </select></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Suffix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber634' value='' size='16' style="" ></td>
                <th  align="right">Country:</th>
                <td ><select name="select2" size="1" tabindex="9">
                    <option value="" selected>select:</option>

                    <option value="US">United States</option>
                    <option value="AF">Afghanistan</option>
                    <option value="AL">Albania</option>
                    <option value="AG">Algeria</option>
                    <option value="AQ">American Samoa</option>
                    <option value="AN">Andorra</option>

                    <option value="AO">Angola</option>
                    <option value="AV">Anguilla</option>
                    <option value="AY">Antarctica</option>
                    <option value="AC">Antigua &amp; Barbuda</option>
                    <option value="AR">Argentina</option>

                    <option value="AM">Armenia</option>
                    <option value="AA">Aruba</option>
                    <option value="AT">Ashmore &amp; Caratier Island</option>
                    <option value="AS">Australia</option>
                    <option value="AU">Austria</option>

                    <option value="AJ">Azerbaijan</option>
                    <option value="BF">Bahamas</option>
                    <option value="BA">Bahrain</option>
                    <option value="FQ">Baker Island</option>
                    <option value="BG">Bangladesh</option>
                    <option value="BB">Barbados</option>

                    <option value="BS">Bassas Da India</option>
                    <option value="BO">Belarus</option>
                    <option value="BE">Belgium</option>
                    <option value="BH">Belize</option>
                    <option value="BN">Benin</option>
                    <option value="BD">Bermuda</option>

                    <option value="BT">Bhutan</option>
                    <option value="BL">Boliva</option>
                    <option value="BK">Bosnia &amp; Hercegovina</option>
                    <option value="BC">Botswana</option>
                    <option value="BV">Bouvet Island</option>

                    <option value="BR">Brazil</option>
                    <option value="IO">British Indian Ocean Territory</option>
                    <option value="BX">Brunei</option>
                    <option value="BU">Bulgaria</option>
                    <option value="BY">Burundi</option>
                    <option value="CB">Cambodia</option>

                    <option value="CM">Cameroon</option>
                    <option value="CA">Canada</option>
                    <option value="CV">Cape Verde</option>
                    <option value="CJ">Cayman Islands</option>
                    <option value="CT">Central African Republic</option>
                    <option value="CD">Chad</option>

                    <option value="CI">Chile</option>
                    <option value="CH">China</option>
                    <option value="KT">Christmas Island - Indian Ocean</option>
                    <option value="IP">Clipperton Island</option>
                    <option value="CO">Colombia</option>
                    <option value="CN">Comoros</option>

                    <option value="CF">Congo</option>
                    <option value="CW">Cook Islands</option>
                    <option value="CR">Coral Sea Islands Territory</option>
                    <option value="CS">Costa Rica</option>
                    <option value="HR">Croatia</option>
                    <option value="CY">Cyprus</option>

                    <option value="EZ">Czech Republic</option>
                    <option value="CZ">Czechoslovakia</option>
                    <option value="DA">Denmark</option>
                    <option value="DJ">Djibouti</option>
                    <option value="DO">Dominica</option>
                    <option value="DR">Dominican Republic</option>

                    <option value="EC">Ecuador</option>
                    <option value="EG">Egypt</option>
                    <option value="ES">El Salvador</option>
                    <option value="EK">Equatorial Guinea</option>
                    <option value="ER">Eritrea</option>
                    <option value="EN">Estonia</option>

                    <option value="ET">Ethiopia</option>
                    <option value="EU">Europa Island</option>
                    <option value="FA">Falkland Islands</option>
                    <option value="FO">Faroe Islands</option>
                    <option value="FJ">Fiji</option>
                    <option value="FI">Finland</option>

                    <option value="FR">France</option>
                    <option value="FG">French Guiana</option>
                    <option value="FP">French Polynesia</option>
                    <option value="FS">French Southern &amp; Antarctic Lands</option>
                    <option value="GB">Gabon</option>

                    <option value="GA">Gambia</option>
                    <option value="GZ">Gaza Strip</option>
                    <option value="GG">Georgia</option>
                    <option value="GE">Germany</option>
                    <option value="GH">Ghana</option>
                    <option value="GI">Gibraltar</option>

                    <option value="GO">Glorioso Islands</option>
                    <option value="GR">Greece</option>
                    <option value="GL">Greenland</option>
                    <option value="GJ">Grenada</option>
                    <option value="GP">Guadeloupe</option>
                    <option value="GQ">Guam</option>

                    <option value="GT">Guatemala</option>
                    <option value="GK">Guernsey</option>
                    <option value="GV">Guinea</option>
                    <option value="PU">Guinea-Bissau</option>
                    <option value="GY">Guyana</option>
                    <option value="HA">Haita</option>

                    <option value="HM">Heard Island &amp; Mcdonald Islands</option>
                    <option value="HO">Honduras</option>
                    <option value="HK">Hong Kong</option>
                    <option value="HQ">Howland Island</option>
                    <option value="HU">Hungary</option>

                    <option value="IC">Iceland</option>
                    <option value="IN">India</option>
                    <option value="ID">Indonesia</option>
                    <option value="IZ">Iraq</option>
                    <option value="IY">Iraq-Saudi Arabia</option>
                    <option value="EI">Ireland</option>

                    <option value="IM">Isle of Man</option>
                    <option value="IS">Israel</option>
                    <option value="IT">Italy</option>
                    <option value="IV">Ivory Coast</option>
                    <option value="JM">Jamaica</option>
                    <option value="JN">Jan Mayen</option>

                    <option value="JA">Japan</option>
                    <option value="JE">Jersey</option>
                    <option value="JQ">Johnston Atoll</option>
                    <option value="JO">Jordan</option>
                    <option value="JU">Juan De Nova Island</option>
                    <option value="KZ">Kazakhstan</option>

                    <option value="CK">Keeling Island</option>
                    <option value="KE">Kenya</option>
                    <option value="KQ">Kingman Reef</option>
                    <option value="KR">Kiribati Christmas Island</option>
                    <option value="KS">Korea South</option>
                    <option value="KU">Kuwait</option>

                    <option value="KG">Kyrgyzstan</option>
                    <option value="LA">Laos</option>
                    <option value="LG">Latvia</option>
                    <option value="LE">Lebanon</option>
                    <option value="LT">Lesotho</option>
                    <option value="LI">Liberia</option>

                    <option value="LS">Liechtenstein</option>
                    <option value="LH">Lithuania</option>
                    <option value="LU">Luxembourg</option>
                    <option value="MC">Macai</option>
                    <option value="MK">Macedonia</option>
                    <option value="MA">Madagascar</option>

                    <option value="MI">Malawi</option>
                    <option value="MY">Malaysia</option>
                    <option value="MV">Maldives</option>
                    <option value="ML">Mali</option>
                    <option value="MT">Malta</option>
                    <option value="RM">Marshall Islands</option>

                    <option value="MB">Martinique</option>
                    <option value="MR">Mauritania</option>
                    <option value="MP">Mauritius</option>
                    <option value="MF">Mayotte</option>
                    <option value="MX">Mexico</option>
                    <option value="FM">Micronesia</option>

                    <option value="MQ">Midway Islands</option>
                    <option value="MD">Moldova</option>
                    <option value="MN">Monaco</option>
                    <option value="MG">Mongolia</option>
                    <option value="MH">Montserrat</option>
                    <option value="MO">Morocco</option>

                    <option value="MZ">Mozambique</option>
                    <option value="WA">Namibia</option>
                    <option value="NR">Nauru</option>
                    <option value="BQ">Navassa Island</option>
                    <option value="NP">Nepal</option>
                    <option value="NL">Netherlands</option>

                    <option value="NA">Netherlands Antilles</option>
                    <option value="NC">Netherlands Antilles</option>
                    <option value="NZ">New Zealand</option>
                    <option value="NU">Nicaragua</option>
                    <option value="NG">Niger</option>
                    <option value="NI">Nigeria</option>

                    <option value="NE">Niue</option>
                    <option value="NF">Norfolk Island</option>
                    <option value="CQ">Northern Mariana Islands</option>
                    <option value="NO">Norway</option>
                    <option value="MU">Oman</option>
                    <option value="PS">Pacific Islands, Trust Territory Of The</option>

                    <option value="PK">Pakistan</option>
                    <option value="LQ">Palmyra Atoll</option>
                    <option value="PM">Panama</option>
                    <option value="PP">Papau New Guinea</option>
                    <option value="PF">Paracel Islands</option>
                    <option value="PA">Paraguay</option>

                    <option value="PE">Peru</option>
                    <option value="RP">Philippines</option>
                    <option value="PC">Pitcairn Island</option>
                    <option value="PL">Poland</option>
                    <option value="PO">Portugal &amp; Azores</option>

                    <option value="RQ">Puerto Rico</option>
                    <option value="QA">Qatar</option>
                    <option value="RE">Reunion</option>
                    <option value="RO">Romania</option>
                    <option value="RS">Russia</option>
                    <option value="RW">Rwanda</option>

                    <option value="SM">San Marino</option>
                    <option value="TP">Sao Tome &amp; Principle</option>
                    <option value="SA">Saudi Arabia</option>
                    <option value="SG">Senegal</option>
                    <option value="SR">Serbia &amp; Montenegro</option>

                    <option value="SE">Seychelles</option>
                    <option value="SL">Sierra Leone</option>
                    <option value="SN">Singapore</option>
                    <option value="LO">Slovakia</option>
                    <option value="SI">Slovenia</option>
                    <option value="BP">Solomon Islands</option>

                    <option value="SO">Somalia</option>
                    <option value="SF">South Africa</option>
                    <option value="SP">Spain &amp; Canary Islands</option>
                    <option value="PG">Spratly Islands</option>
                    <option value="CE">Sri Lanka</option>

                    <option value="SH">St Helena</option>
                    <option value="SC">St Kitts &amp; Nevis</option>
                    <option value="ST">St Lucia</option>
                    <option value="SB">St Pierre &amp; Miquelon</option>
                    <option value="VC">St Vincent And The Grenadines </option>

                    <option value="NS">Suriname</option>
                    <option value="SV">Svalbard</option>
                    <option value="WZ">Swaziland</option>
                    <option value="SW">Sweden</option>
                    <option value="SZ">Switzerland</option>
                    <option value="TW">Taiwan</option>

                    <option value="TI">Tajikistan</option>
                    <option value="TZ">Tanzania</option>
                    <option value="TH">Thailand</option>
                    <option value="TO">Togo</option>
                    <option value="TL">Tokelau</option>
                    <option value="TN">Tonga</option>

                    <option value="TD">Trinidad &amp; Tobago</option>
                    <option value="TE">Tromelin Island</option>
                    <option value="TS">Tunisia</option>
                    <option value="TU">Turkey</option>
                    <option value="TX">Turkmenistan</option>

                    <option value="TK">Turks &amp; Caicos Island</option>
                    <option value="TV">Tuvalu</option>
                    <option value="UR">USSR</option>
                    <option value="UG">Uganda</option>
                    <option value="UP">Ukraine</option>

                    <option value="TC">United Arab Emirates</option>
                    <option value="UK">United Kingdom &amp; Northern Ireland</option>
                    <option value="UY">Uruguay</option>
                    <option value="UZ">Uzbekistan</option>
                    <option value="NH">Vanuatu</option>

                    <option value="VT">Vatican City</option>
                    <option value="VE">Venezuela</option>
                    <option value="VM">Vietnam</option>
                    <option value="VQ">Virgin Islands</option>
                    <option value="VI">Virgin Islands-British</option>
                    <option value="WQ">Wake Island</option>

                    <option value="WF">Wallis &amp; Futuna</option>
                    <option value="WE">West Bank</option>
                    <option value="WI">Western Sahara</option>
                    <option value="WS">Western Samoa</option>
                    <option value="YM">Yemen</option>

                    <option value="YO">Yugoslavia</option>
                    <option value="CG">Zaire</option>
                    <option value="ZA">Zambia</option>
                    <option value="ZB">Zimbabwe</option>
                  </select></td>
              </tr>
              <tr class="datatable">

                <th  align="right">Position Title:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber632' value='' size='16' style="" ></td>
                <th  align="right">Zip:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63112' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Organization:</th>

                <td >BL</td>
                <th  align="right">Phone:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63113' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Department:</th>
                <td >UITS</td>

                <th  align="right">Fax:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63114' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Division:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber637' value='' size='16' style="" ></td>
                <th  align="right">E-mail:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63115' value='' size='16' style="" ></td>
              </tr>
              <%--
              <tr class="datatable">
                <th  align="right">% Intellectual Credit:</th>
                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>

                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr class="datatable">
                <th  align="right">% F&amp;A Credit:</th>

                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>
                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>

                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>

                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">

                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>

                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              --%>
            </table>
          </div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>

              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle" > Marsh, Warren</td>
              <td class="tabtable1-mid1">Co-PD/PI </td>
              <td class="tabtable2-mid"><a id="A03" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F03" ></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G03" style="display: none;">

            <div class="h2-container">
              <h2><span class="subhead-left">Marsh, Warren</span></h2>
              <span class="subhead-right"> <span class="subhead"><a href="asdf.html"><img src="images/my_cp_inf.gif" alt="help" width="15" height="14" border="0" align="absmiddle"></a></span> </span> </div>
            <table cellpadding=0 cellspacing="0" class="datatable">
              <tr class="datatable">
                <th  align="right">Role:</th>

                <td >Co-PD/PI</td>
                <th  align="right">Street 1:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber638' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">First Name:</th>
                <td >Warren</td>

                <th  align="right">Street 2:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber639' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Middle Name:</th>
                <td >R</td>
                <th  align="right">City:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6310' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Last Name:</th>
                <td >Marsh</td>
                <th  align="right">County:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6311' value='' size='16' style="" ></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Prefix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber633' value='' size='16' style="" ></td>
                <th  align="right">State:</th>
                <td ><select name="select" size="1" tabindex="6">
                    <option value="" selected>select:</option>

                    <option value="--">Out Of Country</option>
                    <option value="AK">Alaska</option>
                    <option value="AL">Alabama</option>
                    <option value="AR">Arkansas</option>
                    <option value="AZ">Arizona</option>
                    <option value="CA">California</option>

                    <option value="CO">Colorado</option>
                    <option value="CT">Connecticut</option>
                    <option value="DC">District Of Columbia</option>
                    <option value="DE">Delaware</option>
                    <option value="FL">Florida</option>
                    <option value="GA">Georgia</option>

                    <option value="HI">Hawaii</option>
                    <option value="IA">Iowa</option>
                    <option value="ID">Idaho</option>
                    <option value="IL">Illinois</option>
                    <option value="IN">Indiana</option>
                    <option value="KS">Kansas</option>

                    <option value="KY">Kentucky</option>
                    <option value="LA">Louisiana</option>
                    <option value="MA">Massachusetts</option>
                    <option value="MD">Maryland</option>
                    <option value="ME">Maine</option>
                    <option value="MI">Michigan</option>

                    <option value="MN">Minnesota</option>
                    <option value="MO">Missouri</option>
                    <option value="MS">Mississippi</option>
                    <option value="MT">Montana</option>
                    <option value="NC">North Carolina</option>
                    <option value="ND">North Dakota</option>

                    <option value="NE">Nebraska</option>
                    <option value="NH">New Hampshire</option>
                    <option value="NJ">New Jersey</option>
                    <option value="NM">New Mexico</option>
                    <option value="NV">Nevada</option>
                    <option value="NY">New York</option>

                    <option value="OH">Ohio</option>
                    <option value="OK">Oklahoma</option>
                    <option value="OR">Oregon</option>
                    <option value="PA">Pennsylvania</option>
                    <option value="PR">Puerto Rico</option>
                    <option value="RI">Rhode Island</option>

                    <option value="SC">South Carolina</option>
                    <option value="SD">South Dakota</option>
                    <option value="TN">Tennessee</option>
                    <option value="TX">Texas</option>
                    <option value="UT">Utah</option>
                    <option value="VA">Virginia</option>

                    <option value="VT">Vermont</option>
                    <option value="WA">Washington</option>
                    <option value="WI">Wisconsin</option>
                    <option value="WV">West Virginia</option>
                    <option value="WY">Wyoming</option>
                  </select></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Suffix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber634' value='' size='16' style="" ></td>
                <th  align="right">Country:</th>
                <td ><select name="select2" size="1" tabindex="9">
                    <option value="" selected>select:</option>

                    <option value="US">United States</option>
                    <option value="AF">Afghanistan</option>
                    <option value="AL">Albania</option>
                    <option value="AG">Algeria</option>
                    <option value="AQ">American Samoa</option>
                    <option value="AN">Andorra</option>

                    <option value="AO">Angola</option>
                    <option value="AV">Anguilla</option>
                    <option value="AY">Antarctica</option>
                    <option value="AC">Antigua &amp; Barbuda</option>
                    <option value="AR">Argentina</option>

                    <option value="AM">Armenia</option>
                    <option value="AA">Aruba</option>
                    <option value="AT">Ashmore &amp; Caratier Island</option>
                    <option value="AS">Australia</option>
                    <option value="AU">Austria</option>

                    <option value="AJ">Azerbaijan</option>
                    <option value="BF">Bahamas</option>
                    <option value="BA">Bahrain</option>
                    <option value="FQ">Baker Island</option>
                    <option value="BG">Bangladesh</option>
                    <option value="BB">Barbados</option>

                    <option value="BS">Bassas Da India</option>
                    <option value="BO">Belarus</option>
                    <option value="BE">Belgium</option>
                    <option value="BH">Belize</option>
                    <option value="BN">Benin</option>
                    <option value="BD">Bermuda</option>

                    <option value="BT">Bhutan</option>
                    <option value="BL">Boliva</option>
                    <option value="BK">Bosnia &amp; Hercegovina</option>
                    <option value="BC">Botswana</option>
                    <option value="BV">Bouvet Island</option>

                    <option value="BR">Brazil</option>
                    <option value="IO">British Indian Ocean Territory</option>
                    <option value="BX">Brunei</option>
                    <option value="BU">Bulgaria</option>
                    <option value="BY">Burundi</option>
                    <option value="CB">Cambodia</option>

                    <option value="CM">Cameroon</option>
                    <option value="CA">Canada</option>
                    <option value="CV">Cape Verde</option>
                    <option value="CJ">Cayman Islands</option>
                    <option value="CT">Central African Republic</option>
                    <option value="CD">Chad</option>

                    <option value="CI">Chile</option>
                    <option value="CH">China</option>
                    <option value="KT">Christmas Island - Indian Ocean</option>
                    <option value="IP">Clipperton Island</option>
                    <option value="CO">Colombia</option>
                    <option value="CN">Comoros</option>

                    <option value="CF">Congo</option>
                    <option value="CW">Cook Islands</option>
                    <option value="CR">Coral Sea Islands Territory</option>
                    <option value="CS">Costa Rica</option>
                    <option value="HR">Croatia</option>
                    <option value="CY">Cyprus</option>

                    <option value="EZ">Czech Republic</option>
                    <option value="CZ">Czechoslovakia</option>
                    <option value="DA">Denmark</option>
                    <option value="DJ">Djibouti</option>
                    <option value="DO">Dominica</option>
                    <option value="DR">Dominican Republic</option>

                    <option value="EC">Ecuador</option>
                    <option value="EG">Egypt</option>
                    <option value="ES">El Salvador</option>
                    <option value="EK">Equatorial Guinea</option>
                    <option value="ER">Eritrea</option>
                    <option value="EN">Estonia</option>

                    <option value="ET">Ethiopia</option>
                    <option value="EU">Europa Island</option>
                    <option value="FA">Falkland Islands</option>
                    <option value="FO">Faroe Islands</option>
                    <option value="FJ">Fiji</option>
                    <option value="FI">Finland</option>

                    <option value="FR">France</option>
                    <option value="FG">French Guiana</option>
                    <option value="FP">French Polynesia</option>
                    <option value="FS">French Southern &amp; Antarctic Lands</option>
                    <option value="GB">Gabon</option>

                    <option value="GA">Gambia</option>
                    <option value="GZ">Gaza Strip</option>
                    <option value="GG">Georgia</option>
                    <option value="GE">Germany</option>
                    <option value="GH">Ghana</option>
                    <option value="GI">Gibraltar</option>

                    <option value="GO">Glorioso Islands</option>
                    <option value="GR">Greece</option>
                    <option value="GL">Greenland</option>
                    <option value="GJ">Grenada</option>
                    <option value="GP">Guadeloupe</option>
                    <option value="GQ">Guam</option>

                    <option value="GT">Guatemala</option>
                    <option value="GK">Guernsey</option>
                    <option value="GV">Guinea</option>
                    <option value="PU">Guinea-Bissau</option>
                    <option value="GY">Guyana</option>
                    <option value="HA">Haita</option>

                    <option value="HM">Heard Island &amp; Mcdonald Islands</option>
                    <option value="HO">Honduras</option>
                    <option value="HK">Hong Kong</option>
                    <option value="HQ">Howland Island</option>
                    <option value="HU">Hungary</option>

                    <option value="IC">Iceland</option>
                    <option value="IN">India</option>
                    <option value="ID">Indonesia</option>
                    <option value="IZ">Iraq</option>
                    <option value="IY">Iraq-Saudi Arabia</option>
                    <option value="EI">Ireland</option>

                    <option value="IM">Isle of Man</option>
                    <option value="IS">Israel</option>
                    <option value="IT">Italy</option>
                    <option value="IV">Ivory Coast</option>
                    <option value="JM">Jamaica</option>
                    <option value="JN">Jan Mayen</option>

                    <option value="JA">Japan</option>
                    <option value="JE">Jersey</option>
                    <option value="JQ">Johnston Atoll</option>
                    <option value="JO">Jordan</option>
                    <option value="JU">Juan De Nova Island</option>
                    <option value="KZ">Kazakhstan</option>

                    <option value="CK">Keeling Island</option>
                    <option value="KE">Kenya</option>
                    <option value="KQ">Kingman Reef</option>
                    <option value="KR">Kiribati Christmas Island</option>
                    <option value="KS">Korea South</option>
                    <option value="KU">Kuwait</option>

                    <option value="KG">Kyrgyzstan</option>
                    <option value="LA">Laos</option>
                    <option value="LG">Latvia</option>
                    <option value="LE">Lebanon</option>
                    <option value="LT">Lesotho</option>
                    <option value="LI">Liberia</option>

                    <option value="LS">Liechtenstein</option>
                    <option value="LH">Lithuania</option>
                    <option value="LU">Luxembourg</option>
                    <option value="MC">Macai</option>
                    <option value="MK">Macedonia</option>
                    <option value="MA">Madagascar</option>

                    <option value="MI">Malawi</option>
                    <option value="MY">Malaysia</option>
                    <option value="MV">Maldives</option>
                    <option value="ML">Mali</option>
                    <option value="MT">Malta</option>
                    <option value="RM">Marshall Islands</option>

                    <option value="MB">Martinique</option>
                    <option value="MR">Mauritania</option>
                    <option value="MP">Mauritius</option>
                    <option value="MF">Mayotte</option>
                    <option value="MX">Mexico</option>
                    <option value="FM">Micronesia</option>

                    <option value="MQ">Midway Islands</option>
                    <option value="MD">Moldova</option>
                    <option value="MN">Monaco</option>
                    <option value="MG">Mongolia</option>
                    <option value="MH">Montserrat</option>
                    <option value="MO">Morocco</option>

                    <option value="MZ">Mozambique</option>
                    <option value="WA">Namibia</option>
                    <option value="NR">Nauru</option>
                    <option value="BQ">Navassa Island</option>
                    <option value="NP">Nepal</option>
                    <option value="NL">Netherlands</option>

                    <option value="NA">Netherlands Antilles</option>
                    <option value="NC">Netherlands Antilles</option>
                    <option value="NZ">New Zealand</option>
                    <option value="NU">Nicaragua</option>
                    <option value="NG">Niger</option>
                    <option value="NI">Nigeria</option>

                    <option value="NE">Niue</option>
                    <option value="NF">Norfolk Island</option>
                    <option value="CQ">Northern Mariana Islands</option>
                    <option value="NO">Norway</option>
                    <option value="MU">Oman</option>
                    <option value="PS">Pacific Islands, Trust Territory Of The</option>

                    <option value="PK">Pakistan</option>
                    <option value="LQ">Palmyra Atoll</option>
                    <option value="PM">Panama</option>
                    <option value="PP">Papau New Guinea</option>
                    <option value="PF">Paracel Islands</option>
                    <option value="PA">Paraguay</option>

                    <option value="PE">Peru</option>
                    <option value="RP">Philippines</option>
                    <option value="PC">Pitcairn Island</option>
                    <option value="PL">Poland</option>
                    <option value="PO">Portugal &amp; Azores</option>

                    <option value="RQ">Puerto Rico</option>
                    <option value="QA">Qatar</option>
                    <option value="RE">Reunion</option>
                    <option value="RO">Romania</option>
                    <option value="RS">Russia</option>
                    <option value="RW">Rwanda</option>

                    <option value="SM">San Marino</option>
                    <option value="TP">Sao Tome &amp; Principle</option>
                    <option value="SA">Saudi Arabia</option>
                    <option value="SG">Senegal</option>
                    <option value="SR">Serbia &amp; Montenegro</option>

                    <option value="SE">Seychelles</option>
                    <option value="SL">Sierra Leone</option>
                    <option value="SN">Singapore</option>
                    <option value="LO">Slovakia</option>
                    <option value="SI">Slovenia</option>
                    <option value="BP">Solomon Islands</option>

                    <option value="SO">Somalia</option>
                    <option value="SF">South Africa</option>
                    <option value="SP">Spain &amp; Canary Islands</option>
                    <option value="PG">Spratly Islands</option>
                    <option value="CE">Sri Lanka</option>

                    <option value="SH">St Helena</option>
                    <option value="SC">St Kitts &amp; Nevis</option>
                    <option value="ST">St Lucia</option>
                    <option value="SB">St Pierre &amp; Miquelon</option>
                    <option value="VC">St Vincent And The Grenadines </option>

                    <option value="NS">Suriname</option>
                    <option value="SV">Svalbard</option>
                    <option value="WZ">Swaziland</option>
                    <option value="SW">Sweden</option>
                    <option value="SZ">Switzerland</option>
                    <option value="TW">Taiwan</option>

                    <option value="TI">Tajikistan</option>
                    <option value="TZ">Tanzania</option>
                    <option value="TH">Thailand</option>
                    <option value="TO">Togo</option>
                    <option value="TL">Tokelau</option>
                    <option value="TN">Tonga</option>

                    <option value="TD">Trinidad &amp; Tobago</option>
                    <option value="TE">Tromelin Island</option>
                    <option value="TS">Tunisia</option>
                    <option value="TU">Turkey</option>
                    <option value="TX">Turkmenistan</option>

                    <option value="TK">Turks &amp; Caicos Island</option>
                    <option value="TV">Tuvalu</option>
                    <option value="UR">USSR</option>
                    <option value="UG">Uganda</option>
                    <option value="UP">Ukraine</option>

                    <option value="TC">United Arab Emirates</option>
                    <option value="UK">United Kingdom &amp; Northern Ireland</option>
                    <option value="UY">Uruguay</option>
                    <option value="UZ">Uzbekistan</option>
                    <option value="NH">Vanuatu</option>

                    <option value="VT">Vatican City</option>
                    <option value="VE">Venezuela</option>
                    <option value="VM">Vietnam</option>
                    <option value="VQ">Virgin Islands</option>
                    <option value="VI">Virgin Islands-British</option>
                    <option value="WQ">Wake Island</option>

                    <option value="WF">Wallis &amp; Futuna</option>
                    <option value="WE">West Bank</option>
                    <option value="WI">Western Sahara</option>
                    <option value="WS">Western Samoa</option>
                    <option value="YM">Yemen</option>

                    <option value="YO">Yugoslavia</option>
                    <option value="CG">Zaire</option>
                    <option value="ZA">Zambia</option>
                    <option value="ZB">Zimbabwe</option>
                  </select></td>
              </tr>
              <tr class="datatable">

                <th  align="right">Position Title:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber632' value='' size='16' style="" ></td>
                <th  align="right">Zip:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63112' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Organization:</th>

                <td >BL</td>
                <th  align="right">Phone:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63113' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Department:</th>
                <td >UITS</td>

                <th  align="right">Fax:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63114' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Division:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber637' value='' size='16' style="" ></td>
                <th  align="right">E-mail:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63115' value='' size='16' style="" ></td>
              </tr>
              <%--
              <tr class="datatable">
                <th  align="right">% Intellectual Credit:</th>
                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>

                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr class="datatable">
                <th  align="right">% F&amp;A Credit:</th>

                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>
                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>

                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>

                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">

                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>

                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              --%>
            </table>
          </div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>

              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle" > Daniels, Eddie</td>
              <td class="tabtable1-mid1">Faculty</td>
              <td class="tabtable2-mid"><a id="A04" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F04" ></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G04" style="display: none;">

            <div class="h2-container">
              <h2><span class="subhead-left">Daniels, Eddie</span></h2>
              <span class="subhead-right"> <span class="subhead"><a href="asdf.html"><img src="images/my_cp_inf.gif" alt="help" width="15" height="14" border="0" align="absmiddle"></a></span> </span> </div>
            <table cellpadding=0 cellspacing="0" class="datatable">
              <tr class="datatable">
                <th  align="right">Role:</th>

                <td >Faculty</td>
                <th  align="right">Street 1:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber638' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">First Name:</th>
                <td >Eddie</td>

                <th  align="right">Street 2:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber639' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Middle Name:</th>
                <td >R</td>
                <th  align="right">City:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6310' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Last Name:</th>
                <td >Daniel</td>
                <th  align="right">County:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6311' value='' size='16' style="" ></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Prefix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber633' value='' size='16' style="" ></td>
                <th  align="right">State:</th>
                <td ><select name="select" size="1" tabindex="6">
                    <option value="" selected>select:</option>

                    <option value="--">Out Of Country</option>
                    <option value="AK">Alaska</option>
                    <option value="AL">Alabama</option>
                    <option value="AR">Arkansas</option>
                    <option value="AZ">Arizona</option>
                    <option value="CA">California</option>

                    <option value="CO">Colorado</option>
                    <option value="CT">Connecticut</option>
                    <option value="DC">District Of Columbia</option>
                    <option value="DE">Delaware</option>
                    <option value="FL">Florida</option>
                    <option value="GA">Georgia</option>

                    <option value="HI">Hawaii</option>
                    <option value="IA">Iowa</option>
                    <option value="ID">Idaho</option>
                    <option value="IL">Illinois</option>
                    <option value="IN">Indiana</option>
                    <option value="KS">Kansas</option>

                    <option value="KY">Kentucky</option>
                    <option value="LA">Louisiana</option>
                    <option value="MA">Massachusetts</option>
                    <option value="MD">Maryland</option>
                    <option value="ME">Maine</option>
                    <option value="MI">Michigan</option>

                    <option value="MN">Minnesota</option>
                    <option value="MO">Missouri</option>
                    <option value="MS">Mississippi</option>
                    <option value="MT">Montana</option>
                    <option value="NC">North Carolina</option>
                    <option value="ND">North Dakota</option>

                    <option value="NE">Nebraska</option>
                    <option value="NH">New Hampshire</option>
                    <option value="NJ">New Jersey</option>
                    <option value="NM">New Mexico</option>
                    <option value="NV">Nevada</option>
                    <option value="NY">New York</option>

                    <option value="OH">Ohio</option>
                    <option value="OK">Oklahoma</option>
                    <option value="OR">Oregon</option>
                    <option value="PA">Pennsylvania</option>
                    <option value="PR">Puerto Rico</option>
                    <option value="RI">Rhode Island</option>

                    <option value="SC">South Carolina</option>
                    <option value="SD">South Dakota</option>
                    <option value="TN">Tennessee</option>
                    <option value="TX">Texas</option>
                    <option value="UT">Utah</option>
                    <option value="VA">Virginia</option>

                    <option value="VT">Vermont</option>
                    <option value="WA">Washington</option>
                    <option value="WI">Wisconsin</option>
                    <option value="WV">West Virginia</option>
                    <option value="WY">Wyoming</option>
                  </select></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Suffix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber634' value='' size='16' style="" ></td>
                <th  align="right">Country:</th>
                <td ><select name="select2" size="1" tabindex="9">
                    <option value="" selected>select:</option>

                    <option value="US">United States</option>
                    <option value="AF">Afghanistan</option>
                    <option value="AL">Albania</option>
                    <option value="AG">Algeria</option>
                    <option value="AQ">American Samoa</option>
                    <option value="AN">Andorra</option>

                    <option value="AO">Angola</option>
                    <option value="AV">Anguilla</option>
                    <option value="AY">Antarctica</option>
                    <option value="AC">Antigua &amp; Barbuda</option>
                    <option value="AR">Argentina</option>

                    <option value="AM">Armenia</option>
                    <option value="AA">Aruba</option>
                    <option value="AT">Ashmore &amp; Caratier Island</option>
                    <option value="AS">Australia</option>
                    <option value="AU">Austria</option>

                    <option value="AJ">Azerbaijan</option>
                    <option value="BF">Bahamas</option>
                    <option value="BA">Bahrain</option>
                    <option value="FQ">Baker Island</option>
                    <option value="BG">Bangladesh</option>
                    <option value="BB">Barbados</option>

                    <option value="BS">Bassas Da India</option>
                    <option value="BO">Belarus</option>
                    <option value="BE">Belgium</option>
                    <option value="BH">Belize</option>
                    <option value="BN">Benin</option>
                    <option value="BD">Bermuda</option>

                    <option value="BT">Bhutan</option>
                    <option value="BL">Boliva</option>
                    <option value="BK">Bosnia &amp; Hercegovina</option>
                    <option value="BC">Botswana</option>
                    <option value="BV">Bouvet Island</option>

                    <option value="BR">Brazil</option>
                    <option value="IO">British Indian Ocean Territory</option>
                    <option value="BX">Brunei</option>
                    <option value="BU">Bulgaria</option>
                    <option value="BY">Burundi</option>
                    <option value="CB">Cambodia</option>

                    <option value="CM">Cameroon</option>
                    <option value="CA">Canada</option>
                    <option value="CV">Cape Verde</option>
                    <option value="CJ">Cayman Islands</option>
                    <option value="CT">Central African Republic</option>
                    <option value="CD">Chad</option>

                    <option value="CI">Chile</option>
                    <option value="CH">China</option>
                    <option value="KT">Christmas Island - Indian Ocean</option>
                    <option value="IP">Clipperton Island</option>
                    <option value="CO">Colombia</option>
                    <option value="CN">Comoros</option>

                    <option value="CF">Congo</option>
                    <option value="CW">Cook Islands</option>
                    <option value="CR">Coral Sea Islands Territory</option>
                    <option value="CS">Costa Rica</option>
                    <option value="HR">Croatia</option>
                    <option value="CY">Cyprus</option>

                    <option value="EZ">Czech Republic</option>
                    <option value="CZ">Czechoslovakia</option>
                    <option value="DA">Denmark</option>
                    <option value="DJ">Djibouti</option>
                    <option value="DO">Dominica</option>
                    <option value="DR">Dominican Republic</option>

                    <option value="EC">Ecuador</option>
                    <option value="EG">Egypt</option>
                    <option value="ES">El Salvador</option>
                    <option value="EK">Equatorial Guinea</option>
                    <option value="ER">Eritrea</option>
                    <option value="EN">Estonia</option>

                    <option value="ET">Ethiopia</option>
                    <option value="EU">Europa Island</option>
                    <option value="FA">Falkland Islands</option>
                    <option value="FO">Faroe Islands</option>
                    <option value="FJ">Fiji</option>
                    <option value="FI">Finland</option>

                    <option value="FR">France</option>
                    <option value="FG">French Guiana</option>
                    <option value="FP">French Polynesia</option>
                    <option value="FS">French Southern &amp; Antarctic Lands</option>
                    <option value="GB">Gabon</option>

                    <option value="GA">Gambia</option>
                    <option value="GZ">Gaza Strip</option>
                    <option value="GG">Georgia</option>
                    <option value="GE">Germany</option>
                    <option value="GH">Ghana</option>
                    <option value="GI">Gibraltar</option>

                    <option value="GO">Glorioso Islands</option>
                    <option value="GR">Greece</option>
                    <option value="GL">Greenland</option>
                    <option value="GJ">Grenada</option>
                    <option value="GP">Guadeloupe</option>
                    <option value="GQ">Guam</option>

                    <option value="GT">Guatemala</option>
                    <option value="GK">Guernsey</option>
                    <option value="GV">Guinea</option>
                    <option value="PU">Guinea-Bissau</option>
                    <option value="GY">Guyana</option>
                    <option value="HA">Haita</option>

                    <option value="HM">Heard Island &amp; Mcdonald Islands</option>
                    <option value="HO">Honduras</option>
                    <option value="HK">Hong Kong</option>
                    <option value="HQ">Howland Island</option>
                    <option value="HU">Hungary</option>

                    <option value="IC">Iceland</option>
                    <option value="IN">India</option>
                    <option value="ID">Indonesia</option>
                    <option value="IZ">Iraq</option>
                    <option value="IY">Iraq-Saudi Arabia</option>
                    <option value="EI">Ireland</option>

                    <option value="IM">Isle of Man</option>
                    <option value="IS">Israel</option>
                    <option value="IT">Italy</option>
                    <option value="IV">Ivory Coast</option>
                    <option value="JM">Jamaica</option>
                    <option value="JN">Jan Mayen</option>

                    <option value="JA">Japan</option>
                    <option value="JE">Jersey</option>
                    <option value="JQ">Johnston Atoll</option>
                    <option value="JO">Jordan</option>
                    <option value="JU">Juan De Nova Island</option>
                    <option value="KZ">Kazakhstan</option>

                    <option value="CK">Keeling Island</option>
                    <option value="KE">Kenya</option>
                    <option value="KQ">Kingman Reef</option>
                    <option value="KR">Kiribati Christmas Island</option>
                    <option value="KS">Korea South</option>
                    <option value="KU">Kuwait</option>

                    <option value="KG">Kyrgyzstan</option>
                    <option value="LA">Laos</option>
                    <option value="LG">Latvia</option>
                    <option value="LE">Lebanon</option>
                    <option value="LT">Lesotho</option>
                    <option value="LI">Liberia</option>

                    <option value="LS">Liechtenstein</option>
                    <option value="LH">Lithuania</option>
                    <option value="LU">Luxembourg</option>
                    <option value="MC">Macai</option>
                    <option value="MK">Macedonia</option>
                    <option value="MA">Madagascar</option>

                    <option value="MI">Malawi</option>
                    <option value="MY">Malaysia</option>
                    <option value="MV">Maldives</option>
                    <option value="ML">Mali</option>
                    <option value="MT">Malta</option>
                    <option value="RM">Marshall Islands</option>

                    <option value="MB">Martinique</option>
                    <option value="MR">Mauritania</option>
                    <option value="MP">Mauritius</option>
                    <option value="MF">Mayotte</option>
                    <option value="MX">Mexico</option>
                    <option value="FM">Micronesia</option>

                    <option value="MQ">Midway Islands</option>
                    <option value="MD">Moldova</option>
                    <option value="MN">Monaco</option>
                    <option value="MG">Mongolia</option>
                    <option value="MH">Montserrat</option>
                    <option value="MO">Morocco</option>

                    <option value="MZ">Mozambique</option>
                    <option value="WA">Namibia</option>
                    <option value="NR">Nauru</option>
                    <option value="BQ">Navassa Island</option>
                    <option value="NP">Nepal</option>
                    <option value="NL">Netherlands</option>

                    <option value="NA">Netherlands Antilles</option>
                    <option value="NC">Netherlands Antilles</option>
                    <option value="NZ">New Zealand</option>
                    <option value="NU">Nicaragua</option>
                    <option value="NG">Niger</option>
                    <option value="NI">Nigeria</option>

                    <option value="NE">Niue</option>
                    <option value="NF">Norfolk Island</option>
                    <option value="CQ">Northern Mariana Islands</option>
                    <option value="NO">Norway</option>
                    <option value="MU">Oman</option>
                    <option value="PS">Pacific Islands, Trust Territory Of The</option>

                    <option value="PK">Pakistan</option>
                    <option value="LQ">Palmyra Atoll</option>
                    <option value="PM">Panama</option>
                    <option value="PP">Papau New Guinea</option>
                    <option value="PF">Paracel Islands</option>
                    <option value="PA">Paraguay</option>

                    <option value="PE">Peru</option>
                    <option value="RP">Philippines</option>
                    <option value="PC">Pitcairn Island</option>
                    <option value="PL">Poland</option>
                    <option value="PO">Portugal &amp; Azores</option>

                    <option value="RQ">Puerto Rico</option>
                    <option value="QA">Qatar</option>
                    <option value="RE">Reunion</option>
                    <option value="RO">Romania</option>
                    <option value="RS">Russia</option>
                    <option value="RW">Rwanda</option>

                    <option value="SM">San Marino</option>
                    <option value="TP">Sao Tome &amp; Principle</option>
                    <option value="SA">Saudi Arabia</option>
                    <option value="SG">Senegal</option>
                    <option value="SR">Serbia &amp; Montenegro</option>

                    <option value="SE">Seychelles</option>
                    <option value="SL">Sierra Leone</option>
                    <option value="SN">Singapore</option>
                    <option value="LO">Slovakia</option>
                    <option value="SI">Slovenia</option>
                    <option value="BP">Solomon Islands</option>

                    <option value="SO">Somalia</option>
                    <option value="SF">South Africa</option>
                    <option value="SP">Spain &amp; Canary Islands</option>
                    <option value="PG">Spratly Islands</option>
                    <option value="CE">Sri Lanka</option>

                    <option value="SH">St Helena</option>
                    <option value="SC">St Kitts &amp; Nevis</option>
                    <option value="ST">St Lucia</option>
                    <option value="SB">St Pierre &amp; Miquelon</option>
                    <option value="VC">St Vincent And The Grenadines </option>

                    <option value="NS">Suriname</option>
                    <option value="SV">Svalbard</option>
                    <option value="WZ">Swaziland</option>
                    <option value="SW">Sweden</option>
                    <option value="SZ">Switzerland</option>
                    <option value="TW">Taiwan</option>

                    <option value="TI">Tajikistan</option>
                    <option value="TZ">Tanzania</option>
                    <option value="TH">Thailand</option>
                    <option value="TO">Togo</option>
                    <option value="TL">Tokelau</option>
                    <option value="TN">Tonga</option>

                    <option value="TD">Trinidad &amp; Tobago</option>
                    <option value="TE">Tromelin Island</option>
                    <option value="TS">Tunisia</option>
                    <option value="TU">Turkey</option>
                    <option value="TX">Turkmenistan</option>

                    <option value="TK">Turks &amp; Caicos Island</option>
                    <option value="TV">Tuvalu</option>
                    <option value="UR">USSR</option>
                    <option value="UG">Uganda</option>
                    <option value="UP">Ukraine</option>

                    <option value="TC">United Arab Emirates</option>
                    <option value="UK">United Kingdom &amp; Northern Ireland</option>
                    <option value="UY">Uruguay</option>
                    <option value="UZ">Uzbekistan</option>
                    <option value="NH">Vanuatu</option>

                    <option value="VT">Vatican City</option>
                    <option value="VE">Venezuela</option>
                    <option value="VM">Vietnam</option>
                    <option value="VQ">Virgin Islands</option>
                    <option value="VI">Virgin Islands-British</option>
                    <option value="WQ">Wake Island</option>

                    <option value="WF">Wallis &amp; Futuna</option>
                    <option value="WE">West Bank</option>
                    <option value="WI">Western Sahara</option>
                    <option value="WS">Western Samoa</option>
                    <option value="YM">Yemen</option>

                    <option value="YO">Yugoslavia</option>
                    <option value="CG">Zaire</option>
                    <option value="ZA">Zambia</option>
                    <option value="ZB">Zimbabwe</option>
                  </select></td>
              </tr>
              <tr class="datatable">

                <th  align="right">Position Title:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber632' value='' size='16' style="" ></td>
                <th  align="right">Zip:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63112' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Organization:</th>

                <td >BL</td>
                <th  align="right">Phone:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63113' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Department:</th>
                <td >UITS</td>

                <th  align="right">Fax:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63114' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Division:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber637' value='' size='16' style="" ></td>
                <th  align="right">E-mail:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63115' value='' size='16' style="" ></td>
              </tr>
              <%--
              <tr class="datatable">
                <th  align="right">% Intellectual Credit:</th>
                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>

                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr class="datatable">
                <th  align="right">% F&amp;A Credit:</th>

                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>
                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>

                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>

                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">

                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>

                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              --%>
            </table>
          </div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="tab" summary="">
            <tr>

              <td class="tabtable1-left"><img src="images/tab-topleft1.gif" alt="" width="12" height="29" align="absmiddle" > Parker, Charlie</td>
              <td class="tabtable1-mid1">PD/PI</td>
              <td class="tabtable2-mid"><a id="A04" onclick="rend(this, false)"><img src="images/tinybutton-show.gif" alt="show/hide this panel" width=45 height=15 border=0 id="F04" ></a> </td>
              <td class="tabtable2-right"><img src="images/tab-topright1.gif" alt="" width="12" height="29" align="absmiddle"></td>
            </tr>
          </table>
          <div class="tab-container" align="center" id="G04" style="display: none;">

            <div class="h2-container">
              <h2><span class="subhead-left">Parker, Charlie</span></h2>
              <span class="subhead-right"> <span class="subhead"><a href="asdf.html"><img src="images/my_cp_inf.gif" alt="help" width="15" height="14" border="0" align="absmiddle"></a></span> </span> </div>
            <table cellpadding=0 cellspacing="0" class="datatable">
              <tr class="datatable">
                <th  align="right">Role:</th>

                <td >PD/PI</td>
                <th  align="right">Street 1:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber638' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">First Name:</th>
                <td >Charlie</td>

                <th  align="right">Street 2:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber639' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Middle Name:</th>
                <td >R</td>
                <th  align="right">City:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6310' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Last Name:</th>
                <td >Parker</td>
                <th  align="right">County:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber6311' value='' size='16' style="" ></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Prefix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber633' value='' size='16' style="" ></td>
                <th  align="right">State:</th>
                <td ><select name="select" size="1" tabindex="6">
                    <option value="" selected>select:</option>

                    <option value="--">Out Of Country</option>
                    <option value="AK">Alaska</option>
                    <option value="AL">Alabama</option>
                    <option value="AR">Arkansas</option>
                    <option value="AZ">Arizona</option>
                    <option value="CA">California</option>

                    <option value="CO">Colorado</option>
                    <option value="CT">Connecticut</option>
                    <option value="DC">District Of Columbia</option>
                    <option value="DE">Delaware</option>
                    <option value="FL">Florida</option>
                    <option value="GA">Georgia</option>

                    <option value="HI">Hawaii</option>
                    <option value="IA">Iowa</option>
                    <option value="ID">Idaho</option>
                    <option value="IL">Illinois</option>
                    <option value="IN">Indiana</option>
                    <option value="KS">Kansas</option>

                    <option value="KY">Kentucky</option>
                    <option value="LA">Louisiana</option>
                    <option value="MA">Massachusetts</option>
                    <option value="MD">Maryland</option>
                    <option value="ME">Maine</option>
                    <option value="MI">Michigan</option>

                    <option value="MN">Minnesota</option>
                    <option value="MO">Missouri</option>
                    <option value="MS">Mississippi</option>
                    <option value="MT">Montana</option>
                    <option value="NC">North Carolina</option>
                    <option value="ND">North Dakota</option>

                    <option value="NE">Nebraska</option>
                    <option value="NH">New Hampshire</option>
                    <option value="NJ">New Jersey</option>
                    <option value="NM">New Mexico</option>
                    <option value="NV">Nevada</option>
                    <option value="NY">New York</option>

                    <option value="OH">Ohio</option>
                    <option value="OK">Oklahoma</option>
                    <option value="OR">Oregon</option>
                    <option value="PA">Pennsylvania</option>
                    <option value="PR">Puerto Rico</option>
                    <option value="RI">Rhode Island</option>

                    <option value="SC">South Carolina</option>
                    <option value="SD">South Dakota</option>
                    <option value="TN">Tennessee</option>
                    <option value="TX">Texas</option>
                    <option value="UT">Utah</option>
                    <option value="VA">Virginia</option>

                    <option value="VT">Vermont</option>
                    <option value="WA">Washington</option>
                    <option value="WI">Wisconsin</option>
                    <option value="WV">West Virginia</option>
                    <option value="WY">Wyoming</option>
                  </select></td>

              </tr>
              <tr class="datatable">
                <th  align="right">Suffix:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber634' value='' size='16' style="" ></td>
                <th  align="right">Country:</th>
                <td ><select name="select2" size="1" tabindex="9">
                    <option value="" selected>select:</option>

                    <option value="US">United States</option>
                    <option value="AF">Afghanistan</option>
                    <option value="AL">Albania</option>
                    <option value="AG">Algeria</option>
                    <option value="AQ">American Samoa</option>
                    <option value="AN">Andorra</option>

                    <option value="AO">Angola</option>
                    <option value="AV">Anguilla</option>
                    <option value="AY">Antarctica</option>
                    <option value="AC">Antigua &amp; Barbuda</option>
                    <option value="AR">Argentina</option>

                    <option value="AM">Armenia</option>
                    <option value="AA">Aruba</option>
                    <option value="AT">Ashmore &amp; Caratier Island</option>
                    <option value="AS">Australia</option>
                    <option value="AU">Austria</option>

                    <option value="AJ">Azerbaijan</option>
                    <option value="BF">Bahamas</option>
                    <option value="BA">Bahrain</option>
                    <option value="FQ">Baker Island</option>
                    <option value="BG">Bangladesh</option>
                    <option value="BB">Barbados</option>

                    <option value="BS">Bassas Da India</option>
                    <option value="BO">Belarus</option>
                    <option value="BE">Belgium</option>
                    <option value="BH">Belize</option>
                    <option value="BN">Benin</option>
                    <option value="BD">Bermuda</option>

                    <option value="BT">Bhutan</option>
                    <option value="BL">Boliva</option>
                    <option value="BK">Bosnia &amp; Hercegovina</option>
                    <option value="BC">Botswana</option>
                    <option value="BV">Bouvet Island</option>

                    <option value="BR">Brazil</option>
                    <option value="IO">British Indian Ocean Territory</option>
                    <option value="BX">Brunei</option>
                    <option value="BU">Bulgaria</option>
                    <option value="BY">Burundi</option>
                    <option value="CB">Cambodia</option>

                    <option value="CM">Cameroon</option>
                    <option value="CA">Canada</option>
                    <option value="CV">Cape Verde</option>
                    <option value="CJ">Cayman Islands</option>
                    <option value="CT">Central African Republic</option>
                    <option value="CD">Chad</option>

                    <option value="CI">Chile</option>
                    <option value="CH">China</option>
                    <option value="KT">Christmas Island - Indian Ocean</option>
                    <option value="IP">Clipperton Island</option>
                    <option value="CO">Colombia</option>
                    <option value="CN">Comoros</option>

                    <option value="CF">Congo</option>
                    <option value="CW">Cook Islands</option>
                    <option value="CR">Coral Sea Islands Territory</option>
                    <option value="CS">Costa Rica</option>
                    <option value="HR">Croatia</option>
                    <option value="CY">Cyprus</option>

                    <option value="EZ">Czech Republic</option>
                    <option value="CZ">Czechoslovakia</option>
                    <option value="DA">Denmark</option>
                    <option value="DJ">Djibouti</option>
                    <option value="DO">Dominica</option>
                    <option value="DR">Dominican Republic</option>

                    <option value="EC">Ecuador</option>
                    <option value="EG">Egypt</option>
                    <option value="ES">El Salvador</option>
                    <option value="EK">Equatorial Guinea</option>
                    <option value="ER">Eritrea</option>
                    <option value="EN">Estonia</option>

                    <option value="ET">Ethiopia</option>
                    <option value="EU">Europa Island</option>
                    <option value="FA">Falkland Islands</option>
                    <option value="FO">Faroe Islands</option>
                    <option value="FJ">Fiji</option>
                    <option value="FI">Finland</option>

                    <option value="FR">France</option>
                    <option value="FG">French Guiana</option>
                    <option value="FP">French Polynesia</option>
                    <option value="FS">French Southern &amp; Antarctic Lands</option>
                    <option value="GB">Gabon</option>

                    <option value="GA">Gambia</option>
                    <option value="GZ">Gaza Strip</option>
                    <option value="GG">Georgia</option>
                    <option value="GE">Germany</option>
                    <option value="GH">Ghana</option>
                    <option value="GI">Gibraltar</option>

                    <option value="GO">Glorioso Islands</option>
                    <option value="GR">Greece</option>
                    <option value="GL">Greenland</option>
                    <option value="GJ">Grenada</option>
                    <option value="GP">Guadeloupe</option>
                    <option value="GQ">Guam</option>

                    <option value="GT">Guatemala</option>
                    <option value="GK">Guernsey</option>
                    <option value="GV">Guinea</option>
                    <option value="PU">Guinea-Bissau</option>
                    <option value="GY">Guyana</option>
                    <option value="HA">Haita</option>

                    <option value="HM">Heard Island &amp; Mcdonald Islands</option>
                    <option value="HO">Honduras</option>
                    <option value="HK">Hong Kong</option>
                    <option value="HQ">Howland Island</option>
                    <option value="HU">Hungary</option>

                    <option value="IC">Iceland</option>
                    <option value="IN">India</option>
                    <option value="ID">Indonesia</option>
                    <option value="IZ">Iraq</option>
                    <option value="IY">Iraq-Saudi Arabia</option>
                    <option value="EI">Ireland</option>

                    <option value="IM">Isle of Man</option>
                    <option value="IS">Israel</option>
                    <option value="IT">Italy</option>
                    <option value="IV">Ivory Coast</option>
                    <option value="JM">Jamaica</option>
                    <option value="JN">Jan Mayen</option>

                    <option value="JA">Japan</option>
                    <option value="JE">Jersey</option>
                    <option value="JQ">Johnston Atoll</option>
                    <option value="JO">Jordan</option>
                    <option value="JU">Juan De Nova Island</option>
                    <option value="KZ">Kazakhstan</option>

                    <option value="CK">Keeling Island</option>
                    <option value="KE">Kenya</option>
                    <option value="KQ">Kingman Reef</option>
                    <option value="KR">Kiribati Christmas Island</option>
                    <option value="KS">Korea South</option>
                    <option value="KU">Kuwait</option>

                    <option value="KG">Kyrgyzstan</option>
                    <option value="LA">Laos</option>
                    <option value="LG">Latvia</option>
                    <option value="LE">Lebanon</option>
                    <option value="LT">Lesotho</option>
                    <option value="LI">Liberia</option>

                    <option value="LS">Liechtenstein</option>
                    <option value="LH">Lithuania</option>
                    <option value="LU">Luxembourg</option>
                    <option value="MC">Macai</option>
                    <option value="MK">Macedonia</option>
                    <option value="MA">Madagascar</option>

                    <option value="MI">Malawi</option>
                    <option value="MY">Malaysia</option>
                    <option value="MV">Maldives</option>
                    <option value="ML">Mali</option>
                    <option value="MT">Malta</option>
                    <option value="RM">Marshall Islands</option>

                    <option value="MB">Martinique</option>
                    <option value="MR">Mauritania</option>
                    <option value="MP">Mauritius</option>
                    <option value="MF">Mayotte</option>
                    <option value="MX">Mexico</option>
                    <option value="FM">Micronesia</option>

                    <option value="MQ">Midway Islands</option>
                    <option value="MD">Moldova</option>
                    <option value="MN">Monaco</option>
                    <option value="MG">Mongolia</option>
                    <option value="MH">Montserrat</option>
                    <option value="MO">Morocco</option>

                    <option value="MZ">Mozambique</option>
                    <option value="WA">Namibia</option>
                    <option value="NR">Nauru</option>
                    <option value="BQ">Navassa Island</option>
                    <option value="NP">Nepal</option>
                    <option value="NL">Netherlands</option>

                    <option value="NA">Netherlands Antilles</option>
                    <option value="NC">Netherlands Antilles</option>
                    <option value="NZ">New Zealand</option>
                    <option value="NU">Nicaragua</option>
                    <option value="NG">Niger</option>
                    <option value="NI">Nigeria</option>

                    <option value="NE">Niue</option>
                    <option value="NF">Norfolk Island</option>
                    <option value="CQ">Northern Mariana Islands</option>
                    <option value="NO">Norway</option>
                    <option value="MU">Oman</option>
                    <option value="PS">Pacific Islands, Trust Territory Of The</option>

                    <option value="PK">Pakistan</option>
                    <option value="LQ">Palmyra Atoll</option>
                    <option value="PM">Panama</option>
                    <option value="PP">Papau New Guinea</option>
                    <option value="PF">Paracel Islands</option>
                    <option value="PA">Paraguay</option>

                    <option value="PE">Peru</option>
                    <option value="RP">Philippines</option>
                    <option value="PC">Pitcairn Island</option>
                    <option value="PL">Poland</option>
                    <option value="PO">Portugal &amp; Azores</option>

                    <option value="RQ">Puerto Rico</option>
                    <option value="QA">Qatar</option>
                    <option value="RE">Reunion</option>
                    <option value="RO">Romania</option>
                    <option value="RS">Russia</option>
                    <option value="RW">Rwanda</option>

                    <option value="SM">San Marino</option>
                    <option value="TP">Sao Tome &amp; Principle</option>
                    <option value="SA">Saudi Arabia</option>
                    <option value="SG">Senegal</option>
                    <option value="SR">Serbia &amp; Montenegro</option>

                    <option value="SE">Seychelles</option>
                    <option value="SL">Sierra Leone</option>
                    <option value="SN">Singapore</option>
                    <option value="LO">Slovakia</option>
                    <option value="SI">Slovenia</option>
                    <option value="BP">Solomon Islands</option>

                    <option value="SO">Somalia</option>
                    <option value="SF">South Africa</option>
                    <option value="SP">Spain &amp; Canary Islands</option>
                    <option value="PG">Spratly Islands</option>
                    <option value="CE">Sri Lanka</option>

                    <option value="SH">St Helena</option>
                    <option value="SC">St Kitts &amp; Nevis</option>
                    <option value="ST">St Lucia</option>
                    <option value="SB">St Pierre &amp; Miquelon</option>
                    <option value="VC">St Vincent And The Grenadines </option>

                    <option value="NS">Suriname</option>
                    <option value="SV">Svalbard</option>
                    <option value="WZ">Swaziland</option>
                    <option value="SW">Sweden</option>
                    <option value="SZ">Switzerland</option>
                    <option value="TW">Taiwan</option>

                    <option value="TI">Tajikistan</option>
                    <option value="TZ">Tanzania</option>
                    <option value="TH">Thailand</option>
                    <option value="TO">Togo</option>
                    <option value="TL">Tokelau</option>
                    <option value="TN">Tonga</option>

                    <option value="TD">Trinidad &amp; Tobago</option>
                    <option value="TE">Tromelin Island</option>
                    <option value="TS">Tunisia</option>
                    <option value="TU">Turkey</option>
                    <option value="TX">Turkmenistan</option>

                    <option value="TK">Turks &amp; Caicos Island</option>
                    <option value="TV">Tuvalu</option>
                    <option value="UR">USSR</option>
                    <option value="UG">Uganda</option>
                    <option value="UP">Ukraine</option>

                    <option value="TC">United Arab Emirates</option>
                    <option value="UK">United Kingdom &amp; Northern Ireland</option>
                    <option value="UY">Uruguay</option>
                    <option value="UZ">Uzbekistan</option>
                    <option value="NH">Vanuatu</option>

                    <option value="VT">Vatican City</option>
                    <option value="VE">Venezuela</option>
                    <option value="VM">Vietnam</option>
                    <option value="VQ">Virgin Islands</option>
                    <option value="VI">Virgin Islands-British</option>
                    <option value="WQ">Wake Island</option>

                    <option value="WF">Wallis &amp; Futuna</option>
                    <option value="WE">West Bank</option>
                    <option value="WI">Western Sahara</option>
                    <option value="WS">Western Samoa</option>
                    <option value="YM">Yemen</option>

                    <option value="YO">Yugoslavia</option>
                    <option value="CG">Zaire</option>
                    <option value="ZA">Zambia</option>
                    <option value="ZB">Zimbabwe</option>
                  </select></td>
              </tr>
              <tr class="datatable">

                <th  align="right">Position Title:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber632' value='' size='16' style="" ></td>
                <th  align="right">Zip:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63112' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Organization:</th>

                <td >BL</td>
                <th  align="right">Phone:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63113' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Department:</th>
                <td >UITS</td>

                <th  align="right">Fax:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63114' value='' size='16' style="" ></td>
              </tr>
              <tr class="datatable">
                <th  align="right">Division:</th>
                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber637' value='' size='16' style="" ></td>
                <th  align="right">E-mail:</th>

                <td ><input type="text" name='document.newMaintainableObject.contractControlAccountNumber63115' value='' size='16' style="" ></td>
              </tr>
              <%--
              <tr class="datatable">
                <th  align="right">% Intellectual Credit:</th>
                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>

                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>

                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              <tr class="datatable">
                <th  align="right">% F&amp;A Credit:</th>

                <td colspan="3" ><table cellpadding=0 cellspacing=0 class="neutral">
                    <tr>
                      <td nowrap class="neutral"><strong>Credit % </strong></td>
                      <td nowrap class="neutral"><strong>Unit Name </strong></td>
                      <td class="neutral"><strong>Action</strong></td>
                    </tr>
                    <tr>

                      <td nowrap class="neutral"><div align=left>
                          <input name="T12323" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs62" src="images/tinybutton-add1.gif" class="tinybutton" alt="showAll">
                        </div></td>

                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T1232" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>
                          <input type="image" name="methodToCall.showAllTabs72" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">

                        </div></td>
                    </tr>
                    <tr>
                      <td nowrap class="neutral"><div align=left>
                          <input name="T12322" value="30%" size=5 style="text-align:right;">
                        </div></td>
                      <td nowrap class="neutral"> Department of ******** </td>
                      <td class="neutral"><div align=center>

                          <input type="image" name="methodToCall.showAllTabs82" src="images/tinybutton-delete1.gif" class="tinybutton" alt="showAll">
                        </div></td>
                    </tr>
                  </table></td>
              </tr>
              --%>
            </table>
          </div>
          <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
            <tr>

              <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
              <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
            </tr>
          </table>
          <p>&nbsp;</p>
        </div>
        <div class="globalbuttons"> <a href="overview.html"><img src="images/buttonsmall_save.gif" alt="save" width="53" height="18" hspace="5" border="0"></a><a href="../confirm-cancel.html"><img src="images/buttonsmall_cancel.gif" alt="cancel" width="66" height="18" hspace="5" border="0"></a> </div></td>
      <td class="column-right"><img src="images/pixel_clear.gif" alt="" width="20" height="20"></td>

    </tr>
  </table>
	
</kul:documentPage>