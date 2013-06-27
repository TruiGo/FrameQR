package com.alipay.android.ui.guide;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.comon.component.CommonGallery;
import com.eg.android.AlipayGphone.R;

public class AppGuide{
	private AlipayDataStore mOtherDataSettings;
	private AlipayApplication application;
	private Context mContext; 
	private String GUIDE_PATH;
	private GuideListener mOnCompleteListener;
	private boolean mSkipable;//是否可以跳过
	private String mVersionInfo;
	
	public AppGuide(Context context,String path,boolean skipable,String versionInfo) {
		 application = (AlipayApplication) context.getApplicationContext();
		 mOtherDataSettings = new AlipayDataStore(context);
		 mContext = context;
		 GUIDE_PATH = path;
		 mSkipable = skipable;
		 mVersionInfo = versionInfo;
	}
	
	public void setOnCompleteListener(GuideListener onCompleteListener){
		mOnCompleteListener = onCompleteListener; 
	}
	
	public boolean needShowGuide(){
		try {
			return mOtherDataSettings.getString(mVersionInfo, "0.0.0")
				.compareTo(application.getConfigData().getProductVersion()) < 0 
				&& mContext.getAssets().list(GUIDE_PATH).length > 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void initGuide(CommonGallery guideGallery) {
        String[] guides = null;
        try {
        	guides = mContext.getAssets().list(GUIDE_PATH);
            if (guides.length <= 0)
                return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (guides.length > 0) {
        	guideGallery.setVisibility(View.VISIBLE);
            setAdapter4GuideGallery(guideGallery,guides);
            if(mSkipable)
            	mOtherDataSettings.putString(mVersionInfo, application.getConfigData().getProductVersion());
        } else {
        	guideGallery.setVisibility(View.GONE);
        }
    }
	
	private void setAdapter4GuideGallery(final CommonGallery guideGallery,final String[] guides) {
		guideGallery.setAdapter(new MyImageAdapter(guides));
		guideGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == guides.length) {
                	skipOrCompleteGuide(guideGallery);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            	
            }
        });
    }
	
	/**
	 * 跳过或者完成向导浏览
	 * @param guideGallery
	 */
	public void skipOrCompleteGuide(final CommonGallery guideGallery) {
//		guideGallery.setVisibility(View.GONE);
		if(mOnCompleteListener != null){
			mOnCompleteListener.onGuideComplete();
		}
		mOtherDataSettings.putString(mVersionInfo, application.getConfigData().getProductVersion());
	}
	
	private class MyImageAdapter extends BaseAdapter {
        private String[] mPaths;

        public MyImageAdapter(String[] strings) {
            mPaths = strings;
        }

        @Override
        public int getCount() {
            return mPaths.length + 1;
        }

        @Override
        public Object getItem(int position) {
            return mPaths[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHodler {
            ImageView imageview;
            ImageView stepOver;
            ImageView start;
            View skipLayout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
            	convertView = LayoutInflater.from(mContext).inflate(R.layout.guide, null);
                ViewHodler hodler = new ViewHodler();
                hodler.imageview = (ImageView) convertView.findViewById(R.id.guide);
                hodler.stepOver = (ImageView) convertView.findViewById(R.id.stepOver);
                hodler.start = (ImageView) convertView.findViewById(R.id.start);
                hodler.skipLayout = convertView.findViewById(R.id.enter_app);
                
                OnClickListener clickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	if(mOnCompleteListener != null){
                    		mOnCompleteListener.onGuideComplete();
                    		mOtherDataSettings.putString(mVersionInfo, application.getConfigData().getProductVersion());
                    	}
                    }
                };
                hodler.start.setOnClickListener(clickListener);
                hodler.stepOver.setOnClickListener(clickListener);
                hodler.skipLayout.setOnClickListener(clickListener);
                convertView.setTag(hodler);
            }
            ViewHodler viewHodler = (ViewHodler) convertView.getTag();
            convertView.setVisibility(View.VISIBLE);
            
            if(!mSkipable){
            	viewHodler.start.setVisibility(View.INVISIBLE);
                viewHodler.stepOver.setVisibility(View.INVISIBLE);
            }else{
            	viewHodler.start.setVisibility(View.INVISIBLE);
                viewHodler.stepOver.setVisibility(View.VISIBLE);
            }
            
            if (position == mPaths.length) {
                convertView.setVisibility(View.INVISIBLE);
            }else{
                if (position == mPaths.length-1) {
                	if("transfer_guide".equals(GUIDE_PATH)){
                		viewHodler.start.setVisibility(View.GONE);
	                    viewHodler.stepOver.setVisibility(View.GONE);
	                    viewHodler.skipLayout.setVisibility(View.VISIBLE);
                	}else{
	                    viewHodler.start.setVisibility(View.VISIBLE);
	                    viewHodler.stepOver.setVisibility(View.INVISIBLE);
	                    viewHodler.skipLayout.setVisibility(View.GONE);
                	}
                }
                try {
                    
                    // Prepare appropriate options for target bitmap.
                    final Options options = new Options();
                    options.inDensity =(int) Helper.getDensityDpi(mContext);
                    options.inScaled = true;
                    
                    Bitmap image = BitmapFactory.decodeStream(mContext.getAssets().open(
                        GUIDE_PATH + File.separator + mPaths[position]), null, options);
                    Bitmap stepImg = BitmapFactory.decodeStream(mContext.getAssets().open(
                        "guide/button_skip.png"), null, options);
                    Bitmap startImg = BitmapFactory.decodeStream(mContext.getAssets().open(
                    "guide/button_play.png"), null, options);
                    viewHodler.imageview.setImageBitmap(image);
                    viewHodler.stepOver.setImageBitmap(stepImg);
                    viewHodler.start.setImageBitmap(startImg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            convertView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
            return convertView;
        }
    }
}

class ViewHodler {
    ImageView imageview;
    ImageView stepOver;
    ImageView start;
}


