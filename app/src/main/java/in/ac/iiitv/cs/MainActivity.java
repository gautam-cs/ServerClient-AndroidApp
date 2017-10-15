package in.ac.iiitv.cs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button clientButton = (Button) findViewById(R.id.client);
        Button serverButton = (Button) findViewById(R.id.server);
        clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  clientIntent = new Intent(getApplicationContext(),ClientActivity.class);
                startActivity(clientIntent);
//                finish();
            }
        });
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  serverIntent = new Intent(getApplicationContext(),ServerActivity.class);
                startActivity(serverIntent);
//                finish();
            }
        });
    }
}
