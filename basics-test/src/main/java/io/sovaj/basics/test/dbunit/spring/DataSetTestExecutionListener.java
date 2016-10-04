package io.sovaj.basics.test.dbunit.spring;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.springframework.util.ClassUtils.getPackageName;
import static org.springframework.util.ClassUtils.getQualifiedName;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Constants;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.TestContext;

import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Spring test framework TestExecutionListener which looks for the DataSet
 * annotation and if found, attempts to load a data set (test fixture) before
 * the test is run.
 * 
 * @author Mickael Dubois
 */
public class DataSetTestExecutionListener extends AbstractTestExecutionListener{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<Method, DatasetConfiguration> configurationCache = Collections
                    .synchronizedMap(new IdentityHashMap<Method, DatasetConfiguration>());

    private final static Constants databaseOperations = new Constants(DatabaseOperation.class);

    static {
        ClasspathURLHandler.register();
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        // Determine if a dataset should be loaded, from where, and extract any
        // special configuration.
        DatasetConfiguration configuration =
                        determineConfiguration(testContext.getTestClass(), testContext.getTestMethod());

        if (configuration == null || configuration.getLocations().length == 0) {
            return;
        }

        configurationCache.put(testContext.getTestMethod(), configuration);

        // Find a single, unambiguous data source.
        DataSource dataSource = lookupDataSource(testContext, configuration.getDatasourceName());

        // Fetch a connection from the data source, using an existing one if
        // we're already participating in a
        // transaction.
        Connection connection = DataSourceUtils.getConnection(dataSource);
        configuration.setConnectionTransactional(DataSourceUtils.isConnectionTransactional(connection, dataSource));

        // Load the data set.
        try {
            loadData(configuration, connection);
        } catch (DataSetException e) {
            if (!configuration.isConnectionTransactional()) {
                connection.close();
            }
            throw e;
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        DatasetConfiguration configuration = configurationCache.get(testContext.getTestMethod());

        if (configurationCache.remove(testContext.getTestMethod()) == null) {
            return;
        }

        if (log.isInfoEnabled()) {
            log.info(format("Tearing down dataset using operation '%s', %s.", configuration.getTeardownOperation(),
                            configuration.isConnectionTransactional() ? "leaving database connection open"
                                            : "closing database connection"));
        }

        configuration.getDatabaseTester().onTearDown();

        if (!configuration.isConnectionTransactional()) {
            try {
                configuration.getDatabaseTester().getConnection().close();
            } catch (Exception e) {
                // do nothing: this connection is associated with an active
                // transaction and we assume the framework will
                // close the connection.
            }
        }
    }

    protected void configureReplacementDataSet(ReplacementDataSet dataSet) {
        // Subclasses can add additional replacements.
    }

    protected void configureDatabaseConfig(DatabaseConfig config) {
        // Subclasses can further configure dbunit.
    }

    DatasetConfiguration determineConfiguration(Class< ? > testClass, Method testMethod) {
        DataSet annotation = (DataSet ) findAnnotation(testMethod, testClass, DataSet.class);

        if (annotation == null) {
            return null;
        }

        DatasetConfiguration configuration = new DatasetConfiguration();

        // Dataset source value.
        String[] locations = annotation.value();

        // if there is no location specified
        if (locations.length == 0) {
            // then revert to default location
            locations = new String[]{"classpath:/" + getQualifiedName(testClass).replace('.', '/') + ".xml" };
        } else {
            for (int i = 0; i < locations.length; i++) {
                String location = locations[i];
                if (location != null) {
                    if (!location.contains(":") && !location.contains("/")) {
                        location = "classpath:/" + getPackageName(testClass).replace('.', '/') + "/" + location;
                    }
                }
                locations[i] = location;
            }
        }

        configuration.setLocations(locations);

        // datasource name
        if (isNotBlank(annotation.datasourceName())) {
            configuration.setDatasourceName(annotation.datasourceName());
        }

        // Setup and teardown operations.
        if (isNotBlank(annotation.setupOperation())) {
            configuration.setSetupOperation(annotation.setupOperation());
        }

        if (isNotBlank(annotation.teardownOperation())) {
            configuration.setTeardownOperation(annotation.teardownOperation());
        }

        return configuration;
    }

    /**
     * Looks for a single, unambiguous datasource in the test's application
     * context.
     * 
     * @param testContext the current test context
     * @param datasourceName
     * @return the only datasource in the current application context
     */
    DataSource lookupDataSource(TestContext testContext, String datasourceName) {
        String[] dsNames = testContext.getApplicationContext().getBeanNamesForType(DataSource.class);

        if (dsNames.length == 0) {
            final String s = "There should be atleast one datasource in the applicaiton context.";
            log.error(s);
            throw new IllegalStateException(s);
        }

        if (dsNames.length > 1 && isBlank(datasourceName)) {
            final String s =
                            "There are more than one datasources in the applicaiton context. "
                                            + "Please specify the datasourceName in DataSet annotation to specify which datasource to use.";
            log.error(s);
            throw new IllegalStateException(s);
        }

        // take the first datasource name.
        String dsName = dsNames[0];
        if (dsNames.length > 1) {
            // if there are more that one datasources use the one specified in
            // the DataSet annotation.
            dsName = datasourceName;
        }
        return (DataSource ) testContext.getApplicationContext().getBean(dsName);
    }

    /**
     * Given the locations of the dataset and the datasource.
     * 
     * @param configuration the spring-style resource locations of the dataset
     *            to be loaded
     * @param connection the connection to use for loading the dataset
     * @throws Exception if an error occurs when loading the dataset
     */
    void loadData(DatasetConfiguration configuration, Connection connection) throws Exception {
        log.info("Loading dataset from locations '{}' using operation '{}'.",
                        Arrays.toString(configuration.getLocations()), configuration.getSetupOperation());

        final List<IDataSet> dataSets = new ArrayList<>();

        for (final String location : configuration.getLocations()) {
            final InputStream is = new DefaultResourceLoader().getResource(location).getInputStream();
            IDataSet ds = null;

            // determine the dataset to load from location
            if (location.endsWith("xml")) {
                ds = new FlatXmlDataSetBuilder().setColumnSensing(true).build(is);
            } else if (location.endsWith("xls")) {
                ds = new XlsDataSet(is);
            } else {
                throw new IllegalStateException("Only XLS and XML DataSet formats are supported");
            }

            final ReplacementDataSet dataSet = new ReplacementDataSet(ds);
            dataSet.addReplacementObject("[NULL]", null);
            configureReplacementDataSet(dataSet);
            dataSets.add(dataSet);
        }

        IDatabaseTester tester = new DefaultDatabaseTester(new DatabaseConnection(connection) {
            @Override
            public void close() throws SQLException {
                // do nothing: this will be closed later if necessary.
            }
        });

        tester.getConnection()
                        .getConfig()
                        .setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                                        DBUnitUtils.determineDataTypeFactory(connection));
        configureDatabaseConfig(tester.getConnection().getConfig());

        configuration.setDatabaseTester(tester);

        // create a composite dataset
        final CompositeDataSet dataSet = new CompositeDataSet(dataSets.toArray(new IDataSet[dataSets.size()]));

        tester.setDataSet(dataSet);
        tester.setSetUpOperation((DatabaseOperation ) databaseOperations.asObject(configuration.getSetupOperation()));
        tester.setTearDownOperation((DatabaseOperation ) databaseOperations.asObject(configuration
                        .getTeardownOperation()));
        tester.onSetup();
    }

