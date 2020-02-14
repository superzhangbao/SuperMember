package com.xishitong.supermember.util;

import android.view.Gravity;
import android.widget.Toast;
import com.xishitong.supermember.base.App;

public class ToastUtils {
        private static Toast toast;
        public static void showToast(String text){
            if (toast==null){
                toast = Toast.makeText(App.Companion.getInstance(),text, Toast.LENGTH_SHORT);
            }else{
                toast.setText(text);
            }
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        public static void showToast(int id){
            String msg = App.Companion.getInstance().getResources().getString(id);
            if (toast==null){
                toast = Toast.makeText(App.Companion.getInstance(),msg, Toast.LENGTH_SHORT);
            }else{
                toast.setText(msg);
            }
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        public static void showErrorToast(int code){
            switch (code){
                case 1:
                    showToast("");
                    break;
                case 2:
                    showToast("");
                    break;
                case 3:
                    showToast("");
                    break;
                case 4:
                    showToast("");
                    break;
                case 5:
                    showToast("");
                    break;
                case 6:
                    showToast("");
                    break;
                case 7:
                    showToast("");
                    break;
                case 8:
                    showToast("");
                    break;
                case 9:
                    showToast("");
                    break;
                case 11:
                    showToast("");
                    break;
                default:
                    showToast("网络有点偷懒");
                    break;

            }
        }
}
