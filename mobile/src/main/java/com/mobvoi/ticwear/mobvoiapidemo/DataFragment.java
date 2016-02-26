package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.PutDataMapRequest;
import com.mobvoi.android.wearable.PutDataRequest;
import com.mobvoi.android.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DataFragment extends Fragment implements View.OnClickListener,
        MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener {

    private static final String TAG = "DataFrament";
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String COUNT_PATH = "/count";
    private static final String IMAGE_PATH = "/image";
    private static final String IMAGE_KEY = "photo";
    private static final String COUNT_KEY = "count";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private MobvoiApiClient mMobvoiApiClient;
    private boolean mResolvingError = false;
    private boolean mCameraSupported = false;
    private ListView mDataItemList;
    private Button mTakePhotoBtn;
    private Button mSendPhotoBtn;
    private ImageView mThumbView;
    private Bitmap mImageBitmap;
    private View mStartActivityBtn;
    private DataItemAdapter mDataItemListAdapter;
    private Handler mHandler;
    // Send DataItems.
    private ScheduledExecutorService mGeneratorExecutor;
    private ScheduledFuture<?> mDataItemGeneratorFuture;
    private NodeApi.NodeListener mNodeListener;
    private MessageApi.MessageListener mMessageListener;
    private DataApi.DataListener mDataListener;

    /**
     * Builds an {@link com.mobvoi.android.wearable.Asset} from a bitmap. The image that we get
     * back from the camera in "data" is a thumbnail size. Typically, your image should not exceed
     * 320x320 and if you want to have zoom and parallax effect in your app, limit the size of your
     * image to 640x400. Resize your image before transferring to your wearable device.
     */
    private static Asset toAsset(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_data, container, false);
        bindViews(v);
        init();
        return v;
    }

    private void init() {
        mHandler = new Handler();
        mCameraSupported = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        // Stores DataItems received by the local broadcaster or from the paired watch.
        mDataItemListAdapter = new DataItemAdapter(getActivity(), android.R.layout.simple_list_item_1);
        mDataItemList.setAdapter(mDataItemListAdapter);

        mGeneratorExecutor = new ScheduledThreadPoolExecutor(1);
        mNodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(final Node peer) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDataItemListAdapter.add(new Event("Connected", peer.toString()));
                    }
                });
            }

            @Override
            public void onPeerDisconnected(final Node peer) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDataItemListAdapter.add(new Event("Disconnected", peer.toString()));
                    }
                });
            }
        };
        mMessageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(final MessageEvent messageEvent) {
                Log.d(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
                        .getRequestId() + " " + messageEvent.getPath());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));
                    }
                });
            }
        };
        mDataListener = new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {
                Log.d(TAG, "onDataChanged: " + dataEvents);
                final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
                dataEvents.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataEvent event : events) {
                            if (event.getType() == DataEvent.TYPE_CHANGED) {
                                mDataItemListAdapter.add(
                                        new Event("DataItem Changed", event.getDataItem().toString()));
                            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                                mDataItemListAdapter.add(
                                        new Event("DataItem Deleted", event.getDataItem().toString()));
                            }
                        }
                    }
                });
            }
        };
        mMobvoiApiClient = new MobvoiApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void bindViews(View v) {
        mTakePhotoBtn = (Button) v.findViewById(R.id.takePhoto);
        mSendPhotoBtn = (Button) v.findViewById(R.id.sendPhoto);

        // Shows the image received from the handset
        mThumbView = (ImageView) v.findViewById(R.id.imageView);
        mDataItemList = (ListView) v.findViewById(R.id.data_item_list);

        mStartActivityBtn = v.findViewById(R.id.start_wearable_activity);

        mTakePhotoBtn.setOnClickListener(this);
        mSendPhotoBtn.setOnClickListener(this);
        mStartActivityBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");
            mThumbView.setImageBitmap(mImageBitmap);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataItemGeneratorFuture = mGeneratorExecutor.scheduleWithFixedDelay(
                new DataItemGenerator(), 1, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDataItemGeneratorFuture.cancel(true);
        /* mayInterruptIfRunning */
    }

    @Override
    public void onStop() {
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mMobvoiApiClient, mDataListener);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, mMessageListener);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, mNodeListener);
            mMobvoiApiClient.disconnect();
        }
        super.onStop();
    }

    @Override //ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        mResolvingError = false;
        mStartActivityBtn.setEnabled(true);
        mSendPhotoBtn.setEnabled(mCameraSupported);
        Wearable.DataApi.addListener(mMobvoiApiClient, mDataListener);
        Wearable.MessageApi.addListener(mMobvoiApiClient, mMessageListener);
        Wearable.NodeApi.addListener(mMobvoiApiClient, mNodeListener);
    }

    @Override //ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "suspended!");
        mStartActivityBtn.setEnabled(false);
        mSendPhotoBtn.setEnabled(false);
    }

    @Override //OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Failed");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mMobvoiApiClient.connect();
            }
        } else {
            mResolvingError = false;
            mStartActivityBtn.setEnabled(false);
            mSendPhotoBtn.setEnabled(false);
            Wearable.DataApi.removeListener(mMobvoiApiClient, mDataListener);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, mMessageListener);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, mNodeListener);
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mMobvoiApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private void sendStartActivityMessage(String node) {
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    /**
     * Dispatches an {@link Intent} to take a photo. Result will be returned back
     * in onActivityResult().
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Sends the asset that was created form the photo we took by adding it to the Data Item store.
     */
    private void sendPhoto(Asset asset) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(IMAGE_PATH);
        dataMap.getDataMap().putAsset(IMAGE_KEY, asset);
        dataMap.getDataMap().putLong("time", new Date().getTime());
        PutDataRequest request = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(mMobvoiApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.d(TAG, "Sending image was successful: " + dataItemResult.getStatus()
                                .isSuccess());
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto: {
                dispatchTakePictureIntent();
                break;
            }
            case R.id.sendPhoto: {
                if (null != mImageBitmap && mMobvoiApiClient.isConnected()) {
                    sendPhoto(toAsset(mImageBitmap));
                }
                break;
            }
            case R.id.start_wearable_activity: {
                Log.d(TAG, "Generating RPC");
                // Trigger an AsyncTask that will query for a list of connected nodes and send a
                // "start-activity" message to each connected node.
                new StartWearableActivityTask().execute();
                break;
            }
        }
    }

    private static class DataItemAdapter extends ArrayAdapter<Event> {

        private final Context mContext;

        public DataItemAdapter(Context context, int unusedResource) {
            super(context, unusedResource);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.two_line_list_item, null);
                convertView.setTag(holder);
                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Event event = getItem(position);
            holder.text1.setText(event.title);
            holder.text2.setText(event.text);
            return convertView;
        }

        private class ViewHolder {

            TextView text1;
            TextView text2;
        }
    }

    private class Event {

        String title;
        String text;

        public Event(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    /**
     * Generates a DataItem based on an incrementing count.
     */
    private class DataItemGenerator implements Runnable {

        private int count = 0;

        @Override
        public void run() {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(COUNT_PATH);
            putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Log.d(TAG, "Generating DataItem: " + request);
            if (!mMobvoiApiClient.isConnected()) {
                return;
            }
            Wearable.DataApi.putDataItem(mMobvoiApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                Log.e(TAG, "ERROR: failed to putDataItem, status code: "
                                        + dataItemResult.getStatus().getStatusCode());
                            }
                        }
                    });
        }
    }
}