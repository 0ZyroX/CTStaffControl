package ir.OZyroX.cTStaffControl.Events

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class DBManager {

    fun runDB(){
        connect()
        createTable()
        addMissingColumns()
    }

    fun connect(): Connection? {
        try {
            val dbPath = "plugins/CTStaffControl/storage.db"
            val dbFile = File(dbPath)

            if (!dbFile.exists()) {
                dbFile.parentFile.mkdirs()
                dbFile.createNewFile()
            }

            Class.forName("org.sqlite.JDBC")

            val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
            return connection
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }



    fun createTable() {
        val connection = connect()
        val sql = """
        CREATE TABLE IF NOT EXISTS staffs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            uuid TEXT NOT NULL,
            rank TEXT NOT NULL,
            lastLogin TEXT DEFAULT 'Never',
            prefix TEXT NOT NULL,
            weight INTEGER NOT NULL,
            oldServer TEXT NULL
        )
    """
        connection?.use {
            val statement = it.createStatement()
            statement.execute(sql)
        }
    }

    fun addMissingColumns() {
        val connection = connect()

        val columnsToCheck = mapOf(
            "id" to "INTEGER PRIMARY KEY AUTOINCREMENT",
            "name" to "TEXT NOT NULL",
            "uuid" to "TEXT NOT NULL",
            "rank" to "TEXT NOT NULL",
            "lastLogin" to "TEXT DEFAULT 'Never'",
            "prefix" to "TEXT NOT NULL",
            "weight" to "INTEGER NOT NULL",
            "oldServer" to "TEXT NULL"
        )

        connection?.use { conn ->
            val checkQuery = "PRAGMA table_info(staffs)"
            val resultSet = conn.createStatement().executeQuery(checkQuery)

            val existingColumns = mutableSetOf<String>()
            while (resultSet.next()) {
                existingColumns.add(resultSet.getString("name"))
            }

            for ((column, type) in columnsToCheck) {
                if (!existingColumns.contains(column)) {
                    val alterQuery = "ALTER TABLE staffs ADD COLUMN $column $type"
                    conn.createStatement().execute(alterQuery)
                }
            }
        }
    }



    fun insertData(name: String, uuid: String, rank: String, prefix: String, weight: Int) {
        val connection = connect()
        val checkSql = "SELECT COUNT(*) FROM staffs WHERE uuid = ?"
        val insertSql = "INSERT INTO staffs (name, uuid, rank, prefix, weight) VALUES (?, ?, ?, ?, ?)"
        val updateSql = "UPDATE staffs SET name = ?, rank = ?, prefix = ?, weight = ? WHERE uuid = ?"

        connection?.use { conn ->
            val checkStatement = conn.prepareStatement(checkSql)
            checkStatement.setString(1, uuid)
            val resultSet = checkStatement.executeQuery()

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                val preparedStatement = conn.prepareStatement(updateSql)
                preparedStatement.setString(1, name)
                preparedStatement.setString(2, rank)
                preparedStatement.setString(3, prefix)
                preparedStatement.setInt(4, weight)
                preparedStatement.setString(5, uuid)
                preparedStatement.executeUpdate()

            } else {
                val preparedStatement = conn.prepareStatement(insertSql)
                preparedStatement.setString(1, name)
                preparedStatement.setString(2, uuid)
                preparedStatement.setString(3, rank)
                preparedStatement.setString(4, prefix)
                preparedStatement.setInt(5, weight)
                preparedStatement.executeUpdate()
            }
        }

    }


    fun readData(): List<Staff> {
        val connection = connect()
        val sql = "SELECT * FROM staffs"
        val staffList = mutableListOf<Staff>()

        connection?.use {
            val resultSet = it.createStatement().executeQuery(sql)
            while (resultSet.next()) {
                val name = resultSet.getString("name")
                val uuid = resultSet.getString("uuid")
                val rank = resultSet.getString("rank")
                val prefix = resultSet.getString("prefix")
                val weight = resultSet.getInt("weight")
                val lastLogin = resultSet.getString("lastLogin")
                staffList.add(Staff(name, uuid, rank, prefix, weight, lastLogin))
            }
        }
        return staffList
    }

    fun getPlayerDataByUUID(uuid: String): Staff? {
        val connection = connect()
        val query = "SELECT * FROM staffs WHERE uuid = ?"

        connection?.use { conn ->
            val preparedStatement = conn.prepareStatement(query)
            preparedStatement.setString(1, uuid)

            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                val name = resultSet.getString("name")
                val rank = resultSet.getString("rank")
                val prefix = resultSet.getString("prefix")
                val weight = resultSet.getInt("weight")
                val lastLogin = resultSet.getString("lastLogin")
                return Staff(name, uuid, rank, prefix, weight, lastLogin)
            }
        }

        return null
    }


    fun getStaffList(): List<Staff> {
        val connection = connect()
        val query = "SELECT name, uuid, rank, prefix, weight FROM staffs"
        val staffList = mutableListOf<Staff>()

        connection?.use { conn ->
            val statement = conn.prepareStatement(query)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val name = resultSet.getString("name")
                val uuid = resultSet.getString("uuid")
                val rank = resultSet.getString("rank")
                val prefix = resultSet.getString("prefix")
                val weight = resultSet.getInt("weight")
                val lastOnlineTime = try {
                    resultSet.getString("lastLogin") ?: "Never"
                } catch (e: SQLException) {
                    "Never"
                }


                staffList.add(Staff(name, uuid, rank, prefix, weight, lastOnlineTime))
            }
        }
        return staffList
    }

    fun updateLastOnline(uuid: String) {
        val connection = connect()
        val updateSql = "UPDATE staffs SET lastLogin = CURRENT_TIMESTAMP WHERE uuid = ?"

        connection?.use { conn ->
            val preparedStatement = conn.prepareStatement(updateSql)
            preparedStatement.setString(1, uuid)
            preparedStatement.executeUpdate()
        }
    }

    fun getLastOnlineTime(uuid: String): String? {
        val connection = connect()
        val query = "SELECT lastLogin FROM staffs WHERE uuid = ?"

        connection?.use { conn ->
            val preparedStatement = conn.prepareStatement(query)
            preparedStatement.setString(1, uuid)

            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getString("lastLogin")
            }
        }

        return null
    }

    fun updateOldServer(uuid: String, oldserver: String?) {
        val connection = connect()
        val updateSql = "UPDATE staffs SET oldServer = ? WHERE uuid = ?"

        connection?.use { conn ->
            val preparedStatement = conn.prepareStatement(updateSql)
            if (oldserver == null) {
                preparedStatement.setNull(1, java.sql.Types.VARCHAR)
            } else {
                preparedStatement.setString(1, oldserver)
            }
            preparedStatement.setString(2, uuid)
            preparedStatement.executeUpdate()
        }
    }


    fun getOldServer(uuid: String): String? {
        val connection = connect()
        val query = "SELECT oldServer FROM staffs WHERE uuid = ?"

        connection?.use { conn ->
            val preparedStatement = conn.prepareStatement(query)
            preparedStatement.setString(1, uuid)

            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getString("oldServer")
            }
        }

        return null
    }

    fun deleteStaff(uuid: String) {
        val connection = connect()
        val checkSql = "SELECT COUNT(*) FROM staffs WHERE uuid = ?"
        val deleteSql = "DELETE FROM staffs WHERE uuid = ?"

        connection?.use { conn ->
            val checkStatement = conn.prepareStatement(checkSql)
            checkStatement.setString(1, uuid)
            val resultSet = checkStatement.executeQuery()

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                val deleteStatement = conn.prepareStatement(deleteSql)
                deleteStatement.setString(1, uuid)
                deleteStatement.executeUpdate()
            }
        }
    }


}