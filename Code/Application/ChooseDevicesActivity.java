package com.example.distributediss;




import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ChooseDevicesActivity extends Activity 
{
	static boolean allFound = false;//true if all devices were found false otherwise
	
	static boolean first = true;

	
	
	Button b;
	ProgressDialog p;
	AlertDialog.Builder adb;
	
	
	ArrayAdapter<String> adapter;
	ListView listview;
	
	private class GetDataTask extends AsyncTask<Void, Void, Integer> 
	{
		@Override
		protected Integer doInBackground(Void... params) 
		{         
			//do all your background tasks
			Log.d("In doinbackground", "made it to doInBackground");
			
			
			searchHere();//searching algorithm
			
			return 1;
        }
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			Log.d("In onPostExecute", "started Finishing up async");
			
			//finish up ( or close the progressbar )
			p.dismiss();

			//propagate device list
			
			listview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);	
			
			//Core.getDeviceList()
			
			//this, android.R.layout.simple_list_item_multiple_choice,
			
			listview.setAdapter(adapter); 	

			
			
			
			adapter.notifyDataSetChanged();	
			
			
			
			
			//test if search timed out or finished
			if(ChooseDevicesActivity.allFound)//means search exited with all devices found
			{
				
				
				
			}
			else//means timeout occurred alert user not all devices were found
			{				
				adb = new AlertDialog.Builder(ChooseDevicesActivity.this);
				adb.setTitle("Notice Search Timeout!");
				adb.setIcon(R.drawable.ic_dialog_time);
				adb.setMessage("Not all Devices were Detected!\n\nSome devices may be offline.");
				adb.setPositiveButton("Ok", null);
				adb.show();
			}
			super.onPostExecute(result);
			
			Log.d("In onPostExecute", "end");
        }
    }
	
	
	
	@Override
	public void onBackPressed() 
	{
		ChooseDevicesActivity.first = true;
		
		Intent intent = new Intent(ChooseDevicesActivity.this, MainScreenActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    
	    startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_choose_devices);

			
			
			
			Log.d("In onCreate", "start"); 
			
			
			
			adapter = new ArrayAdapter<String>(ChooseDevicesActivity.this,
					android.R.layout.simple_list_item_multiple_choice,
					MainScreenActivity.mD.addyList.getFullHostList());
	
			listview = (ListView) findViewById(R.id.listView1);		
			
				
			
			
				
				
			Log.d("In onCreate", "before item click listener code");	
			listview.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					//view is check view
					
					
					try
					{
						//boolean[] bob = {true,					
		//boolean[] isDeviceConnected = MainScreenActivity.mD.getConnectedList();
						
						
						
						if(!MainScreenActivity.mD.getConnectionStatus(position))
						{
							adb = new AlertDialog.Builder(ChooseDevicesActivity.this);
							adb.setTitle("Notice");
							adb.setIcon(android.R.drawable.ic_delete);
							adb.setMessage("Device Not Available");
							adb.setPositiveButton("Ok", null);
							adb.show(); 
							
							listview.setItemChecked(position, false);
							adapter.notifyDataSetChanged();
						}
						else
						{
						
							listview.setItemChecked(position, (listview.isItemChecked(position)));
			
							if (!listview.isItemChecked(position)) 
						    {
								Toast.makeText(ChooseDevicesActivity.this, "Not Selected", Toast.LENGTH_SHORT).show();
						    } 
						    else 
						    {
						    	Toast.makeText(ChooseDevicesActivity.this, "Selected", Toast.LENGTH_SHORT).show();
						    }
							
							adapter.notifyDataSetChanged();
						}
					}
					catch(Exception ex)
					{
						Log.e("In listener", "SHIT FUCKED UP");
						ex.printStackTrace();
						
						terminate();
						
					}
			    }
			});
			
			Log.d("In onCreate", "end on Create");
		}
		catch(Exception ex)
		{
			Log.e("In onCreate", "SHIT FUCK DAMN IT BROKE");
			Log.e("In onCreate", "" + ex.toString());
			ex.printStackTrace();
			
			terminate();
			
		}
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		Log.d("In onStart", "start");
		
		if(ChooseDevicesActivity.first)
		{
			Log.d("In onStart", "define b");
			b = (Button) findViewById(R.id.button2);
			Log.d("In onStart", "about to click hidden button");
			b.performClick();
			Log.d("In onStart", "after hidden click");
			ChooseDevicesActivity.first = false;
		}
		
		
		Log.d("In onStart", "end");
	}
	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d("In onPause", "start");
		
		
		
		Log.d("In onPause", "end");
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d("In onResume", "start");
		
		
		Log.d("In onResume", "end");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_choose_devices, menu);
		return true;
	}

	public void finishClick(View view)
	{
		String userDefinedHosts = new String("");
		
		for(int p = 0; p < MainScreenActivity.mD.addyList.getFullHostList().length; p++)
		{
			if(listview.isItemChecked(p))
			{
				if(userDefinedHosts.isEmpty())
				{
				
					userDefinedHosts += "" + adapter.getItem(p).toString();
				}
				else
				{
					userDefinedHosts += ":" + adapter.getItem(p).toString();
				}
			}
		}
		if(userDefinedHosts.isEmpty())
		{
			MainScreenActivity.setDevicesChoosen(false);
		}
		else
		{
			MainScreenActivity.setDevicesChoosen(true);
			
			//for testing host list
			MainScreenActivity.setUserDefinedHosts(userDefinedHosts);
		}
		
		Intent intent = new Intent(ChooseDevicesActivity.this, MainScreenActivity.class);
	    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    
	    startActivity(intent);
		
	}
	
	
	
	
	
	
	


	
	
	
	
	public void searchHere()
	{
		
		
		//this is for testing DELETE afterward  every other device is "discovered"
		
		for(int apple = 0; apple < Core.getDeviceList().size(); apple += 2)
		{
			Core.getDeviceList().get(apple).setAvailable(false);
		}
		
		//------------------------------------
		
		
		
		
		
		//start search here
		
		
		
		
		
		//here we need to decide if device is discovered
		
		
		
		
		//Log.d("In searchHere", "before system sleeps for 1 seconds");
		//android.os.SystemClock.sleep(1000);//search emulation
		
		
		//Log.d("In searchHere", "after system sleeps for 1 seconds");
		
		while(!MainScreenActivity.mD.good)
		{
			
		}
		
		ChooseDevicesActivity.allFound = true;
		for(int pears = 0; pears < Core.getDeviceList().size(); pears++)
		{
			if(Core.getDeviceList().get(pears).isAvailable())//device was discovered
			{
				
			}
			else
			{
				ChooseDevicesActivity.allFound = false;
			}
			
		}
		
		Log.d("In searchHere", "End Search");
	}
	
	public void hiddenClick(View view)
	{
		Log.d("In hidden Click", "start hidden click");
		
		//start Async task
		WifiManager wifimanager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
		Log.d("In hiddenClick", "after WifiManager init");
		
		if (!(wifimanager.isWifiEnabled()))//if wifi is not enabled
		{
			Log.d("In hiddenClick", "If Wifi-Disabled");
			
			adb = new AlertDialog.Builder(ChooseDevicesActivity.this);
			adb.setTitle("Notice");
			adb.setIcon(android.R.drawable.ic_delete);
			adb.setMessage("Wifi is not enabled!  Please Enable Wifi.");
			adb.setPositiveButton("Ok", null);
			adb.show(); 
		}
		else//wifi is enabled please continue
		{
			Log.d("In hiddenClick", "If Wifi-Enabled");
			
			p = new ProgressDialog(ChooseDevicesActivity.this);
			p.setCancelable(true);
			p.setMessage("Searching...");
			p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			p.show();
			
			Log.d("In hiddenClick", "after progress dialog is shown");
			
			new GetDataTask().execute();
			Log.d("In hiddenClick", "end hiddenClick");
		}
		//----------------
		
		
		
		
		
	}
	
	
	public void terminate()
	{
		Log.i("myid","terminated!!");
		super.onDestroy();
		this.finish();
	}
}
