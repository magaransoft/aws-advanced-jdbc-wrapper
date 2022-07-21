/*
 * AWS JDBC Proxy Driver
 * Copyright Amazon.com Inc. or affiliates.
 * See the LICENSE file in the project root for more information.
 */

package software.aws.rds.jdbc.proxydriver;

import static software.aws.rds.jdbc.proxydriver.ConnectionPropertyNames.DATABASE_PROPERTY_NAME;
import static software.aws.rds.jdbc.proxydriver.ConnectionPropertyNames.PASSWORD_PROPERTY_NAME;
import static software.aws.rds.jdbc.proxydriver.ConnectionPropertyNames.USER_PROPERTY_NAME;
import static software.aws.rds.jdbc.proxydriver.util.StringUtils.isNullOrEmpty;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class is a basic implementation of {@link ConnectionProvider} interface. It creates and returns a connection
 * provided by a target driver or a data source.
 */
public class DriverConnectionProvider implements ConnectionProvider {

  private final java.sql.Driver driver;
  private final String userPropertyName;
  private final String passwordPropertyName;

  public DriverConnectionProvider(final java.sql.Driver driver) {
    this(driver, null, null);
  }

  public DriverConnectionProvider(final java.sql.Driver driver, String userPropertyName, String passwordPropertyName) {
    this.driver = driver;
    this.userPropertyName = userPropertyName;
    this.passwordPropertyName = passwordPropertyName;
  }

  /**
   * Called once per connection that needs to be created.
   *
   * @param protocol The connection protocol (example "jdbc:mysql://")
   * @param hostSpec The HostSpec containing the host-port information for the host to connect to
   * @param props    The Properties to use for the connection
   * @return {@link Connection} resulting from the given connection information
   * @throws SQLException if an error occurs
   */
  @Override
  public Connection connect(
      final @NonNull String protocol,
      final @NonNull HostSpec hostSpec,
      final @NonNull Properties props)
      throws SQLException {

    final String databaseName =
        props.getProperty(DATABASE_PROPERTY_NAME) != null ? props.getProperty(DATABASE_PROPERTY_NAME) : "";
    final StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(protocol).append(hostSpec.getUrl()).append(databaseName);

    if (!isNullOrEmpty(this.userPropertyName) && !isNullOrEmpty(props.getProperty(USER_PROPERTY_NAME))) {
      props.setProperty(this.userPropertyName, props.getProperty(USER_PROPERTY_NAME));
    }

    if (!isNullOrEmpty(this.passwordPropertyName) && !isNullOrEmpty(props.getProperty(PASSWORD_PROPERTY_NAME))) {
      props.setProperty(this.passwordPropertyName, props.getProperty(PASSWORD_PROPERTY_NAME));
    }

    return this.driver.connect(urlBuilder.toString(), props);
  }

  /**
   * Called once per connection that needs to be created.
   *
   * @param url   The connection URL
   * @param props The Properties to use for the connection
   * @return {@link Connection} resulting from the given connection information
   * @throws SQLException if an error occurs
   */
  public Connection connect(@NonNull String url, @NonNull Properties props) throws SQLException {

    return this.driver.connect(url, props);
  }
}