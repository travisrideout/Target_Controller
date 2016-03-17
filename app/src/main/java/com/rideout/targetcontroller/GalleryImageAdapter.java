package com.rideout.targetcontroller;

import java.util.List;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter {

    private Activity context;
    private static ImageView imageView;
    private List<Drawable> plotsImages;
    private static ViewHolder holder;  
    int ImageBiggerAtPosition;
    
    public GalleryImageAdapter(Activity context, List<Drawable> plotsImages) {
        this.context = context;
        this.plotsImages = plotsImages;
    }

    @Override
    public int getCount() {
        if (plotsImages.size() != 0) {
        	return Integer.MAX_VALUE;
        }else {
            return 0;
        } 
    }

    @Override
    public Object getItem(int position) { 
    	if(position >= plotsImages.size()) {
    		position = position % plotsImages.size();
    		}
    		return position;
    }
    
    @Override
    public long getItemId(int position) {   
    	if(position >= plotsImages.size()) {
    		position = position % plotsImages.size();
    		}
    		return position;
    }
    
    public void setShowImageBiggerAtPosition(int pos) {
		// TODO Auto-generated method stub
    	if(pos >= plotsImages.size()) {
    		pos = pos % plotsImages.size();
    		}
		ImageBiggerAtPosition = pos;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            imageView = new ImageView(this.context);
            convertView = imageView;
            holder.imageView = imageView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
                       
        if(position >= plotsImages.size()) {
    		position = position % plotsImages.size();
    		}
                
        holder.imageView.setImageDrawable(plotsImages.get(position));
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.imageView.setLayoutParams(new Gallery.LayoutParams(325, 325));
        
        if (ImageBiggerAtPosition == position){
        	imageView.setPadding(0, 0, 0, 0);
        }else{
        	imageView.setPadding(50, 50, 50, 50);
        }

        return imageView;
    }
    
    public int checkPosition(int position) {
    	 if(position >= plotsImages.size()) {
    	position = position % plotsImages.size();
    	}
    	return position;
    	}

    private static class ViewHolder {
        ImageView imageView;
    }
}


