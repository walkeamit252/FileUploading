package app.com.fileuploading.fragment;


import com.google.gson.Gson;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.fileuploading.R;
import app.com.fileuploading.model.SignupModel;
import app.com.fileuploading.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView mUserName,mUserEmail,mUserNumber;
    ImageView mImageProfile;

    SharedPreferences prefs;



    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserName=(TextView)view.findViewById(R.id.mUserName);
        mUserEmail=(TextView)view.findViewById(R.id.mUserEmail);
        mUserNumber=(TextView)view.findViewById(R.id.mUserNumber);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        setUserDetail();
    }

    private void setUserDetail() {
        Gson gson = new Gson();
        String json = prefs.getString(Constants.USER_MODEL, "");
        if(!TextUtils.isEmpty(json)){
            SignupModel model = gson.fromJson(json, SignupModel.class);
            if(model!=null){
                 mUserName.setText(model.getName());
                 mUserEmail.setText(model.getEmail());
                 mUserNumber.setText(model.getNumber());
            }
        }
    }
}
