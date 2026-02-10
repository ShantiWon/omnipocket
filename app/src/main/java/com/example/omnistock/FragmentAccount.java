package com.example.omnistock;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAccount extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAccount newInstance(String param1, String param2) {
        FragmentAccount fragment = new FragmentAccount();
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
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
    MaterialButton logoutBtn;
    TextView name, email, shipName, shipAddress;
    View completedBtn, shipBtn, receiveBtn;
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        shipName = view.findViewById(R.id.shipName);
        shipAddress = view.findViewById(R.id.shipAddress);
        completedBtn = view.findViewById(R.id.completedBtn);
        shipBtn = view.findViewById(R.id.shipBtn);
        receiveBtn = view.findViewById(R.id.receiveBtn);

        logoutBtn.setOnClickListener(v-> {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id");
                editor.remove("is_admin");
                editor.apply();

                startActivity(new Intent(getContext(), AccountLayout.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                );
                getActivity().finish();
            }
        );
        
        // Set up transaction viewer buttons
        completedBtn.setOnClickListener(v -> openTransactionViewer("completed"));
        shipBtn.setOnClickListener(v -> openTransactionViewer("pending"));
        receiveBtn.setOnClickListener(v -> openTransactionViewer("received"));
        
        loadUserData();
    }
    
    private void openTransactionViewer(String status) {
        Intent intent = new Intent(getContext(), ViewTransactions.class);
        intent.putExtra("status", status);
        startActivity(intent);
    }
    private void loadUserData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String url = "http://10.0.2.2/omnistock/getUser.php?user_id="+userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String userName = data.getString("name");
                            name.setText(userName);
                            shipName.setText(userName);
                            email.setText(data.getString("email"));
                            shipAddress.setText(data.getString("address"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
}