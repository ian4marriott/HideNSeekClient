package cs480.hidenseek.client;


import cs480.hidenseek.client.R;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

  private Button send_location;
  private GPSTracker gps;
  private TCPClient mTcpClient;
  private String android_id;
  
  @Override
  public void onResume() {
    super.onResume();
    setContentView(R.layout.activity_main);
    
    send_location = (Button)findViewById(R.id.tag);
    android_id = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    
    new connectTask().execute("");
 
    send_location.setOnClickListener( new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
          double latitude = gps.getLatitude();
          double longitude = gps.getLongitude();
          String message = android_id + ':' + latitude + ':' + longitude;
          if (mTcpClient != null) {
            mTcpClient.sendMessage(message);
          }
        } else {
          gps.showSettingsAlert();
        }
      }
   });
  }
  
  
  public class connectTask extends AsyncTask<String, String, TCPClient> {

    @Override
    protected TCPClient doInBackground(String... message) {

      // we create a TCPClient object and
      mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
        @Override
        // here the messageReceived method is implemented
        public void messageReceived(String message) {
          // this method calls the onProgressUpdate
          publishProgress(message);
        }
      });
      mTcpClient.run();

      return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
      super.onProgressUpdate(values);
      Context context = getApplicationContext();
      CharSequence text = values[0];
      int duration = Toast.LENGTH_SHORT;
      Toast toast = Toast.makeText(context, text, duration);
      toast.show();
      
    }
  }  
  
}


