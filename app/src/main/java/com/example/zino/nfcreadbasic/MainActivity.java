/*
    1.이 앱의 실험 목표
      (1)인턴트 필터에 의해 읽혀진 데이터는 onCreate에서 확인할 수 있는가?
          - 확인이 가능하다

    1.결론
       시스템은 NFC 태그를 읽으면, 그 정보를 Intent에 담아서 브로드케스팅한다는 것을 알 수 있다.

    3.문제점
        인텐트 필터에 의해 시스템이 우리앱의 MainActivity를 호출하게 되는데, 이때 매번 호출하게되면
        매번 새로운 액티비티가 생성된다.. 즉 현재 화면의 유지가 될 수 없다!!

    4.NFC 태그 읽기처리 전에 알아둘 사항
        (1) 하나의 NFC Tag는 하나이상의 NdefMessage 로 이루어져 있고, NdefMessage는 다시
              한개 이상의 NdefRecord로 되어 있다. 따라서 실제 데이터를 담을 곳은 바로  NdefRecord 이다
 */
package com.example.zino.nfcreadbasic;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getName();
    NfcAdapter nfcAdapter;
    TextView txt_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_msg =(TextView)findViewById(R.id.txt_msg);

        //NFC 지원 여부 확인
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            showMsg("이 디바이스는 NFC 칩이 없네요");
            return;
        }else{
            showMsg("NFC 칩이 지원되는 디바이스입니다.");
        }

        //시스템으로부터 받은 Tag 정보를 읽어 화면에 출력해본다
        Intent intent = getIntent();
        Log.d(TAG, "MainActivity is  "+this);

        readTag(intent);
    }

    //nfc 읽기
    public void readTag(Intent intent) {
        Parcelable[] message = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        Log.d(TAG, "message is " + message);
        if(message==null){
            showMsg("읽혀진 데이터가 없습니다.");
            return;
        }
        for (int i = 0; i < message.length; i++) {
            NdefMessage ndefMessage = (NdefMessage) message[i];

            NdefRecord[] records = ndefMessage.getRecords();
            for (int a = 0; a < records.length; a++) {
                NdefRecord record = records[a];

                byte[] b = record.getPayload();
                String msg = decode(b);
                txt_msg.setText(msg);
            }

        }
    }

    public String decode(byte[] buf) {
        String strText = "";
        String textEncoding = ((buf[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langCodeLen = buf[0] & 0077;

        try {
            strText = new String(buf, langCodeLen + 1, buf.length - langCodeLen - 1, textEncoding);
        } catch (Exception e) {
            Log.d("tag1", e.toString());
        }
        return strText;
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

}
