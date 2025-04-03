package com.example.quanlikhachsan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quanlikhachsan.models.Room;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditRoomActivity extends AppCompatActivity {

    private TextInputEditText edtRoomNumber, edtRoomPrice, edtRoomDescription;
    private Spinner spinnerRoomType;
    private Button btnSave, btnCancel;
    private DatabaseHelper databaseHelper; // Database helper instance

    private int roomId = -1; // ID phòng (mặc định là -1 nếu thêm mới)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        // Ánh xạ các thành phần giao diện
        edtRoomNumber = findViewById(R.id.edtRoomNumber);
        edtRoomPrice = findViewById(R.id.edtRoomPrice);
        edtRoomDescription = findViewById(R.id.edtRoomDescription);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Khởi tạo DatabaseHelper
        databaseHelper = DatabaseHelper.getInstance(this);
        if (databaseHelper == null) {
            Toast.makeText(this, "Lỗi: DatabaseHelper không thể khởi tạo!", Toast.LENGTH_LONG).show();
            finish(); // Thoát Activity nếu databaseHelper bị lỗi
            return;
        }

        // Thiết lập danh sách loại phòng cho Spinner
        String[] roomTypes = {"Standard", "Deluxe", "Suite", "VIP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roomTypes);
        spinnerRoomType.setAdapter(adapter);

        // Kiểm tra xem có dữ liệu phòng cần sửa không
        Intent intent = getIntent();
        if (intent.hasExtra("roomId")) {
            roomId = intent.getIntExtra("roomId", -1);
            if (roomId != -1) {
                loadRoomData(roomId);
            }
        }

        // Xử lý sự kiện lưu phòng
        btnSave.setOnClickListener(v -> saveOrUpdateRoom());

        // Xử lý sự kiện hủy
        btnCancel.setOnClickListener(v -> finish());
    }

    // Hàm tải dữ liệu phòng nếu đang sửa
    private void loadRoomData(int roomId) {
        Room room = databaseHelper.getRoomById(roomId);
        if (room != null) {
            edtRoomNumber.setText(room.getRoomNumber());
            edtRoomPrice.setText(String.valueOf(room.getPrice()));
            edtRoomDescription.setText(room.getDescription());

            // Chọn loại phòng tương ứng
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerRoomType.getAdapter();
            int position = adapter.getPosition(room.getRoomType());
            spinnerRoomType.setSelection(position);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy phòng!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void saveOrUpdateRoom() {
        // Lấy dữ liệu từ các trường nhập
        String roomNumber = edtRoomNumber.getText().toString().trim();
        String roomPriceStr = edtRoomPrice.getText().toString().trim();
        String roomDescription = edtRoomDescription.getText().toString().trim();
        String roomType = spinnerRoomType.getSelectedItem().toString();

        // Kiểm tra dữ liệu hợp lệ
        if (roomNumber.isEmpty() || roomPriceStr.isEmpty() || roomDescription.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int roomPrice;
        try {
            roomPrice = Integer.parseInt(roomPriceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá phòng phải là số hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        String status = "Available"; // Mặc định trạng thái phòng là "Available"

        if (roomId == -1) {
            // Thêm mới phòng
            boolean isInserted = databaseHelper.addRoom(roomNumber, roomType, roomPrice, roomDescription, status);
            if (isInserted) {
                Toast.makeText(this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi! Số phòng đã tồn tại hoặc chèn dữ liệu thất bại.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Cập nhật phòng
            boolean isUpdated = databaseHelper.updateRoom(roomId, roomNumber, roomType, roomPrice, roomDescription, status);
            if (isUpdated) {
                Toast.makeText(this, "Cập nhật phòng thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi! Cập nhật không thành công.", Toast.LENGTH_SHORT).show();
            }
        }

        // Đóng activity sau khi lưu hoặc cập nhật
        finish();
    }
}
