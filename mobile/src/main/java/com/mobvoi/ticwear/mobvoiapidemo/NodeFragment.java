package com.mobvoi.ticwear.mobvoiapidemo;

import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NodeFragment extends Fragment implements View.OnClickListener,
        MobvoiApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private final String TAG = "NodeFragment";

    private MobvoiApiClient mMobvoiApiClient;
    private Button mCheckBtn;
    private TextView mShowTv;
    private boolean mResolvingError = false;
    private NodeApi.NodeListener mNodeListener;
    private String mConnectdNode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_node, container, false);
        mCheckBtn = (Button) v.findViewById(R.id.check);
        mShowTv = (TextView) v.findViewById(R.id.text);
        mCheckBtn.setOnClickListener(this);
        mNodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(final Node peer) {
                Log.d(TAG, "onPeerConnected: " + peer);
            }

            @Override
            public void onPeerDisconnected(final Node peer) {
                Log.d(TAG, "onPeerDisconnected: " + peer);
            }
        };
        mMobvoiApiClient = new MobvoiApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(new MobvoiApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                        Wearable.NodeApi.addListener(mMobvoiApiClient, mNodeListener);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new MobvoiApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (mResolvingError) {
                            // Already attempting to resolve an error.
                            return;
                        } else if (result.hasResolution()) {
                            try {
                                mResolvingError = true;
                                Log.d(TAG, "Failing in Connecting");
                                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                // There was an error with the resolution intent. Try again.
                                mMobvoiApiClient.connect();
                                Log.d(TAG, "Try Again");
                            }
                        } else {
                            mResolvingError = false;
                            Wearable.NodeApi.removeListener(mMobvoiApiClient, mNodeListener);
                        }
                    }
                })
                .build();
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check: {
                new StartCheckTask().execute();
                break;
            }
        }
    }

    @Override //OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                Log.d(TAG, "Failing in Connecting");
                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mMobvoiApiClient.connect();
                Log.d(TAG, "Try Again");
            }
        } else {
            mResolvingError = false;
            Wearable.NodeApi.removeListener(mMobvoiApiClient, mNodeListener);
        }
    }

    private Collection<String> getNodes() {
        Set<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mMobvoiApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private class StartCheckTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                if (node != null) {
                    mConnectdNode = "The connected node is " + node;
                    Log.d(TAG, "The connected node is " + node);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mShowTv.setText(mConnectdNode);
        }
    }

}