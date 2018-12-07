package anative.apps.piyush.smartattendence;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static Bitmap imageBitmap ;
    static String imgPath = "";
    public static TextView txtView;
    static  boolean marked;
    static String grNumber;
    EditText gr_edit_text;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create camera file
        File fileDir = new File(Environment.getExternalStorageDirectory()
                + "/piyush");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        imgPath = Environment.getExternalStorageDirectory() + "/piyush/img.jpg";
        File carmeraFile = new File(imgPath);
        Uri imageCarmeraUri = Uri.fromFile(carmeraFile);

        takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                imageCarmeraUri);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // Do nothing for now
        }

        //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ImageView myImageView = (ImageView) findViewById(R.id.imgview);
            txtView = (TextView) findViewById(R.id.txtContent);

            Bitmap bMap = BitmapFactory.decodeFile(imgPath);

             //Bitmap myBitmap = BitmapFactory.decodeResource(
             //       getApplicationContext().getResources(),
             //      R.drawable.puppy);

            Bitmap myBitmap = bMap;

            myImageView.setImageBitmap(myBitmap);

            BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.ITF)
                    .build();
            if(!detector.isOperational()){
                txtView.setText("Could not set up the detector!");
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            if(barcodes.size()>0){
                Barcode code = barcodes.valueAt(0);
                grNumber = "gr"+code.rawValue.substring(code.rawValue.length()-6);
                txtView.setText(barcodes.size()+", "+grNumber);

                MainActivity.marked = markAttendence(grNumber);

                if(MainActivity.marked){
                    //txtView.setText("Attendance Marked for "+grNumber);
                }else{
                    //txtView.setText("database not updated");
                }
            }else{
                txtView.setText("Cannot read this barcode");
            }

        }
    }

    private boolean markAttendence(String gr) {
        String add = "http://cpt.cisatnec.org/mark_attendence.php?gr="+gr+"&request=validateUser=true@piyush.isvalid()";
        //http://cpt.cisatnec.org/mark_attendence.php?gr=gr007230&request=validateUser=true@piyush.isvalid()

        String ar[] = {add};
        UpdateData obj = new UpdateData();
        obj.execute(ar);
        return true;
    }

    public void readCode(View v){
        if(gr_edit_text.getText().length()>5){
            grNumber = "GR"+gr_edit_text.getText();
            MainActivity.marked = markAttendence(grNumber);
        }else {
            dispatchTakePictureIntent();
        }
        //dispatchTakePictureIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gr_edit_text =  (EditText) findViewById(R.id.gr_text_edit);

    }
}
