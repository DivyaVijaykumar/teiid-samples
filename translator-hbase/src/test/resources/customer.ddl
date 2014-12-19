SET NAMESPACE 'http://www.teiid.org/translator/hbase/2014' AS teiid_hbase;
            
CREATE FOREIGN TABLE Customer (
    PK string OPTIONS ("teiid_hbase:CELL" 'ROW_ID'),
    city string OPTIONS ("teiid_hbase:CELL" 'customer:city'),
    name string OPTIONS ("teiid_hbase:CELL" 'customer:name'),
    amount string OPTIONS ("teiid_hbase:CELL" 'sales:amount'),
    product string OPTIONS ("teiid_hbase:CELL" 'sales:product'),
    CONSTRAINT PK0 PRIMARY KEY(PK)
) OPTIONS("teiid_hbase:TABLE" 'Customer', "UPDATABLE" 'TRUE');

CREATE FOREIGN TABLE TypesTest (
    PK string OPTIONS ("teiid_hbase:CELL" 'ROW_ID'),
    column1 varchar OPTIONS ("teiid_hbase:CELL" 'f:q1'),
    column2 varbinary OPTIONS ("teiid_hbase:CELL" 'f:q2'),
    column3 char OPTIONS ("teiid_hbase:CELL" 'f:q3'),
    column4 boolean OPTIONS ("teiid_hbase:CELL" 'f:q4'),
    column5 byte OPTIONS ("teiid_hbase:CELL" 'f:q5'),
    column6 tinyint OPTIONS ("teiid_hbase:CELL" 'f:q6'),
    column7 short OPTIONS ("teiid_hbase:CELL" 'f:q7'),
    column8 smallint OPTIONS ("teiid_hbase:CELL" 'f:q8'),
    column9 integer OPTIONS ("teiid_hbase:CELL" 'f:q9'),
    column10 serial OPTIONS ("teiid_hbase:CELL" 'f:q10'),
    column11 long OPTIONS ("teiid_hbase:CELL" 'f:q11'),
    column12 bigint OPTIONS ("teiid_hbase:CELL" 'f:q12'),
    column13 float OPTIONS ("teiid_hbase:CELL" 'f:q13'),
    column14 real OPTIONS ("teiid_hbase:CELL" 'f:q14'),
    column15 double OPTIONS ("teiid_hbase:CELL" 'f:q15'),
    column16 bigdecimal OPTIONS ("teiid_hbase:CELL" 'f:q16'),
    column17 decimal OPTIONS ("teiid_hbase:CELL" 'f:q17'),
    column18 date OPTIONS ("teiid_hbase:CELL" 'f:q18'),
    column10 time OPTIONS ("teiid_hbase:CELL" 'f:q29'),
    column20 timestamp OPTIONS ("teiid_hbase:CELL" 'f:q20'),
    CONSTRAINT PK0 PRIMARY KEY(PK)
) OPTIONS("teiid_hbase:TABLE" 'TypesTest', "UPDATABLE" 'TRUE');