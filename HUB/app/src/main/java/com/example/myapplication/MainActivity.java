package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private Button btn_1;
    private ImageView image_1;
    private ImageView image_2;
    private ImageView image_3;
    private ImageView image_4;
    private ImageView image_5;
    private TextView text_1;
    private int led_flag = 1;
    private String host = "tcp://broker-cn.emqx.io:1883";
    private String userName = "android";
    private String passWord = "android";
    private String mqtt_id = "2117194284"; //定义成自己的QQ号  切记！不然会掉线！！！
    private String mqtt_sub_topic = "2117194284"; //为了保证你不受到别人的消息  哈哈
    private String mqtt_pub_topic = "2117194284_PC"; //为了保证你不受到别人的消息  哈哈  自己QQ好后面加 _PC
    private MqttClient client;
    private MqttConnectOptions options;
    private Handler handler;
    private ScheduledExecutorService scheduler;

    @SuppressLint("HandlerLeak")
    @SuppressWarnings("deprecation")//弥补的Handle会被划掉
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //一般先进行界面初始化，控件初始化，初始化一些参数和变量......
        btn_1 = findViewById(R.id.btn_1);//绑定按钮id
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里就是单击之后执行的地方
                System.out.println("hello!");//调试
                //更直观的方法，弹窗:toast
                //在当前activity显示内容为“hello”
                Toast.makeText(MainActivity.this,"欢迎来到这里！", Toast.LENGTH_SHORT).show();
            }
        });
        //图片点击
        image_1 = findViewById(R.id.image_1);
        image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里就是单击之后执行的地方
                System.out.println("hello!");//调试
                //更直观的方法，弹窗:toast
                //在当前activity显示内容为“hello”
                Toast.makeText(MainActivity.this,"你是烤全羊！", Toast.LENGTH_SHORT).show();
            }
        });
        image_2 = findViewById(R.id.image_2);
        image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里就是单击之后执行的地方
                System.out.println("hello!");//调试
                //更直观的方法，弹窗:toast
                //在当前activity显示内容为“hello”
                Toast.makeText(MainActivity.this,"你是烤鸭！", Toast.LENGTH_SHORT).show();
            }
        });
        //两个控件联动，点击按钮，更改textview的内容
        image_4 = findViewById(R.id.image_4);
        image_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"我是第一个图片" ,Toast.LENGTH_SHORT).show();
                publishmessageplus(mqtt_pub_topic,"{\n" +
                        "    \"MODEL\": \"HMS600\",\n" +
                        "    \"IMEI\": \"862177241709877\",\n" +
                        "    \"PID\": 386025,\n" +
                        "    \"VALUE\": {\n" +
                        "        \"INDEX\": 1,\n" +
                        "        \"XANG\": 0.000,\n" +
                        "        \"YANG\": 0.000,\n" +
                        "        \"XACC\": 0.000,\n" +
                        "        \"YACC\": 0.000,\n" +
                        "        \"ZACC\": 0.000,\n" +
                        "        \"ALARM_FLG\": 0,\n" +
                        "        \"ALARM_TYPE\": 0,\n" +
                        "        \"ANG\": 0.000,\n" +
                        "        \"ACC\": 0.000,\n" +
                        "        \"CLR_FLG\": 0,\n" +
                        "        \"FIX\": 0,\n" +
                        "        \"FREQ\": 28800,\n" +
                        "        \"RFAA\": 600,\n" +
                        "        \"ACTC\": 0,\n" +
                        "        \"CSQ\": 0,\n" +
                        "        \"EQC\": 0,\n" +
                        "        \"TEMP\": 0.00,\n" +
                        "        \"LNGTD\": 0.00,\n" +
                        "        \"LATTD\": 0.00,\n" +
                        "        \"SOFTVERSION\": \"205A220916\"\n" +
                        "    }\n" +
                        "}");

            }
        });
        image_5 = findViewById(R.id.image_5);
        image_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(led_flag == 0)
                {
                    publishmessageplus(mqtt_pub_topic,"{\"set_led\":1}");
                    led_flag = 1;
                }else
                {
                    publishmessageplus(mqtt_pub_topic,"{\"set_led\":0}");
                    led_flag = 0;
                }
            }
        });
        image_3 = findViewById(R.id.image_3);
        image_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_1.setText("我是新的内容！");
            }
        });
        text_1 = findViewById(R.id.text_1);
//**********************************************************/
        Mqtt_init();
        startReconnect();

        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1: //开机校验更新回传
                        break;
                    case 2:  // 反馈回传

                        break;
                    case 3:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());

                        //JSON截取{“tem:1”}中的数据
                        String T_val = msg.obj.toString().substring(msg.obj.toString().indexOf("tem\":")+5,msg.obj.toString().indexOf("}"));
                        String text_val = "温度: " + T_val;
                        text_1.setText(text_val);
                        //Toast.makeText(MainActivity.this,T_val,Toast.LENGTH_SHORT).show();
                        break;
                    case 30:  //连接失败
                        Toast.makeText(MainActivity.this,"连接失败" ,Toast.LENGTH_SHORT).show();
                        break;
                    case 31:   //连接成功
                        Toast.makeText(MainActivity.this,"连接成功" ,Toast.LENGTH_SHORT).show();
                        try {
                            client.subscribe(mqtt_sub_topic,1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void Mqtt_init()
    {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    //startReconnect();
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 3;   //收到消息标志位
                    msg.obj = message.toString();
                    handler.sendMessage(msg);    // hander 回传
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!(client.isConnected()) )  //如果还未连接
                    {
                        client.connect(options);
                        Message msg = new Message();
                        msg.what = 31;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }
    private void publishmessageplus(String topic,String message2)
    {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic,message);
        } catch (MqttException e) {

            e.printStackTrace();
        }
    }
}