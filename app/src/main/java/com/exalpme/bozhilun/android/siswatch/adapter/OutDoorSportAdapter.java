package com.exalpme.bozhilun.android.siswatch.adapter;

/**
 * Created by Administrator on 2017/11/20.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exalpme.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.siswatch.bean.OutDoorSportBean;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import java.util.List;

/**
 * 跑步列表显示适配器
 */
public class OutDoorSportAdapter extends RecyclerView.Adapter<OutDoorSportAdapter.OutDoorSportViewHolder> {

    private List<OutDoorSportBean> outDoorSportBeanList;
    private Context mContext;


    private OnOutDoorSportItemClickListener listener;

    public void setListener(OnOutDoorSportItemClickListener listener) {
        this.listener = listener;
    }

    public OutDoorSportAdapter(List<OutDoorSportBean> outDoorSportBeanList, Context mContext) {
        this.outDoorSportBeanList = outDoorSportBeanList;
        this.mContext = mContext;
    }

    @Override
    public OutDoorSportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sporthistory_list_item, parent, false);
        return new OutDoorSportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OutDoorSportViewHolder holder, int position) {
        //日期
        holder.yearTv.setText(outDoorSportBeanList.get(position).getRtc());
        //公里
        double distance = Double.valueOf(outDoorSportBeanList.get(position).getDistance());
        double tempDistance = distance * 1000;
        //总公里数
        boolean w30sunit = (boolean) SharedPreferenceUtil.get(mContext, "w30sunit", true);
        if(w30sunit){   //公里
            holder.discTv.setText(""+Math.round(distance)+"km");
            holder.sumdiscTv.setText(""+Math.round(distance)+"km");
        }else{  //Ft 英里
            holder.discTv.setText(""+Math.round(WatchUtils.mul(tempDistance,3.28))+"ft");
            holder.sumdiscTv.setText(""+Math.round(WatchUtils.mul(tempDistance,3.28))+"ft");
        }

        //开始时间
        holder.dateWeekTv.setText(outDoorSportBeanList.get(position).getStartTime());
        //0 跑步 ；1 骑行
        int outSportType = outDoorSportBeanList.get(position).getType();
        if (outSportType == 0) {
            holder.typeImg.setImageResource(R.mipmap.huwaipaohuan);
            holder.outSportTypeTv.setText(mContext.getResources().getString(R.string.outdoor_running));
        } else {
            holder.typeImg.setImageResource(R.mipmap.image_w30s_qixing_run);
            holder.outSportTypeTv.setText(mContext.getResources().getString(R.string.outdoor_cycling));
        }
        //运动时间
        holder.sportDruationTv.setText(outDoorSportBeanList.get(position).getTimeLen());
        //卡里留
        String calories = outDoorSportBeanList.get(position).getCalories();
        holder.kcalTv.setText(""+Double.valueOf(calories)+"kcal");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = holder.getLayoutPosition();
                    listener.doItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return outDoorSportBeanList.size();
    }

    class OutDoorSportViewHolder extends RecyclerView.ViewHolder {

        TextView yearTv, sumdiscTv, dateWeekTv, outSportTypeTv, discTv, sportDruationTv, kcalTv;
        ImageView typeImg;

        public OutDoorSportViewHolder(View itemView) {
            super(itemView);
            //日期
            yearTv = (TextView) itemView.findViewById(R.id.my_year);
            //总里程
            sumdiscTv = (TextView) itemView.findViewById(R.id.shuji_zonggongli);
            //运动类型展示图片
            typeImg = (ImageView) itemView.findViewById(R.id.my_paobu);
            //星期
            dateWeekTv = (TextView) itemView.findViewById(R.id.ri_xiangqing);
            //运动类型
            outSportTypeTv = (TextView) itemView.findViewById(R.id.qixing_my_huwai);
            //此次运动里程
            discTv = (TextView) itemView.findViewById(R.id.chixugongli_time);
            //持续运动时长
            sportDruationTv = (TextView) itemView.findViewById(R.id.chixu_time);
            //卡路里
            kcalTv = (TextView) itemView.findViewById(R.id.my_kacal);
        }
    }

    public interface OnOutDoorSportItemClickListener {
        void doItemClick(int position);
    }

}
