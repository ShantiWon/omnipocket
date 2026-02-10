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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSignup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSignup extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSignup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSignup.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSignup newInstance(String param1, String param2) {
        FragmentSignup fragment = new FragmentSignup();
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }
    EditText nameInpt, emailInpt, passInpt;
    MaterialButton signupBtn;
    TextView loginBtn;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameInpt = view.findViewById(R.id.nameInpt);
        emailInpt = view.findViewById(R.id.emailInpt);
        passInpt = view.findViewById(R.id.passInpt);
        loginBtn = view.findViewById(R.id.loginBtn);
        signupBtn = view.findViewById(R.id.signupBtn);

        loginBtn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new FragmentLogin())
                    .addToBackStack(null)
                    .commit();
        });
        signupBtn.setOnClickListener(v -> {
            String name = nameInpt.getText().toString().trim();
            String email = emailInpt.getText().toString().trim();
            String pass = passInpt.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getActivity(), "Input all fields", Toast.LENGTH_LONG).show();
                return;
            }
            registerUser(name, email, pass);
        });
    }

    public void registerUser(String name, String email, String password) {
        String url = "http://10.0.2.2/omnistock/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if (status.equals("success")) {
                            int newUserId = jsonResponse.getInt("user_id");

                            SharedPreferences prefs = getContext().getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
                            prefs.edit().putInt("user_id", newUserId).apply();
                            prefs.edit().putInt("is_admin", 0).apply();

                            Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), AddAddress.class));
                        } else {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(getActivity()).add(request);
    }
}