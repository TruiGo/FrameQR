package com.alipay.android.ui.personlcard;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.client.Main;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.common.data.UserData;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.RootController;
import com.eg.android.AlipayGphone.R;

public class LargeAvatarViewController extends BaseViewController implements OnTouchListener {

    private RootController mRootController = null;

    private RelativeLayout bg_layout;// bg_layout
    private TextView topTitle;
    private ImageButton topRightButton;
    private ImageView largeAvatar = null;

    DisplayMetrics mDisplayMetrics;

    private Bitmap avatarImg;
    private int mWidth;
    private int mHeight;
    private int statusBarHeight;

    private AlertDialog avatarDialog = null;
    private ListView personlCardEditListView = null;

    private Rect rectIV;
    
    private LinearLayout titleLayout;

    @Override
    protected void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mRootController = getRootController();
        mView = LayoutInflater.from(mRootController).inflate(R.layout.large_avatar, null, false);
        addView(mView, null);

        loadAllVariables();
    }

    private void loadAllVariables() {

        mDisplayMetrics = new DisplayMetrics();
        mRootController.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        
        titleLayout = (LinearLayout)findViewById(R.id.AlipaySubTitle);
        titleLayout.measure(0, 0);
        
        topTitle = (TextView) findViewById(R.id.title_text);
        topTitle.setText("查看原图");

        topRightButton = (ImageButton) findViewById(R.id.title_right);
        topRightButton.setVisibility(View.VISIBLE);
        topRightButton.setImageResource(R.drawable.personlcard_edit_button);
        topRightButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 在这里调出菜单 拍头像、本地上传头像、分享
            	AlipayLogAgent.writeLog(mRootController, Constants.BehaviourID.CLICKED, null, null,
                        Constants.WALLETACCOUNT, null, Constants.SAVEFACEVIEW, Constants.SEEFACEVIEW,
                        Constants.EDITICON, "");
                avatarDialog.show();
            }
        });
        bg_layout = (RelativeLayout) findViewById(R.id.bg_layout);
        largeAvatar = (ImageView) findViewById(R.id.user_avatar);

        avatarImg = mRootController.getUserData().getUserAvtar();
        if (avatarImg != null) {
            largeAvatar.setImageBitmap(avatarImg);
            mWidth = avatarImg.getWidth();
            mHeight = avatarImg.getHeight();
        }
        
        if (largeAvatar.getDrawable() != null) {
            rectIV = largeAvatar.getDrawable().getBounds();
        }
        getStatusBarHeight();
        if (avatarImg != null) {
            minZoom();
            center();
        }
        largeAvatar.setImageMatrix(mMatrix);
        largeAvatar.setOnTouchListener(this);

        imageRepeat();
        preparePersonlCardEdit();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void imageRepeat() {
        BaseHelper.fixBackgroundRepeat(bg_layout);
    }

    private void preparePersonlCardEdit() {
        personlCardEditListView = new ListView(mRootController);
        personlCardEditListView.setBackgroundColor(Color.WHITE);
        personlCardEditListView.setDividerHeight(1);
        personlCardEditListView.setCacheColorHint(Color.WHITE);
        BaseAdapter personlCardEditAdapter = new PersonlCardEditAdapter(mRootController);
        personlCardEditListView.setAdapter(personlCardEditAdapter);
        personlCardEditListView
            .setOnItemClickListener((OnItemClickListener) personlCardEditAdapter);
        if (avatarDialog == null) {
            avatarDialog = new AlertDialog.Builder(mRootController)
                .setTitle(mRootController.getResources().getString(R.string.saveAvatar))
                .setView(personlCardEditListView).create();
            ((PersonlCardEditAdapter) personlCardEditAdapter).setDialog(avatarDialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PersonlCardViewController.PHOTOCROP:
                if (resultCode == Activity.RESULT_OK) {
                    mRootController.setResult(resultCode);
                    mRootController.finish();
                } else if (resultCode == Activity.RESULT_CANCELED) {//失败的情况
                    UserData userData = mRootController.getUserData();
                    if (userData.hasUserAvatar()) {

                    } else {
                        mRootController.setResult(Activity.RESULT_CANCELED);
                        mRootController.finish();
                    }
                }

            default:
                break;
        }

    }

    Matrix mMatrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    float minScaleR;// 最小缩放比例
    static final float MAX_SCALE = 8f;// 最大缩放比例

    static final int NONE = 0;// 初始状态
    static final int DRAG = 1;// 拖动
    static final int ZOOM = 2;// 缩放
    int mode = NONE;

    PointF prev = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        // 主点按下
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(mMatrix);
                // 设置初始点位置
                prev.set(event.getX(), event.getY());
                if (isOnCP(event.getX(), event.getY())) {
                    // 触点在图片区域内
                    mode = DRAG;
                } else {
                    mode = NONE;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                boolean isonpic = isOnCP(event.getX(), event.getY());
                if (oldDist > 10f && isonpic) {
                    savedMatrix.set(mMatrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    return true;
                } else if (mode == ZOOM) {
                	
                	float midX = largeAvatar.getWidth()/2;
                	float midY = largeAvatar.getHeight()/2;
                	
                	
                	
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        mMatrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        mMatrix.postScale(scale, scale, midX, midY);
                    }

//                    float p[] = new float[9];
//                    mMatrix.getValues(p);
//                    float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
//                    if (scale < minScaleR) {
//                        mMatrix.set(initMatrix);
//                    }
                }
                break;
        }
        view.setImageMatrix(mMatrix);
        CheckView();
        largeAvatar.invalidate();
        return true; // indicate event was handled
    }

    /**
     * 两点的距离 Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * 两点的中点 Calculate the mid point of the first two fingers
     * */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private boolean isOnCP(float evx, float evy) {
        float p[] = new float[9];
        mMatrix.getValues(p);
        float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
        RectF rectf = null;
        rectf = new RectF(p[2], p[5], (p[2] + rectIV.width() * scale), (p[5] + rectIV.height()
                                                                               * scale));
        if (rectf != null && rectf.contains(evx, evy)) {
            return true;
        }
        return false;
    }

    private void minZoom() {
//        minScaleR = Math.min((float) mDisplayMetrics.widthPixels / (float) mWidth / 2,
//            (float) mDisplayMetrics.widthPixels / (float) mHeight / 2);
//        if (minScaleR < 1.0 / 2) {
//        } else {
//            minScaleR = 1.0f;
//        }
        float scale = Math.max(240 * mDisplayMetrics.density / (float) mWidth,
            240 * mDisplayMetrics.density / (float) mHeight);
        
        minScaleR = scale;
        
        mMatrix.postScale(scale, scale);
    }
    
    /**
	 * 限制最大最小缩放比例
	 */
	private void CheckView() {
		float p[] = new float[9];
		mMatrix.getValues(p);
		float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
		if (mode == ZOOM) {
			if (scale < minScaleR) {
				mMatrix.setScale(minScaleR, minScaleR);
				center();
			}
			if (scale > MAX_SCALE) {
				mMatrix.set(savedMatrix);
			}
		}
	}

    private void center() {
        center(true, true);
    }

    /**
     * 横向、纵向居中
     */
    protected void center(boolean horizontal, boolean vertical) {

        Matrix m = new Matrix();
        m.set(mMatrix);
        RectF rect = new RectF(0, 0, mWidth, mHeight);
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight = mDisplayMetrics.heightPixels-titleLayout.getMeasuredHeight();
            if (height < screenHeight) {
                deltaY = (screenHeight - height - statusBarHeight) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = largeAvatar.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = mDisplayMetrics.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;

            } else if (rect.right > screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            }
        }
        mMatrix.postTranslate(deltaX, deltaY);
    }

    private void getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = mRootController.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/* (non-Javadoc)
	 * @see com.alipay.android.ui.framework.BaseViewController#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlipayLogAgent.writeLog(mRootController, Constants.BehaviourID.CLICKED, null, null,
                    Constants.WALLETACCOUNT, null, Constants.MYNAMECARD, Constants.SEEFACEVIEW,
                    Constants.BACKICON, "");
		}
		return super.onKeyDown(keyCode, event);
	}
    
    
}
