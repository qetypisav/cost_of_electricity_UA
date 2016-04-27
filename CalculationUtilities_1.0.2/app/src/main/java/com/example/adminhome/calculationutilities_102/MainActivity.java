package com.example.adminhome.calculationutilities_102;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final static String BUNDLE_KEY_RESULTS_AL = "keyResultsAL";

    private final static String BUNDLE_KEY_SIMPLE_RESULT = "keySimpleResult";

    private final static String BUNDLE_KEY_WHICH_ACTIVITY_IS_OPEN = "keyWhichActivityIsOpen";

    private final static String ACTIVITY_MAIN_IS_OPEN = "ActivityMainIsOpen";

    private final static String ACTIVITY_EXTENDED_IS_OPEN = "ActivityExtendedIsOpen";

    private final static String ACTIVITY_CATEGORIES_IS_OPEN = "ActivityCategoriesIsOpen";

    private final static int RADIO_BUTTON_CHOISE_ONE = 0;

    private final static int RADIO_BUTTON_CHOISE_TWO = 1;

    /**
     * arrayList for all categories of tariffs
     */
    private ArrayList<Category> mCategoriesAL = new ArrayList<>();

    /**
     * Array adapter for spinner
     */
    private ArrayAdapter mAdapter;

    /**
     * linear Layout for activity main
     */
    private LinearLayout mMainLL;

    /**
     * Linear Layout for activity_extended_result
     */
    private LinearLayout mExtendedLL;

    /**
     * Linear Layout for activity_categories
     */
    private LinearLayout mCategoriesLL;

    /**
     * spinner for categories
     */
    private Spinner mSpinner;

    /**
     * edit Texts
     */
    private EditText mEditText1;
    private EditText mEditText2;

    /**
     * object Result
     */
    private Result mObjResult;

    /**
     * TextView for simple result
     */
    private TextView mTextViewSimpleResult;

    /**
     * string for bundle which activity is now open
     */
    private String mWhichActivityIsOpen;

    /**
     * array list of results
     */
    private ArrayList <Result> mResultsAL = new ArrayList<>();

    /**
     * string for bundle with simple result
     */
    private String mSimpleResult;

    //====================================FOR DIALOG VIEW===========================================
    private AlertDialog mDialog;

    private AlertDialog.Builder mBuilder;

    private View mDialogView;

    //==============================================================================================

    private int mWhichRadioButtonSelected = 0;

    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //create Layout Inflater
        LayoutInflater inflater = this.getLayoutInflater();

        //make inflate for layouts
        this.mMainLL = (LinearLayout) inflater.inflate(R.layout.activity_main, null, false);
        this.mExtendedLL = (LinearLayout) inflater.inflate(R.layout.activity_extended_result, null, false);
        this.mCategoriesLL = (LinearLayout) inflater.inflate(R.layout.activity_categories, null, false);
        this.mDialogView = (LinearLayout) inflater.inflate(R.layout.dialog_maket, null, false);

        //set values for bundle which activity was open
        mWhichActivityIsOpen = ACTIVITY_MAIN_IS_OPEN;

        setAllCategoriesValue();

        mAdapter = new ArrayAdapter(this, R.layout.my_spinner_item, mCategoriesAL);
        mSpinner = (Spinner) mMainLL.findViewById(R.id.spinner1);
        mAdapter.setDropDownViewResource(R.layout.my_spinner_item);
        mSpinner.setAdapter(mAdapter);

        mEditText1 = (EditText) mMainLL.findViewById(R.id.edtLastIndication);
        mEditText2 = (EditText) mMainLL.findViewById(R.id.edtCurrentIndication);

        mTextViewSimpleResult = (TextView) mMainLL.findViewById(R.id.tvResult);

        if (savedInstanceState != null){
            this.mWhichActivityIsOpen = savedInstanceState.getString(BUNDLE_KEY_WHICH_ACTIVITY_IS_OPEN);
            this.mResultsAL = (ArrayList) savedInstanceState.getSerializable(BUNDLE_KEY_RESULTS_AL);
            this.mSimpleResult = savedInstanceState.getString(BUNDLE_KEY_SIMPLE_RESULT);
            mTextViewSimpleResult.setText(mSimpleResult);
            if (mResultsAL.size() > 0){
                this.mObjResult = mResultsAL.get(0);
            }
            //if activity main was opened before turning device
            if (mWhichActivityIsOpen == ACTIVITY_MAIN_IS_OPEN){
                this.setContentView(mMainLL);
            }
            else if (mWhichActivityIsOpen == ACTIVITY_EXTENDED_IS_OPEN){
                this.setContentView(mExtendedLL);
                setExtendedResult(mObjResult);
            }
            else if (mWhichActivityIsOpen == ACTIVITY_CATEGORIES_IS_OPEN){
                this.setContentView(mCategoriesLL);
                mWhichActivityIsOpen = ACTIVITY_CATEGORIES_IS_OPEN;
                LinearLayout linearLayout = (LinearLayout) mCategoriesLL.findViewById(R.id.categoriesScrollLL);

                for (int i = 0; i < mCategoriesAL.size(); i++){
                    Category category = mCategoriesAL.get(i);
                    TextView textView = (TextView) linearLayout.getChildAt(i);
                    textView.setText(category.getCategoryDescription());
                }
            }
        }
        else {
            this.setContentView(mMainLL);
        }
        //==========================================================================================
        this.mBuilder = new AlertDialog.Builder(this);
        //this.mBuilder.setTitle("Обратите внимание!");

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //checkRadioButton();

                String string = Integer.toString(mWhichRadioButtonSelected);
                Log.d("====", string);
                Category category = mCategoriesAL.get(mWhichRadioButtonSelected);
                simpleCalculate(category);
                ((ViewGroup) (mDialogView.getParent())).removeAllViews();
            }
        });
        mBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ViewGroup) (mDialogView.getParent())).removeAllViews();
                    }
                });
        mRadioButton1 = (RadioButton) mDialogView.findViewById(R.id.radioB1);
        mRadioButton2 = (RadioButton) mDialogView.findViewById(R.id.radioB2);
        MyCheckedChangeListener MCCL = new MyCheckedChangeListener();
        mRadioButton1.setOnCheckedChangeListener(MCCL);
        mRadioButton2.setOnCheckedChangeListener(MCCL);
    }

    class MyCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String string = Integer.toString(buttonView.getId());
            Log.d("+++++id", string);
            switch (buttonView.getId()){
                case R.id.radioB1:
                    mWhichRadioButtonSelected = RADIO_BUTTON_CHOISE_ONE;
                    Log.d("======", "0000");
                    return;
                case R.id.radioB2:
                    mWhichRadioButtonSelected = RADIO_BUTTON_CHOISE_TWO;
                    Log.d("======", "1111");
                    return;
            }
        }
    }


    /**
     * method make calculation in case button click calculate
     * @param view
     */
    public void btnClickCalculate (View view){

        //check editTexts for not empty
        String stringEdt1 = mEditText1.getText().toString();
        String stringEdit2 = mEditText2.getText().toString();
        char char1 [] = stringEdt1.toCharArray();
        char char2 [] = stringEdit2.toCharArray();
        int check1 = 0;
        int check2 = 0;
        for (int i = 0; i < char1.length; i++){
            check1++;
        }
        for (int i = 0; i < char2.length; i++){
            check2++;
        }

        //if editTexts is not empty
       if (check1 != 0 && check2 != 0){
            /*variable which contains used electrical energy*/
            double usedEnergy = (Double.parseDouble(mEditText2.getText().toString())
                    - (Double.parseDouble(mEditText1.getText().toString())));

            //get spinner position
            int indexCategory = mSpinner.getSelectedItemPosition();

            //check current Data
            Calendar currentCalendar = Calendar.getInstance();
            Calendar mayCalendar = Calendar.getInstance();

            //set for mayCalendar value 2016,04,30, 23:59:59
            mayCalendar.set(Calendar.YEAR, 2015);
            mayCalendar.set(Calendar.MONTH, 3);
            mayCalendar.set(Calendar.DATE, 26);
            mayCalendar.set(Calendar.HOUR, 23);
            mayCalendar.set(Calendar.MINUTE, 59);
            mayCalendar.set(Calendar.SECOND, 59);

            if (currentCalendar.after(mayCalendar)){
                if (indexCategory == 2 || indexCategory == 3){
                    this.mBuilder.setView(this.mDialogView);
                    mDialog = mBuilder.create();
                    mDialog.show();
                }
                else {
                    Category category = mCategoriesAL.get(indexCategory);
                    simpleCalculate(category);
                }
            }
            //get current category from categoriesAL by spinner position (indexCategory)
//            Category category = mCategoriesAL.get(indexCategory);
//
//            //get all tariffs from current category
//            double firstTariff = category.getTariff1();
//            double secondTariff = category.getTariff2();
//            double thirdTariff = category.getTariff3();
//
//            //get all max tariff values from current category
//            double maxTariff1 = category.getMaxForTariff1();
//            double maxTariff2 = category.getMaxForTariff2();
//            double maxTariff3 = category.getMaxForTariff3();
//
//            boolean isReaddressing = category.getIsReaddressing();
//
//            String firstTextInfo = "До " + maxTariff1 + " кВт (" + firstTariff + " грн):";
//            String secondTextInfo = "От " + maxTariff1 + " до " + maxTariff2 + " кВт ("
//                    + secondTariff + " грн):";
//            String thirdTextInfo = "От " + maxTariff2 + " кВт (" + thirdTariff + " грн):";
//
//            //if (isReaddressing == false){
//            if (usedEnergy <= maxTariff1){
//                double result = usedEnergy * firstTariff;
//                double generalResult = result;
//                setSimpleResult(generalResult);//show Result
//                String result1 = Double.toString(generalResult) + " грн";
//                mObjResult = new Result(firstTextInfo, result1);
//            }
//            else if (usedEnergy > maxTariff1){
//                if (usedEnergy <= maxTariff2){
//                    double result1 = maxTariff1 * firstTariff;
//                    double remainingEnergy = usedEnergy - maxTariff1;
//                    double result2 = remainingEnergy * secondTariff;
//                    double generalResult = result1 + result2;
//                    setSimpleResult(generalResult);//show Result
//                    String resultOne = Double.toString(result1);
//                    String resultTwo = Double.toString(result2);
//                    mObjResult = new Result(firstTextInfo, resultOne, secondTextInfo, resultTwo);
//                }
//                else if (usedEnergy > maxTariff2){
//                    double result1 = maxTariff1 * firstTariff;
//                    double result2 = (maxTariff2 - maxTariff1) * secondTariff;
//                    double remainingEnergy = usedEnergy - maxTariff2;
//                    double result3 = remainingEnergy * thirdTariff;
//                    double generalResult = result1 + result2 + result3;
//                    setSimpleResult(generalResult);//show Result
//                    String resultOne = Double.toString(result1);
//                    String resultTwo = Double.toString(result2);
//                    String resultThree = Double.toString(result3);
//                    mObjResult = new Result(firstTextInfo, resultOne, secondTextInfo, resultTwo,
//                            thirdTextInfo, resultThree);
//                }
//            }
//            int checkResults = 0;
//            for (int i = 0; i < mResultsAL.size(); i++){
//                checkResults++;
//            }
//            if (checkResults > 0){
//                mResultsAL.remove(0);
//            }
//            mResultsAL.add(mObjResult);
//            //}
        }
        else{
            Toast.makeText(this, "Есть незаполненные поля", Toast.LENGTH_SHORT).show();
        }

    }

    public void simpleCalculate(Category category){
        /*variable which contains used electrical energy*/
        double usedEnergy = (Double.parseDouble(mEditText2.getText().toString())
                - (Double.parseDouble(mEditText1.getText().toString())));

        //get all tariffs from current category
        double firstTariff = category.getTariff1();
        double secondTariff = category.getTariff2();
        double thirdTariff = category.getTariff3();

        //get all max tariff values from current category
        double maxTariff1 = category.getMaxForTariff1();
        double maxTariff2 = category.getMaxForTariff2();
        double maxTariff3 = category.getMaxForTariff3();

        boolean isReaddressing = category.getIsReaddressing();

        String firstTextInfo = "До " + maxTariff1 + " кВт (" + firstTariff + " грн):";
        String secondTextInfo = "От " + maxTariff1 + " до " + maxTariff2 + " кВт ("
                + secondTariff + " грн):";
        String thirdTextInfo = "От " + maxTariff2 + " кВт (" + thirdTariff + " грн):";

        //if (isReaddressing == false){
        if (usedEnergy <= maxTariff1){
            double result = usedEnergy * firstTariff;
            double generalResult = result;
            setSimpleResult(generalResult);//show Result
            String result1 = Double.toString(generalResult) + " грн";
            mObjResult = new Result(firstTextInfo, result1);
        }
        else if (usedEnergy > maxTariff1){
            if (usedEnergy <= maxTariff2){
                double result1 = maxTariff1 * firstTariff;
                double remainingEnergy = usedEnergy - maxTariff1;
                double result2 = remainingEnergy * secondTariff;
                double generalResult = result1 + result2;
                setSimpleResult(generalResult);//show Result
                String resultOne = Double.toString(result1);
                String resultTwo = Double.toString(result2);
                mObjResult = new Result(firstTextInfo, resultOne, secondTextInfo, resultTwo);
            }
            else if (usedEnergy > maxTariff2){
                double result1 = maxTariff1 * firstTariff;
                double result2 = (maxTariff2 - maxTariff1) * secondTariff;
                double remainingEnergy = usedEnergy - maxTariff2;
                double result3 = remainingEnergy * thirdTariff;
                double generalResult = result1 + result2 + result3;
                setSimpleResult(generalResult);//show Result
                String resultOne = Double.toString(result1);
                String resultTwo = Double.toString(result2);
                String resultThree = Double.toString(result3);
                mObjResult = new Result(firstTextInfo, resultOne, secondTextInfo, resultTwo,
                        thirdTextInfo, resultThree);
            }
        }
        int chekResults = 0;
        for (int i = 0; i < mResultsAL.size(); i++){
            chekResults++;
        }
        if (chekResults > 0){
            mResultsAL.remove(0);
        }
        mResultsAL.add(mObjResult);
    }

    /**
     * method put data into bundle
     * @param B - Bundle
     */
    public void onSaveInstanceState (Bundle B){
        super.onSaveInstanceState(B);
        //put AL of results
        B.putSerializable(BUNDLE_KEY_RESULTS_AL, mResultsAL);
        //put string of which activity was opened
        B.putString(BUNDLE_KEY_WHICH_ACTIVITY_IS_OPEN, mWhichActivityIsOpen);
        //put values of simple result
        B.putString(BUNDLE_KEY_SIMPLE_RESULT, mSimpleResult);
    }

    /**
     * this method create new Categories and add it to categoriesAL
     */
    public void setAllCategoriesValue (){
        Category category1 = new Category("Категория 1: Население, проживающее в жилых домах (в том числе " +
                "в домах, оборудованных кухонными электроплитами) (До 100 кВт час в месяц - 0.57 грн" +
                " от 100 кВт час до 600 кВт час - 0.99 грн, свыше 600 кВт час - 1.56 грн)", Category.TARIFF_057,
                Category.TARIFF_099, Category.TARIFF_156, Category.MAX_INDICATION_100,
                Category.MAX_INDICATION_600, Category.MAXIMAL_NUMBER_INDICATION, false,
                "Категория 1");

        Category category2 = new Category("Категория 2: Население, проживающее в сельской местности " +
                "(в том числе в домах, оборудованных кухонными электроплитами) (до 150 кВт час - 0.57 грн" +
                " от 150 до 600 кВт час - 0.99 грн, свыше 600 кВт час - 1.56 грн", Category.TARIFF_057,
                Category.TARIFF_099, Category.TARIFF_156, Category.MAX_INDICATION_150,
                Category.MAX_INDICATION_600, Category.MAXIMAL_NUMBER_INDICATION, false, "Категория 2");

        Category category3 = new Category("Категория 3: Население, проживающее в жилых домах (в том числе " +
                "в домах гостиничного типа и общежитиях), оборудованных в установленном порядке " +
                "электроотопительными установками (в том числе в сельской местности) " +
                "(до 3600 кВт - 0.57 грн,  свыше 3600 кВт·- 1.56 грн)", Category.TARIFF_057,
                Category.TARIFF_156, Category.ZERO_TARIFF_OR_INDICATION_VALUE, Category.MAX_INDICATION_3600,
                Category.MAXIMAL_NUMBER_INDICATION, Category.MAXIMAL_NUMBER_INDICATION, true, "Категория 3");

        Category category4 = new Category("Категория4: Население, проживающее в многоквартирных домах, " +
                "не газифицируемых природным газом и в которых отсутствуют или не " +
                "функционируют системы централизованного теплоснабжения (в том числе " +
                "в сельской местности) (до 3600 кВт - 0.57 грн,  свыше 3600 кВт·- 1.56 грн)", Category.TARIFF_057,
                Category.TARIFF_156, Category.ZERO_TARIFF_OR_INDICATION_VALUE, Category.MAX_INDICATION_3600,
                Category.MAXIMAL_NUMBER_INDICATION, Category.MAXIMAL_NUMBER_INDICATION, true, "Категория4");

        Category category5 = new Category("Категория5: Многодетные, приемные семьи и детские дома " +
                "семейного типа (0.57 грн)", Category.TARIFF_057, Category.ZERO_TARIFF_OR_INDICATION_VALUE,
                Category.ZERO_TARIFF_OR_INDICATION_VALUE, Category.MAXIMAL_NUMBER_INDICATION,
                Category.MAXIMAL_NUMBER_INDICATION, Category.MAXIMAL_NUMBER_INDICATION, false, "Категория5");

        Category category6 = new Category("Категория6: Население, которое рассчитывается с " +
                "энергоснабжающей организацией по общим расчетным средствам учета и " +
                "объединенное путем создания юридического лица, жилищно-эксплуатационным " +
                "организациям, кроме общежитий (0.99 грн)", Category.TARIFF_099, Category.ZERO_TARIFF_OR_INDICATION_VALUE,
                Category.ZERO_TARIFF_OR_INDICATION_VALUE, Category.MAXIMAL_NUMBER_INDICATION,
                Category.MAXIMAL_NUMBER_INDICATION, Category.MAXIMAL_NUMBER_INDICATION, false,
                "Категория6");

        Category category7 = new Category("Категория7: Общежития (0.57 грн)", Category.TARIFF_057,
                Category.ZERO_TARIFF_OR_INDICATION_VALUE, Category.ZERO_TARIFF_OR_INDICATION_VALUE,
                Category.MAXIMAL_NUMBER_INDICATION, Category.MAXIMAL_NUMBER_INDICATION,
                Category.MAXIMAL_NUMBER_INDICATION, false, "Категория7");

        mCategoriesAL.add(category1);
        mCategoriesAL.add(category2);
        mCategoriesAL.add(category3);
        mCategoriesAL.add(category4);
        mCategoriesAL.add(category5);
        mCategoriesAL.add(category6);
        mCategoriesAL.add(category7);
    }

    /**
     * method for case when button Extended Info was clicked
     * @param view
     */
    public void btnClickExtendedInfo (View view){
        if (mObjResult != null){
            this.setContentView(mExtendedLL);
            setExtendedResult(mObjResult);
            mWhichActivityIsOpen = ACTIVITY_EXTENDED_IS_OPEN;
        }
        else {
            Toast.makeText(this, "Сначала нажмите: РАСЧИТАТЬ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * method for case when button Categories was clicked
     * @param view
     */
    public void btnClickCategories(View view){
        this.setContentView(mCategoriesLL);
        mWhichActivityIsOpen = ACTIVITY_CATEGORIES_IS_OPEN;
        LinearLayout linearLayout = (LinearLayout) mCategoriesLL.findViewById(R.id.categoriesScrollLL);

        for (int i = 0; i < mCategoriesAL.size(); i++){
            Category category = mCategoriesAL.get(i);
            TextView textView = (TextView) linearLayout.getChildAt(i);
            textView.setText(category.getCategoryDescription());
        }
    }

    /**
     * method set simple result in to textView
     * @param result
     */
    public void setSimpleResult(double result){
        String strResult = Double.toString(result);
        Log.d("======", strResult);
        //TextView tvResult = (TextView) this.findViewById(R.id.tvResult);
        mTextViewSimpleResult.setText(strResult + " грн");
        mSimpleResult = strResult + " грн";
    }

    /**
     * method set result in textViews of Extended result
     * @param result
     */
    public void setExtendedResult(Result result){
        String firstTextInfo = result.getFirstTextInfo();
        String secondTextInfo = result.getSecondTextInfo();
        String thirdTextInfo = result.getThirdTextInfo();
        String result1 = result.getResult1();
        String result2 = result.getResult2();
        String result3 = result.getResult3();

        TextView tvFirstTextInfo = (TextView) mExtendedLL.findViewById(R.id.tvFirstTextInfo);
        TextView tvSecondTextInfo = (TextView) mExtendedLL.findViewById(R.id.tvSecondTextInfo);
        TextView tvThirdTextInfo = (TextView) mExtendedLL.findViewById(R.id.tvThirdTextInfo);
        TextView tvResult1 = (TextView) mExtendedLL.findViewById(R.id.tvResult1);
        TextView tvResult2 = (TextView) mExtendedLL.findViewById(R.id.tvResult2);
        TextView tvResult3 = (TextView) mExtendedLL.findViewById(R.id.tvResult3);

        tvFirstTextInfo.setText(firstTextInfo);
        tvResult1.setText(result1);
        tvSecondTextInfo.setText(secondTextInfo);
        tvResult2.setText(result2);
        tvThirdTextInfo.setText(thirdTextInfo);
        tvResult3.setText(result3);
    }

    /**
     * method set activity main when you click button Back to Main Calculate
     * @param view
     */
    public void btnClickBackToMainCalculate (View view){
        this.setContentView(this.mMainLL);
        mWhichActivityIsOpen = ACTIVITY_MAIN_IS_OPEN;

//        adapter.setDropDownViewResource(R.layout.my_spinner_item);
//        spinner.setAdapter(adapter);
    }

    /**
     * if current activity is not activity main setContentView to activity_main if user press
     * button back
     */
    @Override
    public void onBackPressed() {
        if (mWhichActivityIsOpen != ACTIVITY_MAIN_IS_OPEN){
            this.setContentView(this.mMainLL);
            mWhichActivityIsOpen = ACTIVITY_MAIN_IS_OPEN;
        }
        else if (mWhichActivityIsOpen == ACTIVITY_MAIN_IS_OPEN){
            super.onBackPressed();
        }
    }

    /*
    public void btnClickBackToCalculate (View view){
        this.setContentView(this.mMainLL);
        mWhichActivityIsOpen = ACTIVITY_MAIN_IS_OPEN;

//        adapter.setDropDownViewResource(R.layout.my_spinner_item);
//        spinner.setAdapter(adapter);
    }
    */
}


class Category {
    public final static double TARIFF_057 = 0.57;
    public final static double TARIFF_099 = 0.99;
    public final static double TARIFF_156 = 1.56;

    public final static double MAX_INDICATION_100 = 100;
    public final static double MAX_INDICATION_600 = 600;
    public final static double MAX_INDICATION_150 = 150;
    public final static double MAX_INDICATION_3600 = 3600;
    public final static double MAXIMAL_NUMBER_INDICATION = 999999;
    public final static double ZERO_TARIFF_OR_INDICATION_VALUE = 0;

    /**
     * name of current category
     */
    private String mCategoryName;

    /**
     * description of category
     */
    private String mCategoryDescription;

    /**
     * first tariff
     */
    private double mTariff1;

    /**
     * second tariff
     */
    private double mTariff2;

    /**
     * third tariff
     */
    private double mTariff3;

    /**
     * maximal indication for first tariff
     */
    private double mMaxForTariff1;

    /**
     * maximal indication for second tariff
     */
    private double mMaxForTariff2;

    /**
     * maximal indication for third tariff
     */
    private double mMaxForTariff3;

    /**
     * did we need to readdressing to first or second categories in time stamp
     */
    private boolean mIsReaddressing;

    /**
     * constructor for class Category
     * @param categoryDescription - String
     * @param tariff1 - double
     * @param tariff2 - double
     * @param tariff3 - double
     * @param maxForTariff1 - double
     * @param maxForTariff2 - double
     * @param maxForTariff3 - double
     * @param isReaddressing - boolean
     * @param categoryName - String
     */
    public Category (String categoryDescription, double tariff1, double tariff2,
                     double tariff3, double maxForTariff1, double maxForTariff2, double maxForTariff3,
                     boolean isReaddressing, String categoryName){
        this.mCategoryDescription = categoryDescription;
        this.mTariff1 = tariff1;
        this.mTariff2 = tariff2;
        this.mTariff3 = tariff3;
        this.mMaxForTariff1 = maxForTariff1;
        this.mMaxForTariff2 = maxForTariff2;
        this.mMaxForTariff3 = maxForTariff3;
        this.mIsReaddressing = isReaddressing;
        this.mCategoryName = categoryName;
    }

    /**
     * getter for Tariff1
     * @return double
     */
    public double getTariff1 (){
        return this.mTariff1;
    }

    /**
     * getter for tariff2
     * @return double
     */
    public double getTariff2 (){
        return this.mTariff2;
    }

    /**
     * getter for tariff3
     * @return double
     */
    public  double getTariff3 (){
        return this.mTariff3;
    }

    /**
     * getter for maxTariff1
     * @return int
     */
    public double getMaxForTariff1 (){
        return this.mMaxForTariff1;
    }

    /**
     * getter for maxTariff2
     * @return int
     */
    public double getMaxForTariff2 (){
        return this.mMaxForTariff2;
    }

    /**
     * getter for maxTariff3
     * @return int
     */
    public double getMaxForTariff3 (){
        return this.mMaxForTariff3;
    }

    /**
     * getter for category description
     * @return String
     */
    public String getCategoryDescription (){
        return this.mCategoryDescription;
    }

    /**
     * getter for is readdressing
     * @return boolean
     */
    public boolean getIsReaddressing(){
        return this.mIsReaddressing;
    }

    /**
     * this overridden method return string with description of current category
     * @return String
     */
    @Override
    public String toString (){
        String string = this.mCategoryName;
        return string;
    }
}


class Result implements Serializable {

    private String mFirstTextInfo;

    private String mSecondTextInfo;

    private String mThirdTextInfo;

    private String mResult1;

    private String mResult2;

    private String mResult3;

    /**
     * Constructor for case when result was calculated only by first tariff
     * @param firstTextInfo String
     * @param result1 String
     */
    public Result (String firstTextInfo, String result1){
        this.mFirstTextInfo = firstTextInfo;
        this.mResult1 = result1;
        this.mSecondTextInfo = "";
        this.mResult2 = "";
        this.mThirdTextInfo = "";
        this.mResult3 = "";
    }

    /**
     * Constructor for case when result was calculated by first and second tariffs
     * @param firstTextInfo - String
     * @param result1 - String
     * @param secondTextInfo - String
     * @param result2 - String
     */
    public Result (String firstTextInfo, String result1, String secondTextInfo,
                   String result2){
        this.mFirstTextInfo = firstTextInfo;
        this.mResult1 = result1;
        this.mSecondTextInfo = secondTextInfo;
        this.mResult2 = result2;
        this.mThirdTextInfo = "";
        this.mResult3 = "";
    }

    /**
     * Constructor for case when result was calculated by first, second and third tariffs
     * @param firstTextInfo - String
     * @param result1 - String
     * @param secondTextInfo - String
     * @param result2 - String
     * @param thirdTextInfo - String
     * @param result3 - String
     */
    public Result (String firstTextInfo, String result1, String secondTextInfo, String result2,
                   String thirdTextInfo, String result3){
        this.mFirstTextInfo = firstTextInfo;
        this.mResult1 = result1;
        this.mSecondTextInfo = secondTextInfo;
        this.mResult2 = result2;
        this.mThirdTextInfo = thirdTextInfo;
        this.mResult3 = result3;
    }

    /**
     * getter for firstTextInfo
     * @return - String
     */
    public String getFirstTextInfo(){
        return this.mFirstTextInfo;
    }

    /**
     * getter for secondTextInfo
     * @return - String
     */
    public String getSecondTextInfo(){
        return  this.mSecondTextInfo;
    }

    /**
     * getter for thirdTextInfo
     * @return - String
     */
    public String getThirdTextInfo(){
        return this.mThirdTextInfo;
    }

    /**
     * getter for result1
     * @return - String
     */
    public String getResult1(){
        return this.mResult1;
    }

    /**
     * getter for result2
     * @return - String
     */
    public String getResult2(){
        return this.mResult2;
    }

    /**
     * getter for result3
     * @return - String
     */
    public String getResult3(){
        return this.mResult3;
    }
}