package br.com.vsgdev.leilaodojo.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.models.Auction;
import br.com.vsgdev.leilaodojo.models.Product;
import br.com.vsgdev.leilaodojo.models.User;
import br.com.vsgdev.leilaodojo.utils.JSONConverter;
import br.com.vsgdev.leilaodojo.utils.WebServiceUtils;

public class NewProduct extends AppCompatActivity {

    private static final int REQUEST_CODE_GET_IMAGE_FILE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private int cameraID;
    private Uri pictureUri;
    public EditText nome;
    public EditText descricao;
    public EditText valor;
    private EditText tempoLeilao;
    public Button cadastrar;
    private ImageView iv_camera;
    private FrameLayout preview;
    private CameraPreview mPreview;
    private Camera mCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        nome = (EditText) findViewById(R.id.edt_nome_produto);
        descricao = (EditText) findViewById(R.id.edt_desc_produto);
        valor = (EditText) findViewById(R.id.edt_valor_produto);
        tempoLeilao = (EditText) findViewById(R.id.et_tempo_new_product);
        preview = (FrameLayout) findViewById(R.id.fl_camera_preview_new_product);

        cadastrar = (Button) findViewById(R.id.bt_ok);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = nome.getText().toString();
                String d = descricao.getText().toString();
                float va = Float.parseFloat(valor.getText().toString());
                int tempo = Integer.valueOf(tempoLeilao.getText().toString());
                Product product = new Product(null, n, d, va, tempo);
                final Auction auction = new Auction();
                auction.setCreatedAt(Calendar.getInstance());
                final Calendar endAt = Calendar.getInstance();
                endAt.add(Calendar.HOUR_OF_DAY, tempo);
                auction.setEndAt(endAt);
                auction.setProduct(product);
                final User thisUser = new User();
                thisUser.setDeviceId(JSONConverter.deviceId);
                //TODO XXX completar com demais dados posteriormente
                auction.setOwner(thisUser);
                WebServiceUtils.registerAuction(auction, getApplicationContext());
            }


        });

        iv_camera = (ImageView) findViewById(R.id.iv_button_activity_main);
        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NewProduct.this);
                builder.setTitle("Selecione")
                        .setItems(R.array.selecione, new Dialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int itemIndex) {
                                        switch (itemIndex) {
                                            case 0://from file
                                                pickImage();
                                                break;
                                            case 1://from camera
                                                //startActivity(new Intent(NewPetActivity.this, MainActivity.class));
                                                //prepareCamera();


                                                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                                                    // this device has a camera
                                                    int numberOfCameras = Camera.getNumberOfCameras();
                                                    for (int i = 0; i < numberOfCameras; i++) {
                                                        Camera.CameraInfo info = new Camera.CameraInfo();
                                                        Camera.getCameraInfo(i, info);
                                                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                                            Log.d("NewProduct", "Camera found");
                                                            cameraID = i;
                                                            break;
                                                        }
                                                    }
                                                    mCamera = getCameraInstance();
                                                    mPreview = new CameraPreview(NewProduct.this, mCamera);
                                                    preview.addView(mPreview);

                                                } else {
                                                    // no camera on this device
                                                    Toast.makeText(NewProduct.this, "Não possui camera", Toast.LENGTH_SHORT).show();
                                                }

                                                break;
                                        }
                                    }
                                }

                        );
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void pickImage() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CODE_GET_IMAGE_FILE);
    }

    private Bitmap rotateImage(final Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(cameraID); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            switch (resultCode) {
                //o usuario cancelou a acao (pressionou 'back button', nao tirou a foto, em nosso exemplo)
                case RESULT_CANCELED:
                    File file = new File(pictureUri.getPath());
                    file.delete();
                    break;
                //O usuario tirou uma foto.
                case RESULT_OK:
                    //final ImageView imageView = (ImageView) findViewById(R.id.iv_photo_taken);
                    Bitmap photoTaken = BitmapFactory.decodeFile(pictureUri.getPath());

                    final ExifInterface ei;
                    try {
                        ei = new ExifInterface(pictureUri.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                photoTaken = rotateImage(photoTaken, 90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                photoTaken = rotateImage(photoTaken, 180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                photoTaken = rotateImage(photoTaken, 270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iv_camera.setImageBitmap(photoTaken);
                    break;
            }
        } else {
            if (requestCode == REQUEST_CODE_GET_IMAGE_FILE) {
                //TODO XXX
            }
        }

    }

}