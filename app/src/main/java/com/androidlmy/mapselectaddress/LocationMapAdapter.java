package com.androidlmy.mapselectaddress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: Liming
 * Date: 2019/8/22 11:13
 * Created by Android Studio.
 */
public class LocationMapAdapter extends RecyclerView.Adapter {


    private Context context;
    private List<PoiItem> mPoiList;

    public LocationMapAdapter(Context context, List<PoiItem> mPoiList) {
        this.context = context;
        this.mPoiList = mPoiList;
    }

    public void setData(List<PoiItem> mPoiList) {
        this.mPoiList = mPoiList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_loacation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            ((ViewHolder) holder).tvTitle.setTextColor(context.getResources().getColor(R.color.bt_select));
            ((ViewHolder) holder).ivLeft.setImageResource(R.drawable.ic_weizhilist);
        } else {
            ((ViewHolder) holder).tvTitle.setTextColor(context.getResources().getColor(R.color.tvtextcolor));
            ((ViewHolder) holder).ivLeft.setImageResource(R.mipmap.ic_mapnoselect);
        }
        ((ViewHolder) holder).tvTitle.setText(mPoiList.get(position).getTitle());
        ((ViewHolder) holder).tvContent.setText(mPoiList.get(position).getProvinceName() +
                mPoiList.get(position).getCityName() +
                mPoiList.get(position).getAdName()
                + mPoiList.get(position).getSnippet());
        ((ViewHolder) holder).rlAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onClick.OnClickListener(mPoiList.get(position).getProvinceName() +
                                mPoiList.get(position).getCityName() +
                                mPoiList.get(position).getDirection(),
                        mPoiList.get(position).getProvinceName() +
                                mPoiList.get(position).getCityName() +
                                mPoiList.get(position).getAdName()
                                + mPoiList.get(position).getSnippet()
                                + mPoiList.get(position).getTitle());
            }
        });
    }

    private OnClick onClick;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;

    }

    public interface OnClick {
        void OnClickListener(String province, String address);
    }

    @Override
    public int getItemCount() {
        return mPoiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_left)
        ImageView ivLeft;
        @BindView(R.id.rl_all)
        RelativeLayout rlAll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
