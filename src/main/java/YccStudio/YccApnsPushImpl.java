package YccStudio;

import java.io.FileInputStream;
import java.util.Calendar;

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
//
//Xbootclasspath/p:alpn-boot-8.1.13.v20181017.jar

public class YccApnsPushImpl implements IYccApnsPush {
	
	private static Logger logger=LoggerFactory.getLogger(YccApnsPushImpl.class);
	
	@Override
	public boolean push(String certFilePath, String certPassword, String topic, String deviceToken, String pushMessage) {
		boolean b=false;
		class processThread extends Thread {
			boolean doPushComplete=false;
			public void run() {
				try {
					FileInputStream cert = new FileInputStream(certFilePath);
					ApnsClient  client=new com.clevertap.apns.clients.ApnsClientBuilder().withProductionGateway().inSynchronousMode().withCertificate(cert).withPassword(certPassword).withDefaultTopic(topic).build();
					logger.debug("connectTimeoutMillis="+client.getHttpClient().connectTimeoutMillis()+", retryOnConnectionFailure="+client.getHttpClient().retryOnConnectionFailure());
					
					Notification n = new Notification.Builder(deviceToken).alertBody(pushMessage).build(); //建立推播訊息
					
					NotificationResponse result = client.push(n); //同步推播訊息
					logger.debug("result.getCause()="+result.getCause());
					logger.debug("推播回應的結果是 result="+result);
					
					if(result.getCause()==null) {
						logger.error("[推播成功] deviceToken="+deviceToken);
						doPushComplete=true;
					}
					else {
						logger.error("[推播失敗] deviceToken="+deviceToken+" "+result.getCause());
					}
				}catch(Exception e) {
					e.printStackTrace();
					doPushComplete=false;
					logger.error("[推播失敗] deviceToken="+deviceToken+" "+e.getMessage());
				}
				
				synchronized(this) {
					this.notifyAll();
				}
				
				System.exit(0);
			}
		}
		processThread t=new processThread();
		
		synchronized(t) {
			t.start();
			try { t.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
			b=t.doPushComplete;
		}
				
		return b;
	}

	@Override
	public boolean push(String certFilePath, String certPassword, String topic, String[] deviceTokens, String pushMessage) {
		
		boolean b=false;
		class processThread extends Thread {
			boolean doPushComplete=false;
			public void run() {				
				try {
					FileInputStream cert = new FileInputStream(certFilePath);
					ApnsClient  client=new com.clevertap.apns.clients.ApnsClientBuilder().withProductionGateway().inSynchronousMode().withCertificate(cert).withPassword(certPassword).withDefaultTopic(topic).build();
					logger.debug("connectTimeoutMillis="+client.getHttpClient().connectTimeoutMillis()+", retryOnConnectionFailure="+client.getHttpClient().retryOnConnectionFailure());
					
					boolean b=true;
					for(String deviceToken:deviceTokens) {
						try {
							Notification n = new Notification.Builder(deviceToken).alertBody(pushMessage).build(); //建立推播訊息
							NotificationResponse result = client.push(n); //同步推播訊息
							//logger.debug("result.getCause()="+result.getCause());
							logger.debug("推播回應的結果是 result="+result);
							if(result.getCause()!=null) {
								b=false;
								logger.error("[推播失敗] deviceToken="+deviceToken+" "+result.getCause());
							}
						}
						catch(Exception ex) {
							logger.error("[推播失敗] deviceToken="+deviceToken+" "+ex.getMessage());
							b=false;
						}
					}
					doPushComplete=b;
				}catch(Exception e) {
					e.printStackTrace();
					doPushComplete=false;
				}
				
				synchronized(this) {
					this.notifyAll();
				}				
				System.exit(0);
			}
		}
		processThread t=new processThread();
		
		synchronized(t) {
			t.start();
			try { t.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
			b=t.doPushComplete;
		}

		
		return b;
	}

	
	public static void main(String[] args) {
				
		String certFilePath="D:/SenaoECBackendWorkspace/prod_appsvc_apache-tomcat-7.0.85/webapps/prod_appsvc/apps_cert_files/SPLUS2R.p12";
		String certPassword="qwer1234";
		String topic="tw.com.senao.splus2rInhouse";		
		String deviceToken="375c78f0411c3246ffcc85faae33438a080a854ad131ba55df9709fc259020ed";
		String deviceToken2="490bc459f2cffcf2d989ed7b784b3cdf9051b14f459976785c830402505460ce";
		String pushMessage="Hello 張晏哲跟自己問好! 工作太多囉!!!";
		IYccApnsPush demo=new YccApnsPushImpl();
		//logger.debug("推播測試結果 "+demo.push(certFilePath, certPassword, topic, deviceToken, pushMessage));
		
		Calendar cal=Calendar.getInstance();
		java.text.NumberFormat nf=java.text.NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		String now=cal.get(cal.YEAR)+"-"+nf.format(1+cal.get(cal.MONTH))+"-"+nf.format(cal.get(cal.DAY_OF_MONTH))+" "+nf.format(cal.get(cal.HOUR_OF_DAY))+":"+nf.format(cal.get(cal.MINUTE))+":"+nf.format(cal.get(cal.SECOND));
		logger.debug(now);
		String[] deviceTokens= {deviceToken, deviceToken2};
		pushMessage="Hello 張晏哲跟您問好! 現在時間是 "+now;
		logger.debug("推播測試結果 "+demo.push(certFilePath, certPassword, topic, deviceTokens, pushMessage));
		
	}

}
