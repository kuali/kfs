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

CREATE TABLE kuluser_maint_pk_support_t
    (user_id                        VARCHAR2(30) NOT NULL,
    password_text                  VARCHAR2(200)
  ,
  CONSTRAINT USER_MAINT_PK_SUPPORT_TP1
  PRIMARY KEY (user_id))
  ORGANIZATION INDEX
   PCTTHRESHOLD 50 
/

INSERT INTO kuluser_maint_pk_support_t VALUES( 'TEMP_USER_PW', 'Test123' )
/
COMMIT
/


CREATE OR REPLACE
PACKAGE kuluser_maint_pk
  IS
    FUNCTION check_schema_name( UserID IN VARCHAR ) RETURN BOOLEAN;
    FUNCTION create_user( UserID IN VARCHAR, UserPassword IN VARCHAR DEFAULT NULL ) RETURN INTEGER;
    FUNCTION recreate_user( UserID IN VARCHAR ) RETURN INTEGER;
    FUNCTION create_app_user( UserID IN VARCHAR, UserPassword IN VARCHAR DEFAULT NULL ) RETURN INTEGER;
    FUNCTION recreate_app_user( UserID IN VARCHAR ) RETURN INTEGER;
    FUNCTION drop_user( UserID IN VARCHAR ) RETURN INTEGER;
    FUNCTION kill_user_sessions( UserID IN VARCHAR ) RETURN INTEGER;
    FUNCTION use_temp_password( UserID IN VARCHAR ) RETURN INTEGER;
    FUNCTION restore_normal_password( UserID IN VARCHAR ) RETURN INTEGER;
END; -- Package spec
/

GRANT EXECUTE ON kuluser_maint_pk TO kuluser_admin
/

