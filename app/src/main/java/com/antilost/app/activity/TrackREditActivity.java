package com.antilost.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.antilost.app.R;
import com.antilost.app.model.TrackR;
import com.antilost.app.prefs.PrefsManager;
import com.antilost.app.service.BluetoothLeService;

public class TrackREditActivity extends Activity implements View.OnClickListener {

    public static final int REQUEST_CODE_TAKE_PHOTO = 1;
    public static final int REQUEST_CODE_CHOOSE_PICTURE = 2;

    public static final String BLUETOOTH_ADDRESS_BUNDLE_KEY = "bluetooth_address_key";
    private static final String LOG_TAG = "TrackREditActivity";
    private AlertDialog mImageSourceDialog;
    private String mBluetoothDeviceAddress;
    private EditText mTrackRName;
    private PrefsManager mPrefs;
    private TrackR mTrack;
    private Resources mResource;

    public static int[] TypeIds = {
            R.id.key,
            R.id.wallet,
            R.id.bag,
            R.id.computer,
            R.id.pet,
            R.id.car,
            R.id.child,
            R.id.other
    };

    public static int[] DrawableIds = {
            R.drawable.key,
            R.drawable.wallet,
            R.drawable.bag,
            R.drawable.computer,
            R.drawable.pet,
            R.drawable.car,
            R.drawable.child,
            R.drawable.other
    };

    private int mPositionSelected;
    private View.OnClickListener mTypesIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = positionOfView(v);
            if(position != -1) {
                mPositionSelected = position;
            }
            int drawableId = DrawableIds[mPositionSelected];
            mImageView.setImageResource(drawableId);
            mTrackRName.setText(mTypeNames[mPositionSelected]);
        }

        private int positionOfView(View v) {
            int id = v.getId();

            for(int i = 0; i < TypeIds.length; i++) {
                if(TypeIds[i] == id) {
                    return i;
                }
            }
            return -1;
        }
    };
    private ImageView mImageView;
    private String[] mTypeNames;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_redit);
        mBluetoothDeviceAddress = getIntent().getStringExtra(BLUETOOTH_ADDRESS_BUNDLE_KEY);
        if(TextUtils.isEmpty(mBluetoothDeviceAddress)) {
            finish();
            return;
        }
        findViewById(R.id.changeImage).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnOK).setOnClickListener(this);

        mTrackRName = (EditText) findViewById(R.id.track_r_type_name);
        mPrefs = PrefsManager.singleInstance(this);
        mTrack = mPrefs.getTrack(mBluetoothDeviceAddress);
        if(mTrack == null) {
            mTrack = new TrackR();
            mTrack.address = mBluetoothDeviceAddress;
        }
        mResource = getResources();
        mImageView = (ImageView) findViewById(R.id.centerLargeImage);
        mImageView.setImageResource(DrawableIds[mTrack.type]);

        mTypeNames = getResources().getStringArray(R.array.default_type_names);
        if(!TextUtils.isEmpty(mTrack.name)) {
            mTrackRName.setText(mTrack.name);
        } else {
            mTrackRName.setText(mTypeNames[mTrack.type]);
        }
        for(int id: TypeIds) {
            findViewById(id).setOnClickListener(mTypesIconClickListener);
        }

        startService(new Intent(this, BluetoothLeService.class));
    }



    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.changeImage:
                showImageSourceDialog();
                break;
            case R.id.takePhoto:
                Toast.makeText(this, "Take Photo", Toast.LENGTH_LONG).show();
                break;
            case R.id.choosePicture:
                Toast.makeText(this, "Choose Picture", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnOK:
                saveTrackRSetting();
                finish();
                break;
        }
    }

    private void saveTrackRSetting() {
        String name = mTrackRName.getText().toString();
        if(!TextUtils.isEmpty(name)) {
            mTrack.name = name;
        }

        mTrack.type = mPositionSelected;
        mPrefs.addTrackIds(mBluetoothDeviceAddress);
        mPrefs.saveTrack(mBluetoothDeviceAddress, mTrack);

    }


    private void showImageSourceDialog() {
        if(mImageSourceDialog != null) {
            mImageSourceDialog.show();
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.custom_photo_source_dialog, null);
        b.setView(customView);
        b.setTitle(getString(R.string.change_image));
        customView.findViewById(R.id.takePhoto).setOnClickListener(this);
        customView.findViewById(R.id.choosePicture).setOnClickListener(this);
        b.setNegativeButton(R.string.cancel, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mImageSourceDialog.dismiss();
            }
        });

        mImageSourceDialog = b.create();
        mImageSourceDialog.show();
    }
}
