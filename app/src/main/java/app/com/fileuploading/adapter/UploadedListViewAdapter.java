package app.com.fileuploading.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.com.fileuploading.R;
import app.com.fileuploading.model.UploadFileModel;


public class UploadedListViewAdapter extends RecyclerView.Adapter<UploadedListViewAdapter.MyViewHolder> {

    private List<UploadFileModel> uploadedDocList;
    Context mContext;
    public OnFileItemClick fileItemClick;


    public interface OnFileItemClick{
        public void onFileItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        ImageView imageView;
        LinearLayout mLayout;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_view_title);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            mLayout=(LinearLayout)view.findViewById(R.id.linear_layout_row_main);
        }
    }


    public UploadedListViewAdapter(Context mContext, List<UploadFileModel> uploadedDocList, OnFileItemClick fileItemClick) {
        this.uploadedDocList = uploadedDocList;
        this.mContext=mContext;
        this.fileItemClick=fileItemClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_uploaded_doc, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        UploadFileModel document = uploadedDocList.get(position);
        holder.title.setText(document.getName());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileItemClick.onFileItemClick(position);
            }
        });


//        Glide.with(mContext).
//                load(document.getUrl()).
//                fitCenter().
//                placeholder(R.drawable.logo).
//                into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return uploadedDocList.size();
    }
}