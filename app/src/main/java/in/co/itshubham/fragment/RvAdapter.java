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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder>  {

    private RequestOptions options ;
    private Context mContext ;
    private List<Anime> mData ;
    private Dialog myDialog;
    public RvAdapter(Context mContext, List<Anime> lst) {


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
        v = mInflater.inflate(R.layout.history_item,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);
//        Dialog Ini
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.historyitemview);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vHolder.item_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView frag_name = myDialog.findViewById(R.id.dialog_name_id);
                final TextView frag_mobile = myDialog.findViewById(R.id.dialog_mobile_id);
                final TextView frag_purpose = myDialog.findViewById(R.id.dialog_purpose_id);
                final TextView frag_address = myDialog.findViewById(R.id.dialog_address_id);
                final TextView frag_date = myDialog.findViewById(R.id.dialog_time_id);
                final TextView frag_exitTime = myDialog.findViewById(R.id.dialog_outTime);
                final ImageView frag_photo  = myDialog.findViewById(R.id.dialog_photo_id);
                final Button audioPlay = myDialog.findViewById(R.id.audioPlay);
                frag_name.setText(mData.get(vHolder.getAdapterPosition()).getName());
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
                frag_purpose.setText(mData.get(vHolder.getAdapterPosition()).getPurpose());
                frag_address.setText(mData.get(vHolder.getAdapterPosition()).getAddress());
                frag_date.setText(mData.get(vHolder.getAdapterPosition()).getDate());
                if(TextUtils.isEmpty(mData.get(vHolder.getAdapterPosition()).getExittime())){
                    frag_exitTime.setText("Click to add out time");
                }else{
                    frag_exitTime.setText(mData.get(vHolder.getAdapterPosition()).getExittime());
                }
                if(frag_exitTime.getText().equals("Click to add out time")){
                    frag_exitTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String datetime = java.text.SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                            updateExitTime(datetime,mData.get(vHolder.getAdapterPosition()).getId());
                        }
                    });
                }
                audioPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri data = Uri.parse(mData.get(vHolder.getAdapterPosition()).getAudio_url());
                        mContext.startActivity(new Intent(android.content.Intent.ACTION_VIEW).setDataAndType(data,"video/3gpp"));
                    }
                });
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

    private void updateExitTime(final String exittime, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UPDATEEXITTIME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("exittime",exittime);
                return params;
            }
        };
        RequestHandler.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    @Override
    public int getItemCount() {
        return mData.size();
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

