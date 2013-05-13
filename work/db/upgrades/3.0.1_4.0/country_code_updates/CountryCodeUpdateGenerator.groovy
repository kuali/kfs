/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * This script generates 2 SQL files used for the conversion of country code data from
 * pre-Rice 1.0.3 to Rice 1.0.3 codes.  The 2 files are a validation file that checks 
 * for the presence of codes not covered and the second file performs the conversion.
 * The SQL generated should run in both Oracle and MySQL.
 */

if (args.length != 2 && args.length != 3) {
	println(" Usage: groovy CountryCodeUpdateGenerator.groovy TABLE_NAME COLUMN_NAME [OUTPUT_DIR_PATH]")
	println()
	println(instructiontext())
	System.exit(1)
}

def tableName = args[0]
def colName = args[1]
def outputPath = "."
if (args.length == 3) outputPath = args[2]
if (tableName.toString().indexOf("yyyyy") != -1) {
	println('"yyyyy" is a reserved string and cannot be used as the table name when using this script.')
	System.exit(1);
}

print("processing " + tableName + "." + colName + "...")

if (! new File(outputPath).exists()) new File(outputPath).mkdirs();

file = new File(outputPath + "/convert_" + tableName + "_countryCode.sql")
file.delete()
file << conversiontext().toString().replace("xxxxx", tableName).replace("yyyyy", colName)
file = new File(outputPath + "/validate_" + tableName + "_countryCode.sql")
file.delete()
file << validationtext().toString().replace("xxxxx", tableName).replace("yyyyy", colName)

println("...complete.")

System.exit(0)

def instructiontext() {
''' This groovy script creates 2 sql files. "countryCodeValidate.sql" will check a
 particular table and column for the presence of country codes that are not 
 covered by the conversion. "countryCodeConvert.sql" performs the conversion of
 the contents of a particular table and column from pre-Rice1.0.3 country codes
 to Rice 1.0.3 codes. If the files with those names exist, they will be 
 overwritten.
'''
}

