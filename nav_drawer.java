package apps.finance.simple.CryptoCalculator;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class nav_drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ApiClass api = new ApiClass();
        if (api.isNetworkAvailable(this) == false) {
            alert();
        } else {
            //add Combo Elements
            final String[] arrayCoin = api.listCoins();
            Spinner combo1 = (Spinner) findViewById(R.id.combo1);
            Spinner combo2 = (Spinner) findViewById(R.id.combo2);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, arrayCoin);
            combo1.setAdapter(adapter);
            combo2.setAdapter(adapter);


            //button function
            Button btnGetPrice = (Button) findViewById(R.id.btnGetPrice);
            btnGetPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiClass api = new ApiClass();
                    if (api.isNetworkAvailable(nav_drawer.this) == false) {
                        alert();
                    } else {
                        //items
                        Spinner combo1 = (Spinner) findViewById(R.id.combo1);
                        String item = combo1.getSelectedItem().toString();
                        Spinner combo2 = (Spinner) findViewById(R.id.combo2);
                        String item2 = combo2.getSelectedItem().toString();


                        //prices
                        String coin = item.substring(item.indexOf(")") + 2, item.length());
                        String price = api.getPrice(coin, "USD");
                        String coin2 = item2.substring(item2.indexOf(")") + 2, item2.length());
                        String price2 = api.getPrice(coin2, "USD");

                        //anzahl
                        EditText txtanzahl = (EditText) findViewById(R.id.txtAnzahl);
                        if (txtanzahl.getText().toString().matches("")) {
                            Toast.makeText(nav_drawer.this, "Please enter a number", Toast.LENGTH_SHORT).show();
                        } else {
                            Double anzahl = Double.parseDouble(txtanzahl.getText().toString());

                            double number1 = Double.parseDouble(price);
                            double number2 = Double.parseDouble(price2);
                            double ergebnis = (number1 * anzahl) / number2;

                            TextView text = (TextView) findViewById(R.id.mainText);
                            text.setText(Double.toString(ergebnis));
                        }

                    }
                }
            });


            //changebutton
            Button btnChange = (Button) findViewById(R.id.btnChange);
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Spinner combo1 = (Spinner) findViewById(R.id.combo1);
                    String item = combo1.getSelectedItem().toString();

                    Spinner combo2 = (Spinner) findViewById(R.id.combo2);
                    String item2 = combo2.getSelectedItem().toString();

                    for (int i = 0; i < arrayCoin.length; i++) {
                        if (arrayCoin[i].toString().equals(item)) {
                            combo2.setSelection(i);
                        }
                        if (arrayCoin[i].toString().equals(item2)) {
                            combo1.setSelection(i);
                        }
                    }
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_crypto) {


        } else if (id == R.id.nav_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String sAux = "\nHey! Check this cool new Cryptocurrency Calculator\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id="+this.getPackageName()+"\n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
        }
        else if (id == R.id.nav_fiat) {
            startActivity(new Intent(nav_drawer.this, FiatActivity.class));
        }
        else if(id==R.id.nav_about){
            startActivity(new Intent(nav_drawer.this, AboutActivity.class));
        } else if(id==R.id.nav_donate){
            startActivity(new Intent(nav_drawer.this, Donate.class));
        }
        else if (id == R.id.nav_rate) {
            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void alert(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("No internet connection available.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
