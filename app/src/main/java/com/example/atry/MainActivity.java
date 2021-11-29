package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.I;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {
    private TextView mtvregister,mtvfindback;
    private EditText metusername,metpassword;
    private Button metlogin;
    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x00:
                    Intent intent =new Intent(MainActivity.this,ApplicationActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    break;
                case  0x01:
                    Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_SHORT).show();

            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "1477ee57c3443843cb11124c78e05af9");
        //Bmob.initialize(this, "d2d09b82e39c6cabef90dfd74c34b11e");
        mtvregister=findViewById(R.id.tv_3);
        metusername=findViewById(R.id.username);
        metpassword=findViewById(R.id.password);
        metlogin = findViewById(R.id.login);
        mtvfindback=findViewById(R.id.tv_1);
        metlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
        mtvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,RegisterActivity.class);
                startActivityForResult(intent,0);
            }
        });
        mtvfindback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,findbackctivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode
            , int resultCode, Intent intent)
    {
        // 当requestCode、resultCode同时为0时，也就是处理特定的结果
        if (requestCode == 0 && resultCode == 0)
        {
            try{
                // 取出Intent里的Extras数据
                Bundle data = intent.getExtras();
                // 取出Bundle中的数据
                String resultTel = data.getString("tel");
                String resultPwd = data.getString("passwd");

                metusername.setText(resultTel);
                metpassword.setText(resultPwd);}
            catch (Exception e){
                Log.e("--------------","1intent is null");
            }
        }
    }
    void login(View view){
        BmobQuery<Person> query=new BmobQuery<Person>();
        query.addWhereEqualTo("name", String.valueOf(metusername.getText()));


        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (e == null) {
                    if ( list.size()==1) {


                        Person person = list.get(0);

                        if (person.getAddress().equals(String.valueOf(metpassword.getText())))
                        {Message message = handler.obtainMessage();
                            message.what = 0x00;
                            //以消息为载体
                            message.obj = list;
                            //向handler发送消息
                            handler.sendMessage(message);}
                        else{
                            Message message = handler.obtainMessage();
                            message.what =0x01;
                            handler.sendMessage(message);
                        }


                    } else {
                        Toast.makeText(MainActivity.this,"该手机号未注册请先注册",Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
//            query.findObjects(new FindListener<Person>() {
//                @Override
//                public void done(List<Person> list, BmobException e) {
//                    if (e == null) {
//
//                        for(Person p :list)
//                        {
//
//
//                        }
////
//                    } else {
//                        Log.e("bmob", ""+e);
//                    }
//                }
//            });
    }
}
class Person extends BmobObject {
    private String name;
    private String address;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
