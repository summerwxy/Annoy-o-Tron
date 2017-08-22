package fun.wxy.annoy_o_tron;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import fun.wxy.annoy_o_tron.utils.U;

/**
 * 放棄不寫了, SightDraftInfo 裡面多了一些規則, 找不到資料, 沒辦法自己新增資料進去
 * 這邊只能參考解碼 EnMicroMsg.db 的部分
 */
public class WeVideoFragment extends Fragment {
    private static final String TAG = WeVideoFragment.class.getSimpleName();

    private String wxKey = null;

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
        wxKey = this.getKey();
        // === request su permission ===
        try {
            Process p = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ===  ===
        Button btn = (Button) view.findViewById(R.id.wevideo_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foo();
            }
        });

        Button btn2 = (Button) view.findViewById(R.id.wevideo_copy_db);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from_str = Environment.getDataDirectory().getAbsolutePath() + "/data/com.tencent.mm/MicroMsg/" + getWechatUuid() + "/EnMicroMsg.db";
                File from_file = new File(from_str);
                String to_str = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EnMicroMsg.db";
                File to_file = new File(to_str);
                try {
                    U.copyFileUsingStream(from_file, to_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Snackbar.make(view, "copy database from data to download folder ok!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private void foo() {

        SQLiteDatabase database = null;
        Cursor cur = null;
        try {
            // SUCCESS!!
            SQLiteDatabase.loadLibs(getContext());
            // String xxx = Environment.getDataDirectory().getAbsolutePath() + "/data/com.tencent.mm/MicroMsg/" + this.getWechatUuid() + "/EnMicroMsg.db";
            String xxx = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EnMicroMsg.db";
            database = SQLiteDatabase.openOrCreateDatabase(xxx, wxKey.toCharArray(), null, new WeDatabaseHook(), new WeDatabaseErrorHandler());
            this.decrypt(database);
            database.execSQL("update SightDraftInfo set fileMd5 = 'ef5180d66494b376c77a0ccdb58b775d', fileLength = 93505, fileDuration = 2, createTime = 1478060670721 where localId = 15;");
            cur = database.rawQuery("select * from SightDraftInfo;", null);
            // 14 | 000f61bfe19c0625d403e2f145ef88bd | 1505788323 | ef5180d66494b376c77a0ccdb58b775d | 93505 | 1 | 2 | 1478060670721
            // 15 | f69ecd53b389270ecb0b9e2406c06cd9 | -747866693 | 61dde942fb71e3ccd2b9f980f8184ebe | 328352 | 1 | 6 | 1478060692955
            // bc2680e0ba97728156c1af80e39d2673
            // 666854
            // 20
            showTable(cur);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
        // bc2680e0ba97728156c1af80e39d2673
        // 666854
        // 20
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    private void showTable(Cursor cur) {
        System.out.println(TextUtils.join(" | ", cur.getColumnNames()));
        int cnt = cur.getColumnCount();
        while(cur.moveToNext()) {
            String[] cols = new String[cnt];
            for (int i = 0; i < cur.getColumnCount(); i++) {
                cols[i] = cur.getString(i);
            }
            System.out.println(TextUtils.join(" | ", cols));
        }
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

            // TODO: 沒有檔案權限的時候, result = 0, 後續的部分會出錯!
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
        @Override
        public void preKey(SQLiteDatabase sqLiteDatabase) {
            Log.v(TAG, "enter preKey!");
            decrypt(sqLiteDatabase);
            Cursor cur = sqLiteDatabase.rawQuery("select count(*) from sqlite_master;", new String[0]); // 不知道為什麼一定要先執行一次 不然後面會出錯 !!??
            if (!cur.isClosed()) {
                cur.close();
            }
            Log.v(TAG, "exit preKey!");
        }
        @Override
        public void postKey(SQLiteDatabase sqLiteDatabase) {
        }
    }

    private void decrypt(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.rawExecSQL("PRAGMA key = '" + wxKey + "';");
        sqLiteDatabase.rawExecSQL("PRAGMA cipher_use_hmac = off;");
        sqLiteDatabase.rawExecSQL("PRAGMA cipher_page_size = 1024;");
        sqLiteDatabase.rawExecSQL("PRAGMA kdf_iter = 4000;");
    }


    private class WeDatabaseErrorHandler implements DatabaseErrorHandler {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {
            if (sqLiteDatabase.isOpen()) {
                try {
                    sqLiteDatabase.close();
                } catch (Exception e) {
                    /* ignored */
                    Log.e(TAG, "Exception closing Database object for corrupted database, ignored", e);
                }
            }
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
