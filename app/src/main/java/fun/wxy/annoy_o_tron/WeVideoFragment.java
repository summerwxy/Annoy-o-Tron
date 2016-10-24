package fun.wxy.annoy_o_tron;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.sqlcipher.Cursor;
import net.sqlcipher.DefaultDatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import fun.wxy.annoy_o_tron.utils.U;


public class WeVideoFragment extends Fragment {
    private static final String TAG = WeVideoFragment.class.getSimpleName();

    public WeVideoFragment() {
        // Required empty public constructor
    }

    public static WeVideoFragment newInstance(String param1, String param2) {
        WeVideoFragment fragment = new WeVideoFragment();
        // TODO: list draft folder
        // TODO: choice video
        // TODO: get wechat database
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_we_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.wevideo_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foo();
            }
        });
    }



    private void foo() {
        // SUCCESS!!
        SQLiteDatabase.loadLibs(getContext());
        String xxx = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EnMicroMsg.db";
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(xxx, getKey().toCharArray(), null, new WeDatabaseHook(getKey()), new DefaultDatabaseErrorHandler());
        Cursor cur = database.rawQuery("select * from AppInfo;", null);
        System.out.println(cur);

        System.out.println("/////////////////////////////////");

    }

    private String getKey() {
        String imei = U.getIMEI(getContext());
        String uin = getUin();
        String key = U.md5(imei + uin);
        key = (key.length() > 7) ? key.substring(0, 7) : key;
        return key;
    }

    private String getUin() {
        String result = "0";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            String path_of_xml = Environment.getDataDirectory().getAbsolutePath() + "/data/com.tencent.mm/shared_prefs/system_config_prefs.xml";
            FileInputStream fileInputStream = new FileInputStream(new File(path_of_xml));

            Document document = builder.parse(fileInputStream);
            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("int");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (element.getAttribute("name").equals("default_uin")) {
                    result = element.getAttribute("value");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void foo2() {
        // TODO: 自動更改資料夾檔案權限!! 開發時是手動改的
        // TODO: copy data from data/data
        File file_to_data = Environment.getDataDirectory();
        String foo = file_to_data.getAbsolutePath() + "/data/com.tencent.mm/shared_prefs/system_config_prefs.xml";
        File file1 = new File(foo);
        System.out.println(foo);
        System.out.println(file1.exists());

        foo = file_to_data.getAbsolutePath() + "/data/com.tencent.mm/MicroMsg/" + this.getWechatUuid() + "/EnMicroMsg.db";
        file1 = new File(foo);
        System.out.println(foo);
        System.out.println(file1.exists());
    }

    private class WeDatabaseHook implements SQLiteDatabaseHook {
        private String key;
        public WeDatabaseHook(String key) {
            this.key = key;
        }
        @Override
        public void preKey(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.rawExecSQL("PRAGMA key = '" + this.key + "';");
            sqLiteDatabase.rawExecSQL("PRAGMA cipher_use_hmac = off;");
            sqLiteDatabase.rawExecSQL("PRAGMA cipher_page_size = 1024;");
            sqLiteDatabase.rawExecSQL("PRAGMA kdf_iter = 4000;");
            sqLiteDatabase.rawQuery("select count(*) from sqlite_master;", new String[0]); // 不知道為什麼一定要先執行一次 不然後面會出錯 !!??
        }
        @Override
        public void postKey(SQLiteDatabase sqLiteDatabase) {
        }
    }


    private String getWechatUuid() {
        String result = "";
        String path_to_sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file1 = new File(path_to_sd + "/tencent/MicroMsg");
        if (file1.listFiles() != null) {
            for (File f : file1.listFiles()) {
                String[] dir = f.getAbsolutePath().split("/");
                if (dir[dir.length - 1].length() == 32) {
                    result = dir[dir.length - 1];
                    break;
                }
            }
        }
        return result;
    }

    private void getWechatDraftFiles() {
        String path_to_sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file_of_draft = new File(path_to_sd + "/tencent/MicroMsg/" + this.getWechatUuid() + "/draft");
        for (File f : file_of_draft.listFiles()) {
            System.out.println(f.getAbsolutePath());
        }
    }
}
