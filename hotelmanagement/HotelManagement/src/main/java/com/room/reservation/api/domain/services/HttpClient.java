package com.room.reservation.api.domain.services;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.room.reservation.api.domain.enums.CustomHttpStatus;
import com.room.reservation.api.domain.exceptions.ValidationException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class HttpClient {

     OkHttpClient client = new OkHttpClient.Builder()
    		 .connectTimeout(10,TimeUnit.SECONDS).
    		 readTimeout(10,TimeUnit.SECONDS).
    		 build();

    public HttpClient() {
        this.client = new OkHttpClient();
    }

    
    public String makeRequest(String url, int staffId, int custId) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("staffId", String.valueOf(staffId))
                .addHeader("custId", String.valueOf(custId))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ValidationException("Failed to get response from server",
                        CustomHttpStatus.INTERNAL_SERVER_ERROR);
            }
            return response.body().string();
        }
        catch(Exception e)
        {
        	throw new ValidationException("Failed to get response from server cause time out to get response or to make connection",
                    CustomHttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
