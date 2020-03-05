package cn.cystal.app.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import cn.cystal.app.R;


public class LoadingViewUtils {
    private DialogUtils loadingView;
    private DialogUtils.Builder builder1;

    public LoadingViewUtils(Context context) {
        DialogUtils.Builder builder = new DialogUtils.Builder(context);
        loadingView = builder.view(R.layout.loadingview)
                .style(R.style.SrcbDialog)
                .gravity(Gravity.CENTER)
                .cancelTouchout(false)
                .cancelable(true)
                .build();
    }

    public LoadingViewUtils(Context context, int layoutId) {
        DialogUtils.Builder builder = new DialogUtils.Builder(context);

        builder1 = builder.view(layoutId)
                .style(R.style.Dialog_NoAnimation)
                .gravity(Gravity.CENTER)
                .cancelTouchout(false);
    }

    public void show() {
        loadingView.show();
    }

    public void buildAndShow() {
        loadingView = builder1.build();
        loadingView.show();
    }

    public void noticeSetText(String text) {
        builder1.settext(text, R.id.text);
    }

    public  void dismiss() {
        if (loadingView != null && loadingView.isShowing()){
            if (builder1 != null) {
                View childView = builder1.getChildView();
                if (childView.getParent() != null) {
                    ((ViewGroup)childView.getParent()).removeAllViews();
                }
            }
            loadingView.dismiss();
        }
    }
}