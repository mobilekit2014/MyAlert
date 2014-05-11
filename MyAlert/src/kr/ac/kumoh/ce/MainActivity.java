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
	
    private LocationManager manager;  // ��ġ������
    private IntentReceiver intentReceiver;  // ��ε�ĳ��Ʈ ������
    ArrayList listPendingIntent;  // �������Ʈ�� ����� ����Ʈ
 
    String intentKey = "posAlert";  // ��ε�ĳ��Ʈ ������ �ĺ�Ű
    
    TextView textAlertPos1;
    TextView textAlertPos2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        // ��ġ ������ ����
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listPendingIntent = new ArrayList();
        
        textAlertPos1 = (TextView) findViewById(R.id.textAlertPos1);
        textAlertPos2 = (TextView) findViewById(R.id.textAlertPos2);
     
        // ��ư �̺�Ʈ ó��
        Button btnStartAlert = (Button) findViewById(R.id.btnStart);
        btnStartAlert.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int count = 2;  // �ִ� ��� ��
                
                // �溸 ���
                addAlert(1001, 35.8369861, 128.568867, 200, -1);
                addAlert(1002, 38.222222, 128.222222, 200, -1);
 
                // ��� ��ġ ���
                textAlertPos1.setText("��ġ 1 : 35.8369861, 128.568867");
                textAlertPos2.setText("��ġ 2 : 38.222222, 128.222222");
 
                // ������ ��ü ����, ������ �ĺ�Ű �̿�
                intentReceiver = new IntentReceiver(intentKey);
                
                // ����Ʈ ���͸� �̿��Ͽ� ���, �溸�� �� �����ڿ��Ը� ������ ��
                registerReceiver(intentReceiver, intentReceiver.getFilter());
 
                Toast.makeText(getApplicationContext(), count + "�� ������ �溸 ���", 1000).show();
            }
        });
 
        Button btnStopAlert = (Button) findViewById(R.id.btnStop);
        btnStopAlert.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                clearAlert();
                // ��ġ ��� ����
                textAlertPos1.setText("��ġ 1 :");
                textAlertPos2.setText("��ġ 2 :");
                Toast.makeText(getApplicationContext(), "��ϵ� �溸 ����", 1000).show();
            }
        });

    }
  
    // �溸 ��ġ ���� ���
    private void addAlert(int id, double latitude, double longitude, float radius, long expiration) {
    	
    	// ��ġ ������ ������ ������ ����Ʈ ����
        Intent intentAlertPos = new Intent(intentKey);
        intentAlertPos.putExtra("id", id);
        intentAlertPos.putExtra("latitude", latitude);
        intentAlertPos.putExtra("longitude", longitude);
        
        // �������Ʈ�� �̿��� ��ε�ĳ��Ʈ ������ ����
        PendingIntent pIntent = PendingIntent.getBroadcast(this, id, intentAlertPos, PendingIntent.FLAG_CANCEL_CURRENT);
        
        // ��ġ�����ڿ��� �溸 ��ġ ���
        manager.addProximityAlert(latitude, longitude, radius, expiration, pIntent);
        
        // ����Ʈ�� �߰�
        listPendingIntent.add(pIntent);
    }
 
    // ��� ���� ����
    private void clearAlert() {
        if (listPendingIntent != null) {
            for (int i = 0; i < listPendingIntent.size(); i++) {
                PendingIntent curIntent = (PendingIntent) listPendingIntent.get(i);
                manager.removeProximityAlert(curIntent);  // �溸 ����
                listPendingIntent.remove(i);              // ����Ʈ ����
            }
        }
        
        // ������ ����
        if (intentReceiver != null) {
            unregisterReceiver(intentReceiver);
            intentReceiver = null;
        }
    }
 
    // ��ε�ĳ��Ʈ ������ ����
    private class IntentReceiver extends BroadcastReceiver {
 
        private String actionKey;
        private Intent receivedIntent;
        
        // ������
        public IntentReceiver(String actionkey) {
            actionKey = actionkey;
            receivedIntent = null;
        }
        
        // ������ ��Ͽ� ���� ����Ʈ ���� ����
        public IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter(actionKey);
            return filter;
        }
 
        
         // �޾��� �� ȣ��Ǵ� �޼ҵ�
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                receivedIntent = intent;
 
                //int id = intent.getIntExtra("id", 0);
                double latitude = intent.getDoubleExtra("latitude", 0.0D);
                double longitude = intent.getDoubleExtra("longitude", 0.0D);
 
                Toast.makeText(context, "���� ��ġ : " + latitude + ", " + longitude, 2000).show();
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
