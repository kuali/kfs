--
-- The Kuali Financial System, a comprehensive financial management system for higher education.
-- 
-- Copyright 2005-2014 The Kuali Foundation
-- 
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

SELECT * FROM END_ACRL_MTHD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_ACRL_MTHD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_CAE_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_CAE_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_CLS_CD_TYP_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_CLS_CD_TYP_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_DONR_LBL_SEL_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_DONR_LBL_SEL_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_ETRAN_TYP_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_ETRAN_TYP_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_FEE_BAL_TYP_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_FEE_BAL_TYP_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_FEE_BASE_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_FEE_BASE_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_FEE_RT_DEF_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_FEE_RT_DEF_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_FEE_TYP_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_FEE_TYP_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_FREQ_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_FREQ_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/

SELECT * FROM END_IP_IND_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_IP_IND_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_PMT_TYP_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_PMT_TYP_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_SEC_VLTN_MTHD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_SEC_VLTN_MTHD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_TRAN_TYP_CD_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_TRAN_TYP_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_TRAN_SUB_TYP_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_TRAN_SUB_TYP_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_TRAN_SRC_TYP_T
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_TRAN_SRC_TYP_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_SEC_RPT_GRP_T WHERE SEC_RPT_GRP = 'CSHEQ' 
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_SEC_RPT_GRP_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_AGRMNT_SPCL_INSTRC_CD_T WHERE AGRMNT_SPCL_INSTRC_CD = '0'
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_AGRMNT_SPCL_INSTRC_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_AGRMNT_STAT_CD_T WHERE AGRMNT_STAT_CD IN ( 'COMP', 'NONE', 'PEND' )
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_AGRMNT_STAT_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_TRAN_RESTR_CD_T WHERE TRAN_RESTR_CD IN ( 'NDISB', 'NONE', 'NTRAN' )
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_TRAN_RESTR_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
SELECT * FROM END_TYP_RESTR_CD_T WHERE TYP_RESTR_CD IN ( 'U', 'NA', 'P', 'T' )
/
.saveLastResult 'Format=CSV,
	File="/Users/jonathan/dev/projects/kfs/work/db/upgrades/4.0/db/02_data/END_TYP_RESTR_CD_T.csv",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	QuoteIdentifier="\"",
	Delimiter=",",
	Encoding=UTF-8,
	IncludeHeader=true,
	IncludeRowCount=false,
	IncludeNullText=false' 
/
