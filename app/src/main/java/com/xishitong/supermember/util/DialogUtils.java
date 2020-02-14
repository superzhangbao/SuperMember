package com.xishitong.supermember.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by zb on 2017/7/13.
 * 自定义dialog管理类
 */

public class DialogUtils extends Dialog{

    private boolean cancelTouchout;
    private boolean cancelable = false;
    private View view;
    private int gravity;
    private DialogUtils(Builder builder) {
        super(builder.context);
        cancelTouchout = builder.cancelTouchout;
        cancelable = builder.cancelable;
        view = builder.view;
        gravity = builder.gravity;
    }

    private DialogUtils(Builder builder, int resStyle) {
        super(builder.context, resStyle);
        cancelTouchout = builder.cancelTouchout;
        cancelable = builder.cancelable;
        view = builder.view;
        gravity = builder.gravity;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        setContentView(view);
        setCanceledOnTouchOutside(cancelTouchout);
        setCancelable(cancelable);
        Window win = getWindow();
        WindowManager.LayoutParams lp;
        if (win != null) {
            lp = win.getAttributes();
            lp.gravity = gravity;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            win.setAttributes(lp);
        }
    }

    public static final class Builder {
        private Context context;
        private boolean cancelTouchout;
        private boolean cancelable;
        private View view;
        private TextView textView,leftButton,rightButton;
        private int resStyle = -1;
        private int gravity = Gravity.CENTER;

        public Builder(Context context) {
            this.context = context;
        }
        public Builder view(int resView) {
            view = LayoutInflater.from(context).inflate(resView, null,false);
            return this;
        }

        public Builder view(View view) {
            this.view = view;
            return this;
        }
        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder settext(String text,int id) {
            textView = view.findViewById(id);
            textView.setText(text);
            return this;
        }
        public Builder setLeftButton(String text,int id) {
            leftButton = view.findViewById(id);
            leftButton.setText(text);
            return this;
        }
        public Builder setRightButton(String text,int id) {
            rightButton = view.findViewById(id);
            rightButton.setText(text);
            return this;
        }

        public Builder setLeftButton(String text,int color,int id) {
            leftButton = view.findViewById(id);
            leftButton.setText(text);
            leftButton.setTextColor(color);
            return this;
        }

        public Builder setRightButton(String text,int color,int id) {
            rightButton = view.findViewById(id);
            rightButton.setText(text);
            rightButton.setTextColor(color);
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle; return this;
        }
        public Builder cancelTouchout(boolean val) {
            cancelTouchout = val; return this;
        }

        public Builder cancelable(boolean val) {
            cancelable = val; return this;
        }

        public Builder addViewOnclick(int viewRes,View.OnClickListener listener){
            view.findViewById(viewRes).setOnClickListener(listener);
            return this;
        }

        public View getChildView() {
            return view;
        }

        public DialogUtils build() {
            if (resStyle != -1) {
                return new DialogUtils(this, resStyle);
            } else {
                return new DialogUtils(this);
            }
        }
    }
}
