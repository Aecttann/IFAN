package com.aectann.ifan;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aectann.ifan.adapters.FactsAdapter;
import com.aectann.ifan.database.IFANDatabase;
import com.aectann.ifan.interfaces.NumbersAPI;
import com.aectann.ifan.models.Fact;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://numbersapi.com/";
    private EditText ETNumber;
    private CardView CVGetNumberFact;
    private CardView CVGetRandomNumberFact;
    private String number;
    private IFANDatabase ifanDb;
    private FactsAdapter factsAdapter;
    private RecyclerView RVFacts;
    private ProgressBar PBLoading;
    private List<Fact> facts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setUpActivity();
    }

    private void init(){
        // ініціювання view
        ETNumber = findViewById(R.id.ETNumber);
        CVGetNumberFact = findViewById(R.id.CVGetNumberFact);
        CVGetRandomNumberFact = findViewById(R.id.CVGetRandomNumberFact);
        RVFacts = findViewById(R.id.RVFacts);
        PBLoading = findViewById(R.id.PBLoading);

        // ініціювання бд
        ifanDb = IFANDatabase.getInstance(this);
        // отримати дані
        Observable.just(ifanDb)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IFANDatabase>(){
                    @Override
                    public void onSubscribe(Disposable d) {
                        Completable.fromAction(() -> facts = ifanDb.factDao().getFactList())
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }

                    @Override
                    public void onNext(IFANDatabase factDatabase) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        // робота з адаптером
                        factsAdapter = new FactsAdapter(facts);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, true);
                        RVFacts.setLayoutManager(linearLayoutManager);
                        RVFacts.setAdapter(factsAdapter);
                        PBLoading.setVisibility(View.GONE);
                    }
                });
    }

    private void setUpActivity(){
        // клік для отримання факту про введене число
        CVGetNumberFact.setOnClickListener(v->{
            number = ETNumber.getText().toString();
            if(isEmpty(number)){
                ETNumber.setError(getResources().getString(R.string.error_empty_number));
                ETNumber.requestFocus();
            } else{
                loadFact(number);
            }
        });
        // клік на клавіатурі
        ETNumber.setOnEditorActionListener((v, actionId, event) -> {
            number = ETNumber.getText().toString();
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(isEmpty(number)){
                    ETNumber.setError(getResources().getString(R.string.error_empty_number));
                    ETNumber.requestFocus();
                } else{
                    InputMethodManager in = (InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(ETNumber.getWindowToken(), 0);
                    loadFact(number);
                }
                return true;
            }
            return false;
        });
        // клік для отримання факту про рандомне число
        CVGetRandomNumberFact.setOnClickListener(v->{
            // якщо клікали по іншій кнопці і т.д., то наведемо лад на екрані
            ETNumber.clearFocus();
            ETNumber.setError(null);
            InputMethodManager in = (InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(ETNumber.getWindowToken(), 0);
            // генеруємо рандомне число
            String number = String.valueOf(new SecureRandom().nextInt(10000));
            loadFact(number);
        });
    }

    // API call
    private void loadFact(String number){
        OkHttpClient okHttp = new OkHttpClient.Builder()
                .build();
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttp)
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        NumbersAPI numbersAPI = retrofit.create(NumbersAPI.class);
        Observable<String> numberObservable = numbersAPI.getNumberFact(number);
        numberObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(result -> result)
                .subscribe(this::handleResults, this::handleError);
    }

    // додаємо запис в бд
    private void insertData(Fact fact){
        Completable.fromRunnable(() -> ifanDb.factDao().insertFact(fact)).subscribeOn(Schedulers.io()).subscribe();
    }

    // запис у бд, читання з бд, вивід у RV адаптер
    private void handleResults(String result){
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        Fact fact = new Fact();
        fact.setFact(result);
        facts.add(fact);
        insertData(fact);
        // оновлюємо адаптер з фактами
         factsAdapter.notifyItemInserted(facts.size());
        // гортатимемо RV
        RVFacts.smoothScrollToPosition(facts.size());
    }

    private void handleError(Throwable t){
        Toast.makeText(this, getResources().getString(R.string.error_api), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}