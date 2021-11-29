package com.example.atry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.StringPrepParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.util.I;
import cn.bmob.v3.util.V;

public class findbackctivity extends AppCompatActivity {
    String template = "“1231”";
    String appkey ="1477ee57c3443843cb11124c78e05af9";
    String TAG = "---------------";
    String s;
    Handler myhandler =new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Intent intent=new Intent();

            switch (msg.what){
                case 0x00:

                    intent.putExtra("ver",true);
                    break;
                case  0x01:
                    Bundle bundle=msg.getData();
                     s = bundle.getString("ID");
                    Log.d(TAG, "handleMessage: 0x01"+s);
                    break;
                case 0x02:
                    Person p2 = new Person();
                    if(s!=null){
                    p2.setAddress(msg.getData().getString("npasswd"));
                    p2.update(s, new UpdateListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                toast("更新成功:" + p2.getUpdatedAt());
                            } else {
                                toast("更新失败：" + e.getMessage());
                            }
                        }

                    });}
                    else{
                        Log.d(TAG, "handleMessage: 未发送验证码");
                        }
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findbackctivity);
        Bmob.initialize(this, appkey);
        Button fb_commit = findViewById(R.id.fb_commit);
        Button fb_sendver = findViewById(R.id.fb_sendver);
        fb_sendver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(v);
            }
        });
        fb_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit(v);
            }
        });

    }

    private void commit(View view) {


        boolean verified=getIntent().getBooleanExtra("ver",false);
        EditText fb_telephone = findViewById(R.id.fb_telephone);
        EditText fb_verified = findViewById(R.id.fb_verified);
        EditText newpasswd = findViewById(R.id.newpasswd);
        EditText newpasswdconfrim = findViewById(R.id.newpasswdconfirm);
        String verification=fb_verified.getText().toString();
        String phone = fb_telephone.getText().toString();
        verifi(phone,verification);

//        if (  !ver    ) {
//            Log.d(TAG, "commit: 验证码错误");
//        } else if (!RegisterActivity.checkPasswd(newpasswd.getText().toString())) {
//            Log.d(TAG, "commit: 密码必须包含字母和数字，且在6-18位之间");
//        } else if (!newpasswd.getText().toString().equals(newpasswdconfrim.getText().toString()))
//            Log.d(TAG, "commit: 密码不一致");
//        else {
//            Log.d(TAG, "success");
//        String a=intent.getStringExtra("ID");
//            Log.d(TAG, "commit: a");
//            if (a.length()>=10) {
//                Person p2 = new Person();
//                p2.setAddress(newpasswd.getText().toString());
//                p2.update(a, new UpdateListener() {
//
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            toast("更新成功:" + p2.getUpdatedAt());
//                        } else {
//                            toast("更新失败：" + e.getMessage());
//                        }
//                    }
//
//                });
//            }else toast("未找到id");
//        }
    }
    private void verifi(String phone,String code){
        BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    EditText newpasswd = findViewById(R.id.newpasswd);



                    toast("验证码验证成功，您可以在此时进行绑定操作！\n");
                    Message message = new Message();
                    message.what=0x02;
                    Bundle bundle = new Bundle();
                    bundle.putString("npasswd",newpasswd.getText().toString());
                    message.setData(bundle);
                    myhandler.sendMessage(message);
                } else {
                    toast("验证码验证失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                }
            }
        });

    }
    private void send(View view) {
        EditText fb_telephone = findViewById(R.id.fb_telephone);

        if (!RegisterActivity.checkCellphone(fb_telephone.getText().toString())) {
            toast( "send: 手机号码格式错误");
        } else {
            BmobQuery<Person> query = new BmobQuery<Person>();
            query.addWhereEqualTo("name", String.valueOf(fb_telephone.getText()));
            query.findObjects(new FindListener<Person>() {


                @Override
                public void done(List<Person> list, BmobException e) {
                    if (e == null) {
                        Person p = list.get(0);
                        String s = p.getObjectId();
                        Message message = new Message();
                        Bundle bundle =new Bundle();
                        message.what=0x01;
                        bundle.putString("ID",s);
                        message.setData(bundle);
                        myhandler.sendMessage(message);
                        //发送验证码
                        BmobSMS.requestSMSCode(fb_telephone.getText().toString(),template , new QueryListener<Integer>() {
                            @Override
                            public void done(Integer smsId, BmobException e) {
                                if (e == null) {
                                    Log.d(TAG, "发送验证码成功，短信ID：" + smsId + "\n");
                                } else {
                                    Log.d(TAG, "发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                                }
                            }
                        });
                    } else {
                        toast("done: 未找到用户"+e.getMessage());
                    }

                }
            });


        }

    }

    void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
