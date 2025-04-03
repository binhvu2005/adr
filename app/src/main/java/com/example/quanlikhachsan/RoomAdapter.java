package com.example.quanlikhachsan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlikhachsan.models.Room;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> roomList;
    private OnRoomClickListener listener;

    public RoomAdapter(Context context, List<Room> roomList, OnRoomClickListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        if (room == null) return;

        holder.roomNumber.setText("Phòng: " + room.getRoomNumber());
        holder.roomType.setText("Loại: " + room.getRoomType());
        holder.price.setText("Giá: " + room.getPrice() + " VNĐ/đêm");
        holder.status.setText(room.isBooked() ? "Đã đặt" : "Trống");

        // Nhấn vào item để sửa phòng
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditRoomActivity.class);
            intent.putExtra("roomId", room.getId());
            context.startActivity(intent);
        });

        // Xử lý nút sửa phòng
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(room));

        // Xử lý nút xóa phòng
        holder.btnDelete.setOnClickListener(v -> showDeleteConfirmation(room, position));
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    // Cập nhật danh sách khi có thay đổi
    public void updateRoomList(List<Room> newRoomList) {
        this.roomList = newRoomList;
        notifyDataSetChanged();
    }

    private void showDeleteConfirmation(Room room, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc muốn xóa phòng này?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            deleteRoomFromDatabase(room);
            roomList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Không", null);
        builder.show();
    }

    private void deleteRoomFromDatabase(Room room) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("rooms", "id=?", new String[]{String.valueOf(room.getId())});
        db.close();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNumber, roomType, price, status;
        Button btnEdit, btnDelete;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNumber = itemView.findViewById(R.id.txtRoomNumber);
            roomType = itemView.findViewById(R.id.txtRoomType);
            price = itemView.findViewById(R.id.txtRoomPrice);
            status = itemView.findViewById(R.id.txtRoomStatus);
            btnEdit = itemView.findViewById(R.id.btnEditRoom);
            btnDelete = itemView.findViewById(R.id.btnDeleteRoom);
        }
    }

    public interface OnRoomClickListener {
        void onEditClick(Room room);
        void onDeleteClick(Room room);
    }
}
