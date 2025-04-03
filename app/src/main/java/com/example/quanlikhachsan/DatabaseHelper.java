package com.example.quanlikhachsan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quanlikhachsan.models.Room;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hotel.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ROOMS = "rooms";

    // Định nghĩa các cột của bảng
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ROOM_NUMBER = "room_number";
    public static final String COLUMN_ROOM_TYPE = "room_type";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    // Instance để đảm bảo chỉ có một instance của DatabaseHelper (Singleton)
    private static DatabaseHelper instance;

    // Phương thức lấy instance duy nhất
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Constructor được khai báo là private để đảm bảo chỉ sử dụng getInstance
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate() is running...");
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_ROOMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ROOM_NUMBER + " TEXT UNIQUE, " +
                COLUMN_ROOM_TYPE + " TEXT NOT NULL, " +
                COLUMN_PRICE + " INTEGER NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_STATUS + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
        Log.d("DatabaseHelper", "Table 'rooms' created successfully.");

        // Thêm dữ liệu mẫu
        db.execSQL("INSERT INTO " + TABLE_ROOMS + " (" +
                COLUMN_ROOM_NUMBER + ", " +
                COLUMN_ROOM_TYPE + ", " +
                COLUMN_PRICE + ", " +
                COLUMN_DESCRIPTION + ", " +
                COLUMN_STATUS +
                ") VALUES ('101', 'Standard', 300000, 'Room 101 description', 'Available')");
        db.execSQL("INSERT INTO " + TABLE_ROOMS + " (" +
                COLUMN_ROOM_NUMBER + ", " +
                COLUMN_ROOM_TYPE + ", " +
                COLUMN_PRICE + ", " +
                COLUMN_DESCRIPTION + ", " +
                COLUMN_STATUS +
                ") VALUES ('102', 'Deluxe', 500000, 'Room 102 description', 'Occupied')");
        Log.d("DatabaseHelper", "Sample data inserted.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xoá bảng cũ và tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        Log.d("DatabaseHelper", "Database upgraded: oldVersion = " + oldVersion + ", newVersion = " + newVersion);
        onCreate(db);
    }

    // Phương thức thêm phòng mới
    public boolean addRoom(String roomNumber, String roomType, int price, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NUMBER, roomNumber);
        values.put(COLUMN_ROOM_TYPE, roomType);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_STATUS, status);

        long result = db.insert(TABLE_ROOMS, null, values);
        db.close();
        if (result == -1) {
            Log.e("DatabaseHelper", "Error inserting room with room_number: " + roomNumber);
            return false;
        } else {
            Log.d("DatabaseHelper", "Room inserted with room_number: " + roomNumber);
            return true;
        }
    }

    // Hàm lấy thông tin phòng theo ID
    public Room getRoomById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM rooms WHERE id=?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            Room room = new Room(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );
            cursor.close();
            return room;
        }
        cursor.close();
        return null;
    }

    // Hàm cập nhật phòng
    public boolean updateRoom(int id, String roomNumber, String roomType, int price, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NUMBER, roomNumber);
        values.put(COLUMN_ROOM_TYPE, roomType);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_STATUS, status);

        int rowsAffected = db.update(TABLE_ROOMS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }


}
