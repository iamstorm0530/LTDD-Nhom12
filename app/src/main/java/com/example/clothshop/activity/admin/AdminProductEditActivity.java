package com.example.clothshop.activity.admin;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.example.clothshop.model.Variant;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AdminProductEditActivity extends AppCompatActivity {

    // Views
    private TextInputEditText edtName, edtPrice, edtDescription;
    private AutoCompleteTextView autoCompleteTag, autoCompleteStatus; // Dropdowns

    private SwitchCompat switchSale;
    private MaterialCardView cvSaleContainer;
    private RadioGroup rgSaleMode;
    private RadioButton rbByPrice, rbByPercent;
    private TextInputLayout tilSalePrice, tilSalePercent;
    private TextInputEditText edtSalePrice, edtSalePercent;

    private LinearLayout llVariantList;
    private TextView btnAddVariant;

    private ProgressBar progressBar;
    private TextView btnSave;
    private ImageView btnBack;

    // Images
    private RecyclerView rvImages;
    private MaterialCardView btnAddImage;
    private ImageEditAdapter imageAdapter;

    // Data
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String productId;
    private Product currentProduct;

    private final List<String> oldImageUrls = new ArrayList<>();
    private final List<Uri> newImageUris = new ArrayList<>();
    private final Set<String> originalVariantIds = new HashSet<>();

    // Logic Flags
    private boolean hasChanges = false;
    private boolean isProgrammaticChange = false;

    // Default Status (Category will be loaded from DB)
    private static final String[] STATUSES = {"active", "hidden"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_edit);

        productId = getIntent().getStringExtra("productId");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        initViews();
        setupDropdowns(); // Setup Categories/Status
        setupDescriptionScroll(); // Fix scrolling
        setupImageRecycler();
        setupChangeTracker();
        setupSaleLogic();
        setupBackPress();

        loadCategories(); // 🔥 Load Categories from DB
        loadProductData();

        btnBack.setOnClickListener(v -> handleOnBack());
        btnSave.setOnClickListener(v -> saveProduct());
        btnAddImage.setOnClickListener(v -> openGallery());
        btnAddVariant.setOnClickListener(v -> addVariantRow(null));
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);

        autoCompleteTag = findViewById(R.id.autoCompleteTag);
        autoCompleteStatus = findViewById(R.id.autoCompleteStatus);

        edtDescription = findViewById(R.id.edtDescription);

        switchSale = findViewById(R.id.switchSale);
        cvSaleContainer = findViewById(R.id.cvSaleContainer);
        rgSaleMode = findViewById(R.id.rgSaleMode);
        rbByPrice = findViewById(R.id.rbByPrice);
        rbByPercent = findViewById(R.id.rbByPercent);
        tilSalePrice = findViewById(R.id.tilSalePrice);
        tilSalePercent = findViewById(R.id.tilSalePercent);
        edtSalePrice = findViewById(R.id.edtSalePrice);
        edtSalePercent = findViewById(R.id.edtSalePercent);

        llVariantList = findViewById(R.id.llVariantList);
        btnAddVariant = findViewById(R.id.btnAddVariant);

        progressBar = findViewById(R.id.progressBar);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        rvImages = findViewById(R.id.rvImages);
        btnAddImage = findViewById(R.id.btnAddImage);
    }

    // ==================== 1. DROPDOWN SETUP & LOAD DB ====================

    private void setupDropdowns() {
        // Status Adapter (Fixed)
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, STATUSES);
        autoCompleteStatus.setAdapter(statusAdapter);

        // Tracking changes
        autoCompleteTag.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isProgrammaticChange) hasChanges = true;
            }
        });
        autoCompleteStatus.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isProgrammaticChange) hasChanges = true;
            }
        });
    }

    // 🔥 Query DB để lấy tất cả tag hiện có
    private void loadCategories() {
        db.collection("products").get().addOnSuccessListener(queryDocumentSnapshots -> {
            Set<String> tagSet = new HashSet<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String tag = doc.getString("tag");
                if (tag != null && !tag.isEmpty()) {
                    tagSet.add(tag.toUpperCase()); // Chuẩn hóa chữ hoa
                }
            }
            List<String> tagList = new ArrayList<>(tagSet);
            Collections.sort(tagList);

            // Nếu DB mới, chưa có category nào, thêm mẫu để test
            if (tagList.isEmpty()) {
                tagList.add("MALE"); tagList.add("FEMALE"); tagList.add("UNISEX");
            }

            ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tagList);
            autoCompleteTag.setAdapter(tagAdapter);
        });
    }

    // ==================== 2. DESCRIPTION SCROLL FIX ====================
    private void setupDescriptionScroll() {
        // Logic này giúp EditText cuộn được nội dung bên trong nó
        // mà không bị ScrollView chính của màn hình chiếm quyền điều khiển
        edtDescription.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.edtDescription) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });
    }

    // ==================== 3. SALE LOGIC ====================
    private void setupSaleLogic() {
        switchSale.setOnCheckedChangeListener((btn, isChecked) -> {
            cvSaleContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isProgrammaticChange) hasChanges = true;
        });

        rgSaleMode.setOnCheckedChangeListener((group, checkedId) -> {
            updateSaleInputState(checkedId == R.id.rbByPrice);
        });

        edtSalePrice.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isProgrammaticChange) return;
                hasChanges = true;
                if (rbByPrice.isChecked()) calculatePercentFromPrice();
            }
        });

        edtSalePercent.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isProgrammaticChange) return;
                hasChanges = true;
                if (rbByPercent.isChecked()) calculatePriceFromPercent();
            }
        });
    }

    private void updateSaleInputState(boolean isByPrice) {
        tilSalePrice.setEnabled(isByPrice);
        tilSalePercent.setEnabled(!isByPrice);

        if(!isByPrice) {
            edtSalePrice.clearFocus();
            calculatePriceFromPercent();
        } else {
            edtSalePercent.clearFocus();
            calculatePercentFromPrice();
        }
    }

    private void calculatePercentFromPrice() {
        try {
            double original = Double.parseDouble(edtPrice.getText().toString().trim());
            double sale = Double.parseDouble(edtSalePrice.getText().toString().trim());
            if (original > 0 && sale < original) {
                int percent = (int) Math.round(((original - sale) / original) * 100);
                isProgrammaticChange = true;
                edtSalePercent.setText(String.valueOf(percent));
                isProgrammaticChange = false;
            }
        } catch (NumberFormatException ignored) {}
    }

    private void calculatePriceFromPercent() {
        try {
            double original = Double.parseDouble(edtPrice.getText().toString().trim());
            int percent = Integer.parseInt(edtSalePercent.getText().toString().trim());
            if (original > 0 && percent >= 0 && percent <= 100) {
                double sale = original * (1 - (percent / 100.0));
                isProgrammaticChange = true;
                edtSalePrice.setText(String.valueOf((long)sale));
                isProgrammaticChange = false;
            }
        } catch (NumberFormatException ignored) {}
    }

    // ==================== 4. TRACKING & BACK PRESS ====================
    private void setupChangeTracker() {
        SimpleTextWatcher dirtyWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isProgrammaticChange) hasChanges = true;
            }
        };
        edtName.addTextChangedListener(dirtyWatcher);
        edtPrice.addTextChangedListener(dirtyWatcher);
        edtDescription.addTextChangedListener(dirtyWatcher);
    }

    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleOnBack();
            }
        });
    }

    private void handleOnBack() {
        if (hasChanges) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("You have modified this product. Do you want to save changes before exiting?")
                    .setPositiveButton("Save & Exit", (dialog, which) -> saveProduct())
                    .setNegativeButton("Exit without Saving", (dialog, which) -> finish())
                    .setNeutralButton("Cancel", null)
                    .show();
        } else {
            finish();
        }
    }

    // ==================== 5. LOAD & SAVE DATA ====================

    private void loadProductData() {
        if (productId == null) return;
        setLoading(true);

        db.collection("products").document(productId).get()
                .addOnSuccessListener(doc -> {
                    currentProduct = doc.toObject(Product.class);
                    if (currentProduct == null) { setLoading(false); return; }

                    isProgrammaticChange = true;

                    edtName.setText(currentProduct.getName());
                    edtPrice.setText(String.valueOf((long) currentProduct.getPrice()));

                    // Set Dropdown values with filter=false
                    autoCompleteTag.setText(currentProduct.getTag(), false);
                    autoCompleteStatus.setText(currentProduct.getStatus(), false);

                    edtDescription.setText(currentProduct.getDescription());

                    switchSale.setChecked(currentProduct.isOnSale());
                    if (currentProduct.isOnSale()) {
                        cvSaleContainer.setVisibility(View.VISIBLE);
                        if (currentProduct.getSalePrice() != null)
                            edtSalePrice.setText(String.valueOf(currentProduct.getSalePrice().longValue()));
                        if (currentProduct.getSalePercent() != null)
                            edtSalePercent.setText(String.valueOf(currentProduct.getSalePercent()));
                        updateSaleInputState(true);
                    }

                    if (currentProduct.getImages() != null) {
                        oldImageUrls.addAll(currentProduct.getImages());
                        imageAdapter.notifyDataSetChanged();
                    }

                    db.collection("products").document(productId).collection("variants").get()
                            .addOnSuccessListener(qs -> {
                                setLoading(false);
                                llVariantList.removeAllViews();
                                originalVariantIds.clear();
                                for (var d : qs.getDocuments()) {
                                    Variant v = d.toObject(Variant.class);
                                    if (v != null) {
                                        v.setId(d.getId());
                                        originalVariantIds.add(d.getId());
                                        addVariantRow(v);
                                    }
                                }
                                isProgrammaticChange = false;
                                hasChanges = false;
                            });
                });
    }

    private void addVariantRow(Variant variant) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_variant_edit_row, llVariantList, false);

        TextInputEditText edtColor = row.findViewById(R.id.edtVarColor);
        TextInputEditText edtSize = row.findViewById(R.id.edtVarSize);
        TextInputEditText edtQty = row.findViewById(R.id.edtVarQty);
        ImageView btnRemove = row.findViewById(R.id.btnRemoveVariant);

        if (variant != null) {
            row.setTag(variant.getId());
            edtColor.setText(variant.getColor());
            edtSize.setText(variant.getSize());
            edtQty.setText(String.valueOf(variant.getQuantity()));
        }

        SimpleTextWatcher vWatcher = new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isProgrammaticChange) hasChanges = true;
            }
        };
        edtColor.addTextChangedListener(vWatcher);
        edtSize.addTextChangedListener(vWatcher);
        edtQty.addTextChangedListener(vWatcher);

        btnRemove.setOnClickListener(v -> {
            llVariantList.removeView(row);
            hasChanges = true;
        });

        llVariantList.addView(row);
        if(variant == null) hasChanges = true;
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        if (!newImageUris.isEmpty()) {
            uploadNewImages(uploadedUrls -> {
                List<String> finalImages = new ArrayList<>(oldImageUrls);
                finalImages.addAll(uploadedUrls);
                executeBatchSave(finalImages);
            });
        } else {
            executeBatchSave(oldImageUrls);
        }
    }

    private void executeBatchSave(List<String> finalImages) {
        WriteBatch batch = db.batch();
        DocumentReference productRef = db.collection("products").document(productId);

        Map<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("name", edtName.getText().toString().trim());
        productUpdates.put("price", Double.parseDouble(edtPrice.getText().toString().trim()));

        productUpdates.put("tag", autoCompleteTag.getText().toString().trim());
        productUpdates.put("status", autoCompleteStatus.getText().toString().trim());

        productUpdates.put("description", edtDescription.getText().toString().trim());
        productUpdates.put("images", finalImages);

        boolean isOnSale = switchSale.isChecked();
        productUpdates.put("isOnSale", isOnSale);

        if (isOnSale) {
            String salePriceStr = edtSalePrice.getText().toString().trim();
            String salePercentStr = edtSalePercent.getText().toString().trim();
            if (!salePriceStr.isEmpty()) productUpdates.put("salePrice", Double.parseDouble(salePriceStr));
            if (!salePercentStr.isEmpty()) productUpdates.put("salePercent", Integer.parseInt(salePercentStr));
        } else {
            productUpdates.put("salePrice", FieldValue.delete());
            productUpdates.put("salePercent", FieldValue.delete());
        }

        batch.update(productRef, productUpdates);

        Set<String> remainingIds = new HashSet<>();
        for (int i = 0; i < llVariantList.getChildCount(); i++) {
            View row = llVariantList.getChildAt(i);
            TextInputEditText edtColor = row.findViewById(R.id.edtVarColor);
            TextInputEditText edtSize = row.findViewById(R.id.edtVarSize);
            TextInputEditText edtQty = row.findViewById(R.id.edtVarQty);

            String color = edtColor.getText().toString().trim();
            String size = edtSize.getText().toString().trim();
            String qtyStr = edtQty.getText().toString().trim();
            int qty = qtyStr.isEmpty() ? 0 : Integer.parseInt(qtyStr);

            String variantId = (String) row.getTag();
            DocumentReference variantRef;

            if (variantId != null) {
                variantRef = productRef.collection("variants").document(variantId);
                remainingIds.add(variantId);
            } else {
                variantRef = productRef.collection("variants").document();
            }

            Map<String, Object> variantData = new HashMap<>();
            variantData.put("color", color);
            variantData.put("size", size);
            variantData.put("quantity", qty);
            batch.set(variantRef, variantData);
        }

        for (String originalId : originalVariantIds) {
            if (!remainingIds.contains(originalId)) {
                batch.delete(productRef.collection("variants").document(originalId));
            }
        }

        batch.commit().addOnSuccessListener(aVoid -> {
            setLoading(false);
            Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
            hasChanges = false;
            finish();
        }).addOnFailureListener(e -> {
            setLoading(false);
            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadNewImages(OnImagesUploadedListener listener) {
        List<String> uploadedUrls = new ArrayList<>();
        List<com.google.android.gms.tasks.Task<Uri>> tasks = new ArrayList<>();

        for (Uri uri : newImageUris) {
            String fileName = UUID.randomUUID().toString();
            StorageReference ref = storage.getReference().child("product_images/" + fileName);
            tasks.add(ref.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) throw task.getException();
                return ref.getDownloadUrl();
            }));
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            for (Object result : results) uploadedUrls.add(((Uri) result).toString());
            listener.onUploaded(uploadedUrls);
        }).addOnFailureListener(e -> {
            setLoading(false);
            Toast.makeText(this, "Upload images failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupImageRecycler() {
        imageAdapter = new ImageEditAdapter();
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(imageAdapter);
    }

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    newImageUris.add(uri);
                    imageAdapter.notifyDataSetChanged();
                    hasChanges = true;
                }
            }
    );

    private void openGallery() { galleryLauncher.launch("image/*"); }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isLoading);
        btnAddImage.setEnabled(!isLoading);
        btnAddVariant.setEnabled(!isLoading);
        btnBack.setEnabled(!isLoading);
    }

    interface OnImagesUploadedListener { void onUploaded(List<String> urls); }

    abstract class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {}
    }

    private class ImageEditAdapter extends RecyclerView.Adapter<ImageEditAdapter.ImgViewHolder> {
        @NonNull @Override
        public ImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_image, parent, false);
            return new ImgViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ImgViewHolder holder, int position) {
            if (position < oldImageUrls.size()) {
                Glide.with(holder.itemView).load(oldImageUrls.get(position)).centerCrop().into(holder.img);
                holder.btnRemove.setOnClickListener(v -> {
                    oldImageUrls.remove(position);
                    notifyDataSetChanged();
                    hasChanges = true;
                });
            } else {
                int newPos = position - oldImageUrls.size();
                Glide.with(holder.itemView).load(newImageUris.get(newPos)).centerCrop().into(holder.img);
                holder.btnRemove.setOnClickListener(v -> {
                    newImageUris.remove(newPos);
                    notifyDataSetChanged();
                    hasChanges = true;
                });
            }
        }
        @Override public int getItemCount() { return oldImageUrls.size() + newImageUris.size(); }
        class ImgViewHolder extends RecyclerView.ViewHolder {
            ImageView img, btnRemove;
            public ImgViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.imgThumb);
                btnRemove = itemView.findViewById(R.id.btnRemove);
            }
        }
    }
}