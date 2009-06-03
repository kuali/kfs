insert into kr_county_t values ('59', 'CA', 'US', sys_guid(), 1, 'Orange', 'Y');
insert into kr_county_t values ('65', 'CA', 'US', sys_guid(), 1, 'Riverside', 'Y');
insert into kr_county_t values ('111', 'CA', 'US', sys_guid(), 1, 'Ventura', 'Y');
insert into kr_county_t values ('83', 'CA', 'US', sys_guid(), 1, 'Santa Barbara', 'Y');
insert into kr_county_t values ('71', 'CA', 'US', sys_guid(), 1, 'San Bernadino', 'Y');
insert into fs_tax_region_t values ('15', sys_guid(), 1, '0.25% Postal District','POST','Y','BL','1023295','9015','Y');
insert into FS_TAX_REGION_RATE_T values ('15',to_date('7/1/2008','mm/dd/yyyy'),sys_guid(), 1,'0.0025');
insert into fs_tax_postal_cd_t values ('US', '92627','1', sys_guid(), 1,'Y'); 
insert into fs_tax_postal_cd_t values ('US', '93033','1', sys_guid(), 1,'Y'); 
insert into fs_tax_postal_cd_t values ('US', '91710','15', sys_guid(), 1,'Y'); 
insert into fs_tax_county_t values ('US', '65', 'CA','05PERCNTY', sys_guid(), 1,'Y'); 
insert into fs_tax_county_t values ('US', '83', 'CA','05PERCNTY', sys_guid(), 1,'Y'); 
insert into fs_tax_county_t values ('US', '71', 'CA','05PERCNTY', sys_guid(), 1,'Y'); 
update fs_tax_region_t
set tax_region_nm = 'State Tax 8.25%'
where tax_region_cd = '2';
update fs_tax_region_rate_t set tax_rate = 0.01, 
tax_rate_effective_dt = to_date('7/1/2008','mm/dd/yyyy')
where tax_region_cd = '4';
update fs_tax_region_rate_t set tax_rate = 0.0825
where tax_region_cd = '2';
commit;