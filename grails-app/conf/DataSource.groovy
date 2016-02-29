dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "org.postgresql.Driver"
    dialect = org.hibernate.dialect.PostgreSQLDialect
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://10.0.0.2:5432/sadweb_prdc"
            username = "postgres"
            password = "postgres"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://10.0.0.2:5432/happy"
            username = "postgres"
            password = "postgres"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://127.0.0.1:5432/happy"
            username = "postgres"
            password = "janus"
        }

/*
        properties {
            jmxEnabled = true
            initialSize = 10
            maxActive = 100
            minIdle = 10
            maxIdle = 100
            maxWait = 30000         // 10 segundos
//                maxAge = 30 * 60000     // 0 por defecto: No se chequea las conexiones
            timeBetweenEvictionRunsMillis = 30000   //1800000
            minEvictableIdleTimeMillis = 60000
            validationQuery = "SELECT 1"
            validationQueryTimeout = 3
            validationInterval = 30000
            testOnBorrow = true
            testWhileIdle = true
            testOnReturn = false
            jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
            defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
        }
*/
    }
}

