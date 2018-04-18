package com.example.thirdlogin;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String APP_ID = "1106404425";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //传入参数APPID和全局Context上下文

        mTencent = Tencent.createInstance(APP_ID,MainActivity.this.getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void buttonJupe(View v){
        Intent intent=new Intent(this,SecondActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),"ccccc",Toast.LENGTH_LONG).show();
    }
    public void buttonLogin(View v){
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(MainActivity.this,"all", mIUiListener);
    }

    private static final String TAG = "MainActivity";
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject jsonObject=(JSONObject)response;

                        Log.e(TAG,"登录成功"+response.toString());
                        try {
                            Toast.makeText(MainActivity.this, "登录成功"+jsonObject.getString("city"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG,"登录失败"+uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG,"登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