def conversiontext() {
'''
-- This script will migrate a column in a table from the former Rice country 
-- codes which were based on FIPS 10-4 (with some minor differences) to the new
-- Rice country codes which are based on ISO 3166-1.  This script may not 
-- properly execute on columns with a primary key or unique constraint as some
-- of the former codes have merged (i.e. - all US Minor Outlying Islands have 
-- been unified under a single code, UM).  This script also does not take any 
-- action on codes that are not part of the list of former Rice country codes.
--
-- Table Name: 	xxxxx
-- Column Name: yyyyy
--
-- In order to avoid collisions between the former codes and the new codes, the 
-- codes are first changed to an interim, unique code.  Once that change is 
-- complete, they are changed to the new, correct code.
--
-- Change to temporary code
UPDATE xxxxx SET yyyyy='A0' where yyyyy='AA';
UPDATE xxxxx SET yyyyy='A1' where yyyyy='AC';
UPDATE xxxxx SET yyyyy='A3' where yyyyy='AG';
UPDATE xxxxx SET yyyyy='A4' where yyyyy='AJ';
UPDATE xxxxx SET yyyyy='A7' where yyyyy='AN';
UPDATE xxxxx SET yyyyy='A9' where yyyyy='AQ';
UPDATE xxxxx SET yyyyy='B1' where yyyyy='AS';
UPDATE xxxxx SET yyyyy='B2' where yyyyy='AT';
UPDATE xxxxx SET yyyyy='B3' where yyyyy='AU';
UPDATE xxxxx SET yyyyy='B4' where yyyyy='AV';
UPDATE xxxxx SET yyyyy='B5' where yyyyy='AY';
UPDATE xxxxx SET yyyyy='B6' where yyyyy='BA';
UPDATE xxxxx SET yyyyy='B8' where yyyyy='BC';
UPDATE xxxxx SET yyyyy='B9' where yyyyy='BD';
UPDATE xxxxx SET yyyyy='C1' where yyyyy='BF';
UPDATE xxxxx SET yyyyy='C2' where yyyyy='BG';
UPDATE xxxxx SET yyyyy='C3' where yyyyy='BH';
UPDATE xxxxx SET yyyyy='C4' where yyyyy='BK';
UPDATE xxxxx SET yyyyy='C5' where yyyyy='BL';
UPDATE xxxxx SET yyyyy='C6' where yyyyy='BM';
UPDATE xxxxx SET yyyyy='C7' where yyyyy='BN';
UPDATE xxxxx SET yyyyy='C8' where yyyyy='BO';
UPDATE xxxxx SET yyyyy='C9' where yyyyy='BP';
UPDATE xxxxx SET yyyyy='D0' where yyyyy='BQ';
UPDATE xxxxx SET yyyyy='D2' where yyyyy='BS';
UPDATE xxxxx SET yyyyy='D4' where yyyyy='BU';
UPDATE xxxxx SET yyyyy='D6' where yyyyy='BX';
UPDATE xxxxx SET yyyyy='D7' where yyyyy='BY';
UPDATE xxxxx SET yyyyy='D9' where yyyyy='CB';
UPDATE xxxxx SET yyyyy='E0' where yyyyy='CD';
UPDATE xxxxx SET yyyyy='E1' where yyyyy='CE';
UPDATE xxxxx SET yyyyy='E2' where yyyyy='CF';
UPDATE xxxxx SET yyyyy='E3' where yyyyy='CG';
UPDATE xxxxx SET yyyyy='E4' where yyyyy='CH';
UPDATE xxxxx SET yyyyy='E5' where yyyyy='CI';
UPDATE xxxxx SET yyyyy='E6' where yyyyy='CJ';
UPDATE xxxxx SET yyyyy='E7' where yyyyy='CK';
UPDATE xxxxx SET yyyyy='E9' where yyyyy='CN';
UPDATE xxxxx SET yyyyy='F1' where yyyyy='CQ';
UPDATE xxxxx SET yyyyy='F2' where yyyyy='CR';
UPDATE xxxxx SET yyyyy='F3' where yyyyy='CS';
UPDATE xxxxx SET yyyyy='F4' where yyyyy='CT';
UPDATE xxxxx SET yyyyy='F7' where yyyyy='CW';
UPDATE xxxxx SET yyyyy='F9' where yyyyy='CZ';
UPDATE xxxxx SET yyyyy='G0' where yyyyy='DA';
UPDATE xxxxx SET yyyyy='G2' where yyyyy='DO';
UPDATE xxxxx SET yyyyy='G3' where yyyyy='DR';
UPDATE xxxxx SET yyyyy='G6' where yyyyy='EI';
UPDATE xxxxx SET yyyyy='G7' where yyyyy='EK';
UPDATE xxxxx SET yyyyy='G8' where yyyyy='EN';
UPDATE xxxxx SET yyyyy='H0' where yyyyy='ES';
UPDATE xxxxx SET yyyyy='H2' where yyyyy='EU';
UPDATE xxxxx SET yyyyy='H3' where yyyyy='EZ';
UPDATE xxxxx SET yyyyy='H4' where yyyyy='FA';
UPDATE xxxxx SET yyyyy='H5' where yyyyy='FG';
UPDATE xxxxx SET yyyyy='I0' where yyyyy='FP';
UPDATE xxxxx SET yyyyy='I1' where yyyyy='FQ';
UPDATE xxxxx SET yyyyy='I3' where yyyyy='FS';
UPDATE xxxxx SET yyyyy='I4' where yyyyy='GA';
UPDATE xxxxx SET yyyyy='I5' where yyyyy='GB';
UPDATE xxxxx SET yyyyy='I6' where yyyyy='GE';
UPDATE xxxxx SET yyyyy='I7' where yyyyy='GG';
UPDATE xxxxx SET yyyyy='J0' where yyyyy='GJ';
UPDATE xxxxx SET yyyyy='J1' where yyyyy='GK';
UPDATE xxxxx SET yyyyy='J3' where yyyyy='GM';
UPDATE xxxxx SET yyyyy='J4' where yyyyy='GO';
UPDATE xxxxx SET yyyyy='J6' where yyyyy='GQ';
UPDATE xxxxx SET yyyyy='J9' where yyyyy='GV';
UPDATE xxxxx SET yyyyy='K1' where yyyyy='GZ';
UPDATE xxxxx SET yyyyy='K2' where yyyyy='HA';
UPDATE xxxxx SET yyyyy='K5' where yyyyy='HO';
UPDATE xxxxx SET yyyyy='K6' where yyyyy='HQ';
UPDATE xxxxx SET yyyyy='K9' where yyyyy='IC';
UPDATE xxxxx SET yyyyy='L4' where yyyyy='IP';
UPDATE xxxxx SET yyyyy='L6' where yyyyy='IS';
UPDATE xxxxx SET yyyyy='L8' where yyyyy='IV';
UPDATE xxxxx SET yyyyy='L9' where yyyyy='IY';
UPDATE xxxxx SET yyyyy='M0' where yyyyy='IZ';
UPDATE xxxxx SET yyyyy='M1' where yyyyy='JA';
UPDATE xxxxx SET yyyyy='M4' where yyyyy='JN';
UPDATE xxxxx SET yyyyy='M6' where yyyyy='JQ';
UPDATE xxxxx SET yyyyy='M7' where yyyyy='JU';
UPDATE xxxxx SET yyyyy='N0' where yyyyy='KN';
UPDATE xxxxx SET yyyyy='N1' where yyyyy='KQ';
UPDATE xxxxx SET yyyyy='N2' where yyyyy='KR';
UPDATE xxxxx SET yyyyy='N3' where yyyyy='KS';
UPDATE xxxxx SET yyyyy='N4' where yyyyy='KT';
UPDATE xxxxx SET yyyyy='N5' where yyyyy='KU';
UPDATE xxxxx SET yyyyy='N8' where yyyyy='LE';
UPDATE xxxxx SET yyyyy='N9' where yyyyy='LG';
UPDATE xxxxx SET yyyyy='O0' where yyyyy='LH';
UPDATE xxxxx SET yyyyy='O1' where yyyyy='LI';
UPDATE xxxxx SET yyyyy='O2' where yyyyy='LO';
UPDATE xxxxx SET yyyyy='O3' where yyyyy='LQ';
UPDATE xxxxx SET yyyyy='O4' where yyyyy='LS';
UPDATE xxxxx SET yyyyy='O5' where yyyyy='LT';
UPDATE xxxxx SET yyyyy='O8' where yyyyy='MA';
UPDATE xxxxx SET yyyyy='O9' where yyyyy='MB';
UPDATE xxxxx SET yyyyy='P0' where yyyyy='MC';
UPDATE xxxxx SET yyyyy='P2' where yyyyy='MF';
UPDATE xxxxx SET yyyyy='P3' where yyyyy='MG';
UPDATE xxxxx SET yyyyy='P4' where yyyyy='MH';
UPDATE xxxxx SET yyyyy='P5' where yyyyy='MI';
UPDATE xxxxx SET yyyyy='P8' where yyyyy='MN';
UPDATE xxxxx SET yyyyy='P9' where yyyyy='MO';
UPDATE xxxxx SET yyyyy='Q0' where yyyyy='MP';
UPDATE xxxxx SET yyyyy='Q1' where yyyyy='MQ';
UPDATE xxxxx SET yyyyy='Q4' where yyyyy='MU';
UPDATE xxxxx SET yyyyy='Q6' where yyyyy='MW';
UPDATE xxxxx SET yyyyy='R0' where yyyyy='NA';
UPDATE xxxxx SET yyyyy='R2' where yyyyy='NE';
UPDATE xxxxx SET yyyyy='R4' where yyyyy='NG';
UPDATE xxxxx SET yyyyy='R5' where yyyyy='NH';
UPDATE xxxxx SET yyyyy='R6' where yyyyy='NI';
UPDATE xxxxx SET yyyyy='S1' where yyyyy='NS';
UPDATE xxxxx SET yyyyy='S2' where yyyyy='NU';
UPDATE xxxxx SET yyyyy='S4' where yyyyy='OC';
UPDATE xxxxx SET yyyyy='S5' where yyyyy='PA';
UPDATE xxxxx SET yyyyy='S6' where yyyyy='PC';
UPDATE xxxxx SET yyyyy='S8' where yyyyy='PF';
UPDATE xxxxx SET yyyyy='S9' where yyyyy='PG';
UPDATE xxxxx SET yyyyy='T2' where yyyyy='PM';
UPDATE xxxxx SET yyyyy='T3' where yyyyy='PO';
UPDATE xxxxx SET yyyyy='T4' where yyyyy='PP';
UPDATE xxxxx SET yyyyy='T5' where yyyyy='PS';
UPDATE xxxxx SET yyyyy='T6' where yyyyy='PU';
UPDATE xxxxx SET yyyyy='T9' where yyyyy='RM';
UPDATE xxxxx SET yyyyy='U1' where yyyyy='RP';
UPDATE xxxxx SET yyyyy='U2' where yyyyy='RQ';
UPDATE xxxxx SET yyyyy='U3' where yyyyy='RS';
UPDATE xxxxx SET yyyyy='U6' where yyyyy='SB';
UPDATE xxxxx SET yyyyy='U7' where yyyyy='SC';
UPDATE xxxxx SET yyyyy='U8' where yyyyy='SE';
UPDATE xxxxx SET yyyyy='U9' where yyyyy='SF';
UPDATE xxxxx SET yyyyy='V0' where yyyyy='SG';
UPDATE xxxxx SET yyyyy='V5' where yyyyy='SN';
UPDATE xxxxx SET yyyyy='V7' where yyyyy='SP';
UPDATE xxxxx SET yyyyy='V8' where yyyyy='SR';
UPDATE xxxxx SET yyyyy='V9' where yyyyy='ST';
UPDATE xxxxx SET yyyyy='W0' where yyyyy='SU';
UPDATE xxxxx SET yyyyy='W1' where yyyyy='SV';
UPDATE xxxxx SET yyyyy='W2' where yyyyy='SW';
UPDATE xxxxx SET yyyyy='W4' where yyyyy='SZ';
UPDATE xxxxx SET yyyyy='W5' where yyyyy='TC';
UPDATE xxxxx SET yyyyy='W6' where yyyyy='TD';
UPDATE xxxxx SET yyyyy='W7' where yyyyy='TE';
UPDATE xxxxx SET yyyyy='W9' where yyyyy='TI';
UPDATE xxxxx SET yyyyy='X0' where yyyyy='TK';
UPDATE xxxxx SET yyyyy='X1' where yyyyy='TL';
UPDATE xxxxx SET yyyyy='X2' where yyyyy='TN';
UPDATE xxxxx SET yyyyy='X3' where yyyyy='TO';
UPDATE xxxxx SET yyyyy='X4' where yyyyy='TP';
UPDATE xxxxx SET yyyyy='X5' where yyyyy='TS';
UPDATE xxxxx SET yyyyy='X6' where yyyyy='TU';
UPDATE xxxxx SET yyyyy='X9' where yyyyy='TX';
UPDATE xxxxx SET yyyyy='Y2' where yyyyy='UK';
UPDATE xxxxx SET yyyyy='Y3' where yyyyy='UP';
UPDATE xxxxx SET yyyyy='Y4' where yyyyy='UR';
UPDATE xxxxx SET yyyyy='Z0' where yyyyy='VI';
UPDATE xxxxx SET yyyyy='Z1' where yyyyy='VM';
UPDATE xxxxx SET yyyyy='Z2' where yyyyy='VQ';
UPDATE xxxxx SET yyyyy='Z3' where yyyyy='VT';
UPDATE xxxxx SET yyyyy='Z4' where yyyyy='WA';
UPDATE xxxxx SET yyyyy='Z5' where yyyyy='WE';
UPDATE xxxxx SET yyyyy='Z7' where yyyyy='WI';
UPDATE xxxxx SET yyyyy='Z8' where yyyyy='WQ';
UPDATE xxxxx SET yyyyy='00' where yyyyy='WZ';
UPDATE xxxxx SET yyyyy='01' where yyyyy='YM';
UPDATE xxxxx SET yyyyy='02' where yyyyy='YO';
UPDATE xxxxx SET yyyyy='03' where yyyyy='ZA';
UPDATE xxxxx SET yyyyy='04' where yyyyy='ZI';
-- Change to final code
UPDATE xxxxx SET yyyyy='AW' where yyyyy='A0';
UPDATE xxxxx SET yyyyy='AG' where yyyyy='A1';
UPDATE xxxxx SET yyyyy='DZ' where yyyyy='A3';
UPDATE xxxxx SET yyyyy='AZ' where yyyyy='A4';
UPDATE xxxxx SET yyyyy='AD' where yyyyy='A7';
UPDATE xxxxx SET yyyyy='AS' where yyyyy='A9';
UPDATE xxxxx SET yyyyy='AU' where yyyyy='B1';
UPDATE xxxxx SET yyyyy='AU' where yyyyy='B2';
UPDATE xxxxx SET yyyyy='AT' where yyyyy='B3';
UPDATE xxxxx SET yyyyy='AI' where yyyyy='B4';
UPDATE xxxxx SET yyyyy='AQ' where yyyyy='B5';
UPDATE xxxxx SET yyyyy='BH' where yyyyy='B6';
UPDATE xxxxx SET yyyyy='BW' where yyyyy='B8';
UPDATE xxxxx SET yyyyy='BM' where yyyyy='B9';
UPDATE xxxxx SET yyyyy='BS' where yyyyy='C1';
UPDATE xxxxx SET yyyyy='BD' where yyyyy='C2';
UPDATE xxxxx SET yyyyy='BZ' where yyyyy='C3';
UPDATE xxxxx SET yyyyy='BA' where yyyyy='C4';
UPDATE xxxxx SET yyyyy='BO' where yyyyy='C5';
UPDATE xxxxx SET yyyyy='MM' where yyyyy='C6';
UPDATE xxxxx SET yyyyy='BJ' where yyyyy='C7';
UPDATE xxxxx SET yyyyy='BY' where yyyyy='C8';
UPDATE xxxxx SET yyyyy='SB' where yyyyy='C9';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='D0';
UPDATE xxxxx SET yyyyy='TF' where yyyyy='D2';
UPDATE xxxxx SET yyyyy='BG' where yyyyy='D4';
UPDATE xxxxx SET yyyyy='BN' where yyyyy='D6';
UPDATE xxxxx SET yyyyy='BI' where yyyyy='D7';
UPDATE xxxxx SET yyyyy='KH' where yyyyy='D9';
UPDATE xxxxx SET yyyyy='TD' where yyyyy='E0';
UPDATE xxxxx SET yyyyy='LK' where yyyyy='E1';
UPDATE xxxxx SET yyyyy='CG' where yyyyy='E2';
UPDATE xxxxx SET yyyyy='CD' where yyyyy='E3';
UPDATE xxxxx SET yyyyy='CN' where yyyyy='E4';
UPDATE xxxxx SET yyyyy='CL' where yyyyy='E5';
UPDATE xxxxx SET yyyyy='KY' where yyyyy='E6';
UPDATE xxxxx SET yyyyy='CC' where yyyyy='E7';
UPDATE xxxxx SET yyyyy='KM' where yyyyy='E9';
UPDATE xxxxx SET yyyyy='MP' where yyyyy='F1';
UPDATE xxxxx SET yyyyy='AU' where yyyyy='F2';
UPDATE xxxxx SET yyyyy='CR' where yyyyy='F3';
UPDATE xxxxx SET yyyyy='CF' where yyyyy='F4';
UPDATE xxxxx SET yyyyy='CK' where yyyyy='F7';
UPDATE xxxxx SET yyyyy='CS' where yyyyy='F9';
UPDATE xxxxx SET yyyyy='DK' where yyyyy='G0';
UPDATE xxxxx SET yyyyy='DM' where yyyyy='G2';
UPDATE xxxxx SET yyyyy='DO' where yyyyy='G3';
UPDATE xxxxx SET yyyyy='IE' where yyyyy='G6';
UPDATE xxxxx SET yyyyy='GQ' where yyyyy='G7';
UPDATE xxxxx SET yyyyy='EE' where yyyyy='G8';
UPDATE xxxxx SET yyyyy='SV' where yyyyy='H0';
UPDATE xxxxx SET yyyyy='TF' where yyyyy='H2';
UPDATE xxxxx SET yyyyy='CZ' where yyyyy='H3';
UPDATE xxxxx SET yyyyy='FK' where yyyyy='H4';
UPDATE xxxxx SET yyyyy='GF' where yyyyy='H5';
UPDATE xxxxx SET yyyyy='PF' where yyyyy='I0';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='I1';
UPDATE xxxxx SET yyyyy='TF' where yyyyy='I3';
UPDATE xxxxx SET yyyyy='GM' where yyyyy='I4';
UPDATE xxxxx SET yyyyy='GA' where yyyyy='I5';
UPDATE xxxxx SET yyyyy='DE' where yyyyy='I6';
UPDATE xxxxx SET yyyyy='GE' where yyyyy='I7';
UPDATE xxxxx SET yyyyy='GD' where yyyyy='J0';
UPDATE xxxxx SET yyyyy='GG' where yyyyy='J1';
UPDATE xxxxx SET yyyyy='DE' where yyyyy='J3';
UPDATE xxxxx SET yyyyy='TF' where yyyyy='J4';
UPDATE xxxxx SET yyyyy='GU' where yyyyy='J6';
UPDATE xxxxx SET yyyyy='GN' where yyyyy='J9';
UPDATE xxxxx SET yyyyy='PS' where yyyyy='K1';
UPDATE xxxxx SET yyyyy='HT' where yyyyy='K2';
UPDATE xxxxx SET yyyyy='HN' where yyyyy='K5';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='K6';
UPDATE xxxxx SET yyyyy='IS' where yyyyy='K9';
UPDATE xxxxx SET yyyyy='FR' where yyyyy='L4';
UPDATE xxxxx SET yyyyy='IL' where yyyyy='L6';
UPDATE xxxxx SET yyyyy='CI' where yyyyy='L8';
UPDATE xxxxx SET yyyyy='NT' where yyyyy='L9';
UPDATE xxxxx SET yyyyy='IQ' where yyyyy='M0';
UPDATE xxxxx SET yyyyy='JP' where yyyyy='M1';
UPDATE xxxxx SET yyyyy='NO' where yyyyy='M4';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='M6';
UPDATE xxxxx SET yyyyy='TF' where yyyyy='M7';
UPDATE xxxxx SET yyyyy='KP' where yyyyy='N0';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='N1';
UPDATE xxxxx SET yyyyy='KI' where yyyyy='N2';
UPDATE xxxxx SET yyyyy='KR' where yyyyy='N3';
UPDATE xxxxx SET yyyyy='CX' where yyyyy='N4';
UPDATE xxxxx SET yyyyy='KW' where yyyyy='N5';
UPDATE xxxxx SET yyyyy='LB' where yyyyy='N8';
UPDATE xxxxx SET yyyyy='LV' where yyyyy='N9';
UPDATE xxxxx SET yyyyy='LT' where yyyyy='O0';
UPDATE xxxxx SET yyyyy='LR' where yyyyy='O1';
UPDATE xxxxx SET yyyyy='SK' where yyyyy='O2';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='O3';
UPDATE xxxxx SET yyyyy='LI' where yyyyy='O4';
UPDATE xxxxx SET yyyyy='LS' where yyyyy='O5';
UPDATE xxxxx SET yyyyy='MG' where yyyyy='O8';
UPDATE xxxxx SET yyyyy='MQ' where yyyyy='O9';
UPDATE xxxxx SET yyyyy='MO' where yyyyy='P0';
UPDATE xxxxx SET yyyyy='YT' where yyyyy='P2';
UPDATE xxxxx SET yyyyy='MN' where yyyyy='P3';
UPDATE xxxxx SET yyyyy='MS' where yyyyy='P4';
UPDATE xxxxx SET yyyyy='MW' where yyyyy='P5';
UPDATE xxxxx SET yyyyy='MC' where yyyyy='P8';
UPDATE xxxxx SET yyyyy='MA' where yyyyy='P9';
UPDATE xxxxx SET yyyyy='MU' where yyyyy='Q0';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='Q1';
UPDATE xxxxx SET yyyyy='OM' where yyyyy='Q4';
UPDATE xxxxx SET yyyyy='ME' where yyyyy='Q6';
UPDATE xxxxx SET yyyyy='AN' where yyyyy='R0';
UPDATE xxxxx SET yyyyy='NU' where yyyyy='R2';
UPDATE xxxxx SET yyyyy='NE' where yyyyy='R4';
UPDATE xxxxx SET yyyyy='VU' where yyyyy='R5';
UPDATE xxxxx SET yyyyy='NG' where yyyyy='R6';
UPDATE xxxxx SET yyyyy='SR' where yyyyy='S1';
UPDATE xxxxx SET yyyyy='NI' where yyyyy='S2';
UPDATE xxxxx SET yyyyy='ZZ' where yyyyy='S4';
UPDATE xxxxx SET yyyyy='PY' where yyyyy='S5';
UPDATE xxxxx SET yyyyy='PN' where yyyyy='S6';
UPDATE xxxxx SET yyyyy='XP' where yyyyy='S8';
UPDATE xxxxx SET yyyyy='XS' where yyyyy='S9';
UPDATE xxxxx SET yyyyy='PA' where yyyyy='T2';
UPDATE xxxxx SET yyyyy='PT' where yyyyy='T3';
UPDATE xxxxx SET yyyyy='PG' where yyyyy='T4';
UPDATE xxxxx SET yyyyy='PW' where yyyyy='T5';
UPDATE xxxxx SET yyyyy='GW' where yyyyy='T6';
UPDATE xxxxx SET yyyyy='MH' where yyyyy='T9';
UPDATE xxxxx SET yyyyy='PH' where yyyyy='U1';
UPDATE xxxxx SET yyyyy='PR' where yyyyy='U2';
UPDATE xxxxx SET yyyyy='RU' where yyyyy='U3';
UPDATE xxxxx SET yyyyy='PM' where yyyyy='U6';
UPDATE xxxxx SET yyyyy='KN' where yyyyy='U7';
UPDATE xxxxx SET yyyyy='SC' where yyyyy='U8';
UPDATE xxxxx SET yyyyy='ZA' where yyyyy='U9';
UPDATE xxxxx SET yyyyy='SN' where yyyyy='V0';
UPDATE xxxxx SET yyyyy='SG' where yyyyy='V5';
UPDATE xxxxx SET yyyyy='ES' where yyyyy='V7';
UPDATE xxxxx SET yyyyy='RS' where yyyyy='V8';
UPDATE xxxxx SET yyyyy='LC' where yyyyy='V9';
UPDATE xxxxx SET yyyyy='SD' where yyyyy='W0';
UPDATE xxxxx SET yyyyy='SJ' where yyyyy='W1';
UPDATE xxxxx SET yyyyy='SE' where yyyyy='W2';
UPDATE xxxxx SET yyyyy='CH' where yyyyy='W4';
UPDATE xxxxx SET yyyyy='AE' where yyyyy='W5';
UPDATE xxxxx SET yyyyy='TT' where yyyyy='W6';
UPDATE xxxxx SET yyyyy='TF' where yyyyy='W7';
UPDATE xxxxx SET yyyyy='TJ' where yyyyy='W9';
UPDATE xxxxx SET yyyyy='TC' where yyyyy='X0';
UPDATE xxxxx SET yyyyy='TK' where yyyyy='X1';
UPDATE xxxxx SET yyyyy='TO' where yyyyy='X2';
UPDATE xxxxx SET yyyyy='TG' where yyyyy='X3';
UPDATE xxxxx SET yyyyy='ST' where yyyyy='X4';
UPDATE xxxxx SET yyyyy='TN' where yyyyy='X5';
UPDATE xxxxx SET yyyyy='TR' where yyyyy='X6';
UPDATE xxxxx SET yyyyy='TM' where yyyyy='X9';
UPDATE xxxxx SET yyyyy='GB' where yyyyy='Y2';
UPDATE xxxxx SET yyyyy='UA' where yyyyy='Y3';
UPDATE xxxxx SET yyyyy='SU' where yyyyy='Y4';
UPDATE xxxxx SET yyyyy='VG' where yyyyy='Z0';
UPDATE xxxxx SET yyyyy='VN' where yyyyy='Z1';
UPDATE xxxxx SET yyyyy='VI' where yyyyy='Z2';
UPDATE xxxxx SET yyyyy='VA' where yyyyy='Z3';
UPDATE xxxxx SET yyyyy='NA' where yyyyy='Z4';
UPDATE xxxxx SET yyyyy='PS' where yyyyy='Z5';
UPDATE xxxxx SET yyyyy='EH' where yyyyy='Z7';
UPDATE xxxxx SET yyyyy='UM' where yyyyy='Z8';
UPDATE xxxxx SET yyyyy='SZ' where yyyyy='00';
UPDATE xxxxx SET yyyyy='YE' where yyyyy='01';
UPDATE xxxxx SET yyyyy='YU' where yyyyy='02';
UPDATE xxxxx SET yyyyy='ZM' where yyyyy='03';
UPDATE xxxxx SET yyyyy='ZW' where yyyyy='04';

'''
}

