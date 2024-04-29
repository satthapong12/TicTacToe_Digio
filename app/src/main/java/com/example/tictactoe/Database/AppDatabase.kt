package com.example.tictactoe.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [GameResult::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // สร้างตารางใหม่
                //database.execSQL("CREATE TABLE IF NOT EXISTS `game_results_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `winner` TEXT, `timestamp` TEXT NOT NULL DEFAULT '')")
                // คัดลอกข้อมูลจากตารางเก่าไปยังตารางใหม่
                //database.execSQL("INSERT INTO `game_results_new` (`id`, `winner`) SELECT `id`, `winner` FROM `game_results`")
                // ลบตารางเก่า
                //database.execSQL("DROP TABLE IF EXISTS `game_results`")
                // เปลี่ยนชื่อตารางใหม่เป็นชื่อตารางเดิม
                //database.execSQL("ALTER TABLE `game_results_new` RENAME TO `game_results`")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // เพิ่ม migration ในการเปลี่ยนแปลง schema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

