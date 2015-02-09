/**
 * Copyright (C) ${project.inceptionYear} ConnId (connid-dev@googlegroups.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tirasa.connid.bundles.soap.wssample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DefaultContentLoader implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContentLoader.class);

    private final String DBSCHEMA = "/schema.sql";

    public static DataSource localDataSource = null;

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final WebApplicationContext springContext =
                WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());

        if (springContext == null) {
            LOG.error("Invalid Spring context");
            return;
        }

        final DataSource dataSource = springContext.getBean(DataSource.class);
        localDataSource = dataSource;

        final DefaultDataTypeFactory dbUnitDataTypeFactory =
                (DefaultDataTypeFactory) springContext.getBean("dbUnitDataTypeFactory");

        final Connection conn = DataSourceUtils.getConnection(dataSource);

        // create schema
        final StringBuilder statement = new StringBuilder();

        final InputStream dbschema = DefaultContentLoader.class.getResourceAsStream(DBSCHEMA);

        final BufferedReader buff = new BufferedReader(new InputStreamReader(dbschema));

        String line = null;
        try {
            while ((line = buff.readLine()) != null) {
                if (!line.isEmpty() && !line.startsWith("--")) {
                    statement.append(line);
                }
            }
        } catch (IOException e) {
            LOG.error("Error reading file " + DBSCHEMA, e);
            return;
        }

        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute(statement.toString());
        } catch (SQLException e) {
            LOG.error("Error creating schema:\n" + statement.toString(), e);
            return;
        } finally {
            try {
                st.close();
            } catch (Throwable t) {
                // ignore exception
            }
        }

        try {
            IDatabaseConnection dbUnitConn = new DatabaseConnection(conn);

            final DatabaseConfig config = dbUnitConn.getConfig();
            config.setProperty("http://www.dbunit.org/properties/datatypeFactory", dbUnitDataTypeFactory);

            boolean existingData = false;
            final IDataSet existingDataSet = dbUnitConn.createDataSet();
            for (final ITableIterator itor = existingDataSet.iterator(); itor.next() && !existingData;) {
                existingData = (itor.getTable().getRowCount() > 0);
            }

            final FlatXmlDataSetBuilder dataSetBuilder = new FlatXmlDataSetBuilder();
            dataSetBuilder.setColumnSensing(true);
            final IDataSet dataSet = dataSetBuilder.build(getClass().getResourceAsStream("/content.xml"));
            DatabaseOperation.REFRESH.execute(dbUnitConn, dataSet);
        } catch (Throwable t) {
            LOG.error("Error loding default content", t);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                LOG.info("Deregistering JDBC driver: {}", driver);
            } catch (SQLException e) {
                LOG.error("Error deregistering JDBC driver {}", driver, e);
            }
        }
    }
}
