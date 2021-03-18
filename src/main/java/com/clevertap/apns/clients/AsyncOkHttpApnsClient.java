/*
 * Copyright (c) 2016, CleverTap
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of CleverTap nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.clevertap.apns.clients;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clevertap.apns.Notification;
import com.clevertap.apns.NotificationResponse;
import com.clevertap.apns.NotificationResponseListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A wrapper around OkHttp's http client to send out notifications using Apple's HTTP/2 API.
 */
public class AsyncOkHttpApnsClient extends SyncOkHttpApnsClient {
	private static Logger logger=LoggerFactory.getLogger(AsyncOkHttpApnsClient.class);

    public AsyncOkHttpApnsClient(String apnsAuthKey, String teamID, String keyID,
                                 boolean production, String defaultTopic, ConnectionPool connectionPool) {
        super(apnsAuthKey, teamID, keyID, production, defaultTopic, connectionPool);
    }

    public AsyncOkHttpApnsClient(InputStream certificate, String password, boolean production,
                                 String defaultTopic, ConnectionPool connectionPool)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException, UnrecoverableKeyException, KeyManagementException {
        super(certificate, password, production, defaultTopic, connectionPool);
    }

    public AsyncOkHttpApnsClient(String apnsAuthKey, String teamID, String keyID,
                                 boolean production, String defaultTopic, OkHttpClient.Builder builder) {
        this(apnsAuthKey, teamID, keyID, production, defaultTopic, builder, 443);
    }

    public AsyncOkHttpApnsClient(String apnsAuthKey, String teamID, String keyID,
                                 boolean production, String defaultTopic, OkHttpClient.Builder builder, int connectionPort) {
        super(apnsAuthKey, teamID, keyID, production, defaultTopic, builder);
    }

    public AsyncOkHttpApnsClient(InputStream certificate, String password, boolean production,
                                 String defaultTopic, OkHttpClient.Builder builder)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException, UnrecoverableKeyException, KeyManagementException {
        this(certificate, password, production, defaultTopic, builder, 443);
    }

    public AsyncOkHttpApnsClient(InputStream certificate, String password, boolean production,
                                 String defaultTopic, OkHttpClient.Builder builder, int connectionPort)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException, UnrecoverableKeyException, KeyManagementException {
        super(certificate, password, production, defaultTopic, builder);
    }

    @Override
    public NotificationResponse push(Notification notification) {
        throw new UnsupportedOperationException("Synchronous requests are not supported by this client");
    }

    @Override
    public boolean isSynchronous() {
        return false;
    }

    @Override
    public void push(Notification notification, NotificationResponseListener nrl) {
    	logger.debug("");
        final Request request = buildRequest(notification);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            	logger.debug("");
                nrl.onFailure(notification, new NotificationResponse(null, -1, null, e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            	logger.debug("");
                final NotificationResponse nr;

                try {
                    nr = parseResponse(response);
                } catch (Throwable t) {
                    nrl.onFailure(notification, new NotificationResponse(null, -1, null, t));
                    return;
                } finally {
                    if (response != null) {
                        response.body().close();
                    }
                }

                if (nr.getHttpStatusCode() == 200) {
                    nrl.onSuccess(notification);
                } else {
                    nrl.onFailure(notification, nr);
                }
                
                logger.debug("");
            }
        });

    }
}
