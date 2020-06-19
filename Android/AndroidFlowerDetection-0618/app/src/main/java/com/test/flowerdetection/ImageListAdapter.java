package com.test.flowerdetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    ArrayList<Item> ImagesList;

    public ImageListAdapter(Context context, int layout, ArrayList<Item> ImagesList ) {
        this.context = context;
        this.layout = layout;
        this.ImagesList = ImagesList;
    }


    @Override
    public int getCount() {
        return ImagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return ImagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.label_name);
            holder.imageView = (ImageView) row.findViewById(R.id.img_his);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Item item = ImagesList.get(position);
        holder.txtName.setText(item.getName());
        byte[] image = item.getImage();

        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
