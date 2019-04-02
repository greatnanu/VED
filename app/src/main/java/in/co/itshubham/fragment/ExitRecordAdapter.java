package in.co.itshubham.fragment;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExitRecordAdapter extends RecyclerView.Adapter<ExitRecordAdapter.MyViewHolder> implements Filterable {

    private RequestOptions options ;
    private Context mContext ;
    private List<ExitRecordModel> mData ;
    private List<ExitRecordModel> newData ;
    private Dialog myDialog;


    public ExitRecordAdapter(Context mContext, List<ExitRecordModel> lst) {


        this.mContext = mContext;
        this.mData = lst;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@Nullable ViewGroup parent, int viewType) {

        View v ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        v = mInflater.inflate(R.layout.exitrecorditem,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);
//        Dialog Ini
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.exitrecordview);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vHolder.item_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView frag_name = myDialog.findViewById(R.id.dialog_name_id);
                TextView frag_relation = myDialog.findViewById(R.id.dialog_relation_id);
                TextView frag_studentName = myDialog.findViewById(R.id.dialog_student_name);
                TextView frag_mobile = myDialog.findViewById(R.id.dialog_mobile_id);
                TextView frag_exitTime = myDialog.findViewById(R.id.dialog_time_id);
                TextView frag_studentDetails = myDialog.findViewById(R.id.dialog_student_details);
                TextView frag_address = myDialog.findViewById(R.id.dialog_address_id);
                ImageView frag_photo  = myDialog.findViewById(R.id.dialog_photo_id);
                Button audioPlay = myDialog.findViewById(R.id.audioPlay);
                audioPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String audioUrl = mData.get(vHolder.getAdapterPosition()).getAudio_url();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse(audioUrl);
                        intent.setDataAndType(data,"video/3gpp");
                        mContext.startActivity(intent);
                    }
                });
                frag_name.setText(mData.get(vHolder.getAdapterPosition()).getName());
                frag_relation.setText(mData.get(vHolder.getAdapterPosition()).getRelation());
                frag_studentName.setText(mData.get(vHolder.getAdapterPosition()).getStudentName());
                frag_mobile.setText(mData.get(vHolder.getAdapterPosition()).getMobile());
                frag_mobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number=mData.get(vHolder.getAdapterPosition()).getMobile();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+number));
                        mContext.startActivity(callIntent);
                    }
                });
                frag_exitTime.setText(mData.get(vHolder.getAdapterPosition()).getDate());
                frag_studentDetails.setText(mData.get(vHolder.getAdapterPosition()).getStudentDetails());
                frag_address.setText(mData.get(vHolder.getAdapterPosition()).getAddress());
                Glide.with(mContext).load(mData.get(vHolder.getAdapterPosition()).getImage_url()).apply(options).into(frag_photo);
                myDialog.show();

            }
        });
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(mData.get(position).getName());
        holder.mobile.setText(mData.get(position).getMobile());
        holder.web_Id.setText(mData.get(position).getId());
        Glide.with(mContext).load(mData.get(position).getImage_url()).apply(options).into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ExitRecordModel> filteredResults;
                if (constraint.length() == 0) {
                    filteredResults = mData;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                newData = (List<ExitRecordModel>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    private List<ExitRecordModel> getFilteredResults(String toLowerCase) {
        List<ExitRecordModel> results = new ArrayList<>();

        for (ExitRecordModel item : newData) {
            if (item.getName().toLowerCase().contains(toLowerCase)) {
                results.add(item);
            }
        }
        return results;
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item_contact;
        TextView name,mobile,web_Id;
        ImageView photo;


        public MyViewHolder(View itemView) {
            super(itemView);
            item_contact = itemView.findViewById(R.id.contact_item);
            name = itemView.findViewById(R.id.Name);
            mobile = itemView.findViewById(R.id.Mobile);
            web_Id = itemView.findViewById(R.id.webId);
            photo = itemView.findViewById(R.id.photo);
        }
    }
}

