package spaak.supertec.vedioupload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import spaak.supertec.vedioupload.api.ApiClient;
import spaak.supertec.vedioupload.api.ApiInterface;
import spaak.supertec.vedioupload.model.ResponseModel;

public class GetDataActivity extends AppCompatActivity {

    public TextView video_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        video_link = findViewById(R.id.video_link);

        getAllVideos();
    }

    public void getAllVideos() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseModel> call = apiInterface.getAllVideos();
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    video_link.setText(response.body().toString());
                    Log.d("PPPPPP",response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
            }
        });
    }
}
