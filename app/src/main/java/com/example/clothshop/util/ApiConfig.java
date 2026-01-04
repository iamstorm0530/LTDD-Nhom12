package com.example.clothshop.util;

public class ApiConfig {

    // Base domain (nếu muốn tách)
    public static final String BASE_URL =
            "https://69495faf1282f890d2d60743.mockapi.io/api/clothshop/";

    // ===== API ENDPOINTS =====
    public static final String USER_API = BASE_URL + "user";
    public static final String PRODUCT_API = BASE_URL + "product";
    public static final String CATEGORY_API = BASE_URL + "category";
    public static final String ORDER_API = BASE_URL + "order";

    // ===== DEFAULT DATA =====
    public static final String DEFAULT_AVATAR =
            "https://i.pravatar.cc/150?img=1";
}