def validationtext() {
'''
SELECT DISTINCT yyyyy AS NON_CONVERTABLE_CODES FROM xxxxx WHERE yyyyy NOT IN
('AA','AC','AF','AG','AJ','AL','AM','AN','AO','AQ','AR','AS','AT','AU','AV',
 'AY','BA','BB','BC','BD','BE','BF','BG','BH','BK','BL','BM','BN','BO','BP',
 'BQ','BR','BS','BT','BU','BV','BX','BY','CA','CB','CD','CE','CF','CG','CH',
 'CI','CJ','CK','CM','CN','CO','CQ','CR','CS','CT','CU','CV','CW','CY','DA',
 'DJ','DO','DR','EC','EG','EI','EK','EN','ER','ES','ET','EU','EZ','FA','FG',
 'FI','FJ','FM','FO','FP','FQ','FR','FS','GA','GB','GE','GG','GH','GI','GJ',
 'GK','GL','GM','GO','GP','GQ','GR','GT','GV','GY','GZ','HA','HK','HM','HO',
 'HQ','HR','HU','IC','ID','IM','IN','IO','IP','IR','IS','IT','IV','IZ','JA',
 'JE','JM','JN','JO','JQ','JU','KE','KG','KN','KQ','KR','KS','KT','KU','KZ',
 'LA','LE','LG','LH','LI','LO','LQ','LS','LT','LU','LY','MA','MB','MC','MD',
 'MF','MG','MH','MI','MK','ML','MN','MO','MP','MQ','MR','MT','MU','MV','MW',
 'MX','MY','MZ','NA','NC','NE','NF','NG','NH','NI','NL','NO','NP','NR','NS',
 'NU','NZ','OC','PA','PC','PE','PK','PL','PM','PO','PP','PS','PU','QA','RE',
 'RM','RO','RP','RQ','RS','RW','SA','SB','SC','SE','SF','SG','SH','SI','SL',
 'SM','SN','SO','SP','SR','ST','SU','SV','SW','SY','SZ','TC','TD','TE','TH',
 'TI','TK','TL','TN','TO','TP','TS','TU','TV','TW','TX','TZ','UG','UK','UP',
 'US','UY','UZ','VC','VE','VI','VM','VQ','VT','WA','WE','WF','WI','WQ','WS',
 'WZ','YM','ZA','ZI');
 '''
 }