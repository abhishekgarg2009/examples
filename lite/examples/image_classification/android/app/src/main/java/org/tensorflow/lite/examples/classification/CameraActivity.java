/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.classification;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.nio.ByteBuffer;
import java.util.List;
import org.tensorflow.lite.examples.classification.env.ImageUtils;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.storage.Basket;
import org.tensorflow.lite.examples.classification.storage.ClusterMapper;
import org.tensorflow.lite.examples.classification.storage.ItemDetails;
import org.tensorflow.lite.examples.classification.storage.ProductDetails;
import org.tensorflow.lite.examples.classification.storage.SharedPreferenceManager;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private LinearLayout bottomSheetLayout;
  private BottomSheetBehavior<LinearLayout> sheetBehavior;
  protected TextView recognitionTextView,
          recognition1TextView,
          recognition2TextView;
  protected TextView priceView,
          priceView1,
          priceView2;

  protected ImageView itemImage,
          itemImage1,
          itemImage2;

  protected TextView basketPriceView;

  protected ImageView bottomSheetArrowImageView;
  protected ImageView plusImageView, minusImageView;
  protected ImageView plusImageView1, minusImageView1;
  protected ImageView plusImageView2, minusImageView2;
  private TextView itemCountTextView;
  private TextView itemCountTextView1;
  private TextView itemCountTextView2;

  private ItemDetails currentItem;
  private ItemDetails currentItem1;
  private ItemDetails currentItem2;


  private Model model = Model.QUANTIZED_EFFICIENTNET;
  private Device device = Device.CPU;
  private int numThreads = -1;
  private int items1 = -1;
  private int items2 = -1;
  private int items = -1;

  private static boolean initialized = false;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {

    if (!initialized) {
      ProductDetails.populateProductDetails(getApplicationContext());
      initialized = true;
    }
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.tfe_ic_activity_camera);

    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }


    currentItem =  new ItemDetails("popcorn", 150, "pop", "Popcorn", "products");
    currentItem1 = new ItemDetails();
    currentItem2 = new ItemDetails();

    ClusterMapper.addItem(new ItemDetails());
    SharedPreferenceManager.addItem(getApplicationContext(), currentItem);

    ClusterMapper.addItem(new ItemDetails());
    ItemDetails itemDetails1 = new ItemDetails("nachos", 200, "nachos", "Nachos", "products");
    ClusterMapper.addItem(itemDetails1);
    SharedPreferenceManager.addItem(getApplicationContext(), itemDetails1);

    ItemDetails itemDetails2 = new ItemDetails("glass", 20, "glass", "3D glasses", "products");
    ClusterMapper.addItem(itemDetails2);
    SharedPreferenceManager.addItem(getApplicationContext(), itemDetails2);

    itemCountTextView = findViewById(R.id.threads);
    itemCountTextView1 = findViewById(R.id.threads1);
    itemCountTextView2 = findViewById(R.id.threads2);

    itemImage = findViewById(R.id.image);
    itemImage1 = findViewById(R.id.image1);
    itemImage2 = findViewById(R.id.image2);


    plusImageView = findViewById(R.id.plus);
    minusImageView = findViewById(R.id.minus);

    plusImageView1 = findViewById(R.id.plus1);
    minusImageView1 = findViewById(R.id.minus1);

    plusImageView2 = findViewById(R.id.plus2);
    minusImageView2 = findViewById(R.id.minus2);

    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

    basketPriceView = findViewById((R.id.basket));

    sheetBehavior.setHideable(false);

    sheetBehavior.setBottomSheetCallback(
            new BottomSheetBehavior.BottomSheetCallback() {
              @Override
              public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                  case BottomSheetBehavior.STATE_HIDDEN:
                    break;
                  case BottomSheetBehavior.STATE_EXPANDED: {
//                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
                  }
                  break;
                  case BottomSheetBehavior.STATE_COLLAPSED: {
//                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                  }
                  break;
                  case BottomSheetBehavior.STATE_DRAGGING:
                    break;
                  case BottomSheetBehavior.STATE_SETTLING:
                    bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                    break;
                }
              }

              @Override
              public void onSlide(@NonNull View bottomSheet, float slideOffset) {
              }
            });

    recognitionTextView = findViewById(R.id.detected_item);
    recognition1TextView = findViewById(R.id.detected_item1);
    recognition2TextView = findViewById(R.id.detected_item2);

    basketPriceView.setText("₹"+Basket.getBasketValue());

    priceView = findViewById(R.id.price);
    priceView1 = findViewById(R.id.price1);
    priceView2 = findViewById(R.id.price2);

    plusImageView.setOnClickListener(this);
    minusImageView.setOnClickListener(this);

    plusImageView1.setOnClickListener(this);
    minusImageView1.setOnClickListener(this);

    plusImageView2.setOnClickListener(this);
    minusImageView2.setOnClickListener(this);

    if(Basket.getItemIdVsCount().get(currentItem.getId()) != null)
      itemCountTextView.setText(String.valueOf(Basket.getItemIdVsCount().get(currentItem.getId())));

    if(Basket.getItemIdVsCount().get(currentItem2.getId()) != null)
      itemCountTextView2.setText(String.valueOf(Basket.getItemIdVsCount().get(currentItem2.getId())));

    if(Basket.getItemIdVsCount().get(currentItem1.getId()) != null)
      itemCountTextView1.setText(String.valueOf(Basket.getItemIdVsCount().get(currentItem1.getId())));
  }

  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /**
   * Callback for android.hardware.Camera API
   */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
            new Runnable() {
              @Override
              public void run() {
                ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
              }
            };

    postInferenceCallback =
            new Runnable() {
              @Override
              public void run() {
                camera.addCallbackBuffer(bytes);
                isProcessingFrame = false;
              }
            };
    processImage();
  }

  /**
   * Callback for Camera2 API
   */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
              new Runnable() {
                @Override
                public void run() {
                  ImageUtils.convertYUV420ToARGB8888(
                          yuvBytes[0],
                          yuvBytes[1],
                          yuvBytes[2],
                          previewWidth,
                          previewHeight,
                          yRowStride,
                          uvRowStride,
                          uvPixelStride,
                          rgbBytes);
                }
              };

      postInferenceCallback =
              new Runnable() {
                @Override
                public void run() {
                  image.close();
                  isProcessingFrame = false;
                }
              };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
                .show();
      }
      requestPermissions(new String[]{PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
          CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
                (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                        || isHardwareLevelSupported(
                        characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
              CameraConnectionFragment.newInstance(
                      new CameraConnectionFragment.ConnectionCallback() {
                        @Override
                        public void onPreviewSizeChosen(final Size size, final int rotation) {
                          previewHeight = size.getHeight();
                          previewWidth = size.getWidth();
                          CameraActivity.this.onPreviewSizeChosen(size, rotation);
                        }
                      },
                      this,
                      getLayoutId(),
                      getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
              new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  @UiThread
  protected void showResultsInBottomSheet(List<Recognition> results) {
    if (results != null && results.size() >= 3) {
      Recognition recognition = results.get(0);
      if (recognition != null) {
        currentItem = SharedPreferenceManager.getItem(getApplicationContext(), recognition.getTitle());
        //currentItem = new ItemDetails("popcorn", 150, "pop", "Popcorn", "products");

        if (currentItem != null) {
          recognitionTextView.setText(currentItem.getDisplayName());
          priceView.setText("₹" + currentItem.getPrice());
          itemImage.setImageResource(getImageResourceByName(currentItem.getImageUrl()));
        }
      }

      List<String> similarItemIds = ClusterMapper.getSimilarProducts(currentItem);

      if(similarItemIds.size() >= 2) {
        currentItem1 = SharedPreferenceManager.getItem(getApplicationContext(), similarItemIds.get(0));
        recognition1TextView.setText(currentItem1.getDisplayName());
        priceView1.setText("₹" + currentItem1.getPrice());
        itemImage1.setImageResource(getImageResourceByName(currentItem1.getImageUrl()));

        currentItem2 = SharedPreferenceManager.getItem(getApplicationContext(), similarItemIds.get(1));
        recognition2TextView.setText(currentItem2.getDisplayName());
        priceView2.setText("₹" + currentItem2.getPrice());
        itemImage2.setImageResource(getImageResourceByName(currentItem2.getImageUrl()));

      } else {
        Recognition recognition1 = results.get(1);
        if (recognition1 != null) {
          currentItem1 = SharedPreferenceManager.getItem(getApplicationContext(), recognition1.getTitle());
          if (recognition1.getTitle() != null)
            recognition1TextView.setText(recognition1.getTitle());
          priceView1.setText("₹" + 0);
        }

        Recognition recognition2 = results.get(2);
        if (recognition2 != null) {
          currentItem2 = SharedPreferenceManager.getItem(getApplicationContext(), recognition2.getTitle());
          if (recognition2.getTitle() != null)
            recognition2TextView.setText(recognition2.getTitle());
          priceView2.setText("₹" + 1);
        }
      }

      if(Basket.getItemIdVsCount().get(currentItem.getId()) != null) {
        itemCountTextView.setText(String.valueOf(Basket.getItemIdVsCount().get(currentItem.getId())));
      } else {
        itemCountTextView.setText("0");
      }

      if(Basket.getItemIdVsCount().get(currentItem2.getId()) != null) {
        itemCountTextView2.setText(String.valueOf(Basket.getItemIdVsCount().get(currentItem2.getId())));
      } else {
        itemCountTextView2.setText("0");
      }

      if(Basket.getItemIdVsCount().get(currentItem1.getId()) != null) {
        itemCountTextView1.setText(String.valueOf(Basket.getItemIdVsCount().get(currentItem1.getId())));
      } else {
        itemCountTextView1.setText("0");
      }

    }
  }




  private int getImageResourceByName(String imagename) {
    return getResources().getIdentifier(imagename, "drawable",this.getPackageName());
  }

  protected Model getModel() {
    return model;
  }

  private void setModel(Model model) {
    if (this.model != model) {
      LOGGER.d("Updating  model: " + model);
      this.model = model;
      onInferenceConfigurationChanged();
    }
  }

  protected Device getDevice() {
    return device;
  }

  protected int getNumThreads() {
    return 9;
  }


  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.plus) {
      String threads = itemCountTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads >= 50) return;
      setNumItems(++numThreads);
      itemCountTextView.setText(String.valueOf(numThreads));
      Basket.addItem(currentItem);

    } else if (v.getId() == R.id.minus) {
      String threads = itemCountTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 0) {
        return;
      }
      setNumItems(--numThreads);
      itemCountTextView.setText(String.valueOf(numThreads));
      Basket.removeItem(currentItem);
    }



    else if (v.getId() == R.id.plus1) {
      String threads = itemCountTextView1.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads >= 50) return;
      setNumItems1(++numThreads);
      itemCountTextView1.setText(String.valueOf(numThreads));
      Basket.addItem(currentItem1);
    } else if (v.getId() == R.id.minus1) {
      String threads = itemCountTextView1.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 0) {
        return;
      }
      setNumItems1(--numThreads);
      itemCountTextView1.setText(String.valueOf(numThreads));
      Basket.removeItem(currentItem1);
    }


    else if (v.getId() == R.id.plus2) {
      String threads = itemCountTextView2.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads >= 50) return;
      setNumItems2(++numThreads);
      itemCountTextView2.setText(String.valueOf(numThreads));
      Basket.addItem(currentItem2);
    } else if (v.getId() == R.id.minus2) {
      String threads = itemCountTextView2.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 0) {
        return;
      }
      setNumItems2(--numThreads);
      itemCountTextView2.setText(String.valueOf(numThreads));
      Basket.removeItem(currentItem2);
    }

    basketPriceView.setText("₹"+Basket.getBasketValue());
  }

  public void loadBasket(View view){
    view.setBackgroundColor(Color.parseColor("#344622"));
    Intent intent = new Intent(CameraActivity.this, BasketActivity.class);
    startActivity(intent);
  }

  private void setNumItems(int i) {
    items = i;
  }

  private void setNumItems1(int i) {
    items1 = i;
  }

  private void setNumItems2(int i) {
    items2 = i;
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }
}
