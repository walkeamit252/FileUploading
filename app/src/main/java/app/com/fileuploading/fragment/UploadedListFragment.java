package app.com.fileuploading.fragment;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.com.fileuploading.R;
import app.com.fileuploading.adapter.UploadedListViewAdapter;
import app.com.fileuploading.model.SignupModel;
import app.com.fileuploading.model.UploadFileModel;
import app.com.fileuploading.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadedListFragment extends Fragment {

    RecyclerView mRecyclerView;
    TextView mTxtNoDataFound;
    DatabaseReference mDatabaseReference;
    List<UploadFileModel> mUploadedDocList;
    UploadedListViewAdapter mAdapter;
    ProgressDialog mDialog;
    SignupModel userModel;
    SharedPreferences prefs;

    public UploadedListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_uploaded_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=view.findViewById(R.id.recycler_view);
        mTxtNoDataFound=view.findViewById(R.id.mTxtNoDataFound);
        mUploadedDocList=new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        mAdapter=new UploadedListViewAdapter(getActivity(), mUploadedDocList, new UploadedListViewAdapter.OnFileItemClick() {
            @Override
            public void onFileItemClick(int position) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUploadedDocList.get(position).getUrl()));
                startActivity(browserIntent);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        Gson gson = new Gson();
        String json = prefs.getString(Constants.USER_MODEL, "");
        userModel = gson.fromJson(json, SignupModel.class);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mDialog=new ProgressDialog(getContext());
        mDialog.setMessage("LOADING");
        mDialog.show();

        mDatabaseReference= FirebaseDatabase.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDialog.dismiss();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UploadFileModel model=snapshot.getValue(UploadFileModel.class);
                        mUploadedDocList.add(model);
                }
                mAdapter.notifyDataSetChanged();
                if(mUploadedDocList.size()==0){
                    mRecyclerView.setVisibility(View.GONE);
                    mTxtNoDataFound.setVisibility(View.VISIBLE);
                }else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTxtNoDataFound.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });


    }
}
