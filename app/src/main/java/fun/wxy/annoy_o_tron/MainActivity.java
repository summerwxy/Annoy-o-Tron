package fun.wxy.annoy_o_tron;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import fun.wxy.annoy_o_tron.utils.U;


// @EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

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
        } else if (existFrag == null && item.getItemId() == R.id.navi_ship) {
            frag = new ShipFragment();
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

}
