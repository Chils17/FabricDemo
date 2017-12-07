package com.webmyne.adinterstitialdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import io.fabric.sdk.android.Fabric;

import static android.R.id.message;

public class PubNubActivity extends AppCompatActivity {

    private EditText edtMsg;
    private Button btnSend;
    private Button btnNotification;
    private String msg;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this);

        setContentView(R.layout.activity_pub_nub);

        token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token :", token);
        edtMsg = (EditText) findViewById(R.id.edtMsg);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnNotification = (Button) findViewById(R.id.btnNotification);

        final Pubnub pubnub = new Pubnub(
                getString(R.string.com_pubnub_publishKey),
                getString(R.string.com_pubnub_subscribeKey),
                getString(R.string.com_pubnub_secretKey));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = edtMsg.getText().toString().trim();

                pubnub.publish(getString(R.string.pubnub_channel), msg, new Callback() {
                    @Override
                    public void successCallback(String channel, Object message) {
                        System.out.println(message);
                    }

                    @Override
                    public void errorCallback(String channel, PubnubError error) {
                        System.out.println(error);
                    }
                });

                subscribePubNub(pubnub);
                history(pubnub);
                channelGroup(pubnub);
                channelGroupSubscribe(pubnub);
            }
        });

    }

    private void channelGroupSubscribe(Pubnub pubnub) {

    }

    private void channelGroup(Pubnub pubnub) {
        pubnub = new Pubnub(getString(R.string.com_pubnub_publishKey), getString(R.string.com_pubnub_subscribeKey));

        pubnub.channelGroupAddChannel("demo_channel_group", getString(R.string.pubnub_channel), new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                System.out.println(message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error);
            }
        });

    }

    private void history(Pubnub pubnub) {
        pubnub = new Pubnub(getString(R.string.com_pubnub_publishKey), getString(R.string.com_pubnub_subscribeKey));

        pubnub.history("demo", 10, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                System.out.println(message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error);
            }
        });

    }

    private void subscribePubNub(Pubnub pubnub) {
        pubnub = new Pubnub(getString(R.string.com_pubnub_publishKey), getString(R.string.com_pubnub_subscribeKey));

        try {
            pubnub.subscribe(getString(R.string.pubnub_channel), new Callback() {

                        @Override
                        public void connectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
        }

        pubnub.enablePushNotificationsOnChannel(getString(R.string.pubnub_channel), token, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);

                Log.d("channel", channel);
                Log.d("message", String.valueOf(message));

            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
            }
        });

    }




}
