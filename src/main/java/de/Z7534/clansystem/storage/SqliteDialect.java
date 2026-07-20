package de.Z7534.clansystem.storage;

import com.zaxxer.hikari.HikariConfig;
import de.Z7534.clansystem.managers.ConfigManager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteDialect implements SqlDialect {

    @Override
    public StorageType type() {
        return StorageType.SQLITE;
    }

    @Override
    public void configureDataSource(HikariConfig hikariConfig, ConfigManager config, File dataFolder) {
        File dbFile = new File(dataFolder, config.getDbFile());

        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        hikariConfig.setMaximumPoolSize(1);
        hikariConfig.setConnectionTimeout(10000);
        hikariConfig.setMaxLifetime(0);
        hikariConfig.setConnectionInitSql("PRAGMA foreign_keys = ON");
    }

    @Override
    public void afterConnect(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode = WAL");
        }
    }

    @Override
    public String autoIncrementPrimaryKey() {
        return "INTEGER PRIMARY KEY AUTOINCREMENT";
    }

    @Override
    public boolean hasColumn(Connection connection, String table, String column) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(column)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String upsert(String insertSql, String[] conflictColumns, String[] updateColumns) {
        StringBuilder sb = new StringBuilder(insertSql);
        sb.append(" ON CONFLICT(").append(String.join(", ", conflictColumns)).append(") DO UPDATE SET ");
        for (int i = 0; i < updateColumns.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(updateColumns[i]).append(" = excluded.").append(updateColumns[i]);
        }
        return sb.toString();
    }
}
