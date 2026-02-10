package com.example.omnistock;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.omnistock.adapter.CartAdapter;
import com.example.omnistock.model.CartItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCart extends Fragment implements CartAdapter.OnCartUpdateListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentCart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCart.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCart newInstance(String param1, String param2) {
        FragmentCart fragment = new FragmentCart();
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

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartList = new ArrayList<>();
    private TextView totalTextView;
    private CheckBox allCheckbox;
    private com.google.android.material.button.MaterialButton checkoutBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        totalTextView = view.findViewById(R.id.totalPrice);
        allCheckbox = view.findViewById(R.id.allCheckbox);
        checkoutBtn = view.findViewById(R.id.checkoutBtn);

        adapter = new CartAdapter(cartList, this);
        recyclerView.setAdapter(adapter);

        allCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartList) {
                item.setChecked(isChecked);
            }
            adapter.notifyDataSetChanged();
            onTotalChanged();
        });

        checkoutBtn.setOnClickListener(v -> {
            ArrayList<CartItem> checkedItems = new ArrayList<>();
            for (CartItem item : cartList) {
                if (item.isChecked()) {
                    checkedItems.add(item);
                }
            }
            
            if (checkedItems.isEmpty()) {
                Toast.makeText(getContext(), "Please select items to checkout", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(getActivity(), ConfirmOrder.class);
            intent.putParcelableArrayListExtra("cart_items", checkedItems);
            startActivity(intent);
        });

        loadCartData();
    }
    @Override
    public void onTotalChanged() {
        double total = 0;
        for (CartItem item : cartList) {
            if (item.isChecked()) {
                total += (item.getPrice() * item.getQty());
            }
        }
        totalTextView.setText("PHP " + String.format("%.2f", total));
    }

    private void loadCartData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String url = "http://10.0.2.2/omnistock/getCart.php?user_id="+userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            cartList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                CartItem item = new CartItem(
                                        obj.getInt("cart_id"),
                                        obj.getInt("product_id"),
                                        obj.getString("name"),
                                        obj.getDouble("price"),
                                        obj.getString("product_image"),
                                        obj.getInt("qty")
                                );
                                cartList.add(item);
                            }

                            adapter.notifyDataSetChanged();
                            onTotalChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Data Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Server Error: Check connection", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
    @Override
    public void onUpdateQty(int cartId, String action, int position) {
        String url = "http://10.0.2.2/omnistock/updateCart.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    CartItem item = cartList.get(position);
                    if (action.equals("increase")) {
                        item.setQty(item.getQty() + 1);
                    } else {
                        item.setQty(item.getQty() - 1);
                    }
                    adapter.notifyItemChanged(position);
                    onTotalChanged();
                },
                error -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cart_id", String.valueOf(cartId));
                params.put("action", action);
                return params;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }

    @Override
    public void onDeleteItem(int cartId, int position) {
        String url = "http://10.0.2.2/omnistock/updateCart.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    cartList.remove(position);
                    adapter.notifyItemRemoved(position);
                    onTotalChanged();
                },
                error -> Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cart_id", String.valueOf(cartId));
                params.put("action", "delete");
                return params;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }

}