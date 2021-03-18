package com.clevertap.apns;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

//目前非同步的還未實作完成

//Device_Token : 375c78f0411c3246ffcc85faae33438a080a854ad131ba55df9709fc259020ed
//Credentials  : D:/Senao\bitbucket.org/prod_appsvc.git/WebContent/apps_cert_files/SPLUS2R.p12 密碼是 qwer1234
//topic        : tw.com.senao.splus2rInhouse 
public class Demo2 {

	//在啟動的部位
	//Bootstrap 加入 appn-boot-8.1.13.v20181017.jar
	
	public static void main(String[] args) {
		System.out.println("YenCheChang");
		try {
			FileInputStream cert = new FileInputStream("D:/SenaoECBackendWorkspace/prod_appsvc_apache-tomcat-7.0.85/webapps/prod_appsvc/apps_cert_files/SPLUS2R.p12");
			
			//Using provider certificates
			ApnsClient  client=new com.clevertap.apns.clients.ApnsClientBuilder().withProductionGateway().inSynchronousMode().withCertificate(cert).withPassword("qwer1234").withDefaultTopic("tw.com.senao.splus2rInhouse").build();
			System.out.println(client.getHttpClient().connectTimeoutMillis());
			System.out.println(client.getHttpClient().retryOnConnectionFailure());
			
			//Using provider authentication tokens
			//ApnsClient  client=new com.clevertap.apns.clients.ApnsClientBuilder().inSynchronousMode().withProductionGateway().withApnsAuthKey("").withTeamID("").withKeyID("").withDefaultTopic("").build();
			
			//建立推播訊息
			Notification n = new Notification.Builder("375c78f0411c3246ffcc85faae33438a080a854ad131ba55df9709fc259020ed").alertBody("Hello 張晏哲").build();			
			
			//Asynchronous requests are not supported by this client 待研究
			//非同步 (目前非同步的還未實作完成)
			client.push(n, new NotificationResponseListener() {
			    @Override
			    public void onSuccess(Notification notification) {
			        System.out.println("success!");
			    }
			    @Override
			    public void onFailure(Notification notification, NotificationResponse nr) {
			        System.out.println("failure: " + nr);
			    }
			});
			
			//同步
			//NotificationResponse result = client.push(n);
			//System.out.println(result);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
