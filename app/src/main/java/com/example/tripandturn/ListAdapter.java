package com.example.tripandturn;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

    Context context;
    private String[] images;
    private String[] title;
    private String[] subtitle;

    public ListAdapter(Context context, String[] title, String[] subtitle, String[] images) {
        this.context = context;
        this.images = images;
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item, parent, false);
            viewHolder.txtTitle = convertView.findViewById(R.id.listTitle);
            viewHolder.txtSubtitle = convertView.findViewById(R.id.listSubtitle);
            viewHolder.icon = convertView.findViewById(R.id.listIcon);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtTitle.setText(title[position]);
        viewHolder.txtSubtitle.setText(subtitle[position]);
        viewHolder.icon.setImageURI(Uri.parse(images[position]));

        return convertView;
    }

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtSubtitle;
        ImageView icon;
    }
}
