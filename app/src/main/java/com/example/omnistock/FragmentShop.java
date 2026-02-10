package com.example.omnistock;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.omnistock.adapter.ShopAdapter;
import com.example.omnistock.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentShop#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentShop extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentShop() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentShop.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentShop newInstance(String param1, String param2) {
        FragmentShop fragment = new FragmentShop();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }
    private List<Product> productList = new ArrayList<>();
    private ShopAdapter adapter;
    private RecyclerView recyclerView;
    private EditText searchInput;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
        
        searchInput = view.findViewById(R.id.search);

        adapter = new ShopAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Add search listener
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadProducts();
                } else {
                    searchProducts(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadProducts();
    }
    private void loadProducts() {
        String url = "http://10.0.2.2/omnistock/getProducts.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            productList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                Product product = new Product(
                                        obj.getInt("product_id"),
                                        obj.getString("name"),
                                        obj.getDouble("price"),
                                        obj.getString("product_image"),
                                        obj.getString("description"),
                                        obj.getInt("stock_qty")
                                );
                                productList.add(product);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Server Error. Check Connection.", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

    private void searchProducts(String query) {
        String url = "http://10.0.2.2/omnistock/searchProducts.php?query=" + android.net.Uri.encode(query);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        if (response == null || response.isEmpty()) {
                            productList.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        
                        if (status.equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            productList.clear();
                            
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Product product = new Product(
                                        obj.getInt("product_id"),
                                        obj.getString("name"),
                                        obj.getDouble("price"),
                                        obj.getString("product_image"),
                                        obj.getString("description"),
                                        obj.getInt("stock_qty")
                                );
                                productList.add(product);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            productList.clear();
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        productList.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    productList.clear();
                    adapter.notifyDataSetChanged();
                    String errorMsg = "Search failed";
                    if (error.networkResponse != null) {
                        errorMsg = "Server Error: " + error.networkResponse.statusCode;
                    } else if (error.getMessage() != null) {
                        errorMsg = error.getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
}