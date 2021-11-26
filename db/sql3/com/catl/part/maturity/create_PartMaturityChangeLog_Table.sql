set echo on
REM Creating table PartMaturityChangeLog for com.catl.part.maturity.PartMaturityChangeLog
set echo off
CREATE TABLE PartMaturityChangeLog (
   newMaturity   VARCHAR2(750),
   oldMaturity   VARCHAR2(750),
   operatorIsNull   NUMBER(1),
   classnamekeyA4   VARCHAR2(600),
   idA3A4   NUMBER,
   classnamekeyB4   VARCHAR2(600),
   idA3B4   NUMBER,
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
   version   VARCHAR2(750),
 CONSTRAINT PK_PartMaturityChangeLog PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE PartMaturityChangeLog IS 'Table PartMaturityChangeLog created for com.catl.part.maturity.PartMaturityChangeLog'
/
REM @//com/catl/part/maturity/PartMaturityChangeLog_UserAdditions
