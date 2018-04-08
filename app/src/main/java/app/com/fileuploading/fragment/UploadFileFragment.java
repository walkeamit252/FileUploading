package app.com.fileuploading.fragment;

import static android.app.Activity.RESULT_OK;

import java.io.File;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import app.com.fileuploading.R;
import app.com.fileuploading.model.SignupModel;
import app.com.fileuploading.model.UploadFileModel;
import app.com.fileuploading.utils.Constants;


public class UploadFileFragment extends Fragment {
    @Nullable
    private Button mButtonChooseFile,mButtonUploadFile,mBtnListDoc;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    Uri filePath;
    EditText mEditTextFileName;
    ImageView mImageView;
    LinearLayout mLinearLayoutEdit;
    SharedPreferences prefs;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_file, container, false);
        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        initView(view);
        setListner();
        return view;
    }

    private void initView(View view) {

        mButtonChooseFile = (Button) view.findViewById(R.id.btn_choose);
        mButtonUploadFile = (Button) view.findViewById(R.id.btn_upload);
        mBtnListDoc = (Button) view.findViewById(R.id.btn_list_doc);
        mEditTextFileName=(EditText)view.findViewById(R.id.edit_text_file_name);
        mLinearLayoutEdit=(LinearLayout)view.findViewById(R.id.mLinearLayoutEditFile);
    }

    private void setListner() {

        mButtonChooseFile.setOnClickListener(new ChooseButtonListener());
        mButtonUploadFile.setOnClickListener(new UploadButtonclickListner());
        mBtnListDoc.setOnClickListener(new ShowListDocumentFragment());

    }

    private class UploadButtonclickListner implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            uploadFile(filePath);
        }
    }

    private void getPDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
            return;
        }

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_PDF_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                filePath=data.getData();
                if(getMimeType(getActivity(),filePath).equalsIgnoreCase("pdf")){
                    filePath=data.getData();
                    mLinearLayoutEdit.setVisibility(View.VISIBLE);
                }else {
                    filePath=null;
                    Toast.makeText(getActivity(), "Please select PDF Only", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data) {
        if(data!=null){
            if(TextUtils.isEmpty(mEditTextFileName.getText().toString())){
                Toast.makeText(getActivity(),"Please Enter PDF Name",Toast.LENGTH_SHORT).show();
                mEditTextFileName.setSelected(true);
                return;
            }
            final ProgressDialog dialog=new ProgressDialog(getActivity());
            dialog.setTitle("Uploading File");
            dialog.show();

        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        mLinearLayoutEdit.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_LONG).show();
                        String fileName=mEditTextFileName.getText().toString();
                        mEditTextFileName.setText("");
                        Gson gson = new Gson();
                        String json = prefs.getString(Constants.USER_MODEL, "");
                        SignupModel userModel = gson.fromJson(json, SignupModel.class);
                        UploadFileModel model=new UploadFileModel(fileName,taskSnapshot.getDownloadUrl().toString(),userModel.getUserid());
                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(model);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploading....."+(int)progress+"%");
                    }
                });
        }else {
            Toast.makeText(getActivity(),"Please Select Valid PDF Only",Toast.LENGTH_SHORT).show();
        }
    }

    private class ChooseButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getPDF();
        }
    }

    private class ShowListDocumentFragment implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            UploadedListFragment nextFrag= new UploadedListFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }


    public static String getMimeType(Context context, Uri uri) {
        String extension;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

}