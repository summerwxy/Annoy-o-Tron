package fun.wxy.annoy_o_tron;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fun.wxy.annoy_o_tron.list.Contact;
import fun.wxy.annoy_o_tron.utils._;


// @EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "fun.wxy.annoy_o_tron.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView contentView = (TextView) findViewById(R.id.content_view);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Toast.makeText(MainActivity.this, item.getTitle() + " pressed", Toast.LENGTH_LONG).show();
                contentView.setText(item.getTitle());

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
                bitmap = _.toSquare(bitmap);
                bitmap = _.scaleBitmap(bitmap, 160);
                bitmap = _.roundCorner(bitmap, 9999.f);
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
                Snackbar.make(contentView, "I am snackbar. a very very very very very very very very very very very very very very very very long snackbar.", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // do something.
                            }
                        }).show();
            }
        });

        // adapter and recyclerview
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
        final List<Contact.ContactModel> list = Contact.generateSampleList();
        final Contact.ContactsAdapter adapter = new Contact().new ContactsAdapter(list);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        // add and del button
        Button addBtn = (Button) findViewById(R.id.add_btn);
        Button delBtn = (Button) findViewById(R.id.del_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact.ContactModel contact = new Contact().new ContactModel();
                contact.setName("Lin Yu Wei");
                list.add(3, contact);
                adapter.notifyItemInserted(3);
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(0);
                adapter.notifyItemRemoved(0);
            }
        });

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String msg = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
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
}
