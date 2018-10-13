package com.example.standardlayout;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends BaseAdapter implements Filterable {
    List<ConnectInfo> info;
    List<ConnectInfo> backInfo; //备份
    private MyFilter mFilter;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(Context context, List<ConnectInfo> info) {
        this.info = info;
        this.backInfo = info;
        this.context = context;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    @Override
    public Object getItem(int position) {
        return info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.main_list_item, null);
        } else {
            view = convertView;
        }
        //convertView = layoutInflater.inflate(R.layout.main_list_item,null);
        TextView name = view.findViewById(R.id.id_name);
        TextView sex = view.findViewById(R.id.id_sex);
        TextView phone = view.findViewById(R.id.id_phone);
        ImageView avatar = view.findViewById(R.id.id_avatar);

        name.setText(info.get(position).getName());
        sex.setText(info.get(position).getSex());
        phone.setText(info.get(position).getPhone());

        if (info.get(position).getAvatar() != null) {
            avatar.setImageBitmap(info.get(position).getAvatar());
        } else {
            avatar.setImageResource(R.drawable.head);
        }
        return view;
    }

    class MyFilter extends Filter {
        //在这里定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ConnectInfo> list;
            //当过滤的关键字为空的时候，我们则显示所有的数据
            if (TextUtils.isEmpty(constraint)) {
                list = backInfo;
            } else {
                //否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();   //新建ArrayList用于存放符合过滤条件的项
                for (ConnectInfo c : backInfo) {
                    if (c.toString().contains(constraint)) {
                        list.add(c);
                    }
                }
            }
            results.values = list; //将得到的集合保存到FilterResults的value变量中
            results.count = list.size();//将集合的大小保存到FilterResults的count变量中
            return results; //返回results，publishResults(CharSequence constraint, FilterResults results)接受此返回值
        }

        //在这里更新显示结果
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            info = (List<ConnectInfo>) results.values;   //取出results中存放的集合
            if (results.count > 0) {
                notifyDataSetChanged();//通知数据发生了改变
            } else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }
    }
}
