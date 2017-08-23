# 图片选择器
* 拍照
``` code
public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Utils.createImageFile()));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
            finish();
        }
    } 
```
* ![拍照](https://github.com/YougaKing/ImageSelector/blob/master/source/1.gif)
``` code
public String getCroppedImagePath() {
        Bitmap croppedImage = cropImageView.getCroppedImage();
        try {
            String imageDir = Utils.createImageDir();
            File cropFile = new File(imageDir + "/" + "CropImage.jpg");
            Log.i(TAG, "cropFile:" + cropFile);
            Utils.writeBitmapFile(cropFile, croppedImage);
            return cropFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
```
* ![裁剪](https://github.com/YougaKing/ImageSelector/blob/master/source/2.gif)
``` code
public interface PhotoChoiceCallBack {

        void onImageSelected(ArrayList<String> resultList);

        void shouldOpenCamera();

        void onSingleSelected(String path);
    }
```
* ![多图选择](https://github.com/YougaKing/ImageSelector/blob/master/source/3.gif)

#### Download
* Download the latest JAR or grab via Maven:
```xml
<dependency>
  <groupId>com.youga.imageselector</groupId>
  <artifactId>imageselector</artifactId>
  <version>1.1.3</version>
  <type>pom</type>
</dependency>
```
* or Gradle:
```xml
compile 'com.youga.imageselector:imageselector:1.1.3'
```

#### 1.1.3
* 适配android 6.0 权限
* 支持拍照.相册选择.图片裁剪

# 关于作者
* QQ交流群:158506055
* Email: YougaKingWu@gmail.com
