package com.example.distributediss;



import java.io.File;
import java.io.InputStream;



import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends Activity 
{

	static String userDefinedHosts = "Test of user Defined Hosts";
	
	static boolean devicesChoosen = false;
	
	static MasterDevice mD;
	static boolean firstTime = true;
	AssetManager am;
	
	public static boolean isDevicesChoosen() {
		return devicesChoosen;
	}


	public static void setDevicesChoosen(boolean devicesChoosen) {
		MainScreenActivity.devicesChoosen = devicesChoosen;
	}


	public static String getUserDefinedHosts() {
		return userDefinedHosts;
	}


	public static void setUserDefinedHosts(String userDefinedHosts) {
		MainScreenActivity.userDefinedHosts = userDefinedHosts;
	}

	
	private class GetDataTask extends AsyncTask<Void, Void, Integer> 
	{
		@Override
		protected Integer doInBackground(Void... params) 
		{         
			//do all your background tasks
			
			try
			{
				
				
				
				if(MainScreenActivity.firstTime)
				{
					InputStream is2 = am.open("IpFile.txt");
					InputStream is3 = am.open("seeds.txt");
					InputStream is1 = am.open("HostFile.txt");
					
					mD = new MasterDevice(is1,is2,is3);
					MainScreenActivity.firstTime = false;
				}
				
			}
			catch(Exception e)
			{
				Log.e("In doInBackground", "File NOPE of maybe something else?");
				
				e.printStackTrace();
			}
			
			try
			{
				if(!MainScreenActivity.firstTime)
				{
					mD.setUpHosts(userDefinedHosts);
					
				}
			}
			catch(Exception ex)
			{
				
				ex.printStackTrace();
			}
			
			
			
			
			
			
			return 1;
        }
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			Log.d("In onPostExecute", "started Finishing up async");
			super.onPostExecute(result);
			
			Log.d("In onPostExecute", "end");
        }
    }
	
	
	@Override
	public void onBackPressed() 
	{
		terminate();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		Log.d("In onCreate", "FirstTime? " + MainScreenActivity.firstTime);
		am = MainScreenActivity.this.getAssets();
		
		
		
		
		
		
		
		//use this to test user defined host name
		TextView t;
		t = (TextView) findViewById(R.id.textView1);
		t.setText(userDefinedHosts);
		
		
		
		Button b;
		
		
		if(!isDevicesChoosen())
		{
			b = (Button) findViewById(R.id.button2);
			b.setEnabled(false);
		}
		else
		{
			b = (Button) findViewById(R.id.button2);
			b.setEnabled(true);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		
		//File fileFiles = getFilesDir();
	
		
		Log.d("In Main onStart", "start");
		try
		{
			//background stuff
			
			new GetDataTask().execute();
		}
		catch(Exception e)
		{
			System.out.println(e);
			terminate();
		}
		Log.d("In Main onStart", "end");
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		Log.d("In Main onResume", "start");
		
		
		Log.d("In Main onResume", "end");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_screen, menu);
		return true;
	}
	
	
	
	public void chooseClick(View view)
	{
		Intent intent = new Intent(MainScreenActivity.this, ChooseDevicesActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    
	    startActivity(intent);
	}
	
	public void loginClick(View view)
	{
		Intent intent = new Intent(MainScreenActivity.this, LoginActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    
	    startActivity(intent);
	}
	
	
	
	
	
	
	
	
	
	
	
	public void terminate()
	{
		Log.i("myid","terminated!!");
		super.onDestroy();
		this.finish();
	}

}
