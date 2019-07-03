package edu.therealbranik.therealflower.utility;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.login_register.RegisterActivity;

public class UploadImageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageURI;

    private Button buttonUpload;
    private Button buttonTakephoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonTakephoto = (Button) findViewById(R.id.buttonTakePhoto);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
    }

    private void openImageChooser () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageURI = data.getData();

            Intent intent = new Intent();
            intent.putExtra("uri", imageURI.toString());

            setResult(RegisterActivity.UPLOAD_FROM_FILE_SYSTEM, intent);
            finish();
        }
    }
}
