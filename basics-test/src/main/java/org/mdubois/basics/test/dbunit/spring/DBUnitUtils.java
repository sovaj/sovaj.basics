package org.mdubois.basics.test.dbunit.spring;

import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.db2.Db2DataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

/**
 * DBunit Utility Class that houses utility functions.
 * 
 * @author Mickael Dubois
 */
public class DBUnitUtils {
  /**
   * Returns an appropriate {@link IDataTypeFactory} instance for the passed in connection.
   * 
   * @param connection The connection for which the factory is needed.
   * @return an appropriate {@link IDataTypeFactory} instance for the passed in connection.
   */
  public static IDataTypeFactory determineDataTypeFactory(final Connection connection) {
    try {
      final DatabaseType databaseType = DatabaseUtils.databaseType(connection.getMetaData().getURL());
      switch (databaseType) {
        case DB2:
          return new Db2DataTypeFactory();
        case H2:
          return new H2DataTypeFactory();
        case HSQL:
          return new HsqldbDataTypeFactory();
        case MYSQL:
          return new MySqlDataTypeFactory();
        case ORACLE:
          return new Oracle10DataTypeFactory();
        case POSTGRESQL:
          return new PostgresqlDataTypeFactory();
        case SQL_SERVER:
          return new MsSqlDataTypeFactory();
        case UNKNOWN:
      }
    } catch (SQLException ignored) {
    }
    return new DefaultDataTypeFactory();
  }
}
