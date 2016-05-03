package fun.wxy.annoy_o_tron;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

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
                // TODO: 應該放到 drawer 的 onDraw 之類的地方去 不過不知道怎麼寫
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
        HomeFragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, fragment).commit();




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
        Fragment frag = null;
        int bgc = 0x0;
        if (item.getItemId() == R.id.navi_home) {
            frag = new HomeFragment();
            bgc = 0x0;
        } else if (item.getItemId() == R.id.navi_ship) {
            frag = new ShipFragment();
            bgc = 0xffff4444;
        }

        if (frag != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, frag).commit();
            CollapsingToolbarLayout tbl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            tbl.setContentScrimColor(bgc);
        } else {
            Toast.makeText(MainActivity.this, item.getTitle() + " pressed", Toast.LENGTH_LONG).show();
        }
    }
}
