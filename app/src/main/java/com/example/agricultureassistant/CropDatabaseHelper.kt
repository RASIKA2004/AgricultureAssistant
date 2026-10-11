package com.example.agricultureassistant

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CropDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AgricultureAssistant.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CROPS = "crop_diary"

        private const val COLUMN_ID = "id"
        private const val COLUMN_CROP_NAME = "crop_name"
        private const val COLUMN_SOWING_DATE = "sowing_date"
        private const val COLUMN_AREA = "area"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE $TABLE_CROPS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CROP_NAME TEXT NOT NULL,
                $COLUMN_SOWING_DATE TEXT NOT NULL,
                $COLUMN_AREA TEXT NOT NULL,
                $COLUMN_STATUS TEXT NOT NULL,
                $COLUMN_NOTES TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CROPS")
        onCreate(db)
    }

    // Add a new crop record
    fun addCrop(crop: Crop): Long {

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_CROP_NAME, crop.cropName)
            put(COLUMN_SOWING_DATE, crop.sowingDate)
            put(COLUMN_AREA, crop.area)
            put(COLUMN_STATUS, crop.status)
            put(COLUMN_NOTES, crop.notes)
        }

        return db.insert(TABLE_CROPS, null, values)
    }

    // Get all crop records
    fun getAllCrops(): MutableList<Crop> {

        val cropList = mutableListOf<Crop>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CROPS ORDER BY $COLUMN_ID DESC",
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                val crop = Crop(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    cropName = it.getString(
                        it.getColumnIndexOrThrow(COLUMN_CROP_NAME)
                    ),
                    sowingDate = it.getString(
                        it.getColumnIndexOrThrow(COLUMN_SOWING_DATE)
                    ),
                    area = it.getString(
                        it.getColumnIndexOrThrow(COLUMN_AREA)
                    ),
                    status = it.getString(
                        it.getColumnIndexOrThrow(COLUMN_STATUS)
                    ),
                    notes = it.getString(
                        it.getColumnIndexOrThrow(COLUMN_NOTES)
                    )
                )

                cropList.add(crop)
            }
        }

        return cropList
    }

    // Update an existing crop
    fun updateCrop(crop: Crop): Int {

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_CROP_NAME, crop.cropName)
            put(COLUMN_SOWING_DATE, crop.sowingDate)
            put(COLUMN_AREA, crop.area)
            put(COLUMN_STATUS, crop.status)
            put(COLUMN_NOTES, crop.notes)
        }

        return db.update(
            TABLE_CROPS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(crop.id.toString())
        )
    }

    // Delete a crop
    fun deleteCrop(id: Int): Int {

        val db = writableDatabase

        return db.delete(
            TABLE_CROPS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }
}