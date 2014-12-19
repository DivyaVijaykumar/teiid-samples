package org.teiid.translator.hbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.teiid.language.Command;
import org.teiid.language.Literal;
import org.teiid.language.Parameter;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;
import org.teiid.logging.MessageLevel;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.resource.adapter.hbase.HBaseConnection;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.TranslatorException;
import org.teiid.translator.hbase.phoenix.PhoenixUtils;

public abstract class HBaseExecution {
	
	protected HBaseExecutionFactory executionFactory;
	protected ExecutionContext executionContext;
	protected RuntimeMetadata metadata;
	protected HBaseConnection hbconnection;
	protected Connection connection;
	protected Command command;
	
	protected Statement statement;
	
	protected int fetchSize;
	
	public HBaseExecution(Command command, HBaseExecutionFactory executionFactory, ExecutionContext executionContext, RuntimeMetadata metadata, HBaseConnection hbconnection) {
		this.command = command ;
		this.executionFactory = executionFactory;
		this.executionContext = executionContext;
		this.metadata = metadata;
		this.hbconnection = hbconnection;
		this.connection = hbconnection.getConnection();
		this.fetchSize = executionContext.getBatchSize();
	}
		
	protected synchronized Statement getStatement() throws SQLException {
        if (statement != null) {
            statement.close();
            statement = null;
        }
        statement = connection.createStatement();
//        setSizeContraints(statement);
        return statement;
    }
	
	protected void phoenixTableMapping(List<String> list) throws HBaseExecutionException {
		Set<String> ddls = new HashSet<String>();
		for(String ddl : list){
			if(!executionFactory.getDDLCacheSet().contains(ddl)) {
				try {
					PhoenixUtils.executeUpdate(connection, ddl);
				} catch (SQLException e) {
					throw new HBaseExecutionException(HBasePlugin.Event.TEIID27001, e, HBasePlugin.Event.TEIID27012, ddl);
				}
				ddls.add(ddl);
			}
		}
		executionFactory.getDDLCacheSet().addAll(ddls);
	}
	
	protected synchronized PreparedStatement getPreparedStatement(String sql) throws SQLException {
        if (statement != null) {
            statement.close();
            statement = null;
        }
        statement = connection.prepareStatement(sql);
//        setSizeContraints(statement);
        return (PreparedStatement)statement;
    }
	
	public synchronized void close() {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			LogManager.logDetail(LogConstants.CTX_CONNECTOR, e, "Exception closing");
		}
	}
	
	public synchronized void cancel() throws TranslatorException {
        try {
            if (statement != null) {
                statement.cancel();
            }
        } catch (SQLException e) {
        	LogManager.logDetail(LogConstants.CTX_CONNECTOR, e, "Exception cancelling");
        }
    }
	
	protected void setSizeContraints(Statement statement) {
    	try {
    		executionFactory.setFetchSize(command, executionContext, statement, fetchSize);
		} catch (SQLException e) {
			if (LogManager.isMessageToBeRecorded(LogConstants.CTX_CONNECTOR, MessageLevel.DETAIL)) {
    			LogManager.logDetail(LogConstants.CTX_CONNECTOR, executionContext.getRequestId(), " could not set fetch size: ", fetchSize); //$NON-NLS-1$
    		}
		}
    }
	
	protected void bind(PreparedStatement stmt, List<?> params, List<?> batchValues)
			throws SQLException {
		for (int i = 0; i< params.size(); i++) {
		    Object paramValue = params.get(i);
		    Object value = null;
		    Class<?> paramType = null;
		    if (paramValue instanceof Literal) {
		    	Literal litParam = (Literal)paramValue;
		    	value = litParam.getValue();
		    	paramType = litParam.getType();
		    } else {
		    	Parameter param = (Parameter)paramValue;
		    	if (batchValues == null) {
		    		throw new AssertionError("Expected batchValues when using a Parameter"); 
		    	}
		    	value = batchValues.get(param.getValueIndex());
		    	paramType = param.getType();
		    }
		    this.executionFactory.bindValue(stmt, value, paramType, i+1);
		}
		if (batchValues != null) {
			stmt.addBatch();
		}
	}
	
	public void addStatementWarnings() throws SQLException {
    	SQLWarning warning = this.statement.getWarnings();
    	if (warning != null) {
    		executionContext.addWarning(warning);
			if (LogManager.isMessageToBeRecorded(LogConstants.CTX_CONNECTOR, MessageLevel.DETAIL)) {
		    	while (warning != null) {
					LogManager.logDetail(LogConstants.CTX_CONNECTOR, executionContext.getRequestId() + " Warning: ", warning); //$NON-NLS-1$
		    		warning = warning.getNextWarning();
		    	}
			}
		}
    	this.statement.clearWarnings();
    }


}
