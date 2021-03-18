package YccStudio;

import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.clevertap.apns.ApnsClient;
import com.clevertap.apns.Notification;
import com.clevertap.apns.NotificationResponse;


//Device_Token : 375c78f0411c3246ffcc85faae33438a080a854ad131ba55df9709fc259020ed
//Credentials  : D:/Senao\bitbucket.org/prod_appsvc.git/WebContent/apps_cert_files/SPLUS2R.p12 密碼是 qwer1234
//topic        : tw.com.senao.splus2rInhouse 
//
//將此檔案包裝起來成Demo.jar
//JDK1.8範例 執行需要額外引入 alpn-boot-8.1.13.v20181017.jar 到 bootclasspath
//java -Xbootclasspath/p:alpn-boot-8.1.13.v20181017.jar -jar Demo.jar
//
//JDK11範例 執行需要額外引入 alpn-boot-8.1.13.v20181017.jar 到 bootclasspath
//java --patch-module:alpn-boot-8.1.13.v20181017.jar -jar Demo.jar
//
//TOMCAT範例
//在bin/setenv.sh
//export CATALINA_OPTS="-Xbootclasspath/p:alpn-boot-8.1.13.v20181017.jar"

public class YccApnsPush {

	private static Logger logger=LoggerFactory.getLogger(YccApnsPush.class);

	//同步推播訊息
	public boolean push(String certFilePath, String certPassword, String topic, String deviceToken, String pushMessage) {
		boolean doPushComplete=false;
		try {
			FileInputStream cert = new FileInputStream(certFilePath);
			ApnsClient  client=new com.clevertap.apns.clients.ApnsClientBuilder().withProductionGateway().inSynchronousMode().withCertificate(cert).withPassword(certPassword).withDefaultTopic(topic).build();
			logger.debug("connectTimeoutMillis="+client.getHttpClient().connectTimeoutMillis()+", retryOnConnectionFailure="+client.getHttpClient().retryOnConnectionFailure());
			
			Notification n = new Notification.Builder(deviceToken).alertBody(pushMessage).build(); //建立推播訊息
			
			NotificationResponse result = client.push(n); //同步推播訊息
			logger.debug("result.getCause()="+result.getCause());
			logger.debug("推播回應的結果是 result="+result);
			
			if(result.getCause()==null) doPushComplete=true;
		}catch(Exception e) {
			e.printStackTrace();
			doPushComplete=false;
		}
		
		System.exit(0);
		return doPushComplete;
	}
	
	public static void main(String[] args) {
		
		/*
		try {
			FileInputStream cert = new FileInputStream("D:/SenaoECBackendWorkspace/prod_appsvc_apache-tomcat-7.0.85/webapps/prod_appsvc/apps_cert_files/SPLUS2R.p12");
			
			//Using provider certificates
			ApnsClient  client=new com.clevertap.apns.clients.ApnsClientBuilder().withProductionGateway().inSynchronousMode().withCertificate(cert).withPassword("qwer1234").withDefaultTopic("tw.com.senao.splus2rInhouse").build();
			logger.debug("connectTimeoutMillis="+client.getHttpClient().connectTimeoutMillis()+", retryOnConnectionFailure="+client.getHttpClient().retryOnConnectionFailure());
						
			//Using provider authentication tokens
			//ApnsClient  client=new com.clevertap.apns.clients.ApnsClientBuilder().inSynchronousMode().withProductionGateway().withApnsAuthKey("").withTeamID("").withKeyID("").withDefaultTopic("").build();
			
			//建立推播訊息
			Notification n = new Notification.Builder("375c78f0411c3246ffcc85faae33438a080a854ad131ba55df9709fc259020ed").alertBody("Hello 張晏哲跟自己問好!").build();
			//Notification n = new Notification.Builder("490bc459f2cffcf2d989ed7b784b3cdf9051b14f459976785c830402505460ce").alertBody("Hello 張晏哲 跟你問好!").build();			
				
			//非同步
			//client.push(n, new NotificationResponseListener() {
			//    @Override
			//    public void onSuccess(Notification notification) {
			//        System.out.println("success!");
			//    }
			//    @Override
			//    public void onFailure(Notification notification, NotificationResponse nr) {
			//        System.out.println("failure: " + nr);
			//    }
			//});
			
			//同步
			NotificationResponse result = client.push(n);
			logger.debug("result.getCause()="+result.getCause());
			logger.debug("推播回應的結果是"+result);
			
			System.exit(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		String certFilePath="D:/SenaoECBackendWorkspace/prod_appsvc_apache-tomcat-7.0.85/webapps/prod_appsvc/apps_cert_files/SPLUS2R.p12";
		String certPassword="qwer1234";
		String topic="tw.com.senao.splus2rInhouse";
		String deviceToken="375c78f0411c3246ffcc85faae33438a080a854ad131ba55df9709fc259020ed";
		String pushMessage="Hello 張晏哲跟自己問好!";
		YccApnsPush demo=new YccApnsPush();
		demo.push(certFilePath, certPassword, topic, deviceToken, pushMessage);
		
	}

}
