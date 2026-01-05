package com.example.clothshop.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.adapter.address.AddressAdapter;
import com.example.clothshop.adapter.address.SwipeToDeleteAddressCallback;
import com.example.clothshop.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity {

    private RecyclerView rvAddress;
    private Button btnApply, btnAddAddress;
    private AddressAdapter adapter;
    private List<Address> addressList;
    private static final int REQUEST_CODE_ADD = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        initViews();
        setupRecyclerView();

        // Xử lý nút Apply
        btnApply.setOnClickListener(v -> {
            Address selectedAddress = adapter.getSelectedAddress();
            if (selectedAddress != null) {
                // Trả dữ liệu về cho CheckoutActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_address", selectedAddress);
                setResult(RESULT_OK, resultIntent);
                finish(); // Đóng màn hình này lại
            }
        });

        // Nút Add New
        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });
    }

    private void initViews() {
        rvAddress = findViewById(R.id.rvAddress);
        btnApply = findViewById(R.id.btnApply);
        btnAddAddress = findViewById(R.id.btnAddAddress);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvAddress.setLayoutManager(new LinearLayoutManager(this));

        // Fake data
        addressList = new ArrayList<>();
        addressList.add(new Address("1", "Home", "1901 Thornridge Cir. Shiloh, Hawaii 81063", "0911236658",true));
        addressList.add(new Address("2", "Office", "4517 Washington Ave. Manchester, Kentucky 39495", "0911236658",false));
        addressList.add(new Address("3", "Parent's House", "8502 Preston Rd. Inglewood, Maine 98380 8502 Preston Rd. Inglewood, Maine 98380", "0911236658",false));
        addressList.add(new Address("1", "Home", "1901 Thornridge Cir. Shiloh, Hawaii 81063", "0911236658",true));
        addressList.add(new Address("2", "Office", "4517 Washington Ave. Manchester, Kentucky 39495", "0911236658",false));
        addressList.add(new Address("3", "Parent's House", "8502 Preston Rd. Inglewood, Maine 98380 8502 Preston Rd. Inglewood, Maine 98380", "0911236658",false));
        addressList.add(new Address("1", "Home", "1901 Thornridge Cir. Shiloh, Hawaii 81063", "0911236658",true));
        addressList.add(new Address("2", "Office", "4517 Washington Ave. Manchester, Kentucky 39495", "0911236658",false));
        addressList.add(new Address("3", "Parent's House", "8502 Preston Rd. Inglewood, Maine 98380 8502 Preston Rd. Inglewood, Maine 98380", "0911236658",false));

        adapter = new AddressAdapter(addressList, new AddressAdapter.OnAddressActionListener() {
            @Override
            public void onAddressDeleted(int position) {
                // Có thể thêm Toast báo xóa thành công
            }
        });
        rvAddress.setAdapter(adapter);

        // Gắn Swipe Address
        ItemTouchHelper.SimpleCallback simpleCallback = new SwipeToDeleteAddressCallback(adapter);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rvAddress);
    }

    // Override onActivityResult để nhận địa chỉ mới
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK && data != null) {
            Address newAddress = (Address) data.getSerializableExtra("new_address");
            if (newAddress != null) {
                // Logic: Bỏ chọn tất cả cái cũ
                for (Address addr : addressList) {
                    addr.setSelected(false);
                }
                // Thêm cái mới vào đầu list và chọn nó
                addressList.add(0, newAddress);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
