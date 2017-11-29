package fun.wxy.annoy_o_tron;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import fun.wxy.annoy_o_tron.utils.U;


// @EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public final static String EXTRA_MESSAGE = "fun.wxy.annoy_o_tron.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                renderFragment(item);
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        // action bar toggle
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer , R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // TODO: TOFIX 應該放到 drawer 的 onDraw 之類的地方去 不過不知道怎麼寫
                // avatar
                ImageView avatar = (ImageView) findViewById(R.id.avatar);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baobao);
                bitmap = U.toSquare(bitmap);
                bitmap = U.scaleBitmap(bitmap, 160);
                bitmap = U.roundCorner(bitmap, 9999.f);
                avatar.setImageBitmap(bitmap);
            }

        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(MainActivity.this, "Float action button Clicked!", Toast.LENGTH_LONG).show();
                /*
                Snackbar.make(contentView, "I am snackbar. a very very very very very very very very very very very very very very very very long snackbar.", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // do something.
                            }
                        }).show();
                */
            }
        });

        /*
        // adapter and recyclerview
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
        final List<ContactList.ContactModel> list = ContactList.generateSampleList();
        final ContactList.ContactsAdapter adapter = new ContactList().new ContactsAdapter(list);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        */



        // render default fragment
        String tag = "frag_" + R.id.navi_home;
        Fragment frag = new HomeFragment();
        renderFragment(frag, tag);
//        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, frag, tag).commit();

        // 檢查有沒有寫檔案權限
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "i have not Manifest.permission.WRITE_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        // 檢查能不能讀檔案權限
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "i have not Manifest.permission.READ_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        // check READ_PHONE_STATE
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "i have not Manifest.permission.READ_PHONE_STATE");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }

        // init firebase
        System.out.println("1111");
        initFirebase();
        System.out.println("222");
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */


    private void renderFragment(MenuItem item) {
        String tag = "frag_" + item.getItemId();
        Fragment existFrag = getSupportFragmentManager().findFragmentByTag(tag);
        Fragment frag = null;

        // TODO: NOTE ===== new menu item, code here =====
        if (existFrag == null && item.getItemId() == R.id.navi_home) {
            frag = new HomeFragment();
//        } else if (existFrag == null && item.getItemId() == R.id.navi_wevideo) {
//            frag = new WeVideoFragment();
        } else if (existFrag == null && item.getItemId() == R.id.navi_ship) {
            frag = new ShipFragment();
        }  else if (existFrag == null && item.getItemId() == R.id.navi_zookeeper) {
            frag = new ZookeeperFragment();
        }  else if (existFrag == null && item.getItemId() == R.id.navi_dev) {
            frag = new DevFragment();
        }
        renderFragment(frag, tag);
//        int bgc = 0xffff4444;
//        CollapsingToolbarLayout tbl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//        tbl.setContentScrimColor(bgc);
    }


    public void renderFragment(Fragment frag, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        View view = findViewById(R.id.drawer_layout);
        renderFragment(frag, tag, manager, view);
    }

    public static void renderFragment(Fragment frag, String tag, FragmentManager manager, View view) {
        View contentLayout = view.findViewById(R.id.content_layout);
        if (contentLayout != null) {
            contentLayout.setVisibility(View.VISIBLE);
        }
        View contentReplaceLayout = view.findViewById(R.id.content_replace_layout);
        if (contentReplaceLayout != null) {
            contentReplaceLayout.setVisibility(View.INVISIBLE);
        }
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment existFrag = manager.findFragmentByTag(tag);

        // hide all
        List<Fragment> list = manager.getFragments();
        if (list != null) {
            for (Fragment it: manager.getFragments()) {
                if (it != null && !it.isHidden()) {
                    transaction.hide(it);
                }
            }
        }

        // show target
        if (existFrag == null && frag != null) { // got new frag
            transaction.add(R.id.content_layout, frag, tag).commit();
        //} else if (existFrag != null && frag != null) { // replace it
        //    transaction.replace(R.id.content_layout, frag, tag).commit();
        } else if (existFrag != null) { // got old frag
            transaction.show(existFrag).commit();
        } else { // 沒有新建的時候 顯示
            Snackbar.make(view, "TBD: tag: " + tag, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void replaceFragment(Fragment frag, FragmentManager manager, View view) {
        View contentLayout = view.findViewById(R.id.content_layout);
        if (contentLayout != null) {
            contentLayout.setVisibility(View.INVISIBLE);
        }
        View contentReplaceLayout = view.findViewById(R.id.content_replace_layout);
        if (contentReplaceLayout != null) {
            contentReplaceLayout.setVisibility(View.VISIBLE);
        }
        manager.beginTransaction().replace(R.id.content_replace_layout, frag).commit();
    }



    // ===== signin =====
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;


    private void initFirebase() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed: " + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // firebase auth
        mAuth = FirebaseAuth.getInstance();

        NavigationView navi = (NavigationView) findViewById(R.id.navigation_view);
        navi.getMenu().add("AAAAAAA");
        navi.getMenu().addSubMenu("LALALA");
        System.out.println("LALALA");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }



    private void updateUI(FirebaseUser user) {
////        hideProgressDialog();
//        if (user != null) {
////            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
////            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//            mStatusTextView.setText(user.getEmail());
//            mDetailTextView.setText(user.getUid());
//
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setText("sign up");
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if (result.isSuccess()) {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = result.getSignInAccount();
//                firebaseAuthWithGoogle(account);
//            } else {
//                // Google Sign In failed, update UI appropriately
//                // [START_EXCLUDE]
//                updateUI(null);
//                // [END_EXCLUDE]
//            }
//        }
    }
}
