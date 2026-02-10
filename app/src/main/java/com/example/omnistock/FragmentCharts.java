package com.example.omnistock;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCharts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCharts extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentCharts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCharts.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCharts newInstance(String param1, String param2) {
        FragmentCharts fragment = new FragmentCharts();
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
    private TextView totalProductsTxt, usersTxt, lowStockTxt, noStockTxt;
    private CardView addAdminBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalProductsTxt = view.findViewById(R.id.totalProduct);
        usersTxt = view.findViewById(R.id.users);
        lowStockTxt = view.findViewById(R.id.lowStock);
        noStockTxt = view.findViewById(R.id.noStock);
        addAdminBtn = view.findViewById(R.id.addAdminBtn);

        addAdminBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddAdmin.class);
            startActivity(intent);
        });

        fetchMetrics();
    }
    private void fetchMetrics() {
        String url = "http://10.0.2.2/omnistock/getDashboardMetrics.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            // Set the counts to your UI
                            totalProductsTxt.setText(data.getString("total_products"));
                            usersTxt.setText(data.getString("total_users"));
                            lowStockTxt.setText(data.getString("low_stock"));
                            noStockTxt.setText(data.getString("out_of_stock"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
}