CREATE OR REPLACE
PACKAGE BODY KULUSER_MAINT_PK
IS
    WaitIntervalSeconds CONSTANT NUMBER  := 5;
    MaxWaitSeconds      CONSTANT NUMBER  := 60;
    ProtectedSchemaList CONSTANT VARCHAR2(4000) := '~SYS~SYSTEM~DBSNMP~OUTLN~DIP~TSMSYS~WMSYS~KULDBA~KULUSERMAINT~KULUSER_ADMIN~KULCFGUSR~';
    AllowedSchemaPrefixList CONSTANT VARCHAR2(4000) := 'KUL,RICE,KR,KRA,KEW,KEM,KS,KFS,KMM,KC';

	-- FOR NOW THIS NEEDS TO BE CHANGED TO TEMP02 FOR THE KUALI FOUNDATION DAILY UPDATE PROCESS
    TemporaryTablespace CONSTANT VARCHAR2(30) := 'TEMP';
    PrimaryTablespace   CONSTANT VARCHAR2(30) := 'USERS';
	-- FOR NOW THIS NEEDS TO BE CHANGED TO TRUE FOR THE KUALI FOUNDATION DAILY UPDATE PROCESS
    UseUserNameForTablespace CONSTANT BOOLEAN := FALSE;
    DebugMode BOOLEAN := TRUE;

    FUNCTION check_schema_name( UserID IN VARCHAR ) RETURN BOOLEAN
    IS
        PrefixList VARCHAR2(4000) := AllowedSchemaPrefixList;
        CurrPrefix VARCHAR2(30);
        allowed    BOOLEAN := FALSE;
        nextPos    INTEGER;
        Temp       NUMBER;
    BEGIN
        IF ProtectedSchemaList LIKE '%~'||UPPER( UserID )||'~%' THEN
            dbms_output.put_line('Schema is protected from modification.');
            RETURN FALSE;
        END IF;
        LOOP
            nextPos := INSTR( PrefixList, ',' );
            IF nextPos > 0 THEN
                CurrPrefix := SUBSTR( PrefixList, 1, nextPos - 1 );
                PrefixList := SUBSTR( PrefixList, nextPos + 1 );
            ELSE 
                CurrPrefix := PrefixList;
            END IF;
            IF UPPER( UserID ) LIKE CurrPrefix||'%' THEN
                allowed := TRUE;
            END IF;
            EXIT WHEN allowed = TRUE OR nextPos = 0;
        END LOOP;
        IF NOT allowed THEN
            dbms_output.put_line('Schema does not have a valid prefix for modification.');
        ELSE
            IF UseUserNameForTablespace THEN
                SELECT COUNT(*) INTO Temp 
                    FROM dba_tablespaces 
                    WHERE tablespace_name = UPPER( UserID )
                      AND status = 'ONLINE';
                IF Temp = 0 THEN
                    dbms_output.put_line('UseUserNameForTablespace is set and tablespace '||UPPER(UserID)||' does not exist');
                    allowed := FALSE;
                END IF;
            END IF;
        END IF;
        RETURN allowed;
    END;
    

    FUNCTION kill_user_sessions( UserID IN VARCHAR ) RETURN INTEGER
    IS
        CURSOR sess_cur( UserID VARCHAR ) IS 
              select sid, serial# AS serial
              from v$session
              where username = UserID
                AND status != 'KILLED';      
        TotalWaitTime NUMBER := 0;
        RemainingSessionCount NUMBER;
    BEGIN
        IF NOT check_schema_name(UserID) THEN
            RETURN 8;
        END IF;
 	    SELECT COUNT(*)
 	      INTO RemainingSessionCount
 	      FROM v$session
 	      WHERE username = UPPER( UserID )
            AND status != 'KILLED';
        dbms_output.put_line('Initial Session Count:'||RemainingSessionCount);            
        IF RemainingSessionCount > 0 THEN
            FOR rec IN sess_cur( UPPER( UserID ) ) LOOP
                dbms_output.put_line ('Killing session --> ' || rec.sid || ',' || rec.serial);
                EXECUTE IMMEDIATE 'ALTER SYSTEM KILL SESSION '''||rec.sid||','||rec.serial||'''';
            END LOOP ;
        
            -- wait, to allow all sessions to abort
            LOOP
        		dbms_output.put_line('Waiting for '||WaitIntervalSeconds||' seconds for kill to complete.' );
            	dbms_lock.sleep(WaitIntervalSeconds);
                -- check if any sessions remain
         	    SELECT COUNT(*)
         	      INTO RemainingSessionCount
         	      FROM v$session
         	      WHERE username = UPPER( UserID )
                    AND status != 'KILLED';
                TotalWaitTime := TotalWaitTime + WaitIntervalSeconds;
                EXIT WHEN RemainingSessionCount = 0 OR TotalWaitTime >= MaxWaitSeconds;
         	END LOOP;
         	-- Wait one more interval for good measure
            dbms_lock.sleep(WaitIntervalSeconds);        
        END IF;
 	    SELECT COUNT(*)
 	      INTO RemainingSessionCount
 	      FROM v$session
 	      WHERE username = UPPER( UserID )
            AND status != 'KILLED';
        dbms_output.put_line('Final Session Count:'||RemainingSessionCount);            
        IF RemainingSessionCount = 0 THEN
            RETURN 0;
        ELSE
            RETURN 4; -- not all sessions could be killed
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            dbms_output.put_line('kill sessions - Exception:'||SUBSTR(SQLERRM,1,200));
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;    
    
    
    FUNCTION create_user( UserID IN VARCHAR, UserPassword IN VARCHAR DEFAULT NULL ) RETURN INTEGER
    IS
        UserPW VARCHAR2(30) := UserPassword;
        PasswordHash BOOLEAN := FALSE;
        CreateUserDDL VARCHAR2(4000);
    BEGIN
        IF UserPW IS NULL THEN
            dbms_output.put_line('Creating new User with stored password: '||UserID);            
            BEGIN
                SELECT password_text
                    INTO UserPW
                    FROM kuluser_maint_pk_support_t
                    WHERE user_id = UPPER( UserID );
                PasswordHash := TRUE;
            EXCEPTION
                WHEN no_data_found THEN
                    raise_application_error( -20000, 'No Password Provided and password not stored in support table.' );
            END;
        ELSE
            dbms_output.put_line('Creating new User with given password: '||UserID);                    
        END IF;
        -- create user
        CreateUserDDL := 'CREATE USER '||UserID||' IDENTIFIED BY ';
        IF PasswordHash THEN
            CreateUserDDL := CreateUserDDL||'VALUES '''||UserPW||''' ';
        ELSE
            CreateUserDDL := CreateUserDDL||UserPW||' ';
        END IF;
        IF UseUserNameForTablespace THEN
            CreateUserDDL := CreateUserDDL||'DEFAULT TABLESPACE '||UserID;
        ELSE
            CreateUserDDL := CreateUserDDL||'DEFAULT TABLESPACE '||PrimaryTablespace;
        END IF;
        CreateUserDDL := CreateUserDDL||' TEMPORARY TABLESPACE '||TemporaryTablespace;
        IF UseUserNameForTablespace THEN
            CreateUserDDL := CreateUserDDL||' QUOTA UNLIMITED ON '||UserID;
        ELSE
            CreateUserDDL := CreateUserDDL||' QUOTA UNLIMITED ON '||PrimaryTablespace;
        END IF;
        EXECUTE IMMEDIATE CreateUserDDL;
        -- assign roles
        EXECUTE IMMEDIATE 'GRANT kfs_role TO '||UserID;
        -- set default roles
        EXECUTE IMMEDIATE 'ALTER USER '||UserID||' DEFAULT ROLE ALL';
        IF PasswordHash THEN
            DELETE FROM kuluser_maint_pk_support_t
                WHERE user_id = UPPER(UserID);
            COMMIT;
        END IF;            
        RETURN 0;
    EXCEPTION
        WHEN OTHERS THEN
            dbms_output.put_line('create User - Exception:'||SUBSTR(SQLERRM,1,200));
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;

 FUNCTION create_app_user( UserID IN VARCHAR, UserPassword IN VARCHAR DEFAULT NULL ) RETURN INTEGER
    IS
        UserPW VARCHAR2(30) := UserPassword;
        PasswordHash BOOLEAN := FALSE;
        CreateUserDDL VARCHAR2(4000);
    BEGIN
        IF UserPW IS NULL THEN
            BEGIN
                SELECT password_text
                    INTO UserPW
                    FROM kuluser_maint_pk_support_t
                    WHERE user_id = UPPER( UserID );
                PasswordHash := TRUE;
            EXCEPTION
                WHEN no_data_found THEN
                    raise_application_error( -20000, 'No Password Provided and password not stored in support table.' );
            END;
        END IF;
        -- create user
        CreateUserDDL := 'CREATE USER '||UserID||' IDENTIFIED BY ';
        IF PasswordHash THEN
            CreateUserDDL := CreateUserDDL||'VALUES '''||UserPW||''' ';
        ELSE
            CreateUserDDL := CreateUserDDL||UserPW||' ';
        END IF;
        EXECUTE IMMEDIATE CreateUserDDL;
	        -- assign roles
	        EXECUTE IMMEDIATE 'GRANT kfs_user_role TO '||UserID;
	        -- set default roles
	        EXECUTE IMMEDIATE 'ALTER USER '||UserID||' DEFAULT ROLE ALL';
        IF PasswordHash THEN
            DELETE FROM kuluser_maint_pk_support_t
                WHERE user_id = UPPER(UserID);
            COMMIT;
        END IF;
        RETURN 0;
    EXCEPTION
        WHEN OTHERS THEN
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;
    PROCEDURE save_user_password( UserID IN VARCHAR )
    IS
        UserPassword VARCHAR(30);
    BEGIN
        SELECT password
            INTO UserPassword  
            FROM dba_users
            WHERE username = UPPER(UserID);
        UPDATE kuluser_maint_pk_support_t
            SET password_text = UserPassword
            WHERE user_id = UPPER(UserID);
        IF SQL%ROWCOUNT = 0 THEN
            INSERT INTO kuluser_maint_pk_support_t ( user_id, password_text )
                VALUES ( UPPER(UserID), UserPassword );
        END IF;
        COMMIT;        
    END;
    

    FUNCTION drop_user( UserID IN VARCHAR ) RETURN INTEGER
    IS
        Result INTEGER;
        Temp NUMBER;
    BEGIN        
        SELECT COUNT(*)
            INTO Temp
            FROM dba_users
            WHERE username = UPPER(UserID);
        IF Temp > 0 THEN
            dbms_output.put_line('Dropping User:'||UserID);            
            -- get the user's hashed password and store to the support table
            -- change password to prevent additional logins
            Result := use_temp_password(UserID);
            IF Result != 0 THEN
                RETURN Result;
            END IF;
            -- ensure all sessions are gone
            Result := kill_user_sessions(UserID);
            IF Result != 0 THEN
                RETURN Result;
            END IF;
            -- get the user's hashed password and store to the support table
            save_user_password(UserID);
            -- drop the user
            EXECUTE IMMEDIATE 'DROP USER '||UserID||' CASCADE';
        END IF;
        RETURN 0;
    EXCEPTION
        WHEN OTHERS THEN
            dbms_output.put_line('drop User - Exception:'||SUBSTR(SQLERRM,1,200));
            Temp := restore_normal_password(UserID);
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;

    FUNCTION recreate_user( UserID IN VARCHAR ) RETURN INTEGER
    IS
        Result INTEGER;
    BEGIN
        Result := drop_user(UserID);
        IF Result != 0 THEN
            RETURN Result;
        END IF;
        Result := create_user(UserID);
        IF Result != 0 THEN
            RETURN Result;
        END IF;
        RETURN 0;
    EXCEPTION
        WHEN OTHERS THEN
            dbms_output.put_line('Recreate User - Exception:'||SUBSTR(SQLERRM,1,200));
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;

    FUNCTION recreate_app_user( UserID IN VARCHAR ) RETURN INTEGER
    IS
        Result INTEGER;
    BEGIN
        Result := drop_user(UserID);
        IF Result != 0 THEN
            RETURN Result;
        END IF;
        Result := create_app_user(UserID);
        IF Result != 0 THEN
            RETURN Result;
        END IF;
        RETURN 0;
    EXCEPTION
        WHEN OTHERS THEN
            dbms_output.put_line('Recreate User - Exception:'||SUBSTR(SQLERRM,1,200));
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;

    FUNCTION use_temp_password( UserID IN VARCHAR ) RETURN INTEGER
    IS
        TempPassword VARCHAR(30);
    BEGIN
        dbms_output.put_line('Switching to Temp password:'||UserID);            
        -- save password
        save_user_password(UserID);
        -- get temp password
        SELECT password_text
            INTO TempPassword
            FROM kuluser_maint_pk_support_t
            WHERE user_id = 'TEMP_USER_PW';
        -- set password
        EXECUTE IMMEDIATE 'ALTER USER '||UserID||' IDENTIFIED BY '||TempPassword;
        RETURN 0;
    EXCEPTION
        WHEN OTHERS THEN
            dbms_output.put_line('use temp password - Exception:'||SUBSTR(SQLERRM,1,200));
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;
    
    
    FUNCTION restore_normal_password( UserID IN VARCHAR ) RETURN INTEGER
    IS
        TempPassword VARCHAR(30);
    BEGIN
        dbms_output.put_line('Restoring Normal password:'||UserID);            
        -- get password
        SELECT password_text
            INTO TempPassword
            FROM kuluser_maint_pk_support_t
            WHERE user_id = UPPER(UserID);
        -- set password
        EXECUTE IMMEDIATE 'ALTER USER '||UserID||' IDENTIFIED BY VALUES '''||TempPassword||'''';
        DELETE FROM kuluser_maint_pk_support_t
            WHERE user_id = UPPER(UserID);
        COMMIT;
        RETURN 0;
    EXCEPTION
        WHEN OTHERS THEN
            dbms_output.put_line('restore password - Exception:'||SUBSTR(SQLERRM,1,200));
            IF DebugMode THEN
                RAISE;
            ELSE
                RETURN 8;
            END IF;
    END;
    
END;
/
