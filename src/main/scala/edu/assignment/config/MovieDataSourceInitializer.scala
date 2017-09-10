package edu.assignment.config

import org.flywaydb.core.Flyway

/**
  * Initialize data source viz. Movie table if it doesn't exist.
  */
trait MovieDataSourceInitializer extends MovieConfig {

  private val flyway = new Flyway()
  flyway.setDataSource(databaseUrl, databaseUser, databasePassword)

  def migrate(): Int = {
    flyway.migrate()
  }

  def reloadSchema(): Int = {
    flyway.clean()
    flyway.migrate()
  }

}