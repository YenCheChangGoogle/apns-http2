package YccStudio;

public interface IYccApnsPush {
	
	public boolean push(String certFilePath, String certPassword, String topic, String deviceToken, String pushMessage);
	public boolean push(String certFilePath, String certPassword, String topic, String[] deviceToken, String pushMessage);
}
