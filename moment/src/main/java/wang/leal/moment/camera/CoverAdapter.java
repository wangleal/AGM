package wang.leal.moment.camera;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import wang.leal.moment.R;

public class CoverAdapter extends PagerAdapter {

    private List<Integer> resList = new ArrayList<>();
    private List<ImageView> imageList = new ArrayList<>();

    CoverAdapter(){
        resList.add(R.drawable.shape_camera_cover_transparent);
        resList.add(R.drawable.pic_camera_cover_circle);
        resList.add(R.drawable.pic_camera_cover_square);
        resList.add(R.drawable.pic_camera_cover_eye);
        resList.add(R.drawable.pic_camera_cover_circle2);
        resList.add(R.drawable.pic_camera_cover_cloud);
    }

    @Override
    public int getCount() {
        return resList.size();
    }

    public int getResourceId(int position){
        return resList.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView;
        if (imageList.size()<=position){
            imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(resList.get(position));
            imageList.add(imageView);
        }else {
            imageView = imageList.get(position);
        }

        container.addView(imageView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}
