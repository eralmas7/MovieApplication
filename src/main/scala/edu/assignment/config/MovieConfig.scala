package edu.assignment.config

import com.typesafe.config.ConfigFactory

/**
  * Movies application configuration.
  */
trait MovieConfig {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")
  private val databaseConfig = config.getConfig("db.movies")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

  val databaseUrl = databaseConfig.getString("db.url")
  val databaseUser = databaseConfig.getString("db.user")
  val databasePassword = databaseConfig.getString("db.password")
}