    Annotation findAnnotation(Method method, Class< ? > testClass, Class< ? extends Annotation> annotationType) {
        Annotation annotation = AnnotationUtils.findAnnotation(method, annotationType);
        return annotation == null ? AnnotationUtils.findAnnotation(testClass, annotationType) : annotation;
    }

    static class DatasetConfiguration {
        private String[] locations;

        private String setupOperation;

        private String teardownOperation;

        private String datasourceName;

        private IDatabaseTester databaseTester;

        private boolean connectionTransactional;

        public void setDatasourceName(String datasourceName) {
            this.datasourceName = datasourceName;
        }

        public String getDatasourceName() {
            return datasourceName;
        }

        public String[] getLocations() {
            return locations;
        }

        public void setLocations(String[] locations) {
            this.locations = locations;
        }

        public String getSetupOperation() {
            return setupOperation;
        }

        public void setSetupOperation(String setupOperation) {
            this.setupOperation = setupOperation;
        }

        public String getTeardownOperation() {
            return teardownOperation;
        }

        public void setTeardownOperation(String teardownOperation) {
            this.teardownOperation = teardownOperation;
        }

        public IDatabaseTester getDatabaseTester() {
            return databaseTester;
        }

        public void setDatabaseTester(IDatabaseTester databaseTester) {
            this.databaseTester = databaseTester;
        }

        public boolean isConnectionTransactional() {
            return connectionTransactional;
        }

        public void setConnectionTransactional(boolean connectionTransactional) {
            this.connectionTransactional = connectionTransactional;
        }
    }
}