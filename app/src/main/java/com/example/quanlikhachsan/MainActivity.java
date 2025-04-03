package com.example.quanlikhachsan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlikhachsan.models.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RoomAdapter.OnRoomClickListener {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Room> roomList;
    private DatabaseHelper dbHelper;
    private FloatingActionButton btnAddRoom;  // Sửa tên biến để khớp với XML

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewRooms);
        btnAddRoom = findViewById(R.id.btnAddRoom); // Sửa ID để khớp với layout XML

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomList = loadRoomsFromDatabase();
        adapter = new RoomAdapter(this, roomList, this);
        recyclerView.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn nút thêm phòng
        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditRoomActivity.class);
            startActivity(intent);
        });

    }

    private List<Room> loadRoomsFromDatabase() {
        List<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM rooms", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String roomNumber = cursor.getString(1);
                String roomType = cursor.getString(2);
                int price = cursor.getInt(3);
                boolean isBooked = cursor.getInt(4) == 1;

                rooms.add(new Room(id, roomNumber, roomType, price, isBooked));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return rooms;
    }

    @Override
    public void onEditClick(Room room) {
        // Tạo Intent để mở màn hình AddEditRoomActivity
        Intent intent = new Intent(MainActivity.this, AddEditRoomActivity.class);

        // Truyền thông tin của phòng (có thể là ID, roomNumber, roomType, price, isBooked)
        intent.putExtra("roomId", room.getId());
        intent.putExtra("roomNumber", room.getRoomNumber());
        intent.putExtra("roomType", room.getRoomType());
        intent.putExtra("price", room.getPrice());
        intent.putExtra("isBooked", room.isBooked());  // Truyền thông tin phòng đã được đặt hay chưa

        // Mở màn hình AddEditRoomActivity
        startActivity(intent);
    }


    @Override
    public void onDeleteClick(Room room) {
        Toast.makeText(this, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
        roomList.remove(room);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        roomList = loadRoomsFromDatabase();
        adapter.updateRoomList(roomList);
    }
}
