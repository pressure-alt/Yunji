package com.example.atry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


public class RegisterActivity extends AppCompatActivity {


//    public void insertuserinfo(String tel, String passwd) {
//
//        Person p2 = new Person();
//        p2.setName(tel);
//        p2.setAddress(passwd);
//        p2.save(new SaveListener<String>() {
//            @Override
//            public void done(String objectId, BmobException e) {
//                if (e == null) {
//                    toast("添加数据成功，返回objectId为：" + objectId);
//                } else {
//                    toast("创建数据失败：" + e.getMessage());
//                }
//            }
//        });
//
//
//    }

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkCellphone(String cellphone) {
        String regex = "^1[3456789]\\d{9}$";
        return cellphone.matches(regex);
    }

    public static boolean checkPasswd(String passwd) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,18}$";
        return passwd.matches(regex);
    }

    public void commit(View view) {
         final String tel;
         final String passwd;
         final Handler handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0x01:
                        Bundle bundle=msg.getData();
                        String s=bundle.getString("tel");
                        String s1=bundle.getString("passwd");
                        Intent intent=getIntent();
                        intent.putExtras(bundle);
                        RegisterActivity.this.setResult(0,intent);
                        RegisterActivity.this.finish();
                        break;
                }

            }
        };

        EditText teledt = findViewById(R.id.username_register);
        EditText passwdedt = findViewById(R.id.password_register);
        CheckBox clause = findViewById(R.id.clause);


        tel = String.valueOf(teledt.getText());
        passwd = String.valueOf(passwdedt.getText());

        if (!checkCellphone(tel)) {
            toast("手机号码格式错误");
        } else if (!checkPasswd(passwd)) {
            toast("密码必须包含字母和数字，且在6-18位之间");
        } else {


            if (clause.isChecked()) {


                Person p2 = new Person();
                p2.setName(tel);
                p2.setAddress(passwd);

                p2.save(new SaveListener<String>() {

                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            toast("添加数据成功，返回objectId为：" + objectId);
//                    Intent i = new Intent();
//                    i.putExtra("tel",tel);
//                    i.putExtra("passwd",passwd);
//                    i.setClass(context,MainActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("tel",tel);
                            bundle.putString("passwd",passwd);

                            Message message = Message.obtain();
                            message.setData(bundle);
                            message.what=0x01;
                            handler.sendMessage(message);
                        } else {
                            switch (e.getErrorCode()) {
                                case 401:
                                    toast("该电话号码已被注册");
                                    break;
                                default:
                                    toast(e.getMessage());
                            }

                        }
                    }
                });
//                Intent i = intent;
//                i.setClass(RegisterActivity.this,MainActivity.class);
//                startActivity(i);
//                setResult(0,i);
//                toast(intent.getStringExtra("tel"));
//                toast(intent.getStringExtra("passwd"));
            } else
                toast("请阅读并同意相关服务条款和隐私政策");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bmob.initialize(this, "1477ee57c3443843cb11124c78e05af9");
        //Bmob.initialize(this, "d2d09b82e39c6cabef90dfd74c34b11e");
        Button register = findViewById(R.id.commit);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit(v);
            }
        });
        TextView clausetext = findViewById(R.id.clatext);
        clausetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,WebViewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void queryMultiData(String tel,BmobQuery<Person> query) {

        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (e == null) {
                    toast("查询成功：共" + list.size() + "条数据。");
                    for (Person p : list) {
                        //获得数据的objectId信息
                        toast(p.getObjectId());

                        //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
                        toast(p.getAddress());
                    }


                } else
                    toast("查询失败：" + e.getMessage());
            }

        });
    }

}