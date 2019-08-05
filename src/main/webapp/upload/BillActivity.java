package com.example.nishal.good_food_final;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nishal.good_food_final.Adapters.LocationTrack;
import com.example.nishal.good_food_final.Model.Order;
import com.example.nishal.good_food_final.Model.Sellers;
import com.example.nishal.good_food_final.Model.Users;
import com.example.nishal.good_food_final.Utils.CheckNetwork;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class BillActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FirebaseDatabase database;
    DatabaseReference ref,refX;
    private FirebaseAuth mAuth;
    Users users;
    TextView total, name, seller, food, qnt,username,useremail,timeBill;
    EditText add, tNo;
    Order orderX;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String userAddress;
    int netTotal;
    ProgressDialog mDialog;

    String sellerID,sellerName;
    private static final int LOCATION_REQUEST_CODE = 9999;
    String newTime;

    private FusedLocationProviderClient client;
    String sellerNameX;
    String curryX;
    String foodName,timeX,selPhone;
    int hours,min;
    LocationTrack locationTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_content);
        if(!CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefX = getSharedPreferences("My pref", MODE_PRIVATE);




        mAuth = FirebaseAuth.getInstance();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{

                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sellerID = preferences.getString("sellerId", null);
        sellerName = preferences.getString("sellerName", null);
        sellerNameX = preferences.getString("sellerName", null);
        selPhone = preferences.getString("sellerNum", null);

        timeX = preferences.getString("time", null);



        locationTrack = new LocationTrack(BillActivity.this,BillActivity.this);

        Calendar now = Calendar.getInstance();
        int hoursx=now.get(Calendar.HOUR_OF_DAY);
        String menuTime1,menuTime2;
        if(11>hoursx){
            menuTime1 ="Breakfast";
            menuTime2 ="breakfast";

        }else if(17>hoursx){
            menuTime1 ="Lunch";
            menuTime2 ="lunch";

        }else{
            menuTime1 ="Dinner";
            menuTime2 ="dinner";

        }

        String timePattern = "hh-mm";
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timePattern);
        String time = simpleTimeFormat.format(new Date());
        String[] billtime = time.split("-");

        hours = Integer.parseInt(billtime[0]);
        min = Integer.parseInt(billtime[1]);
        min = min + 30;

        if(!(timeX.equals(menuTime1)||timeX.equals(menuTime2))){
            if(timeX.equals("Lunch")||timeX.equals("lunch")){
                newTime= "11:30";
            }else{
                newTime= "5:30";
            }
        }
        else if (min < 60) {
            if(min<10){
                newTime = hours + ":0" + min;
            }else {
                newTime = hours + ":" + min;
            }
        }else{
            hours++;
            min=min-60;
            if(min<10){
                newTime = hours + ":0" + min;
            }else {
                newTime = hours + ":" + min;
            }

        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ScrollView scrollView= findViewById(R.id.scrollingViewBill);
        boolean enable = true;



        FirebaseUser user = mAuth.getCurrentUser();
        total = findViewById(R.id.billTotal);

        add = findViewById(R.id.billAdd);
        tNo = findViewById(R.id.billTno);
        seller = findViewById(R.id.billSeller);

        food = findViewById(R.id.billFood);
        qnt = findViewById(R.id.billQnt);

        timeBill = findViewById(R.id.timeBill);
        timeBill.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);


        radioGroup = (RadioGroup) findViewById(R.id.billRadioGroup);

        client = LocationServices.getFusedLocationProviderClient(this);

        int id = radioGroup.getCheckedRadioButtonId();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                if(radioButton.getText().toString().equals("Address")){
                    add.setText(userAddress);
                }else{
                    add.setText("");
                }
            }
        });
        radioButton = findViewById(id);
        String locationX = radioButton.getText().toString();


        displayBill();



        timeBill.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    // TODO Auto-generated method stub


                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(BillActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            newTime = selectedHour+":"+selectedMinute;
                            timeBill.setText( selectedHour + ":" + selectedMinute);
                            hours = selectedHour;
                            min=selectedMinute;
                        }
                    }, hours, min, true);
                    mTimePicker.setTitle("Select Time");

                    mTimePicker.show();

                }

        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    orderNow();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void orderNow() throws InterruptedException {
        String teleNo = tNo.getText().toString();
        String address = add.getText().toString();
        if(teleNo.isEmpty()){
            Toast.makeText(getApplicationContext(),"Fields can't be empty", Toast.LENGTH_SHORT).show();
        }else {
            if(!CheckNetwork.isInternetAvailable(this)) //returns true if internet available
            {
                Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                return;
            }
            mDialog = new ProgressDialog(this);
            mDialog.setMessage("Please wait....");
            mDialog.show();
            int id = radioGroup.getCheckedRadioButtonId();

            radioButton = findViewById(id);
            String locationX = radioButton.getText().toString();


            orderX = new Order();



            orderX.setSellerName(sellerName);
            orderX.setFood(foodName);
            orderX.setQnt(Integer.parseInt(qnt.getText().toString()));
            String patternX = "yy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternX);
            String currentDateTimeString = simpleDateFormat.format(new Date());
            orderX.setTime(timeBill.getText().toString());
            orderX.setDate(currentDateTimeString);
            orderX.setCurry(curryX);
            orderX.setSellerId(sellerID);
            orderX.setTotal(""+netTotal);
            orderX.setOrderStatus("Requested");
            orderX.setRateStatus(false);


            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myRef = database.getReference("Customers/" + FirebaseAuth.getInstance().getUid() + "/History");
            String mGroupId = uniqueId();
            myRef.child(mGroupId).setValue(orderX);
            myRef.child(mGroupId).child("Phone").setValue(tNo.getText().toString());
            myRef.child(mGroupId).child("orderId").setValue(mGroupId);
            System.out.println(netTotal);

            DatabaseReference myRef1 = database.getReference("Sellers/" + sellerID + "/requests").child("requested");
            DatabaseReference myRef2 = database.getReference("History").child(mGroupId);


            if (locationX.equals("Current location")) {


                //TimeUnit.SECONDS.sleep(3);

                final SharedPreferences prefs = getSharedPreferences("My pref", MODE_PRIVATE);


                myRef1.child(mGroupId).child("Food").setValue(foodName);
                myRef1.child(mGroupId).child("Quantity").setValue(qnt.getText().toString());
                myRef1.child(mGroupId).child("Total").setValue(""+netTotal);
                myRef1.child(mGroupId).child("Phone").setValue(tNo.getText().toString());
                myRef1.child(mGroupId).child("Customer").setValue(FirebaseAuth.getInstance().getUid());
                myRef1.child(mGroupId).child("Date").setValue(currentDateTimeString);
                myRef1.child(mGroupId).child("OrderStatus").setValue("request");
                myRef1.child(mGroupId).child("curry").setValue(curryX);
                myRef1.child(mGroupId).child("Time").setValue(timeBill.getText().toString());
                myRef1.child(mGroupId).child("Address").setValue("");
                myRef1.child(mGroupId).child("RateStatus").setValue(false);
                myRef1.child(mGroupId).child("Longitude").setValue(prefs.getFloat("selectedLongitude",0));
                myRef1.child(mGroupId).child("Latitude").setValue(prefs.getFloat("selectedLatitude",0));


                myRef2.child("Food").setValue(foodName);
                myRef2.child("SellerId").setValue(sellerID);
                myRef2.child("SellerNum").setValue(selPhone);
                myRef2.child("CustomerId").setValue(FirebaseAuth.getInstance().getUid());
                myRef2.child("Quantity").setValue(qnt.getText().toString());
                myRef2.child("Total").setValue(""+netTotal);
                myRef2.child("CusPhone").setValue(tNo.getText().toString());
                myRef2.child("Date").setValue(currentDateTimeString);
                myRef2.child("curry").setValue(curryX);
                myRef2.child("Time").setValue(timeBill.getText().toString());
                myRef2.child("Longitude").setValue(prefs.getFloat("selectedLongitude",0));
                myRef2.child("Latitude").setValue(prefs.getFloat("selectedLatitude",0));



            } else if(!address.isEmpty()){

                myRef1.child(mGroupId).child("Address").setValue(add.getText().toString());
                myRef1.child(mGroupId).child("Food").setValue(foodName);
                myRef1.child(mGroupId).child("Time").setValue(timeBill.getText().toString());
                myRef1.child(mGroupId).child("Quantity").setValue(qnt.getText().toString());
                myRef1.child(mGroupId).child("Total").setValue(""+netTotal);
                myRef1.child(mGroupId).child("Phone").setValue(tNo.getText().toString());
                myRef1.child(mGroupId).child("Customer").setValue(FirebaseAuth.getInstance().getUid());
                myRef1.child(mGroupId).child("Date").setValue(currentDateTimeString);
                myRef1.child(mGroupId).child("RateStatus").setValue(false);
                myRef1.child(mGroupId).child("curry").setValue(curryX);
                myRef1.child(mGroupId).child("OrderStatus").setValue("request");
                myRef1.child(mGroupId).child("Longitude").setValue("");
                myRef1.child(mGroupId).child("Latitude").setValue("");

                myRef2.child("Food").setValue(foodName);
                myRef2.child("SellerId").setValue(sellerID);
                myRef2.child("SellerNum").setValue(selPhone);
                myRef2.child("CustomerId").setValue(FirebaseAuth.getInstance().getUid());
                myRef2.child("Quantity").setValue(qnt.getText().toString());
                myRef2.child("Total").setValue(""+netTotal);
                myRef2.child("CusPhone").setValue(tNo.getText().toString());
                myRef2.child("Date").setValue(currentDateTimeString);
                myRef2.child("curry").setValue(curryX);
                myRef2.child("Time").setValue(timeBill.getText().toString());
                myRef2.child("Address").setValue(add.getText().toString());

            }else{
                Toast.makeText(BillActivity.this,"Please fill the address",Toast.LENGTH_SHORT).show();
            }


            Toast.makeText(this, "Order Confirmed", Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BillActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("sellerId", null);
            editor.putString("sellerName",null);
            editor.putString("foodName",null);
            editor.putString("price",null);
            editor.putString("time",null);
            editor.putString("foodId",null);
            editor.commit();

            startActivity(new Intent(BillActivity.this, HomeActivity.class));
            mDialog.dismiss();
            finish();

        }

    }



    private void displayBill() {


        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Customers/"+ FirebaseAuth.getInstance().getUid());

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String qntity=  preferences.getString("quantity", null);
        final String totalX =  preferences.getString("price", null);

        SharedPreferences prefs = getSharedPreferences("My pref", MODE_PRIVATE);

        final double longi = prefs.getFloat("Longitude",0);
        final double lati= prefs.getFloat("Latitude",0);


        System.out.println(lati);
        System.out.println(longi);


        netTotal = Integer.parseInt(qntity)*Integer.parseInt(totalX);
        System.out.println(qntity);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(Users.class);
                seller.setText(sellerNameX);

                System.out.println(preferences.getString("foodName", null));
                System.out.println(preferences.getString("price", null));
                tNo.setText(users.getNum());

                userAddress = users.getAddress();





                timeBill.setText(newTime);
                curryX=preferences.getString("curry", null);
                foodName=preferences.getString("foodName", null);
                food.setMovementMethod(new ScrollingMovementMethod());

                if(!curryX.equals("")){
                    StringBuffer responseText = new StringBuffer();
                    int x=0;

                    final String [] curryNew = curryX.split(",");
                    for(int w=0;w<curryNew.length;w++){
                        if(x!=0){
                            responseText.append(",");
                        }
                        responseText.append("  "+curryNew[w]);
                        x++;
                    }
                    food.setText(foodName+"\n"+responseText);
                }else{
                    food.setText(foodName);
                }

                total.setText("Rs. "+netTotal+".00");
                qnt.setText(qntity.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this,QuantityActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.navProfile) {

            startActivity( new Intent(getApplicationContext(),ProfileActivity.class));

        }else if (id == R.id.navHome) {

            startActivity( new Intent(getApplicationContext(),HomeActivity.class));

        } else if (id == R.id.navHistory) {

            startActivity(new Intent(getApplicationContext(),OrderHistoryActivity.class));

        } else if (id == R.id.navSeller) {

            startActivity(new Intent(getApplicationContext(),SellerDetailsActivity.class));

        } else if (id == R.id.navFav) {

            startActivity(new Intent(getApplicationContext(),FavFoodsActivity.class));

        } else if (id == R.id.navLogout) {

            FirebaseAuth.getInstance().signOut();
            String uid = FirebaseAuth.getInstance().getUid();

            if(uid==null){
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkLogin() {
        // Check if user is signed in (non-null) and update UI accordingly.
        String uid = FirebaseAuth.getInstance().getUid();
        if(uid==null){

            startActivity(new Intent(this,LoginActivity.class));
        }
        //updateUI(currentUser);
    }


    public  String uniqueId(){
        String pattern = "yy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date1 = simpleDateFormat.format(new Date());


        String timePattern = "hh-mm";
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timePattern);
        String time = simpleTimeFormat.format(new Date());
        String[] time1=time.split("-");

        //Toast.makeText(this,"Time"+date1,Toast.LENGTH_SHORT).show();
        System.out.println(date1);
        String[] date2=date1.split("-");
        StringBuffer date3 = new StringBuffer();
        date3.append("GFO");
        date3.append("-");
        date3.append(date2[0]);
        date3.append(date2[1]);
        date3.append(date2[2]);
        date3.append("-");
        date3.append(time1[0]);
        date3.append(time1[1]);
        date3.append("-");
        int random = (int )(Math.random() * 99999 + 10000);
        date3.append(random);
        System.out.println(date3);

        return  date3.toString();

    }
}
