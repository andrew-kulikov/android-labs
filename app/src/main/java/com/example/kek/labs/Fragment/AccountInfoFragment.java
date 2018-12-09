package com.example.kek.labs.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kek.labs.R;
import com.example.kek.labs.Util.FileManager;
import com.example.kek.labs.Util.ImageManager;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class AccountInfoFragment extends Fragment {
    private View infoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.account_info_fragment, container, false);

        ImageView logo = infoView.findViewById(R.id.accountLogo);
        ImageManager imageManager = new ImageManager(getActivity());

        imageManager.LoadImage(logo,
                "logo.jpg",
                R.drawable.about);

        NavHostFragment host = (NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        final NavController controller = host.getNavController();

        infoView.findViewById(R.id.edit_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.accountEditFragment);
            }
        });

        FileManager fileManager = new FileManager(getActivity());
        String data = fileManager.read("storage.json");
        try {
            JSONObject json = new JSONObject(data);
            setText(R.id.info_email_textView, json.getString("email"));
            setText(R.id.info_name_textView, json.getString("name"));
            setText(R.id.info_surname_textView, json.getString("surname"));
            setText(R.id.info_phone_textView, json.getString("phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return infoView;
    }

    private void setText(int viewId, String value) {
        ((TextView) infoView.findViewById(viewId)).setText(value);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
