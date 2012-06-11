package com.teamkn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SynDataBroadcastReceiver extends BroadcastReceiver {
    public class Type {
        public static final String SET_MAX = "set_max";
        public static final String START_SYN = "start_syn";
        public static final String PROGRESS = "progress";
        public static final String EXCEPTION = "exception";
        public static final String SUCCESS = "success";
        public static final String FINAL = "final";
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        new TeamknAsyncTask<String, Integer, Void>() {

            public void on_start() {
                send_progress_broadcast(context, 0);
            }

            ;

            @Override
            public Void do_in_background(String... params) throws Exception {
                HashMap<String, Object> map = HttpApi.Syn.handshake();
                String uuid = (String) map.get("syn_task_uuid");
                int server_count = (Integer) map.get("note_count");
                int unsyn_count = NoteDBHelper.unsyn_count();
                int count = server_count + unsyn_count;

                send_max_num(context, count);
                int index = 0;
                send_start_syn(context);
                List<Note> list = NoteDBHelper.all(true);
                for (Iterator<Note> iterator = list.iterator(); iterator.hasNext(); ) {
                    Note note = iterator.next();
                    HttpApi.Syn.compare(uuid, note);
                    index += 1;
                    send_progress_broadcast(context, index);
                }
                boolean has_next = true;
                while (has_next) {
                    has_next = HttpApi.Syn.syn_next(uuid);
                    index += 1;
                    send_progress_broadcast(context, index);
                }
                return null;
            }

            public void on_success(Void v) {
                send_success(context);
            }

            public boolean on_unknown_exception() {
                send_exception(context);
                return false;
            }

            ;

            public void on_final() {
                send_final(context);
            }

            ;
        }.execute();

    }

    private void send_max_num(Context context, int max_num) {
        Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
        i.putExtra("type", Type.SET_MAX);
        i.putExtra(Type.SET_MAX, max_num);
        context.sendBroadcast(i);
    }

    private void send_start_syn(Context context) {
        Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
        i.putExtra("type", Type.START_SYN);
        context.sendBroadcast(i);
    }

    private void send_progress_broadcast(Context context, int progress) {
        Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
        i.putExtra("type", Type.PROGRESS);
        i.putExtra(Type.PROGRESS, progress);
        context.sendBroadcast(i);
    }

    private void send_exception(Context context) {
        Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
        i.putExtra("type", Type.EXCEPTION);
        context.sendBroadcast(i);
    }

    private void send_success(Context context) {
        Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
        i.putExtra("type", Type.SUCCESS);
        context.sendBroadcast(i);
    }

    private void send_final(Context context) {
        Intent i = new Intent(BroadcastReceiverConstants.ACTION_SYN_DATA_UI);
        i.putExtra("type", Type.FINAL);
        context.sendBroadcast(i);
    }

}
