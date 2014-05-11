package kr.ac.kumoh.ce;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
    private LocationManager manager;  // 위치관리자
    private IntentReceiver intentReceiver;  // 브로드캐스트 수신자
    ArrayList listPendingIntent;  // 펜딩인텐트가 저장될 리스트
 
    String intentKey = "posAlert";  // 브로드캐스트 수신자 식별키
    
    TextView textAlertPos1;
    TextView textAlertPos2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        // 위치 관리자 생성
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listPendingIntent = new ArrayList();
        
        textAlertPos1 = (TextView) findViewById(R.id.textAlertPos1);
        textAlertPos2 = (TextView) findViewById(R.id.textAlertPos2);
     
        // 버튼 이벤트 처리
        Button btnStartAlert = (Button) findViewById(R.id.btnStart);
        btnStartAlert.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int count = 2;  // 최대 등록 수
                
                // 경보 등록
                addAlert(1001, 35.8369861, 128.568867, 200, -1);
                addAlert(1002, 38.222222, 128.222222, 200, -1);
 
                // 등록 위치 출력
                textAlertPos1.setText("위치 1 : 35.8369861, 128.568867");
                textAlertPos2.setText("위치 2 : 38.222222, 128.222222");
 
                // 수신자 객체 생성, 수신자 식별키 이용
                intentReceiver = new IntentReceiver(intentKey);
                
                // 인텐트 필터를 이용하여 등록, 경보가 이 수신자에게만 오도록 함
                registerReceiver(intentReceiver, intentReceiver.getFilter());
 
                Toast.makeText(getApplicationContext(), count + "개 지점의 경보 등록", 1000).show();
            }
        });
 
        Button btnStopAlert = (Button) findViewById(R.id.btnStop);
        btnStopAlert.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                clearAlert();
                // 위치 출력 삭제
                textAlertPos1.setText("위치 1 :");
                textAlertPos2.setText("위치 2 :");
                Toast.makeText(getApplicationContext(), "등록된 경보 해제", 1000).show();
            }
        });

    }
  
    // 경보 위치 정보 등록
    private void addAlert(int id, double latitude, double longitude, float radius, long expiration) {
    	
    	// 위치 정보를 저장할 별도의 인텐트 생성
        Intent intentAlertPos = new Intent(intentKey);
        intentAlertPos.putExtra("id", id);
        intentAlertPos.putExtra("latitude", latitude);
        intentAlertPos.putExtra("longitude", longitude);
        
        // 펜딩인텐트를 이용해 브로드캐스트 수신자 참조
        PendingIntent pIntent = PendingIntent.getBroadcast(this, id, intentAlertPos, PendingIntent.FLAG_CANCEL_CURRENT);
        
        // 위치관리자에게 경보 위치 등록
        manager.addProximityAlert(latitude, longitude, radius, expiration, pIntent);
        
        // 리스트에 추가
        listPendingIntent.add(pIntent);
    }
 
    // 등록 정보 해제
    private void clearAlert() {
        if (listPendingIntent != null) {
            for (int i = 0; i < listPendingIntent.size(); i++) {
                PendingIntent curIntent = (PendingIntent) listPendingIntent.get(i);
                manager.removeProximityAlert(curIntent);  // 경보 해제
                listPendingIntent.remove(i);              // 리스트 해제
            }
        }
        
        // 수신자 해제
        if (intentReceiver != null) {
            unregisterReceiver(intentReceiver);
            intentReceiver = null;
        }
    }
 
    // 브로드캐스트 수신자 정의
    private class IntentReceiver extends BroadcastReceiver {
 
        private String actionKey;
        private Intent receivedIntent;
        
        // 생성자
        public IntentReceiver(String actionkey) {
            actionKey = actionkey;
            receivedIntent = null;
        }
        
        // 수신자 등록에 사용될 인텐트 필터 생성
        public IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter(actionKey);
            return filter;
        }
 
        
         // 받았을 때 호출되는 메소드
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                receivedIntent = intent;
 
                //int id = intent.getIntExtra("id", 0);
                double latitude = intent.getDoubleExtra("latitude", 0.0D);
                double longitude = intent.getDoubleExtra("longitude", 0.0D);
 
                Toast.makeText(context, "근접 위치 : " + latitude + ", " + longitude, 2000).show();
            }
        }
        
        /*
        public Intent getLastReceivedIntent() {
            return receivedIntent;
        }
 
        public void clearReceivedIntents() {
            receivedIntent = null;
        }
        */
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
}
