import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.i2p.android.router.service.RouterService;
import org.torproject.android.service.OrbotConstants;
import org.torproject.android.service.TorService;

public class MainActivity extends AppCompatActivity {

    private RouterService mRouterService;
    private TorService mTorService;
    private boolean mRouterBound = false;
    private boolean mTorBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicia el servicio de I2P
        Intent i2pIntent = new Intent(getApplicationContext(), RouterService.class);
        bindService(i2pIntent, mI2PConnection, Context.BIND_AUTO_CREATE);

        // Inicia el servicio de Tor
        Intent torIntent = new Intent(getApplicationContext(), TorService.class);
        bindService(torIntent, mTorConnection, Context.BIND_AUTO_CREATE);
    }

    // Conecta al servicio de I2P
    private ServiceConnection mI2PConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RouterService.LocalBinder binder = (RouterService.LocalBinder) service;
            mRouterService = binder.getService();
            mRouterBound = true;

            // Verifica si I2P está conectado
            if (mRouterService.isRouterRunning()) {
                Toast.makeText(MainActivity.this, "I2P está conectado", Toast.LENGTH_SHORT).show();
                // Aquí puedes realizar acciones adicionales, como acceder a la red I2P
            } else {
                Toast.makeText(MainActivity.this, "I2P no está conectado", Toast.LENGTH_SHORT).show();
                // Aquí puedes iniciar I2P si no está conectado
                mRouterService.startRouter();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRouterBound = false;
        }
    };

    // Conecta al servicio de Tor
    private ServiceConnection mTorConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TorService.LocalBinder binder = (TorService.LocalBinder) service;
            mTorService = binder.getService();
            mTorBound = true;

            // Verifica si Tor está conectado
            if (mTorService.getStatus() == OrbotConstants.STATUS_ON) {
                Toast.makeText(MainActivity.this, "Tor está conectado", Toast.LENGTH_SHORT).show();
                // Aquí puedes realizar acciones adicionales, como acceder a la red Tor
            } else {
                Toast.makeText(MainActivity.this, "Tor no está conectado", Toast.LENGTH_SHORT).show();
                // Aquí puedes iniciar Tor si no está conectado
                mTorService.requestStartTor();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTorBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRouterBound) {
            unbindService(mI2PConnection);
            mRouterBound = false;
        }
        if (mTorBound) {
            unbindService(mTorConnection);
            mTorBound = false;
        }
    }
                               }
