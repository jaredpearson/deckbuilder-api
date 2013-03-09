package deckbuilder.mtg.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

public class JdbcUtils {
	
	public static void setLong(PreparedStatement stmt, int parameterIndex, Long value) throws SQLException {
		if(value == null) {
			stmt.setNull(parameterIndex, Types.BIGINT);
		} else {
			stmt.setLong(parameterIndex, value);
		}
	}

	public static Long getLong(ResultSet rst, String columnLabel) throws SQLException {
		long value = rst.getLong(columnLabel);
		if(rst.wasNull()) {
			return null;
		}
		return Long.valueOf(value);
	}
	
	public static Long getLong(ResultSet rst, int columnIndex) throws SQLException {
		long value = rst.getLong(columnIndex);
		if(rst.wasNull()) {
			return null;
		}
		return Long.valueOf(value);
	}

	public static String getString(ResultSet rst, String columnLabel) throws SQLException {
		return rst.getString(columnLabel);
	}
	
	public static String getString(ResultSet rst, int columnIndex) throws SQLException {
		return rst.getString(columnIndex);
	}
	
	public static Integer getInteger(ResultSet rst, String columnLabel) throws SQLException {
		int value = rst.getInt(columnLabel);
		if(rst.wasNull()) {
			return null;
		}
		return Integer.valueOf(value);
	}
	
	public static Integer getInteger(ResultSet rst, int columnIndex) throws SQLException {
		int value = rst.getInt(columnIndex);
		if(rst.wasNull()) {
			return null;
		}
		return Integer.valueOf(value);
	}
	
	public static Object insert(DataSource dataSource, String sql, Object... parameters) throws SQLException {
		Object id = null;
		try(Connection cnn = dataSource.getConnection()) {
			try(PreparedStatement stmt = cnn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				
				//apply the parameters
				applyParameters(stmt, parameters);
				
				//execute the update
				int affectedRows = stmt.executeUpdate();
				if(affectedRows == 0) {
					throw new SQLException("Insert failed, no rows affected.");
				}
				
				//get the generated ID
				try(ResultSet rst = stmt.getGeneratedKeys()) {
					if(rst.next()) {
						id = rst.getObject(1);
					} else {
						throw new SQLException("Inserted failed, no generated key obtained");
					}
				}
			}
		}
		return id;
	}
	
	public static void applyParameters(PreparedStatement stmt, Object... parameters) throws SQLException {
		int parameterIndex = 1;
		for(Object parameter : parameters) {
			stmt.setObject(parameterIndex++, parameter);
		}
	}
}