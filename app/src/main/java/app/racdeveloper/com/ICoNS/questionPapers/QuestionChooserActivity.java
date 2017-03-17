package app.racdeveloper.com.ICoNS.questionPapers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

import app.racdeveloper.com.ICoNS.Constants;
import app.racdeveloper.com.ICoNS.FilePath;
import app.racdeveloper.com.ICoNS.QueryPreferences;
import app.racdeveloper.com.ICoNS.R;

/**
 * Created by Rachit on 10/19/2016.
 */
public class QuestionChooserActivity extends AppCompatActivity{

    private boolean isSpinnerInitial = true;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    public static String branch = null;
    public static String semester = null;
    int branchPosition,semesterPosition;

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fabQuestion;
    private Uri filePath;
    String URL_UPLOAD = Constants.URL + "questionPapers/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_paper_chooser);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toast.makeText(QuestionChooserActivity.this, "Select your choice", Toast.LENGTH_SHORT).show();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coorLayout);
        fabQuestion = (FloatingActionButton) findViewById(R.id.fabQuestion);
        fabQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "     Upload Question Papers", Snackbar.LENGTH_LONG)
                        .setAction("UPLOAD", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.setType("application/pdf");
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(i, "Select your Resume PDF"), 1);
                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.WHITE);

                // Changing action button text color
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.rgb(25,25,112));
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);
                snackbar.show();
            }
        });

        spinner = (Spinner) findViewById(R.id.spinnerbranch);
        adapter = ArrayAdapter.createFromResource(this, R.array.branch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                branchPosition = position;

                if (isSpinnerInitial) {
                    branch = String.valueOf(position);
                    isSpinnerInitial = false;
                } else {
                    if (position == 0) ;
                    else {
                        branch = String.valueOf(position);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinner = (Spinner) findViewById(R.id.spinnersemester);
        adapter = ArrayAdapter.createFromResource(this, R.array.semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                semesterPosition = position;

                if (isSpinnerInitial) {
                    semester = String.valueOf(position);
                    isSpinnerInitial = false;
                } else {
                    if
                            (position == 0) ;
                    else {
                        semester = String.valueOf(position);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {            }
        });

        Button button = (Button) findViewById(R.id.search_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    if(branchPosition != 0 && semesterPosition != 0)
                    {
                        Intent questionIntent = new Intent(QuestionChooserActivity.this, QuestionPaperActivity.class);
                        startActivity(questionIntent);
                    }
                    else
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(QuestionChooserActivity.this).create();
                        alertDialog.setTitle("Inappropriate Choices Selected");
                        alertDialog.setMessage("Please,select one of the given choices to fetch the question papers.");
                        alertDialog.setIcon(R.drawable.emergency);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==RESULT_OK  && data!=null){
            filePath = data.getData();

            createAlertDialog();
        }
    }

    public void uploadMultipart(String subjectName, int branchCode, int semCode) {
        //getting name for the image

        //getting the actual path of the image
        String path = FilePath.getPath(this, filePath);

        if (path == null) {
            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, URL_UPLOAD)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("token", QueryPreferences.getToken(this))
                        .addParameter("subject", "" + subjectName)
                        .addParameter("branch", "" + branchCode)
                        .addParameter("semester", "" + semCode)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createAlertDialog() {
        final int[] branchCode = {0};
        final int[] semester = {0};
        final String[] subjectName = new String[1];

        final AlertDialog.Builder builder= new AlertDialog.Builder(QuestionChooserActivity.this);
        builder.setTitle("Select question paper details");

        LinearLayout layout = new LinearLayout(QuestionChooserActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText subject = new EditText(QuestionChooserActivity.this);
        subject.setTextSize(20);
        subject.setMaxLines(1);
        subject.setHint("Subject Name");
        layout.addView(subject);

        final Spinner alertSpinner = new Spinner(this);
        adapter = ArrayAdapter.createFromResource(this, R.array.branch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertSpinner.setAdapter(adapter);
        alertSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isSpinnerInitial) {
                    branchCode[0] = position;
                    isSpinnerInitial = false;
                } else {
                    if (position == 0) ;
                    else {
                        branchCode[0] = position;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        layout.addView(alertSpinner);

        Spinner alertSpinnerSem = new Spinner(this);
        adapter = ArrayAdapter.createFromResource(this, R.array.semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertSpinnerSem.setAdapter(adapter);
        alertSpinnerSem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isSpinnerInitial) {
                    semester[0] = position;
                    isSpinnerInitial = false;
                } else {
                    if (position == 0) ;
                    else {
                        semester[0] = position;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        layout.addView(alertSpinnerSem);

        builder.setView(layout);
        builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                subjectName[0] = subject.getText().toString();
                if(!subjectName[0].equals("") && branchCode[0]!=0 && semester[0]!=0 )
                    uploadMultipart(subjectName[0], branchCode[0], semester[0]);
                else {
                    Toast.makeText(QuestionChooserActivity.this, "Submit Again and Fill details properly!!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
