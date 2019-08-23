package com.androidlmy.mapselectaddress;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * @功能: 自定义标题栏View
 * @Creat 2019/07/16 9:59
 * @User Lmy
 * @By Android Studio
 */
public class HeadCustomView extends RelativeLayout implements View.OnClickListener {
    View inflate;
    private ImageView img_back;

    private TextView txt_back;

    private TextView txt_title;

    private TextView txt_finish;

    private ImageView img_finish;

    private RelativeLayout navi_title;

    private ClickCallback clickCallback;

    public HeadCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate = LayoutInflater.from(context).inflate(R.layout.title_layout, this, true);
        img_back = (ImageView) inflate.findViewById(R.id.img_back);
        txt_back = (TextView) inflate.findViewById(R.id.txt_back);
        txt_title = (TextView) inflate.findViewById(R.id.txt_title);
        txt_finish = (TextView) inflate.findViewById(R.id.txt_finish);
        img_finish = (ImageView) inflate.findViewById(R.id.img_finish);
        navi_title = (RelativeLayout) inflate.findViewById(R.id.navi_title);
        img_back.setOnClickListener(this);
        txt_finish.setOnClickListener(this);
        img_finish.setOnClickListener(this);
    }

    public HeadCustomView(Context context) {
        this(context, null);
    }

    /**
     * 设置标题背景色
     *
     * @author :limingyang
     */
    public void setBackgroundColor(int color) {
        navi_title.setBackgroundColor(color);
    }


    /**
     * 设置返回文字颜色
     *
     * @author :limingyang
     */
    public void setBackTitleColor(int resId) {
        setTextColor(txt_back, resId);
    }

    /**
     * 隐藏返回键按钮
     *
     * @author :limingyang
     */
    public void setBackGone() {
        setImageViewVisibility(img_back, GONE);
    }

    /**
     * 设置返回文字
     *
     * @author :limingyang
     */
    public void setBackTitle(String backTitle) {
        setText(txt_back, backTitle + "");
    }

    public void setBackTextSize(int size) {
        setTextSize(txt_back, size);
    }

    /**
     * 设置返回键图标
     *
     * @author :limingyang
     */
    public void setBackImg(int backImg) {
        setImageBackground(img_back, backImg);
    }


    /**
     * 设置中间标题文字颜色
     *
     * @author :limingyang
     */
    public void setTitleTextColor(int resId) {
        setTitleVisible();
        txt_title.setTextColor(resId);
    }

    /**
     * 设置中间标题文字
     *
     * @author :limingyang
     */
    public void setTitle(String title) {
        setTitleVisible();
        txt_title.setText(title + "");
    }

    public void setTitleTextSize(int size) {
        setTextSize(txt_title, size);
    }


    /**
     * 设置完成Textview颜色
     *
     * @param color
     */
    public void setFinishColor(int color) {
        setTextColor(txt_finish, color);
    }


    /**
     * 设置完成文字
     *
     * @author :limingyang
     */
    public void setFinishText(String finish) {
        setFinishVisible();
        txt_finish.setText(finish + "");
    }

    public void setFinishTextSize(int size) {
        setTextSize(txt_finish, size);
    }


    /**
     * 设置完成imageview图标
     *
     * @author :limingyang
     */
    public void setFinishImg(int finishIcon) {
        setFinishImgVisible();
        img_finish.setImageResource(finishIcon);
    }


    /**
     * 设置ImageView背景
     */
    private void setImageBackground(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    /**
     * 设置TextView文字
     */
    private void setText(TextView textView, String content) {
        textView.setText(content);
    }

    /**
     * 设置TextView文字色彩
     */
    private void setTextColor(TextView textView, int color) {
        textView.setTextColor(color);
    }

    /**
     * 设置文字大小
     *
     * @author :limingyang
     */

    private void setTextSize(TextView textView, int size) {
        textView.setTextSize((float) size);
    }

    @Override
    public void onClick(View view) {
        if (clickCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.img_back:
                clickCallback.onBackClick();
                break;
            case R.id.txt_finish:
                clickCallback.onRightClick();
                break;
            case R.id.img_finish:
                clickCallback.onRightImgClick();
                break;
        }
    }


    private void setTextViewVisibility(TextView textView, int visibility) {
        textView.setVisibility(visibility);
    }


    private void setImageViewVisibility(ImageView imageView, int visibility) {
        imageView.setVisibility(visibility);
    }


    private void setTitleVisible() {
        setTextViewVisibility(txt_title, VISIBLE);
    }

    private void setFinishVisible() {
        setTextViewVisibility(txt_finish, VISIBLE);
    }

    private void setFinishImgVisible() {
        setImageViewVisibility(img_finish, VISIBLE);
    }

    public void setFinishImgGone() {
        setImageViewVisibility(img_finish, GONE);
    }

    public void setFinishTextGone() {
        setTextViewVisibility(txt_finish, GONE);
    }

    public void setBackTitleGone() {
        setTextViewVisibility(txt_back, GONE);
    }


    public void setClickCallBack(ClickCallback clickCallBack) {
        this.clickCallback = clickCallBack;
    }


}
