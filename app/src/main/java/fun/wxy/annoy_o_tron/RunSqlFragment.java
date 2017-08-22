package fun.wxy.annoy_o_tron;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import fun.wxy.annoy_o_tron.dao.DBHelper;
import fun.wxy.annoy_o_tron.dao.DaoSession;
import fun.wxy.annoy_o_tron.utils.U;


public class RunSqlFragment extends Fragment {

    public RunSqlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run_sql, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.btn_run_it);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(runThread).start();
            }
        });
    }

    private Runnable runThread = new Runnable() {
        @Override
        public void run() {
            ArrayList<String> list = new ArrayList<>();
            try {
                // get from url
                String url = "https://bitbucket.org/snippets/shg_group/r4jxn";
                url += "?v" + Math.random();
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("div.bb-content-container-header-secondary div.aui-buttons a.aui-button");
                if (elements.size() > 0) {
                    url = elements.get(0).attr("href");
                    String sql = U.getStringFromUrl(url);
                    list.add("本次執行的SQL: " + url);
                    // run it
                    SQLiteDatabase database = DBHelper.getInstance(getContext()).getWritableDatabase();
                    Cursor cursor = database.rawQuery(sql, null);
                    list.add("查詢結果: ");
                    // add header first
                    list.add(TextUtils.join(" | ", cursor.getColumnNames()));
                    // add data
                    while (cursor.moveToNext()) {
                        ArrayList<String> cols = new ArrayList<>();
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            cols.add(cursor.getString(i));
                        }
                        list.add(TextUtils.join(" | ", cols));
                    }
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                list.add("===== 出錯了 =====");
                list.add(sw.toString());
            }

            String result = TextUtils.join("\r\n", list);
            // send email
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"summer.wxy@gmail.com", "ssd863419@foxmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, U.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss") + " 執行結果");
            i.putExtra(Intent.EXTRA_TEXT, result);
            try {
                startActivity(Intent.createChooser(i, "請選擇寄送 email 的 app..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "此設備上沒有找到可以寄送 email 的 app.", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
