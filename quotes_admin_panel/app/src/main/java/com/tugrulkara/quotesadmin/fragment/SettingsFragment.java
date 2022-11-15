package com.tugrulkara.quotesadmin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.util.Setting;

public class SettingsFragment extends Fragment {

    private CardView share_app,rate_app,contact_us_app,privacy;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        share_app=view.findViewById(R.id.share_app);
        rate_app=view.findViewById(R.id.rate_app);
        contact_us_app=view.findViewById(R.id.contact_us_app);
        privacy=view.findViewById(R.id.privacy_policy_app);

        rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.openAppPage(getActivity());
            }
        });

        share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.shareApp(getActivity());
            }
        });

        contact_us_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.contactUs(getActivity());
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.openPrivacyPolicy(getActivity());
            }
        });
    }

}