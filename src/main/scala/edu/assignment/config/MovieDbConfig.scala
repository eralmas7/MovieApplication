package edu.assignment.config

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait MovieDbConfig {
  protected val dbProfile: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("db.movies")
  protected val db = dbProfile.db
}
