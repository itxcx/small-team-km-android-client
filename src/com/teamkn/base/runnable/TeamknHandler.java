package com.teamkn.base.runnable;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.teamkn.R;
import com.teamkn.activity.base.LoginActivity;
import com.teamkn.application.TeamknApplication;

public abstract class TeamknHandler extends Handler {
    private Activity activity;

    public TeamknHandler(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        boolean is_success = teamkn_handle_message(msg);

        if (!is_success) {
            switch (msg.what) {
                case TeamknRunnable.METHOD_NOT_DEFINE_EXCEPTION:
                    // 方法没有定义
                    Toast.makeText(
                            TeamknApplication.context,
                            R.string.app_method_not_define_exception,
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
                case TeamknRunnable.INTENT_CONNECTION_EXCEPTION:
                    // 网络连接错误
                    Toast.makeText(
                            TeamknApplication.context,
                            R.string.app_intent_connection_exception,
                            Toast.LENGTH_SHORT
                    ).show();

                    on_intent_connection_exception();

                    break;
                case TeamknRunnable.AUTHENTICATE_EXCEPTION:
                    // 用户身份验证错误
                    Toast.makeText(
                            TeamknApplication.context,
                            R.string.app_authenticate_exception,
                            Toast.LENGTH_SHORT
                    ).show();

                    if (activity.getClass() != LoginActivity.class) {
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }

                    break;
                case TeamknRunnable.UNKNOW_EXCEPTION:
                    // 程序执行错误
                    Toast.makeText(
                            TeamknApplication.context,
                            R.string.app_unknown_exception,
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
                default:
                    // message.what 传入了无法被处理的值，也算程序执行错误
                    Toast.makeText(
                            TeamknApplication.context,
                            R.string.app_unknown_exception,
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
            }
        }
    }

    public abstract boolean teamkn_handle_message(Message msg);

    public void on_intent_connection_exception() {
        //do nothing
        //开发者可以自行重载此方法
    }

    ;
}
